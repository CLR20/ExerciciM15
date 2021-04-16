package com.luckyseven.application.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckyseven.application.entities.Rounds;

@Repository
public interface RoundsDAO extends JpaRepository<Rounds, Long> {

}
