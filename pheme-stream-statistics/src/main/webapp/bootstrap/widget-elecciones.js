$(document).ready(function() {

	var endDate;
	var startDate;
	var frecuency;
	var id=[];
	var clickedALL=false;
	var partNames=[];
	var partNamesLabels=['Time'];
	var partIds=[];
	var colors=[];
	var images=[];
	
	var j_dc_values = {};
	
	var vstartDate;
	var datosV = [];
	var datosS = [];
	var video = false;
	var videoID;//="GV9K7jK-BL4";
	var fixedDelay = 180000; //3 mins
	
	var circlesHeight=0;
	var circlesWidth=0;
	var circleRadius=0;
	var gageValue;	
	
	var twitterMain;
	var twitterIDs=[];
	var twitterNames=[];
	var brandID = null;
	var brandDate = null;
	
	
	// ------------------- INICIO PRIMERA CARGA -----------------
	
	//Widgets default date
	
	var dcInitDate= document.getElementById('dcInitDate').getAttribute('data-value');
	console.log("dcInitDate="+dcInitDate);
	
	var dcEndDate= document.getElementById('dcEndDate').getAttribute('data-value');
	console.log("dcEndDate="+dcEndDate);
	
	//for testing
	//dcInitDate = moment("2015-12-09T00:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");	
	//dcEndDate = moment("2015-12-31T23:59:59").utc().format("ddd MMM DD YYYY HH:mm:ss z");

	var m = moment().subtract(1,'days');	
	endDate = m.format("ddd MMM DD YYYY 23:59:59");
	
	
	if(document.getElementById('isMonitoring'))
		isMonitoring = document.getElementById('isMonitoring').getAttribute('data-value');
	console.log("isMonitoring="+isMonitoring);
	
	if(document.getElementById('isDebate'))
		isDebate = document.getElementById('isDebate').getAttribute('data-value');
	console.log("isDebate="+isDebate);
	
	if(document.getElementById('isCompetitors'))
		isCompetitors = document.getElementById('isCompetitors').getAttribute('data-value');
	console.log("isCompetitors="+isCompetitors);
	
	var elecciones = (isMonitoring == "true") ? true : false;
	var debates = (isDebate == "true") ? true : false;
	var competidores = (isCompetitors == "true") ? true : false;
	
	//for testing
	//var elecciones = true;
	//var debates = false;
	//var competidores = false;
	
	if (elecciones){
		startDate = moment(dcInitDate).utc().format("ddd MMM DD YYYY HH:mm:ss z");
		
		if (moment(dcEndDate).isBefore(endDate)){
			endDate = moment(dcEndDate).utc().format("ddd MMM DD YYYY HH:mm:ss z");
		}
		
		frecuency="daily";	
	}
	else if (debates){
		startDate = moment(dcInitDate).utc().format("ddd MMM DD YYYY HH:mm:ss z");
		
		if (moment(dcEndDate).isBefore(endDate)){
			endDate = moment(dcEndDate).utc().format("ddd MMM DD YYYY HH:mm:ss z");
		}
		
		frecuency="minutes1";	
	}
	else if (competidores){
		var ini = m - (1000 * 3600 * 24 * 7);
	    startDate = moment(ini).format("ddd MMM DD YYYY 00:00:00");
	    
	    frecuency="daily";	
	}	    
    
    $("#fromDate").children("input").val(moment(startDate).format("YYYY-MM-DD HH:mm:ss"));
    $("#toDate").children("input").val(moment(endDate).format("YYYY-MM-DD HH:mm:ss"));
        
	//Num ids
	var numIds= document.getElementById('numIds').getAttribute('data-value');
	console.log("numIds="+numIds);
	
	//Party names
	for(var i=0; document.getElementById('partName'+i)!= null;i++){
		var aux = document.getElementById('partName'+i).getAttribute('data-value');
		console.log("Participante" + i + " = " + aux);	
		partNames[i]=aux;
		partNamesLabels[i+1]=aux;
	}
	console.log("partNames="+partNames);
	
	//Party ids
	for(var i=0; document.getElementById('partId'+i)!= null;i++){
		var aux = document.getElementById('partId'+i).getAttribute('data-value');
		console.log("Participante" + i + " = " + aux);	
		partIds[i]=aux;
	}
	console.log("partIds="+partIds);
	
	//Party colors
	for(var i=0; document.getElementById('partCol'+i)!= null;i++){
		var aux = document.getElementById('partCol'+i).getAttribute('data-value');
		console.log("color" + i + " = " + aux);	
		colors[i]=aux;		
	}
	console.log("colors="+colors);
	
	//Party images
	for(var i=0; document.getElementById('partImg'+i)!= null;i++){
		var aux = document.getElementById('partImg'+i).getAttribute('data-value');
		console.log("images" + i + " = " + aux);	
		images[i]=aux;		
	}
	
	//INICIO: On Load
	for(var i=0; document.getElementById('idsSel'+i)!= null;i++){
		var aux = document.getElementById('idsSel'+i).getAttribute('data-value');
		console.log("ids Selected" + i + " = " + aux);	
		id[i]=aux;		
	}

	
	if (competidores) {
		//twitter Main account
		twitterMain = document.getElementById('partTwitterMain').getAttribute('data-value');
		console.log("twitterMain="+twitterMain);
		
		//twitter IDs
		for(var i=0; document.getElementById('partTwitterID'+i)!= null;i++){
			var aux = document.getElementById('partTwitterID'+i).getAttribute('data-value');
			console.log("twitterID" + i + " = " + aux);	
			twitterIDs[i]=aux;		
		}
		console.log("twitterIDs="+twitterIDs);
		
		//twitter Names
		for(var i=0; document.getElementById('partTwitterName'+i)!= null;i++){
			var aux = document.getElementById('partTwitterName'+i).getAttribute('data-value');
			console.log("twitterName" + i + " = " + aux);	
			twitterNames[i]=aux;		
		}
		console.log("twitterNames="+twitterNames);
	}
	
	//test
	//twitterMain = "20509689";
	//console.log("twitterMain="+twitterMain);
	//test
	//twitterIDs[0]="20509689";
	//twitterIDs[1]="50982086";
	//twitterIDs[2]="2288138575";
	//twitterIDs[3]="19028805";
	//twitterIDs[4]="14824411";
	//test
	//twitterNames[0]="PPopular";
	//twitterNames[1]="PSOE";
	//twitterNames[2]="ahorapodemos";
	//twitterNames[3]="CiudadanosCs";
	//twitterNames[4]="iunida";
	
	
	if (debates) {
		//video
		videoID = document.getElementById('idVideo').getAttribute('data-value');
		if (videoID!=null) video = true;
		vstartDate = moment(startDate);
	}
	
	//dc attr to json
	for (var i=0; i < numIds; i++) {
		var atrib = {};	
		atrib["color"] = colors[i];
		atrib["name"] = partNames[i];
		atrib["image"] = images[i];
		j_dc_values[partIds[i]] = atrib;
	}

	callVolumeUpdate(startDate,endDate,frecuency);
	callSentimentUpdate(startDate,endDate,frecuency);
	callVolumeUpdateOS(partIds,id,startDate,endDate,frecuency);
	callVolumeUpdateBAR(partIds,id,startDate,endDate,frecuency);
	
	
	// ------------------- FIN PRIMERA CARGA -----------------
	
	
	// ------------------- INICIO WIDGET OPCIONES -----------------
	
	//Last 24 hours
	$('#lastday').on('click', function (e) {
		//CLEAN DATES
		$('#fromDate').find('input:text').val('');
		$('#toDate').find('input:text').val('');
		
		var now = moment().subtract(1,'days');//new Date;//Date.now();
		var ini = now - (1000 * 3600 * 24); // 24 hours in millisecs
		var date= moment(ini);//new Date(ini);
		console.log("Last hours: now="+now.utc().format("ddd MMM DD YYYY HH:mm:ss z")+" ini="+date.utc().format("ddd MMM DD YYYY HH:mm:ss z"));
		endDate=now.utc().format("ddd MMM DD YYYY 23:59:59 z");//toGMTString();
		startDate=date.utc().format("ddd MMM DD YYYY 00:00:00 z");//toGMTString();
		frecuency="hourly";
		
		$("#fromDate").children("input").val(date.utc().format("YYYY-MM-DD 00:00:00"));
	    $("#toDate").children("input").val(now.utc().format("YYYY-MM-DD 23:59:59"));
		
		waitingDialog.show();
		setTimeout(function () {updateGeneralWidgets();}, 500);
	    
	});
	
	//Last week
	$('#lastweek').on('click', function (e) {
		//CLEAN DATES
		$('#fromDate').find('input:text').val('');
		$('#toDate').find('input:text').val('');
		
		var now = moment().subtract(1,'days'); //new Date;//Date.now();
		var ini = now - (1000 * 3600 * 24 * 7); //24=> 1 day, 7 => seven days= 1 week
		var date= moment(ini); //new Date(ini);
		console.log("Last hours: now="+now.utc().format("ddd MMM DD YYYY HH:mm:ss z")+" ini="+date.utc().format("ddd MMM DD YYYY HH:mm:ss z"));
		endDate=now.utc().format("ddd MMM DD YYYY 23:59:59 z");//.toGMTString();
		startDate=date.utc().format("ddd MMM DD YYYY 00:00:00 z");//.toGMTString();
		frecuency="daily";
		
		$("#fromDate").children("input").val(date.utc().format("YYYY-MM-DD 00:00:00"));
	    $("#toDate").children("input").val(now.utc().format("YYYY-MM-DD 23:59:59"));
		
	    waitingDialog.show();
		setTimeout(function () {updateGeneralWidgets();}, 500);
	});
	
	//Last 4 weeks
	$('#lastmonth').on('click', function (e) {
		//CLEAN DATES
		$('#fromDate').find('input:text').val('');
		$('#toDate').find('input:text').val('');
		
		var now = moment().subtract(1,'days'); //new Date;//Date.now();
		var ini = now - (1000 * 3600 * 24 * 7 * 4); //24=> 1 day, 7 => seven days= 1 week, 4 => 4 weeks 
		var date= moment(ini); //new Date(ini);
		console.log("Last hours: now="+now.utc().format("ddd MMM DD YYYY HH:mm:ss z")+" ini="+date.utc().format("ddd MMM DD YYYY HH:mm:ss z"));
		endDate=now.utc().format("ddd MMM DD YYYY 23:59:59 z");//.toGMTString();
		startDate=date.utc().format("ddd MMM DD YYYY 00:00:00 z");//.toGMTString();
		frecuency="daily";
		
		$("#fromDate").children("input").val(date.utc().format("YYYY-MM-DD 00:00:00"));
	    $("#toDate").children("input").val(now.utc().format("YYYY-MM-DD 23:59:59"));
		
	    waitingDialog.show();
		setTimeout(function () {updateGeneralWidgets();}, 500);
	});
	
	
	$('#ALL').on('ifClicked',function(event) {
		clickedALL=true;
		console.log("clickedALL true");
	});
	
	
	//If ALL is checked/unchecked, check/uncheck all 
	$('#ALL').on('ifChanged',function() {
		if(clickedALL){
			checkboxes = document.getElementsByName('participant');
			console.log("checkboxes="+checkboxes.length);
			for(var i=0, n=checkboxes.length;i<n;i++) {
				if($('#ALL').is(':checked')){
					//checkboxes[i].checked = true;	
					$("input[type='checkbox'][name='participant']").iCheck('check'); 
					var value=checkboxes[i].value; // add checkbox id to id's array
					var index = id.indexOf(value);
					if (index <= -1) { // if it is not, add it
			       		id[id.length]=value;
			       	}
				}else{
					//checkboxes[i].checked = false;
					$("input[type='checkbox'][name='participant']").iCheck('uncheck');
					var index = id.indexOf(checkboxes[i].value); //remove checkbox id from id's array
					if (index > -1) {
						id.splice(index, 1);
					}
				}
			}		
			console.log("IDS:");
			for(var i=0, n=id.length;i<n;i++) {
				console.log("Id="+id[i]);
			}
		}
	});

	
	$("input[type='checkbox'][name='participant']").on('ifClicked',function() {
		console.log("Entra en input not ALL!");
		clickedALL=false;
		if($("input[type='checkbox'][name='isALL']").iCheck('check')){
			$("input[type='checkbox'][name='isALL']").iCheck('uncheck');
		}
		
		var selected = $(this).val();
		var index = id.indexOf(selected); // If selected is on list of selecteds ids
        if($(this).iCheck('check')){ //is(':checked')){
        	if (index <= -1) { // if it is not, add it
        		id[id.length]=selected;
        	}
        }
        if($(this).iCheck('uncheck')){//else{
        	if (index > -1) { // if it is, remove it
        		id.splice(index, 1);
			}
        }
      
        console.log("IDS:");
		for(var i=0, n=id.length;i<n;i++) {
			console.log("Id="+id[i]);
		}
	});
	
	
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
	
	
	$(document).ready(function() {
        $('#fromDate').datetimepicker({
            //format: 'MM/dd/yyyy HH:mm:ss',
        	format: 'yyyy-mm-dd HH:mm:ss',
            language: 'en',
            startDate: startDate,//moment("2015-10-11 00:00:00"),	            
            pick12HourFormat: true
        });
    });	
	
	$(document).ready(function() {
        $('#toDate').datetimepicker({
            //format: 'MM/dd/yyyy HH:mm:ss',
        	format: 'yyyy-mm-dd HH:mm:ss',
            language: 'en',
            startDate: endDate, //moment("2015-10-11 00:00:00"),
            pick12HourFormat: true
        });
    });		
	
	$('#fromDate').datetimepicker()
	.on('changeDate', function(ev){
		if(ev.date != null && (typeof ev.date != 'undefined')){
			//ev.date.getSeconds() => put 00
			//startDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),ev.date.getHours()-2, ev.date.getMinutes(), '00'); //-2, considering it GMT+2
			//startDate=startDate.toGMTString();//toISOString(); //format	
			//Fix date, since datetimepicker takes chosen date as UTC, and modify it accordly to user timezone. We fix
			//it in a way that the selected hour is on user timezone by default
			var TimeZoned = new Date(ev.date.setTime(ev.date.getTime() + (ev.date.getTimezoneOffset() * 60000)));			
			$('#fromDate').datetimepicker('setDate', TimeZoned);			
			startDate = moment(TimeZoned).utc().format("ddd MMM DD YYYY HH:mm:ss z");

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
					$('#fromDate').find('input:text').val('');
					$('#toDate').find('input:text').val('');
					startDate=null;
					endDate=null;
				}
			}
		}
	});
	 
	$('#toDate').datetimepicker()
	.on("changeDate", function(ev){		
		if(ev.date != null && (typeof ev.date != 'undefined')){
			//ev.date.getSeconds() => put 00

			//endDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),ev.date.getHours()-2, ev.date.getMinutes(), '00'); //-2, considering it GMT+2
			//endDate=endDate.toGMTString();//toISOString(); //format
			//Fix date, since datetimepicker takes chosen date as UTC, and modify it accordly to user timezone. We fix
			//it in a way that the selected hour is on user timezone by default
			var TimeZoned = new Date(ev.date.setTime(ev.date.getTime() + (ev.date.getTimezoneOffset() * 60000)));
			$('#toDate').datetimepicker('setDate', TimeZoned);
			endDate = moment(TimeZoned).utc().format("ddd MMM DD YYYY HH:mm:ss z");		

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
					$("#fromDate").find('input:text').val("");
					$('#toDate').find('input:text').val('');
					startDate=null;
					endDate=null;
				}
			}
		}
	});
	
	//Hide when date selected
	$("#toDate").on('changeDate', function(ev){
	    $(this).datetimepicker('hide');
	});
	//Hide when date selected
	$("#fromDate").on('changeDate', function(ev){
	    $(this).datetimepicker('hide');	
	});	
	
	
	//refresh chart when clicked
	$("#refresh").on('click', function(ev){		
		waitingDialog.show();
		setTimeout(function () {updateGeneralWidgets();}, 500);
	});
	
	//Erase start date 
	$("#removeFromDate").on('click', function(ev){
		startDate=null;
	});

	//Erase start date 
	$("#removeToDate").on('click', function(ev){
		endDate=null;
	});
	
	
	//reload all widgets with default dates
	$("#reload").on('click', function(ev){
		if (elecciones){
			startDate = moment(dcInitDate).utc().format("ddd MMM DD YYYY HH:mm:ss z");
			
			if (moment(dcEndDate).isBefore(endDate)){
				endDate = moment(dcEndDate).utc().format("ddd MMM DD YYYY HH:mm:ss z");
			}
			
			frecuency="daily";	
		}
		else if (debates){
			startDate = moment(dcInitDate).utc().format("ddd MMM DD YYYY HH:mm:ss z");
			
			if (moment(dcEndDate).isBefore(endDate)){
				endDate = moment(dcEndDate).utc().format("ddd MMM DD YYYY HH:mm:ss z");
			}
			
			frecuency="minutes1";	
		}
		else if (competidores){
			var ini = m - (1000 * 3600 * 24 * 7);
		    startDate = moment(ini).format("ddd MMM DD YYYY 00:00:00");
		    
		    frecuency="daily";	
		}	    
	    
	    $("#fromDate").children("input").val(moment(startDate).format("YYYY-MM-DD HH:mm:ss"));
	    $("#toDate").children("input").val(moment(endDate).format("YYYY-MM-DD HH:mm:ss"));
	        
	    waitingDialog.show();
		setTimeout(function () {updateGeneralWidgets();}, 500);
	});
	
	//show-hide options
	$("#option-hide").on('click', function(ev){
		var optionsState = document.getElementById('option-hide').getAttribute('data-value');
		if (optionsState == "enabled"){
			document.getElementById('option-hide').setAttribute('data-value','disabled');
			$('#div-options').attr("style","");
			$('#div-options-showhide').fadeOut(300);
			$('#eye-button').addClass("glyphicon-eye-open").removeClass("glyphicon-eye-close");
		}
		else {
			document.getElementById('option-hide').setAttribute('data-value','enabled');
			$('#div-options').attr("style","border-style: outset;");
			$('#eye-button').addClass("glyphicon-eye-close").removeClass("glyphicon-eye-open");
			$('#div-options-showhide').fadeIn(300);
		}
	});

	
	function updateGeneralWidgets(){
		
		callVolumeUpdate(startDate,endDate,frecuency);
		callSentimentUpdate(startDate,endDate,frecuency);
		callVolumeUpdateOS(partIds,id,startDate,endDate,frecuency);
		callVolumeUpdateBAR(partIds,id,startDate,endDate,frecuency);
		
		updateVolume();
		updateSentiment();
		
		hideTagcloud();
		hideTweets();
		hideMap();
		hideVideo();
		hideBrand();
		hideBrandCompetidor();		
				
		waitingDialog.hide();
	}
	
	function getInitDatePer(myDate){
		if (frecuency == "daily"){
			return moment(myDate).format("ddd MMM DD YYYY 00:00:00 UTC");
		}
		else if (frecuency == "hourly"){
			return moment(myDate).format("ddd MMM DD YYYY HH:00:00 UTC");
		}
		/*else if (frecuency == "minutes30"){
			return moment(myDate).format("ddd MMM DD YYYY 00:00:00 UTC");
		}
		else if (frecuency == "minutes15"){
			return moment(myDate).format("ddd MMM DD YYYY 00:00:00 UTC");
		}
		else if (frecuency == "minutes5"){
			return moment(myDate).format("ddd MMM DD YYYY 00:00:00 UTC");
		}*/
		else if (frecuency == "minutes1"){
			return moment(myDate).format("ddd MMM DD YYYY HH:mm:00 UTC");
		}
		else return moment(myDate).format("ddd MMM DD YYYY HH:mm:ss UTC");
	}
	
	function getEndDatePer(myDate){
		if (frecuency == "daily"){
			return moment(myDate).format("ddd MMM DD YYYY 23:59:59 UTC");
		}
		else if (frecuency == "hourly"){
			return moment(myDate).format("ddd MMM DD YYYY HH:59:59 UTC");
		}
		/*else if (frecuency == "minutes30"){
			return moment(myDate).format("ddd MMM DD YYYY HH:59:59 UTC");
		}
		else if (frecuency == "minutes15"){
			return moment(myDate).format("ddd MMM DD YYYY HH:59:59 UTC");
		}
		else if (frecuency == "minutes5"){
			return moment(myDate).format("ddd MMM DD YYYY HH:59:59 UTC");
		}*/
		else if (frecuency == "minutes1"){
			return moment(myDate).format("ddd MMM DD YYYY HH:mm:59 UTC");
		}
		else return moment(myDate).format("ddd MMM DD YYYY HH:mm:ss UTC");
	}
	
	
	// ------------------- FIN WIDGET OPCIONES -----------------

	
	// ------------------- INICIO WIDGET VOLUMEN-SENT -----------------
	
	function clickPointAction(e, pt) {	
				
		console.log(pt);
		var allDate = new Date(pt.xval);
		console.log("date = " + allDate);
		var year = allDate.getUTCFullYear();
		var sMonth = allDate.getUTCMonth();
		var month =('0' + sMonth).slice(-2);
		var day = allDate.getUTCDate();
		var sHour = allDate.getUTCHours();
		var hour =('0' + sHour).slice(-2);
		var sMinute = allDate.getUTCMinutes();
		var minute =('0' + sMinute).slice(-2);
		console.log(year+month+day+hour+minute);
		var myDate=new Date(year,month,day,hour, minute, '00'); //-2, considering it GMT+2
        var auxDate = new Date(year,month,day,hour, minute, '00'); //to show
		console.log("startDate="+myDate+" auxDate="+auxDate);
		//$('#tagFromDate').datetimepicker('setDate', auxDate);
		var startDateTC=moment(myDate).utc().format("ddd MMM DD YYYY HH:mm:ss UTC"); //format	
		console.log("startDate(to server)="+startDateTC);
		
		var partSelected = pt.name;
		var index = partNames.indexOf(partSelected);
		var partTC=[];
		partTC.push(partIds[index]);
		
		callTagUpdate(partTC, startDateTC);
				
		var startDateOS=getInitDatePer(myDate);
		console.log("startDateOS(to server)="+startDateOS);
		var endDateOS=getEndDatePer(myDate);
		console.log("endDateOS(to server)="+endDateOS);
		callVolumeUpdateOS(partIds,id,startDateOS,endDateOS,frecuency);
		
		callVolumeUpdateBAR(partIds,id,startDateOS,endDateOS,frecuency);
		
		callTweetsUpdate(partIds[index],startDateOS,endDateOS,null,"1","10");

		callMapUpdate(partIds[index],startDateOS,endDateOS);
		
		if (debates && video) {
			callVideoUpdate(pt.xval);
		}
		if (competidores) {
			callBrandUpdate(pt);
		}
				
		waitingDialog.hide();
    }
	
	var graphVol = new Dygraph(document.getElementById("VolumeAreaChart"), datosV,
	{
		drawPoints: true,
		showRoller: false,
		colors:colors,
		//valueRange: [0.0, 1.2],
		labels: partNamesLabels,
		fillGraph: true,
		ylabel: "menciones",
		yAxisLabelWidth: 70,
		
		highlightCircleSize: 2,
		strokeWidth: 1,
		highlightSeriesOpts: {
		  strokeWidth: 3,
		  strokeBorderWidth: 1,
		  highlightCircleSize: 5
		},
		
		pointClickCallback: function(e,pt){
			waitingDialog.show();
			setTimeout(function () {clickPointAction(e, pt);}, 500);
		},
		
		zoomCallback: function(min, max, range) {
           	zoomGraphSent(min, max, range);
        },
	});

	function callVolumeUpdate(fIni, fFin,sec){
		//if((idV.length>0)&&(sec!=null)&&(sec!=undefined)){				
		if((id.length>0)&&(sec!=null)&&(sec!=undefined)){
			  var iD="";
			  for (var i=0; i < id.length; i++) {				 
				  if(""==iD)
					  iD=id[i];
				  else
					  iD=iD+','+id[i]; 
			  }
			  console.log("iD="+iD);		
			  
			  var dI = new moment(startDate);
			  var dF = new moment(endDate);			 
			  var now = new moment();
			  
			  if(dF>now){
				  dF=now;
			  }
			  
		      console.log('rrtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+frecuency+'&numIds='+numIds);		      
		      var dV=dataRT('rrtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+frecuency+'&numIds='+numIds);//Math.random();
			  
		      datosV=new Array();
		      if (dV.length == 1){
					datosV[0]=[0,0,0,0,0,0];	
		      }
		      else{
		      	for (var i=0; i<dV.length;i++){
				  dV[i][0]=new Date(dV[i][0]);
				  datosV[i]=dV[i];				  
			  	}	
		      }
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
	
	
	// update the volumen chart with the new data.
	function updateVolume() {
	  if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')){//&&
				 //(frecuency!=null&&frecuency!='undefined')){
		  clearInterval(window.intervalId);
		  graphVol.destroy();	
		  
		  graphVol = new Dygraph(document.getElementById("VolumeAreaChart"), datosV,
	    	{
				drawPoints: true,
				showRoller: false,
				colors:colors,
				//valueRange: [0.0, 1.2],
				labels: partNamesLabels,
				fillGraph: true,
				ylabel: "menciones",
				yAxisLabelWidth: 70,
				
				highlightCircleSize: 2,
				strokeWidth: 1,
				highlightSeriesOpts: {
				  strokeWidth: 3,
				  strokeBorderWidth: 1,
				  highlightCircleSize: 5
				},
				
				pointClickCallback: function(e,pt){
					waitingDialog.show();
					setTimeout(function () {clickPointAction(e, pt);}, 500);
				},
	            
	            zoomCallback: function(min, max, range) {
	            	zoomGraphSent(min, max, range);
	            },
	    	});
		  
		  callVolumeUpdate(startDate,endDate,frecuency); //frecuency);
		  graphVol.updateOptions( { 'file': datosV } ); //PRUEBA
		  
	  }else{
		  BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_DANGER,
	            title: 'AVISO!!',
	            message: 'Fecha de inicio, fecha de fin y/o frecuencia no han sido definidos'
	       });
	  }
	}
	
	
	function zoomGraphVol(min, max, range) {
    	graphVol.updateOptions({
    		dateWindow: [min, max]
    	});      
    }


	var graphSent = new Dygraph(document.getElementById("SentimentChart"), datosS,
	{
		drawPoints: true,
		showRoller: false,
		colors:colors,
		//valueRange: [0.0, 1.2],
		labels: partNamesLabels,
		ylabel: "sentimiento [-1, 1]",
		yAxisLabelWidth: 70,
		
		highlightCircleSize: 2,
		strokeWidth: 1,
		highlightSeriesOpts: {
		  strokeWidth: 3,
		  strokeBorderWidth: 1,
		  highlightCircleSize: 5
		},
		
		pointClickCallback: function(e,pt){
			waitingDialog.show();
			setTimeout(function () {clickPointAction(e, pt);}, 500);
		},
		
		zoomCallback : function(min, max, range) {
        	zoomGraphVol(min, max, range);
        },
	});

	
	function callSentimentUpdate(fIni, fFin,sec){
		//if((idV.length>0)&&(sec!=null)&&(sec!=undefined)){				
		if((id.length>0)&&(sec!=null)&&(sec!=undefined)){
			  var iD="";
			  for (var i=0; i < id.length; i++) {				 
				  if(""==iD)
					  iD=id[i];
				  else
					  iD=iD+','+id[i]; 
			  }
			  console.log("iD="+iD);		
			  
			  var dI = new moment(startDate);
			  var dF = new moment(endDate);			 
			  var now = new moment();
			  
			  if(dF>now){
				  dF=now;
			  }
			  
			  //First time
		      //console.log('rrtsentimentdata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);		      
		      //var dS=dataRT('rrtsentimentdata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);//Math.random();
		      console.log('rrtsentimentdata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+frecuency+'&numIds='+numIds);		      
		      var dS=dataRT('rrtsentimentdata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+frecuency+'&numIds='+numIds);//Math.random();
		      
		      datosS=new Array();
		      if (dS.length == 1){
					datosS[0]=[0,0,0,0,0,0];	
		      }
		      else{
		      	for (var i=0; i<dS.length;i++){
				  dS[i][0]=new Date(dS[i][0]);
				  datosS[i]=dS[i];				  
			  	}	
		      }
		}else{
			BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_WARNING,
	            title: 'Warning!!',
	            message: 'Please select a party first'		            	
	        });
			graphSent.setData([ {
				  date: 0,
				  volume : 0
			}]);
		}
	}
	
	
	// update the sentiment chart with the new data.
	function updateSentiment() {
	  if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')){//&&
				 //(frecuency!=null&&frecuency!='undefined')){
		  clearInterval(window.intervalId);

		  graphSent.destroy();	
	
		  graphSent = new Dygraph(document.getElementById("SentimentChart"), datosS,
        	{
				drawPoints: true,
				showRoller: false,
				colors:colors,
				//valueRange: [0.0, 1.2],
				labels: partNamesLabels,
				ylabel: "sentimiento [-1, 1]",
				yAxisLabelWidth: 70,
				
				highlightCircleSize: 2,
				strokeWidth: 1,
				highlightSeriesOpts: {
				  strokeWidth: 3,
				  strokeBorderWidth: 1,
				  highlightCircleSize: 5
				},
				
				pointClickCallback: function(e,pt){
					waitingDialog.show();
					setTimeout(function () {clickPointAction(e, pt);}, 500);
				},
	            
	            zoomCallback : function(min, max, range) {
	            	zoomGraphVol(min, max, range);
	            },
        	});
		  callSentimentUpdate(startDate,endDate,frecuency); //frecuency);
		  graphSent.updateOptions( { 'file': datosS } ); //PRUEBA
	  }else{
		  BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_DANGER,
	            title: 'AVISO!!',
	            message: 'Fecha de inicio, fecha de fin y/o frecuencia no han sido definidos'
	       });
	  }
	}
	
	
    function zoomGraphSent(min, max, range) {
    	graphSent.updateOptions({
    		dateWindow: [min, max]
    	});
    }
	
			
	function dataRT(url,value) {
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
					}					
					//json =data; //PRUEBA
					//json=parseJsonDate(data); //PRUEBA
					json = data.sort(order());
					for(var i=0; i<json.length ;i++){
						for(var j=0; j<partIds.length ;j++){
							if (id.indexOf(partIds[j]) > -1){}
							else {
								json[i][j+1] = "-";
								json[i][j+1] = "-";
							}
						}
					}
				},
				'error': function(jqXHR, textStatus, errorThrown){
					waitingDialog.hide();
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
	
	function order(){
		   return function(a,b){
		      if( a[0] > b[0]){
		          return 1;
		      }else if( a[0] < b[0] ){
		          return -1;
		      }
		      return 0;
		   }
	}
	
	
	$(window).resize(function() {
		graphVol.resize();
		graphSent.resize();
	});

	
	// ------------------- FIN WIDGET VOLUMEN-SENT -----------------
	
	
	
	// ------------------- INICIO WIDGET TAGCLOUD -----------------

	function updateTag(tagsSizes){
		
		$('#myCanvasContainer').empty();
		
		var jsonTags = {};
		var arrayWords = [];
		
		for(var i=0;i<tagsSizes.length;i++){
			var object = tagsSizes[i];
			for (var property in object) {
				jsonTags[property] = object[property];
				arrayWords.push(property);
			}
		}
		
		var fill = d3.scale.category20();

		  d3.layout.cloud().size([400, 400])
		      .words(arrayWords.map(function(d) {
		        return {text: d, size: jsonTags[d]*5};
		      }))
		      .rotate(function() { return ~~(Math.random() * 2) * 0; })
		      .font("Impact")
		      .fontSize(function(d) { return d.size; })
		      .on("end", draw)
		      .start();
		  
			function draw(words) {
			    d3.select("#myCanvasContainer").append("svg")
			        .attr("width", "400")
			        .attr("height", "400")
			      .append("g")
			        .attr("transform", "translate(200,200)")
			      .selectAll("text")
			        .data(words)
			      .enter().append("text")
			        .style("font-size", function(d) { return d.size + "px"; })
			        .style("font-family", "Impact")
			        .style("fill", function(d, i) { return fill(i); })
			        .attr("text-anchor", "middle")
			        .attr("transform", function(d) {
			          return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
			        })
			        .text(function(d) { return d.text; })
			        .on("click", function (d, i){
			        	showTagcloudTweetModal(d.text);
			      });
			}
						
		  /*
		var tagString;	
		console.log("tagsSizes="+tagsSizes);
		console.log("tagsSizes.length="+tagsSizes.length);
		for(var i=0;i<tagsSizes.length;i++){
			var object = tagsSizes[i];
			for (var property in object) {
				console.log('Get: ' + property + '=' + object[property]);		
				tagString+=	'<a href="#" style="font-size:'+object[property]+'ex">'+property+'</a>';
			}
		}
		//$('#tags').empty().append('<a href="#" style="font-size: 10ex">Prueba1</a><a href="#" style="font-size: 9ex">Prueba2</a><a href="#" style="font-size: 15ex">Prueba3</a>');
		$('#tags').empty().append(tagString);
		$('#myCanvas').tagcanvas({
		    textColour: '#ff0000',
		    outlineColour: '#ff00ff',
		    reverse: true,
		    depth: 0.8,
		    maxSpeed: 0.02,
		    textFont: null,
		    textColour: null,
		    weightMode:'both',
		    weight: true,
		    weightGradient: {
		     0:  '#f00', // red
		     '0.11': '#990099',
		     '0.33': '#FF3300', 
		     '0.66': '#FF0099',
		     '0.99': '#FF0000',
		     //0.33: '#ff0', // yellow
		     //0.66: '#0f0', // green
		     1:  '#00f',  // blue
		    }
		  },'tags');*/
	}
	
	function showTagcloudTweetModal(text){
		$("#TagcloudTweetmodal-text").val(text);
		$('#TagcloudTweetmodal').modal();
	}
	
	$('#TagcloudTweetmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    	$("#TagcloudTweetmodal-container").empty();
			
	    	var tagID = $("#TagcloudTweetmodal-id").val();
	    	var tagText = $("#TagcloudTweetmodal-text").val();
	    	var tagInitDate = $("#TagcloudTweetmodal-initDate").val();
	    	var tagEndDate = $("#TagcloudTweetmodal-endDate").val();
	    	
			var call = 'tweetswidget?id='+tagID+'&fInit='+moment(tagInitDate).utc().format("YYYY-MM-DDTHH:mm:ss.SSS")+'Z&fEnd='+ moment(tagEndDate).utc().format("YYYY-MM-DDTHH:mm:ss.SSS")+"Z";
			call = call +'&text='+tagText;
			$("#TagcloudTweetmodal-text").text("Tweets ("+tagText+")");

			
			var data = dataTweets(call);
			console.log("--> tweets response: ");
			console.log(data);
			
			if(typeof data !== "undefined"){
				
				var html = '<div class="table-responsive">';
				html += '<table class="table table-striped table-bordered">';
				html += '<thead><tr><th>#</th><th>tweet</th></tr></thead>';
				//html += '<thead><tr><th>#</th><th>tweet</th><th style="width: 90px;">sentimiento</th></tr></thead>';
				html += '<tbody>';
				for (var i = 0, len = data.length; i < len; ++i) {
				    html += '<tr>';
				    html += '<td>' + (i+1) + '</td>';
				    html += '<td id="TC'+data[i].tweetID+'"></td>';
				    
				    /*
				    var sentiment = parseFloat(data[i].sentiment);
				    if (sentiment < 0.0){
				    	html += '<td><span class="label label-danger">negativo</span></td>';
				    } else if (sentiment > 0.0){
				    	html += '<td><span class="label label-success">positivo</span></td>';
				    } else {
				    	html += '<td><span class="label label-warning">neutro</span></td>';
				    }
				    */
				    
				    html += "</tr>";
				}
				html += '</tbody>';
				html += '</table>';
				html += '</div>';
		
				$(html).appendTo("#TagcloudTweetmodal-container");
				
				for (var i = 0, len = data.length; i < len; ++i) {
				    twttr.widgets.createTweet(
					    data[i].tweetID,
					    document.getElementById("TC"+data[i].tweetID),
					    {
					    	align: 'center',
					    	width: '100%'
					    }
				    );
				}
			}
	
        });
     });
	
	/*
	if(!$('#myCanvas').tagcanvas({
	    //textColour: '#ff0000',
	    outlineColour: '#ff00ff',
	    reverse: true,
	    depth: 0.8,
	    maxSpeed: 0.02,
	    textFont: null,
	    textColour: null,
	    weightMode:'both',
	    weight: true,
	    weightGradient: {
	     0:  '#f00', // red
	     '0.11': '#990099',
	     '0.33': '#FF3300', 
	     '0.66': '#FF0099',
	     '0.99': '#FF0000',
	     //0.33: '#ff0', // yellow
	     //0.66: '#0f0', // green
	     1:  '#00f',  // blue
	    }
	  },'tags')) {
	    // something went wrong, hide the canvas container
	    $('#myCanvasContainer').hide();
	  }
*/
	// update the chart with the new data.
	function updateTagCloud() {
	console.log("(updateTagCloud)startDate="+startDate);		
	  if((startDate!=null&&startDate!='undefined')){	
		  callTagUpdate(id,startDate);
	  }
	}
	
	function callTagUpdate(id,date){//(date){
		
		showTagcloud();
		
		if(id.length>0){			  
			  var iD="";
			  for (var i=0; i < id.length; i++) {
				  if(""==iD)
					  iD=id[i];
				  else
					  iD=iD+','+id[i]; 
			  }
			  console.log("iD="+iD);
			  
			  $("#TagcloudTweetmodal-id").val(iD);
			  $("#TagcloudTweetmodal-initDate").val(getInitDatePer(date));
			  $("#TagcloudTweetmodal-endDate").val(getEndDatePer(date));
			  
			  //console.log("url="+url);			 
			  //var call = url+'tagdata?id='+iD+'&date='+date;
			  var myDate=moment(date).format("ddd MMM DD YYYY HH:mm:ss UTC"); //format
			  var call = 'tagdata?id='+iD+'&date='+myDate+'&per='+frecuency;//date;
			  console.log(call);
			  setTimeout(function(){updateTag(dataTag(call))},1000); //Delay necessary to show the loading-indicator
			  
			  var tcFieldText = "";
			  tcFieldText = tcFieldText + myDate;
			  tcFieldText = tcFieldText + "\n";
			  var names = "";
			  for (var i=0; i < id.length; i++) {
				  var index = partIds.indexOf(id[i]);
				  if(""==names)
					  names=partNames[index];
				  else
					  names=names+','+partNames[index];
			  }
			  tcFieldText = tcFieldText + names;
			  
			  $("#tcDatePartField").text(tcFieldText);
		}else{
			BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_WARNING,
	            title: 'Warning!!',
	            message: 'Please select a party first'		            	
	        });			
		}
	}		
	
	function dataTag(url) {
		var json;		
		if(url!=""){						
			$.ajax({
				'type': 'GET',
				'async': false,
				//'global': false,
				'url': url, 
				'dataType': "json",				
				'success': function (data) {
					json = data;
					console.log("ALP:"+json);
				},
				'error': function(jqXHR, textStatus, errorThrown){
					waitingDialog.hide();
					BootstrapDialog.alert({
						type: BootstrapDialog.TYPE_DANGER,
			            title: 'ERROR!!',
			            message: 'Error retreiving data: '+errorThrown
			        });
				}
			});
		}//else{
//			//json=[{"volume":0,"date":"2015-04-22"},{"volume":1,"date":"2015-04-05"},{"volume":2,"date":"2015-04-10"},{"volume":3,"date":"2015-04-11"},{"volume":4,"date":"2015-04-06"},{"volume":5,"date":"2015-04-18"},{"volume":6,"date":"2015-04-09"},{"volume":7,"date":"2015-04-16"},{"volume":8,"date":"2015-04-20"},{"volume":9,"date":"2015-04-04"},{"volume":10,"date":"2015-04-02"},{"volume":11,"date":"2015-04-12"},{"volume":12,"date":"2015-04-03"},{"volume":13,"date":"2015-03-31"},{"volume":14,"date":"2015-04-13"},{"volume":15,"date":"2015-04-17"},{"volume":16,"date":"2015-04-21"},{"volume":17,"date":"2015-04-14"},{"volume":18,"date":"2015-04-15"},{"volume":19,"date":"2015-04-08"},{"volume":20,"date":"2015-04-07"},{"volume":21,"date":"2015-04-19"},{"volume":22,"date":"2015-04-01"}];
//			json= [ {
//				date : '0',
//				volume : 0,
//			}];
//		}
		//alert(json); //BORRAR
		return json;
	}
	
	
	function hideTagcloud(){
		$('#div-tagcloud').addClass("hidden");
	}
	
	function showTagcloud(){
		$('#div-tagcloud').removeClass("hidden");
	}
	
	
	

	// ------------------- FIN WIDGET TAGCLOUD -----------------
	
	
