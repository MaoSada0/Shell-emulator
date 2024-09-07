package ru.qq;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;


public class VirtualFileSystem {
    private ZipFile zipFile;
    private String currentDir;
    private String name;
    private TreeNode tree;

    public VirtualFileSystem(String zipPath) throws IOException {
        this.zipFile = new ZipFile(zipPath);

        String[] parts = zipFile.getName().split("\\\\");
        name = parts[parts.length - 1].substring(0, parts[parts.length - 1].lastIndexOf('.'));

        this.currentDir = name + "/";

        tree = new TreeNode(name, false);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();

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
        else if(newDir.equals("/")) currentDir = name + "/";
        else if(newDir.equals("..")){
            String[] parts = currentDir.split("/");

            String temp = name + "/";

            if(parts.length == 2){
                currentDir = temp;
                return;
            }

            for (int i = 0; i < parts.length - 1; i++) {
                temp = temp + parts[i];

                if(i != parts.length - 2) temp += "/";
            }

            currentDir = temp;
        }
        else if(tree.containsPath((currentDir + newDir).split("/"))) currentDir = currentDir + newDir + "/";
        else System.out.println(" No such file or directory");
    }

    public void readFile(String fileName) throws IOException {
        String fullPath = currentDir + fileName;
        ZipEntry entry = zipFile.getEntry(fullPath);

        if (entry == null) {
            System.out.println("cat: " + fileName + ": No such file");
            return;
        }

        try (InputStream stream = zipFile.getInputStream(entry)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    public String getCurrentDir() {
        return currentDir;
    }
}
