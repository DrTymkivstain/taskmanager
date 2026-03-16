package com.example.taskmanager.util;

public class PasswordUtil {
    public static String hashPassword(String password) {
        return password;
    }
    public static boolean checkPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
}
