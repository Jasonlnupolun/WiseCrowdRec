/**
 * reference: http://bl.ocks.org/mbostock
 * https://groups.google.com/forum/#!topic/storm-user/yOBVVxFW8mo
 * 
 * http://www.d3noob.org/2013/03/d3js-force-directed-graph-example-basic.html
 * http://jsfiddle.net/DEeNB/36/
 * 
 * D3 API: https://github.com/mbostock/d3/wiki/API-Reference
 */

function WCRD3() {
    if (typeof document.addEventListener === "undefined") {
        window.alert("Your browser does not support document.addEventListener, use other browsers like Chrome instead please.");
    }

    if (!window.WebSocket) {
        window.alert("WebSocket is not supported by this browser!!! Use other browsers like Chrome instead please.");
    }

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
        //.linkDistance(function(d) { return d.distance; }),
        node = svg.selectAll(".node"),
        link = svg.selectAll(".link"),
        label = svg.selectAll(".text");

    //   node.append("image")
    //      .attr("xlink:href", "https://github.com/favicon.ico")
    //      .attr("x", -8)
    //      .attr("y", -8)
    //      .attr("width", 16)
    //      .attr("height", 16);

    var cursor = svg.append("circle")
        .attr("r", 30)
        .attr("transform", "translate(-100,-100)")
        .attr("class", "cursor");

    restart();
    stormMessage();

    //    function show() {
    //        nodes.push({"name":"Myriel","group":1});
    //        nodes.push({"name":"Napoleon","group":1});
    //        links.push({"source":1,"target":0,"value":1});
    //    }

    function stormMessage() {
        var wsurl = 'ws://0.0.0.0:9292/wcrstorm';
        var wcrstormws = new WebSocket(wsurl);
        wcrstormws.onopen = function () {
            //            wcrstormws.send("Message to send");
            console.log(wsurl + " is open, and message is sent from the client to the server");
        };
        wcrstormws.onmessage = function (msg) {
            console.log(wsurl + " received a message! " + msg.data);
            var json = JSON.parse(msg.data);
            //            nodes.push({"movieName":json.count,"group":1}); //Math.random()*width, y: Math.random()*height});
            //            console.log('node[0]---- ' + node[0].Entity);
            //            nodes.push(node);
            nodes.push({
                "name": json.movie.movieName,
                "count": json.count
            });
            restart();
        };
        wcrstormws.onclose = function () {
            console.log(wsurl + " is closed...");
        };
    }

    this.sparkMsgWS = function sparkMsgWebSocket() {
        var wsurl = 'ws://localhost:8899/sparkws';
        var sparkws = new WebSocket(wsurl);
        console.log(wsurl + " connecting...");
        sparkws.onopen = function () {
            console.log(wsurl + " connected!");
        };
        sparkws.onclose = function () {
            console.log(wsurl + " closed!");
        };
        sparkws.onmessage = function (msg) {
            console.log(wsurl + " received message: " + msg.data);
            nodes.push({
                "name": msg.data,
                "count": 1
            });
            restart();
        };
    };

    this.smcSubGraphMsgWS = function smcSubGraphWebSocket(userID) {
        var wsurl = 'ws://localhost:9988/smcsubgraphws/' + userID;
        var smcsubgraphws = new WebSocket(wsurl);
        console.log(userID + " " + wsurl + " connecting...");
        smcsubgraphws.onopen = function () {
            console.log(userID + " " + wsurl + " connected!");
        };
        smcsubgraphws.onclose = function () {
            console.log(userID + " " + wsurl + " closed!");
        };
        smcsubgraphws.onmessage = function (msg) {
            console.log(userID + " " + wsurl + " received message: " + msg.data);
            nodes.push({
                "name": msg.data,
                "count": 1
            });
            restart();
        };
    };

    //    function sparkSSEmessage() {
    //    	sparkEventSourceSocket.onmessage = function(event1) {
    //    	   console.log('sparkSSEmessage-------' + event1.data);
    // 		   nodes.push({"name": event1.data ,"count":1});
    // 		   restart();
    // 	   };
    //    }
    //    
    //    function smgSubGraphSSEmessage() {
    //    	smgSubGraphEventSourceSocket.onmessage = function(event2) {
    //    	   console.log('smgSubGraphSSEmessage-------' + event2.data);
    // 		   nodes.push({"name": event2.data ,"count":1});
    // 		   restart();
    // 	   };
    //    }

    function mousemove() {
        cursor.attr("transform", "translate(" + d3.mouse(this) + ")");
    }

    function mousedown() {
        var point = d3.mouse(this),
            node = {
                x: point[0],
                y: point[1],
                count: "2"
            },
            n = nodes.push(node);

        // add links to any nearby nodes
        nodes.forEach(function (target) {
            var x = target.x - node.x,
                y = target.y - node.y;
            if (Math.sqrt(x * x + y * y) < 30) {
                links.push({
                    source: node,
                    target: target
                });
            };
        });

        restart();
    }

    function tick() {
        link.attr("x1", function (d) {
            return d.source.x;
        })
            .attr("y1", function (d) {
            return d.source.y;
        })
            .attr("x2", function (d) {
            return d.target.x;
        })
            .attr("y2", function (d) {
            return d.target.y;
        });

        node.attr("cx", function (d) {
            return d.x;
        })
            .attr("cy", function (d) {
            return d.y;
        });
        //.attr("movieRating", "1");

        label.attr("dx", function (d) {
            return d.x;
        })
            .attr("dy", function (d) {
            return d.y;
        });

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
            .attr("r", function (d) {
            return d.count;
        })
            .style("fill", function (d) {
            return color(d.count);
        })
            .call(force.drag);

        label = label.data(nodes);
        label.enter().append("text")
            .attr("class", "label")
            .attr("fill", "blue")
            .text(function (d) {
            return d.name;
        });

        force.start();
    }
}