package ru.qq;
import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.out.println("Not correct args");
//            System.exit(1);
//        }

        String configPath = "C:\\Users\\user\\IdeaProjects\\config_task1\\src\\main\\resources\\application.yaml"; // args[0]
        Configuration configuration = new Configuration(configPath);

        String username = configuration.getUsername();
        String hostname = configuration.getHostname();
        String zipPath = configuration.getZipPath();

        if (zipPath.isEmpty()) {
            System.out.println("Error: zip-path is missing in config.");
            System.exit(1);
        }

        try {
            VirtualFileSystem vfs = new VirtualFileSystem(zipPath);
            Shell shell = new Shell(vfs, username, hostname);
            shell.start();
        } catch (IOException e) {
            System.out.println("Error initializing virtual file system: " + e.getMessage());
        }
    }
}
