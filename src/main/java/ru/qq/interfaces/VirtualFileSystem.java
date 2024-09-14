package ru.qq.interfaces;

import java.io.IOException;
import java.util.List;

public interface VirtualFileSystem {

    List<String> ls();
    String pwd();

    void cd(String newDir);

    void cat(String fileName) throws IOException;

    void cp(String filename, String directory) throws IOException;

    String uptime();
}
