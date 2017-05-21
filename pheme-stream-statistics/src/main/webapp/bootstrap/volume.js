//Volume Line Chart
$(document).ready(function() {
	
	var endDate;
	var startDate;
	var frecuency=null;
	var id=[];
	var clickedALL=false;	
	//Morris chart values
	//var partNames = ['PP','PSOE', 'Podemos','Ciudadanos','UPyD','IU'];
	//var yKeysVol= ['volume0','volume1','volume2','volume3', 'volume4', 'volume5'];
	//var colors = ['#3366CC','#FF0000','#990099','#FF3300','#FF0099','#FF0000'];
	var partNames=[];
	var yKeysVol=[];
	var colors=[];	
	var isBrands;
	var isPartiesG;
	
//	var url= document.getElementById('myUrl').getAttribute('data-value');
//	console.log("myUrl="+url);
	
	if(document.getElementById('isBrands'))
		isBrands = document.getElementById('isBrands').getAttribute('data-value');
	console.log("isBrands="+isBrands);
	
	if(document.getElementById('isPartiesG'))
		isPartiesG = document.getElementById('isPartiesG').getAttribute('data-value');
	console.log("isPartiesG="+isPartiesG);
	
	var numIds= document.getElementById('numIds').getAttribute('data-value');
	console.log("numIds="+numIds);
	
	//Party names
	for(var i=0; document.getElementById('partName'+i)!= null;i++){
		var aux = document.getElementById('partName'+i).getAttribute('data-value');
		console.log("Participante" + i + " = " + aux);	
		partNames[i]=aux;
		yKeysVol[i]="volume"+i;
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
		id[i]=aux;		
	}
	
	//PRUEBA	
	//var now = new Date("Mon May 25 2015 08:05:00 GMT+0200");//PRUEBA => Quito "new Date;" y pruebo con un valor fijo //Date.now();
	//var ini = new Date("Sun May 24 2015 08:05:00 GMT+0200");//PRUEBA => Quito "now - (1000 * 3600 * 24);" y pruebo con un valor fijo //24=> 1 day 
	//var date = new Date(ini);	
	//console.log("ON LOAD: Last hours: now="+now.toGMTString()+" ini="+date.toGMTString());
	//console.log("ON LOAD:ids="+id);
	if(isBrands=="true"){
		var m = moment().subtract(1,'days');	
		endDate = m.format("ddd MMM DD YYYY 23:59:59");
	    var ini = m - (1000 * 3600 * 24 * 7);
	    startDate = moment(ini).format("ddd MMM DD YYYY 00:00:00");
	    frecuency="daily";
		
		//endDate = moment("2015-10-18T23:59:59").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		//startDate = moment("2015-10-11T00:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");	
		
		$("#fromDate").children("input").val(moment(ini).format("YYYY-MM-DD 00:00:00"));
	    $("#toDate").children("input").val(m.format("YYYY-MM-DD 23:59:59"));		
		
	}
	else if(isPartiesG=="true"){
		endDate = moment("2015-12-31T23:59:59").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		startDate = moment("2015-12-09T00:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");	
		
		$("#fromDate").children("input").val(moment("2015-12-09T00:00:00").format("YYYY-MM-DD 00:00:00"));
	    $("#toDate").children("input").val(moment("2015-12-31T23:59:59").format("YYYY-MM-DD 23:59:59"));
		
		frecuency="daily";
	} 
	else{
		endDate = moment("2015-05-25T08:05:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		startDate = moment("2015-05-24T08:05:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");	
		
		$("#fromDate").children("input").val(moment("2015-05-25T08:05:00").format("YYYY-MM-DD 00:00:00"));
	    $("#toDate").children("input").val(moment("2015-05-24T08:05:00").format("YYYY-MM-DD 23:59:59"));
		
		frecuency="hourly";
	}
	//startDate=date.toGMTString();
	//endDate=now.toGMTString();	
	callVolumeUpdate(startDate,endDate,frecuency); //1800
	//FIN PRUEBA

//	//Zoom 1 => Last hour with sizeSec=300(5 mins)
//	$('#lasthour').on('click', function (e) {	
////		var tzoffset = (new Date()).getTimezoneOffset() * 60000; //offset in milliseconds
////		var localISOTime = (new Date(Date.now() - tzoffset)).toISOString().slice(0,-1)+"Z";
////		var ini = Date.now() - (1000 * 3600); // 1 hour in millisecs
////		var iniISOTime = (new Date(ini - tzoffset)).toISOString().slice(0,-1)+"Z";
////		console.log("now="+new Date()+" nowISO="+localISOTime);
////		console.log("ini="+ini+"iniISO="+iniISOTime);		
//		var now = new Date;//Date.now();
//		console.log("now="+now);
//		var ini = now - (1000 * 3600); // 1 hour in millisecs
//		console.log("Last hour: now="+new Date(now).toISOString()+" ini="+new Date(ini).toISOString());		
//		callVolumeUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),300);
//		//callVolumeUpdate(iniISOTime,localISOTime,300);
//	});
//	
//	//Zoom 2 => Last hours. Last 12 hours with sizeSec=1800(30 mins)
//	$('#lasthours').on('click', function (e) {
//		var now = new Date;//Date.now();
//		var ini = now - (1000 * 3600 * 12); // 12 hour in millisecs
//		console.log("Last hours: now="+new Date(now).toISOString()+" ini="+new Date(ini).toISOString());
//		callVolumeUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),1800);
//	});
//	
//	//Zoom 3 => Last weeks. Last 3 weeks with sizeSec=14400 (4 hours)
//	$('#lastweeks').on('click', function (e) {
//		var now = new Date;//Date.now();
//		var ini = now - (1000 * 3600 * 24 * 7 * 3); //24=> 1 day, 7 => seven days= 1 week, 3 => 3 weeks 
//		console.log("Last hours: now="+now+" ini="+new Date(ini).toISOString());
//		callVolumeUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),14400);
//	});
	
	//Last 24 hours with sizeSec=1800(30 mins)
	$('#lastday').on('click', function (e) {
		//CLEAN DATES
		$('#fromDate').find('input:text').val('');
		$('#toDate').find('input:text').val('');
		
		var now = moment().subtract(1,'days');//new Date;//Date.now();
		var ini = now - (1000 * 3600 * 24); // 12 hour in millisecs
		var date= moment(ini);//new Date(ini);
		console.log("Last hours: now="+now.utc().format("ddd MMM DD YYYY HH:mm:ss z")+" ini="+date.utc().format("ddd MMM DD YYYY HH:mm:ss z"));
		endDate=now.utc().format("ddd MMM DD YYYY HH:mm:ss z");//toGMTString();
		startDate=date.utc().format("ddd MMM DD YYYY HH:mm:ss z");//toGMTString();
		frecuency="hourly";
		
		$("#fromDate").children("input").val(date.utc().format("YYYY-MM-DD 00:00:00"));
	    $("#toDate").children("input").val(now.utc().format("YYYY-MM-DD 23:59:59"));
		
		callVolumeUpdate(startDate,endDate,frecuency); //1800
	});
	
	//Last 4 weeks with sizeSec=86400 (24 hours=86400000 millisecs)
	$('#lastmonth').on('click', function (e) {
		//CLEAN DATES
		$('#fromDate').find('input:text').val('');
		$('#toDate').find('input:text').val('');
		
		var now = moment().subtract(1,'days'); //new Date;//Date.now();
		var ini = now - (1000 * 3600 * 24 * 7 * 4); //24=> 1 day, 7 => seven days= 1 week, 4 => 4 weeks
		var date= moment(ini); //new Date(ini);
		console.log("Last hours: now="+now.utc().format("ddd MMM DD YYYY HH:mm:ss z")+" ini="+date.utc().format("ddd MMM DD YYYY HH:mm:ss z"));
		endDate=now.utc().format("ddd MMM DD YYYY HH:mm:ss z");//.toGMTString();
		startDate=date.utc().format("ddd MMM DD YYYY HH:mm:ss z");//.toGMTString();
		frecuency="daily";
		
		$("#fromDate").children("input").val(date.utc().format("YYYY-MM-DD 00:00:00"));
	    $("#toDate").children("input").val(now.utc().format("YYYY-MM-DD 23:59:59"));
		
		callVolumeUpdate(startDate,endDate,frecuency); //86400
	});
	
	
	function callVolumeUpdate(fIni, fFin,sec){
		if((id.length>0)&&(sec!=null)&&(sec!=undefined)){			  
			  var iD="";
			  for (var i=0; i < id.length; i++) {
				  if(""==iD)
					  iD=id[i];
				  else
					  iD=iD+','+id[i]; 
			  }
			  console.log("iD="+iD);	
			  //console.log("url="+url);
			 // var call = 'http://localhost:8080/volumedata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec;
			  //var call = url+'volumedata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec;
			  var call = 'volumedata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec+'&numIds='+numIds;
			  console.log(call);
			  $("#loading-indicator").show();
			  setTimeout(function(){graph.setData(data(call))},1000); //Delay necessary to show the loading-indicator
			  //graph.setData(data(call));
		}else{
			BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_WARNING,
	            title: 'Warning!!',
	            message: 'Please select a party first'		            	
	        });
			graph.setData([ {
			  date: 0,
			  volume : 0
			}]);
		}
	}
	
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
					$("input[type='checkbox']").iCheck('check'); 
					var value=checkboxes[i].value; // add checkbox id to id's array
					var index = id.indexOf(value);
					if (index <= -1) { // if it is not, add it
			       		id[id.length]=value;
			       	}
				}else{
					//checkboxes[i].checked = false;
					$("input[type='checkbox']").iCheck('uncheck');
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
			updateVolume();
		}
	});
	
	//$("input[type='checkbox']").on('ifChanged',function() {
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
		updateVolume();
	});
	
	function parseDate(input) {
		  var parts = input.match(/(\d+)/g);		  
		  return new Date(parts[0], parts[1]-1, parts[2]); // months are 0-based
	}
	
	//Get frequency selected
	$(document).on('click', '.dropdown-menu li a', function () {
	   console.log("Entra aqui="+$(this).text());
	   if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')){
		   console.log(endDate);
		   console.log(startDate);		   
		   //var timeDiff = Math.abs((new Date(endDate)).getTime() - (new Date(startDate)).getTime());
		   var timeDiff = Math.abs(moment(endDate).diff(moment(startDate)));
		   console.log(timeDiff);
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
		        	frecuency = "daily";//"86400"; //secs in one day
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
//		   updateVolume(); PRUEBA BOTÓN
	   }else{
		   BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_WARNING,
	            title: 'Warning!!',
	            message: 'Please, first select a start and end date!'		            	
	        });
	   }
	});	
	
	$(document).ready(function() {
        $('#fromDate').datetimepicker({        	
        	//format: 'MM/dd/yyyy HH:ii:00P',        	
        	format: 'yyyy-mm-dd HH:mm:ss',
            language: 'en',
            pick12HourFormat: true,
            showTodayButton: true  
        });
    });
	
	
	$(document).ready(function() {
        $('#toDate').datetimepicker({        	
            //format: 'MM/dd/yyyy HH:ii:00P',
        	format: 'yyyy-mm-dd HH:mm:ss',
            language: 'en',
            pick12HourFormat: true,
            showTodayButton: true            
        });       
    });	
	
	//$('#fromDate').datepicker()
	$('#fromDate').datetimepicker()
	.on('changeDate', function(ev){
		//CLEAN DATES
//		$('#fromDate').find('input:text').val('');
//		startDate=null;		
		
		if(ev.date != null && (typeof ev.date != 'undefined')){
			//ev.date.getSeconds() => put 00
			
			//startDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),ev.date.getHours()-2, ev.date.getMinutes(), '00'); ////-2, considering it GMT+2
			//console.log("startDate="+startDate);
			//startDate=startDate.toGMTString();//toISOString(); //format
			//console.log("startDate="+startDate+"endDate="+endDate);
			//console.log("startDate(mils)="+Date.parse(startDate)+"endDate(mils)="+Date.parse(endDate));
			
			//Fix date, since datetimepicker takes chosen date as UTC, and modify it accordly to user timezone. We fix
			//it in a way that the selected hour is on user timezone by default
			var TimeZoned = new Date(ev.date.setTime(ev.date.getTime() + (ev.date.getTimezoneOffset() * 60000)));			
			$('#fromDate').datetimepicker('setDate', TimeZoned);			
			startDate = moment(TimeZoned).utc().format("ddd MMM DD YYYY HH:mm:ss z");			
			frecuency = null; 
			
			if(endDate!=null&&endDate!='undefined'){
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
    
	
	//$("#toDate").datepicker()
	$('#toDate').datetimepicker()
	.on("changeDate", function(ev){
		//CLEAN DATES
//		$('#toDate').find('input:text').val('');
//		endDate=null;
		
		if(ev.date != null && (typeof ev.date != 'undefined')){
			//ev.date.getSeconds() => put 00
			
			//endDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),ev.date.getHours()-2, ev.date.getMinutes(), '00'); //-2, considering it GMT+2
			//console.log("endDate="+startDate);
			//endDate=endDate.toGMTString();//toISOString(); //format
			//console.log("startDate="+startDate+"endDate="+endDate);
			//console.log("startDate(mils)="+Date.parse(startDate)+"endDate(mils)="+Date.parse(endDate));
			
			var TimeZoned = new Date(ev.date.setTime(ev.date.getTime() + (ev.date.getTimezoneOffset() * 60000)));			
			$('#toDate').datetimepicker('setDate', TimeZoned);
			endDate = moment(TimeZoned).utc().format("ddd MMM DD YYYY HH:mm:ss z");			
			frecuency = null; 
			
			if(startDate!=null&&startDate!='undefined'){
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
	
	//delete when clicked
//	$("#fromDate").on('click', function(ev){
//		$('#fromDate').find('input:text').val('');
//		startDate=null;
//	});
	
	//refresh chart when clicked
	$("#refresh").on('click', function(ev){
		updateVolume();
	});
	
	//Hide when date selected
	$("#toDate").on('changeDate', function(ev){
	    $(this).datetimepicker('hide');
	    //   if(startDate!=null&&startDate!='undefined'){
	    //   	$('#VolumeChart').html(''); //Clean chart
	    //   	_chart.setData(json.setData(startDate,));	    	
	    //   }
    	//updateVolume(); PRUEBA BOTON
	});
	//Hide when date selected
	$("#fromDate").on('changeDate', function(ev){
	    $(this).datetimepicker('hide');	
	    //if(endDate!=null&&endDate!='undefined'){
	    //	$('#VolumeChart').html(''); //Clean chart
	    //	_chart.setData(json.setData());
	    //}
    	//updateVolume(); PRUEBA BOTON
	});	
	
	
	

	function data(url) {
		var json;		
		if(url!=""){						
			$.ajax({
				'type': 'GET',
				'async': false,
				//'global': false,
				'url': url, 
				'dataType': "json",				
				'success': function (data) {
					//$('#myModal').modal('hide'); //PRUEBA 
					$("#loading-indicator").hide();
					//json =data; PRUEBA
					json=parseJsonDate(data); //PRUEBA
					//json =[{"volume":0,"volume1":1,"date":"2015-04-22"},{"volume":1,"volume1":2,"date":"2015-04-05"},{"volume":2,"volume1":3,"date":"2015-04-10"},{"volume":3,"volume1":4,"date":"2015-04-11"},{"volume":4,"volume1":5,"date":"2015-04-06"},{"volume":5,"volume1":6,"date":"2015-04-18"},{"volume":6,"volume1":7,"date":"2015-04-09"},{"volume":7,"volume1":8,"date":"2015-04-16"},{"volume":8,"volume1":9,"date":"2015-04-20"},{"volume":9,"volume1":10,"date":"2015-04-04"},{"volume":10,"volume1":11,"date":"2015-04-02"},{"volume":11,"volume1":12,"date":"2015-04-12"},{"volume":12,"volume1":13,"date":"2015-04-03"},{"volume":13,"volume1":14,"date":"2015-03-31"},{"volume":14,"volume1":15,"date":"2015-04-13"},{"volume":15,"volume1":16,"date":"2015-04-17"},{"volume":16,"volume1":17,"date":"2015-04-21"},{"volume":17,"volume1":18,"date":"2015-04-14"},{"volume":18,"volume1":19,"date":"2015-04-15"},{"volume":19,"volume1":20,"date":"2015-04-08"},{"volume":20,"volume1":25,"date":"2015-04-07"},{"volume":21,"volume1":24,"date":"2015-04-19"},{"volume":22,"volume1":23,"date":"2015-04-01"}]					
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
			//json=[{"volume":0,"date":"2015-04-22"},{"volume":1,"date":"2015-04-05"},{"volume":2,"date":"2015-04-10"},{"volume":3,"date":"2015-04-11"},{"volume":4,"date":"2015-04-06"},{"volume":5,"date":"2015-04-18"},{"volume":6,"date":"2015-04-09"},{"volume":7,"date":"2015-04-16"},{"volume":8,"date":"2015-04-20"},{"volume":9,"date":"2015-04-04"},{"volume":10,"date":"2015-04-02"},{"volume":11,"date":"2015-04-12"},{"volume":12,"date":"2015-04-03"},{"volume":13,"date":"2015-03-31"},{"volume":14,"date":"2015-04-13"},{"volume":15,"date":"2015-04-17"},{"volume":16,"date":"2015-04-21"},{"volume":17,"date":"2015-04-14"},{"volume":18,"date":"2015-04-15"},{"volume":19,"date":"2015-04-08"},{"volume":20,"date":"2015-04-07"},{"volume":21,"date":"2015-04-19"},{"volume":22,"date":"2015-04-01"}];
			json= [ {
				date : '0',
				volume : 0,
			}];
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
	
	// create the morris chart. 
	var graph = Morris.Line({
	    element: 'VolumeChart',
	    data: data(""),
	    xkey: 'date',
	    ykeys: yKeysVol,
	    labels: partNames,
	    lineColors:colors,
	    smooth: false,
	    stacked: true,
        hideHover: 'auto',
        pointSize: 0,
	    resize: true,
	    redraw: true
	});
	
	// update the chart with the new data.
	function updateVolume() {
	  console.log("updateVolume: startDate="+startDate+" endDate="+endDate+" frecuency="+frecuency);	
	  if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')&&
				 (frecuency!=null&&frecuency!='undefined')){	
		  callVolumeUpdate(startDate,endDate,frecuency);
	  }
	}
	//FIN
	
	$(window).resize(function() {
		graph.redraw();
	});
	
	$(function () {
		  $('#ddParticipants').dropdown('toggle');
	});
	
});

