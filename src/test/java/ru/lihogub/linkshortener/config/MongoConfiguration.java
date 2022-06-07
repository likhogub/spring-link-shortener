package ru.lihogub.linkshortener.config;

import com.mongodb.WriteConcern;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import ru.lihogub.linkshortener.repository.AliasRepository;

@Configuration
public class MongoConfiguration implements InitializingBean, DisposableBean {

    MongodExecutable executable;

    @Override
    public void afterPropertiesSet() throws Exception {
        int port = 27017;

        MongodStarter starter = MongodStarter.getDefaultInstance();

        MongodConfig mongodConfig = MongodConfig.builder()
                .version(Version.Main.V3_6)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();
        ;
        executable = starter.prepare(mongodConfig);
        executable.start();
    }

    @Bean
    public MongoDatabaseFactory factory() {
        return new SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/imager200_test");
    }


    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory) {
        MongoTemplate template = new MongoTemplate(mongoDbFactory);
        template.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return template;
    }

    @Bean
    public MongoRepositoryFactoryBean mongoFactoryRepositoryBean(MongoTemplate template) {
        MongoRepositoryFactoryBean mongoDbFactoryBean = new MongoRepositoryFactoryBean(AliasRepository.class);
        mongoDbFactoryBean.setMongoOperations(template);

        return mongoDbFactoryBean;
    }

    @Override
    public void destroy() throws Exception {
        executable.stop();
    }
}