package api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;

//Old imports
/*import api.User;
import api.UserRepository;
import api.Person;
import api.PersonRepository;*/
//New imports
import api.Repositories.*;
import api.Models.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;



@Controller    // This means that this class is a Controller
public class MainController {
	@Autowired // This means to get the bean called userRepository
	           // Which is auto-generated by Spring, we will use it to handle the data
	private UserRepository userRepository;

	@Autowired
	private PersonRepository personRepository;

	@PostMapping(path="/adduser")
	public @ResponseBody String addNewUser (@RequestBody User newUser) {
		if(userRepository.findByName(newUser.getName()) != null){
			return "Failure: Name taken";
		}else{
			User n = new User();
			n.setName(newUser.getName());
			n.setPassword(newUser.getPassword());
			userRepository.save(n);
			return "Success";
		}
	}

	@PostMapping(path="/finduser")
	public @ResponseBody String findUser (@RequestBody User myUser) {
		boolean check = userRepository.verifyUser(myUser.getName(),myUser.getPassword()).getStatus().equals("active");

		if(check){
			return "Success"
		}else{
			return "Failure, user does not exist.";
		}
	}

	@GetMapping(path="/getallusers")
	public @ResponseBody Iterable<User> getAllUsers() {
		// This returns a JSON or XML with the users
		return userRepository.findAll();
	}
	//test, works. Syntax: /demo/search?name=searchname
	/*@GetMapping(path="/search")
	public @ResponseBody Iterable<User> getAUser(@RequestParam String name) {
		return userRepository.findByName(name);
	}*/
	//testing non-list
	@GetMapping(path="/search")
	public @ResponseBody User getAUser(@RequestParam String name) {
		return userRepository.findByName(name);
	}

	@GetMapping(path="/updateusername")
	public @ResponseBody String updateAUserName(@RequestParam String oldName,@RequestParam String newName) {
		User u = (userRepository.findByName(oldName));
		u.setName(newName);
		userRepository.save(u);
		return "Updated";
	}

	@GetMapping(path="/updatepw")
	public @ResponseBody String updateAUserPW(@RequestParam String name,@RequestParam String oldPw,@RequestParam String newPw) {
		User u = (userRepository.verifyUser(name,oldPw));
		u.setPassword(newPw);
		userRepository.save(u);
		return "Updated";
	}

	@GetMapping(path="/deleteuser")
	public @ResponseBody String deleteAUser(@RequestParam String name) {
		User u = (userRepository.findByName(name));
		u.setStatus("inactive");
		userRepository.save(u);
		return "Updated";
	}

	//PERSONTEST follows
	@GetMapping(path="/addP") // Map ONLY GET Requests
	public @ResponseBody String addNewPerson (@RequestParam Integer addressID, @RequestParam String firstName, @RequestParam String lastName
			, @RequestParam String bday) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		List<Person> test = new ArrayList<>();
		test = personRepository.findByFirstAndLast(firstName,lastName);
		System.out.println(test.isEmpty());
		if(!test.isEmpty()){
			return "Error: Name taken";
		}else{
			Person p = new Person();
			p.setAddressId(addressID);
			p.setFirstName(firstName);
			p.setLastName(lastName);
			p.setDateOfBirth(bday);
			personRepository.save(p);
			return "Saved";
		}
	}

	@GetMapping(path="/allP")
	public @ResponseBody Iterable<Person> getAllPersons() {
		// This returns a JSON or XML with the users
		return personRepository.findAll();
	}
	//test
	@GetMapping(path="/searchPfirst")
	public @ResponseBody Iterable<Person> getAPersonByFirstName(@RequestParam String name) {
		return personRepository.findByFirstName(name);
	}
	@GetMapping(path="/searchPlast")
	public @ResponseBody Iterable<Person> getAPersonByLastName(@RequestParam String name) {
		return personRepository.findByLastName(name);
	}

	@GetMapping(path="/searchP")
	public @ResponseBody Iterable<Person> getAPersonByFirstAndLast(@RequestParam String firstName,@RequestParam String lastName) {
		return personRepository.findByFirstAndLast(firstName,lastName);
	}
}