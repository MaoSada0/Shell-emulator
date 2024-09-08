package ru.qq.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FolderToZip {

    public static void zipDirectory(Path folderPath, Path zipFilePath) throws IOException {

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Path rootPath = folderPath.getFileName();
            Files.walk(folderPath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        Path zipEntryPath = rootPath.resolve(folderPath.relativize(path)).normalize();
                        ZipEntry zipEntry = new ZipEntry(zipEntryPath.toString().replace("\\", "/"));
                        try {
                            zipOutputStream.putNextEntry(zipEntry);
                            Files.copy(path, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            System.err.println("Error in FolderToZip: " + path + " - " + e);
                        }
                    });
        }

    }

}