// ------------------- INICIO OVERALL SENTIMENT-----------------
	
	// update the chart with the new data.
	function callVolumeUpdateOS(idsAll,idsToUPDT,startDate,endDate,frecuency){
		  
		  $("#overallDateField").text(startDate+" - "+endDate);
		  
		for(var i=0;i<idsAll.length;i++){
			if ((idsToUPDT.indexOf(idsAll[i]) > -1)){
				$("#gauge"+idsAll[i]).parent().removeClass("hidden");
			}
			else {
				$("#gauge"+idsAll[i]).parent().addClass("hidden");
			}
		}
		
		//update circles
		var num = idsToUPDT.length;
	  for(var i=0;i<idsToUPDT.length;i++){
		  //update li
		  $("#li"+idsToUPDT[i]).css("width",96/num+"%");
		  
		  
		//update Bubble
		  
		  var bubble = $("#bubble"+idsToUPDT[i]);
		  bubble.empty();
		  var h = bubble.height();
		  var w = bubble.width();
		  
		  var color = j_dc_values[idsToUPDT[i]]["color"];
		  var name = j_dc_values[idsToUPDT[i]]["name"];
		  var imageObj = new Image();
		  imageObj.src = j_dc_values[idsToUPDT[i]]["image"];
		  var value = getValue(idsAll, idsToUPDT[i], startDate, endDate, frecuency);
			 
		  var dataset = [{label : name, value: value, xPos: 0}];
		                 
		  var svg = d3.select("#bubble"+idsToUPDT[i])
			.append("svg")
			.attr("width", w/2)
			.attr("height", w/2)

			var circles = svg.selectAll("circle")
			.data(dataset)
			.enter()
			.append("circle");
			
			circles.attr("cx", function(d) {
				return w/4;
			})
			.attr("cy", function(d) {
				return w/4;
			})
			.attr("r", function(d) {
				return (w/4)/100*value;
			})
			.attr("fill", function(d) {
				return color;
			})
			;

			/* Place text in the circles. Could try replacing this with foreignObject */

			svg.selectAll("foreignObject")
			.data(dataset)
			.enter()
			.append("foreignObject")
			.attr("x", function(d, i) {
				return w/4 - (d.value/2);
			})
			.attr("y", function(d, i) {
				return w/4 - (d.value/2);
			})
			.attr("width", function(d) { return d.value; })
			.attr("height", function(d) { return d.value; })
			.append("xhtml:body")
			.attr("style","rgba(0, 0, 0, 0)")
			.append("div")
			.attr("style", function(d) { return "width: " + d.value + "; height: " + d.value; })
			.attr("class", "labelDiv")
			.html(function(d, i) { return "<p class='label' style='color:black;'>" + d.label + "</p>" + "</br>" + d.value +"%"+ "</p>"; });


			
			
 		 //update Circle
		 var name; 
		 var c=document.getElementById("circle"+idsToUPDT[i]);
		 var cxt=c.getContext("2d");
		 
		 circlesHeight=c.height;
		 circlesWidth=c.width;    
		 circleRadius = Math.min(circlesWidth, circlesHeight) * 0.5;
		
		 cxt.fillStyle = j_dc_values[idsToUPDT[i]]["color"];
		 name = j_dc_values[idsToUPDT[i]]["name"];
		 
		 //cxt.fillStyle =colorsMP[i];
		 cxt.beginPath();	
		 console.log("ANA2=>height="+circlesHeight+" width="+circleRadius+" radius="+circleRadius);
		 cxt.arc(circlesWidth/2,circlesHeight/2,circleRadius,51,Math.PI*2,true);
		 //cxt.arc(111,111,101,51,Math.PI*2,true);
		 cxt.closePath();
		 cxt.fill();
		    
		 cxt.fillStyle = "black"; // font color to write the text with    
		 cxt.font = "32px Georgia cursive";
		 
		 var imageObj = new Image();
		 imageObj.src=j_dc_values[idsToUPDT[i]]["image"];
		 cxt.drawImage(imageObj, 120-90/4 ,190-90/2);
		 
		 // Move it down by half the text height and left by half the text width
		 cxt.textBaseline = "top";
		 cxt.fillText(name,  23 ,110-90/2);//100-90/2 ,110-90/2);
		 cxt.textBaseline = "center";
		 cxt.fillText(getValue(idsAll, idsToUPDT[i], startDate, endDate, frecuency)+"%", 100-90/4 ,140-90/2);

		 //update Sentiment
		 gageValue = getSent(idsToUPDT[i], startDate, endDate, frecuency);
		 
		 $("#gauge"+idsToUPDT[i]).empty();
		 
		 var graph = new JustGage({
			    id: "gauge"+idsToUPDT[i],
			    value: gageValue,
			    //min: 0,
			    //max: 100,
			    min: -1,
			    max: 1,
			    relativeGaugeSize: true,
			    decimals:2,
			    title: "Sentimiento"
			  });
		 }
	  }

	
	function getValue(dcIdList, dcId, initDate, endDate, per){
		var call = 'volumeper?dcIdList='+dcIdList+'&dcId='+dcId+'&initDate='+initDate+'&endDate='+endDate+'&per='+per;
		return dataOS(call);
	}
	
	function getSent(dcList, initDate, endDate, per){
		var call = 'sentper?dcList='+dcList+'&initDate='+initDate+'&endDate='+endDate+'&per='+per;
		return dataOS(call);
	}
	
	function dataOS(url) {
		var json;		
		if(url!=""){						
			$.ajax({
				'type': 'GET',
				'async': false,
				//'global': false,
				'url': url, 
				'dataType': "json",				
				'success': function (data) {
					json =data;					
					//json =[{"volume":0,"volume1":1,"date":"2015-04-22"},{"volume":1,"volume1":2,"date":"2015-04-05"},{"volume":2,"volume1":3,"date":"2015-04-10"},{"volume":3,"volume1":4,"date":"2015-04-11"},{"volume":4,"volume1":5,"date":"2015-04-06"},{"volume":5,"volume1":6,"date":"2015-04-18"},{"volume":6,"volume1":7,"date":"2015-04-09"},{"volume":7,"volume1":8,"date":"2015-04-16"},{"volume":8,"volume1":9,"date":"2015-04-20"},{"volume":9,"volume1":10,"date":"2015-04-04"},{"volume":10,"volume1":11,"date":"2015-04-02"},{"volume":11,"volume1":12,"date":"2015-04-12"},{"volume":12,"volume1":13,"date":"2015-04-03"},{"volume":13,"volume1":14,"date":"2015-03-31"},{"volume":14,"volume1":15,"date":"2015-04-13"},{"volume":15,"volume1":16,"date":"2015-04-17"},{"volume":16,"volume1":17,"date":"2015-04-21"},{"volume":17,"volume1":18,"date":"2015-04-14"},{"volume":18,"volume1":19,"date":"2015-04-15"},{"volume":19,"volume1":20,"date":"2015-04-08"},{"volume":20,"volume1":25,"date":"2015-04-07"},{"volume":21,"volume1":24,"date":"2015-04-19"},{"volume":22,"volume1":23,"date":"2015-04-01"}]					
				},
				'error': function(jqXHR, textStatus, errorThrown){
					waitingDialog.hide();
					BootstrapDialog.alert({
						type: BootstrapDialog.TYPE_DANGER,
			            title: 'ERROR!!',
			            message: 'Error retreiving data: '+errorThrown
			        });
					//alert("Error loading chart:"+errorThrown);
				}
			});
		}else{
			//json=[{"volume":0,"date":"2015-04-22"},{"volume":1,"date":"2015-04-05"},{"volume":2,"date":"2015-04-10"},{"volume":3,"date":"2015-04-11"},{"volume":4,"date":"2015-04-06"},{"volume":5,"date":"2015-04-18"},{"volume":6,"date":"2015-04-09"},{"volume":7,"date":"2015-04-16"},{"volume":8,"date":"2015-04-20"},{"volume":9,"date":"2015-04-04"},{"volume":10,"date":"2015-04-02"},{"volume":11,"date":"2015-04-12"},{"volume":12,"date":"2015-04-03"},{"volume":13,"date":"2015-03-31"},{"volume":14,"date":"2015-04-13"},{"volume":15,"date":"2015-04-17"},{"volume":16,"date":"2015-04-21"},{"volume":17,"date":"2015-04-14"},{"volume":18,"date":"2015-04-15"},{"volume":19,"date":"2015-04-08"},{"volume":20,"date":"2015-04-07"},{"volume":21,"date":"2015-04-19"},{"volume":22,"date":"2015-04-01"}];
			json= [ {
				date : '0',
				volume : 0,
			}];
		}
		//alert(json); //BORRAR
		return json;
	}
	
	
	// ------------------- FIN OVERALL SENTIMENT-----------------
	
	
	// ------------------- INICIO VOLUME BAR -----------------
	
	// update the chart with the new data.
	function callVolumeUpdateBAR(idsAll,idsToUPDT,startDate,endDate,frecuency){
		  
		  $("#volumeBarDateField").text(startDate+" - "+endDate);
		  
		  var barData = [];
		  var barColors = {};
		  var barImages = []
		  
		  for(var i=0;i<idsToUPDT.length;i++){
			  
			  var barValues = { part: j_dc_values[idsToUPDT[i]]["name"] };
			  barValues["volume"] = getVolume(partIds,idsToUPDT[i], startDate, endDate, frecuency);

			  barData.push(barValues);
			  
			  barColors[j_dc_values[idsToUPDT[i]]["name"]] = j_dc_values[idsToUPDT[i]]["color"];
			  
			  barImages.push(j_dc_values[idsToUPDT[i]]["image"]);
			  
		  }	  
		  
		  $("#volumeBarChart").empty();
		  
		  var barChart = Morris.Bar({
			  element: 'volumeBarChart',
			  data: barData,
			  xkey: 'part',
			  ykeys: ["volume"],
			  labels: ["volumen"],
			  hideHover: 'always',
			  barColors: function(row, series, type){
				  return barColors[row.label];
			  }
		  });
		  
		  var barWidth = ($("#volumeBarChart").width()/3)*(0.4);
		  
		  $("#volumeBarChart").find('rect').each(function(i){
			var value = barData[i].volume.toLocaleString();
			var image = barImages[i];
			var halfHeight = parseFloat($(this).attr('height')/2);
			var newY = parseFloat($(this).attr('y')-20);
			var halfWidth = parseFloat($(this).attr('width')/2);
			var newX = parseFloat($(this).attr('x')+halfWidth);
			
			var output = '<text style="text-anchor:middle; font:12px sans-serif;" x="'+newX+'" y="'+newY+'" text-anchor="middle" font="10px &quot;Arial&quot;" stroke="none" fill="#000" font-size="12px" font-family="sans-serif" font-weight="normal" transform="matrix(1,0,0,1,0,6.875)"> <tspan dx="'+halfWidth+'" dy="'+halfHeight+'">'+value+'</tspan></text>';
			
			var div = document.createElementNS('http://www.w3.org/1999/xhtml','div');
			div.innerHTML='<svg xmlns="http://www.w3.org/2000/svg">'+output+'</svg>';
			var frag = document.createDocumentFragment();
			while (div.firstChild.firstChild)
				frag.appendChild(div.firstChild.firstChild);
			
			$("#volumeBarChart").find("svg").append(frag);

			var size = 25;
			var svgimg = document.createElementNS('http://www.w3.org/2000/svg','image');
			svgimg.setAttributeNS(null,'height',size.toString());
			svgimg.setAttributeNS(null,'width',size.toString());
			svgimg.setAttributeNS('http://www.w3.org/1999/xlink','href', image);
			svgimg.setAttributeNS(null,'x',newX+($(this).attr('width')/2)-(size/2));
			svgimg.setAttributeNS(null,'y',newY-(size/2));
			svgimg.setAttributeNS(null, 'visibility', 'visible');

			$("#volumeBarChart").find("svg").append(svgimg);

		  });
	  }

	
	function getVolume(dcAll, dcId, initDate, endDate, per){
	      
		var dV=dataRT('rrtvolumedata?id='+dcId+'&fInit='+initDate+'&fEnd='+endDate+'&sizeSec='+per+'&numIds='+dcAll.length);
		  console.log(dV);
		  
		  var total = 0;
		  var index = partIds.indexOf(dcId);
		  
		  for (var i=0; i<dV.length;i++){
			  total += dV[i][index+1];				  
		  	}	

		  return total;
	}
	
	
	// ------------------- FIN VOLUME BAR -----------------
	
	
	// ------------------- INICIO WIDGET VIDEO -----------------
	
	function hideVideo(){
		$('#div-video').addClass("hidden");
	}
	
	function showVideo(){
		$('#div-video').removeClass("hidden");
	}
	
	function callVideoUpdate(now){
		console.log("video now -> "+now);
		var src = '';
		var aux = (now-vstartDate)-fixedDelay;
		console.log("video start -> "+aux);
		
		//testing
		//videoID = "a3LNoROKAW0";
		//aux = 0;
		
		if(aux <= 0){
			aux =0;
			src = 'https://www.youtube.com/embed/'+videoID+'?start=0';
		}else{
			var value = aux/1000;
			var src = 'https://www.youtube.com/embed/'+videoID+'?start='+value;
		}
		
		$('#div-video iframe').attr('src', src);
		
		showVideo();
		
        
	}
	
	// ------------------- FIN WIDGET VIDEO -----------------

	
	
	
	
	// ------------------- INICIO WIDGET BRAND -----------------	
	
	function hideBrand(){
		$('#div-brand').addClass("hidden");
	}
	
	function showBrand(){
		$('#div-brand').removeClass("hidden");
	}
	
	function hideBrandCompetidor(){
		$('#div-brandCompetidor').addClass("hidden");
	}
	
	function showBrandCompetidor(){
		$('#div-brandCompetidor').removeClass("hidden");
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
	
	function callBrandUpdate(pt){
				
		console.log(pt);
		var partSelected = pt.name;
		var index = partNames.indexOf(partSelected);
		var partTC=[];
		partTC.push(partIds[index]);
		
		
		var partTwitterId = twitterIDs[index];
		var tIDs=[];
		tIDs.push(partTwitterId);
		
		brandID = partTwitterId;
		brandDate = pt.xval;
	
		if ( partTwitterId != twitterMain ){
			hideBrand();
			showBrandCompetidor();
			$('#div-brandCompetidor').find("#brandName").text("@"+twitterNames[index]);
			$('#div-brandCompetidor').find("#brandImage").attr('src',images[index]);
			$('#div-brandCompetidor').find("#brandDate").text(new Date(pt.xval));
			callEngagementUpdate(tIDs,pt.xval,true);
			
			$("#competitorChart-mainID").val(twitterMain);
	    	$("#competitorChart-competitorID").val(partTwitterId);
	    	$("#competitorChart-mainName").val(partNames[0]);
	    	$("#competitorChart-competitorName").val(partSelected);
	    	$("#competitorChart-mainColor").val(colors[0]);
	    	$("#competitorChart-competitorColor").val(colors[index]);
		}
		else {
			hideBrandCompetidor();
			showBrand();
			$('#div-brand').find("#brandName").text("@"+twitterNames[index]);
			$('#div-brand').find("#brandImage").attr('src',images[index]);
			$('#div-brand').find("#brandDate").text(new Date(pt.xval));
			callReachUpdate(tIDs,pt.xval);
			callEngagementUpdate(tIDs,pt.xval,false);
		}
		
	}
	
	function callReachUpdate(IDs,date){						
		if((IDs.length>0)&&(date!=null)&&(date!=undefined)){
			  var iD="";
			  for (var i=0; i < IDs.length; i++) {				 
				  if(""==iD)
					  iD=IDs[i];
				  else
					  iD=iD+','+IDs[i]; 
			  }
			  console.log("iDs="+iD);			  
			  var call = 'reachdata?ids='+iD+'&date='+date+'&value=total';
			  console.log(call);
			  var json = dataBrand(call);	
			  console.log("--> reach json: ");
			  console.log(json);
			  
			  var data = [
	//	          {label: "Followers(%)", value: followersP},
		          {label: "Mencionadores(%)", value: json.mentionersP}, //Mentioners
	//	          {label: "SubFollowers(%)", value: subFollowersP},
		          {label: "Retuiteadores(%)", value: json.retweetersP}, //Retweeters
		      ];
			  
			  $("#pie-chartReach").empty();
			  
				var graphReach = Morris.Donut({
				    element: 'pie-chartReach',
				    //data: data(""),
				    data: data,
				    resize: true,
				    redraw: true,	    
				});
			  		
			  //actualizar textos
			  var div = $('#div-brand');
			  div.find("#followersV").text(json.followersV);
			  div.find("#mentionersV").text(json.mentionersV);
			  div.find("#subFollowersV").text(json.subFollowersV);
			  div.find("#retweetersV").text(json.retweetersV);
			  div.find("#apotencialV").text(json.apotencialV);
			  div.find("#aobjetivoV").text(json.reachV);
			  div.find("#reachV").text(json.reachV);
			  
			  var upArrow = '<img src="adminlte/img/upArrow.png"/>';
			  var downArrow = '<img src="adminlte/img/downArrow.png"/>';
			  
			  if (parseFloat(json.followersI) < 0.00){
				  div.find("#followersI").html(downArrow+json.followersI);
			  } 
			  else if (parseFloat(json.followersI) > 0.00){
				  div.find("#followersI").html(upArrow+json.followersI);
			  } 
			  else {
				  div.find("#followersI").html(json.followersI);
			  }
			  
			  if (parseFloat(json.mentionersI) < 0.00){
				  div.find("#mentionersI").html(downArrow+json.mentionersI);
			  } 
			  else if (parseFloat(json.mentionersI) > 0.00){
				  div.find("#mentionersI").html(upArrow+json.mentionersI);
			  } 
			  else {
				  div.find("#mentionersI").html(json.mentionersI);
			  }
			  
			  if (parseFloat(json.subFollowersI) < 0.00){
				  div.find("#subFollowersI").html(downArrow+json.subFollowersI);
			  } 
			  else if (parseFloat(json.subFollowersI) > 0.00){
				  div.find("#subFollowersI").html(upArrow+json.subFollowersI);
			  } 
			  else {
				  div.find("#subFollowersI").html(json.subFollowersI);
			  }
			  
			  if (parseFloat(json.retweetersI) < 0.00){
				  div.find("#retweetersI").html(downArrow+json.retweetersI);
			  } 
			  else if (parseFloat(json.retweetersI) > 0.00){
				  div.find("#retweetersI").html(upArrow+json.retweetersI);
			  } 
			  else {
				  div.find("#retweetersI").html(json.retweetersI);
			  }
			  
			  if (parseFloat(json.apotencialI) < 0.00){
				  div.find("#apotencialI").html(downArrow+json.apotencialI);
			  } 
			  else if (parseFloat(json.apotencialI) > 0.00){
				  div.find("#apotencialI").html(upArrow+json.apotencialI);
			  } 
			  else {
				  div.find("#apotencialI").html(json.apotencialI);
			  }
			  
			  if (parseFloat(json.reachI) < 0.00){
				  div.find("#aobjetivoI").html(downArrow+json.reachI);
			  } 
			  else if (parseFloat(json.reachI) > 0.00){
				  div.find("#aobjetivoI").html(upArrow+json.reachI);
			  } 
			  else {
				  div.find("#aobjetivoI").html(json.reachI);
			  }
			  
			  if (parseFloat(json.reachI) < 0.00){
				  div.find("#reachI").html(downArrow+json.reachI);
			  } 
			  else if (parseFloat(json.reachI) > 0.00){
				  div.find("#reachI").html(upArrow+json.reachI);
			  } 
			  else {
				  div.find("#reachI").html(json.reachI);
			  }
			  
		}else{
			waitingDialog.hide();
			BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_WARNING,
	            title: 'Warning!!',
	            message: 'Error getting Reach data'		            	
	        });
			graphReach.setData([ {
			  date: 0,
			  volume : 0
			}]);
		}
	}
	
	function callEngagementUpdate(IDs,date,brandComp){						
		if((IDs.length>0)&&(date!=null)&&(date!=undefined)){
			  var iD="";
			  for (var i=0; i < IDs.length; i++) {				 
				  if(""==iD)
					  iD=IDs[i];
				  else
					  iD=iD+','+IDs[i]; 
			  }
			  console.log("iDs="+iD);			  
			  var call = 'engagementdata?ids='+iD+'&date='+date+'&value=total';
			  console.log(call);
			  var json = dataBrand(call);
			  console.log("--> engagement json: ");
			  console.log(json);
			  
			  var data = [
		         {label: "Menciones(%)", value: json.mentionsP}, //Mentions
		         {label: "Retuits(%)", value: json.retweetsP}, //Retweets
		         {label: "Favoritos(%)", value: json.favoritsP}, //Favorites
		         {label: "Respuestas(%)", value: json.repliesP} //Replies
		       ];

			  var div = $('#div-brand');
			  if (brandComp){
				  $("#pie-chartEngagementCompetidor").empty();

					var graphEngagementCompetidor = Morris.Donut({
					    element: 'pie-chartEngagementCompetidor',
					    //data: data(""),
					    data: data,
					    resize: true,
					    redraw: true,	   
					});

					div = $('#div-brandCompetidor');
			  }
			  else {
				  $("#pie-chartEngagement").empty();

					var graphEngagement = Morris.Donut({
					    element: 'pie-chartEngagement',
					    //data: data(""),
					    data: data,
					    resize: true,
					    redraw: true,	   
					});
					
			  }
			  
			  //actualizar campos
			  div.find("#favoritsV").text(json.favoritsV);
			  div.find("#mentionsV").text(json.mentionsV);
			  div.find("#repliesV").text(json.repliesV);
			  div.find("#retweetsV").text(json.retweetsV);
			  div.find("#engagementV").text(json.engagementV);
			  
			  var upArrow = '<img src="adminlte/img/upArrow.png"/>';
			  var downArrow = '<img src="adminlte/img/downArrow.png"/>';
			  
			  if (parseFloat(json.favoritsI) < 0.00){
				  div.find("#favoritsI").html(downArrow+json.favoritsI);
			  } 
			  else if (parseFloat(json.favoritsI) > 0.00){
				  div.find("#favoritsI").html(upArrow+json.favoritsI);
			  } 
			  else {
				  div.find("#favoritsI").html(json.favoritsI);
			  }
			  
			  if (parseFloat(json.mentionsI) < 0.00){
				  div.find("#mentionsI").html(downArrow+json.mentionsI);
			  } 
			  else if (parseFloat(json.mentionsI) > 0.00){
				  div.find("#mentionsI").html(upArrow+json.mentionsI);
			  } 
			  else {
				  div.find("#mentionsI").html(json.mentionsI);
			  }
			  
			  if (parseFloat(json.repliesI) < 0.00){
				  div.find("#repliesI").html(downArrow+json.repliesI);
			  } 
			  else if (parseFloat(json.repliesI) > 0.00){
				  div.find("#repliesI").html(upArrow+json.repliesI);
			  } 
			  else {
				  div.find("#repliesI").html(json.repliesI);
			  }
			  
			  if (parseFloat(json.retweetsI) < 0.00){
				  div.find("#retweetsI").html(downArrow+json.retweetsI);
			  } 
			  else if (parseFloat(json.retweetsI) > 0.00){
				  div.find("#retweetsI").html(upArrow+json.retweetsI);
			  } 
			  else {
				  div.find("#retweetsI").html(json.retweetsI);
			  }
			  
			  if (parseFloat(json.engagementI) < 0.00){
				  div.find("#engagementI").html(downArrow+json.engagementI);
			  } 
			  else if (parseFloat(json.engagementI) > 0.00){
				  div.find("#engagementI").html(upArrow+json.engagementI);
			  } 
			  else {
				  div.find("#engagementI").html(json.engagementI);
			  }
			  
		}else{
			waitingDialog.hide();
			BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_WARNING,
	            title: 'Warning!!',
	            message: 'Error getting Engagement data'		            	
	        });
			graphEngagement.setData([ {
			  date: 0,
			  volume : 0
			}]);
		}
	}
	
	
	function dataBrand(url) {
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
					waitingDialog.hide();
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
	//	//alert(json); //BORRAR		
		return json;
	}

	
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
	        chart.setData(dataBrand("reachdata?ids="+brandID+"&date="+brandDate+"&value=mencionadores"));
	            
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
	        chart.setData(dataBrand("reachdata?ids="+brandID+"&date="+brandDate+"&value=retuiteadores"));
	            
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
	        chart.setData(dataBrand("reachdata?ids="+brandID+"&date="+brandDate+"&value=aobjetivo"));
	            
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
	        chart.setData(dataBrand("reachdata?ids="+brandID+"&date="+brandDate+"&value=seguidores"));
	            
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
	        chart.setData(dataBrand("reachdata?ids="+brandID+"&date="+brandDate+"&value=aposible"));
	            
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
	        chart.setData(dataBrand("reachdata?ids="+brandID+"&date="+brandDate+"&value=apotencial"));
	            
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
	        chart.setData(dataBrand("engagementdata?ids="+brandID+"&date="+brandDate+"&value=menciones"));
	            
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
	        chart.setData(dataBrand("engagementdata?ids="+brandID+"&date="+brandDate+"&value=retuits"));
	            
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
	        chart.setData(dataBrand("engagementdata?ids="+brandID+"&date="+brandDate+"&value=favoritos"));
	            
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
	        chart.setData(dataBrand("engagementdata?ids="+brandID+"&date="+brandDate+"&value=respuestas"));
	            
        });
     });
	
	
