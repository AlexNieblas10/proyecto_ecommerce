package org.ecommerce.util;

public class HashGenerator {
    public static void main(String[] args) {
        String password = args.length > 0 ? args[0] : "admin123";
        System.out.println(PasswordUtil.hash(password));
    }
}
