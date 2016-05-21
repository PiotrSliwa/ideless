package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.github.ideless.GsonWrapper;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ManifestReaderTest {

    private final static Path DUMMY_PATH = Paths.get("dummyPath");
    private final static String DUMMY_STRING = "dummyString";

    private FileIO fileIO;
    private GsonWrapper gsonWrapper;
    private ManifestReader sut;

    @Before
    public void beforeTest() {
        fileIO = mock(FileIO.class);
        gsonWrapper = mock(GsonWrapper.class);
        sut = new ManifestReader(fileIO, gsonWrapper);
    }

    @Test
    public void shallReadFileFromFileIO() throws Exception {
        when(fileIO.read(any())).thenReturn(DUMMY_STRING);
        sut.read(DUMMY_PATH);
        verify(fileIO).read(DUMMY_PATH);
    }

    @Test
    public void shallReturnManifestObjectReturnedByJsonParser() throws IOException {
        final Manifest manifest = new Manifest(Arrays.asList("dummyFile"));

        when(fileIO.read(any())).thenReturn(DUMMY_STRING);
        when(gsonWrapper.fromJson(DUMMY_STRING, Manifest.class)).thenReturn(manifest);

        assertEquals(manifest, sut.read(DUMMY_PATH));
    }

}
