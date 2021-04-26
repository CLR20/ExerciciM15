package com.luckyseven.application.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.persistence.OrderBy;
import javax.swing.text.html.Option;

import org.hibernate.sql.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.luckyseven.application.dao.HigherDiceRoundsDAO;
import com.luckyseven.application.dao.LuckySevenRoundsDAO;
import com.luckyseven.application.dao.SameDiceRoundsDAO;
import com.luckyseven.application.dao.UsersDAO;
import com.luckyseven.application.entities.HigherDiceRounds;
import com.luckyseven.application.entities.LuckySevenRounds;
import com.luckyseven.application.entities.SameDiceRounds;
import com.luckyseven.application.entities.Users;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClientFactory;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Controller
@Import(value=MongoClientFactory.class)
@RequestMapping("")
public class GameController {

	@Autowired
	MongoClient mop = MongoClients.create("mongodb://localhost:27017");
	@OrderBy("name ASC")
	@Autowired
	UsersDAO uDao;
	@Autowired
	LuckySevenRoundsDAO lsRDAO;
	@Autowired
	SameDiceRoundsDAO sdRDAO;
	@Autowired
	HigherDiceRoundsDAO hdRDAO;
	
	private String userName;
	private Users user;
	private String game;
	private UUID userId;
	private double successAverage;
	private String winner;
	private String loser;
	
	MongoDatabase db = mop.getDatabase ("luckyseven");
	
	//Starting app, asking for user name.
	@GetMapping("/luckyseven")
	@ResponseStatus(HttpStatus.OK)
	public String Start(Model model, @AuthenticationPrincipal OidcUser principal) {
		
// UNCOMMENT BEFORE FIRST LAUNCH!!		
		//db.createCollection("users");
		//db.createCollection("lsrounds");
		//Users ann = uDao.save(new Users("Ann"));
		//Users boris = uDao.save(new Users("Boris"));
//		
		List<Users> users = uDao.findAll();
		model.addAttribute("users", users);
		return "start";
	}
	
	// Giving options to user.
	@GetMapping("/luckyseven/options")
	@ResponseStatus(HttpStatus.OK)
	public String Options(Model model, String name, String option) {		
		userName = name;
		userId = checkNameAndGetUser(userName, option);
		user = getUserByUUID(userId);
		updateData("luckyseven");
		model.addAttribute("user", user);
		return "options";
	}
	
	// Changing user's name
	@GetMapping("/luckyseven/options/user/name")
	@ResponseStatus(HttpStatus.OK)
	public String changeUserName(Model model) {
		model.addAttribute("user", user);
		return "nameModel";
	}
	
	// Setting new user's name
	@GetMapping("/luckyseven/options/user/name_changed")
	@ResponseStatus(HttpStatus.OK)
	public String ChangeUserName(Model model, String newName, String newOption) {
		userName = newName;
		user.setName(newName);
		if (newOption.equals("YES")) {
			user.setAlias("Anonymous");
		} else {
			user.setAlias(userName);
		}
		uDao.save(user);
		updateData("luckyseven");
		getUserLsData(user);
		model.addAttribute("user", user);
		return "options";
	}
	
	// Giving options to user.
	@GetMapping("/luckyseven/user/options")
	@ResponseStatus(HttpStatus.OK)
	public String Options(Model model) {					
		updateData("luckyseven");
		model.addAttribute("user", user);
		return "userOptions";
	}
		
	// Deleting user.
	@GetMapping("/luckyseven/options/user/deleted")
	@ResponseStatus(HttpStatus.OK)
	public String DeleteUser(Model model) {					
		uDao.delete(user);
		List<Users> users = uDao.findAll();
		model.addAttribute("users", users);
		return "start";
	}

// LUCKY SEVEN GAME
	//Getting luckyseven users ranking.
	@GetMapping("/luckyseven/options/luckyseven/ranking")
	@ResponseStatus(HttpStatus.OK)
	public String GetLsUsers(Model model) {
		updateData("luckyseven");
		List<Users> users = uDao.findAll();
		successAverage = getAverageSuccess("luckyseven");
		winner = getBestUser("luckyseven");
		loser = getWorstUser("luckyseven");
		model.addAttribute("user", user);
		model.addAttribute("users", users);
		model.addAttribute("successAverage", successAverage);
		model.addAttribute("winner", winner);
		model.addAttribute("loser", loser);
		return "lsRankingModel";
	}
	
