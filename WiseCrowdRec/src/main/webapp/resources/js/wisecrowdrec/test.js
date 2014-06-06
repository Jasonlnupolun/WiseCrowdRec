/**
 * reference: http://bl.ocks.org/mbostock
 * https://groups.google.com/forum/#!topic/storm-user/yOBVVxFW8mo
 */
function test(/*eventSourceSocket*/) {

    if (!window.WebSocket) {
        console.log("WebSocket is not supported by this browser!!!");
    } else {
        console.log("WebSocket is supported by this browser.");
    }

    var width = 1500,
        height = 900;

    var fill = d3.scale.category20();

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
    
//    var host = "ws://localhost:9292/wcrstorm";

//       var socket = {
//               start: function () {
//                   var location = "ws://localhost:9292/wcrStorm";
//            	   console.log('Socket Status -----> location: '+ location);
//                   this._ws = new WebSocket(location);
//                   this._ws.onmessage = this._onmessage;
//                   this._ws.onclose = this._onclose;
//               },
//    
//               _onmessage: function (m) {
//            	   console.log('Socket Status -----> _onmessage: ');
//                   nodes.push(node); //Math.random()*width, y: Math.random()*height});
//                   restart();
//                   if (m.data) {
//                       //var theData = m.data;
//    //                   newDataReceived(m.data);
//                   }
//               },
//    
//               _onclose: function (m) {
//            	   console.log('Socket Status -----> _onclose: ');
//                   if (this._ws) {
//                       this._ws.close();
//                   }
//               }
//           };

        restart();
//        socket.start();
           message();
//        connect();

           function message() {
        	   var ws = new WebSocket("ws://0.0.0.0:9292/wcrstorm");
        	   alert(".....->"+ws.toString());
        	   ws.onopen = function()
        	     {
        	        // Web Socket is connected, send data using send()
        	        ws.send("Message to send");
        	        alert("Message is sent...");
        	     };
        	   ws.onmessage = function(event) {
        		   console.log('client: received a message!');
        //		   var node = {x: 500, y: 500};
        		   nodes.push(node); //Math.random()*width, y: Math.random()*height});
        		   restart();
        	   };
        	   ws.onclose = function()
        	     { 
        	        // websocket is closed.
        	        alert("Connection is closed..."); 
        	     };
           }

//           socket.onopen = function(){
//        	    alert("Socket has been opened!");
//        	};
//        	
//        	socket.onmessage = function(msg){
//        	    alert(msg); //Awesome!
//        	};

//        function connect(){
//            try{
//
//                console.log('Host: ----> msg ' + host.toString());
//
//                var socket = new WebSocket(host);
//
//                console.log('Socket Status -> new WebSocket: '+socket.readyState);
//
//                sendMessage(host);
//
//                socket.onopen = function(){
//                    console.log('Socket Status -> onopen: '+socket.readyState+' (open)');
//                };
//
//                socket.onmessage = function(msg){
//                    alert('Received -> onmessage: ');//+msg.data
//                    nodes.push(node); 
//                    restart();
//                };
//
////                socket.onclose = function(){
////                    console.log('Socket Status -> onclose: '+socket.readyState+' (Closed)');
////                };           
//
//            } catch(exception){
//                console.log('Websockets Error --> '+exception);
//            }
//        }
//
//function sendMessage(msg){
//    // Wait until the state of the socket is not ready and send the message when it is...
//    console.log("Waiting For Socket Connection........!!!");
//    waitForSocketConnection(host, function(){
//        console.log("message sent!!!");
//        ws.send(msg);
//    });
//}
//
//// Make the function wait until the connection is made...
//function waitForSocketConnection(socket, callback){
//    setTimeout(
//            function () {
//                if (socket.readyState === 1) {
//                    console.log("Connection is made");
//                    if(callback != null){
//                    	callback();
//                    }
//                    return;
//                } else {
//                    console.log("wait for connection...");
//                    waitForSocketConnection(socket, callback);
//                }
//            }, 5); // wait 5 milisecond for the connection...
//}

function mousemove() {
    cursor.attr("transform", "translate(" + d3.mouse(this) + ")");
}

function mousedown() {
    var point = d3.mouse(this),
        node = {x: point[0], y: point[1]},
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
        .attr("cy", function(d) { return d.y; });
}

function restart() {
    link = link.data(links);

    link.enter().insert("line", ".node")
        .attr("class", "link");

    node = node.data(nodes);

    node.enter().insert("circle", ".cursor")
        .attr("class", "node")
        .attr("r", 3+5*Math.random())
        .call(force.drag);

    force.start();
}
}
