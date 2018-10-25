package api;

import org.apache.tomcat.jni.Local;
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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.time.*;

@Controller // This means that this class is a Controller
public class MainController {
	@Autowired // This means to get the bean called userRepository
				// Which is auto-generated by Spring, we will use it to handle the data
	private UserRepository userRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private AssociationRepository associationRepository;

	/*
	 * This method is used to sign up a new user. It returns an error message if the user exist, 
	 * else it returns the created users Id, name and sessionid. 
	 */
	@PostMapping(path = "/adduser", produces = "application/json")
	public @ResponseBody Object addNewUser(@RequestBody Map<String, Object> body) {
		if (userRepository.findByName(body.get("name").toString()) != null) {
			Messages m = new Messages();
			m.setError("Username already exists");
			return m;
		} else {
			String hashedpassword = BcryptSetup.hashPassword(body.get("password").toString());
			String hashedSessionId = BcryptSetup.generateSessionId();
			User user = new User();
			user.setName(body.get("name").toString());
			user.setPassword(hashedpassword);
			user.setSessionId(hashedSessionId);
			userRepository.save(user);
			return userRepository.findSessionByName(user.getName());
		}
	}

	/*
	 * This method is used to get the role of a user. It returns an error message if parameter
	 * is undefined, else it returns the role for the user.
	 */
	@GetMapping(path = "/getuserrole")
	public @ResponseBody Object getUserRole(@RequestParam("userid") int userid) {
		if(userid == 0) {
			Messages m = new Messages();
			m.setError("Parameter userid not defined");
			return m;
		} else {
			return userRepository.findRoleByUserid(userid);
		}
	}

	/*
	 * This method is used at login, to determene if the user and what type of role
	 * it has.
	 */
	@PostMapping(path = "/finduser")
	public @ResponseBody String findUser(@RequestBody User myUser) {
		boolean check = false;
		User user = userRepository.verifyUser(myUser.getName(), myUser.getPassword());
		if (user != null) {
			check = user.getStatus().equals("active");
		}

		if (check) {
			if (user.getRole() == 0) {
				return "User";
			} else {
				return "Admin";
			}
		} else {
			return "Failure";
		}
	}

	/**
	 * This method creates a new address if it does not exist and checks based on
	 * the first address line.
	 * 
	 * @param newAddress
	 * @return
	 */
	@PostMapping(path = "/addAddress")
	public @ResponseBody Object addAddress(@RequestBody Map<String, Object> body) {
		boolean check = false;
		Messages msg = new Messages();
		Address address = addressRepository.getByAddress(body.get("address_line_1").toString());
		if (address == null) {
			check = true;
		}
		if (check) {
			Address a = new Address();
			a.setAddressLine1(body.get("address_line_1").toString());
			a.setAddressLine2(body.get("address_line_2").toString());
			a.setAddressLine3(body.get("address_line_3").toString());
			a.setPostalCode(body.get("postal_code").toString());
			a.setCity(body.get("city").toString());
			a.setCountry(body.get("country").toString());
			addressRepository.save(a);
			// Return the id the new address got in the database.
			address = addressRepository.getByAddress(a.getAddressLine1());
			System.out.println(address.getId().toString());
			msg.setMessage(address.getId().toString());
			return msg;
		} else {
			msg.setError("Failure, Address was not created.");
			return msg;
		}
	}

	/**
	 * This method creates a new location if it does not exist and checks based on
	 * the name.
	 * 
	 * @param newLocation
	 * @return
	 */
	@PostMapping(path = "/addLocation")
	public @ResponseBody Object addLocation(@RequestBody Map<String, Object> body) {
		Messages m = new Messages();
		boolean check = false;
		Messages msg = new Messages();
		Location location = locationRepository.getByName(body.get("name").toString());
		if (location == null) {
			check = true;
		}
		if (check) {
			Location l = new Location();
			l.setAddressId(addressRepository.getById(Integer.parseInt(body.get("address_id").toString())));
			l.setName(body.get("name").toString());
			l.setDescription(body.get("description").toString());
			locationRepository.save(l);
			msg.setMessage("Success, Location was created.");
			return msg;
		} else {
			msg.setError("Failure, Location was not created.");
			return msg;
		}
	}

