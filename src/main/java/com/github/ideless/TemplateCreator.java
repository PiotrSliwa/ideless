package com.github.ideless;

import com.github.ideless.init.InvalidTemplateException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.jgit.api.errors.GitAPIException;

public class TemplateCreator {

    private final FileIO fileIO;
    private final PathsCreator pathsCreator;
    private final TemplateDownloader templateDownloader;

    public TemplateCreator(FileIO fileIO, PathsCreator pathsCreator, TemplateDownloader templateDownloader) {
        this.fileIO = fileIO;
        this.pathsCreator = pathsCreator;
        this.templateDownloader = templateDownloader;
    }

    public Template create(String templateName) throws InvalidTemplateException, IOException {
        Path localTemplateDir = Paths.get(templateName);
        if (fileIO.isReadable(localTemplateDir))
            return new Template(localTemplateDir, Template.Type.Local);
        Path homeTemplateDir = pathsCreator.createUserHome().resolve(templateName);
        if (fileIO.isReadable(homeTemplateDir))
            return new Template(homeTemplateDir, Template.Type.UserHome);
        try {
            return templateDownloader.download(templateName);
        } catch (InvalidDownloadableTemplateException | GitAPIException ex) {
            throw new InvalidTemplateException(templateName);
        }
    }

}
