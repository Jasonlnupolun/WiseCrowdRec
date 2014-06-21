/**
 * reference: http://bl.ocks.org/mbostock
 * https://groups.google.com/forum/#!topic/storm-user/yOBVVxFW8mo
 * 
 * http://www.d3noob.org/2013/03/d3js-force-directed-graph-example-basic.html
 * http://jsfiddle.net/DEeNB/36/
 */

function test(eventSourceSocket) {

    if (!window.WebSocket) {
        console.log("WebSocket is not supported by this browser!!!");
    } else {
        console.log("WebSocket is supported by this browser.");
    }
    
//	var movieGraph = {
//			  "nodes":[
////			    {"movieName":"Myriel","group":1},
////			    {"movieName":"Count","group":1},
////			    {"movieName":"OldMan","group":1},
////			    {"movieName":"Mabeuf","group":8},
////			    {"movieName":"Mme.Hucheloup","group":8}
//			  ],
//			  "links":[
////			    {"source":1,"target":0,"value":1},
////			    {"source":2,"target":0,"value":8},
////			    {"source":3,"target":0,"value":10},
////			    {"source":4,"target":1,"value":10},
//			  ]
//			};

    var width = 1500,
        height = 900;
    
    var color = d3.scale.category20();

    var force = d3.layout.force()
        .size([width, height])
        //   	.nodes([{}]) // initialize with a single node
        .linkDistance(30)
        .charge(-60)
        .on("tick", tick);

    var svg = d3.select("body").append("svg")
        .attr("width", width)
        .attr("height", height)
        .on("mousemove", mousemove)
        .on("mousedown", mousedown);

    svg.append("rect")
        .attr("width", width)
        .attr("height", height);

    var nodes = force.nodes(),
        links = force.links(),
        node = svg.selectAll(".node"),
        link = svg.selectAll(".link");
    
//    var texts = svg.selectAll(".text")
//    	.data(nodes)
//    	.enter().append("text")
//    	.attr("class", "label")
//    	.attr("fill", "black")
//    	.text(function(d) {  return d.name;  });

    //   node.append("image")
    //      .attr("xlink:href", "https://github.com/favicon.ico")
    //      .attr("x", -8)
    //      .attr("y", -8)
    //      .attr("width", 16)
    //      .attr("height", 16);
    //   node.append("text")
    //      .attr("dx", 12)
    //      .attr("dy", ".35em")
    //      .text("test");

    var cursor = svg.append("circle")
        .attr("r", 30)
        .attr("transform", "translate(-100,-100)")
        .attr("class", "cursor");

    restart();
    stormMessage();
    sparkSSEmessage();
//    sparkMsg();

    function stormMessage() {
        var ws = new WebSocket("ws://0.0.0.0:9292/wcrstorm");
        ws.onopen = function() {
//            ws.send("Message to send");
            console.log("Web Socket "+ws.toString()+" is open, and message is sent from the client to the server");
        };
        ws.onmessage = function(msg) {
            console.log('client: received a message! ' + msg.data);
//            var json = JSON.parse();
//            var json = JSON.parse(msg.data);
            //		   var node = {x: 500, y: 500};
//            nodes.push({"movieName":json.count,"group":1}); //Math.random()*width, y: Math.random()*height});
            console.log('node[0]---- ' + node[0].Entity);
            nodes.push(node);
            restart();
        };
        ws.onclose = function() { 
            alert("Websocket is closed..."); 
        };
    }

    function sparkSSEmessage() {
    	eventSourceSocket.onmessage = function(event) {
// 		   var node = {x: event.id, y: event.data};
// 		   var node = {x: 500, y: 500};
 		   nodes.push(node); //Math.random()*width, y: Math.random()*height});
    	   console.log('spark-------' + event);
 		   restart();
 	   };
    }
    
    function sparkMsg() {
    	// https://github.com/webbit/webbit
//    	var ws = new WebSocket('ws://' + document.location.host + '/hellowebsocket');
    	var ws = new WebSocket('ws://localhost:9876/hellowebsocket');
        console.log('Connecting...');
        ws.onopen = function() { 
        	console.log('Connected!'); 
        	};
        ws.onclose = function() { 
        	console.log('Lost connection'); 
        	};
        ws.onmessage = function(msg) { 
        	console.log(msg.data); 
        	};
    }

    function mousemove() {
        cursor.attr("transform", "translate(" + d3.mouse(this) + ")");
    }

    function mousedown() {
        var point = d3.mouse(this),
            node = {x: point[0], y: point[1], movieRating: "1"}, 
            n = nodes.push(node);
        
        // add links to any nearby nodes
        nodes.forEach(function(target) {
            var x = target.x - node.x,
            y = target.y - node.y;
        if (Math.sqrt(x * x + y * y) < 30) {
            links.push({source: node, target: target});
        }
        });

        restart();
    }

    function tick() {
        link.attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        node.attr("cx", function(d) { return d.x; })
            .attr("cy", function(d) { return d.y; })
        	.attr("movieRating", "1");
//	    node.attr("transform", function(d) { 
//	        return 'translate(' + [d.x, d.y] + ')'; 
//	    });     
        
    }

    function restart() {
        link = link.data(links);
        
        link.enter().insert("line", ".node")
            .attr("class", "link");

        node = node.data(nodes);
        node.enter().insert("circle", ".cursor")
            .attr("class", "node")
            .attr("r", 3+5*Math.random())
//            .style("fill", function(d) { return color(d.movieRating); })
            .call(force.drag);
        
        
//        var labels = node.enter().insert("text")
//		  .text(function(d) { return d.movieRating; });
//        
//        console.log(labels);
        
        force.start();
    }
}
