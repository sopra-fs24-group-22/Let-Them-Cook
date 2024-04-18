package com.letthemcook.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
class ApplicationConfiguration {
  @Value("${spring.data.mongodb.uri}")
  String uri;

  @Value("${spring.data.mongodb.database}")
  String database;

  @Bean
  MongoClient mongoClient() {
    return MongoClients.create(uri);
  }

  @Bean
  MongoOperations mongoTemplate(MongoClient mongoClient) {
    return new MongoTemplate(mongoClient, database);
  }
}