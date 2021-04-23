package com.luckyseven.application.entities;

import java.util.Calendar;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;

import com.luckyseven.application.dao.UsersDAO;

@Entity
@Table(name="rounds")
public class Rounds {
	
	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;
	private int firstDice;
	private int secondDice;
	private int result;
	private String won;
	private String date;	
	private Users user;
//	@ManyToOne()
	private String userId;
	
	private Rounds() {}
	
	public Rounds (Users u) {
		Random r = new Random();
		firstDice = 1 + r.nextInt(5);
		secondDice = 1 + r.nextInt(5);
		result = firstDice + secondDice;
		date = Calendar.getInstance().getTime().toString();
		if (result == 7) {
			won ="Hooray, yes!!";
		} else {
			won = "Yay, no!";
		}
		user = u;
		userId = user.getId();
	}
	
	public void setId(String i) {
		id = i;		
	}
	
	public String getId() {
		return id;
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
	
	public void setDate(String d) {
		date = d;
	}
	
	public String getDate() {
		return date;
	}
	
	public Users getUser() {
		return user;
	}
	
	public String getUserId() {
		return userId;
	}
}
