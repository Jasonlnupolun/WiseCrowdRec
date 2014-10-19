/**
 * reference http://stackoverflow.com/questions/19111581/d3js-force-directed-on-hover-to-node-highlight-colourup-linked-nodes-and-link 
 */
function RBMD3() {
    if (typeof document.addEventListener === "undefined") {
        window.alert("Your browser does not support document.addEventListener, use other browsers like Chrome instead please.");
    }

    if (!window.WebSocket) {
        window.alert("WebSocket is not supported by this browser!!! Use other browsers like Chrome instead please.");
    }

    var w = 1500,
        h = 900;

    var svg = d3.select("body").append("svg:svg")
        .attr("width", w)
        .attr("height", h);

    svg.append("rect")
        .attr("width", w)
        .attr("height", h);

    var force = d3.layout.force()
        .size([w, h])
    //	      .nodes([{}]) // initialize with a single node
    .linkDistance(function (d) {
        return (10 * 12);
    })
    //    .friction(0.6) 
    .charge(-300); //@  

    var nodes = force.nodes(),
        links = force.links(),
        node = svg.selectAll("g.node"),
        link = svg.selectAll("line.link");

    stormMessage();

    function stormMessage() {
        var wsurl = 'ws://0.0.0.0:9292/wcrstorm';
        var wcrstormws = new WebSocket(wsurl);
        wcrstormws.onopen = function () {
            //            wcrstormws.send("Message to send");
            console.log(wsurl + " is open, and message is sent from the client to the server");
        };
        wcrstormws.onmessage = function (msg) {
            console.log(wsurl + " received a message! " + msg.data);
            //	        var json = JSON.parse(msg.data);
            //            nodes.push({"movieName":json.count,"group":1}); //Math.random()*width, y: Math.random()*height});
            //            console.log('node[0]---- ' + node[0].Entity);
            //            nodes.push(node);
            // nodes.push({
            //	        "name": json.movie.movieName,
            //	            "count": json.count
            //	    });
            //	restart();
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
            var json = JSON.parse(msg.data);
            json = {"entity":"movie",
            		"name": "test",
            		"fullname":"json"};
            startShowRelationGraph(json);
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
            var json = JSON.parse(msg.data);
            startShowRelationGraph(json);
        };
    };

    function startShowRelationGraph(json) {
        if (json.hasOwnProperty('nodes')) {
            link = link.data(json.links);
            node = node.data(json.nodes);
        } else {
            if (json.hasOwnProperty('name')) {
                nodes.push(json);
            } else if (json.hasOwnProperty('source')) {
                links.push(json);
            }
            link = link.data(links);
            node = node.data(nodes);
        }

        link.enter().append("svg:line")
            .attr("class", function (d) {
            return d.type;
        })
            .attr("x1", function (d) {
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

        node.enter().append("svg:g")
            .attr("class", "node")
            .call(force.drag);

        node.append("circle")
            .on("click", likeornot())
            .attr("class", function (d) {
            return "node type" + d.entity;
        })
            .attr("r", function (d) {
            if (d.entity == "othernode") {
                return 6;
            } else {
                return 18;
            }
        });

        node.append("text")
            .attr("class", function (d) {
            return "nodetext title_" + d.name;
        })
            .attr("dx", 0)
            .attr("dy", ".35em")
            .style("font-size", "10px")
            .attr("text-anchor", "middle")
            .style("fill", "white")
            .text(function (d) {
            if (d.entity != "othernode") {
                return d.name;
            }
        });

        if (json.hasOwnProperty('nodes')) {
            force = force.nodes(json.nodes)
                .links(json.links);
        }
        force.start();

        node.on("click", function (d) {
            if (d.entity == "movie") {
                d3.select(this).select('circle')
                    .transition()
                    .duration(300)
                    .attr("class", function (d) {
                    return "node typedislikemovie";
                })
                    .attr("r", 6);
            } else if (d.entity == "genre") {
                d3.select(this).select('circle')
                    .transition()
                    .duration(300)
                    .attr("class", function (d) {
                    return "node typedislikegenre";
                })
                    .attr("r", 6);
            }
        });


        node.on("mouseover", function (d) {
            link.style('stroke-width', function (l) {
                if (d === l.source || d === l.target) return 5;
            });
            if (d.entity == "movie" || d.entity == "actor" || d.entity == "genre") {
                d3.select(this).select('text')
                    .transition()
                    .duration(300)
                    .text(function (d) {
                    return d.fullname;
                })
                    .style("font-size", "8px");
            } else {
                d3.select(this).select('text')
                    .transition()
                    .duration(300)
                    .style("font-size", "15px");
            }

            if (d.entity == "movie" || d.entity == "actor" || d.entity == "genre") {
                d3.select(this).select('circle')
                    .transition()
                    .duration(300)
                    .attr("r", 28);
            }
        });


        node.on("mouseout", function (d) {
            link.style('stroke-width', 2);
            if (d.entity == "movie" || d.entity == "genre") {
                d3.select(this).select('text')
                    .transition()
                    .duration(300)
                    .text(function (d) {
                    return d.name;
                })
                    .style("font-size", "10px");
            } else if (d.entity == "actor") {
                d3.select(this).selectAll('text').remove();
                //d3.select(this).select('text')
                d3.select(this).append('text')
                    .text(function (d) {
                    return d.name;
                })
                    .style("font-size", "14px")
                    .attr("dx", 0)
                    .attr("dy", ".35em")
                    .attr("text-anchor", "middle")
                    .style("fill", "white")
                    .attr("class", "nodetext")
                    .transition()
                    .duration(300)
                    .style("font-size", "10px");
            } else {
                d3.select(this).select('text')
                    .transition()
                    .duration(300)
                    .style("font-size", "10px");
            }
            if (d.entity == "movie" || d.entity == "actor" || d.entity == "genre") {
                d3.select(this).select('circle')
                    .transition()
                    .duration(300)
                    .attr("r", 18);
            }
        });


        force.on("tick", function () {
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

            node.attr("transform", function (d) {
                return "translate(" + d.x + "," + d.y + ")";
            });
        });
    }

    function likeornot() {
        return function (d) {
            if (d.entity == "movie" || d.entity == "genre") {
                var wsurl = 'ws://localhost:7777/likeornot';
                var likeornotws = new WebSocket(wsurl);
                console.log(wsurl + " is connecting...");
                likeornotws.onopen = function () {
                    console.log(wsurl + " connected!");
                    var dislikeObject = {
                        "fullname": d.fullname,
                            "entity": d.entity
                    };
                    var msgInfo = JSON.stringify(dislikeObject);
                    likeornotws.send(msgInfo);
                    console.log("sent to server: dislike " + msgInfo);

                };
                likeornotws.onclose = function () {
                    console.log(wsurl + " closed!");
                };
                likeornotws.onmessage = function (msg) {
                    console.log(wsurl + " received message: " + msg.data);
                };
            }
        };
    }
}