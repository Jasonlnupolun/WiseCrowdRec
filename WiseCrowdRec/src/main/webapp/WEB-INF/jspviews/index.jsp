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
	<script type="text/javascript" src="./resources/js/vivagraph/vivagraph.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	<!-- Classie - class helper functions by @desandro https://github.com/desandro/classie -->
	<script src="resources/js/codrops/classie.js"></script>
	
	<!-- scroll bar start-->
	<!-- from perfect-scrollbar https://github.com/noraesae/perfect-scrollbar -->
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
	
	<script type="text/javascript">
		function start() {
			socialGraph();
			menuNav();
			validatePersonId(personId);
		}
		function socialGraph () {
                // Step 1. We create a graph object.
                var graph = Viva.Graph.graph();

                // Step 2. We add nodes and edges to the graph:
                graph.addLink(1, 2);

                /* Note: graph.addLink() creates new nodes if they are not yet
                   present in the graph. Thus calling this method is equivalent to:

                   graph.addNode(1);
                   graph.addNode(2);
                   graph.addLink(1, 2);
                   */

                // Step 3. Render the graph.
                var renderer = Viva.Graph.View.renderer(graph);
                renderer.run();
            }
            function menuNav () {
            	var menuLeft = document.getElementById( 'cbp-spmenu-s1' ),
            	menuRight = document.getElementById( 'cbp-spmenu-s2' ),
            	//menuTop = document.getElementById( 'cbp-spmenu-s3' ),
            	menuBottom = document.getElementById( 'cbp-spmenu-s4' ),
            	//showLeft = document.getElementById( 'showLeft' ),
            	showRight = document.getElementById( 'showRight' ),
            	showBottom = document.getElementById( 'showBottom' ),
            	showLeftPush = document.getElementById( 'showLeftPush' ),
            	//showRightPush = document.getElementById( 'showRightPush' ),
            	body = document.body;

            	/*showLeft.onclick = function() {
            		classie.toggle( this, 'active' );
            		classie.toggle( menuLeft, 'cbp-spmenu-open' );
            		disableOther( 'showLeft' );
            	};*/
            	showRight.onclick = function() {
            		classie.toggle( this, 'active' );
            		classie.toggle( menuRight, 'cbp-spmenu-open' );
            		disableOther( 'showRight' );
            	};
            	showBottom.onclick = function() {
            		classie.toggle( this, 'active' );
            		classie.toggle( menuBottom, 'cbp-spmenu-open' );
            		disableOther( 'showBottom' );
            	};
            	showLeftPush.onclick = function() {
            		classie.toggle( this, 'active' );
            		classie.toggle( body, 'cbp-spmenu-push-toright' );
            		classie.toggle( menuLeft, 'cbp-spmenu-open' );
            		disableOther( 'showLeftPush' );
            	};
           /*  	showRightPush.onclick = function() {
            		classie.toggle( this, 'active' );
            		classie.toggle( body, 'cbp-spmenu-push-toleft' );
            		classie.toggle( menuRight, 'cbp-spmenu-open' );
            		disableOther( 'showRightPush' );
            	}; */
            }
            function disableOther( button ) {
            	/*if( button !== 'showLeft' ) {
            		classie.toggle( showLeft, 'disabled' );
            	}*/
            	if( button !== 'showRight' ) {
            		classie.toggle( showRight, 'disabled' );
            	}
            	if( button !== 'showBottom' ) {
            		classie.toggle( showBottom, 'disabled' );
            	}
            	if( button !== 'showLeftPush' ) {
            		classie.toggle( showLeftPush, 'disabled' );
            	}
            /* 	if( button !== 'showRightPush' ) {
            		classie.toggle( showRightPush, 'disabled' );
            	} */
            }
        </script>
    </head>
    
    <body class="cbp-spmenu-push">
    	<nav class="cbp-spmenu cbp-spmenu-vertical cbp-spmenu-left" id="cbp-spmenu-s1">
    		<h3>Left Menu</h3>
    		<a href="${pageContext.request.contextPath}">Back to home</a>
    <!-- AJAX json begin -->
    			
    	<br><br><br>
	<div class="container">
	
		Spring MVC AJAX Json<br><br>		
		-- Random Person Generator: 
		<br>
		<input type="submit" value="Get Random Person" id="randomPerson" class="btn btn-success">
		<br/><br/>
		<div id="personResponse"> </div>
		<br>
		<br>
		
		-- Get By ID: 
		<br>
		<form id="idForm">
			<div class="error hide" id="idError">Please enter a valid ID in range 0-3</div>
			<label for="personId">ID (0-3): </label><input name="id" id="personId" value="0" type="number" />
			<input type="submit" value="Get Person By ID" class="btn btn-success"/> <br /><br/>
			<div id="personIdResponse"> </div>
		</form>
		
		<br>
		<br>
		
		-- Submit new Person: 
		<br>
		<form id="newPersonForm">
			<label for="nameInput">Name: </label>
			<input type="text" name="name" id="nameInput" />
			<br/>
			
			<label for="ageInput">Age: </label>
			<input type="text" name="age" id="ageInput" />
			<br/>
			<input type="submit" value="Save Person" class="btn btn-success" /><br/><br/>
			<div id="personFormResponse" class="green"> </div>
		</form>
	</div>
				
		<script type="text/javascript">	
		$(document).ready(function() {	
			// Random Person AJAX Request
			$('#randomPerson').click(function() {
				$.getJSON('${pageContext.request.contextPath}/restapi/person/random', function(person) {
					$('#personResponse').text(person.name + ', age ' + person.age);
				});
			});
			
			// Request Person by ID AJAX
			$('#idForm').submit(function(e) {
				var personId = +$('#personId').val();
				if(!validatePersonId(personId)) 
					return false;
				$.get('${pageContext.request.contextPath}/restapi/person/' + personId, function(person) {
					$('#personIdResponse').text(person.name + ', age ' + person.age);
				});
				e.preventDefault(); // prevent actual form submit
			});
			
			// Save Person AJAX Form Submit
			$('#randomPerson').click(function() {
				$.getJSON('${pageContext.request.contextPath}/restapi/person/random', function(person) {
					$('#personResponse').text(person.name + ', age ' + person.age);
				});
			});
			
			$('#newPersonForm').submit(function(e) {
				// will pass the form date using the jQuery serialize function
				$.post('${pageContext.request.contextPath}/restapi/person', $(this).serialize(), function(response) {
					$('#personFormResponse').text(response);
				});
				
				e.preventDefault(); // prevent actual form submit and page reload
			});
			
		});
		
		function validatePersonId(personId) {
			console.log(personId);
			if(personId === undefined || personId < 0 || personId > 3) {
				$('#idError').show();
				return false;
			} else {
				$('#idError').hide();
				return true;
			};
		}
		
	</script>
			
    <!-- AJAX json end -->
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

	<script type="text/javascript" src="resources/js/jquery.js"></script>
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
    	</nav>
    	<div class="containerheadbar">
    		<header>
    			<span></span> <!-- head name -->
    			<nav>
    				<!-- http://tympanus.net/Blueprints/QuotesRotator/ -->
    				<a href="#" class="icon-arrow-left" data-info="Posts">Posts</a>
    				<a href="#" class="icon-drop" data-info="#">#</a>
    				
    			</nav>
    			<button id="showLeftPush" class="icon-drop" data-info="Show/Hide Left Push Menu">Show/Hide Left Push Menu</button>
    			<button id="showBottom" class="icon-drop" data-info="Show/Hide Bottom Slide Menu">Show/Hide Bottom Slide Menu</button>
    			<button id="showRight" class="icon-drop" data-info="Show/Hide Right Slide Menu">Show/Hide Right Slide Menu</button>
                <!-- button id="showLeft"class="icon-drop" data-info="Show/Hide Left Slide Menu">Show/Hide Left Slide Menu</button--> 

            </header>
        </div>
        
        <!-- show linked data graph -->
        <script>
           window.onload=start ;
       	</script>

   </body>
   </html>
