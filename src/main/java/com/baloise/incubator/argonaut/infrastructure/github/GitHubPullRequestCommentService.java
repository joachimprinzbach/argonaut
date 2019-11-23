package com.baloise.incubator.argonaut.infrastructure.github;

import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@ConditionalGitHub
public class GitHubPullRequestCommentService implements PullRequestCommentService {

    @Value("${argonaut.githubtoken}")
    private String apiToken;

    @Value("${argonaut.tempfolder}")
    private FileSystemResource tempFolder;

    @Override
    public void createPullRequestComment(PullRequestComment pullRequestComment, String url) {
        boolean succeeded = tempFolder.getFile().mkdir();
        System.out.println(succeeded);

        try {
            Git git = Git.cloneRepository()
                    .setURI(url)
                    // TODO: Add reponame also for subfolder
                    .setDirectory(tempFolder.getFile())
                    .call();
            System.out.println(git.status().getRepository().getFullBranch());
        } catch (GitAPIException | IOException e) {
            System.out.println(e);
        }

        /*try {
            FileUtils.deleteDirectory(tempFolder.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
/*        try {
            load = yaml.load(new Base64InputStream(new ByteArrayInputStream(content.getBytes("US-ASCII")), false));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
    }
}
