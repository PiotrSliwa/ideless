package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.github.ideless.PathsCreator;
import com.github.ideless.SafeCommandHandler;
import com.github.ideless.Template;
import com.github.ideless.TemplateCreator;
import com.github.ideless.UserIO;
import com.github.ideless.processors.ExpressionConfigUpdater;
import com.github.ideless.processors.UndefinedVariableException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Mockito.*;

public class InitCommandHandlerTest {

    private static class Messages {
        private static final String SAVE_TEMPLATE_AS = "Save template as (leave empty if you don't want to save it): ";

        private static String getFileInitMessage(String file) {
            return "Initializing file: " + file;
        }

        private static String getPropertyQuestionMessage(Property property) {
            return property.getName() + " (" + property.getDescription() + "): ";
        }
    }

    private static final String DIRECTORY = "directory";
    private static final String TEMPLATE_NAME = "dummyName";
    private static final String MANIFEST_FILE_NAME = ".ideless";
    private static final String FILE = "file1";
    private static final List<String> FILES = Arrays.asList("file1", "file2");
    private static final String NAME = "name";
    private static final String VARIABLE = "$" + NAME;
    private static final Path HOME_PATH = Paths.get("HomePath");
    private static final Template LOCAL_TEMPLATE = new Template(Paths.get("somePath"), Template.Type.Local);

    private SafeCommandHandler defaultHandler;
    private ManifestReader manifestReader;
    private UserIO userIO;
    private FileInitializer fileInitializer;
    private VariableRepository variableRepository;
    private ExpressionConfigUpdater expressionConfigUpdater;
    private FileIO fileIO;
    private PathsCreator pathsCreator;
    private TemplateCreator templateCreator;
    private InitCommandHandler sut;

    @Before
    public void beforeTest() throws InvalidTemplateException {
        defaultHandler = mock(SafeCommandHandler.class);
        manifestReader = mock(ManifestReader.class);
        userIO = mock(UserIO.class);
        fileInitializer = mock(FileInitializer.class);
        variableRepository = mock(VariableRepository.class);
        expressionConfigUpdater = mock(ExpressionConfigUpdater.class);
        fileIO = mock(FileIO.class);
        pathsCreator = mock(PathsCreator.class);
        templateCreator = mock(TemplateCreator.class);
        sut = new InitCommandHandler(
                defaultHandler,
                manifestReader,
                userIO,
                fileInitializer,
                variableRepository,
                expressionConfigUpdater,
                fileIO,
                pathsCreator,
                templateCreator);

        when(pathsCreator.createUserHome()).thenReturn(HOME_PATH);
        when(fileIO.isReadable(any())).thenReturn(true);
        when(templateCreator.create(any())).thenReturn(LOCAL_TEMPLATE);
    }

