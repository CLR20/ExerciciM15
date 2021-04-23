package com.luckyseven.application.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.persistence.OrderBy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.luckyseven.application.dao.RoundsDAO;
import com.luckyseven.application.dao.UsersDAO;
import com.luckyseven.application.entities.Rounds;
import com.luckyseven.application.entities.Users;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClientFactory;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Controller
@Import(value=MongoClientFactory.class)
@RequestMapping("")
public class GameController {

	@OrderBy("ID ASC")
	@Autowired
	UsersDAO uDao;
	@OrderBy("id ASC")
	@Autowired
	RoundsDAO rDAO;
	@Autowired
	MongoClient mop = MongoClients.create("mongodb://localhost:27017");
	String userName;
	Users user;
	String userId;
	double successAverage;
	String winner;
	String loser;
	
	MongoDatabase db = mop.getDatabase ("luckyseven");
	//MongoOperations mongoOps = new MongoTemplate(new SimpleMongoClientDatabaseFactory(MongoClients.create(), "db"));
	
	//Starting app, asking for user name.
	@GetMapping("/luckyseven")
	@ResponseStatus(HttpStatus.OK)
	public String start(Model model) {

// UNCOMMENT BEFORE FIRST RUN TO CREATE TABLES!!
		//db.createCollection("users");
		//db.createCollection("rounds");
		//Users ann = uDao.save(new Users("Ann"));
		//Users boris = uDao.save(new Users("Boris"));
		List<Users> users = uDao.findAll();
		model.addAttribute("users", users);
		return "start";
	}
	
	// Giving options to user.
	@GetMapping("/luckyseven/options")
	@ResponseStatus(HttpStatus.OK)
	public String options(Model model, String name) {		
		userName = name;
		user = checkNameAndGetUser(userName);
		updateData();
		model.addAttribute("user", user);
		return "options";
	}
	
	// Changing user's name
	@PostMapping("/luckyseven/options/user/name")
	@ResponseStatus(HttpStatus.OK)
	public String changeUserName(Model model) {
		model.addAttribute("user", user);
		return "nameModel";
	}
	
	// Setting new user's name
	@PostMapping("/luckyseven/options/user/name_changed")
	@ResponseStatus(HttpStatus.OK)
	public String changeUserName(Model model, String newName) {
		user.setName(newName);
		uDao.save(user);
		updateData();
		getUserData(user);
		model.addAttribute("user", user);
		return "options";
	}
	
	//Getting users ranking.
	@GetMapping("/luckyseven/options/ranking")
	@ResponseStatus(HttpStatus.OK)
	public String getUsers(Model model) {
		updateData();
		List<Users> users = uDao.findAll();
		successAverage = getAverageSuccess();
		winner = getBestUser();
		loser = getWorstUser();
		model.addAttribute("user", user);
		model.addAttribute("users", users);
		model.addAttribute("successAverage", successAverage);
		model.addAttribute("winner", winner);
		model.addAttribute("loser", loser);
		return "rankingModel";
	}
	
	// Playing a round of game.
	@PostMapping("/luckyseven/options/playround")
	@ResponseStatus(HttpStatus.OK)
	public String playRound(Model model) {
		Rounds round = rDAO.save(new Rounds(user));
		updateData();
		List<Rounds> rounds = new ArrayList<Rounds>();
		for (Rounds r: rDAO.findAll()) {
			if (r.getUserId() == user.getId()) {
				rounds.add(r);
			}
		}
		model.addAttribute("user", user);
		model.addAttribute("round", round);
		return "playModel";
	}	
	
	// Getting user's list of rounds.
	@GetMapping("/luckyseven/options/user/rounds")
	@ResponseStatus(HttpStatus.OK)
	public String getUserRounds(Model model) {
		updateData();
		getUserData(user);
		List<Rounds> rounds = getUserRounds(user);
		model.addAttribute("user", user);
		model.addAttribute("rounds", rounds);
		return "roundsModel";		
	}
	
	// Deleting user's rounds.
	@PostMapping("/luckyseven/options/user/rounds_deleted")
	@ResponseStatus(HttpStatus.OK)
	public String deleteUserRounds(Model model) {
		updateData();
		deleteUserRounds(user);
		getUserData(user);
		List<Rounds> rounds = getUserRounds(user);
		model.addAttribute("user", user);
		model.addAttribute("rounds", rounds);
		return "roundsModel";		
	}
	
///	Methods used above.
	
	// Entering with user already existing or creating new (with name or anonymous).
	public Users checkNameAndGetUser(String name) {
		String exists = "no";
		Users user = null;
		if (name.isBlank()) {
			user = uDao.save(new Users("Anonymous"));
		} else {
			for (Users u: uDao.findAll()) {
				if (u.getName().equals(name)) {
					exists = "yes";
					user = u;
					break;
				} else {
					exists = "new";
				}
			}
		}
		if (exists.equals("new")) {
			user = uDao.save(new Users(name));
		}
		return user;
	}
	
	// Updating all data from users and overall success average.
	public void updateData() {
		for (Users u: uDao.findAll()) {
			getUserData(u);
		}
		getAverageSuccess();
	}
	
	// Getting rounds information data from user.
	public void getUserData(Users u) {
		double average = 0;
		int won = 0;
		int total = 0;
		for (Rounds r: rDAO.findAll()) {
			if (r.getUser().getName().equals(u.getName())) {
				total++;
				if (r.getWon().equals("Hooray, yes!!")) {
					won++;
				}
			} 
		}
		u.setWonRounds(won);
		u.setRoundsPlayed(total);
		if (total != 0) {
			average = u.getWonRounds() * 100 /u.getRoundsPlayed();
		} else {
			average = 0.0;
		}
		u.setSuccessRate(average);
		uDao.save(u);
	}
	
	// Getting success average for all users.
	public double getAverageSuccess() {
		double average;
		double temp = 0.0;
		List<Users> users = uDao.findAll();
		for (Users u: users) {
			temp = temp + u.getSuccessRate();
		}
		average = temp/users.size();
		return Math.round(average);
	}
	
	//Getting best user.
	public String getBestUser() {
		Users best;
		HashMap<Double, String> rates = new HashMap<Double, String>();
		for (Users u: uDao.findAll()) {
			rates.put(u.getSuccessRate(), u.getName());
		}
		
		best = uDao.findByName(rates.get(Collections.max(rates.keySet())));
		return best.getName();
	}
	
	//Getting best user.
	public String getWorstUser() {
		Users worst;
		HashMap<Double, String> rates = new HashMap<Double, String>();
		for (Users u: uDao.findAll()) {
			rates.put(u.getSuccessRate(), u.getName());
		}
		worst = uDao.findByName(rates.get(Collections.min(rates.keySet())));
		return worst.getName();
	}
		
	// Getting all rounds played by user.
	public List<Rounds> getUserRounds(Users user) {
		List<Rounds> userRounds = new ArrayList<Rounds>();
		for (Rounds round: rDAO.findAll()) {
			if (round.getUser().getName().equals(user.getName())) {
				userRounds.add(round);
			}
		}
		return userRounds;
	}
	
	// Deleting all rounds of user.
	public void deleteUserRounds(Users user) {
		for (Rounds round: rDAO.findAll()) {
			if (round.getUser().getName().equals(user.getName())) {
				rDAO.delete(round);
			}
		}
		updateData();
	}
		
}
