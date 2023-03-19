package kr.yeonkyung.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {"kr.yeonkyung"})
@EntityScan(basePackages = {"kr.yeonkyung"})
@EnableJpaRepositories(basePackages = {"kr.yeonkyung"})
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
