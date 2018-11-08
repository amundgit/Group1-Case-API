package api.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;

//New imports
import api.Repositories.*;
import api.Models.*;
import api.Pojos.*;

//More imports
import api.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.time.*;

@CrossOrigin
@Controller // This means that this class is a Controller
@RequestMapping("/players")
public class PlayerController {
	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private UserRepository userRepository;

	@GetMapping(path = "/getall")
	public @ResponseBody Iterable<Player> getAllPlayers() {
		return playerRepository.findAll();
	}

	@GetMapping(path = "/getallactive")
	public @ResponseBody Iterable<Player> getAllActivePlayers() {
		return playerRepository.getAllActive();
	}

	@GetMapping(path = "/anongetall")
	public @ResponseBody Iterable<String> anonGetAllPlayers() {
		List<String> returnList = new ArrayList<>();
		Iterable<Player> playerList = playerRepository.getAllActive();
		for (Player p : playerList) {
			String tempString = p.getName() + ", " + p.getTeamId();
			returnList.add(tempString);
		}
		return returnList;
	}

	@GetMapping(path = "/getbyteam/")
	public @ResponseBody Iterable<Player> getAllPlayersInTeam(@RequestParam("team_id") String team_id) {
		return playerRepository.getByTeam(team_id);
	}

	@PostMapping(path = "/assign")
	public @ResponseBody Messages addPlayer(@RequestBody Map<String, Object> body) {
		System.out.println(body);
		System.out.println(body.get("person_id").toString());

		Messages m = new Messages();
		m = SecurityUtil.verifySession(body.get("sessionid").toString(), body.get("sessionuser").toString(),
				userRepository);
		if (m.getRole() != 1) {
			return m;
		} else {
			boolean check = false;
			String normal_position = body.get("normal_position").toString();
			Integer number = Integer.parseInt(body.get("number").toString());
			Integer person_id = Integer.parseInt(body.get("person_id").toString());
			String team_id = body.get("team_id").toString();
			Player existenceCheck = playerRepository.getByPersonId(person_id);
			// Actually do stuff
			if (existenceCheck == null) {
				check = true;
			}
			if (check) {
				Player p = new Player();
				p.setNormalPosition(normal_position);
				p.setNumber(number);
				p.setPersonId(personRepository.getById(person_id));
				p.setTeamId(teamRepository.getByTeamId(team_id));
				playerRepository.save(p);
				m.setMessage("Created");
			} else {
				existenceCheck.setNumber(number);
				existenceCheck.setNormalPosition(normal_position);
				existenceCheck.setTeamId(teamRepository.getByTeamId(team_id));
				playerRepository.save(existenceCheck);
				m.setMessage("Updated");
			}
			return m;
		}
	}

	@PostMapping(path = "/delete")
	public @ResponseBody Messages deletePlayer(@RequestBody Map<String, Object> body) {
		Messages m = new Messages();
		m = SecurityUtil.verifySession(body.get("sessionid").toString(), body.get("sessionuser").toString(),
				userRepository);
		if (m.getRole() != 1) {
			return m;
		} else {
			boolean check = true;
			Integer player_id = Integer.parseInt(body.get("player_id").toString());
			Player p = playerRepository.getById(player_id);
			// Actually do stuff
			if (p == null) {
				check = false;
				m.setMessage("Invalid player id");
			}
			if (check) {
				p.setStatus("inactive");
				playerRepository.save(p);
				m.setMessage("Deleted");
			}
			return m;
		}
	}
}
