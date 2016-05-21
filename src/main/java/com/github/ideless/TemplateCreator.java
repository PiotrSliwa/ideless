package com.github.ideless;

import com.github.ideless.init.InvalidTemplateException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TemplateCreator {

    private final FileIO fileIO;
    private final PathsCreator pathsCreator;

    public TemplateCreator(FileIO fileIO, PathsCreator pathsCreator) {
        this.fileIO = fileIO;
        this.pathsCreator = pathsCreator;
    }

    public Template create(String templateName) throws InvalidTemplateException {
        Path localTemplateDir = Paths.get(templateName);
        if (fileIO.isReadable(localTemplateDir))
            return new Template(localTemplateDir, Template.Type.Local);
        Path homeTemplateDir = pathsCreator.createUserHome().resolve(templateName);
        if (fileIO.isReadable(homeTemplateDir))
            return new Template(homeTemplateDir, Template.Type.UserHome);
        throw new InvalidTemplateException(templateName);
    }

}