//inicio competitor modals
	$('#Mensbannerformmodalcompetitor').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    	$( "#MensLastWeekChartcompetitor" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
			
	    	var mainID = $("#competitorChart-mainID").val();
	    	var competitorID = $("#competitorChart-competitorID").val();
	    	var mainName = $("#competitorChart-mainName").val();
	    	var competitorName = $("#competitorChart-competitorName").val();
	    	var mainColor = $("#competitorChart-mainColor").val();
	    	var competitorColor = $("#competitorChart-competitorColor").val();
	    	
			var jsonMain = dataBrand("engagementdata?ids="+mainID+"&date="+brandDate+"&value=menciones");
			var jsonCompetitor = dataBrand("engagementdata?ids="+competitorID+"&date="+brandDate+"&value=menciones");
			var jsonTotal = [];
			for(var i=0; i<jsonMain.length;i++) {
				var jsonParcialMain = jsonMain[i];
				var jsonParcialCompetitor = jsonCompetitor[i];
				jsonTotal.push({"fecha":jsonParcialMain.fecha, "valorA":jsonParcialMain.valor,"valorB":jsonParcialCompetitor.valor});
			}
			
	        // Create a Line Charts with Morris
			var chart = Morris.Line({
	            element: 'MensLastWeekChartcompetitor',
	  		  	data: [{"fecha":"0","valorA":0,"valorB":0},{"fecha":"0","valorA":0,"valorB":0}], 
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valorA','valorB'],
	  		  	labels: [mainName,competitorName],	
	  		  	lineColors: [mainColor,competitorColor],
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: false,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });
			
	        chart.setData(jsonTotal);
        });
     });
	
	$('#Retubannerformmodalcompetitor').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    	$( "#RetuLastWeekChartcompetitor" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	    	
	    	var mainID = $("#competitorChart-mainID").val();
	    	var competitorID = $("#competitorChart-competitorID").val();
	    	var mainName = $("#competitorChart-mainName").val();
	    	var competitorName = $("#competitorChart-competitorName").val();	
	    	var mainColor = $("#competitorChart-mainColor").val();
	    	var competitorColor = $("#competitorChart-competitorColor").val();
	    	
	    	var jsonMain = dataBrand("engagementdata?ids="+mainID+"&date="+brandDate+"&value=retuits");
			var jsonCompetitor = dataBrand("engagementdata?ids="+competitorID+"&date="+brandDate+"&value=retuits");
			var jsonTotal = [];
			for(var i=0; i<jsonMain.length;i++) {
				var jsonParcialMain = jsonMain[i];
				var jsonParcialCompetitor = jsonCompetitor[i];
				jsonTotal.push({"fecha":jsonParcialMain.fecha, "valorA":jsonParcialMain.valor,"valorB":jsonParcialCompetitor.valor});
			}
			
	        // Create a Line Charts with Morris
			var chart = Morris.Line({
	            element: 'RetuLastWeekChartcompetitor',
	  		  	data: [{"fecha":"0","valorA":0,"valorB":0},{"fecha":"0","valorA":0,"valorB":0}], 
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valorA','valorB'],
	  		  	labels: [mainName,competitorName],	
	  		  	lineColors: [mainColor,competitorColor],
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: false,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });
			
	        chart.setData(jsonTotal);	            
        });
     });
	
	$('#Favbannerformmodalcompetitor').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    	$( "#FavLastWeekChartcompetitor" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	    	
	    	var mainID = $("#competitorChart-mainID").val();
	    	var competitorID = $("#competitorChart-competitorID").val();
	    	var mainName = $("#competitorChart-mainName").val();
	    	var competitorName = $("#competitorChart-competitorName").val();	
	    	var mainColor = $("#competitorChart-mainColor").val();
	    	var competitorColor = $("#competitorChart-competitorColor").val();
	    	
	    	var jsonMain = dataBrand("engagementdata?ids="+mainID+"&date="+brandDate+"&value=favoritos");
			var jsonCompetitor = dataBrand("engagementdata?ids="+competitorID+"&date="+brandDate+"&value=favoritos");
			var jsonTotal = [];
			for(var i=0; i<jsonMain.length;i++) {
				var jsonParcialMain = jsonMain[i];
				var jsonParcialCompetitor = jsonCompetitor[i];
				jsonTotal.push({"fecha":jsonParcialMain.fecha, "valorA":jsonParcialMain.valor,"valorB":jsonParcialCompetitor.valor});
			}
			
	        // Create a Line Charts with Morris
			var chart = Morris.Line({
	            element: 'FavLastWeekChartcompetitor',
	  		  	data: [{"fecha":"0","valorA":0,"valorB":0},{"fecha":"0","valorA":0,"valorB":0}], 
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valorA','valorB'],
	  		  	labels: [mainName,competitorName],	
	  		  	lineColors: [mainColor,competitorColor],
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: false,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });
			
	        chart.setData(jsonTotal);	
	            
        });
     });
	
	$('#Repbannerformmodalcompetitor').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    	$( "#RepLastWeekChartcompetitor" ).empty(); //clear chart so it doesn't create multiple if multiple clicks
	    	
	    	var mainID = $("#competitorChart-mainID").val();
	    	var competitorID = $("#competitorChart-competitorID").val();
	    	var mainName = $("#competitorChart-mainName").val();
	    	var competitorName = $("#competitorChart-competitorName").val();	
	    	var mainColor = $("#competitorChart-mainColor").val();
	    	var competitorColor = $("#competitorChart-competitorColor").val();
	    	
	    	var jsonMain = dataBrand("engagementdata?ids="+mainID+"&date="+brandDate+"&value=respuestas");
			var jsonCompetitor = dataBrand("engagementdata?ids="+competitorID+"&date="+brandDate+"&value=respuestas");
			var jsonTotal = [];
			for(var i=0; i<jsonMain.length;i++) {
				var jsonParcialMain = jsonMain[i];
				var jsonParcialCompetitor = jsonCompetitor[i];
				jsonTotal.push({"fecha":jsonParcialMain.fecha, "valorA":jsonParcialMain.valor,"valorB":jsonParcialCompetitor.valor});
			}
			
	        // Create a Line Charts with Morris
			var chart = Morris.Line({
	            element: 'RepLastWeekChartcompetitor',
	  		  	data: [{"fecha":"0","valorA":0,"valorB":0},{"fecha":"0","valorA":0,"valorB":0}], 
	  		  	xkey: 'fecha',
	  		  	ykeys: ['valorA','valorB'],
	  		  	labels: [mainName,competitorName],	
	  		  	lineColors: [mainColor,competitorColor],
	  		  	hideHover: 'true',
	  		  	resize: true,
	  		  	redraw: true,
	            stacked: false,
	            pointSize: 0,
	            xLabelAngle: 45,
	            xLabelFormat: function (x) { return (formatDate(x)); },
	            smooth: false
	        });
			
	        chart.setData(jsonTotal);	
	            
        });
     });
	
