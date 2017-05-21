
//tweet template
var panel = '<div class="panel panel-default"> \
	<!-- Default panel contents --> \
<div class="panel-heading"><span class="date">date</span> location: <span class="location"></span></div> \
<div class="panel-body"><span class="user">user</span>: <span class="twittertext">content</span></div> \
</div>';

var panel2 = '<div class="panel panel-default"> \
	<div class="panel-body"> \
	<span class="avatar" style="float: left; margin: 5px; margin-right: 20px; margin-bottom: 30px;"> \
	<img src=""></span> \
	<span class="user" style="font-weight: bold; padding-right: 20px;">@user</span> \
	<span class="date" style="">Thu Oct 15 14:28:22</span> \
	<span class="twittertext" style="display: block; padding-top: 5px;">text</span> \
	</div> \
	</div>';


var onlineIndicator;





$(document).ready(function() {
	//setup tabs:
//	$('#geo a').click(function (e) {
//		  e.preventDefault()
//		  $(this).tab('show')
//	});

	var switchMapMode = function(mode) {
				
		switch(mode) {
		case "1":
			//show hexbin
			tiles.setUrl(mapTilesBWDark);

			map.removeLayer(markers);
			map.removeLayer(heat1Layer);
			map.removeLayer(heat2Layer);
			hex.show();
			//map.addLayer(hexLayer);
			
			break;
		case "2":
			//show heatmap
			tiles.setUrl(mapTilesBWDark);

			map.removeLayer(markers);
			//map.removeLayer(hexLayer);
			hex.hide();
			
			map.addLayer(heat1Layer);
			map.addLayer(heat2Layer);
			
			break;
			
		case "3":
			//show markers (all)
			tiles.setUrl(mapTilesBWLight);
			
			map.addLayer(markers);
			
			map.removeLayer(heat1Layer);
			map.removeLayer(heat2Layer);
			//map.removeLayer(hexLayer);
			hex.hide();
			
			break

		case "4":
			//show markers (better)
			tiles.setUrl(mapTilesBWLight);

			map.addLayer(markers);
			
			map.removeLayer(heat1Layer);
			map.removeLayer(heat2Layer);
			//map.removeLayer(hexLayer);
			hex.hide();
			
			break
			
		default:
			console.info("default");
			
		}
		
		
	}
	
	
	$('.mapbutton').each(function(){
        //clear selection:
        $(this).removeClass("btn-info").removeClass("active");
        
        $(this).click(function(e) {
        	$('.mapbutton').removeClass("btn-info");
        	$(e.target).addClass("btn-info");
        	switchMapMode($(this).val())
        })
      });
	
	//set default mapbutton (heatmap)
	//values are the following:
	//1 - hexbin
	//2 - heatmap
	//3 - markers
	$('.mapbutton[value=1]').addClass("btn-info");
	
	
	//second map on hidden panel doesn't work 100% as expected.
//	$(".nav-sidebar a").click(function(e) {
//		console.info("SSSSS");
//		map2.invalidateSize();
//	});
	
	function setOffline() {
		$("#stream-status").removeClass("progress-bar-success progress-bar-striped active").addClass("progress-bar-warning");
		$("#stream-status").text("Offline");
	}
	
	function setOnline() {
		$("#stream-status").removeClass("progress-bar-warning").addClass("progress-bar-success progress-bar-striped active");
		$("#stream-status").text("Online");
	}
	
	function setConnecting() {
		$("#stream-status").text("Connecting...");
	}
	
	function setError() {
		$("#stream-status").removeClass("progress-bar-success progress-bar-striped active").addClass("progress-bar-danger");
		$("#stream-status").text("Error! Can't connect.");
	}
	
	function setupEventSource() {
		var output = document.getElementById("log");
		if (typeof (EventSource) !== "undefined") {
			
			var source = new EventSource("async");
			
			source.onmessage = function(event) {
				
				var obj = $.parseJSON(event.data);
				console.info(obj);
				if (!obj["heartbeat"]) {
					
					//debug
					//console.info(obj.msg.geo);
					//console.info(obj.msg.place.bounding_box);
					
					if (obj.msg.geo) {
						console.info("Geo: " + obj.msg.geo);
//						var circle = L.circle(
//							[obj.msg.geo.coordinates[0], obj.msg.geo.coordinates[1]], 
//							500, {
//								color: 'red',
//							    fillColor: '#f03',
//							    fillOpacity: 0.5
//							}).addTo(map);
						
						//paint the marker on the map in the "geo tab" 
						var circle = L.marker(
							[obj.msg.geo.coordinates[0], obj.msg.geo.coordinates[1]], 
							{
								color: 'red',
							    fillColor: '#f03',
							    fillOpacity: 0.5,
							    radius: "8",
							    _type: "circle"
							}).addTo(markers).bindPopup(obj.msg.text);
						
						hex.addPoint([{lat: obj.msg.geo.coordinates[0], long: obj.msg.geo.coordinates[1]}]);
						//map.eachLayer(function(e) {if (e.options._type) map.removeLayer(e)})
						
						//add point ot the heatmap:
						var randomnumber = Math.floor(Math.random() * (100 - 1 + 1)) + 1;
						if (randomnumber % 2 == 0) {
							heat1.addLatLng([obj.msg.geo.coordinates[0], obj.msg.geo.coordinates[1]]);
							
						} else {
							heat2.addLatLng([obj.msg.geo.coordinates[0], obj.msg.geo.coordinates[1]]);
						}
						
						
					}
					
					elem = $(panel2);
					elem.find("span.date")[0].innerHTML = obj.msg.created_at;
					elem.find("span.twittertext")[0].innerHTML = obj.msg.text;
					elem.find("span.user")[0].innerHTML = obj.msg.user.screen_name;
					elem.find("img")[0].src = obj.msg.user.profile_image_url;
//					if (obj.msg.location) { 
//						elem.find("span.location")[0].innerHTML = obj.msg.location;
//					}
					elem.hide();
					
					a = $("#log").prepend(elem);
					
					if ($("#log .panel").size() > 20) {
						$("#log .panel:last-child").remove();
					}
					
					//elem.slideDown().fadeIn(300);
					elem.stop(true, true)
				        .animate({
				            height:"toggle",
				            opacity:"toggle"
				        },200);
					
					//console.info(a);
					//console.info(a.length);
					//output.innerHTML += event.data + "<br>";
					//output.innerHTML += obj.msg.created_at + " " + obj.msg.user.screen_name + " " + obj.msg.text + " " + obj.msg.location + "<br>";					
				} else {
					//heartbeat message
					clearTimeout(onlineIndicator);
					setOnline();
					onlineIndicator = setTimeout(setOffline, 5000);
					
				}
			};
			
			source.addEventListener('close', function(event) {
				//output.innerHTML += event.data + "<hr/>";
				source.close();
			}, false);
			
		} else {
			output.innerHTML = "Sorry, Server-Sent Events are not supported in your browser";
		}
		return false;
	}

	
	$("#progress-bar-controller").click(function() {
		setConnecting();
		setupEventSource();
	});
			
	
	setupEventSource();
	
	//go offline after 5 seconds, unless an heartbeat is received
	onlineIndicator = setTimeout(setOffline, 5000);
	

    
    //create maps using leaflet library
	//use black/white tiles from: http://cartodb.com/basemaps
    map = L.map('map', {maxZoom: 17}).setView([40.4165508,-3.7037990000000036], 10);
    map.se
    
    //"positive" heatmap
    heat1Layer = L.layerGroup();
    heat1Layer.addTo(map);
    
    heat1 = L.heatLayer([]);
    heat1.setOptions({gradient: {"0.1": "gray", "0.6": "green"}, minOpacity: 0.5, radius: 7, blur: 8});
    heat1.addTo(heat1Layer);
    map.removeLayer(heat1Layer);
    
    //"negative" heatmap
    heat2Layer = L.layerGroup();
    heat2Layer.addTo(map);
    
    heat2 = L.heatLayer([]);
    heat2.setOptions({gradient: {"0.1": "yellow", "0.6": "red"}, minOpacity: 0.5, radius: 14, blur: 10})
    heat2.addTo(heat2Layer);
    map.removeLayer(heat2Layer);
    
    //markers layer (one layer to add/remove all markers
    //markers = L.layerGroup();
    //markers.addTo(map);
    //map.removeLayer(markers);
    
    //markerclusters
    var markers = new L.MarkerClusterGroup();
    //markers.addLayer(new L.Marker());
    map.addLayer(markers);
    map.removeLayer(markers);
    
    //create layer for hexbins
    function toGeoJSON(array) {
        var data = [];
        array.map(function (d){
        	var randomnumber = Math.floor(Math.random() * (100 - 1 + 1)) + 1;
        	if (randomnumber % 2 == 0) {
        		var sent = 0;
        	} else {
        		var sent = 1;
        	}
          data.push({
            properties: {
              group: +sent,
              //city: d.city,
              //state: d.state,
              //store: d.storenumber
              type: "tweet"
            }, 
            type: "Feature", 
            geometry: {
              coordinates:[+d.long,+d.lat], 
              type:"Point"
            }
          });
        });
        return data;
      }
    
    
    cscale = d3.scale.linear().domain([0,1]).range(["#00FF00","#FF0000"]);

    L.HexbinLayer = L.Class.extend({
        includes: L.Mixin.Events,
        initialize: function (rawData, options) {
          this.levels = {};
          this.layout = d3.hexbin().radius(10);
          this.rscale = d3.scale.sqrt().range([0, 10]).clamp(true);
          this.rwData = rawData;
          this.config = options;
          this.visible = true;
        },
        project: function(x) {
          var point = this.map.latLngToLayerPoint([x[1], x[0]]);
          return [point.x, point.y];
        },
        getBounds: function(d) {
          var b = d3.geo.bounds(d)
          //fixed bounds 39.76,-4.54,   40.96, -2.71
          
          return L.bounds(this.project([-4.54,39.76]), this.project([-2.71,40.96]));
        },
        addPoint: function(p) {
        	this.rwData.features.push(toGeoJSON(p)[0]);
        	this.update();
        },
        
        update: function () {
        	if (this.rwData.features.length != 0 && this.visible == true) {
                var pad = 100, xy = this.getBounds(this.rwData), zoom = this.map.getZoom();

                this.container
                  .attr("width", xy.getSize().x + (2 * pad))
                  .attr("height", xy.getSize().y + (2 * pad))
                  .style("margin-left", (xy.min.x - pad) + "px")
                  .style("margin-top", (xy.min.y - pad) + "px");

                //if (!(zoom in this.levels)) {
                    this.levels[zoom] = this.container.append("g").attr("class", "zoom-" + zoom);
                    this.genHexagons(this.levels[zoom]);
                    this.levels[zoom].attr("transform", "translate(" + -(xy.min.x - pad) + "," + -(xy.min.y - pad) + ")");
                //}
                if (this.curLevel) {
                  this.curLevel.style("display", "none");
                }
                this.curLevel = this.levels[zoom];
                this.curLevel.style("display", "inline");
        		
        	}
        },
        genHexagons: function (container) {
          var data = this.rwData.features.map(function (d) {
            var coords = this.project(d.geometry.coordinates)
            return [coords[0],coords[1], d.properties];
          }, this);

          var bins = this.layout(data);
          var hexagons = container.selectAll(".hexagon").data(bins);

          var counts = [];
          bins.map(function (elem) { counts.push(elem.length) });
          this.rscale.domain([0, (ss.mean(counts) + (ss.standard_deviation(counts) * 3))]);

          var path = hexagons.enter().append("path").attr("class", "hexagon");
          this.config.style.call(this, path);

          that = this;
          hexagons
            .attr("d", function(d) { return that.layout.hexagon(that.rscale(d.length)); })
            .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
            .on("mouseover", function (d) { 
              var s=0, k=0;
              d.map(function(e){
                if (e.length === 3) e[2].group === 1 ? ++k : ++s;
              });
              that.config.mouse.call(this, [s,k]);
              d3.select("#tooltip")
                .style("visibility", "visible")
                .style("top", function () { return (d3.event.pageY - 100)+"px"})
                .style("left", function () { return (d3.event.pageX - 180)+"px";})
            })
            .on("mouseout", function (d) { d3.select("#tooltip").style("visibility", "hidden") });
        },
        addTo: function (map) {
          map.addLayer(this);
          return this;
        },
        onAdd: function (map) {
          this.map = map;
          var overlayPane = this.map.getPanes().overlayPane;

          if (!this.container || overlayPane.empty) {
              this.container = d3.select(overlayPane)
                  .append('svg')
                      .attr("id", "hex-svg")
                      .attr('class', 'leaflet-layer leaflet-zoom-hide');
          }
          map.on({ 'moveend': this.update }, this);
          this.update();
        },
        hide: function() {
        	this.visible = false;
        	var hexagons = this.container.selectAll(".hexagon");
        	hexagons.style("visibility", "hidden");
        	
        },
        show: function() {
        	this.visible = true;
        	var hexagons = this.container.selectAll(".hexagon");
        	hexagons.style("visibility", "visible");
        	this.update();
        }

        
      });

      L.hexbinLayer = function (data, styleFunction) {
        return new L.HexbinLayer(data, styleFunction);
      };
      
    function hexbinStyle(hexagons) {
        hexagons
          .attr("stroke", "gray")
          .attr("fill", function (d) {
            var values = d.map(function (elem) {
              return elem[2].group;
            })
            var avg = d3.mean(d, function(d) { return +d[2].group; })
            return cscale(avg);
          });
    };
    function makePie (data) {

        d3.select("#tooltip").selectAll(".arc").remove()
        d3.select("#tooltip").selectAll(".pie").remove()

        var arc = d3.svg.arc()
            .outerRadius(45)
            .innerRadius(10);

        var pie = d3.layout.pie()
             .value(function(d) { return d; });

        var svg = d3.select("#tooltip").select("svg")
                    .append("g")
                      .attr("class", "pie")
                      .attr("transform", "translate(50,50)");

        var g = svg.selectAll(".arc")
                  .data(pie(data))
                  .enter().append("g")
                    .attr("class", "arc");

            g.append("path")
              .attr("d", arc)
              .style("fill", function(d, i) { return i === 1 ? 'red':'green'; });

            g.append("text")
                .attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")"; })
                .style("text-anchor", "middle")
                .text(function (d) { return d.value === 0 ? "" : d.value; });
      }
    
    var data = { type: "FeatureCollection", features: toGeoJSON([{long: 40.4165508, lat: -3.7037990000000036}]) };
    
    hexLayer = L.layerGroup();
    hexLayer.addTo(map);
    
    hex = L.hexbinLayer(data, {
        style: hexbinStyle,
        mouse: makePie
    }).addTo(hexLayer);
    //hexLayer.setOptions();

    
    
    
    //paint map
    var mapLink = "<a href='http://openstreetmap.org'>OpenStreetMap</a>";
    var mapTilesBWToner = "//stamen-tiles.a.ssl.fastly.net/toner/{z}/{x}/{y}.png";
    var mapTilesBWLight = "http://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png";
    var mapTilesBWDark = "http://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}.png";
    var mapSourceNormal = "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";

    tiles = L.tileLayer(
    	mapTilesBWDark, {
        attribution: '&copy; ' + mapLink + ' Contributors',
        maxZoom: 18,
    }).addTo(map);

	//
//	map2 = L.map('heatmap').setView([40.4165508,-3.7037990000000036], 10);
//    
// 	var mapLink2 = 
//        '<a href="http://openstreetmap.org">OpenStreetMap</a>';
//    var mapSourceBW2 = "//stamen-tiles.a.ssl.fastly.net/toner/{z}/{x}/{y}.png";
//    var mapSourceNormal2 = "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
//    L.tileLayer(
//        mapSourceNormal2, {
//        attribution: '&copy; ' + mapLink2 + ' Contributors',
//        maxZoom: 18,
//    }).addTo(map2);
    
    
});