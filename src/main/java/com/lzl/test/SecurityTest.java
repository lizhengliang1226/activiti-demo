package com.lzl.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author LZL
 * @version v1.0
 * @date 2022/9/7-10:41
 */
public class SecurityTest {
    public static void main(String[] args) {
testSecurity();
    }
    public static void testSecurity() {
// Create an encoder with strength 16
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        String result = encoder.encode("myPassword");
        System.out.println(encoder.matches("myPassword", result));

    }

}