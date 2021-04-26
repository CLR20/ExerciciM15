package com.luckyseven.application.entities;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import javax.persistence.*;

@Entity
@Table(name="samedicerounds")
public class SameDiceRounds {
	@Id
	private UUID id;
	private String date;
	private int firstDice;
	private int secondDice;
	private String won;
	private Users user;
	@ManyToOne()
	private UUID userId;
	
	public SameDiceRounds() {}
	
	public SameDiceRounds(Users u) {
		id = UUID.randomUUID();
		date = Calendar.getInstance().getTime().toString();
		Random r = new Random();
		firstDice = 1 + r.nextInt(5);
		secondDice = 1 + r.nextInt(5);
		if (firstDice == secondDice) {
			won ="Hooray, yes!!";
		} else {
			won = "Yay, no!";
		}
		user = u;
		userId = user.getId();		
	}
	
	public void setId(UUID i) {
		id = i;		
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setDate(String d) {
		date = d;		
	}
	
	public String getDate() {
		return date;
	}
	
	public void setFirstDice(int fd) {
		firstDice = fd;		
	}
	
	public int getFirstDice() {
		return firstDice;
	}
	
	public void setSecondDice(int sd) {
		secondDice = sd;		
	}
	
	public int getSecondDice() {
		return secondDice;
	}
	
	public void setWon(String w) {
		won = w;		
	}
	
	public String getWon() {
		return won;
	}
	
	public void setUser(Users u) {
		user = u;		
	}
	
	public Users getUser() {
		return user;
	}
	
	public void setUserId(UUID ui) {
		userId = ui;		
	}
	
	public UUID getUserId() {
		return userId;
	}
	
}
