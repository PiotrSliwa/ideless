package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.github.ideless.PathsCreator;
import com.github.ideless.SafeCommandHandler;
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
    }

    private static final String DIRECTORY = "directory";
    private static final String TEMPLATE_NAME = "dummyName";
    private static final String MANIFEST_FILE_NAME = ".ideless";
    private static final Path MANIFEST_PATH = Paths.get(TEMPLATE_NAME, MANIFEST_FILE_NAME);
    private static final String FILE = "file1";
    private static final List<String> FILES = Arrays.asList("file1", "file2");
    private static final String NAME = "name";
    private static final String VARIABLE = "$" + NAME;
    private static final Path HOME_PATH = Paths.get("HomePath");

    private SafeCommandHandler defaultHandler;
    private ManifestReader manifestReader;
    private UserIO userIO;
    private FileInitializer fileInitializer;
    private VariableRepository variableRepository;
    private ExpressionConfigUpdater expressionConfigUpdater;
    private FileIO fileIO;
    private PathsCreator pathsCreator;
    private InitCommandHandler sut;

    private static String getFileInitMessage(String file) {
        return "Initializing file: " + file;
    }

    private static String getPropertyQuestionMessage(Property property) {
        return property.getName() + " (" + property.getDescription() + "): ";
    }

    @Before
    public void beforeTest() {
        defaultHandler = mock(SafeCommandHandler.class);
        manifestReader = mock(ManifestReader.class);
        userIO = mock(UserIO.class);
        fileInitializer = mock(FileInitializer.class);
        variableRepository = mock(VariableRepository.class);
        expressionConfigUpdater = mock(ExpressionConfigUpdater.class);
        fileIO = mock(FileIO.class);
        pathsCreator = mock(PathsCreator.class);
        sut = new InitCommandHandler(
                defaultHandler,
                manifestReader,
                userIO,
                fileInitializer,
                variableRepository,
                expressionConfigUpdater,
                fileIO,
                pathsCreator);

        when(pathsCreator.createUserHome()).thenReturn(HOME_PATH);
        when(fileIO.isReadable(any())).thenReturn(true);
    }

    @Test
    public void shallCallDefaultHandlerWhenNoParametersGiven() throws Exception {
        sut.handle(Arrays.asList());
        verify(defaultHandler).handle(Arrays.asList());
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowErrorWhenManifestCannotBeRead() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenThrow(new IOException());
        sut.handle(Arrays.asList(TEMPLATE_NAME));
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowErrorWhenNullManifestReturned() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(null);
        sut.handle(Arrays.asList(TEMPLATE_NAME));
    }

    @Test(expected = LackOfFieldException.class)
    public void shallThrowErrorWhenEmptyManifestReturned() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest());
        sut.handle(Arrays.asList(TEMPLATE_NAME));
    }

    @Test
    public void shallReadInitFilesAndSaveThemToTarget() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(FILES));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(fileInitializer).initialize(Paths.get(TEMPLATE_NAME, FILES.get(0)), Paths.get(FILES.get(0)));
        verify(fileInitializer).initialize(Paths.get(TEMPLATE_NAME, FILES.get(1)), Paths.get(FILES.get(1)));
    }

    @Test
    public void shallPrintInformationAboutFileInitialization() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(FILES));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(userIO).println(getFileInitMessage(FILES.get(0)));
        verify(userIO).println(getFileInitMessage(FILES.get(1)));
    }

    @Test
    public void shallReadInitFilesAndSaveThemToTargetWithSpecifiedNewDirectory() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(Arrays.asList(FILE), null, null, DIRECTORY));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(fileInitializer).initialize(Paths.get(TEMPLATE_NAME, FILE), Paths.get(DIRECTORY, FILE));
    }

    @Test
    public void shallReadTemplateFromUserHomeWhenDirectoryPassedToHandleMethodIsNotReadable() throws Exception {
        final Path manifestPathForUserHome = HOME_PATH.resolve(TEMPLATE_NAME).resolve(MANIFEST_FILE_NAME);
        when(fileIO.isReadable(Paths.get(TEMPLATE_NAME))).thenReturn(false);
        when(manifestReader.read(manifestPathForUserHome)).thenReturn(new Manifest(Arrays.asList(FILE)));

        sut.handle(Arrays.asList(TEMPLATE_NAME));

        verify(manifestReader).read(manifestPathForUserHome);
        verify(fileInitializer).initialize(HOME_PATH.resolve(TEMPLATE_NAME).resolve(FILE), Paths.get(FILE));
    }

    @Test
    public void shallNotPrintSaveasMessageWhenUsingTemplateFromUserHome() throws Exception {
        when(fileIO.isReadable(Paths.get(TEMPLATE_NAME))).thenReturn(false);
        when(manifestReader.read(any())).thenReturn(new Manifest(Arrays.asList(FILE)));

        sut.handle(Arrays.asList(TEMPLATE_NAME));

        verify(userIO, never()).println(Messages.SAVE_TEMPLATE_AS);
    }

    @Test
    public void shallReadInitFilesAndSaveThemToTargetWithDirectorySpecifiedByVariable() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(Arrays.asList(FILE), null, null, VARIABLE));
        when(variableRepository.get(NAME)).thenReturn(DIRECTORY);
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(variableRepository).get(NAME);
        verify(fileInitializer).initialize(Paths.get(TEMPLATE_NAME, FILE), Paths.get(DIRECTORY, FILE));
    }

    @Test(expected = UndefinedVariableException.class)
    public void shallThrowErrorWhenNullVariableReturnedByRepositoryWhenTryiungToObtainDirectory() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(Arrays.asList(FILE), null, null, VARIABLE));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(variableRepository).get(NAME);
        verify(fileInitializer).initialize(Paths.get(TEMPLATE_NAME, FILE), Paths.get(DIRECTORY, FILE));
    }

    @Test(expected = CannotFindFileException.class)
    public void shallThrowErrorWhenInitializerReturnsIOException() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(FILES));
        doThrow(new IOException()).when(fileInitializer).initialize(Matchers.any(), Matchers.any());
        sut.handle(Arrays.asList(TEMPLATE_NAME));
    }

    @Test
    public void shallAskUserForPropertyWhenManifestContainsOne() throws Exception {
        Property property = new Property("propertyName", "propertyDescription");
        final String userValue = "user-defined value";
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(Arrays.asList(FILE), Arrays.asList(property)));
        when(userIO.read()).thenReturn(userValue);

        sut.handle(Arrays.asList(TEMPLATE_NAME));

        verify(userIO).print(getPropertyQuestionMessage(property));
        verify(variableRepository).setProperty(property.getName(), userValue);
    }

    @Test(expected = InvalidNumberOfElementsInArrayException.class)
    public void shallThrowErrorWhenExpressionFormatDoesNotContainRequiredNumberOfElements() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(Arrays.asList(FILE), null, Arrays.asList("1")));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
    }

    @Test
    public void shallConfigureExpressionConfigUpdaterWithGivenConfig() throws Exception {
        List<String> config = Arrays.asList("1", "2", "3");
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(Arrays.asList(FILE), null, config));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(expressionConfigUpdater).updateConfig(config.get(0), config.get(1), config.get(2));
    }

    @Test
    public void shallAskForSaveasNameWhenDirectoryPassed() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(Arrays.asList(FILE)));
        sut.handle(Arrays.asList(TEMPLATE_NAME));
        verify(userIO, atLeastOnce()).println(Messages.SAVE_TEMPLATE_AS);
    }

    @Test
    public void shallSaveFilesToHomeDirectory() throws Exception {
        final String userName = "userName";
        final String[] fileData = { "fileData1", "fileData2" };
        final String manifestData = "manifestData";

        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(FILES));
        when(userIO.read()).thenReturn(userName);
        when(fileIO.read(MANIFEST_PATH)).thenReturn(manifestData);
        when(fileIO.read(Paths.get(TEMPLATE_NAME, FILES.get(0)))).thenReturn(fileData[0]);
        when(fileIO.read(Paths.get(TEMPLATE_NAME, FILES.get(1)))).thenReturn(fileData[1]);

        sut.handle(Arrays.asList(TEMPLATE_NAME));

        verify(fileIO).write(HOME_PATH.resolve(userName).resolve(MANIFEST_FILE_NAME), manifestData);
        verify(fileIO).write(HOME_PATH.resolve(userName).resolve(FILES.get(0)), fileData[0]);
        verify(fileIO).write(HOME_PATH.resolve(userName).resolve(FILES.get(1)), fileData[1]);
    }

}
