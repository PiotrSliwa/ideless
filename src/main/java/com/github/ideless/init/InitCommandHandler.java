package com.github.ideless.init;

import com.github.ideless.CommandHandler;
import com.github.ideless.SafeCommandHandler;
import com.github.ideless.UserIO;
import com.github.ideless.processors.ExpressionConfigUpdater;
import com.github.ideless.processors.UndefinedVariableException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class InitCommandHandler implements CommandHandler {

    private static final int EXPRESSION_FORMAT_SIZE = 3;

    private final SafeCommandHandler invalidParameterHandler;
    private final ManifestReader manifestReader;
    private final UserIO userIO;
    private final FileInitializer fileInitializer;
    private final VariableRepository variableRepository;
    private final ExpressionConfigUpdater expressionConfigUpdater;

    public InitCommandHandler(
            SafeCommandHandler invalidParameterHandler,
            ManifestReader manifestReader,
            UserIO userIO,
            FileInitializer fileInitializer,
            VariableRepository variableRepository,
            ExpressionConfigUpdater expressionConfigUpdater) {
        this.invalidParameterHandler = invalidParameterHandler;
        this.manifestReader = manifestReader;
        this.userIO = userIO;
        this.fileInitializer = fileInitializer;
        this.variableRepository = variableRepository;
        this.expressionConfigUpdater = expressionConfigUpdater;
    }

    @Override
    public void handle(List<String> parameters) throws Exception {
        if (parameters.isEmpty()) {
            invalidParameterHandler.handle(parameters);
            return;
        }
        String templateDir = getTemplateDir(parameters);
        Manifest manifest = readManifest(templateDir);
        updateExpressionConfig(manifest);
        initProperties(manifest);
        initFiles(manifest, templateDir);
    }

    private static String getTemplateDir(List<String> parameters) {
        return parameters.get(0);
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

    private void initFiles(Manifest manifest, String templateDir) throws Exception {
        for (String path : manifest.getInitFiles()) {
            Path targetPath = createTargetPath(manifest, path);
            try {
                fileInitializer.initialize(Paths.get(templateDir, path), targetPath);
                userIO.println("Initializing file: " + targetPath);
            }
            catch (IOException ex) {
                throw new CannotFindFileException(targetPath.toString());
            }
        }
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
        return expression.substring(1);
    }

    private void updateExpressionConfig(Manifest manifest) {
        List<String> format = manifest.getExpressionFormat();
        if (format == null)
            return;
        expressionConfigUpdater.updateConfig(format.get(0), format.get(1), format.get(2));
    }

    private Manifest readManifest(String templateDir) throws Exception {
        try {
            Manifest manifest = manifestReader.read(Paths.get(templateDir, ".ideless"));
            validate(manifest);
            return manifest;
        }
        catch (IOException ex) {
            throw new InvalidTemplateException(ex.getMessage());
        }
        catch (JsonSyntaxException ex) {
            throw new InvalidJsonException(ex.getMessage());
        }
    }

    private void validate(Manifest manifest) throws Exception {
        if (manifest == null)
            throw new InvalidTemplateException("null manifest");
        if (manifest.getInitFiles() == null)
            throw new LackOfFieldException("initFiles");
        if (manifest.getExpressionFormat() != null && manifest.getExpressionFormat().size() != EXPRESSION_FORMAT_SIZE)
            throw new InvalidNumberOfElementsInArrayException("expressionFormat", EXPRESSION_FORMAT_SIZE);
    }

}
