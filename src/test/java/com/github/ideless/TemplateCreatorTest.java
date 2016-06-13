package com.github.ideless;

import com.github.ideless.init.InvalidTemplateException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class TemplateCreatorTest {

    private final static String TEMPLATE_NAME = "templateName";
    private final static Path USER_HOME = Paths.get("UserHome");

    private FileIO fileIO;
    private PathsCreator pathsCreator;
    private TemplateDownloader templateDownloader;
    private TemplateCreator sut;

    @Before
    public void beforeTest() {
        fileIO = mock(FileIO.class);
        pathsCreator = mock(PathsCreator.class);
        templateDownloader = mock(TemplateDownloader.class);
        sut = new TemplateCreator(fileIO, pathsCreator, templateDownloader);

        when(fileIO.isReadable(Matchers.any())).thenReturn(false);
        when(pathsCreator.createUserHome()).thenReturn(USER_HOME);
    }

    @Test
    public void shallReturnLocalTemplateWhenIsReadable() throws Exception {
        when(fileIO.isReadable(Paths.get(TEMPLATE_NAME))).thenReturn(true);
        Template template = sut.create(TEMPLATE_NAME);
        assertEquals(Paths.get(TEMPLATE_NAME), template.getPath());
        assertEquals(Template.Type.Local, template.getType());
    }

    @Test
    public void shallReturnTemplateFromUserHomeWhenIsReadableButLocalIsNot() throws Exception {
        when(fileIO.isReadable(USER_HOME.resolve(TEMPLATE_NAME))).thenReturn(true);
        Template template = sut.create(TEMPLATE_NAME);
        assertEquals(USER_HOME.resolve(TEMPLATE_NAME), template.getPath());
        assertEquals(Template.Type.UserHome, template.getType());
    }

    @Test
    public void shallDownloadTemplateWhenNeitherLocalNotHomePathIsReadable() throws Exception {
        Template downloadedTemplate = new Template(Paths.get("dummy"), Template.Type.Local);
        when(templateDownloader.download(any())).thenReturn(downloadedTemplate);
        Template template = sut.create(TEMPLATE_NAME);
        assertEquals(downloadedTemplate, template);
        verify(templateDownloader).download(TEMPLATE_NAME);
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowWhenDownloaderThrowsAnyError() throws Exception {
        when(templateDownloader.download(any())).thenThrow(new InvalidDownloadableTemplateException(TEMPLATE_NAME));
        sut.create(TEMPLATE_NAME);
    }

}
