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

    public List<String> ls() {
        Set<String> files = new HashSet<>();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();

            String[] parts = entryName.split("/");

            files.add(parts[1]);
        }

        return new ArrayList<>(files);
    }

    public String getCurrentDir() {
        return currentDir;
    }
}
