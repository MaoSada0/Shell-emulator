package ru.qq;

import java.io.IOException;
import java.util.Scanner;

public class Shell {
    private VirtualFileSystem vfs;
    private String username;
    private String hostname;


    public Shell(VirtualFileSystem vfs, String username, String hostname) {
        this.vfs = vfs;
        this.username = username;
        this.hostname = hostname;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(username + "@" + hostname + ":" + vfs.getCurrentDir() + "$ ");
            String input = scanner.nextLine();
            String[] parts = input.trim().split("\\s+");
            String command = parts[0];
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, parts.length - 1);

            switch (command) {
                case "exit":
                    System.out.println("Exiting shell...");
                    break;
                case "ls":
                    vfs.ls().forEach(System.out::println);
                    break;
                default:
                    System.out.println(command + ": command not found");
            }
        }
    }
}
