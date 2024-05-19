package org.example.orm.totp;

import dev.samstevens.totp.exceptions.CodeGenerationException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author Spike_Zhang
 * @description: TOTP2Utils TODO
 * @date 2024/5/19 10:51
 */
public class TOTP2Utils {

    public static void main(String[] args) throws CodeGenerationException {

        CustomerCodeGenerator generator = new CustomerCodeGenerator();
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0);
        long counter = now.toEpochSecond(ZoneOffset.UTC)/3600;
        String code = generator.generate("364ADD65C42A77D4603A0854245B1F05", counter);
        System.out.println(code);
    }
}
