<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html lang="en" class="no-js">
<head>
	<meta charset="UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> 
	<meta name="viewport" content="width=device-width, initial-scale=1.0"> 
	<title>Information Recommendation</title>
	<meta name="description" content="Blueprint: Slide and Push Menus" />
	<meta name="keywords" content="sliding menu, pushing menu, navigation, responsive, menu, css, jquery" />
	<meta name="author" content="Codrops" />
	<!--  link rel="shortcut icon" href="../favicon.ico" -->
	<link rel="stylesheet" type="text/css" href="resources/css/headbar.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/component.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap.css">
	<script src="resources/js/codrops/modernizr.custom.js"></script>
	<script type="text/javascript" src="./resources/js/vivagraph/vivagraph.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	<!-- Classie - class helper functions by @desandro https://github.com/desandro/classie -->
	<script src="resources/js/codrops/classie.js"></script>
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
            	menuTop = document.getElementById( 'cbp-spmenu-s3' ),
            	menuBottom = document.getElementById( 'cbp-spmenu-s4' ),
            	//showLeft = document.getElementById( 'showLeft' ),
            	showRight = document.getElementById( 'showRight' ),
            	showBottom = document.getElementById( 'showBottom' ),
            	showLeftPush = document.getElementById( 'showLeftPush' ),
            	showRightPush = document.getElementById( 'showRightPush' ),
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
            	showRightPush.onclick = function() {
            		classie.toggle( this, 'active' );
            		classie.toggle( body, 'cbp-spmenu-push-toleft' );
            		classie.toggle( menuRight, 'cbp-spmenu-open' );
            		disableOther( 'showRightPush' );
            	};
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
            	if( button !== 'showRightPush' ) {
            		classie.toggle( showRightPush, 'disabled' );
            	}
            }
        </script>
    </head>
    
    <body class="cbp-spmenu-push">
    	<nav class="cbp-spmenu cbp-spmenu-vertical cbp-spmenu-left" id="cbp-spmenu-s1">
    		<h3>Left Menu</h3>
    		<a href="http://localhost:8080/WiseCrowdRecWebApp/">Back to home</a>
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
			}
			else {
				$('#idError').hide();
				return true;
			}
		}
		
	</script>
			
    <!-- AJAX json end -->
    	</nav>
    	<nav class="cbp-spmenu cbp-spmenu-vertical cbp-spmenu-right" id="cbp-spmenu-s2">
    		<h3>Right Menu</h3>
    		<a href="http://localhost:8080/WiseCrowdRecWebApp/">Back to home</a>  
    <!-- Sentiment Analysis begin -->

	<!-- Sentiment Analysis end -->
	 
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
    				<a href="#" class="icon-drop" data-info="Menus Templates from Codrops">Menus Templates from Codrops</a>
    				
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
