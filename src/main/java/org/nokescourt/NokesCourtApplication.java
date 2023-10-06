package org.nokescourt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NokesCourtApplication {
    public static void main(String[] args) {
        SpringApplication.run(NokesCourtApplication.class, args);
    }
}
