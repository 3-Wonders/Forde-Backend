package com.project.forde;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class FordeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FordeApplication.class, args);
    }

}