	// Playing a round of luckyseven.
	@GetMapping("/luckyseven/options/luckyseven/playround")
	@ResponseStatus(HttpStatus.OK)
	public String PlayLsRound(Model model) {
		LuckySevenRounds round = lsRDAO.save(new LuckySevenRounds(user));
		updateData("luckyseven");
		List<LuckySevenRounds> rounds = new ArrayList<LuckySevenRounds>();
		for (LuckySevenRounds r: lsRDAO.findAll()) {
			if (r.getUserId() == user.getId()) {
				rounds.add(r);
			}
		}
		model.addAttribute("user", user);
		model.addAttribute("round", round);
		return "lsPlayModel";
	}	
	
	// Getting luckyseven user's list of rounds.
	@GetMapping("/luckyseven/options/user/luckyseven/rounds")
	@ResponseStatus(HttpStatus.OK)
	public String GetUserLsRounds(Model model) {
		updateData("luckyseven");
		getUserLsData(user);
		List<LuckySevenRounds> rounds = getUserLsRounds(user);
		model.addAttribute("user", user);
		model.addAttribute("rounds", rounds);
		return "lsRoundsModel";		
	}
	
	// Deleting luckyseven user's rounds.
	@GetMapping("/luckyseven/options/user/luckyseven/rounds_deleted")
	@ResponseStatus(HttpStatus.OK)
	public String DeleteUserLsRounds(Model model) {
		updateData("luckyseven");
		deleteUserLsRounds(user);
		getUserLsData(user);
		List<LuckySevenRounds> rounds = getUserLsRounds(user);
		model.addAttribute("user", user);
		model.addAttribute("rounds", rounds);
		return "lsRoundsModel";		
	}

// SAME DICE GAME
	//Getting samedice users ranking.
	@GetMapping("/luckyseven/options/samedice/ranking")
	@ResponseStatus(HttpStatus.OK)
	public String GetSdUsers(Model model) {
		updateData("samedice");
		List<Users> users = uDao.findAll();
		successAverage = getAverageSuccess("samedice");
		winner = getBestUser("samedice");
		loser = getWorstUser("samedice");
		model.addAttribute("user", user);
		model.addAttribute("users", users);
		model.addAttribute("successAverage", successAverage);
		model.addAttribute("winner", winner);
		model.addAttribute("loser", loser);
		return "sdRankingModel";
	}
	
	// Playing a round of samedice.
	@GetMapping("/luckyseven/options/samedice/playround")
	@ResponseStatus(HttpStatus.OK)
	public String PlaySdRound(Model model) {
		SameDiceRounds round = sdRDAO.save(new SameDiceRounds(user));
		updateData("samedice");
		List<SameDiceRounds> rounds = new ArrayList<SameDiceRounds>();
		for (SameDiceRounds r: sdRDAO.findAll()) {
			if (r.getUserId() == user.getId()) {
				rounds.add(r);
			}
		}
		model.addAttribute("user", user);
		model.addAttribute("round", round);
		return "sdPlayModel";
	}	
	
	// Getting samedice user's list of rounds.
	@GetMapping("/luckyseven/options/user/samedice/rounds")
	@ResponseStatus(HttpStatus.OK)
	public String GetUserSdRounds(Model model) {
		updateData("samedice");
		getUserSdData(user);
		List<SameDiceRounds> rounds = getUserSdRounds(user);
		model.addAttribute("user", user);
		model.addAttribute("rounds", rounds);
		return "sdRoundsModel";		
	}
	
	// Deleting samedice user's rounds.
	@GetMapping("/luckyseven/options/user/samedice/rounds_deleted")
	@ResponseStatus(HttpStatus.OK)
	public String DeleteUserSdRounds(Model model) {
		updateData("samedice");
		deleteUserSdRounds(user);
		getUserSdData(user);
		List<SameDiceRounds> rounds = getUserSdRounds(user);
		model.addAttribute("user", user);
		model.addAttribute("rounds", rounds);
		return "sdRoundsModel";		
	}

// HIGHER DICE GAME
	//Getting higherdice users ranking.
	@GetMapping("/luckyseven/options/higherdice/ranking")
	@ResponseStatus(HttpStatus.OK)
	public String GetHdUsers(Model model) {
		updateData("higherdice");
		List<Users> users = uDao.findAll();
		successAverage = getAverageSuccess("higherdice");
		winner = getBestUser("higherdice");
		loser = getWorstUser("higherdice");
		model.addAttribute("user", user);
		model.addAttribute("users", users);
		model.addAttribute("successAverage", successAverage);
		model.addAttribute("winner", winner);
		model.addAttribute("loser", loser);
		return "hdRankingModel";
	}
	
