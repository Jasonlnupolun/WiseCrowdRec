/**
 * reference http://stackoverflow.com/questions/19111581/d3js-force-directed-on-hover-to-node-highlight-colourup-linked-nodes-and-link
 * @author feiyu
 * https://github.com/faustineinsun/WiseCrowdRec/blob/master/WiseCrowdRec/src/main/webapp/resources/js/wisecrowdrec/RBMD3.js
 */

var panelPsnNmRecordsCount = 0;
var panelMvRecordsCount = 0;
var panelRecRecordsCount = 0;
var panelPsnNmHtml = "";
var panelMvHtml = "";

function RBMD3() {
    if (typeof document.addEventListener === "undefined") {
        window.alert("Your browser does not support document.addEventListener, use other browsers like Chrome instead please.");
    }

    if (!window.WebSocket) {
        window.alert("WebSocket is not supported by this browser!!! Use other browsers like Chrome instead please.");
    }

    var w = "1200",
        h = "800";

    var nodeSmallSize = 6,
        nodeSize = 16,
        nodeLargeSize = 28;

    var likeActor,
    likeMovie,
    likeGenre;

    var svg = d3.select("d3amggraph").append("svg:svg")
        .attr("width", w)
        .attr("height", h);

    svg.append("rect")
        .attr("width", w)
        .attr("height", h);

    this.socketioevents = function socketIOEvents() {
        //var ipAddress = "http://127.0.0.1:9888";
        var ipAddress = "http://146.148.72.155:9888";
        var socket = io.connect(ipAddress, {
            'reconnection delay': 2000,
                'force new connection': true
        });
        console.log(ipAddress + ': client is connecting to the server ... If the connection is correct, you will receive a message: \"' + ipAddress + ': client has connected to the server!\"');

        socket.on('connect', function () {
            console.log(ipAddress + ': client has connected to the server!');
        });
        socket.on('disconnect', function () {
            console.log(ipAddress + ': client has disconnected to the server!');
        });

        socket.on('paneltweetsi', function (data) {
            //console.log("paneltweetsi received message: "+data);
            $(instantTweets).html("<p style='color:LightSeaGreen; text-align:center; font-size:100%; font-family:verdana'>" + data + "</p>");
        });

        socket.on('panelpersonnamesi', function (data) {
            if (panelPsnNmRecordsCount == 15) {
                $(extractedPersonNames).empty();
                panelPsnNmRecordsCount = 0;
                panelPsnNmHtml = "";
            }
            panelPsnNmRecordsCount++;
            //console.log("panelpersonnamesi received message: "+data);
            var dataHtml = "<p style='color:LightSeaGreen; text-align:center; font-size:100%; font-family:verdana'>" + data + "</p>";
            panelPsnNmHtml = dataHtml + panelPsnNmHtml;
            $(extractedPersonNames).html(panelPsnNmHtml);
        });

        socket.on('paneltopkpersonsi', function (data) {
            //console.log("paneltopkpersonsi received message: " + data);
            var json = JSON.parse(data);
            var topKHtml = "";
            for (var i in json) {
                var name = json[i]['MovieStarName'];
                var count = json[i]['CountInFiveMinus'];
                topKHtml += "<p style='color:LightSeaGreen; text-align:center; font-size:100%; font-family:verdana; margin: 0cm 0cm 0cm 0cm;'>" + name + ": " + count + "</p>";
            }
            $(topKPerson).html(topKHtml);
        });

        socket.on('panelrelatedmoviesi', function (data) {
            if (panelMvRecordsCount == 65) {
                $(relatedMovies).empty();
                panelMvRecordsCount = 0;
                panelMvHtml = "";
            }
            panelMvRecordsCount++;
            //console.log("panelrelatedmoviesi received message: " + data);
            var dataHtml = "<p style='color:LightSeaGreen; text-align:left; font-size:100%; font-family:verdana'>>> " + data + "</p>";
            panelMvHtml = dataHtml + panelMvHtml;
            $(relatedMovies).html(panelMvHtml);
        });

        socket.on('panelrecsi', function (data) {
            if (panelRecRecordsCount == 20) {
                $('#movieRatingResults').empty();
                panelRecRecordsCount = 0;
            }
            //console.log("panelrecsi received message: "+data);
            panelRecRecordsCount++;
            var dataJSON = {
                msgID: "--msgID--",
                text: data,
            };
            var template = $('#movieRatingResultInBootstrap').html();
            var html = Mustache.to_html(template, dataJSON);
            $('#movieRatingResults').prepend(html);
        });

        socket.on('showsmcgraphsi', function (data) {
            //console.log("showsmcgraphsi received message: " + JSON.stringify(data));
            var json = JSON.parse(data);
            startShowRelationGraph(json);
        });

    }

    function startShowRelationGraph(json) {
        //d3.select("svg").remove();
        d3.selectAll("svg > *").remove();

        var force = d3.layout.force()
            .size([w, h])
        //.nodes([{}]) // initialize with a single node
        .linkDistance(function (d) {
            return (6 * 12);
        })
        //.friction(0.6)
        .charge(-300); //@

        var nodes = force.nodes(),
            links = force.links(),
            node = svg.selectAll("g.node"),
            link = svg.selectAll("line.link");


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
        //.on("click", likeornot())
        .attr("class", function (d) {
            return "node type" + d.entity;
        })
            .attr("r", function (d) {
            if (d.entity == "othernode") {
                return nodeSmallSize;
            } else {
                return nodeSize;
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
            var currentCur = d3.select(this).select("circle").style("fill");
            //console.log(currentCur);
            switch (currentCur) {
                case "rgb(154, 46, 254)":
                    likeActor = false;
                    break;
                case "rgb(51, 189, 239)":
                    likeMovie = false;
                    break;
                case "rgb(255, 64, 129)":
                    likeGenre = false;
                    break;
                case "rgb(176, 196, 222)":
                    likeActor = true;
                    break;
                case "rgb(32, 178, 170)":
                    likeMovie = true;
                    break;
                case "rgb(250, 128, 114)":
                    likeGenre = true;
                    break;
                default:
            }

            if (d.entity == "actor") {

                d3.select(this).select('circle')
                    .transition()
                    .duration(300)
                    .attr("class", function (d) {
                    if (likeActor) {
                        $("#showLikeOrDis").text("like actor [" + d.fullname+"]");
                        return "node typeactor";
                    } else {
                        $("#showLikeOrDis").text("dislike actor [" + d.fullname+"]");
                        return "node typedislikeactor";
                    }
                })
                    .attr("r", nodeSmallSize);

            } else if (d.entity == "movie") {

                d3.select(this).select('circle')
                    .transition()
                    .duration(300)
                    .attr("class", function (d) {
                    if (likeMovie) {
                        $("#showLikeOrDis").text("like movie [" + d.fullname+"]");
                        return "node typemovie";
                    } else {
                        $("#showLikeOrDis").text("dislike movie [" + d.fullname+"]");
                        return "node typedislikemovie";
                    }
                })
                    .attr("r", nodeSmallSize);

            } else if (d.entity == "genre") {

                d3.select(this).select('circle')
                    .transition()
                    .duration(300)
                    .attr("class", function (d) {
                    if (likeGenre) {
                        $("#showLikeOrDis").text("like genre [" + d.fullname+"]");
                        return "node typegenre";
                    } else {
                        $("#showLikeOrDis").text("dislike genre [" + d.fullname+"]");
                        return "node typedislikegenre";
                    }
                })
                    .attr("r", nodeSmallSize);

            }
        });


        node.on("mouseover", function (d) {
            d3.select(this).select("circle")
                .transition()
                .duration(300)
                .style("opacity", 1);
            d3.select(this).select("text")
                .transition()
                .duration(300)
                .text(function (d) {
                return d.fullname;
            })
                .style("font-size", "11px")
                .style("opacity", 1)
                .style("fill", "black");

            var nodeNeighbors = json.links.filter(function (link) {
                return link.source.index === d.index || link.target.index === d.index;
            })
                .map(function (link) {
                return link.source.index === d.index ? link.target.index : link.source.index;
            });

            svg.selectAll('circle')
                .style("opacity", 0)
                .filter(function (node) {
                return nodeNeighbors.indexOf(node.index) > -1;
            })
                .transition()
                .duration(300)
                .style("opacity", 1);
            d3.selectAll('text')
                .style("opacity", 0)
                .filter(function (node) {
                return nodeNeighbors.indexOf(node.index) > -1;
            })
                .transition()
                .duration(300)
                .text(function (d) {
                return d.fullname;
            })
                .style("font-size", "11px")
                .style("opacity", 1)
                .style("fill", "black");

            link.style("opacity", function (l) {
                return d !== l.source && d !== l.target ? 0 : 1;
            })
                .style('stroke-width', function (l) {
                if (d === l.source || d === l.target) return 3;
            });

        });

        node.on("mouseout", function (d) {
            svg.selectAll('circle')
                .transition()
                .duration(300)
                .attr("r", nodeSize)
                .style("opacity", 1);

            link.transition()
                .duration(300)
                .style('stroke-width', 2)
                .style("opacity", 1);

            d3.selectAll('text')
                .style("opacity", 1)
                .transition()
                .duration(300)
                .text(function (d) {
                return d.name;
            })
                .style("font-size", "10px")
                .style("opacity", 1);

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
                var wsurl = 'ws://146.148.72.155:7777/likeornot';
                var likeornotws = new WebSocket(wsurl);
                console.log(wsurl + " is connecting...");
                likeornotws.onopen = function () {
                    console.log(wsurl + " connected!");
                    var dislikeObject = {
                        "fullname": d.fullname,
                            "entity": d.entity
                    };
                    var msgInfo = JSON.stringify(dislikeObject);
                    //var msgInfo = jQuery.parseJSON(dislikeObject);
                    likeornotws.send(msgInfo);
                    //console.log("sent to server: dislike " + msgInfo);

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
