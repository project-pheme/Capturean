//Volume Area and Sentiment Line Chart
$(document).ready(function() {
	
	var endDate;
	var startDate;
	var frecuency;
	var idS=[];
	var clickedALLV=false;
	var clickedALLS=false;
	var partNames=[];
	var yKeysVol=[];
	var yKeysSent=[];
	var colors=[];
	var isBrands;
	var isPartiesG;
	
	var numIds= document.getElementById('numIds').getAttribute('data-value');
	console.log("numIds="+numIds);
	
	if(document.getElementById('isBrands'))
		isBrands = document.getElementById('isBrands').getAttribute('data-value');
	console.log("isBrands="+isBrands);
	
	if(document.getElementById('isPartiesG'))
		isPartiesG = document.getElementById('isPartiesG').getAttribute('data-value');
	console.log("isPartiesG="+isPartiesG);
	
	//Party names
	for(var i=0; document.getElementById('partName'+i)!= null;i++){
		var aux = document.getElementById('partName'+i).getAttribute('data-value');
		console.log("Participante" + i + " = " + aux);	
		partNames[i]=aux;
		yKeysVol[i]="volume"+i;  //PRUEBA ANA
		yKeysSent[i]="sentiment"+i;
	}
	console.log("partNames="+partNames);
	
	//Party colors
	for(var i=0; document.getElementById('partCol'+i)!= null;i++){
		var aux = document.getElementById('partCol'+i).getAttribute('data-value');
		console.log("color" + i + " = " + aux);	
		colors[i]=aux;		
	}
	console.log("colors="+colors);
	
	//INICIO: On Load
	for(var i=0; document.getElementById('idsSel'+i)!= null;i++){
		var aux = document.getElementById('idsSel'+i).getAttribute('data-value');
		console.log("ids Selected" + i + " = " + aux);	
		//idV[i]=aux;		
		idS[i]=aux;
		//yKeysVol[i]="volume"+i; //PRUEBA ANA
	}
	//Per=seconds15, minutes5, minutes15, minutes30, hourly, daily
	
//PRUEBA
	// generate a string that only has the time portion
	//var tz = jstz.determine(); // Determines the time zone of the browser client
	//var date = moment("2015-05-25T08:05:00").utc().format("ddd MMM DD YYYY HH:MM:SS Z");
	if(isBrands=="true"){
		var m = moment().subtract(1,'days');	
		endDate = m.format("ddd MMM DD YYYY 23:59:59");
	    var ini = m - (1000 * 3600 * 24 * 7);
	    startDate = moment(ini).format("ddd MMM DD YYYY 00:00:00");
	    frecuency="daily";	    
	    
	    $("#sfromDate").children("input").val(moment(ini).format("YYYY-MM-DD 00:00:00"));
	    $("#stoDate").children("input").val(m.format("YYYY-MM-DD 23:59:59"));
	    
//		endDate = moment("2015-10-18T08:05:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
//		startDate = moment("2015-10-11T08:05:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
//		frecuency="daily";
	}
	else if(isPartiesG=="true"){
		endDate = moment("2015-12-31T23:59:59").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		startDate = moment("2015-12-09T00:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");	
		
		$("#sfromDate").children("input").val(moment("2015-12-09T00:00:00").format("YYYY-MM-DD 00:00:00"));
	    $("#stoDate").children("input").val(moment("2015-12-31T23:59:59").format("YYYY-MM-DD 23:59:59"));
		
		frecuency="daily";
	} 
	else{
		endDate = moment("2015-05-25T08:05:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		startDate = moment("2015-05-24T08:05:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		
		$("#sfromDate").children("input").val(moment("2015-05-25T08:05:00").format("YYYY-MM-DD 00:00:00"));
	    $("#stoDate").children("input").val(moment("2015-05-24T08:05:00").format("YYYY-MM-DD 23:59:59"));
		
		frecuency="hourly";
	}
	// extract out the site's timezone identifier (DateWithTimezone.getTimezone() stores the site's timezone)
	//var timezone = moment().tz(tz.name()).format('z');
//alert("timezone="+timezone);	
	// create a moment in the site's timezone and use it to initialize a <span style="font-family: 'courier new', courier;">DateWithTimezone</span>

//alert("date="+date);
	
	
	
//var now = new Date("Mon May 25 2015 08:05:00 GMT+0200");//PRUEBA => Quito "new Date;" y pruebo con un valor fijo //Date.now();
//var ini = new Date("Sun May 24 2015 08:05:00 GMT+0200");//PRUEBA => Quito "now - (1000 * 3600 * 24);" y pruebo con un valor fijo //24=> 1 day 
//var date = new Date(ini);
//console.log("ON LOAD: Last hours: now="+now+" ini="+date.toGMTString());
//console.log("ON LOAD:idS="+idS);
//endDate=now.toGMTString();
//startDate=date.toGMTString();

//FIN PRUEBA
	
	//frecuency="hourly";
	callVolumeUpdate(startDate,endDate,frecuency);//1800); //sizeSec=1800(30 mins)
	callSentimentUpdate(startDate,endDate,frecuency);//1800);
	
	//Zoom 1 => Last hour with sizeSec=300(5 mins)
//	$('#slasthour').on('click', function (e) {		
//		var now = Date.now();
//		var ini = now - (1000 * 3600); // 1 hour in millisecs
//		console.log("Last hour: now="+new Date(now).toISOString()+" ini="+new Date(ini).toISOString());		
//		callVolumeUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),300);
//		callSentimentUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),300);
//	});
//	
//	//Zoom 2 => Last hours. Last 12 hours with sizeSec=1800(30 mins)
//	$('#slasthours').on('click', function (e) {
//		var now = Date.now();
//		var ini = now - (1000 * 3600 * 12); // 12 hour in millisecs
//		console.log("Last hours: now="+new Date(now).toISOString()+" ini="+new Date(ini).toISOString());
//		callVolumeUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),1800);
//		callSentimentUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),1800);
//	});
//	
//	//Zoom 3 => Last weeks. Last 3 weeks with sizeSec=14400 (4 hours)
//	$('#slastweeks').on('click', function (e) {
//		var now = Date.now();
//		var ini = now - (1000 * 3600 * 24 * 7 * 3); //24=> 1 day, 7 => seven days= 1 week, 3 => 3 weeks 
//		console.log("Last hours: now="+now+" ini="+ini);
//		callVolumeUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),14400);
//		callSentimentUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),14400);
//	});
	
	//Last 24 hours with sizeSec=1800(30 mins)
	$('#slastday').on('click', function (e) {
		//CLEAN DATES
		$('#sfromDate').find('input:text').val('');
		$('#stoDate').find('input:text').val('');
		
		var now = moment().subtract(1,'days');//new Date;//Date.now();
		var ini = now - (1000 * 3600 * 24); // 24 hours in millisecs
		var date= moment(ini);//new Date(ini);
		console.log("Last hours: now="+now.utc().format("ddd MMM DD YYYY HH:mm:ss z")+" ini="+date.utc().format("ddd MMM DD YYYY HH:mm:ss z"));
		endDate=now.utc().format("ddd MMM DD YYYY 23:59:59 z");//toGMTString();
		startDate=date.utc().format("ddd MMM DD YYYY 00:00:00 z");//toGMTString();
		frecuency="hourly";
		
		$("#sfromDate").children("input").val(date.utc().format("YYYY-MM-DD 00:00:00"));
	    $("#stoDate").children("input").val(now.utc().format("YYYY-MM-DD 23:59:59"));
		
		
		callVolumeUpdate(startDate,endDate,frecuency);//1800);
		callSentimentUpdate(startDate,endDate,frecuency);//1800);
	});
	
	//Last 4 weeks with sizeSec=86400 (24 hours=86400000 millisecs)
	$('#slastmonth').on('click', function (e) {
		//CLEAN DATES
		$('#sfromDate').find('input:text').val('');
		$('#stoDate').find('input:text').val('');
		
		var now = moment().subtract(1,'days'); //new Date;//Date.now();
		var ini = now - (1000 * 3600 * 24 * 7 * 4); //24=> 1 day, 7 => seven days= 1 week, 4 => 4 weeks 
		var date= moment(ini); //new Date(ini);
		console.log("Last hours: now="+now.utc().format("ddd MMM DD YYYY HH:mm:ss z")+" ini="+date.utc().format("ddd MMM DD YYYY HH:mm:ss z"));
		endDate=now.utc().format("ddd MMM DD YYYY 23:59:59 z");//.toGMTString();
		startDate=date.utc().format("ddd MMM DD YYYY 00:00:00 z");//.toGMTString();
		frecuency="daily";
		
		$("#sfromDate").children("input").val(date.utc().format("YYYY-MM-DD 00:00:00"));
	    $("#stoDate").children("input").val(now.utc().format("YYYY-MM-DD 23:59:59"));
		
		callVolumeUpdate(startDate,endDate,frecuency);//86400);
		callSentimentUpdate(startDate,endDate,frecuency);//86400);
	});
	
	function callVolumeUpdate(fIni, fFin,sec){
		//if((idV.length>0)&&(sec!=null)&&(sec!=undefined)){				
		if((idS.length>0)&&(sec!=null)&&(sec!=undefined)){
			  var iD="";
			  for (var i=0; i < idS.length; i++) {				 
				  if(""==iD)
					  iD=idS[i];
				  else
					  iD=iD+','+idS[i]; 
			  }
			  console.log("iD(V)="+iD);			  
			  //var call = 'http://localhost:8080/volumedata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec;
			  //var call = url+'volumedata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec;
			  var call = 'volumedata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec+'&numIds='+numIds;
			  console.log(call);
			  $("#loading-indicator").show();
			  setTimeout(function(){graphVol.setData(data(call,"v"))},1000); //Delay necessary to show the loading-indicator			  
			  //graph.setData(data(call));
		}else{
			BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_WARNING,
	            title: 'Warning!!',
	            message: 'Please select a party first'		            	
	        });
			graphVol.setData([ {
			  date: 0,
			  volume : 0
			}]);
		}
	}

	function callSentimentUpdate(fIni, fFin,sec){
		console.log("callSentimentUpdate");
		if((idS.length>0)&&(sec!=null)&&(sec!=undefined)){			  
			  var iD="";
			  for (var i=0; i < idS.length; i++) {
				  if(""==iD)
					  iD=idS[i];
				  else
					  iD=iD+','+idS[i]; 
			  }
			  console.log("iDS="+iD);			  
			  //var call = 'http://localhost:8080/volumedata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec;
			  //var call = url+'sentimentdata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec;
			  var call = 'sentimentdata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec;
			  console.log(call);
			  $("#loading-indicator").show();			  
			  setTimeout(function(){graphSent.setData(data(call,"s"))},1000); //Delay necessary to show the loading-indicator			  
			  //graph.setData(data(call));
		}else{
			BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_WARNING,
	            title: 'Warning!!',
	            message: 'Please select a party first'		            	
	        });
			
			graphSent.setData([ {
				  date: 0,
				  sentiment : 0
			}]);

		}
	}
	
