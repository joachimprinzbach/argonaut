package com.baloise.incubator.argonaut.infrastructure.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.YamlJsonParser;
import org.springframework.core.io.FileSystemResource;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public class GitPush {

    @Value("${users.file}")
    private FileSystemResource usersFile;

    public static void main(String[] args) throws GitAPIException {
        new GitPush().pushChanges();
    }

    public void pushChanges() throws GitAPIException {

        /*Git git = Git.cloneRepository()
                .setURI("https://github.com/joachimprinzbach/kloud-assistant-deployment")
                .call();*/
        Yaml yaml = new Yaml();

        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("values.yaml");
        Map<String, Object> obj = yaml.load("route:\n" +
                "  enabled: true\n" +
                "  host: \"kloudassistant.apps.baloise.dev\"\n" +
                "backend:\n" +
                "  replicaCount: 1\n" +
                "  appPort: 8080\n" +
                "  livenessProbePath: api/actuator/health\n" +
                "  readinessProbePath: api/actuator/health\n" +
                "  image:\n" +
                "    repository: joachimprinzbach/kloud-assistant\n" +
                "    tag: latest\n" +
                "    pullPolicy: Always\n" +
                "  service:\n" +
                "    type: ClusterIP\n" +
                "    port: 80\n" +
                "  route:\n" +
                "    enabled: true\n" +
                "    path: /\n" +
                "  resources: {}\n" +
                "  nodeSelector: {}\n" +
                "  tolerations: []\n" +
                "  affinity: {}\n" +
                "\n" +
                "\n");
        System.out.println(obj);

        /*
        String remoteUrl = "https://${token}@github.com/user/repo.git";
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider( "${token}", "" );
        git.push().setRemote( remoteUrl ).setCredentialsProvider( credentialsProvider ).call();*/
    }
}
