package com.luckyseven.application.entities;

import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class Users {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String name;
	private String registered;
	private int roundsPlayed;
	private int wonRounds;
	private double successRate;
	@OneToMany(mappedBy= "user", cascade=CascadeType.REMOVE)
	List<Rounds> rounds;
	
	public Users() {}
	
	public Users (String name) {
		this.name = name;
		registered = Calendar.getInstance().getTime().toString();
	}
	
	public void setId(long i) {
		id = i;
	}
	public Long getId() {
		return id;
	}
	
	public void setName(String n) {
		name = n;
	}
	public String getName() {
		return name;
	}
	
	public void setResgistered(String d) {
		registered = d;
	}
	public String getRegistered() {
		return registered;
	}
	
	public void setRoundsPlayed(int r) {
		roundsPlayed = r;
	}
	
	public int getRoundsPlayed() {
		return roundsPlayed;
	}
	
	public void setWonRounds(int w) {
		wonRounds = w;
	}
	
	public int getWonRounds() {
		return wonRounds;
	}
	
	public void setSuccessRate(double s) {
		successRate = s;
	}
	
	public double getSuccessRate() {
		return successRate;
	}
	
}