    @Test
    public void shallCallDefaultHandlerWhenNoParametersGiven() throws Exception {
        sut.handle(Arrays.asList());
        verify(defaultHandler).handle(Arrays.asList());
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowErrorWhenManifestCannotBeRead() throws Exception {
        when(manifestReader.read(any())).thenThrow(new IOException());
        sut.handle(Arrays.asList(TEMPLATE_NAME));
    }

    @Test
    public void shallCreateTemplateFromGivenName() throws Exception {
        when(manifestReader.read(any())).thenReturn(new Manifest(FILES));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(templateCreator).create(TEMPLATE_NAME);
    }

    @Test
    public void shallReadManifestFileFromCreatedTemplate() throws Exception {
        when(manifestReader.read(any())).thenReturn(new Manifest(FILES));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(manifestReader).read(LOCAL_TEMPLATE.getPath().resolve(MANIFEST_FILE_NAME));
    }

    @Test
    public void shallInitializeFilesFromTemplatePathToLocalPath() throws Exception {
        when(manifestReader.read(any())).thenReturn(new Manifest(FILES));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(fileInitializer).initialize(LOCAL_TEMPLATE.getPath().resolve(FILES.get(0)), Paths.get(FILES.get(0)));
        verify(fileInitializer).initialize(LOCAL_TEMPLATE.getPath().resolve(FILES.get(1)), Paths.get(FILES.get(1)));
    }

    @Test
    public void shallPrintInformationAboutFileInitialization() throws Exception {
        when(manifestReader.read(any())).thenReturn(new Manifest(FILES));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(userIO).println(Messages.getFileInitMessage(FILES.get(0)));
        verify(userIO).println(Messages.getFileInitMessage(FILES.get(1)));
    }

    @Test
    public void shallInitializeFilesFromTemplatePathToSpecifiedDirectoryPath() throws Exception {
        when(manifestReader.read(any())).thenReturn(new Manifest(Arrays.asList(FILE), null, null, DIRECTORY));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(fileInitializer).initialize(LOCAL_TEMPLATE.getPath().resolve(FILE), Paths.get(DIRECTORY, FILE));
    }

    @Test
    public void shallNotPrintSaveasMessageWhenUsingTemplateFromUserHome() throws Exception {
        when(manifestReader.read(any())).thenReturn(new Manifest(Arrays.asList(FILE)));
        when(templateCreator.create(any())).thenReturn(new Template(Paths.get("somePath"), Template.Type.UserHome));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(userIO, never()).println(Messages.SAVE_TEMPLATE_AS);
    }

    @Test
    public void shallObtainVariableFromRepositoryWhenDirectoryIsSpecifiedByVariable() throws Exception {
        when(manifestReader.read(any())).thenReturn(new Manifest(Arrays.asList(FILE), null, null, VARIABLE));
        when(variableRepository.get(any())).thenReturn("dummyString");
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(variableRepository).get(NAME);
    }

    @Test
    public void shallInitializeFilesFromTemplatePathToDirectoryPathSpecifiedByVariable() throws Exception {
        when(manifestReader.read(any())).thenReturn(new Manifest(Arrays.asList(FILE), null, null, VARIABLE));
        when(variableRepository.get(any())).thenReturn(DIRECTORY);
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(fileInitializer).initialize(LOCAL_TEMPLATE.getPath().resolve(FILE), Paths.get(DIRECTORY, FILE));
    }

    @Test(expected = UndefinedVariableException.class)
    public void shallThrowErrorWhenNullVariableReturnedByRepository() throws Exception {
        when(manifestReader.read(any())).thenReturn(new Manifest(Arrays.asList(FILE), null, null, VARIABLE));
        when(variableRepository.get(any())).thenReturn(null);
        sut.handle(Arrays.asList(TEMPLATE_NAME));
    }

    @Test(expected = CannotFindFileException.class)
    public void shallThrowErrorWhenInitializerReturnsIOException() throws Exception {
        when(manifestReader.read(any())).thenReturn(new Manifest(FILES));
        doThrow(new IOException()).when(fileInitializer).initialize(Matchers.any(), Matchers.any());
        sut.handle(Arrays.asList(TEMPLATE_NAME));
    }

    @Test
    public void shallAskUserForPropertyWhenManifestContainsOne() throws Exception {
        Property property = new Property("propertyName", "propertyDescription");
        final String userValue = "user-defined value";
        when(manifestReader.read(any())).thenReturn(new Manifest(Arrays.asList(FILE), Arrays.asList(property)));
        when(userIO.read()).thenReturn(userValue);

        sut.handle(Arrays.asList(TEMPLATE_NAME));

        verify(userIO).print(Messages.getPropertyQuestionMessage(property));
        verify(variableRepository).setProperty(property.getName(), userValue);
    }

    @Test
    public void shallConfigureExpressionConfigUpdaterWithGivenConfig() throws Exception {
        List<String> config = Arrays.asList("1", "2", "3");
        when(manifestReader.read(any())).thenReturn(new Manifest(Arrays.asList(FILE), null, config));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(expressionConfigUpdater).updateConfig(config.get(0), config.get(1), config.get(2));
    }

    @Test
    public void shallAskForSaveasNameWhenTemplateIsLocal() throws Exception {
        when(manifestReader.read(any())).thenReturn(new Manifest(Arrays.asList(FILE)));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(userIO, atLeastOnce()).println(Messages.SAVE_TEMPLATE_AS);
    }

    @Test
    public void shallSaveFilesToHomeDirectory() throws Exception {
        final String userName = "userName";
        final String[] fileData = { "fileData1", "fileData2" };
        final String manifestData = "manifestData";

        when(manifestReader.read(any())).thenReturn(new Manifest(FILES));
        when(userIO.read()).thenReturn(userName);
        when(fileIO.read(LOCAL_TEMPLATE.getPath().resolve(MANIFEST_FILE_NAME))).thenReturn(manifestData);
        when(fileIO.read(LOCAL_TEMPLATE.getPath().resolve(FILES.get(0)))).thenReturn(fileData[0]);
        when(fileIO.read(LOCAL_TEMPLATE.getPath().resolve(FILES.get(1)))).thenReturn(fileData[1]);

        sut.handle(Arrays.asList(TEMPLATE_NAME));

        verify(fileIO).write(HOME_PATH.resolve(userName).resolve(MANIFEST_FILE_NAME), manifestData);
        verify(fileIO).write(HOME_PATH.resolve(userName).resolve(FILES.get(0)), fileData[0]);
        verify(fileIO).write(HOME_PATH.resolve(userName).resolve(FILES.get(1)), fileData[1]);
    }

}
