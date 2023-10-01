package com.chatwave.accountservice.utils;

import com.chatwave.accountservice.AccountServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {
    @Bean
    @ServiceConnection
    public PostgreSQLContainer mongoDbContainer() {
        return new PostgreSQLContainer(DockerImageName.parse("postgres:alpine3.18"));
    }

    public static void main(String[] args) {
        SpringApplication.from(AccountServiceApplication::main).with(ContainersConfig.class).run(args);
    }
}