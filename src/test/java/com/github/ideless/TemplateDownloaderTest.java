package com.github.ideless;

import java.nio.file.Files;
import java.util.stream.Collectors;
import org.eclipse.jgit.api.errors.GitAPIException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

public class TemplateDownloaderTest {

    private static final String GITHUB_TEST_URL = "git://github.com/PiotrSliwa/ideless-test-template-basic.git";

    private UserIO userIO;
    private TemplateDownloader sut;

    @Before
    public void beforeTest() {
        userIO = mock(UserIO.class);
        sut = new TemplateDownloader(userIO);
    }

    @Test(expected = InvalidDownloadableTemplateException.class)
    public void shallThrowWhenNullProvided() throws Exception {
        sut.download(null);
    }

    @Test(expected = InvalidDownloadableTemplateException.class)
    public void shallThrowWhenInvalidUrlProvided() throws Exception {
        sut.download("invalidUrl");
    }

    @Test(expected = InvalidDownloadableTemplateException.class)
    public void shallThrowWhenNoGitExtensionProvided() throws Exception {
        sut.download("git://github.com/PiotrSliwa/ideless-test-template-basic");
    }

    @Test(expected = InvalidDownloadableTemplateException.class)
    public void shallThrowWhenNoPrefixProvided() throws Exception {
        sut.download("github.com/PiotrSliwa/ideless-test-template-basic.git");
    }

    @Test(expected = GitAPIException.class)
    public void shallThrowWhenInvalidGitRepoProvided() throws Exception {
        sut.download("git://github.com/PiotrSliwa/ideless-test-template-basic-INVALID.git");
    }

    @Test
    public void shallCloneGitRepositoryWithAllItsContents() throws Exception {
        Template template = sut.download(GITHUB_TEST_URL);

        assertTrue(Files.exists(template.getPath()));
        assertTrue(Files.isDirectory(template.getPath()));
        assertTrue(Files.exists(template.getPath().resolve(".ideless")));
        assertTrue(Files.exists(template.getPath().resolve("file1")));

        final String expectedManifest = "{\"initFiles\":[\"file1\"]}";
        final String expectedFile1 = "some data";
        String manifest = Files.lines(template.getPath().resolve(".ideless")).collect(Collectors.joining());
        String file1 = Files.lines(template.getPath().resolve("file1")).collect(Collectors.joining());

        assertEquals(expectedManifest, manifest);
        assertEquals(expectedFile1, file1);
    }

    @Test
    public void shallInformUserAboutDownloadingRepo() throws Exception {
        sut.download(GITHUB_TEST_URL);
        Mockito.verify(userIO).println("Downloading template: " + GITHUB_TEST_URL);
    }

}
