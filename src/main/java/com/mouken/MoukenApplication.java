package com.mouken;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class MoukenApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MoukenApplication.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
    }

}