//fin competitor modals
	
	
	$('#Infbannerformmodal').on('shown.bs.modal', function () { //listen for user to open modal
	    $(function () {
	    	
	    	var scrTwitter="<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document,'script','twitter-wjs');</script><script async src='//platform.twitter.com/widgets.js' charset='utf-8'></script>";
		    var mdata="<div class='row'>";
		    var aux="<div class='col-sm-4' style='font-weight: bold;'>"+"Usuario"+"</div>"+"<div class='col-sm-4' style='font-weight: bold;'>"+"NºSeguidores"+"</div>"+"<div class='col-sm-4' style='font-weight: bold;'>"+"TimeLine"+"</div>";
		    var img = new Image();
		    
	        // Fire off an AJAX request to load the data
	        var json = dataBrand("criteriondata?ids="+brandID+"&date="+brandDate+"&value=influencers");
	        
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
	        var json = dataBrand("criteriondata?ids="+brandID+"&date="+brandDate+"&value=replied");
	        
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
	        var json = dataBrand("criteriondata?ids="+brandID+"&date="+brandDate+"&value=retweeted");
	        
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
	        var json = dataBrand("criteriondata?ids="+brandID+"&date="+brandDate+"&value=mentioned");
	        
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
	        var json = dataBrand("tweetthread?id="+$(event.relatedTarget).attr('id'));
	        
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
	
	
	// ------------------- FIN WIDGET BRAND -----------------
	
	
	
	// ------------------- INICIO WIDGET TWEETS -----------------
	
	function hideTweets(){
		$('#div-tweets').addClass("hidden");
	}
	
	function showTweets(){
		$('#div-tweets').removeClass("hidden");
	}
	
	function callTweetsUpdate(id,startDate,endDate,text,page,numResults){
		
		$("#tweets-container").empty();
		showTweets();
		
		var call = 'tweetswidget?id='+id+'&fInit='+moment(startDate).utc().format("YYYY-MM-DDTHH:mm:ss.SSS")+'Z&fEnd='+ moment(endDate).utc().format("YYYY-MM-DDTHH:mm:ss.SSS")+"Z";
		call = call + '&page='+ page + '&numResults='+ numResults;
		if (text!=null){
			call = call +'&text='+text;
		}
		
		var data = dataTweets(call);
		console.log("--> tweets response: ");
		console.log(data);
		
		if(typeof data !== "undefined"){
			
			var html = '<div class="table-responsive">';
			html += '<table class="table table-striped table-bordered">';
			html += '<thead><tr><th>#</th><th>tweet</th></tr></thead>';
			//html += '<thead><tr><th>#</th><th>tweet</th><th style="width: 90px;">sentimiento</th></tr></thead>';
			html += '<tbody>';
			for (var i = 0, len = data.length; i < len; ++i) {
			    html += '<tr>';
			    html += '<td>' + (i+1) + '</td>';
			    html += '<td id="'+data[i].tweetID+'"></td>';
			    
			    /*
			    var sentiment = parseFloat(data[i].sentiment);
			    if (sentiment < 0.0){
			    	html += '<td><span class="label label-danger">negativo</span></td>';
			    } else if (sentiment > 0.0){
			    	html += '<td><span class="label label-success">positivo</span></td>';
			    } else {
			    	html += '<td><span class="label label-warning">neutro</span></td>';
			    }
			    */
			    
			    html += "</tr>";
			}
			html += '</tbody>';
			html += '</table>';
			html += '</div>';
	
			$(html).appendTo("#tweets-container");
			
			for (var i = 0, len = data.length; i < len; ++i) {
			    twttr.widgets.createTweet(
				    data[i].tweetID,
				    document.getElementById(data[i].tweetID),
				    {
				    	align: 'center',
				    	width: '100%'
				    }
			    );
			}
			
			/*var html = '<div class="table-responsive">';
			html += '<table class="table table-striped table-bordered">';
			html += '<thead><tr><th>tweetID</th><th>text</th><th>createdAt</th><th>retweetCount</th><th>userID</th><th>userScreenName</th></tr></thead>';
			html += '<tbody>';
			for (var i = 0, len = data.length; i < len; ++i) {
			    html += '<tr>';
			    html += '<td>' + data[i].tweetID + '</td>';
			    html += '<td>' + data[i].text + '</td>';
			    html += '<td>' + data[i].createdAt + '</td>';
			    html += '<td>' + data[i].retweetCount + '</td>';
			    html += '<td>' + data[i].userID + '</td>';
			    html += '<td>' + data[i].userScreenName + '</td>';
			    html += "</tr>";
			}
			html += '</tbody>';
			html += '</table>';
			html += '</div>';
	
			$(html).appendTo("#tweets-container");*/
		}
		  
	}
	
	
	function dataTweets(url) {
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
					waitingDialog.hide();
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
		return json;
	}
	
	// ------------------- FIN WIDGET TWEETS -----------------

	
	// ------------------- INICIO WIDGET TWEETS SEARCH -----------------
	
	$("#form-tweets-search").submit(function( event ) {
		event.preventDefault();
		
		//text
		//dcIDs
		//criterio orden
		//fecha inicio
		//fecha fin
		
		//callTweetsSearchUpdate(datos recogidos)
	});
	
	/*
	$( "#other" ).click(function() {
	  $( "#target" ).submit();
	});
	*/
	
	function callTweetsSearchUpdate(id,startDate,endDate,text){
		
		$("#tweets-search-container").empty();
		
		var call = 'tweetswidget?id='+id+'&fInit='+moment(startDate).utc().format("YYYY-MM-DDTHH:mm:ss.SSS")+'Z&fEnd='+ moment(endDate).utc().format("YYYY-MM-DDTHH:mm:ss.SSS")+"Z";
		if (text!=null){
			call = call +'&text='+text;
		}
		
		var data = dataTweetsSearch(call);
		console.log("--> tweets response: ");
		console.log(data);
		
		if(typeof data !== "undefined"){
			
			var html = '<div class="table-responsive">';
			html += '<table class="table table-striped table-bordered">';
			html += '<thead><tr><th>#</th><th>tweet</th></tr></thead>';
			html += '<tbody>';
			for (var i = 0, len = data.length; i < len; ++i) {
			    html += '<tr>';
			    html += '<td>' + (i+1) + '</td>';
			    html += '<td id="'+data[i].tweetID+'"></td>';
			    html += "</tr>";
			}
			html += '</tbody>';
			html += '</table>';
			html += '</div>';
	
			$(html).appendTo("#tweets-search-container");
			
			for (var i = 0, len = data.length; i < len; ++i) {
			    twttr.widgets.createTweet(
				    data[i].tweetID,
				    document.getElementById(data[i].tweetID),
				    {
				    	align: 'center',
				    	width: '100%'
				    }
			    );
			}
			
		}
		  
	}
	
	
	function dataTweetsSearch(url) {
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
					waitingDialog.hide();
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
		return json;
	}
	
	// ------------------- FIN WIDGET TWEETS SEARCH -----------------
	
	
	// ------------------- INICIO LOADING MODAL -----------------
	
	var waitingDialog = waitingDialog || (function ($) {
	    'use strict';

		// Creating modal dialog's DOM
		var $dialog = $(
			'<div class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" role="dialog" aria-hidden="true" style="padding-top:15%; overflow-y:visible;">' +
			'<div class="modal-dialog modal-m">' +
			'<div class="modal-content">' +
				'<div class="modal-header"><h3 style="margin:0;"></h3></div>' +
				'<div class="modal-body">' +
					'<div class="progress progress-striped active" style="margin-bottom:0;"><div class="progress-bar" style="width: 100%"></div></div>' +
				'</div>' +
			'</div></div></div>');

		return {
			/**
			 * Opens our dialog
			 * @param message Custom message
			 * @param options Custom options:
			 * 				  options.dialogSize - bootstrap postfix for dialog size, e.g. "sm", "m";
			 * 				  options.progressType - bootstrap postfix for progress bar type, e.g. "success", "warning".
			 */
			show: function (message, options) {
				// Assigning defaults
				if (typeof options === 'undefined') {
					options = {};
				}
				if (typeof message === 'undefined') {
					message = 'Loading';
				}
				var settings = $.extend({
					dialogSize: 'm',
					progressType: '',
					onHide: null // This callback runs after the dialog was hidden
				}, options);

				// Configuring dialog
				$dialog.find('.modal-dialog').attr('class', 'modal-dialog').addClass('modal-' + settings.dialogSize);
				$dialog.find('.progress-bar').attr('class', 'progress-bar');
				if (settings.progressType) {
					$dialog.find('.progress-bar').addClass('progress-bar-' + settings.progressType);
				}
				$dialog.find('h3').text(message);
				// Adding callbacks
				if (typeof settings.onHide === 'function') {
					$dialog.off('hidden.bs.modal').on('hidden.bs.modal', function (e) {
						settings.onHide.call($dialog);
					});
				}
				// Opening dialog
				$dialog.modal();
			},
			/**
			 * Closes dialog
			 */
			hide: function () {
				$dialog.modal('hide');
			}
		};

	})(jQuery);
	
	// ------------------- FIN LOADING MODAL -----------------
	
	
	
	// ------------------- INICIO WIDGET MAP TWEETS -----------------
	
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


		//setup tabs:
//		$('#geo a').click(function (e) {
//			  e.preventDefault()
//			  $(this).tab('show')
//		});

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
//		$(".nav-sidebar a").click(function(e) {
//			console.info("SSSSS");
//			map2.invalidateSize();
//		});
		
	    
	    //create maps using leaflet library
		//use black/white tiles from: http://cartodb.com/basemaps
	    map = L.map('map', {maxZoom: 17}).setView([40.4165508,-3.7037990000000036], 10);
	    map.se
	    
	    //"neutro" heatmap
	    heat0Layer = L.layerGroup();
	    heat0Layer.addTo(map);
	    
	    heat0 = L.heatLayer([]);
	    heat0.setOptions({gradient: {"0.1": "orange", "0.6": "orange"}, minOpacity: 0.5, radius: 7, blur: 8});
	    heat0.addTo(heat0Layer);
	    map.removeLayer(heat0Layer);
	    
	    //"positive" heatmap
	    heat1Layer = L.layerGroup();
	    heat1Layer.addTo(map);
	    
	    heat1 = L.heatLayer([]);
	    heat1.setOptions({gradient: {"0.1": "green", "0.6": "green"}, minOpacity: 0.5, radius: 7, blur: 8});
	    heat1.addTo(heat1Layer);
	    map.removeLayer(heat1Layer);
	    
	    //"negative" heatmap
	    heat2Layer = L.layerGroup();
	    heat2Layer.addTo(map);
	    
	    heat2 = L.heatLayer([]);
	    heat2.setOptions({gradient: {"0.1": "red", "0.6": "red"}, minOpacity: 0.5, radius: 14, blur: 10})
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
	          data.push({
	            properties: {
	              group: +d.sent,
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
	    
	    
	    cscale = d3.scale.linear().domain([-1,0,1]).range(["#FF0000","#FFA500","#00FF00"]);

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
	              var pos=0, neg=0, neu=0;
	              d.map(function(e){
	                if (e.length === 3){
	                	if (e[2].group === 1) ++pos;
	                	else if (e[2].group === -1) ++neg;
	                	else ++neu;
	                } 
	              });
	              that.config.mouse.call(this, [neg,neu,pos]);
	              $("#tooltip").removeClass("hidden");
	              d3.select("#tooltip")
	                .style("visibility", "visible")
	                .style("position", "absolute")
	                .style("top", function () { return (d3.event.pageY - 100)+"px"})
	                .style("left", function () { return (d3.event.pageX - 100)+"px";})
	            })
	            .on("mouseout", function (d) { 
	            	$("#tooltip").addClass("hidden");
	            	d3.select("#tooltip")
	            		.style("visibility", "hidden") 
	            });
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
	              .style("fill", function(d, i) { 
	              console.log(i);
	            	  if (i === 0) return 'red';
	            	  else if (i === 2) return 'green';
	            	  else return 'orange';
	              }
	            );

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
//		map2 = L.map('heatmap').setView([40.4165508,-3.7037990000000036], 10);
	//    
//	 	var mapLink2 = 
//	        '<a href="http://openstreetmap.org">OpenStreetMap</a>';
//	    var mapSourceBW2 = "//stamen-tiles.a.ssl.fastly.net/toner/{z}/{x}/{y}.png";
//	    var mapSourceNormal2 = "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
//	    L.tileLayer(
//	        mapSourceNormal2, {
//	        attribution: '&copy; ' + mapLink2 + ' Contributors',
//	        maxZoom: 18,
//	    }).addTo(map2);
	    

		
	    function cleanOldDataMap(){
	    	map.removeLayer(markers);
			markers = new L.MarkerClusterGroup();
		    map.addLayer(markers);
		    map.removeLayer(markers);
			
		    $("svg[id=hex-svg]").remove();
		    
		    var data = { type: "FeatureCollection", features: toGeoJSON([]) };
		    hexLayer = L.layerGroup();
		    hexLayer.addTo(map);
		    
		    hex = L.hexbinLayer(data, {
		        style: hexbinStyle,
		        mouse: makePie
		    }).addTo(hexLayer);
	    }
	    
	    
		function callMapUpdate(id,startDate,endDate){
					
			//clean old data			
			cleanOldDataMap();
			
			showMap();
			
			var call = 'tweetswidget?id='+id+'&fInit='+moment(startDate).utc().format("YYYY-MM-DDTHH:mm:ss.SSS")+'Z&fEnd='+ moment(endDate).utc().format("YYYY-MM-DDTHH:mm:ss.SSS")+"Z";
			call = call +'&numResults=1000';
			call = call +'&geo=true';
			
			var data = dataTweetsSearch(call);
			console.log("--> map response: ");
			console.log(data);
					
			if(typeof data !== "undefined"){
				for (var i = 0, len = data.length; i < len; ++i) {
					
					var geoTweet = data[i].latLong.split(",");
					var sentiment;
				    if (parseFloat(data[i].sentiment) > 0.0){
						sentiment = 1;
				    }
				    else if (parseFloat(data[i].sentiment) == 0.0){
						sentiment = 0;
				    }
				    else {
						sentiment = -1;
				    }
				    
				    var circle = L.marker(
					[geoTweet[0], geoTweet[1]], 
					{
						color: 'red',
					    fillColor: '#f03',
					    fillOpacity: 0.5,
					    radius: "8",
					    _type: "circle"
					}).addTo(markers).bindPopup(data[i].text);
						
					hex.addPoint([{lat: geoTweet[0], long: geoTweet[1], sent: sentiment}]);
					//map.eachLayer(function(e) {if (e.options._type) map.removeLayer(e)})
					
					//add point ot the heatmap:
				    if (sentiment > 0){
						heat1.addLatLng([geoTweet[0], geoTweet[1]]);
				    }
				    else if (sentiment == 0){
						heat0.addLatLng([geoTweet[0], geoTweet[1]]);
				    }
				    else {
						heat2.addLatLng([geoTweet[0], geoTweet[1]]);
				    }

				}
			}
			
			switchMapMode($('.mapbutton.btn-info').val());

		}
		
		function hideMap(){
			$('#div-map').addClass("hidden");
		}
		
		function showMap(){
			$('#div-map').removeClass("hidden");
		}
		
		
		//call hideMap when it is loaded, not before
		hideMap();
	
	// ------------------- FIN WIDGET MAP TWEETS -----------------

	
});

