package com.atguigu.yygh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
public class ServerGatewayApplication {


    public static void main(String[] args) {
        SpringApplication.run(ServerGatewayApplication.class,args);
    }
}
