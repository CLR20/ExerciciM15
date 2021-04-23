package com.luckyseven.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class AppConfig {

  /*
   * Use the standard Mongo driver API to create a com.mongodb.client.MongoClient instance.
   */
   public @Bean MongoClientFactoryBean mongo() {
	   MongoClientFactoryBean mongo = new MongoClientFactoryBean();
       mongo.setHost("localhost");
       return mongo;
   }
}