package com.atguigu.yygh.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu")
public class ServiceHostApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHostApplication.class,args);
    }
}
