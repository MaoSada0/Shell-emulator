package ru.qq;
import ru.qq.utils.FolderToZip;
import ru.qq.utils.TreeNode;
import ru.qq.utils.ZipUnpacker;
import ru.qq.utils.interfaces.VirtualFileSystem;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.IntStream;
import java.util.zip.*;


public class VirtualFileSystemImpl implements VirtualFileSystem {
    private ZipFile zipFile;
    private String currentDir;
    private String name;
    private TreeNode tree;
    private long startTime;

    public VirtualFileSystemImpl(String zipPath) throws IOException {
        this.startTime = System.currentTimeMillis();

        this.zipFile = new ZipFile(zipPath);

        String[] parts = zipFile.getName().split("\\\\");

        name = parts[parts.length - 1].substring(0, parts[parts.length - 1].lastIndexOf('.'));

        this.currentDir = name + "/";

        updateTree();

    }

    @Override
    public List<String> ls() {
        List<TreeNode> files = tree.getChildrenByNode(currentDir.split("/"));

        List<String> ans = new ArrayList<>();

        for(TreeNode node: files){
            if(node.isFile()) ans.add(node.getPart());
            else ans.add(node.getPart() + "/");
        }

        return ans;

    }

    @Override
    public void cd(String newDir) {
        String fullPath = currentDir + newDir;
        String[] pathParts = fullPath.split("/");

        if (IntStream.range(0, newDir.length() - 1)
                .anyMatch(i -> newDir.charAt(i) == '/' && newDir.charAt(i + 1) == '/')) {
            System.out.println("No such file or directory");
            return;
        }


        if(newDir.equals(".")) return;
        else if(newDir.equals("/")) currentDir = name + "/";
        else if(newDir.equals("..")){
            String[] parts = currentDir.split("/");

            if(parts.length <= 2) {
                currentDir = name + "/";
                return;
            }

            currentDir = String.join("/", Arrays.copyOf(parts, parts.length - 1)) + "/";
        }
        else if(!tree.containsPath(pathParts)  || tree.isFile(pathParts)) {
            System.out.println("No such directory");
        }
        else if(tree.containsPath(pathParts))
            currentDir = currentDir + newDir + "/";
        else System.out.println("No such directory");
    }

    @Override
    public void cat(String fileName) throws IOException {
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

    @Override
    public void cp(String filename, String directory) throws IOException {
        String currentDirFormatted = currentDir.replace("/", "\\");
        String directoryFormatted = directory.replace("/", "\\");

        if(directory.equals("/")){
            directory = Configuration.getZipDirectoryPathInside() + "\\" + currentDirFormatted;
        }
        else if(directory.startsWith("./")) {
            directory = Configuration.getZipDirectoryPathInside() + "\\" +
                    directoryFormatted.substring(2);

        } else {
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

        if(Files.isDirectory(sourcePath)){
            helper(sourcePath, targetPath);
        } else{
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        zipFile.close();
        Files.delete(Paths.get(Configuration.getZipPathStatic()));

        FolderToZip.zipDirectory(Paths.get(Configuration.getZipDirectoryPathInside()),
                Paths.get(Configuration.getZipDirectoryPathInside() + ".zip"));

        zipFile = new ZipFile(Configuration.getZipPathStatic());
        deleteDirectory(Paths.get(Configuration.getZipDirectoryPathInside()));

        updateTree();
    }

    private static void helper(Path sourcePath, Path targetPath) throws IOException {
        Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(final Path dir,
                                                     final BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(targetPath.resolve(sourcePath
                        .relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file,
                                             final BasicFileAttributes attrs) throws IOException {
                Files.copy(file,
                        targetPath.resolve(sourcePath.relativize(file)));
                return FileVisitResult.CONTINUE;
            }
        });
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
    @Override
    public String uptime(){
        long uptimeMillis = System.currentTimeMillis() - startTime;
        long uptimeSeconds = uptimeMillis / 1000;
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        return String.format("Uptime: %02d:%02d:%02d", hours, minutes, seconds);
    }

    private void updateTree(){
        tree = new TreeNode(name, false);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();
            tree.insert(entryName.split("/"), !entry.isDirectory());
        }
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }


    @Override
    public String pwd() {
        return currentDir;
    }
}
