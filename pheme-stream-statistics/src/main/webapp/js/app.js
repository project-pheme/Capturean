nodeTemplate = "<div class=\"w jsplumb-endpoint-anchor jsplumb-connected\" id=\"@id\" " + 
               "		data-topic-name=\"@id\"> " +
               "	<span class=\"flowbox-label\">@title</span> <br> " +
               "    @description <br>" +
               "	output topic: @topic " +
               "</div>"; 



jsPlumb.ready(function () {

	$.ajax({
	    type: "GET",
	    url: "./pipeline",
	    dataType: "json",

	    //if received a response from the server
	    success: function (data) {
	    	//clear "spinner" image
	    	$("div#spinner").hide();
	    	
	    	console.info(data);
	    	
	    	var canvas = document.getElementById("canvas");
	    	
	    	//create divs
	    	for (var i = 0; i < data.length; i++) {
	    		currentNode = nodeTemplate;
	    		currentNode = currentNode.replace(/@id/g, data[i].topic_name)
	    		currentNode = currentNode.replace("@title", data[i].node_name)
	    		currentNode = currentNode.replace("@description", data[i].node_description)
	    		currentNode = currentNode.replace("@topic", data[i].topic_name)

	    		elem = $(currentNode);
				
				//elem.hide();
				a = $(canvas).append(elem);
	    	}
	        
	    	
	    	$.ajax({
			    type: "GET",
			    url: "./details",
			    dataType: "json",

			    //if received a response from the server
			    success: function (data) {
			        console.info(data);
			        $.each(data.last_counts, function(k,v) {
			        	console.info(k);
			        	
			        	if (v == 0) {
			        		$("#"+k).css("background-color", "#d9837f");
			        	} else {
			        		$("#"+k).css("background-color", "#5cb85c");
			        	}
			        	
			        	
			        	//console.info($("#"+boxes[k]));	
			        });
					//console.info(boxes);		          
					
					
			    }
			});

	    	$(".w").click(function(e) {
				//console.info(e);
				//console.info(e.currentTarget.getAttribute("data-topic-name"));
				
				var topic = e.currentTarget.getAttribute("data-topic-name");
				
				$.ajax({
				    type: "GET",
				    url: "./details",
				    dataType: "json",

				    //if received a response from the server
				    success: function (data) {
				        //console.info(data);
						var counts = data.last_counts[topic];		          
						var msg = data.last_messages[topic];
						
						$("#overview-topic-name").text(topic);
						$("#overview-last-hour-count").text(counts);
						$("#overview-message-content").text(msg);
						
				    }
				});
				 
				
			});
	    	
	        // setup some defaults for jsPlumb.
	        var instance = jsPlumb.getInstance({
	            Endpoint: ["Dot", {radius: 2}],
	            Connector:"StateMachine",
	            HoverPaintStyle: {strokeStyle: "#1e8151", lineWidth: 2 },
	            ConnectionOverlays: [
	                [ "Arrow", {
	                    location: 1,
	                    id: "arrow",
	                    length: 14,
	                    foldback: 0.8
	                } ],
	                [ "Label", { label: "FOO", id: "label", cssClass: "aLabel" }]
	            ],
	            Container: "canvas"
	        });

	        instance.registerConnectionType("basic", { anchor:"Continuous", connector:"StateMachine" });

	        window.jsp = instance;

	        
	        var windows = jsPlumb.getSelector(".statemachine-demo .w");

	        

	        
	        
	        // bind a click listener to each connection; the connection is deleted. you could of course
	        // just do this: jsPlumb.bind("click", jsPlumb.detach), but I wanted to make it clear what was
	        // happening.
//	        instance.bind("click", function (c) {
//	        	console.info(c);
//	            instance.detach(c);
//	        });

	        // bind a connection listener. note that the parameter passed to this function contains more than
	        // just the new connection - see the documentation for a full list of what is included in 'info'.
	        // this listener sets the connection's internal
	        // id as the label overlay's text.
	        instance.bind("connection", function (info) {
	            //info.connection.getOverlay("label").setLabel(info.connection.id);
	        	info.connection.getOverlay("label").setLabel("");
	        });

	        // bind a double click listener to "canvas"; add new node when this occurs.
//	        jsPlumb.on(canvas, "dblclick", function(e) {
//	            newNode(e.offsetX, e.offsetY);
//	        });

	        //
	        // initialise element as connection targets and source.
	        //
	        var initNode = function(el) {

	            // initialise draggable elements.
	            instance.draggable(el);

	            instance.makeSource(el, {
	                filter: ".ep",
	                anchor: "Continuous",
	                connectorStyle: { strokeStyle: "#5c96bc", lineWidth: 2, outlineColor: "transparent", outlineWidth: 4 },
	                connectionType:"basic",
	                extract:{
	                    "action":"the-action"
	                },
	                maxConnections: 2,
	                onMaxConnections: function (info, e) {
	                    alert("Maximum connections (" + info.maxConnections + ") reached");
	                }
	            });

	            instance.makeTarget(el, {
	                dropOptions: { hoverClass: "dragHover" },
	                anchor: "Continuous",
	                allowLoopback: true
	            });

	            // this is not part of the core demo functionality; it is a means for the Toolkit edition's wrapped
	            // version of this demo to find out about new nodes being added.
	            //
	            instance.fire("jsPlumbDemoNodeAdded", el);
	        };

	        var newNode = function(x, y) {
	            var d = document.createElement("div");
	            var id = jsPlumbUtil.uuid();
	            d.className = "w";
	            d.id = id;
	            d.innerHTML = id.substring(0, 7) + "<div class=\"ep\"></div>";
	            d.style.left = x + "px";
	            d.style.top = y + "px";
	            instance.getContainer().appendChild(d);
	            initNode(d);
	            return d;
	        };

	        // suspend drawing and initialise.
	        instance.batch(function () {
	            for (var i = 0; i < windows.length; i++) {
	                initNode(windows[i], true);
	            }
	            // and finally, make a few connections
//	            var c1 = instance.connect({ source: "topic-raw", target: "topic-capture", type:"basic", connector: "" });
//	            c1.getOverlay("label").setLabel("");
//	            console.info(c1);

//	            instance.connect({ source: "topic-raw", target: "topic-capture", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-capture", target: "topic-events", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-events", target: "topic-entities", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-entities", target: "topic-concepts", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-concepts", target: "topic-preprocessed", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-preprocessed", target: "topic-processed", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-processed", target: "topic-graphdb", type:"basic", connector: "" });

	            
//	            instance.connect({ source: "topic-raw", target: "topic-capture", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-capture", target: "topic-language", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-language", target: "topic-advert", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-advert", target: "topic-events", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-events", target: "topic-entities", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-entities", target: "topic-concepts", type:"basic", connector: "" });
//	            instance.connect({ source: "topic-concepts", target: "topic-processed", type:"basic", connector: "" });

	            //connect nodes
	            for (var i = 0; i < data.length; i++) {
	            	if (data[i].next_node.length != 0) {
		            	instance.connect({ source: data[i].topic_name, target: data[i].next_node, type:"basic", connector: "" });	            		
	            	}
	            }

	            
	        });

	        jsPlumb.fire("jsPlumbDemoLoaded", instance);

	    	
	    	
	    	
	    }
	});
	
	

});
