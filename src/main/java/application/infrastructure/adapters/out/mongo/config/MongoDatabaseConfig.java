package application.infrastructure.adapters.out.mongo.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.InetSocketAddress;

@Configuration
@EnableMongoRepositories(basePackages = "application.infrastructure.adapters.out.mongo.repository")
public class MongoDatabaseConfig {

    private MongoServer server;

    @Value("${spring.data.mongodb.uri:}")
    private String customMongoUri;

    @Bean
    public MongoClient mongoClient() {
        if (customMongoUri != null && !customMongoUri.trim().isEmpty()) {
            return MongoClients.create(customMongoUri);
        }

        // Start embedded in-memory MongoDB wire server on a free port
        server = new MongoServer(new MemoryBackend());
        InetSocketAddress serverAddress = server.bind();
        String connectionString = "mongodb://" + serverAddress.getHostString() + ":" + serverAddress.getPort() + "/nexusmarket_catalog";
        return MongoClients.create(connectionString);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "nexusmarket_catalog");
    }

    @PreDestroy
    public void shutdown() {
        if (server != null) {
            server.shutdown();
        }
    }
}
