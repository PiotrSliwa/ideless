package com.github.ideless;

import com.github.ideless.init.InvalidTemplateException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TemplateCreatorTest {

    private final static String TEMPLATE_NAME = "templateName";
    private final static Path USER_HOME = Paths.get("UserHome");

    private FileIO fileIO;
    private PathsCreator pathsCreator;
    private TemplateCreator sut;

    @Before
    public void beforeTest() {
        fileIO = mock(FileIO.class);
        pathsCreator = mock(PathsCreator.class);
        sut = new TemplateCreator(fileIO, pathsCreator);

        when(fileIO.isReadable(Matchers.any())).thenReturn(false);
        when(pathsCreator.createUserHome()).thenReturn(USER_HOME);
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowWhenEmptyStringPassed() throws InvalidTemplateException {
        sut.create("");
    }

    @Test
    public void shallReturnLocalTemplateWhenIsReadable() throws InvalidTemplateException {
        when(fileIO.isReadable(Paths.get(TEMPLATE_NAME))).thenReturn(true);
        Template template = sut.create(TEMPLATE_NAME);
        assertEquals(Paths.get(TEMPLATE_NAME), template.getPath());
        assertEquals(Template.Type.Local, template.getType());
    }

    @Test
    public void shallReturnTemplateFromUserHomeWhenIsReadableButLocalIsNot() throws InvalidTemplateException {
        when(fileIO.isReadable(USER_HOME.resolve(TEMPLATE_NAME))).thenReturn(true);
        Template template = sut.create(TEMPLATE_NAME);
        assertEquals(USER_HOME.resolve(TEMPLATE_NAME), template.getPath());
        assertEquals(Template.Type.UserHome, template.getType());
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowWhenNeitherLocalNorFromUserHomeTemplateIsReadable() throws InvalidTemplateException {
        sut.create(TEMPLATE_NAME);
    }

}
