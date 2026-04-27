package com.example.waterbill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class WaterbillApplication {

    public static void main(String[] args) {

        SpringApplication.run(WaterbillApplication.class, args);
    }

}
