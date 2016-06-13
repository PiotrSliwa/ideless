package com.github.ideless;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class TemplateDownloader {

    private static final Pattern URL_PATTERN = Pattern.compile("^(\\w+):\\/\\/.+?\\.git$", Pattern.CASE_INSENSITIVE);
    private final UserIO userIO;

    public TemplateDownloader(UserIO userIO) {
        this.userIO = userIO;
    }

    public Template download(String url) throws InvalidDownloadableTemplateException, IOException, GitAPIException {
        if (!isValidGitRepository(url))
            throw new InvalidDownloadableTemplateException(url);
        userIO.println("Downloading template: " + url);
        Path directory = Files.createTempDirectory(null);
        cloneRepository(url, directory);
        return new Template(directory, Template.Type.Local);
    }

    private static boolean isValidGitRepository(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }

    private static void cloneRepository(String url, Path directory) throws GitAPIException {
        Git.cloneRepository()
                .setURI(url)
                .setDirectory(directory.toFile())
                .call();
    }

}
