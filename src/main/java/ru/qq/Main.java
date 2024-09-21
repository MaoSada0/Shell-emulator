package ru.qq;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Not correct args");
            System.exit(1);
        }

        String configPath = args[0];
        Configuration configuration = new Configuration(configPath);

        //ZipUnpacker.unpackZip(configuration.getZipPath(), Configuration.getZipDirectoryPath());

        String username = configuration.getUsername();
        String hostname = configuration.getHostname();
        String zipPath = configuration.getZipPath();

        if (zipPath.isEmpty()) {
            System.out.println("Error: zip-path is missing in config.");
            System.exit(1);
        }

        try {
            VirtualFileSystemImpl vfs = new VirtualFileSystemImpl(zipPath);
            Shell shell = new Shell(vfs, username, hostname);
            shell.start();
        } catch (IOException e) {
            System.out.println("Error initializing virtual file system: " + e.getMessage());
        }
    }
}
