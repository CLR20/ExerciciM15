package com.luckyseven.application.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.luckyseven.application.entities.Users;

@Repository
public interface UsersDAO extends MongoRepository<Users, Long> {
	public Users findByName(String name);
}

