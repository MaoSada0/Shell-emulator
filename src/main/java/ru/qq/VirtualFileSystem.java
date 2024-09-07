package ru.qq;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;


public class VirtualFileSystem {
    private ZipFile zipFile;
    private String currentDir;
    private File tempDir;

    public VirtualFileSystem(String zipPath) throws IOException {
        this.zipFile = new ZipFile(zipPath);
        this.currentDir = "/";
        this.tempDir = Files.createTempDirectory("vfs").toFile();
    }

    public String getCurrentDir() {
        return currentDir;
    }
}
