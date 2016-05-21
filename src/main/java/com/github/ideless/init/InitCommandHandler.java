package com.github.ideless.init;

import com.github.ideless.CommandHandler;
import com.github.ideless.FileIO;
import com.github.ideless.PathsCreator;
import com.github.ideless.SafeCommandHandler;
import com.github.ideless.Template;
import com.github.ideless.TemplateCreator;
import com.github.ideless.UserIO;
import com.github.ideless.processors.ExpressionConfigUpdater;
import com.github.ideless.processors.UndefinedVariableException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class InitCommandHandler implements CommandHandler {

    private static final String MANIFEST_FILE_NAME = ".ideless";

    private final SafeCommandHandler invalidParameterHandler;
    private final ManifestReader manifestReader;
    private final UserIO userIO;
    private final FileInitializer fileInitializer;
    private final VariableRepository variableRepository;
    private final ExpressionConfigUpdater expressionConfigUpdater;
    private final FileIO fileIO;
    private final PathsCreator pathsCreator;
    private final TemplateCreator templateCreator;

    public InitCommandHandler(
            SafeCommandHandler invalidParameterHandler,
            ManifestReader manifestReader,
            UserIO userIO,
            FileInitializer fileInitializer,
            VariableRepository variableRepository,
            ExpressionConfigUpdater expressionConfigUpdater,
            FileIO fileIO,
            PathsCreator pathsCreator,
            TemplateCreator templateCreator) {
        this.invalidParameterHandler = invalidParameterHandler;
        this.manifestReader = manifestReader;
        this.userIO = userIO;
        this.fileInitializer = fileInitializer;
        this.variableRepository = variableRepository;
        this.expressionConfigUpdater = expressionConfigUpdater;
        this.fileIO = fileIO;
        this.pathsCreator = pathsCreator;
        this.templateCreator = templateCreator;
    }

    @Override
    public void handle(List<String> parameters) throws Exception {
        if (parameters.isEmpty()) {
            invalidParameterHandler.handle(parameters);
            return;
        }
        Template template = getTemplate(parameters);
        Manifest manifest = readManifest(template.getPath());
        updateExpressionConfig(manifest);
        initProperties(manifest);
        initFiles(manifest, template.getPath());
        saveTemplateWithUserSpecifiedName(manifest, template);
    }

    private Template getTemplate(List<String> parameters) throws InvalidTemplateException {
        String templateName = parameters.get(0);
        return templateCreator.create(templateName);
    }

    private void initProperties(Manifest manifest) throws IOException {
        if (manifest.getProperties() == null)
            return;
        for (Property property : manifest.getProperties()) {
            String userValue = askUserForProperty(property);
            variableRepository.setProperty(property.getName(), userValue);
        }
    }

    private String askUserForProperty(Property property) throws IOException {
        userIO.print(property.getName() + " (" + property.getDescription() + "): ");
        return userIO.read();
    }

    private void initFiles(Manifest manifest, Path sourceDir) throws Exception {
        for (String path : manifest.getInitFiles()) {
            Path targetPath = createTargetPath(manifest, path);
            try {
                fileInitializer.initialize(sourceDir.resolve(path), targetPath);
                userIO.println("Initializing file: " + targetPath);
            }
            catch (IOException ex) {
                throw new CannotFindFileException(targetPath.toString());
            }
        }
    }

    private void saveTemplateWithUserSpecifiedName(Manifest manifest, Template template) throws Exception {
        if (template.getType() == Template.Type.UserHome)
            return;
        userIO.println("Save template as (leave empty if you don't want to save it): ");
        final String userTemplateName = userIO.read();
        copyFileToHome(template.getPath().resolve(MANIFEST_FILE_NAME), Paths.get(userTemplateName, MANIFEST_FILE_NAME));
        for (String path : manifest.getInitFiles())
            copyFileToHome(template.getPath().resolve(path), Paths.get(userTemplateName, path));
    }

    private void copyFileToHome(Path source, Path target) throws IOException {
        final String data = fileIO.read(source);
        fileIO.write(pathsCreator.createUserHome().resolve(target), data);
    }

    private Path createTargetPath(Manifest manifest, String path) throws UndefinedVariableException {
        String directory = manifest.getDirectory();
        if (directory == null)
            return Paths.get(path);
        if (isVariable(directory))
            directory = getVariable(parseVariableName(directory));
        return Paths.get(directory, path);
    }

    private String getVariable(String name) throws UndefinedVariableException {
        String value = (String) variableRepository.get(name);
        if (value == null)
            throw new UndefinedVariableException(name);
        return value;
    }

    private static boolean isVariable(String expression) {
        return expression.charAt(0) == '$';
    }

    private static String parseVariableName(String expression) {
        final int offsetAfterWhichVariableSignIsNotIncluded = 1;
        return expression.substring(offsetAfterWhichVariableSignIsNotIncluded);
    }

    private void updateExpressionConfig(Manifest manifest) {
        List<String> format = manifest.getExpressionFormat();
        if (format == null)
            return;
        expressionConfigUpdater.updateConfig(format.get(0), format.get(1), format.get(2));
    }

    private Manifest readManifest(Path templateDir) throws Exception {
        try {
            return manifestReader.read(templateDir.resolve(".ideless"));
        }
        catch (IOException ex) {
            throw new InvalidTemplateException(ex.getMessage());
        }
        catch (JsonSyntaxException ex) {
            throw new InvalidJsonException(ex.getMessage());
        }
    }

}
