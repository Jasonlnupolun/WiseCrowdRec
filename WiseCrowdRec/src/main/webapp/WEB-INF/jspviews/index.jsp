<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html lang="en" class="no-js">
<head>
	<meta name="description" content="WiseCrowdRec">
	<meta name="keywords" content="HTML,CSS,XML,JavaScript,WiseCrowdRec,Recommender system">
	<meta name="author" content="Fei Yu (faustineinsun)">
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> 
	<title>WiseCrowdRec</title>
	
	<!--  link rel="shortcut icon" href="../favicon.ico" -->
	<link rel="stylesheet" type="text/css" href="resources/css/headbar.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/component.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap.css">

	<script src="resources/js/codrops/modernizr.custom.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	<!-- Classie - class helper functions by @desandro https://github.com/desandro/classie -->
	<script src="resources/js/codrops/classie.js"></script>
	
	<!-- scroll bar start from perfect-scrollbar https://github.com/noraesae/perfect-scrollbar -->
	<!-- modified by feiyu -->
    <link href="resources/css/perfect-scrollbar.css" rel="stylesheet" type="text/css">
   	<script src="resources/js/perfectscrollbar/jquery.mousewheel.js"></script>
    <script src="resources/js/perfectscrollbar/perfect-scrollbar.js"></script>	
    <style>
        .contentHolder { position:relative; margin:25px; padding:5 px; width: 800px; height: 500px; overflow: hidden; }
    </style>	
	<script>
      		jQuery(document).ready(function ($) {
        		"use strict";
        	$('#Default').perfectScrollbar();
      	});
    </script>
	<!-- scroll bar end-->
	
	<!-- start social graph from http://bl.ocks.org/mbostock/929623  modified by feiyu-->
    <link href="resources/css/social-graph.css" rel="stylesheet" type="text/css">
    <!-- link href="resources/css/ftest.css" rel="stylesheet" type="text/css"-->
	<script src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>
	<script type="text/javascript" src="resources/js/wisecrowdrec/WCRD3.js"></script>
	<!-- end http://bl.ocks.org/mbostock/929623  -->
	
	<script type="text/javascript" src="resources/js/wisecrowdrec/menu.js"></script>
	<script type="text/javascript" src="resources/js/wisecrowdrec/HistogramD3.js"></script>

	<script type="text/javascript">
	function start() {
		menuNav();
	}
    </script>
     
   </head>
    
    <body class="cbp-spmenu-push">
    	<nav class="cbp-spmenu cbp-spmenu-vertical cbp-spmenu-left" id="cbp-spmenu-s1">
    		<h3>Left Menu</h3>
    <br>

    	</nav>
    	<nav class="cbp-spmenu cbp-spmenu-vertical cbp-spmenu-right" id="cbp-spmenu-s2">
    		<h3> &#8592; Back to home by clicking this round blue button.</h3>
    <br>
    <br>
    		
	<div class="container">
		<div class="row">
			<div class="col-md-6">
				<textarea class="form-control" rows="3"
					placeholder="Text...  current time: ${serverTime}." id="text"
					name="text"></textarea>
				
				<input type="submit" value="Search Phrases & Run Sentiment Analysis" id="submit" class="btn btn-success">
			</div>
		</div>
	</div>
	<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/mustache.js/0.7.2/mustache.min.js"></script>
	<script type="text/javascript">
		$("#submit")
				.on("click",
						function(event) {
							$("#result").empty();
							event.preventDefault();
							$('#loading').show();
							var text = $("textarea#text").val();
							var searchPhrases = text;
							if (document.URL.indexOf("oauth_verifier=") > -1) {
								if (text) {
									$.get('${pageContext.request.contextPath}/restapi/searchPhrases?searchPhrases='+ searchPhrases, function(){
									});
								}else{
									alert("Please enter text in textarea");
								}
							} else {
								alert("Please click the \"Sign in with Twitter\" button in advance");
							}
						});
	</script>	
    <br>
    <br>
    <br>
	
	
	<div class="container">
	    <div id="Default" class="contentHolder">
			Top mentioned movie stars in current 5 mins:
			<div id="sparkchart"></div>
    		<br>
			Top mentioned movies today:
			<div id="stormchart"></div>
		</div>
	</div>
	 
    	</nav>
    	<nav class="cbp-spmenu cbp-spmenu-horizontal cbp-spmenu-bottom" id="cbp-spmenu-s4">
    		<h3>Bottom Menu</h3>
            <a href="http://tympanus.net/codrops/?p=14725">Slide and push menus template</a>
            <a href="http://bl.ocks.org/mbostock">http://bl.ocks.org/mbostock</a>
    	</nav>
    	<div class="containerheadbar">
    		<header>
    			<span></span> <!-- head name -->
    			<nav>
    				<!-- http://tympanus.net/Blueprints/QuotesRotator/ -->
					
    <!-- Sign in with twitter begins here -->
		<img id="signinwithtwitter" src="resources/images/sign-in-with-twitter-gray.png" data-info="Sign in with twitter."/>
		<div id="signinwithtwittershowmsg"></div>
		<script type="text/javascript" id="signinwithtwitter">	
		$('#signinwithtwitter').click(function() {
			var callbackURL = document.URL;
			$.getJSON('${pageContext.request.contextPath}/signinwithtwitter/login?callbackURL='+callbackURL, function(jsonRet) {
				var head_str = "https://api.twitter.com/oauth/authenticate?oauth_token=";
				var oauthToken = jsonRet.oauthToken;
				var redirect2url = head_str.concat(oauthToken);
				console.log(head_str+"***");
				window.open(redirect2url, '_blank');
				});			
			});
		var wcrd3 = new WCRD3();
		if (document.URL.indexOf("oauth_verifier=") > -1) {
			$('#signinwithtwittershowmsg').text('Logged into Twitter!');
			/* var freebase= require('freebase'); */
			var curURL = document.URL;
			var oauth_token = curURL.substring(curURL.indexOf("oauth_token=")+12, curURL.indexOf("&oauth_verifier="));
			var oauth_verifier = curURL.substring(curURL.indexOf("&oauth_verifier=")+16); 
			$.get('${pageContext.request.contextPath}/twitter/callback?oauth_token='+ oauth_token+'&oauth_verifier='+oauth_verifier,
			function(user_id) {
				console.log('twitter/callback?oauth_token='+ oauth_token+'&oauth_verifier='+oauth_verifier);
				
				var stepOne = function () {
					console.log('stepOne');
					var r = $.Deferred();
	 				$.get('${pageContext.request.contextPath}/startWebSocketWithUserID?user_id='+ user_id, function() {
						console.log('client startWebSocketWithUserID');
					}); 

					setTimeout(function () {
					    r.resolve();
					 }, 2500);
					  return r;
					};
					
				var stepTwo = function () {
					console.log('stepTwo');
					var r = $.Deferred();
					wcrd3.smcSubGraphMsgWS(user_id);
					wcrd3.sparkMsgWS();
					renderChart();
					setTimeout(function () {
					    r.resolve();
					 }, 2500);
					  return r;
					};
					
				var stepThree = function () {
					console.log('stepThree');
					var r = $.Deferred();
	 				$.get('${pageContext.request.contextPath}/smcSubGraphws?user_id='+ user_id, function() {
						console.log('client smcSubGraphws');
					}); 

					setTimeout(function () {
					    r.resolve();
					 }, 2500);
					  return r;
					};
					
				stepOne().done(stepTwo);
				stepTwo().done(stepThree);
				});
		} else {
			$('#signinwithtwittershowmsg').text('Click and get the magic!!'); 
		}
		</script>
    <!-- Sign in with twitter ends here-->		
					
    				
    			</nav>
    			<button id="showLeftPush" class="icon-drop" data-info="Show/Hide Left Push Menu">Show/Hide Left Push Menu</button>
    			<button id="showBottom" class="icon-drop" data-info="Show/Hide Bottom Slide Menu">Show/Hide Bottom Slide Menu</button>
    			<button id="showRight" class="icon-drop" data-info="Show/Hide Right Slide Menu">Show/Hide Right Slide Menu</button>
                <!-- button id="showLeft"class="icon-drop" data-info="Show/Hide Left Slide Menu">Show/Hide Left Slide Menu</button--> 

            </header>
        </div>
        
        <!-- show linked data graph -->
        <script>
           window.onload=start;
       	</script>

   </body>
   </html>
