package com.luckyseven.application.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.persistence.OrderBy;

import org.springframework.beans.factory.annotation.Autowired;
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
//import com.luckyseven.application.dao.impl.UsersDAOImpl;
import com.luckyseven.application.entities.Users;

@Controller
@RequestMapping("")
public class GameController {

	@OrderBy("ID ASC")
	@Autowired
	UsersDAO uDao;
	@OrderBy("id ASC")
	@Autowired
	RoundsDAO rDAO;
	String userName;
	Users user;
	long userId;
	double successAverage;
	String winner;
	String loser;
	
	//Starting app, asking for user name.
	@GetMapping("/luckyseven")
	@ResponseStatus(HttpStatus.OK)
	public String start(Model model) {		
		//uDao.save(new Users("Ann"));
		//uDao.save(new Users("Boris"));
		List<Users> users = uDao.findAll();
		model.addAttribute("users", users);
		return "start";
	}
	
	// Giving options to user.
	@GetMapping("/luckyseven/options")
	@ResponseStatus(HttpStatus.OK)
	public String options(Model model, String name) {		
		userName = name;
		updateData();
		user = uDao.findById(checkNameAndGetId(userName)).get();
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
	public long checkNameAndGetId(String name) {
		long userId = 0;
		String exists = "no";
		if (name.isBlank()) {
			Users user = uDao.save(new Users("Anonymous"));
			userId = user.getId();
		} else {
			for (Users u: uDao.findAll()) {
				if (u.getName().equals(name)) {
					exists = "yes";
					userId = u.getId();
					break;
				} else {
					exists = "new";
				}
			}
		}
		if (exists.equals("new")) {
			Users user = uDao.save(new Users(name));
			userId = user.getId();
		}
		return userId;
	}
	
	// Updating all data from users and overall success average.
	public void updateData() {
		for (Users user: uDao.findAll()) {
			getUserData(user);
		}
		getAverageSuccess();
	}
	
	// Getting rounds information data from user.
	public void getUserData(Users user) {
		double average = 0;
		int won = 0;
		int total = 0;
		for (Rounds r: rDAO.findAll()) {
			if (r.getUserId() == user.getId()) {
				total++;
				if (r.getWon().equals("Hooray, yes!!")) {
					won++;
				}
			} 
		}
		user.setWonRounds(won);
		user.setRoundsPlayed(total);
		if (total != 0) {
			average = user.getWonRounds() * 100 /user.getRoundsPlayed();
		} else {
			average = 0.0;
		}
		user.setSuccessRate(average);
		uDao.save(user);
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
		HashMap<Double, Long> rates = new HashMap<Double, Long>();
		for (Users u: uDao.findAll()) {
			rates.put(u.getSuccessRate(), u.getId());
		}
		best = uDao.findById(rates.get(Collections.max(rates.keySet()))).get();
		return best.getName() + " (id: " + best.getId() + ")";
	}
	
	//Getting best user.
	public String getWorstUser() {
		Users worst;
		HashMap<Double, Long> rates = new HashMap<Double, Long>();
		for (Users u: uDao.findAll()) {
			rates.put(u.getSuccessRate(), u.getId());
		}
		worst = uDao.findById(rates.get(Collections.min(rates.keySet()))).get();
		return worst.getName() + " (id: " + worst.getId() + ")";
	}
		
	// Getting all rounds played by user.
	public List<Rounds> getUserRounds(Users user) {
		List<Rounds> userRounds = new ArrayList<Rounds>();
		for (Rounds round: rDAO.findAll()) {
			if (round.getUserId() == user.getId()) {
				userRounds.add(round);
			}
		}
		return userRounds;
	}
	
	// Deleting all rounds of user.
	public void deleteUserRounds(Users user) {
		for (Rounds round: rDAO.findAll()) {
			if (round.getUserId() == user.getId()) {
				rDAO.delete(round);
			}
		}
		updateData();
	}
		
}
