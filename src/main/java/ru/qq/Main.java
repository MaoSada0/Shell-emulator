package ru.qq;
public class Main {
    public static void main(String[] args) {
        Configuration configuration = new Configuration("application.yaml");

        System.out.println(configuration.getUsername());
    }
}