package com.feiyu.springmvc.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import twitter4j.TwitterException;

import com.feiyu.springmvc.service.SignInWithTwitterService;
import com.feiyu.twitter.FollowingWhom;
import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

@Controller
public class TweetsAnalyzerController {
	private static Logger logger = Logger.getLogger(TweetsAnalyzerController.class.getName());
	private SignInWithTwitterService signInWithTwitterService = new SignInWithTwitterService();
	private InitializeWCR initWcr = new InitializeWCR();
	String userID = null;

//	@Autowired
//	public TweetsAnalyzerController(SignInWithTwitterService signInWithTwitterService) {
//		this.signInWithTwitterService = signInWithTwitterService;
//	}

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

		return signInWithTwitterService.obtainingARequestToken();
	}

	@RequestMapping(value = "twitter/callback", params = {"oauth_token", "oauth_verifier"} , method = RequestMethod.GET)
	@ResponseBody
	public String converRequestToken2AccessToken(
			@RequestParam(value = "oauth_token") final String oauth_token, 
			@RequestParam(value = "oauth_verifier") final String oauth_verifier)
	throws Exception  { 
		return signInWithTwitterService.converRequestToken2AccessToken(oauth_token, oauth_verifier);
	}
	
	@RequestMapping(value = "smgSubGraphSSEmessage", params = {"user_id"}, method = RequestMethod.GET)
	@ResponseBody
	public void smgSubGraphSSEmessage(@RequestParam(value = "user_id") final String user_id) throws NumberFormatException, ConnectionException, TwitterException, IOException {
		FollowingWhom fw = new FollowingWhom();
		fw.getFollowingWhomList(user_id);
		userID = user_id;
		logger.info("Welcome -> smgSubGraphSSEmessage");
	}
	
	@RequestMapping(value = "/smgSubGraphSSEmessagebutton" )
	@ResponseBody
	public void smgSubGraphSSEmessagebutton() throws NumberFormatException, ConnectionException, TwitterException, IOException {
		FollowingWhom fw = new FollowingWhom();
		fw.getFollowingWhomList(userID);
		logger.info("Welcome -> smgSubGraphSSEmessagebutton");
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

	@RequestMapping(value = "restapi/searchPhrases", method = RequestMethod.GET)
	@ResponseBody
	public void searchPhrases(@RequestParam("searchPhrases") final String searchPhrases)  {
		//		if (searchPhrases == null) {
		//			return _entityList;
		//		}

		GlobalVariables.SPARK_TWT_STREAMING.startSpark(searchPhrases);
	}
}
