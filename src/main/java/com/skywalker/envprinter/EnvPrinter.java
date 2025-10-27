package com.skywalker.envprinter;

import jakarta.annotation.PostConstruct;
import java.util.Map;

public class EnvPrinter {

    @PostConstruct
    public void printEnv() {
        System.out.println("===============================");
        System.out.println("🌍 Environment Variables");
        System.out.println("===============================");
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("===============================");
    }
}
