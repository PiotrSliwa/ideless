package com.github.ideless;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserIO {

    private final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public void print(String message) {
        System.out.print(message);
    }

    public void println(String message) {
        System.out.println(message);
    }

    public String read() throws IOException {
        return bufferedReader.readLine();
    }

}
