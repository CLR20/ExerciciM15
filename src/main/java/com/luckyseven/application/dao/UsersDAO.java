package com.luckyseven.application.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckyseven.application.entities.Users;

@Repository
public interface UsersDAO extends JpaRepository<Users, Long> {
	/*public Users findByName(String name);*/
}

