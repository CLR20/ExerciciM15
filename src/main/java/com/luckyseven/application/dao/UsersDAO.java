package com.luckyseven.application.dao;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.luckyseven.application.entities.Users;

@Repository
public interface UsersDAO extends MongoRepository<Users, UUID> {
	public Users findByName(String name);
}

