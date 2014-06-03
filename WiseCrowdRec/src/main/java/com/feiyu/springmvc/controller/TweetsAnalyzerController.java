package com.feiyu.springmvc.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
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

import com.feiyu.spark.SparkTwitterStreaming;
import com.feiyu.springmvc.model.EntityInfo;
import com.feiyu.springmvc.model.EntityList;
import com.feiyu.springmvc.model.Person;
import com.feiyu.springmvc.service.PersonService;
import com.feiyu.storm.streamingdatacollection.BackgroundTopology;
import com.feiyu.util.GlobalVariables;
import com.feiyu.util.InitializeWCR;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.StringSerializer;

@Controller
public class TweetsAnalyzerController {
	private static final Logger logger = LoggerFactory.getLogger(TweetsAnalyzerController.class);
	private PersonService personService;
	private EntityInfo entityInfo;
	private List<EntityInfo> entitiesInfo = new ArrayList<>();
	private EntityList _entityList = new EntityList(); 
	private	InitializeWCR initWcr = new InitializeWCR();

	@Autowired
	public TweetsAnalyzerController(PersonService personService) {
		this.personService = personService;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(HttpServletRequest req, HttpServletResponse resp,Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate );

		return "index";
	}

	@RequestMapping(value = "/startbackgroundtopology")
	@ResponseBody
	public void startBackgroundTopology() throws Exception { 
		logger.info("Welcome -> startbackgroundtopology");

		initWcr.getWiseCrowdRecConfigInfo();
		initWcr.twitterInitBack();
		initWcr.cassandraInitial();
		initWcr.coreNLPInitial();
		initWcr.themoviedbOrgInitial();

		BackgroundTopology t = new BackgroundTopology();

		boolean isFakeTopologyForTest = false;
		t.startTopology(isFakeTopologyForTest, "wcr_topology_back", "I rated #IMDb");
	}

	@RequestMapping(value = "/startdynamicsearch")
	@ResponseBody
	public void startDynamicSearch() throws Exception { 
		logger.info("Welcome -> start dynamic search");

//		initWcr.getWiseCrowdRecConfigInfo();
		initWcr.twitterInitDyna();
		initWcr.elasticsearchInitial();
//		initWcr.coreNLPInitial();

		SparkTwitterStreaming sts = new SparkTwitterStreaming();
		sts.sparkInit();
		sts.startSpark("movie");
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
	public EntityList searchPhrases(@RequestParam("searchPhrases") final String searchPhrases) throws NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, ClassNotFoundException, TimedOutException, URISyntaxException, IOException, TException, ConnectionException, InterruptedException, ExecutionException {
		if (searchPhrases == null) {
			return _entityList;
		}

		// onUpdata listener
		int i = 1;
		while (i > 0) {
			i--;
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
				logger.info("--------------------------------------------------------"
						+ "---------------------------------------------------"
						+ "ROW: " + row.getKey() + " " + row.getColumns().size() 
						+ new EntityInfo(e,c,s,sCSS,ti,te).toString());
				if (entityInfo != null) {
					entitiesInfo.add(entityInfo);
				}
				_entityList = new EntityList(searchPhrases, entitiesInfo);
			}//for eachrow
		}

		logger.info("size--" + Integer.toString(_entityList.getEntitiesInfo().size()));
		for (EntityInfo item:_entityList.getEntitiesInfo()) {
			logger.info(_entityList.getKeywordPhrases()+" "+item.toString() + " "+ _entityList.toString());
		}
		return _entityList;
	}
}
