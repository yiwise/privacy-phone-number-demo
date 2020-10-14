package com.yiwise.esl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.yiwise"})
@SpringBootApplication
public class EslApplication {
    private static Logger logger = LoggerFactory.getLogger(EslApplication.class);

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EslApplication.class);
        springApplication.run(args);
        logger.info("=============================spring boot start successful !=============================");
    }
}
