<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:emptypage>
	<jsp:attribute name="bigtitle">
      Kafka overview
    </jsp:attribute>
    
    <jsp:attribute name="smalltitle">
      Charts
    </jsp:attribute>
    
    <jsp:attribute name="pagenamebreadcrumb">
      Topic monitor > ${title}
    </jsp:attribute>

	<jsp:body>
	<!-- <h2>Kafka stats!</h2>  -->

	<script type="text/javascript" src="js/jquery-1.12.0.min.js"></script>
	<script type="text/javascript" src="js/jsPlumb-2.1.4-min.js"></script>
	<script type="text/javascript" src="js/app.js"></script>
	
			
<!-- 	<h3>Journalist dashboard dataflow.</h3>
 -->	
 		<h3>${title}</h3>

	

	<c:forEach var="topic" items="${topics}" varStatus="number">
		
		
		<div id="div_seconds_${topic}" style="width: 700px; height: 300px; margin-bottom: 30px;"></div>
		
		<div id="div_minutes_${topic}" style="width: 700px; height: 300px; margin-bottom: 30px;"></div>
		
		<div id="div_hours_${topic}" style="width: 700px; height: 300px; margin-bottom: 30px;"></div>
	
	</c:forEach>

	<div class="row">
		<div class="col-sm-3 col-md-offset-1" id="canvas">
			<div id="spinner">Loading <img alt="spinner" src="bootstrap/progress.gif"></div>

 <!--               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-raw" 
               		data-topic-name="raw_journalist">
               	<span class="flowbox-label">Capture: Raw Tweets</span><br> 
               	Journalists Datachannels <br>
               	output topic: journalist_raw
                   
               </div>
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-capture"
               		data-topic-name="pheme_capture">
               	<span class="flowbox-label">Capture: Tweets in Pheme format</span> <br> 
               	output topic: pheme_capture
                   
               </div>
               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-events"
               		data-topic-name="pheme_en_events">
               	<span class="flowbox-label">Event detection</span><br> 
               	output topic: pheme_en_events
                   
               </div>
               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-entities"
               		data-topic-name="pheme_en_entities">
               	<span class="flowbox-label">Entity detection</span><br> 
               	output topic: pheme_en_entities
                   
               </div>
               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-concepts"
               		data-topic-name="pheme_en_concepts">
               	<span class="flowbox-label">Concepts linking</span><br> 
               	output topic: pheme_en_concepts
                   
               </div>
               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-preprocessed"
               		data-topic-name="pheme_en_preprocessed">
               	<span class="flowbox-label">SDQ</span><br> 
               	output topic: pheme_en_preprocessed
                   
               </div>
               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-processed"
               		data-topic-name="pheme_en_processed">
               	<span class="flowbox-label">Rumour classification</span><br> 
               	output topic: pheme_en_processed
                   
               </div>
               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-graphdb"
               		data-topic-name="pheme_en_graphdb">
               	<span class="flowbox-label" style="z-index: 100;">Graph DB</span><br> 
               	output topic: pheme_en_graphdb
               </div>
 -->               
				<!-- 
              <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-raw" 
               		data-topic-name="raw_med">
               	<span class="flowbox-label">Capture: Raw Tweets</span><br> 
               	Medical Use Case Datachannel <br>
               	output topic: med_raw
                   
               </div>
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-capture"
               		data-topic-name="med_capture">
               	<span class="flowbox-label">Capture: Tweets in Pheme format</span> <br> 
               	output topic: med_capture
                   
               </div>
               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-language"
               		data-topic-name="med_en">
               	<span class="flowbox-label">USFD Language Detection</span><br> 
               	output topic: med_en
                   
               </div>
               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-advert"
               		data-topic-name="med_advert">
               	<span class="flowbox-label">KCL Advert and Stigma/Anti-stigma Classification</span><br> 
               	output topic: med_advert
                   
               </div>
               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-events"
               		data-topic-name="med_events">
               	<span class="flowbox-label">USFD Event Clustering</span><br> 
               	output topic: med_events
                   
               </div>
               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-entities"
               		data-topic-name="med_entities">
               	<span class="flowbox-label">USFD Entity Tagging</span><br> 
               	output topic: med_entities
                   
               </div>
               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-concepts"
               		data-topic-name="med_concepts">
               	<span class="flowbox-label">ONTO Concepts Extraction</span><br> 
               	output topic: med_concepts
                   
               </div>

               
               <div class="w jsplumb-endpoint-anchor jsplumb-connected" id="topic-processed"
               		data-topic-name="med_processed">
               	<span class="flowbox-label" style="z-index: 100;">USFD Stance Detection</span><br> 
               	output topic: med_processed
               </div>

					-->
                
		</div>
		<div class="col-sm-6 col-md-offset-1" id="overview-details">
			<div class="box-body">
		        <div class="callout callout-info">
		            <p>Click on the boxes for more details.</p>
		            <p>The red color of the box means no data was seen in the last 1 hour.</p>
		            <p>The last message is <b>not</b> faithful representation (some fields might be ommmited, and raw_json might be slightly distorted). Kafka topics contain a properly formatted messages.</p>
		            
		        </div>
		
		    </div>
				<div>
					<h4>Topic name:</h4>
					<span id="overview-topic-name"></span>
				
				</div>
				
				<div>
					<h4>Number of messages in the last hour:</h4>
					<span id="overview-last-hour-count"></span>
				
				</div>
				<div>
					<h4>The last message:</h4>
					<pre id="overview-message-content"></pre>
				</div>
			
		</div>
			
			
		
	</div>


	
	<script type="text/javascript">
		
	</script>

    </jsp:body>
    
    
</t:emptypage>
