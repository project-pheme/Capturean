//Volume Area and Sentiment Line Chart
$(document).ready(function() {
	
//	var endDate;
//	var startDate;
//	var idS=[];
//	
//	//Ids	
//	for(var i=0; document.getElementById('ids'+i)!= null;i++){
//		var aux =document.getElementById('ids'+i).getAttribute('data-value');
//		console.log("IDS:"+aux);
//		idS[i]=aux;
//	}
	
	//REACH
	var followersP =document.getElementById('followersP').getAttribute('data-value');
	var mentionersP =document.getElementById('mentionersP').getAttribute('data-value');
	var subFollowersP =document.getElementById('subFollowersP').getAttribute('data-value');
	var retweetersP =document.getElementById('retweetersP').getAttribute('data-value');

	//ENGAGEMENT
	var favoritsP =document.getElementById('favoritsP').getAttribute('data-value');
	var mentionsP =document.getElementById('mentionsP').getAttribute('data-value');
	var repliesP =document.getElementById('repliesP').getAttribute('data-value');
	var retweetsP =document.getElementById('retweetsP').getAttribute('data-value');
	console.log("favoritsP="+favoritsP+" mentionsP="+mentionsP+" repliesP="+repliesP+" retweetsP="+retweetsP);

//	//PRUEBA
//	//endDate = moment().subtract(1, 'days').endOf('day').utc().format("ddd MMM DD YYYY HH:mm:ss z");//yesterday
//	//startDate = moment().subtract(2, 'days').startOf('day').utc().format("ddd MMM DD YYYY HH:mm:ss z");//Day before yesterday
//	startDate = moment("2015-10-11T02:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
//	endDate = moment("2015-10-13T01:59:59").utc().format("ddd MMM DD YYYY HH:mm:ss z");
//	console.log("startDate="+startDate);
//	console.log("endDate="+endDate);	
	
	//callReachUpdate();//1800); //sizeSec=1800(30 mins)
	//callEngagementUpdate();//1800);
	
	// create the REACH donut morris chart. 
//	var graphReach = Morris.Donut({
//	    element: 'pie-chartReach',
//	    data: data(""),
//	    data: [
//	           {label: "Followers(%)", value: 50},
//	           {label: "User Mentioning(%)", value: 20},
//	           {label: "User Followers(%)", value: 35},
//	         ],
//	    resize: true,
//	    redraw: true,	    
//	});
	
//	function callReachUpdate(){						
//		if((idS.length>0)&&(frecuency!=null)&&(frecuency!=undefined)){
//			  var iD="";
//			  for (var i=0; i < idS.length; i++) {				 
//				  if(""==iD)
//					  iD=idS[i];
//				  else
//					  iD=iD+','+idS[i]; 
//			  }
//			  console.log("iDs="+iD);			  
//			  var call = 'reachdata?id='+iD+'&fInit='+startDate+'&fEnd='+endDate+'&sizeSec='+frecuency;
//			  console.log(call);
//			  $("#loading-indicator").show();
//			  setTimeout(function(){graphReach.setData(data(call,"r"))},1000); //Delay necessary to show the loading-indicator			  
//		}else{
//			BootstrapDialog.alert({
//				type: BootstrapDialog.TYPE_WARNING,
//	            title: 'Warning!!',
//	            message: 'Error getting Reach data'		            	
//	        });
////			graphReach.setData([ {
////			  date: 0,
////			  volume : 0
////			}]);
//		}
//	}
//
//	function callEngagementUpdate(){
//		if((idS.length>0)&&(frecuency!=null)&&(frecuency!=undefined)){			  
//			  var iD="";
//			  for (var i=0; i < idS.length; i++) {
//				  if(""==iD)
//					  iD=idS[i];
//				  else
//					  iD=iD+','+idS[i]; 
//			  }
//			  console.log("iDS="+iD);			  
//			  var call = 'engagementdata?id='+iD+'&fInit='+startDate+'&fEnd='+endDate+'&sizeSec='+frecuency;
//			  console.log(call);
//			  $("#loading-indicator").show();			  
//			  setTimeout(function(){graphEngage.setData(data(call,"e"))},1000); //Delay necessary to show the loading-indicator			  
//		}else{
//			BootstrapDialog.alert({
//				type: BootstrapDialog.TYPE_WARNING,
//	            title: 'Warning!!',
//	            message: 'Error getting Engagement data'		            	
//	        });
//			
////			graphSent.setData([ {
////				  date: 0,
////				  sentiment : 0
////			}]);
////
//		}
//	}
	
	function data(url) {
		var json;
		console.log("Call url="+url);
		if(url!=""){
			$.ajax({
				'type': 'GET',
				'async': false,
				'global': false,
				'url': url, 
				'dataType': "json",	
				'contentType': "application/json; charset=utf-8",
				'success': function (data) {
					console.log("success data="+data);									
					json=data;  
				},
				'error': function(jqXHR, textStatus, errorThrown){
					$("#loading-indicator").hide();
					BootstrapDialog.alert({
						type: BootstrapDialog.TYPE_DANGER,
			            title: 'ERROR!!',
			            message: 'Error retreiving data: '+errorThrown
			        });
				},
				beforeSend: function(jqXHR) {
			        jqXHR.overrideMimeType('application/json;charset=iso-8859-1');
			    }
			});
		}else{
			json= [ {
				fecha : '0',
				valor : 0,
			}];
		}
//		//alert(json); //BORRAR		
		return json;
	}

	
	Morris.Donut({
	    element: 'pie-chartReach',
	    //data: data(""),
	    data: [
//	           {label: "Followers(%)", value: followersP},
	           {label: "Mencionadores(%)", value: mentionersP}, //Mentioners
//	           {label: "SubFollowers(%)", value: subFollowersP},
	           {label: "Retuiteadores(%)", value: retweetersP}, //Retweeters
	           ],
	    resize: true,
	    redraw: true,	    
	});
	
	Morris.Donut({
	    element: 'pie-chartEngagement',
	    //data: data(""),
	    data: [
	           {label: "Menciones(%)", value: mentionsP}, //Mentions
	           {label: "Retuits(%)", value: retweetsP}, //Retweets
	           {label: "Favoritos(%)", value: favoritsP}, //Favorites
	           {label: "Respuestas(%)", value: repliesP} //Replies
	         ],
	    resize: true,
	    redraw: true,	   
	});
	
	$('#Menbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    $( "#MenLastWeekChart" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	        // Create a Line Chart with Morris
	        var chart = Morris.Line({
	            element: 'MenLastWeekChart',
	  		  	data: [{"fecha":"0","valor":0},{"fecha":"0","valor":0}], //data("reachdata?value=mencionadores"),
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valor'],
	  		  	labels: ['Mencionadores'],	  
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: true,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });

	        // Fire off an AJAX request to load the data
	        chart.setData(data("reachdata?value=mencionadores"));
	            
        });
     });
	
	$('#Retbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    $( "#RetLastWeekChart" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	        // Create a Line Chart with Morris
	        var chart = Morris.Line({
	            element: 'RetLastWeekChart',
	  		  	data: [{"fecha":"0","valor":0},{"fecha":"0","valor":0}], //data("reachdata?value=mencionadores"),
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valor'],
	  		  	labels: ['Retuiteadores'],	  
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: true,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });

	        // Fire off an AJAX request to load the data
	        chart.setData(data("reachdata?value=retuiteadores"));
	            
        });
     });
	
	$('#AObannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    $( "#AOLastWeekChart" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	        // Create a Line Chart with Morris
	        var chart = Morris.Line({
	            element: 'AOLastWeekChart',
	  		  	data: [{"fecha":"0","valor":0},{"fecha":"0","valor":0}], //data("reachdata?value=mencionadores"),
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valor'],
	  		  	labels: ['Alcance Objetivo'],	  
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: true,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });

	        // Fire off an AJAX request to load the data
	        chart.setData(data("reachdata?value=aobjetivo"));
	            
        });
     });
	
	$('#Sbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    $( "#SLastWeekChart" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	        // Create a Line Chart with Morris
	        var chart = Morris.Line({
	            element: 'SLastWeekChart',
	  		  	data: [{"fecha":"0","valor":0},{"fecha":"0","valor":0}], //data("reachdata?value=mencionadores"),
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valor'],
	  		  	labels: ['Seguidores'],	  
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: true,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });

	        // Fire off an AJAX request to load the data
	        chart.setData(data("reachdata?value=seguidores"));
	            
        });
     });
	
	$('#APbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    $( "#APLastWeekChart" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	        // Create a Line Chart with Morris
	        var chart = Morris.Line({
	            element: 'APLastWeekChart',
	  		  	data: [{"fecha":"0","valor":0},{"fecha":"0","valor":0}], //data("reachdata?value=mencionadores"),
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valor'],
	  		  	labels: ['Alcance Posible'],	  
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: true,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });

	        // Fire off an AJAX request to load the data
	        chart.setData(data("reachdata?value=aposible"));
	            
        });
     });
	
	$('#APotbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    $( "#APotLastWeekChart" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	        // Create a Line Chart with Morris
	        var chart = Morris.Line({
	            element: 'APotLastWeekChart',
	  		  	data: [{"fecha":"0","valor":0},{"fecha":"0","valor":0}], //data("reachdata?value=mencionadores"),
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valor'],
	  		  	labels: ['Alcance Potencial'],	  
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: true,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });

	        // Fire off an AJAX request to load the data
	        chart.setData(data("reachdata?value=apotencial"));
	            
        });
     });
	
	$('#Mensbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    $( "#MensLastWeekChart" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	        // Create a Line Chart with Morris
	        var chart = Morris.Line({
	            element: 'MensLastWeekChart',
	  		  	data: [{"fecha":"0","valor":0},{"fecha":"0","valor":0}], 
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valor'],
	  		  	labels: ['Menciones'],	  
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: true,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });

	        // Fire off an AJAX request to load the data
	        chart.setData(data("engagementdata?value=menciones"));
	            
        });
     });
	
	$('#Retubannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    $( "#RetuLastWeekChart" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	        // Create a Line Chart with Morris
	        var chart = Morris.Line({
	            element: 'RetuLastWeekChart',
	  		  	data: [{"fecha":"0","valor":0},{"fecha":"0","valor":0}], 
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valor'],
	  		  	labels: ['Retuits'],	  
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: true,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });

	        // Fire off an AJAX request to load the data
	        chart.setData(data("engagementdata?value=retuits"));
	            
        });
     });
	
	$('#Favbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    $( "#FavLastWeekChart" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	        // Create a Line Chart with Morris
	        var chart = Morris.Line({
	            element: 'FavLastWeekChart',
	  		  	data: [{"fecha":"0","valor":0},{"fecha":"0","valor":0}], 
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valor'],
	  		  	labels: ['Favoritos'],	  
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: true,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });

	        // Fire off an AJAX request to load the data
	        chart.setData(data("engagementdata?value=favoritos"));
	            
        });
     });
	
	$('#Repbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    $( "#RepLastWeekChart" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	        // Create a Line Chart with Morris
	        var chart = Morris.Line({
	            element: 'RepLastWeekChart',
	  		  	data: [{"fecha":"0","valor":0},{"fecha":"0","valor":0}], 
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valor'],
	  		  	labels: ['Respuestas'],	  
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: true,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });

	        // Fire off an AJAX request to load the data
	        chart.setData(data("engagementdata?value=respuestas"));
	            
        });
     });
	
	$('#Infbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    	
	    	var scrTwitter="<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document,'script','twitter-wjs');</script><script async src='//platform.twitter.com/widgets.js' charset='utf-8'></script>";
		    var mdata="<div class='row'>";
		    var aux="<div class='col-sm-4' style='font-weight: bold;'>"+"Usuario"+"</div>"+"<div class='col-sm-4' style='font-weight: bold;'>"+"NºSeguidores"+"</div>"+"<div class='col-sm-4' style='font-weight: bold;'>"+"TimeLine"+"</div>";
		    var img = new Image();
		    
	        // Fire off an AJAX request to load the data
	        var json = data("criteriondata?value=influencers");
	        
	        //read result data and create html
	        for(var i = 0; i < json.length; i++) {
	            var obj = json[i];
	            img.src = obj.authorImage;

        		aux = aux+"<div class='col-sm-4'><img src="+img.src+"/>@"+obj.authorName+"</div>"+"<div class='col-sm-4'>"+obj.number+"</div>"+"<div class='col-sm-4'>"+scrTwitter+obj.html+"</div>";	            	
	        }
	        mdata= mdata+aux+"</div>"
	        
		    $("#Infbannerformmodal .modal-body" ).html(mdata); //Show the result list on modal
		    
        });
     });
	
	$("#Infbannerformmodal").on("hidden.bs.modal", function(){
	    $("#Infbannerformmodal .modal-body").html("");
	});
	
	$('#Respondidosbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    	
	    	var scrTwitter="<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document,'script','twitter-wjs');</script>";
		    var mdata="<div class='row'>";
		    var aux="<div class='col-sm-2' style='font-weight: bold;'>"+" "+"</div>"+"<div class='col-sm-2' style='font-weight: bold;'>"+"NºRespuestas"+"</div>"+"<div class='col-sm-8' style='font-weight: bold;'>"+"Tuit"+"</div>";
		    //var cbutton = "<button href='#Conversmodal' class='btn btn-primary btn-xs' >Conversación</button>"
		    
	        // Fire off an AJAX request to load the data
	        var json = data("criteriondata?value=replied");
	        
	        //read result data and create html
	        for(var i = 0; i < json.length; i++) {
	            var obj = json[i];
	          	var cbutton = "<a data-toggle='modal' href='#Conversmodal' class='btn btn-primary btn-xs' id='"+obj.id+"'>Conversación</a>"

        		aux = aux+"<div class='col-sm-2'>"+cbutton+"</div>"+"<div class='col-sm-1'>"+obj.number+"</div>"+"<div class='col-sm-9 entry-content'>"+scrTwitter+obj.html+"</div>";	            	
	        }
	        mdata= mdata+aux+"</div>"
	        
		    $("#Respondidosbannerformmodal .modal-body" ).html(mdata); //Show the result list on modal
		    
        });
     });
	
	$("#Respondidosbannerformmodal").on("hidden.bs.modal", function(){
	    $("#Respondidosbannerformmodal .modal-body").html("");
	});
	
	$('#Retuiteadosbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    	
	    	var scrTwitter="<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document,'script','twitter-wjs');</script>";
		    var mdata="<div class='row'>";
		    var aux="<div class='col-sm-2' style='font-weight: bold;'>"+" "+"</div>"+"<div class='col-sm-2' style='font-weight: bold;'>"+"NºRetuits"+"</div>"+"<div class='col-sm-8' style='font-weight: bold;'>"+"Tuit"+"</div>";
		    //var cbutton = "<button href='#Conversmodal' class='btn btn-primary btn-xs' >Conversación</button>"
		    
	        // Fire off an AJAX request to load the data
	        var json = data("criteriondata?value=retweeted");
	        
	        //read result data and create html
	        for(var i = 0; i < json.length; i++) {
	            var obj = json[i];
	          	var cbutton = "<a data-toggle='modal' href='#Conversmodal' class='btn btn-primary btn-xs' id='"+obj.id+"'>Conversación</a>"

        		aux = aux+"<div class='col-sm-2'>"+cbutton+"</div>"+"<div class='col-sm-1'>"+obj.number+"</div>"+"<div class='col-sm-9 entry-content'>"+scrTwitter+obj.html+"</div>";	            	
	        }
	        mdata= mdata+aux+"</div>"
	        
		    $("#Retuiteadosbannerformmodal .modal-body" ).html(mdata); //Show the result list on modal
		    
        });
     });
	
	$("#Retuiteadosbannerformmodal").on("hidden.bs.modal", function(){
	    $("#Retuiteadosbannerformmodal .modal-body").html("");
	});
	
	$('#Mencionesbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {	    	
	    	var scrTwitter="<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document,'script','twitter-wjs');</script><script async src='//platform.twitter.com/widgets.js' charset='utf-8'></script>";
		    var mdata="<div class='row'>";
		    var aux="<div class='col-sm-2' style='font-weight: bold;'>"+" "+"</div>"+"<div class='col-sm-2' style='font-weight: bold;'>"+"NºRetuits"+"</div>"+"<div class='col-sm-8' style='font-weight: bold;'>"+"Tuit"+"</div>";
		    
	        // Fire off an AJAX request to load the data
	        var json = data("criteriondata?value=mentioned");
	        
	        //read result data and create html
	        for(var i = 0; i < json.length; i++) {
	        	var obj = json[i];
	        	var cbutton = "<a data-toggle='modal' href='#Conversmodal' class='btn btn-primary btn-xs' id='"+obj.id+"'>Conversación</a>"

        		aux = aux+"<div class='col-sm-2'>"+cbutton+"</div>"+"<div class='col-sm-1'>"+obj.number+"</div>"+"<div class='col-sm-9 entry-content'>"+scrTwitter+obj.html+"</div>";	            	
	        }
	        mdata= mdata+aux+"</div>"
	        
		    $("#Mencionesbannerformmodal .modal-body" ).html(mdata); //Show the result list on modal
		    
        });
     });
	
	$("#Mencionesbannerformmodal").on("hidden.bs.modal", function(){
	    $("#Mencionesbannerformmodal .modal-body").html("");
	});
	
	
	$('#Conversmodal').on('shown.bs.modal', function (event) { //listen for user to open modal
	    $(function () {	
	    	$("#Conversmodal .modal-body" ).html("");
	    	
	    	var scrTwitter="<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document,'script','twitter-wjs');</script><script async src='//platform.twitter.com/widgets.js' charset='utf-8'></script>";
		    var mdata="<div class='row'>";
		    
		    console.log("tweetthread id ="+$(event.relatedTarget).attr('id'));
	        // Fire off an AJAX request to load the data
	        var json = data("tweetthread?id="+$(event.relatedTarget).attr('id'));
	        
//	        //read result data and create html
//	        for(var i = 0; i < json.length; i++) {
//	            var obj = json[i];
//
//	            mdata = mdata+"<div class='col-sm-4'>"+obj.userName+"</div>"+":"+"<div class='col-sm-4'>"+obj.text+"</div>";	            	
//	        }
//	        mdata= mdata+"</div>"
	        
		    $("#Conversmodal .modal-body" ).html(to_ul(json)); //Show the result list on modal
		    
        });
     });
	
	$("#Conversmodal").on("hidden.bs.modal", function(){
	    $("#Conversmodal .modal-body").html("");
	});
	
	function to_ul (obj) {
		  // --------v create an <ul> element
		  var f, li,div,divIn,divAu,divCon,divAc,image, ul = document.createElement ("ul");
		  ul.style.listStyleType = "none";
		  ul.style.paddingBottom = "5%";
		  // --v loop through its children
		  if(obj.folder){
			  for (f = 0; f < obj.folder.length; f++) {
			    li = document.createElement ("li");
			    div = document.createElement ("div");
			    div.className = "post";	
			    image = document.createElement ("img");
			    image.className = "avatar";
			    if(obj.folder[f].rawJson.extended_entities){			  
			    	image.src = JSON.parse(obj.folder[f].rawJson).extended_entities.user.profile_image_url_https;
			    }else{			    	
			    	image.src = JSON.parse(obj.folder[f].rawJson).user.profile_image_url_https;
			    }
			    div.appendChild(image);
			    divIn = document.createElement ("div");
			    divIn.className = "postdetails";
			    divAu = document.createElement ("div");
			    divAu.className = "author";
			    var createA = document.createElement('a');
		        var createAText = document.createTextNode("@"+obj.folder[f].userName);
		        createA.setAttribute('href', "http://twitter.com/"+obj.folder[f].userName); 
		        createA.setAttribute('target',"_blank");
		        createA.appendChild(createAText);
		        divAu.appendChild(createA);
			    divCon = document.createElement ("div");
			    divCon.className = "content";	
			    var createCText = document.createTextNode(obj.folder[f].text);
			    divCon.appendChild(createCText);
			    divAc = document.createElement ("div");
			    divAc.className = "actions";
			    var createAc = document.createElement('a');
		        var createAcText = document.createTextNode("ver en Tuiter");
		        createAc.setAttribute('href', "http://twitter.com/"+obj.folder[f].userName+"/statuses/"+obj.folder[f].tweetID); 
		        createAc.setAttribute('target',"_blank");
		        createAc.appendChild(createAcText);
		        divAc.appendChild(createAc);
			    divIn.appendChild(divAu);
			    divIn.appendChild(divCon);
			    divIn.appendChild(divAc);
			    div.appendChild(divIn);				    
			    li.appendChild (div);//document.createTextNode ("@"+obj.folder[f].userName+"   text:"+obj.folder[f].text));
			    //alert("li="+li);
			    // if the child has a 'folder' prop on its own, call me again
			    if (obj.folder[f].folder) {
			      //alert("obj.folder[f].folder="+JSON.stringify(obj.folder[f].folder));
			      var aux = to_ul (obj.folder[f])
			      //alert("aux="+aux);
			      if(aux){
			    	  //alert("añade aux");
			    	  li.appendChild (aux);
			      }
			    }
			    ul.appendChild (li);
			  }			  
		  }else{
			  if(obj.userName){
				  li = document.createElement ("li");
				  li.appendChild (document.createTextNode ("User:"+obj.folder[f].userName+"   text:"+obj.folder[f].text));
				  ul.appendChild (li);
			  }
		  }
		  return ul;  
	}
	
	function formatDate(date){
		var dd = date.getDate();
	    var mm = date.getMonth()+1; //January is 0!

	    var yyyy = date.getFullYear();
	    if(dd<10){
	        dd='0'+dd
	    } 
	    if(mm<10){
	        mm='0'+mm
	    } 
	    var today = yyyy+'-'+mm+'-'+dd;	
	    return today;
	}
});
