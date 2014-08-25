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

	<script type="text/javascript">
	function start() {
		menuNav();
		WCRD3();
	}
    </script>
     
   </head>
    
    <body class="cbp-spmenu-push">
    	<nav class="cbp-spmenu cbp-spmenu-vertical cbp-spmenu-left" id="cbp-spmenu-s1">
    		<h3>Left Menu</h3>
    		<a href="${pageContext.request.contextPath}">Back to home</a>
    <br>

    	</nav>
    	<nav class="cbp-spmenu cbp-spmenu-vertical cbp-spmenu-right" id="cbp-spmenu-s2">
    		<a href="${pageContext.request.contextPath}">Back to home</a>
    		
    <!-- Sentiment Analysis begin from https://github.com/shekhargulati/day20-stanford-sentiment-analysis-demo -->
    <!-- modified by feiyu -->
	<div class="container">
	
		<div class="row">
			<div class="col-md-6">
				<textarea class="form-control" rows="3"
					placeholder="Text...  current time: ${serverTime}." id="text"
					name="text"></textarea>
				
				<input type="submit" value="Search Phrases & Run Sentiment Analysis" id="submit" class="btn btn-success">
			</div>
		</div>
		<div id="loading" style="display: none;" class="container">
			<img src="resources/images/loader.gif" alt="Please wait.." />
		</div>
		
		<!-- sse -->
 		<div id="sparkSSE2D3"></div>
	 	<script>
  		if (typeof (EventSource) !== "undefined") {
   			var s = new EventSource("${pageContext.request.contextPath}/SparkServerSentEvents2D3");
   			s.onmessage = function(event) {
   			 document.getElementById("sparkSSE2D3").innerHTML += "msg from spark --> " + event.data
     		 + "<br><br>";
 		  };
 	 	} else {
 	  		document.getElementById("sparkSSE2D3").innerHTML = "Your browser does not support server-sent events, use other browsers like Chrome instead please.";
  		}
 		</script>
 		<!-- end sse -->
		
		<c:if test="${not empty serverTime}">
		<div class="textarea">
		${serverTime}
		</div>
		</c:if>

		<div id="Default" class="contentHolder">
      		<div id="result" class="row"></div>
    	</div>
    	
	
	</div>
	
	<script type="text/template" id="searchResult">
	<div class="col-md-12" id="{{keywordPhrases}}">
		<ul class="unstyled">
			{{#entityList}}
				<div class="alert alert-success">
					{{text}}
				</div>
			{{/entityList}}
		</ul>
	</div>
</script>

	<!--  script type="text/javascript" src="resources/js/jquery.js"></script-->
	<script type="text/javascript"
		src="//cdnjs.cloudflare.com/ajax/libs/mustache.js/0.7.2/mustache.min.js"></script>
	<script type="text/javascript">
		$("#submit")
				.on("click",
						function(event) {
							$("#result").empty();
							event.preventDefault();
							$('#loading').show();
							var text = $("textarea#text").val();
							var searchPhrases = text;
							if (text) {
								$.get('${pageContext.request.contextPath}/restapi/searchPhrases?searchPhrases='+ searchPhrases,
								 function(entityList) {
									$('#loading').hide();
									console.log('entityList : ' + entityList);
									var data = { keywordPhrases: searchPhrases+"->"+entityList.keywordPhrases,};
									var template = "Keyword Phrases:{{keywordPhrases}}";
									var html = Mustache.to_html(template, data);
									$('#result').append(html);  
				
									var data = {
										keywordPhrases : entityList.keywordPhrases,
										entityList : entityList.entitiesInfo,};
									var template = $('#searchResult').html();
									var html = Mustache.to_html(template, data);
									$('#result').append(html); 
								});
							}else{
								alert("Please enter text in textarea");
							};
						});
	</script>	
	
	
	<!-- Sentiment Analysis end from https://github.com/shekhargulati/day20-stanford-sentiment-analysis-demo -->
	 
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
<!--     				<button id="startbackgroundtopology" class="icon-drop" data-info="Start Background Topology">Start Background Topology</button>
					<script type="text/javascript" id="startbackgroundtopology">	
						var backTopo = document.getElementById('startbackgroundtopology');
						backTopo.onclick = function() {
							$.get('${pageContext.request.contextPath}/startbackgroundtopology', function() {});
						};
					</script> -->
					
    				<button id="startbackgroundtopology" class="icon-drop" data-info="Start Background Topology">Start Background Topology</button>
					<script type="text/javascript" id="startbackgroundtopology">	
						var backTopo = document.getElementById('startbackgroundtopology');
						backTopo.onclick = function() {
							$.get('${pageContext.request.contextPath}/smgSubGraphSSEmessagebutton', function() {});
						};
					</script>
					
    				<button id="startdynamicsearch" class="icon-drop" data-info="Start Dynamic Search">Start Dynamic Search</button>
					<script type="text/javascript" id="startdynamicsearch">	
						var dynaSearch = document.getElementById('startdynamicsearch');
						dynaSearch.onclick = function() {
							$.get('${pageContext.request.contextPath}/startdynamicsearch', function() {});
						};
					</script>
					
    <!-- Sign in with twitter begins here -->
		<img id="signinwithtwitter" src="resources/images/sign-in-with-twitter-gray.png" data-info="Sign in with twitter."/>
		<div id="signinwithtwittershowmsg"></div>
		<script type="text/javascript" id="signinwithtwitter">	
		$('#signinwithtwitter').click(function() {
			$.getJSON('${pageContext.request.contextPath}/signinwithtwitter/login', function(jsonRet) {
				var head_str = "https://api.twitter.com/oauth/authenticate?oauth_token=";
				var oauthToken = jsonRet.oauthToken;
				var redirect2url = head_str.concat(oauthToken);
				console.log(head_str+"***");
				window.open(redirect2url, '_blank');
				});			
			});
		if (document.URL.indexOf("oauth_verifier=") > -1) {
			$('#signinwithtwittershowmsg').text('Logged into Twitter!');
			/* var freebase= require('freebase'); */
			var curURL = document.URL;
			var oauth_token = curURL.substring(curURL.indexOf("oauth_token=")+12, curURL.indexOf("&oauth_verifier="));
			var oauth_verifier = curURL.substring(curURL.indexOf("&oauth_verifier=")+16); 
			$.get('${pageContext.request.contextPath}/twitter/callback?oauth_token='+ oauth_token+'&oauth_verifier='+oauth_verifier,
			function(user_id) {
				console.log('twitter/callback?oauth_token='+ oauth_token+'&oauth_verifier='+oauth_verifier);
				$.get('${pageContext.request.contextPath}/smcSubGraphws?user_id='+ user_id, function() {
						console.log('client smcSubGraphws -> after sign run this automatically');
					});
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
