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

		<!-- Jquery -->
		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

        <!-- Bootstrap -->
		<!-- link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script-->

        <!-- D3.js -->
        <script src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>
        
        <!-- mustache.min.js -->
        <!-- script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/mustache.js/0.7.2/mustache.min.js"></script-->
        
        <link href="resources/css/rbmd3.css" rel="stylesheet" type="text/css">
        <link href="resources/css/wcrstyle.css" rel="stylesheet" type="text/css">

        <script type="text/javascript" src="resources/js/wisecrowdrec/clickinfobox.js"></script>
        <script type="text/javascript" src="resources/js/wisecrowdrec/RBMD3.js"></script>
        <script type="text/javascript" src="resources/js/wisecrowdrec/HistogramD3.js"></script>
        <!--  script type="text/javascript" src="resources/js/wisecrowdrec/WCRD3.js"></script-->
    </head>
            
 	<body>
        <div class="floating-item-container">
            <div class="button-container"> <a href="#" class="btn btn-float "> <i class="mdi-social-people-outline"></i> </a> </div>
            <div class="modal"> <a href="#" class="close"> <i class="mdi-navigation-close"></i> </a>
                <div class="modal-content">
                    <textarea class="form-control" rows="5.3" cols="50" placeholder="Try entering some keywords that you are interested in ..." id="text" name="text"></textarea>
                    <br>
                    <a href="#" class="css_btn_class" id="submit">Search From Twitter and Start Movie Recommendation</a>
                <script type="text/javascript">
                    $("#submit").on("click", function(event) {
                        $("#result").empty();
                        event.preventDefault();
                        $('#loading').show();
                        var text = $("textarea#text").val();
                        var searchPhrases = text;
                        if (document.URL.indexOf("oauth_verifier=") > -1) {
                            if (text) {
                                $.get('${pageContext.request.contextPath}/restapi/searchPhrases?searchPhrases=' + searchPhrases, function() {});
                                $('#infoBarRBM').text('Wait Please. RBM training is in processing...');
                            } else {
                                alert("Please enter text in textarea");
                            }
                        } else {
                            alert("Click the \"Sign in with Twitter\" button first, please.");
                        }
                    });
                </script>
                
                <br> <br> <br> 
                Top mentioned movie stars in current 5 mins:
                <div id="sparkchart"></div>
                
                <br>
                Top mentioned movies today:
                <div id="stormchart"></div>
                
                </div>    
            </div>
        </div>
	
        <div class="floating-item-container-2">
        <!-- image from http://www.psd100.com/sign-in-facebook-and-twitter-buttons-psd/#.VUVDDdNViko -->
         <img id="signinwithtwitter" src="resources/images/TwitterButtons.png" data-info="Sign in with twitter." />
            <div id="genresdislike" class="genresDislikeButton">Genre</div>
            <div id="moviesdislike" class="moviesDislikeButton">Movie</div>
            <div id="infoBarGM"></div>
            <div id="recRBM" class="recButton">Rec &#38; Predi</div>
            <div id="infoBarRBM"></div>
            <div id="infoBarTwitter"></div>
            <script type="text/javascript">
                $('#signinwithtwitter').click(function() {
                    var callbackURL = document.URL;
                    $.getJSON('${pageContext.request.contextPath}/signinwithtwitter/login?callbackURL=' + callbackURL, function(jsonRet) {
                        var head_str = "https://api.twitter.com/oauth/authenticate?oauth_token=";
                        var oauthToken = jsonRet.oauthToken;
                        var redirect2url = head_str.concat(oauthToken);
                        console.log(head_str + "***");
                        window.open(redirect2url, '_blank');
                    });
                });
                /* var wcrd3 = new WCRD3(); */
                var rbmd3 = new RBMD3();
                if (document.URL.indexOf("oauth_verifier=") > -1) {
                    document.getElementById("signinwithtwitter").style.display = "none";
                    document.getElementById("recRBM").style.display = "none";
                    $('#infoBarGM').text('Click Genres(Red)/Movies(Blue) You Dislike');
                    $('#infoBarRBM').text('You also can search any pharses you like in the text box!');
                    $('#infoBarTwitter').text('Logged into Twitter! ');
                    /* var freebase= require('freebase'); */
                    var curURL = document.URL;
                    var oauth_token = curURL.substring(curURL.indexOf("oauth_token=") + 12, curURL.indexOf("&oauth_verifier="));
                    var oauth_verifier = curURL.substring(curURL.indexOf("&oauth_verifier=") + 16);
                    $.get('${pageContext.request.contextPath}/twitter/callback?oauth_token=' + oauth_token + '&oauth_verifier=' + oauth_verifier,
                    function(user_id) {
                        console.log('twitter/callback?oauth_token=' + oauth_token + '&oauth_verifier=' + oauth_verifier);
                        var stepOne = function() {
                            console.log('stepOne');
                            var r = $.Deferred();
                            $.get('${pageContext.request.contextPath}/startWebSocketWithUserID?user_id=' + user_id, function() {
                                console.log('client startWebSocketWithUserID');
                            });
                            setTimeout(function() {
                                r.resolve();
                            }, 2500);
                            return r;
                        };
                        var stepTwo = function() {
                            console.log('stepTwo');
                            var r = $.Deferred();
                            rbmd3.smcSubGraphMsgWS(user_id);
                            rbmd3.sparkMsgWS();
                            renderChart();
                            setTimeout(function() {
                                r.resolve();
                            }, 2500);
                            return r;
                        };
                        var stepThree = function() {
                            console.log('stepThree');
                            var r = $.Deferred();
                            $.get('${pageContext.request.contextPath}/smcSubGraphws?user_id=' + user_id, function() {
                                console.log('client smcSubGraphws');
                            });
                            setTimeout(function() {
                                r.resolve();
                            }, 2500);
                            return r;
                        };
                        stepOne().done(stepTwo);
                        stepTwo().done(stepThree);
                    });
                    $('#genresdislike').click(function() {
                        $('#infoBarGM').text('Click Genres(Red nodes) You Dislike');
                    });
                    $('#moviesdislike').click(function() {
                        $('#infoBarGM').text('Click Movies(Blue nodes) You Dislike');
                    });
                    $('#recRBM').click(function() {
                    	$.get('${pageContext.request.contextPath}/rbmRecPredic', function() {});
                        $('#infoBarRBM').text('Wait Please. Recommended movies will be shown to you..');
                    });
                } else {
                    document.getElementById("genresdislike").style.display = "none";
                    document.getElementById("recRBM").style.display = "none";
                    document.getElementById("moviesdislike").style.display = "none";
                    $('#infoBarTwitter').text('Click and get the magic!!');
                }
            </script>
        </div>
        
 	</body>
        
 </html>
