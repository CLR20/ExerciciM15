package com.luckyseven.application.entities;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.mongodb.core.mapping.Document;

//@Entity
@Document(collection="users")
public class Users {

	@Id
	//@GeneratedValue(strategy=GenerationType.IDENTITY)
	private UUID id;
	private String name;
	private String alias;
	private String registered;
	private int lsroundsPlayed;
	private int lswonRounds;
	private double lssuccessRate;
	private int sdroundsPlayed;
	private int sdwonRounds;
	private double sdsuccessRate;
	private int hdroundsPlayed;
	private int hdwonRounds;
	private double hdsuccessRate;
	@OneToMany(mappedBy= "user", cascade=CascadeType.REMOVE)
	List<LuckySevenRounds> lsRounds;
	@OneToMany(mappedBy= "user", cascade=CascadeType.REMOVE)
	List<SameDiceRounds> sdRounds;
	@OneToMany(mappedBy= "user", cascade=CascadeType.REMOVE)
	List<HigherDiceRounds> hdRounds;
	
	public Users() {}
	
	public Users (String name, String option) {
		id = UUID.randomUUID();
		this.name = name;
		if (option.equals("YES")) {
			alias = "Anonymous";
		} else {
			alias = name;
		}
		registered = Calendar.getInstance().getTime().toString();
	}
	
	public void setId(UUID i) {
		id = i;
	}
	public UUID getId() {
		return id;
	}
	
	public void setName(String n) {
		name = n;
	}
	public String getName() {
		return name;
	}
	
	public void setAlias(String a) {
		alias = a;
	}
	public String getAlias() {
		return alias;
	}
	
	public void setResgistered(String d) {
		registered = d;
	}
	public String getRegistered() {
		return registered;
	}
	
	public void setLsRoundsPlayed(int r) {
		lsroundsPlayed = r;
	}
	
	public int getLsRoundsPlayed() {
		return lsroundsPlayed;
	}
	
	public void setLsWonRounds(int w) {
		lswonRounds = w;
	}
	
	public int getLsWonRounds() {
		return lswonRounds;
	}
	
	public void setLsSuccessRate(double s) {
		lssuccessRate = s;
	}
	
	public double getLsSuccessRate() {
		return lssuccessRate;
	}
	
	public void setSdRoundsPlayed(int r) {
		sdroundsPlayed = r;
	}
	
	public int getSdRoundsPlayed() {
		return sdroundsPlayed;
	}
	
	public void setSdWonRounds(int w) {
		sdwonRounds = w;
	}
	
	public int getSdWonRounds() {
		return sdwonRounds;
	}
	
	public void setSdSuccessRate(double s) {
		sdsuccessRate = s;
	}
	
	public double getSdSuccessRate() {
		return sdsuccessRate;
	}
	
	public void setHdRoundsPlayed(int r) {
		hdroundsPlayed = r;
	}
	
	public int getHdRoundsPlayed() {
		return hdroundsPlayed;
	}
	
	public void setHdWonRounds(int w) {
		hdwonRounds = w;
	}
	
	public int getHdWonRounds() {
		return hdwonRounds;
	}
	
	public void setHdSuccessRate(double s) {
		hdsuccessRate = s;
	}
	
	public double getHdSuccessRate() {
		return hdsuccessRate;
	}
	
	@Override
    public String toString() {
        return "User{" + ", name=" + name + ", registered=" + registered + "}";
    }
	
}
