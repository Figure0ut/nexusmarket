package application.infrastructure.adapters.out.sql.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "application.infrastructure.adapters.out.sql.repository")
@EntityScan(basePackages = "application.infrastructure.adapters.out.sql.entity")
public class JpaDatabaseConfig {
}
