package com.baloise.incubator.argonaut.infrastructure.git;

import com.baloise.incubator.argonaut.domain.DeployPullRequestService;
import com.baloise.incubator.argonaut.domain.PullRequest;
import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GitHub;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class GitDeployPullRequestService implements DeployPullRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitDeployPullRequestService.class);

    @Value("${argonaut.githubtoken}")
    private String apiToken;

    @Value("${argonaut.tempfolder}")
    private FileSystemResource tempFolder;

    @Autowired
    private PullRequestCommentService pullRequestCommentService;

    @Autowired
    private GitHub gitHub;

    @Override
    public void deploy(PullRequest pullRequest, String deploymentRepoUrl, String newImageTag) {
        String branchName = "";
        try {
            branchName = gitHub.getRepository(pullRequest.getFullName()).getPullRequest(pullRequest.getId()).getHead().getRef();
        } catch (IOException e) {
            LOGGER.error("Error getting PR HEAD REF: ", e);
        }
        LOGGER.info("Deploying url: {}, pullRequest: {}, newImageTag: {}", deploymentRepoUrl, pullRequest, newImageTag);
        String sanitizedBranchName = branchName.replace("/", "-");
        LOGGER.info("Sanitized branchname: {}", sanitizedBranchName);
        File tempRootDirectory = tempFolder.getFile();
        boolean succeeded = tempRootDirectory.mkdir();
        LOGGER.info("temp folder created: {}", succeeded);
        File uuidWorkingDir = new File(tempRootDirectory, UUID.randomUUID().toString());
        LOGGER.info("Using subfolder with UUID: {}", uuidWorkingDir.getName());
        String branchSpecificFolderName = pullRequest.getRepository();
        File masterFolder = new File(uuidWorkingDir, branchSpecificFolderName);
        File branchSpecificFolder = masterFolder;

        try {
            Git git = Git.cloneRepository()
                    .setURI(deploymentRepoUrl)
                    .setDirectory(uuidWorkingDir)
                    .call();
            if (!"master".equals(sanitizedBranchName)) {
                branchSpecificFolderName += "-" + sanitizedBranchName;
                branchSpecificFolder = new File(uuidWorkingDir, branchSpecificFolderName);
                if (!branchSpecificFolder.exists() || branchSpecificFolderName.isEmpty()) {
                    FileUtils.copyDirectory(masterFolder, branchSpecificFolder);
                    LOGGER.info("PR Deployment {} does not exist, copying directory content from master directory: {}", branchSpecificFolder.getName(), masterFolder.getName());
                }
            }
            for (File subTempFolderFile : branchSpecificFolder.listFiles()) {
                if ("values.yaml".equals(subTempFolderFile.getName())) {
                    YamlFile yamlFile = new YamlFile(subTempFolderFile);
                    yamlFile.load();
                    LOGGER.info("Replacing old image tag: {} - with new tag: {}", yamlFile.get("backend.image.tag"), newImageTag);
                    yamlFile.set("backend.image.tag", newImageTag);
                    yamlFile.save();
                }
                if ("Chart.yaml".equals(subTempFolderFile.getName())) {
                    YamlFile yamlFile = new YamlFile(subTempFolderFile);
                    yamlFile.load();
                    yamlFile.set("name", branchSpecificFolderName);
                    yamlFile.save();
                }
            }
            git
                    .add()
                    .addFilepattern(".")
                    .call();
            git
                    .commit()
                    .setAuthor("ttt-travis-bot", "joachim.prinzbach+github-ttt-travis-bot@gmail.com")
                    .setMessage("Redeploy")
                    .call();
            git
                    .push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider("ttt-travis-bot", apiToken))
                    .call();
            LOGGER.info("Pushed changes.");
            pullRequestCommentService.createPullRequestComment(new PullRequestComment("Successfully deployed version " + newImageTag, pullRequest));
        } catch (
                GitAPIException | InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
        // TODO: Activate later
/*        try {
            FileUtils.deleteDirectory(uuidWorkingDir);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