	// Playing a round of higherdice.
	@GetMapping("/luckyseven/options/higherdice/playround")
	@ResponseStatus(HttpStatus.OK)
	public String PlayHdRound(Model model) {
		HigherDiceRounds round = hdRDAO.save(new HigherDiceRounds(user));
		updateData("higherdice");
		List<HigherDiceRounds> rounds = new ArrayList<HigherDiceRounds>();
		for (HigherDiceRounds r: hdRDAO.findAll()) {
			if (r.getUserId() == user.getId()) {
				rounds.add(r);
			}
		}
		model.addAttribute("user", user);
		model.addAttribute("round", round);
		return "hdPlayModel";
	}	
	
	// Getting higherdice user's list of rounds.
	@GetMapping("/luckyseven/options/user/higherdice/rounds")
	@ResponseStatus(HttpStatus.OK)
	public String GetUserHdRounds(Model model) {
		updateData("higherdice");
		getUserSdData(user);
		List<HigherDiceRounds> rounds = getUserHdRounds(user);
		model.addAttribute("user", user);
		model.addAttribute("rounds", rounds);
		return "hdRoundsModel";		
	}
	
	// Deleting higherdice user's rounds.
	@GetMapping("/luckyseven/options/user/higherdice/rounds_deleted")
	@ResponseStatus(HttpStatus.OK)
	public String DeleteUserHdRounds(Model model) {
		updateData("higherdice");
		deleteUserSdRounds(user);
		getUserSdData(user);
		List<HigherDiceRounds> rounds = getUserHdRounds(user);
		model.addAttribute("user", user);
		model.addAttribute("rounds", rounds);
		return "hdRoundsModel";		
	}

	
///	Methods used above.
	
	// Entering with user already existing or creating new (with name or anonymous).
	public UUID checkNameAndGetUser(String name, String option) {
		String exists = "no";
		Users user = null;
		for (Users u: uDao.findAll()) {
			if (u.getName().equals(name)) {
				exists = "yes";
				user = u;
				userId = user.getId();
				break;
			}
		}
		if (exists.equals("no")) {
			user = uDao.save(new Users(name, option));
			userId = user.getId();
		}
		return userId;
	}
	
	// Getting user by UUID.
	public Users getUserByUUID(UUID id) {
		Users user = null;
		for (Users u: uDao.findAll()) {
			if (u.getId().equals(id)) {
				user = u;
			}
		}
		return user;
	}
	
	// Updating all data from users and overall success average.
	public void updateData(String game) {
		if (game.equals("luckyseven")) {
			for (Users u: uDao.findAll()) {
				getUserLsData(u);
			}
			getAverageSuccess(game);
		}
		if (game.equals("samedice")) {
			for (Users u: uDao.findAll()) {
				getUserSdData(u);
			}
			getAverageSuccess(game);
		}
		if (game.equals("higherdice")) {
			for (Users u: uDao.findAll()) {
				getUserHdData(u);
			}
			getAverageSuccess(game);
		}
	}
	
	// Getting luckyseven rounds information data from user.
	public void getUserLsData(Users u) {
		double average = 0;
		int won = 0;
		int total = 0;
		for (LuckySevenRounds r: lsRDAO.findAll()) {
			if (r.getUser().getId().equals(u.getId())) {
				total++;
				if (r.getWon().equals("Hooray, yes!!")) {
					won++;
				}
			} 
		}
		u.setLsWonRounds(won);
		u.setLsRoundsPlayed(total);
		if (total != 0) {
			average = u.getLsWonRounds() * 100 /u.getLsRoundsPlayed();
		} else {
			average = 0.0;
		}
		u.setLsSuccessRate(average);
		uDao.save(u);
	}
	
	// Getting samedice rounds information data from user.
	public void getUserSdData(Users u) {
		double average = 0;
		int won = 0;
		int total = 0;
		for (SameDiceRounds r: sdRDAO.findAll()) {
			if (r.getUser().getId().equals(u.getId())) {
				total++;
				if (r.getWon().equals("Hooray, yes!!")) {
					won++;
				}
			} 
		}
		u.setSdWonRounds(won);
		u.setSdRoundsPlayed(total);
		if (total != 0) {
			average = u.getSdWonRounds() * 100 /u.getSdRoundsPlayed();
		} else {
			average = 0.0;
		}
		u.setSdSuccessRate(average);
		uDao.save(u);
	}
		
	// Getting higherdice rounds information data from user.
	public void getUserHdData(Users u) {
		double average = 0;
		int won = 0;
		int total = 0;
		for (HigherDiceRounds r: hdRDAO.findAll()) {
			if (r.getUser().getId().equals(u.getId())) {
				total++;
				if (r.getWon().equals("Hooray, yes!!")) {
					won++;
				}
			} 
		}
		u.setHdWonRounds(won);
		u.setHdRoundsPlayed(total);
		if (total != 0) {
			average = u.getHdWonRounds() * 100 /u.getHdRoundsPlayed();
		} else {
			average = 0.0;
		}
		u.setHdSuccessRate(average);
		uDao.save(u);
	}

