package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.github.ideless.JsonIO;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ManifestReaderTest {

    private final static Manifest MANIFEST = new Manifest(Arrays.asList("dummyFile"));
    private final static Path PATH = Paths.get("dummyPath");
    private final static String CONTENTS = "dummyString";

    private FileIO fileIO;
    private JsonIO jsonIO;
    private ManifestReader sut;

    @Before
    public void beforeTest() {
        fileIO = mock(FileIO.class);
        jsonIO = mock(JsonIO.class);
        sut = new ManifestReader(fileIO, jsonIO);
    }

    @Test
    public void shallReadFileFromFileIO() throws Exception {
        when(fileIO.read(any())).thenReturn(CONTENTS);
        when(jsonIO.fromJson(any(), any())).thenReturn(MANIFEST);
        sut.read(PATH);
        verify(fileIO).read(PATH);
    }

    @Test
    public void shallReturnManifestObjectReturnedByJsonIO() throws Exception {
        when(fileIO.read(any())).thenReturn(CONTENTS);
        when(jsonIO.fromJson(any(), any())).thenReturn(MANIFEST);
        assertEquals(MANIFEST, sut.read(PATH));
        verify(jsonIO).fromJson(CONTENTS, Manifest.class);
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowWhenNullReturnedByJsonIO() throws Exception {
        when(fileIO.read(any())).thenReturn(CONTENTS);
        when(jsonIO.fromJson(any(), any())).thenReturn(null);
        sut.read(PATH);
    }

    @Test(expected = LackOfFieldException.class)
    public void shallThrowWhenManifestDoesNotContainIniFiles() throws Exception {
        when(fileIO.read(any())).thenReturn(CONTENTS);
        when(jsonIO.fromJson(any(), any())).thenReturn(new Manifest());
        sut.read(PATH);
    }

    @Test(expected = InvalidNumberOfElementsInArrayException.class)
    public void shallThrowWhenExpressionFormatIsIncorrect() throws Exception {
        when(fileIO.read(any())).thenReturn(CONTENTS);
        when(jsonIO.fromJson(any(), any())).thenReturn(new Manifest(Arrays.asList("dummyFile"), null, Arrays.asList("onlyOne")));
        sut.read(PATH);
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowWhenFileIOThrows() throws Exception {
        when(fileIO.read(any())).thenThrow(new IOException());
        sut.read(PATH);
    }

    @Test(expected = InvalidJsonException.class)
    public void shallThrowWhenJsonIOThrows() throws Exception {
        when(fileIO.read(any())).thenReturn(CONTENTS);
        when(jsonIO.fromJson(any(), any())).thenThrow(new JsonSyntaxException("message"));
        sut.read(PATH);
    }

}
