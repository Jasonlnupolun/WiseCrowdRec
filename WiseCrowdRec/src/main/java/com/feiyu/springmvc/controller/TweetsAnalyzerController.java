package com.feiyu.springmvc.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.feiyu.signinwithtwitter.SignInWithTwitter;
import com.feiyu.springmvc.model.EntityInfo;
import com.feiyu.springmvc.model.Person;
import com.feiyu.springmvc.service.PersonService;
import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.StringSerializer;

@Controller
public class TweetsAnalyzerController {
	private static Logger logger = Logger.getLogger(TweetsAnalyzerController.class.getName());
	private PersonService personService;
	private EntityInfo entityInfo;
	private InitializeWCR initWcr = new InitializeWCR();
	private SignInWithTwitter signInWithTwitter = new SignInWithTwitter();
//	private List<EntityInfo> entitiesInfo = new ArrayList<>();
//	private EntityList _entityList = new EntityList(); 

	@Autowired
	public TweetsAnalyzerController(PersonService personService) {
		this.personService = personService;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(HttpServletRequest req, HttpServletResponse resp,Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is "+locale.toString()+"." );

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate );

		return "index";
	}
	
	@RequestMapping(value = "signinwithtwitter/login")
	@ResponseBody
	public String signinwithtwitter() throws IOException, KeyManagementException, InvalidKeyException, NoSuchAlgorithmException, HttpException {
		initWcr.getWiseCrowdRecConfigInfo();
		initWcr.signInWithTwitterGetAppOauth();
		
		return signInWithTwitter.obtainingARequestToken();
	}
	
	@RequestMapping(value = "twitter/callback", method = RequestMethod.GET)
	@ResponseBody
	public void converRequestToken2AccessToken(@RequestParam("oauth_token") final String oauth_token, @RequestParam("oauth_verifier") final String oauth_verifier) throws InvalidKeyException, KeyManagementException, NoSuchAlgorithmException, IOException, HttpException  {
		signInWithTwitter.converRequestToken2AccessToken(oauth_token, oauth_verifier);
	}

	@RequestMapping(value = "/startbackgroundtopology")
	@ResponseBody
	public void startBackgroundTopology() throws Exception { 
		//        WebServer webServer = WebServers.createWebServer(9876)
		//                .add("/hellowebsocket", new WS())
		//                .add(new StaticFileHandler("/web"));
		//        webServer.start();
		//        System.out.println("Server running at " + webServer.getUri());

		logger.info("Welcome -> startbackgroundtopology");
		//		initWcr.getWiseCrowdRecConfigInfo();
		//		initWcr.twitterInitBack();
		//		initWcr.cassandraInitial();
		//		initWcr.coreNLPInitial();
		//		initWcr.themoviedbOrgInitial();

		//		initWcr.twitterInitDyna();
		//		initWcr.elasticsearchInitial();
		//		sts.sparkInit();

		//		BackgroundTopology t = new BackgroundTopology();
		//
		//		boolean isFakeTopologyForTest = false;
		//		t.startTopology(isFakeTopologyForTest, "wcr_topology_back", "I rated #IMDb");
	}

	@RequestMapping(value = "/startdynamicsearch")
	@ResponseBody
	public void startDynamicSearch() throws Exception { 
		logger.info("Welcome -> start dynamic search");

//		initWcr.getWiseCrowdRecConfigInfo();//@
//		initWcr.coreNLPInitial();//@
//		initWcr.twitterInitDyna();
//		initWcr.elasticsearchInitial();
//		initWcr.rabbitmqInit();

		GlobalVariables.SPARK_TWT_STREAMING.startSpark("movie");
	}

	// Start: From example https://github.com/stevehanson/spring-mvc-ajax

	@RequestMapping("/restapi/person/random")
	@ResponseBody
	public Person randomPerson() {
		Rows<String, String> rows = GlobalVariables.AST_CASSANDRA_MNPLT.queryAllRowsOneCF(true);
		for (Row<String, String> row : rows) {
			Collection<String> columns = row.getColumns().getColumnNames();
			String e = null, c = null, sCSS = null, ti = null, te = null;
			int s = 0;
			for (String column : columns) {
				if (column.equals("count") || column.equals("entityInfo")) {
					continue;
				} else if (column.equals("entity")) {
					e = row.getColumns().getValue(column, StringSerializer.get(), null); 
					//default value = null
				} else if (column.equals("category")) {
					c = row.getColumns().getValue(column, StringSerializer.get(), null); 
				} else if (column.equals("sentiment")) {
					s = Integer.parseInt(row.getColumns().getValue(column, StringSerializer.get(), null));
					sCSS = GlobalVariables.SENTI_CSS; // int to css do this later
				} else if (column.equals("time")) {
					//http://stackoverflow.com/questions/4216745/java-string-to-date-conversion
					//							DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
					//Thu May 08 22:21:38 PDT 2014
					ti = row.getColumns().getValue(column, StringSerializer.get(), null).toString();
				} else if (column.equals("text")) {
					te = row.getColumns().getValue(column, StringSerializer.get(), null); 
				}
			}// for each column
			entityInfo = new EntityInfo(e,c,s,sCSS,ti,te);
			logger.info("-------------------------------"
					+ "--------------------------------------------------------"
					+ "--------------------ROW: " 
					+ row.getKey() + " " + row.getColumns().size() 
					+ new EntityInfo(e,c,s,sCSS,ti,te).toString());
		}
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

	@RequestMapping(value = "restapi/searchPhrases", method = RequestMethod.GET)
	@ResponseBody
	public void searchPhrases(@RequestParam("searchPhrases") final String searchPhrases)  {
//		if (searchPhrases == null) {
//			return _entityList;
//		}
		
		GlobalVariables.SPARK_TWT_STREAMING.startSpark(searchPhrases);
	}
}
