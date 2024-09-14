package ru.qq;

import ru.qq.interfaces.VirtualFileSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Shell {
    private static VirtualFileSystem vfs;
    private String username;
    private String hostname;


    public Shell(VirtualFileSystem vfs, String username, String hostname) {
        this.vfs = vfs;
        this.username = username;
        this.hostname = hostname;
    }

    public void start() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(username + "@" + hostname + ":~$ ");
            String input = scanner.nextLine();
            executeCommand(input);
        }
    }

    private static void executeScript(String scriptFilePath) {
        File scriptFile = new File(scriptFilePath);
        if (!scriptFile.exists()) {
            System.err.println("Script file not found: " + scriptFilePath);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(scriptFile))) {
            String line = br.readLine();
            while (line != null) {
                executeCommand(line.trim());
                line = br.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error reading script file: " + e.getMessage());
        }
    }

    private static void executeCommand(String input) throws IOException {
        if (input.isEmpty()) {
            return;
        }

        String[] parts = input.trim().split("\\s+");
        String command = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, parts.length - 1);

        switch (command) {
            case "exit":
                System.out.println("Exiting shell...");
                System.exit(1);
            case "ls":
                vfs.ls().forEach(System.out::println);
                break;
            case "cd":
                if (args.length > 0) {
                    vfs.cd(args[0]);
                } else {
                    System.out.println("cd: missing argument");
                }
                break;
            case "pwd":
                System.out.println(vfs.pwd());
                break;
            case "cat":
                if (args.length > 0) {
                    vfs.cat(args[0]);
                } else {
                    System.out.println("cat: missing argument");
                }
                break;
            case "--script":
                if (args.length > 0) {
                    executeScript(args[0]);
                } else {
                    System.out.println("--script: missing argument");
                }
                break;
            case "uptime":
                System.out.println(vfs.uptime());
                break;
            case "cp":
                if (args.length == 2) {
                    vfs.cp(args[0], args[1]);
                } else {
                    System.out.println("cp: missing source or destination");
                }
                break;
            default:
                System.out.println(command + ": command not found");
                break;
        }
    }

}
