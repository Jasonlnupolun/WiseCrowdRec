package com.feiyu.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.feiyu.model.Person;
import com.feiyu.service.PersonService;

@Controller
public class TweetsAnalyzerController {
	private static final Logger logger = LoggerFactory.getLogger(TweetsAnalyzerController.class);
	private PersonService personService;

	@Autowired
	public TweetsAnalyzerController(PersonService personService) {
		this.personService = personService;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String tweetsAnalyzer(HttpServletRequest req, HttpServletResponse resp,Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate );
		
		return "index";
	}
	
	// Start: From example https://github.com/stevehanson/spring-mvc-ajax

	@RequestMapping("/restapi/person/random")
	@ResponseBody
	public Person randomPerson() {
		return personService.getRandom();
	}

	@RequestMapping("/restapi/person/{id}")
	@ResponseBody
	public Person getById(@PathVariable Long id) {
		return personService.getById(id);
	}

	/* same as above method, but is mapped to
	 * /restapi/person?id= rather than /restapi/person/{id}
	 */
	@RequestMapping(value="/restapi/person", params="id")
	@ResponseBody
	public Person getByIdFromParam(@RequestParam("id") Long id) {
		return personService.getById(id);
	}

	/**
	 * Saves new person. Spring automatically binds the name
	 * and age parameters in the request to the person argument
	 * @param person
	 * @return String indicating success or failure of save
	 */
	@RequestMapping(value="/restapi/person", method=RequestMethod.POST)
	@ResponseBody
	public String savePerson(Person person) {
		personService.save(person);
		return "Saved person: " + person.toString();
	}
	// End: From example https://github.com/stevehanson/spring-mvc-ajax
	
	
//	@RequestMapping(value = "restapi/searchPhrases", method = RequestMethod.GET)
//	@ResponseBody
//	public List<Result> searchPhrases(@RequestParam("searchPhrases") String searchPhrases) {
//        List<Result> results = new ArrayList<>();
//        if (searchPhrases == null || searchPhrases.length() == 0) {
//            return results;
//        }
//
//        Topology t = new Topology();
//        t.startTopology(searchPhrases);
//        
//        
//        
//        Result result = new Result(keyword, sentiments);
//        results.add(result);
//        return results;
//    }
}
