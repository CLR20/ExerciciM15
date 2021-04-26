package com.luckyseven.application.entities;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="luckysevenrounds")
public class LuckySevenRounds {
	
	@Id
	private UUID id;
	private String date;	
	private int firstDice;
	private int secondDice;
	private int result;
	private String won;
	private Users user;
	@ManyToOne()
	private UUID userId;
	
	public LuckySevenRounds() {}
	
	public LuckySevenRounds (Users u) {
		id = UUID.randomUUID();
		date = Calendar.getInstance().getTime().toString();
		Random r = new Random();
		firstDice = 1 + r.nextInt(5);
		secondDice = 1 + r.nextInt(5);
		result = firstDice + secondDice;
		if (result == 7) {
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
	
	public int getFirstDice() {
		return firstDice;
	}
	
	public int getSecondDice() {
		return secondDice;
	}
	
	public int getResult() {
		return result;
	}
	
	public String getWon() {
		return won;
	}
	
	public Users getUser() {
		return user;
	}
	
	public UUID getUserId() {
		return userId;
	}
}
