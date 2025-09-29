package com.lottery;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Configurable
@EnableScheduling
@EnableTransactionManagement //开启注解方式的事务管理
@EnableCaching
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

}
