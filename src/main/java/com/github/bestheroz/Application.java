package com.github.bestheroz;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(
    scanBasePackages = "com.github.bestheroz",
    exclude = {JpaRepositoriesAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@EnableTransactionManagement
public class Application {
  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
