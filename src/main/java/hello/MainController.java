package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;

import hello.User;
import hello.UserRepository;

//import hello.Person;
//import hello.PersonRepository;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class MainController {
	@Autowired // This means to get the bean called userRepository
	           // Which is auto-generated by Spring, we will use it to handle the data
	private UserRepository userRepository;
	private PersonRepository personRepository;

	@GetMapping(path="/add" ) // Map ONLY GET Requests
	public @ResponseBody String addNewUser (@RequestParam String name
			, @RequestParam String pw) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		//Syntax:/demo/add?name=testname&pw=somepassword
		User n = new User();
		n.setName(name);
		n.setPassword(pw);
		userRepository.save(n);
		return "Saved";
	}

	@PostMapping(path="/padd", consumes = "text/plain") // Map ONLY POST Requests
	public @ResponseBody String paddNewUser (@RequestBody String jsonStr) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		//Syntax:/demo/add?name=testname&pw=somepassword
		JSONObject jObject = new JSONObject(jsonStr);
		User n = new User();
		n.setName(jObject.name);
		n.setPassword(jObject[0].pw);
		userRepository.save(n);
		return "Saved";
	}

	@GetMapping(path="/all")
	public @ResponseBody Iterable<User> getAllUsers() {
		// This returns a JSON or XML with the users
		return userRepository.findAll();
	}
	//test, works. Syntax: /demo/search?name=searchname
	@GetMapping(path="/search")
	public @ResponseBody Iterable<User> getAUser(@RequestParam String name) {
		return userRepository.findByName(name);
	}
	/*//PERSONTEST follows
	@GetMapping(path="/addP") // Map ONLY GET Requests
	public @ResponseBody String addNewPerson (@RequestParam Integer addressID, @RequestParam String firstName, @RequestParam String lastName
			, @RequestParam String bday) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

		Person p = new Person();
		p.setAddressId(addressID);
		p.setFirstName(firstName);
		p.setLastName(lastName);
		p.setDateOfBirth(bday);
		personRepository.save(p);
		return "Saved";
	}

	@GetMapping(path="/allP")
	public @ResponseBody Iterable<Person> getAllPersons() {
		// This returns a JSON or XML with the users
		return personRepository.findAll();
	}
	//test
	@GetMapping(path="/searchP")
	public @ResponseBody Iterable<Person> getAPerson(@RequestParam String firstName) {
		return personRepository.findBy_First_name(firstName);
	}*/
}