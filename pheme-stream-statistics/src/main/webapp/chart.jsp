<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:emptypage>
	<jsp:attribute name="bigtitle">
      Topic: <c:out value="${topics}" />
    </jsp:attribute>
    
    <jsp:attribute name="smalltitle">
      Charts
    </jsp:attribute>
    
    <jsp:attribute name="pagenamebreadcrumb">
      Topic monitor > <c:out value="${topic}" />
    </jsp:attribute>

	<jsp:body>
	<!-- <h2>Kafka stats!</h2>  -->

	<script type="text/javascript" src="js/dygraph-combined.js"></script>
	<script type="text/javascript" src="js/jquery-1.12.0.min.js"></script>

	

	<h3></h3>

	

	<c:forEach var="topic" items="${topics}" varStatus="number">
		
		
		<div id="div_seconds_${topic}" style="width: 700px; height: 300px; margin-bottom: 30px;"></div>
		
		<div id="div_minutes_${topic}" style="width: 700px; height: 300px; margin-bottom: 30px;"></div>
		
		<div id="div_hours_${topic}" style="width: 700px; height: 300px; margin-bottom: 30px;"></div>
	
	</c:forEach>

	<script type="text/javascript">
		var topics = ${topicsjs};
	</script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			/* var data = [];
			var t = new Date();
			for (var i = 10; i >= 0; i--) {
				var x = new Date(t.getTime() - i * 1000);
				data.push([ x, Math.random() ]);
			} */
			
			var chartList = {};

			for (var i = 0; i<topics.length; i++) {
				//console.info(topics[i]);
			
				var minDate = new Date();
				minDate.setDate(minDate.getDate()-1);
 
				chartList[topics[i]] = {};
				
				chartList[topics[i]].s = new Dygraph(document.getElementById("div_seconds_" + topics[i]), [[0,0]], {
					title : "Messages per second (up to last 5 hours)",
					drawPoints : true,
					showRoller : false,
					valueRange : [ 0.0, null ],
					labels : [ 'Time', 'Volume' ],
					showRangeSelector: true
					//dateWindow : [minDate, Date()]
					
				});
				
				chartList[topics[i]]["m"] = new Dygraph(document.getElementById("div_minutes_" + topics[i]), [[0,0]], {
					title : "Messages per minute",
					drawPoints : true,
					showRoller : false,
					valueRange : [ 0.0, null ],
					labels : [ 'Time', 'Volume' ],
					showRangeSelector: true
				});
				
				chartList[topics[i]]["h"] = new Dygraph(document.getElementById("div_hours_" + topics[i]), [[0,0]], {
					title : "Messages per hour",
					drawPoints : true,
					showRoller : false,
					valueRange : [ 0.0, null ],
					labels : [ 'Time', 'Volume' ],
					showRangeSelector: true
				});
				
				
				//console.info(chartList);
			}
			
			
			var updateCharts = function() {
				
				$.each(topics, function(i,currentTopic) { 
					
					var currentTopic = topics[i];
					//console.info(currentTopic);
					$.get("./seconds/" + currentTopic, function(data, status){
				        //console.info("Data: " + data + "\nStatus: " + status);
				        if (data.length != 0) {
				        	//data.shift();
					        d = data.map(function (item) { return [new Date(item.timestamp), item.count]; })
					        //console.info(d);
					        //d.unshift([ 'Time', 'Random' ])
					        chartList[currentTopic]["s"].updateOptions({
								'file' : d
							});
				        }
				        
				    });
					
					$.get("./minutes/" + currentTopic, function(data, status){
				        //console.info("Data: " + data + "\nStatus: " + status);
				        if (data.length != 0) {
				        	//data.shift();
					        d = data.map(function (item) { return [new Date(item.timestamp), item.count]; })
					        //console.info(d);
					        //d.unshift([ 'Time', 'Random' ])
					        chartList[currentTopic]["m"].updateOptions({
								'file' : d
							});
				        	
				        }
				        
				    });
					
					$.get("./hours/" + currentTopic, function(data, status){
				        //console.info("Data: " + data + "\nStatus: " + status);
				        if (data.length != 0) {
				        	//data.shift();
					        d = data.map(function (item) { return [new Date(item.timestamp), item.count]; })
					        //console.info(i);
					        //d.unshift([ 'Time', 'Random' ])
					        //console.info(chartList);
					        //console.info(topics[i]);
					        //console.info(topics);
					        //console.info(currentTopic);
					        chartList[currentTopic]["h"].updateOptions({
								'file' : d
							});
				        	
				        }
				        
				    });
					
					
				});
				
				
				
				//data.push([ x, y ]);
				
			};
			
			
			
			updateCharts();
			
			window.intervalId = setInterval(updateCharts, 5000);
		});
	</script>

    </jsp:body>
    
    
</t:emptypage>
