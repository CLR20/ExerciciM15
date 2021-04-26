package com.luckyseven.application.entities;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import javax.persistence.*;

@Entity
@Table(name="higherdicerounds")
public class HigherDiceRounds {
	@Id
	private UUID id;
	private String date;
	private int yourDice;
	private int computerDice;
	private String won;
	private Users user;
	@ManyToOne()
	private UUID userId;
	
	public HigherDiceRounds() {}
	
	public HigherDiceRounds(Users u) {
		id = UUID.randomUUID();
		date = Calendar.getInstance().getTime().toString();
		Random r = new Random();
		yourDice = 1 + r.nextInt(5);
		computerDice = 1 + r.nextInt(5);
		if (yourDice > computerDice) {
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
	
	public void setYourDice(int yd) {
		yourDice = yd;		
	}
	
	public int getYourDice() {
		return yourDice;
	}
	
	public void setComputerDice(int cd) {
		computerDice = cd;		
	}
	
	public int getComputerDice() {
		return computerDice;
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
