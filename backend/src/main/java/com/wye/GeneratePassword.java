package com.wye;

import cn.hutool.crypto.digest.BCrypt;

public class GeneratePassword {
    public static void main(String[] args) {
        String password = "12345678";
        String hash = BCrypt.hashpw(password);
        System.out.println("BCrypt Hash for '12345678':");
        System.out.println(hash);
        System.out.println("\nVerification:");
        System.out.println(BCrypt.checkpw(password, hash));
    }
}