	// Getting success average for all users.
	public double getAverageSuccess(String game) {
		double average;
		double temp = 0.0;
		List<Users> users = uDao.findAll();
		if (game.equals("luckyseven")) {
			for (Users u: users) {
				temp = temp + u.getLsSuccessRate();
			}
		} else if (game.equals("samedice")) {
			for (Users u: users) {
				temp = temp + u.getSdSuccessRate();
			}
		} else if (game.equals("higherdice")) {
			for (Users u: users) {
				temp = temp + u.getHdSuccessRate();
			}
		}
		average = temp/users.size();
		return Math.round(average);
	}
	
	//Getting best user.
	public String getBestUser(String game) {
		Users best;
		HashMap<Double, UUID> rates = new HashMap<Double, UUID>();
		if (game.equals("luckyseven")) {
			for (Users u: uDao.findAll()) {
				rates.put(u.getLsSuccessRate(), u.getId());
			}
		} else if (game.equals("samedice")) {
			for (Users u: uDao.findAll()) {
				rates.put(u.getSdSuccessRate(), u.getId());
			}
		} else if (game.equals("higherdice")) {
			for (Users u: uDao.findAll()) {
				rates.put(u.getHdSuccessRate(), u.getId());
			}
		}		
		best = uDao.findById(rates.get(Collections.max(rates.keySet()))).get();
		return best.getAlias() + " (id: " + best.getId() + ")";
	}
	
	//Getting best user.
	public String getWorstUser(String game) {
		Users worst;
		HashMap<Double, UUID> rates = new HashMap<Double, UUID>();
		if (game.equals("luckyseven")) {
			for (Users u: uDao.findAll()) {
				rates.put(u.getLsSuccessRate(), u.getId());
			}
		} else if (game.equals("samedice")) {
			for (Users u: uDao.findAll()) {
				rates.put(u.getSdSuccessRate(), u.getId());
			}
		} else if (game.equals("higherdice")) {
			for (Users u: uDao.findAll()) {
				rates.put(u.getHdSuccessRate(), u.getId());
			}
		}
		worst = uDao.findById(rates.get(Collections.min(rates.keySet()))).get();
		return worst.getAlias() + " (id: " + worst.getId() + ")";
	}
		
	// Getting all luckyseven rounds played by user.
	public List<LuckySevenRounds> getUserLsRounds(Users user) {
		List<LuckySevenRounds> userRounds = new ArrayList<LuckySevenRounds>();
		for (LuckySevenRounds round: lsRDAO.findAll()) {
			if (round.getUser().getId().equals(user.getId())) {
				userRounds.add(round);
			}
		}
		return userRounds;
	}
	
	// Getting all samedice rounds played by user.
	public List<SameDiceRounds> getUserSdRounds(Users user) {
		List<SameDiceRounds> userRounds = new ArrayList<SameDiceRounds>();
		for (SameDiceRounds round: sdRDAO.findAll()) {
			if (round.getUser().getId().equals(user.getId())) {
				userRounds.add(round);
			}
		}
		return userRounds;
	}
	
	// Getting all higherdice rounds played by user.
	public List<HigherDiceRounds> getUserHdRounds(Users user) {
		List<HigherDiceRounds> userRounds = new ArrayList<HigherDiceRounds>();
		for (HigherDiceRounds round: hdRDAO.findAll()) {
			if (round.getUser().getId().equals(user.getId())) {
				userRounds.add(round);
			}
		}
		return userRounds;
	}
	
	// Deleting all luckyseven rounds of user.
	public void deleteUserLsRounds(Users user) {
		for (LuckySevenRounds round: lsRDAO.findAll()) {
			if (round.getUser().getId().equals(user.getId())) {
				lsRDAO.delete(round);
			}
		}
		updateData("luckyseven");
	}
	
	// Deleting all samedice rounds of user.
	public void deleteUserSdRounds(Users user) {
		for (SameDiceRounds round: sdRDAO.findAll()) {
			if (round.getUser().getId().equals(user.getId())) {
				sdRDAO.delete(round);
			}
		}
		updateData("samedice");
	}
	
	// Deleting all higherdice rounds of user.
	public void deleteUserHdRounds(Users user) {
		for (HigherDiceRounds round: hdRDAO.findAll()) {
			if (round.getUser().getId().equals(user.getId())) {
				hdRDAO.delete(round);
			}
		}
		updateData("higherdice");
	}
		
}
