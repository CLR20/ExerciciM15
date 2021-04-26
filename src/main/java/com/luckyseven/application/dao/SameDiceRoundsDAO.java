package com.luckyseven.application.dao;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.luckyseven.application.entities.SameDiceRounds;

public interface SameDiceRoundsDAO extends MongoRepository <SameDiceRounds, UUID>{

}
