/**
 * Server Send Event to D3
 * @author feiyu
 */
function MyEventSource() {
		var sparkServerSentEvents2D3Source=null, starMovieGroupSubGraphServerSentEvents2D3Source=null;
   		if (typeof (EventSource) !== "undefined") {
   			sparkServerSentEvents2D3Source = new EventSource("${pageContext.request.contextPath}/SparkServerSentEvents2D3");
//   			starMovieGroupSubGraphServerSentEvents2D3Source = new EventSource("${pageContext.request.contextPath}/StarMovieGroupSubGraphServerSentEvents2D3");
 	 	} 
    		else {
    		window.alert("Your browser does not support server-sent events, use other browsers like Chrome instead please.");
  		}  
   		return {
   	        sparkSSE2D3Source: sparkServerSentEvents2D3Source,
//   	        smgSubGraphSSE2D3Source: starMovieGroupSubGraphServerSentEvents2D3Source
   	    };  
};