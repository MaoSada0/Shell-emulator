package ru.qq;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;


public class VirtualFileSystem {
    private ZipFile zipFile;
    private String currentDir;
    private File tempDir;
    private String name;
    private TreeNode tree;

    public VirtualFileSystem(String zipPath) throws IOException {
        this.zipFile = new ZipFile(zipPath);

        String[] parts = zipFile.getName().split("\\\\");
        name = parts[parts.length - 1].substring(0, parts[parts.length - 1].lastIndexOf('.'));

        this.currentDir = name + "/";
        this.tempDir = Files.createTempDirectory("vfs").toFile();

        tree = new TreeNode(name, false);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();

            System.out.println(entryName);

            tree.insert(entryName.split("/"), !entry.isDirectory());
        }
    }

    public List<String> ls() {
        List<TreeNode> files = tree.getChildrenByNode(currentDir.split("/"));

        List<String> ans = new ArrayList<>();

        for(TreeNode node: files){
            if(node.isFile()) ans.add(node.getPart());
            else ans.add(node.getPart() + "/");
        }

        return ans;

    }

    public void cd(String newDir) {
        if(newDir.equals(".")) return;
        if(newDir.equals("/")) currentDir = name + "/";

        if(newDir.equals("..")){
            String[] parts = currentDir.split("/");

            String temp = name + "/";

            if(parts.length == 2){
                currentDir =temp;
                return;
            }

            for (int i = 0; i < parts.length - 1; i++) {
                temp = temp + parts[i];

                if(i != parts.length - 2) temp += "/";
            }

            currentDir = temp;
        }
    }

    public String getCurrentDir() {
        return currentDir;
    }
}
