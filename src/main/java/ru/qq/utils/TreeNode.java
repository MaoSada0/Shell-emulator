package ru.qq.utils;

import java.util.*;


public class TreeNode {
    private String part;
    private boolean isFile;
    List<TreeNode> children;

    public TreeNode(String part, boolean isFile) {
        this.part = part;
        this.isFile = isFile;
        children = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "TreeNode{" +
                "part='" + part + '\'' +
                '}';
    }

    public void insert(String[] parts, boolean isFile) {
        if (parts.length == 0) return;

        if (parts.length == 1) {
            this.part = parts[0];
            this.isFile = isFile;
            return;
        }

        for (TreeNode child : children) {
            if (child.getPart().equals(parts[1])) {
                child.insert(Arrays.copyOfRange(parts, 1, parts.length), isFile);
                return;
            }
        }


        TreeNode newChild = new TreeNode(parts[1], false);
        children.add(newChild);
        newChild.insert(Arrays.copyOfRange(parts, 1, parts.length), isFile);
    }

    public List<TreeNode> getChildrenByNode(String[] parts) {
        if(parts == null) return new ArrayList<>();

        if(parts.length == 1 && parts[0].equals(part)) return this.children;

        for(TreeNode child: children){
            if(child.getPart().equals(parts[1])) return child.getChildrenByNode(Arrays.copyOfRange(parts, 1, parts.length));
        }

        return new ArrayList<>();
    }

    public boolean containsPath(String[] parts) {
        if (parts.length == 0) {
            return false;
        }

        if (parts.length == 1) {
            return this.part.equals(parts[0]);
        }

        for (TreeNode child : children) {
            if (child.getPart().equals(parts[1])) {
                return child.containsPath(Arrays.copyOfRange(parts, 1, parts.length));
            }
        }

        return false;
    }


    public boolean isFile() {
        return isFile;
    }

    public String getPart() {
        return part;
    }
}

