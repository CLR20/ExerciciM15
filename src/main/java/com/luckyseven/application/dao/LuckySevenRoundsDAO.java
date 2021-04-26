package com.luckyseven.application.dao;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.luckyseven.application.entities.LuckySevenRounds;

@Repository
public interface LuckySevenRoundsDAO extends MongoRepository<LuckySevenRounds, UUID> {

}