//	$('#ALLV').on('ifClicked',function(event) {
//		clickedALLV=true;
//		console.log("clickedALLV true");
//	});

	$('#ALLS').on('ifClicked',function(event) {
		clickedALLS=true;
		console.log("clickedALLS true");
	});

	//If ALL is checked/unchecked, check/uncheck all 
//	$('#ALLV').on('ifChanged',function() {
//		if(clickedALLV){
//			checkboxes = document.getElementsByName('participantV');
//			console.log("checkboxes="+checkboxes.length);
//			for(var i=0, n=checkboxes.length;i<n;i++) {
//				if($('#ALLV').is(':checked')){
//					//checkboxes[i].checked = true;	
//					$("input[type='checkbox'][name='participantV']").iCheck('check');					
//					var value=checkboxes[i].value; // add checkbox id to id's array
//					var index = idV.indexOf(value);
//					if (index <= -1) { // if it is not, add it
//			       		idV[idV.length]=value;
//			       	}
//				}else{
//					//checkboxes[i].checked = false;
//					$("input[type='checkbox'][name='participantV']").iCheck('uncheck');
//					var index = idV.indexOf(checkboxes[i].value); //remove checkbox id from id's array
//					if (index > -1) {
//						idV.splice(index, 1);
//					}
//				}
//			}		
//			console.log("IDS(V):");
//			for(var i=0, n=idV.length;i<n;i++) {
//				console.log("Id="+idV[i]);
//			}
//			console.log("IDS(S):");
//			for(var i=0, n=idS.length;i<n;i++) {
//				console.log("Id="+idS[i]);
//			}
//			updateVolume();
//		}
//	});
	
	//If ALL is checked/unchecked, check/uncheck all 
	$('#ALLS').on('ifChanged',function() {
		if(clickedALLS){
			checkboxes = document.getElementsByName('participantS');
			console.log("checkboxes="+checkboxes.length);
			for(var i=0, n=checkboxes.length;i<n;i++) {
				if($('#ALLS').is(':checked')){
					//checkboxes[i].checked = true;	
					$("input[type='checkbox'][name='participantS']").iCheck('check'); 
					var value=checkboxes[i].value; // add checkbox id to id's array
					var index = idS.indexOf(value);
					if (index <= -1) { // if it is not, add it
			       		idS[idS.length]=value;
			       	}
				}else{
					//checkboxes[i].checked = false;
					$("input[type='checkbox'][name='participantS']").iCheck('uncheck');
					var index = idS.indexOf(checkboxes[i].value); //remove checkbox id from id's array
					if (index > -1) {
						idS.splice(index, 1);
					}
				}
			}		
			console.log("IDS(S):");
			for(var i=0, n=idS.length;i<n;i++) {
				console.log("Id="+idS[i]);
			}
//			console.log("IDS(V):");
//			for(var i=0, n=idV.length;i<n;i++) {
//				console.log("Id="+idV[i]);
//			}
			//updateVolume();
//			updateSentiment();
		}
	});

	
	$("input[type='checkbox'][name='participantS']").on('ifClicked',function() {
		console.log("Entra en input not ALL para Sentiment!");
		clickedALLS=false;
		if($("input[type='checkbox'][name='isALLS']").iCheck('check')){
			$("input[type='checkbox'][name='isALLS']").iCheck('uncheck');
		}
		
		var selected = $(this).val();
		var index = idS.indexOf(selected); // If selected is on list of selecteds ids
        if($(this).iCheck('check')){ //is(':checked')){
        	if (index <= -1) { // if it is not, add it
        		idS[idS.length]=selected;
        	}
        }
        if($(this).iCheck('uncheck')){//else{
        	if (index > -1) { // if it is, remove it
        		idS.splice(index, 1);
			}
        }
      
        console.log("IDS(S):");
		for(var i=0, n=idS.length;i<n;i++) {
			console.log("Id(S)="+idS[i]);
		}
//        console.log("IDS(V):");
//		for(var i=0, n=idV.length;i<n;i++) {
//			console.log("Id(V)="+idV[i]);
//		}
		//updateVolume();
		//updateSentiment();
	});
	
