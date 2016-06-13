package com.github.ideless;

public class InvalidDownloadableTemplateException extends Exception {

    public InvalidDownloadableTemplateException(String reason) {
        super("Invalid downloadable template (" + reason + ")");
    }

}