	@PostMapping(path = "/addPerson")
	public @ResponseBody Object addPerson(@RequestBody Map<String, Object> body) {
		boolean check = false;
		Messages msg = new Messages();
		String dateArr[] = body.get("date_of_birth").toString().split("-");
		LocalDate date = LocalDate.of(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]),
				Integer.parseInt(dateArr[2]));
		System.out.println(date);
		Person person = personRepository.findByFirstAndLastandBirth(body.get("first_name").toString(),
				body.get("last_name").toString(), date);
		if (person == null) {
			check = true;
		}
		if (check) {
			Person p = new Person();
			System.out.println(body.get("address_id").toString());
			p.setAddressId(addressRepository.getById(Integer.parseInt(body.get("address_id").toString())));
			p.setFirstName(body.get("first_name").toString());
			p.setLastName(body.get("last_name").toString());
			p.setDateOfBirth(date);
			personRepository.save(p);
			person = personRepository.findByFirstAndLastandBirth(p.getFirstName(), p.getLastName(), p.getDateOfBirth());
			System.out.println(person.getId().toString());
			msg.setMessage(person.getId().toString());
			return msg;
		} else {
			msg.setError("Failure, Person was not created.");
			return msg;
		}
	}

	/**
	 * 
	 * @param body
	 * @return
	 */
	@PostMapping(path = "/addContact")
	public @ResponseBody Object addContact(@RequestBody Map<String, Object> body) {
		boolean check = false;
		Messages msg = new Messages();
		System.out.println(body.get("person_id").toString());
		System.out.println(body.get("contact_type").toString());
		System.out.println(body.get("contact_detail").toString());

		Contact contact = contactRepository.findByIDandDetails(Integer.parseInt(body.get("person_id").toString()),
				body.get("contact_detail").toString());
		if (contact == null) {
			check = true;
		}
		if (check) {
			Contact c = new Contact();
			System.out.println(body.get("person_id"));
			c.setPersonId(personRepository.getById(Integer.parseInt(body.get("person_id").toString())));
			c.setContactType(body.get("contact_type").toString());
			c.setContactDetail(body.get("contact_detail").toString());
			contactRepository.save(c);
			msg.setMessage("Success, Contact was created.");
			return msg;
		} else {
			msg.setError("Failure, Contact was not created.");
			return msg;
		}
	}

	@GetMapping(path = "/getallusers")
	public @ResponseBody Iterable<User> getAllUsers() {
		// This returns a JSON or XML with the users
		return userRepository.findAll();
	}

	// test, works. Syntax: /demo/search?name=searchname
	/*
	 * @GetMapping(path="/search") public @ResponseBody Iterable<User>
	 * getAUser(@RequestParam String name) { return userRepository.findByName(name);
	 * }
	 */
	// testing non-list
	@GetMapping(path = "/search")
	public @ResponseBody User getAUser(@RequestParam String name) {
		return userRepository.findByName(name);
	}

	@PostMapping(path = "/searchuser")
	public @ResponseBody String searchUser(@RequestBody User myUser) {
		boolean check = false;
		User user = userRepository.findByName(myUser.getName());
		if (user != null) {
			check = user.getStatus().equals("active");
		}

		if (check) {
			return "Success";
		} else {
			return "Failure";
		}
	}

	@GetMapping(path = "/updateusername")
	public @ResponseBody String updateAUserName(@RequestParam String oldName, @RequestParam String newName) {
		User u = (userRepository.findByName(oldName));
		u.setName(newName);
		userRepository.save(u);
		return "Updated";
	}

	@GetMapping(path = "/updatepw")
	public @ResponseBody String updateAUserPW(@RequestParam String name, @RequestParam String oldPw,
			@RequestParam String newPw) {
		User u = (userRepository.verifyUser(name, oldPw));
		u.setPassword(newPw);
		userRepository.save(u);
		return "Updated";
	}

	@GetMapping(path = "/deleteuser")
	public @ResponseBody String deleteAUser(@RequestParam String name) {
		User u = (userRepository.findByName(name));
		u.setStatus("inactive");
		userRepository.save(u);
		return "Updated";
	}

	/*
	 * // PERSONTEST follows
	 * 
	 * @GetMapping(path = "/addP") // Map ONLY GET Requests public @ResponseBody
	 * String addNewPerson(@RequestParam Integer addressID, @RequestParam String
	 * firstName,
	 * 
	 * @RequestParam String lastName, @RequestParam String bday) { // @ResponseBody
	 * means the returned String is the response, not a view name // @RequestParam
	 * means it is a parameter from the GET or POST request Person test =
	 * personRepository.findByFirstAndLastandBirth(firstName, lastName, bday); if
	 * (test == null) { return "Error: Name taken"; } else { Person p = new
	 * Person(); p.setAddressId(addressRepository.getById(addressID));
	 * p.setFirstName(firstName); p.setLastName(lastName); p.setDateOfBirth(bday);
	 * personRepository.save(p); return "Saved"; } }
	 */
	// test
	@GetMapping(path = "/searchPfirst")
	public @ResponseBody Iterable<Person> getAPersonByFirstName(@RequestParam String name) {
		return personRepository.findByFirstName(name);
	}

	@GetMapping(path = "/searchPlast")
	public @ResponseBody Iterable<Person> getAPersonByLastName(@RequestParam String name) {
		return personRepository.findByLastName(name);
	}

	@GetMapping(path = "/searchP")
	public @ResponseBody Person getAPersonByFirstAndLast(@RequestParam String firstName, @RequestParam String lastName,
			@RequestParam LocalDate bday) {
		return personRepository.findByFirstAndLastandBirth(firstName, lastName, bday);
	}

	// ADDRESS TESTING FOLLOWS
	@GetMapping(path = "/getalladdresses")
	public @ResponseBody Iterable<Address> getAllAddresses() {
		return addressRepository.findAll();
	}

	/**
	 * Get to show all locations in the database
	 */
	@GetMapping(path = "/getalllocations")
	public @ResponseBody Iterable<Location> getAllLocations() {
		return locationRepository.findAll();
	}

	/**
	 * Get to show all Persons in the database
	 */
	@GetMapping(path = "/getallpersons")
	public @ResponseBody Iterable<Person> getAllPersons() {
		return personRepository.findAll();
	}

	/**
	 * Get to show all contacts in the database
	 */
	@GetMapping(path = "/getallcontacts")
	public @ResponseBody Iterable<Contact> getAllContacts() {
		return contactRepository.findAll();
	}

	@GetMapping(path = "/getaddressbyid")
	public @ResponseBody Address getAddressById(@RequestParam Integer id) {
		return addressRepository.getById(id);
	}

	@GetMapping(path = "/getallassociations")
	public @ResponseBody Iterable<Association> getAllAssociations() {
		return associationRepository.findAll();
	}

	// TEST - return other value?
	@PostMapping(path = "/addassociation")
	public @ResponseBody Messages addAssociation(@RequestBody Map<String, Object> body) {
		Messages m = new Messages();
		boolean check = false;
		String name = body.get("name").toString();
		String description = body.get("description").toString();
		Association existenceCheck = associationRepository.getByName(name);
		if (existenceCheck == null) {
			check = true;
		}
		if (check) {
			Association a = new Association();
			a.setName(name);
			a.setDescription(description);
			associationRepository.save(a);
			m.setMessage("Success");
		} else {
			m.setError("Error: Association exists");
		}
		return m;
	}

}