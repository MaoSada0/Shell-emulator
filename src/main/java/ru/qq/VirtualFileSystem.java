package ru.qq;
import ru.qq.utils.FolderToZip;
import ru.qq.utils.TreeNode;
import ru.qq.utils.ZipUnpacker;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.*;


public class VirtualFileSystem {
    private ZipFile zipFile;
    private String currentDir;
    private String name;
    private TreeNode tree;
    private long startTime;

    public VirtualFileSystem(String zipPath) throws IOException {
        this.startTime = System.currentTimeMillis();

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

            if(parts.length == 2){
                currentDir = name + "/";
                return;
            }

            String temp = "";
            for (int i = 0; i < parts.length - 1; i++) {
                temp = temp + parts[i];

                if(i != parts.length - 2) temp += "/";
            }


            currentDir = temp;
        }
        else if(tree.containsPath((currentDir + newDir).split("/"))) currentDir = currentDir + newDir + "/";
        else System.out.println("No such file or directory");
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

    public void cp(String filename, String directory) throws IOException {
        String currentDirFormatted = currentDir.replace("/", "\\");
        String directoryFormatted = directory.replace("/", "\\");

        String fileNewDirectory;

        if(directory.equals("/")){
            fileNewDirectory = currentDir + filename;

            directory = Configuration.getZipDirectoryPathInside() + "\\" + currentDirFormatted;

        }
        else if(directory.startsWith("./")) {
            fileNewDirectory = name + "/" + directory + "/" + filename;

            directory = Configuration.getZipDirectoryPathInside() + "\\" +
                    directoryFormatted.substring(2);

        } else {
            fileNewDirectory = currentDir + directory + "/" +filename;

            directory = Configuration.getZipDirectoryPath() + "\\" + currentDirFormatted +
                    directoryFormatted;
        }


        if(filename.startsWith("./")) {
            filename = Configuration.getZipDirectoryPathInside()+ "\\" + filename.substring(2).replace("/", "\\");
        }else {
            if(filename.charAt(0) == '/'){
                StringBuilder temp = new StringBuilder(filename).deleteCharAt(0);
                filename = Configuration.getZipDirectoryPath() + "\\" + currentDirFormatted + (temp.toString()).replace("/", "\\");
            } else {
                filename = Configuration.getZipDirectoryPath() + "\\" + currentDirFormatted + filename.replace("/", "\\");
            }
        }

        tree.insert(fileNewDirectory.split("/"), true);

        ZipUnpacker.unpackZip(Configuration.getZipPathStatic(), Configuration.getZipDirectoryPath());

        Path sourcePath = Paths.get(filename);
        Path targetDir = Paths.get(directory);

        if (!Files.exists(sourcePath)) {
            System.out.println("No such file!");
            return;
        }

        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        Path targetPath = targetDir.resolve(sourcePath.getFileName());

        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        zipFile.close();
        Files.delete(Paths.get(Configuration.getZipPathStatic()));

        FolderToZip.zipDirectory(Paths.get(Configuration.getZipDirectoryPathInside()),
                Paths.get(Configuration.getZipDirectoryPathInside() + ".zip"));

        zipFile = new ZipFile(Configuration.getZipPathStatic());
        deleteDirectory(Paths.get(Configuration.getZipDirectoryPathInside()));

    }

    private static void deleteDirectory(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public String uptime(){
        long uptimeMillis = System.currentTimeMillis() - startTime;
        long uptimeSeconds = uptimeMillis / 1000;
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        return String.format("Uptime: %02d:%02d:%02d", hours, minutes, seconds);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getCurrentDir() {
        return currentDir;
    }
}