//	function parseDate(input) {
//		  var parts = input.match(/(\d+)/g);		  
//		  return new Date(parts[0], parts[1]-1, parts[2]); // months are 0-based
//	}
	
	//Get frequency selected
	$(document).on('click', '.dropdown-menu li a', function () {
	   console.log("Entra aqui="+$(this).text());
	   if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')){
		   console.log("END_DATE="+endDate);
		   console.log("START_DATE="+startDate);		   
		   //var timeDiff = Math.abs((new Date(endDate)).getTime() - (new Date(startDate)).getTime());
		   var timeDiff = Math.abs(moment(endDate).diff(moment(startDate)));
		   console.log("TIME_DIFF="+timeDiff);
		   var diffHours = timeDiff / (1000 * 3600);
		   console.log(diffHours);
		   var diffDays = timeDiff / (1000 * 3600 * 24);
		   console.log(diffDays);
		   var diffWeeks = timeDiff / (1000 * 3600 * 24 * 7);
		   console.log(diffWeeks);
		   var diffMonths = timeDiff / (1000 * 3600 * 24 * 30);
		   console.log(diffMonths);
		   var diffQuarts = timeDiff / (1000 * 3600 * 24 * 30 * 3);
		   console.log(diffQuarts);
		   var diffYears = timeDiff / (1000 * 3600 * 24 * 365);
		   console.log(diffYears);
	   
		   switch (true) {
		    case /Hourly/.test($(this).text())://Hourly
		    	//$(".btn:first-child").text($(this).text());//Show selected
		        $(".btn:first-child").val($(this).text());
		        if(diffHours >= 1){
		        	frecuency = "hourly";//"86400"; //secs in one day
		        }else{
		        	BootstrapDialog.alert({
						type: BootstrapDialog.TYPE_WARNING,
			            title: 'Warning!!Option not avaliable',
			            message: 'The difference between Start Date and End Date is less than one hour'		            	
			        });
		        	$('#fromDate').find('input:text').val('');
					$('#toDate').find('input:text').val('');
					$(".btn:first-child").text("Frecuency"); // Initial value
					$(".btn:first-child").val("");
		        }
		        break;
		    case /Daily/.test($(this).text())://Daily
		    	//$(".btn:first-child").text($(this).text());//Show selected
		        $(".btn:first-child").val($(this).text());
		        if(diffDays >= 1){
		        	frecuency = "daily"; //"86400"; //secs in one day
		        }else{
		        	BootstrapDialog.alert({
						type: BootstrapDialog.TYPE_WARNING,
			            title: 'Warning!!Option not avaliable',
			            message: 'The difference between Start Date and End Date is less than one day'		            	
			        });
		        	$('#fromDate').find('input:text').val('');
					$('#toDate').find('input:text').val('');
					$(".btn:first-child").text("Frecuency"); // Initial value
					$(".btn:first-child").val("");
		        }
		        break;
//		    case /Weekly/.test($(this).text())://Weekly
//		    	$(".btn:first-child").text($(this).text());//Show selected
//	        	$(".btn:first-child").val($(this).text());
//	        	if(diffWeeks >= 1){
//	        		frecuency = "604800";//secs in one week
//	        	}else{
//		        	BootstrapDialog.alert({
//						type: BootstrapDialog.TYPE_WARNING,
//			            title: 'Warning!!Option not avaliable',
//			            message: 'The difference between Start Date and End Date is less than one week'
//			        });
//		        	$('#fromDate').find('input:text').val('');
//					$('#toDate').find('input:text').val('');
//					$(".btn:first-child").text("Frecuency"); // Initial value
//					$(".btn:first-child").val("");
//		        }
//		        break;
//		    case /Monthly/.test($(this).text())://Monthly
//		    	$(".btn:first-child").text($(this).text());//Show selected
//	        	$(".btn:first-child").val($(this).text());
//	        	if(diffMonths >= 1){
//	        		frecuency = "2629743,83";//secs in one month
//	        	}else{
//		        	BootstrapDialog.alert({
//						type: BootstrapDialog.TYPE_WARNING,
//			            title: 'Warning!!Option not avaliable',
//			            message: 'The difference between Start Date and End Date is less than one month'
//			        });
//		        	$('#fromDate').find('input:text').val('');
//					$('#toDate').find('input:text').val('');
//					$(".btn:first-child").text("Frecuency"); // Initial value
//					$(".btn:first-child").val("");
//		        }
//		        break;
//		    case /Quartly/.test($(this).text())://Quartly
//		    	$(".btn:first-child").text($(this).text());//Show selected
//	        	$(".btn:first-child").val($(this).text());
//	        	if(diffQuarts >= 1){
//	        		frecuency = "7776000";//secs in one quarter=>three months
//	        	}else{
//		        	BootstrapDialog.alert({
//						type: BootstrapDialog.TYPE_WARNING,
//			            title: 'Warning!!Option not avaliable',
//			            message: 'The difference between Start Date and End Date is less than one quarter'
//			        });
//		        	$('#fromDate').find('input:text').val('');
//					$('#toDate').find('input:text').val('');
//					$(".btn:first-child").text("Frecuency"); // Initial value
//					$(".btn:first-child").val("");
//		        }
//		        break;
//		    case /Yearly/.test($(this).text())://Yearly
//		    	$(".btn:first-child").text($(this).text());//Show selected
//	        	$(".btn:first-child").val($(this).text());
//	        	if(diffYears >= 1){
//	        		frecuency = "31556926";//secs in one year
//	        	}else{
//		        	BootstrapDialog.alert({
//						type: BootstrapDialog.TYPE_WARNING,
//			            title: 'Warning!!Option not avaliable',
//			            message: 'The difference between Start Date and End Date is less than one year'
//			        });
//		        	$('#fromDate').find('input:text').val('');
//					$('#toDate').find('input:text').val('');
//					$(".btn:first-child").text("Frecuency"); // Initial value
//					$(".btn:first-child").val("");
//		        }	
//		        break;	    
			}
		   //updateVolume(); PRUEBA BOTÓN
		   //updateSentiment(); PRUEBA BOTÓN
	   }else{
		   BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_WARNING,
	            title: 'Warning!!',
	            message: 'Please, first select a start and end date!'		            	
	        });
	   }
	});	
	
	$(function () {
		  $('#ddParticipants').dropdown('toggle');
	});	
	
	if(isBrands=="true"){
		$(document).ready(function() {
	        $('#sfromDate').datetimepicker({
	            //format: 'MM/dd/yyyy HH:mm:ss',
	        	format: 'yyyy-mm-dd HH:mm:ss',
	            language: 'en',
	            startDate: startDate,//moment("2015-10-11 00:00:00"),	            
	            pick12HourFormat: true
	        });
	    });	
	
		$(document).ready(function() {
	        $('#stoDate').datetimepicker({
	            //format: 'MM/dd/yyyy HH:mm:ss',
	        	format: 'yyyy-mm-dd HH:mm:ss',
	            language: 'en',
	            startDate: endDate, //moment("2015-10-11 00:00:00"),
	            pick12HourFormat: true
	        });
	    });		
		
	}else{
		$(document).ready(function() {
	        $('#sfromDate').datetimepicker({	           
	        	format: 'yyyy-mm-dd HH:mm:ss',
	            language: 'en',	           
	            pick12HourFormat: true
	        });
	    });	
	
		$(document).ready(function() {
	        $('#stoDate').datetimepicker({	            
	        	format: 'yyyy-mm-dd HH:mm:ss',
	            language: 'en',	            
	            pick12HourFormat: true
	        });
	    });		
	}
	
	
	//$('#fromDate').datepicker()
	$('#sfromDate').datetimepicker()
	.on('changeDate', function(ev){
		if(ev.date != null && (typeof ev.date != 'undefined')){
			//ev.date.getSeconds() => put 00
			//startDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),ev.date.getHours()-2, ev.date.getMinutes(), '00'); //-2, considering it GMT+2
			//startDate=startDate.toGMTString();//toISOString(); //format	
			//Fix date, since datetimepicker takes chosen date as UTC, and modify it accordly to user timezone. We fix
			//it in a way that the selected hour is on user timezone by default
			var TimeZoned = new Date(ev.date.setTime(ev.date.getTime() + (ev.date.getTimezoneOffset() * 60000)));			
			$('#sfromDate').datetimepicker('setDate', TimeZoned);			
			startDate = moment(TimeZoned).utc().format("ddd MMM DD YYYY HH:mm:ss z");
			frecuency = null; 

			if(endDate!=null&&endDate!='undefined'){
				console.log("startDate="+startDate);
				console.log("endDate="+endDate);
				if(Date.parse(endDate)<Date.parse(startDate)){
					//alert("End Date is less than Start Date");
					BootstrapDialog.alert({
						type: BootstrapDialog.TYPE_WARNING,
			            title: 'Warning!!',
			            message: 'End Date is less than Start Date'
			        });
					$('#sfromDate').find('input:text').val('');
					$('#stoDate').find('input:text').val('');
					startDate=null;
					endDate=null;
				}
			}
		}
	});
	 
	//$("#toDate").datepicker()
	$('#stoDate').datetimepicker()
	.on("changeDate", function(ev){		
		if(ev.date != null && (typeof ev.date != 'undefined')){
			//ev.date.getSeconds() => put 00

			//endDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),ev.date.getHours()-2, ev.date.getMinutes(), '00'); //-2, considering it GMT+2
			//endDate=endDate.toGMTString();//toISOString(); //format
			//Fix date, since datetimepicker takes chosen date as UTC, and modify it accordly to user timezone. We fix
			//it in a way that the selected hour is on user timezone by default
			var TimeZoned = new Date(ev.date.setTime(ev.date.getTime() + (ev.date.getTimezoneOffset() * 60000)));
			$('#stoDate').datetimepicker('setDate', TimeZoned);
			endDate = moment(TimeZoned).utc().format("ddd MMM DD YYYY HH:mm:ss z");		
			frecuency = null; 

			if(startDate!=null&&startDate!='undefined'){
				console.log("startDate="+startDate);
				console.log("endDate="+endDate);
				if(Date.parse(endDate)<Date.parse(startDate)){
					//alert("End Date is less than Start Date");
					BootstrapDialog.alert({
						type: BootstrapDialog.TYPE_WARNING,
			            title: 'Warning!!',
			            message: 'End Date is less than Start Date'
			        });
					$("#sfromDate").find('input:text').val("");
					$('#stoDate').find('input:text').val('');
					startDate=null;
					endDate=null;
				}
			}
		}
	});
	
	//Hide when date selected
	$("#stoDate").on('changeDate', function(ev){
	    $(this).datetimepicker('hide');
	    //   if(startDate!=null&&startDate!='undefined'){
	    //   	$('#VolumeChart').html(''); //Clean chart
	    //   	_chart.setData(json.setData(startDate,));	    	
	    //   }
    	//updateVolume(); PRUEBA BOTÓN
    	//updateSentiment(); PRUEBA BOTÓN
	});
	//Hide when date selected
	$("#sfromDate").on('changeDate', function(ev){
	    $(this).datetimepicker('hide');	
	    //if(endDate!=null&&endDate!='undefined'){
	    //	$('#VolumeChart').html(''); //Clean chart
	    //	_chart.setData(json.setData());
	    //}
    	//updateVolume(); PRUEBA BOTÓN
    	//updateSentiment(); PRUEBA BOTÓN
	});	
	
	//refresh chart when clicked
	$("#refresh").on('click', function(ev){		
		updateVolume();
		updateSentiment();
	});
	
	//Erase start date 
	$("#removeSfromDate").on('click', function(ev){
		startDate=null;
	});

	//Erase start date 
	$("#removeStoDate").on('click', function(ev){
		endDate=null;
	});

	function data(url,value) {
		var json;
		console.log("En data url="+url);
		if(url!=""){
			//$('#myModal').modal('show'); //PRUEBA 
			//console.log("después del modal show");
			$.ajax({
				'type': 'GET',
				'async': false,
				'global': false,
				'url': url, 
				'dataType': "json",				
				'success': function (data) {
					console.log("success data="+data);
					if(url.indexOf("sentimentdata") > -1){ //When sentiment(the second) chart is shown
						console.log("url Contiene sentimentdata");
						$("#loading-indicator").hide();
					}					
					//json =data; //PRUEBA
					json=parseJsonDate(data); //PRUEBA
				},
				'error': function(jqXHR, textStatus, errorThrown){
					//$('#myModal').modal('hide'); //PRUEBA
					$("#loading-indicator").hide();
					BootstrapDialog.alert({
						type: BootstrapDialog.TYPE_DANGER,
			            title: 'ERROR!!',
			            message: 'Error retreiving data: '+errorThrown
			        });
					//alert("Error loading chart:"+errorThrown);
				}
			});
		}else{
			if(value="v"){
				json= [ {
					date : '0',
					volume : 0,
				}];
			}else{
				json= [ {
					date : '0',
					sentiment : 0,
				}];
			}
		}
		//alert(json); //BORRAR		
		return json;
	}
	
	//Format receiving date here(not in Servlet), to ensure that it shows users date
	function parseJsonDate(jsonData){		
		for(var i=0;i<jsonData.length;i++){
			jsonData[i].date = moment(new Date(jsonData[i].date)).format("YYYY-MM-DD HH:mm:ss");
	    }		
	    return jsonData;
	}
	
	// create the volume morris chart. <div id="VolumeAreaChart" style="height: 200px;"></div>
	var graphVol = Morris.Area({
	    element: 'VolumeAreaChart',
	    data: data(""),
	    xkey: 'date',
	    ykeys: yKeysVol,
	    labels: partNames,
	    lineColors:colors,
	    smooth: false,
	    stacked: false,//true,
        hideHover: 'auto',
	    resize: true,
	    redraw: true,
	    //fillOpacity: 0.6,
	    fillOpacity: 'auto',
	    pointSize: 0,
        behaveLikeLine: true
	});

	// create the sentiment morris chart. 
	var graphSent = Morris.Line({
	    element: 'SentimentChart',
	    data: data(""),
	    xkey: 'date',
	    ykeys: yKeysSent,
	    labels: partNames,
	    lineColors:colors,
	    smooth: false,
	    stacked: true,
        hideHover: 'auto',
	    resize: true,
	    redraw: true,
	    fillOpacity: 0.6,
	    pointSize: 0,
        behaveLikeLine: true,
	});

	// update the volumen chart with the new data.
	function updateVolume() {
	  if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')&&
				 (frecuency!=null&&frecuency!='undefined')){	
		  callVolumeUpdate(startDate,endDate,frecuency);
	  }else{
		  BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_DANGER,
	            title: 'AVISO!!',
	            message: 'Fecha de inicio, fecha de fin y/o frecuencia no han sido definidos'
	       });
	  }
	}
	// update the volumen chart with the new data.
	function updateSentiment() {
	  if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')&&
				 (frecuency!=null&&frecuency!='undefined')){	
		  callSentimentUpdate(startDate,endDate,frecuency);
	  }else{
		  BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_DANGER,
	            title: 'ERROR!!',
	            message: 'Fecha de inicio, fecha de fin y/o frecuencia no han sido definidos'
	      });
	  }
	}

//	$(window).resize(function() {
//		graphVol.redraw();
//		graphSent.redraw();
//	});
	
	//FIN
});
