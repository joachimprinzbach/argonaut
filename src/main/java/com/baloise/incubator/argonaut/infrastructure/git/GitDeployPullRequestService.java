package com.baloise.incubator.argonaut.infrastructure.git;

import com.baloise.incubator.argonaut.domain.DeployPullRequestService;
import com.baloise.incubator.argonaut.domain.PullRequest;
import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestService;
import com.baloise.incubator.argonaut.infrastructure.github.ConditionalGitHub;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@ConditionalGitHub
@RequiredArgsConstructor
public class GitDeployPullRequestService implements DeployPullRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitDeployPullRequestService.class);

    @Value("${argonaut.gittoken}")
    private String apiToken;

    @Value("${argonaut.tempfolder}")
    private FileSystemResource tempFolder;

    private final PullRequestService pullRequestService;

    @Override
    public void deploy(PullRequest pullRequest, boolean isProd) {
        LOGGER.info("Deploying - isProd: {}, pullRequest: {}", isProd, pullRequest);
        String sanitizedBranchName = pullRequest.getHeadBranchName().replace("/", "-");
        LOGGER.info("Sanitized branchname: {}", sanitizedBranchName);
        String newImageTag = sanitizedBranchName + "-" + pullRequest.getHeadCommitSHA();
        LOGGER.info("New Image Tag: {}", newImageTag);
        File tempRootDirectory = tempFolder.getFile();
        boolean succeeded = tempRootDirectory.mkdir();
        LOGGER.info("temp folder created: {}", succeeded);
        File uuidWorkingDir = new File(tempRootDirectory, UUID.randomUUID().toString());
        LOGGER.info("Using subfolder with UUID: {}", uuidWorkingDir.getName());
        String branchSpecificFolderName = pullRequest.getRepository();
        File masterFolder = new File(uuidWorkingDir, branchSpecificFolderName);
        File branchSpecificFolder = masterFolder;
        String deployConfigNameSuffix = "-deployment-configuration";

        try {
            Git git = Git.cloneRepository()
                    .setURI(pullRequest.getBaseRepoGitUrl() + deployConfigNameSuffix)
                    .setDirectory(uuidWorkingDir)
                    .call();
            if (!isProd) {
                branchSpecificFolderName += "-" + sanitizedBranchName;
                branchSpecificFolder = new File(uuidWorkingDir, branchSpecificFolderName);
                if (!branchSpecificFolder.exists()) {
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
            String branchName = "deploy/" + pullRequest.getRepository() + pullRequest.getHeadCommitSHA();
            git
                    .checkout()
                    .setCreateBranch(true)
                    .setName(branchName)
                    .call();
            git
                    .commit()
                    .setAuthor("ttt-travis-bot", "joachim.prinzbach+github-ttt-travis-bot@gmail.com")
                    .setMessage(branchName)
                    .call();
            git
                    .push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider("ttt-travis-bot", apiToken))
                    .call();
            LOGGER.info("Pushed changes to branch {}", branchName);
            String deployConfigFullName = pullRequest.getFullName() + deployConfigNameSuffix;
            PullRequest deployPullRequest = pullRequestService.createPullRequest(deployConfigFullName, branchName);
            pullRequestService.createPullRequestComment(new PullRequestComment("Successfully deployed version " + newImageTag + ". See the PR here: " + deployPullRequest.getPrWebUrl(), pullRequest));
            pullRequestService.mergePullRequest(deployConfigFullName, deployPullRequest.getId());
            pullRequestService.createPullRequestComment(new PullRequestComment("Pull Request merged.", pullRequest));
        } catch (
                GitAPIException | InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
        try {
            FileUtils.deleteDirectory(uuidWorkingDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
