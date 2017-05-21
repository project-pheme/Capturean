//Volume Line Chart
$(document).ready(function() {
	
	var endDate;
	var startDate;
	var vstartDate;
	var frecuency=null;
	var id=[];
	var idsVT;
	var clickedALL=false;	
	var partNames=[];
	var partNamesLabels=['Time'];
	var yKeysVol=[];
	var colors=[];	
	var isBrands;
	var datos = [];
	var datosVT = [];
	var fixedDelay = 180000; //3 mins
	var videoID;//="GV9K7jK-BL4";
	
//	var url= document.getElementById('myUrl').getAttribute('data-value');
//	console.log("myUrl="+url);
	
//	if(document.getElementById('isBrands'))
//		isBrands = document.getElementById('isBrands').getAttribute('data-value');
//	console.log("isBrands="+isBrands);
	
	idsVT= document.getElementById('idsVT').getAttribute('data-value');
	
	videoID= document.getElementById('idVideo').getAttribute('data-value');
	
	var numIds= document.getElementById('numIds').getAttribute('data-value');
	console.log("numIds="+numIds);
	
	//Party names
	var labels;
	for(var i=0; document.getElementById('partName'+i)!= null;i++){
		var aux = document.getElementById('partName'+i).getAttribute('data-value');
		console.log("Participante" + i + " = " + aux);	
		partNames[i]=aux;
		yKeysVol[i]="volume"+i;
		partNamesLabels[i+1]=aux;
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
	
	//Debate de 21:00 a 23:59, hora española
	//endDate = moment("2015-12-07T23:59:59").utc().format("ddd MMM DD YYYY HH:mm:ss z");
	//startDate = moment("2015-12-07T21:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");	
		
	//$("#fromDate").children("input").val(moment("2015-12-07T21:00:00").format("YYYY-MM-DD HH:mm:ss"));
	//$("#toDate").children("input").val(moment("2015-12-07T23:59:59").format("YYYY-MM-DD HH:mm:ss"));
	
	if(document.getElementById('isRTCara')){
		endDate = moment("2015-12-15T02:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		startDate = moment("2015-12-14T20:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");	
		vstartDate = moment("2015-12-14T22:00:00"); //Hora inicio debate
		
		$("#fromDate").children("input").val(moment("2015-12-14T20:00:00").format("YYYY-MM-DD HH:mm:ss"));
		$("#toDate").children("input").val(moment("2015-12-15T02:00:00").format("YYYY-MM-DD HH:mm:ss"));
	}else{
		endDate = moment("2015-12-07T23:59:59").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		startDate = moment("2015-12-07T21:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		vstartDate = moment("2015-12-07T22:00:00"); //Hora inicio debate
			
		$("#fromDate").children("input").val(moment("2015-12-07T21:00:00").format("YYYY-MM-DD HH:mm:ss"));
		$("#toDate").children("input").val(moment("2015-12-07T23:59:59").format("YYYY-MM-DD HH:mm:ss"));
	}
		
	frecuency="minutes1";	
	
	callVolumeUpdate(startDate,endDate,frecuency); //1800
	
	
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
			  
			  var dI = new moment(startDate);
			  var dF = new moment(dI + (60*1000))			 
			  var end = new moment(endDate);
			  var now = new moment();
			  
			  if(dF>now){
				  dF=now;
			  }
			  
			  //First time
		      console.log('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);
		      var y = dataRT('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);//Math.random();
		      x=new Date(dI);
			  console.log("x="+x+" y="+y);
			  var d=[x];
			  var k=1;
			  var key;
			  for(key in y) {
				  d[k]=y[key];
				  k++;
			  }
			  console.log("d="+d);
			  datos.push(d);	
		      
			  console.log('rtvolumedata?id='+idsVT+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+1);
		      var yVT = dataRT('rtvolumedata?id='+idsVT+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+1);//Math.random();
			  var dVT=[x];
			  var kVT=1;
			  var keyVT;
			  for(keyVT in yVT) {
				  dVT[kVT]=yVT[keyVT];
				  kVT++;
			  }
			  console.log("dVT="+dVT);
			  datosVT.push(dVT);			  
			  
			  
		      dI=dF;
		      dF=new moment(dF + (60*1000));

		      window.intervalId = setInterval(function() {
//			  while(dF <= end){
//			      console.log('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);
//			      var y = data('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);//Math.random();
//			      x=new Date(dI);
//				  var d=[x];
//				  var k=1;
//				  var key, count = 0;
//				  for(key in y) {
//					  d[k]=y[key];
//					  k++;
//				  }
//				  datos.push(d);
//			      dI=dF;
//			      dF=new moment(dF + (60*1000));
//			  }
		    	  
		    	  if(dF <= end){
		    		  console.log('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);
		    		  var y = dataRT('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);//Math.random();
		    		  x=new Date(dI);
		    		  var d=[x];
		    		  var k=1;
		    		  var key;
		    		  for(key in y) {
		    			  d[k]=y[key];
		    			  k++;
		    		  }
		    		  console.log("d"+d);
		    		  datos.push(d);		    		  
		    		  graph.updateOptions( { 'file': datos } );
		    		  
		    		  console.log('rtvolumedata?id='+idsVT+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+1);
		    		  var yVT = dataRT('rtvolumedata?id='+idsVT+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+1);//Math.random();
		    		  var dVT=[x];
		    		  var kVT=1;
		    		  var keyVT;
		    		  for(keyVT in yVT) {
		    			  dVT[kVT]=yVT[keyVT];
		    			  kVT++;
		    		  }
		    		  console.log("dVT"+dVT);
		    		  datosVT.push(dVT);		    		  
		    		  graphVT.updateOptions( { 'file': datosVT } );
		    		  
		    		  dI=dF;
		    		  dF=new moment(dF + (60*1000));
		          }else{
		        	  clearInterval(window.intervalId);
		          }  
		      }, 1000);
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
			graphVT.setData([ {
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
			//updateVolume();
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
		//updateVolume();
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
	

	function dataRT(url) {
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
					//$("#loading-indicator").hide();
					//json =data; PRUEBA
					//json=parseJsonDate(data); //PRUEBA
					json =data.sort(order());
					//json =[{"volume":0,"volume1":1,"date":"2015-04-22"},{"volume":1,"volume1":2,"date":"2015-04-05"},{"volume":2,"volume1":3,"date":"2015-04-10"},{"volume":3,"volume1":4,"date":"2015-04-11"},{"volume":4,"volume1":5,"date":"2015-04-06"},{"volume":5,"volume1":6,"date":"2015-04-18"},{"volume":6,"volume1":7,"date":"2015-04-09"},{"volume":7,"volume1":8,"date":"2015-04-16"},{"volume":8,"volume1":9,"date":"2015-04-20"},{"volume":9,"volume1":10,"date":"2015-04-04"},{"volume":10,"volume1":11,"date":"2015-04-02"},{"volume":11,"volume1":12,"date":"2015-04-12"},{"volume":12,"volume1":13,"date":"2015-04-03"},{"volume":13,"volume1":14,"date":"2015-03-31"},{"volume":14,"volume1":15,"date":"2015-04-13"},{"volume":15,"volume1":16,"date":"2015-04-17"},{"volume":16,"volume1":17,"date":"2015-04-21"},{"volume":17,"volume1":18,"date":"2015-04-14"},{"volume":18,"volume1":19,"date":"2015-04-15"},{"volume":19,"volume1":20,"date":"2015-04-08"},{"volume":20,"volume1":25,"date":"2015-04-07"},{"volume":21,"volume1":24,"date":"2015-04-19"},{"volume":22,"volume1":23,"date":"2015-04-01"}]					
				},
				'error': function(jqXHR, textStatus, errorThrown){
					//$('#myModal').modal('hide'); //PRUEBA
					//$("#loading-indicator").hide();
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
//	function parseJsonDate(jsonData){			
//		for(var i=0;i<jsonData.length;i++){
//			jsonData[i].date = moment(new Date(jsonData[i].date)).format("YYYY-MM-DD HH:mm:ss");
//	    }		
//	    return jsonData;
//	}
	
	//order json by date
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
	
	var graph = new Dygraph(document.getElementById("RTVolumeChart"), datos,
            	{
					drawPoints: true,
					showRoller: true,
					colors:colors,
					//valueRange: [0.0, 1.2],
					labels: partNamesLabels,
					
					pointClickCallback: function(e, pt) {
						var src = '';
						var aux = (pt.xval-vstartDate)-fixedDelay;
						if(aux <= 0){
							aux =0;
							src = 'https://www.youtube.com/embed/'+videoID+'?start=0';
						}else{
							var value = aux/1000;
							var src = 'https://www.youtube.com/embed/'+videoID+'?start='+value;
						}
						$('#videoModal').modal('show');
				        $('#videoModal iframe').attr('src', src);
		            },
		            
		            zoomCallback : function(min, max, range) {
		            	zoomGraphVT(min, max, range);
		            }
		            
            	});
	
	var graphVT = new Dygraph(document.getElementById("RTVolumeTChart"), datosVT,
        	{
				drawPoints: true,
				showRoller: true,
				colors:colors,
				//valueRange: [0.0, 1.2],
				labels: ['Time','Volume'],
				fillGraph: true,
				
				pointClickCallback: function(e, pt) {
					var src = '';
					var aux = (pt.xval-vstartDate)-fixedDelay;
					if(aux <= 0){
						aux =0;
						src = 'https://www.youtube.com/embed/'+videoID+'?start=0';
					}else{
						var value = aux/1000;
						var src = 'https://www.youtube.com/embed/'+videoID+'?start='+value;
					}
					$('#videoModal').modal('show');
			        $('#videoModal iframe').attr('src', src);
	            },	
	            
	            zoomCallback : function(min, max, range) {
	            	zoomGraph(min, max, range);
	            },
	            
	            drawCallback: function(g, is_initial) {
	            	$("#loading-indicator").hide();
	            }
        	});
	
	// update the chart with the new data.
	function updateVolume() {
	  console.log("updateVolume: startDate="+startDate+" endDate="+endDate+" frecuency="+frecuency);	
	  if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')){ //&&
//				 (frecuency!=null&&frecuency!='undefined')){		  
		  clearInterval(window.intervalId);
		  graph.destroy();	
		  //datos = [];
		  datos = new Array();
		  
		  graph = new Dygraph(document.getElementById("RTVolumeChart"), datos,
	            	{
						drawPoints: true,
						showRoller: true,
						colors:colors,
						//valueRange: [0.0, 1.2],
						labels: partNamesLabels,
						
						pointClickCallback: function(e, pt) {
							var src = '';
							var aux = (pt.xval-vstartDate)-fixedDelay;
							if(aux <= 0){
								aux =0;
								src = 'https://www.youtube.com/embed/'+videoID+'?start=0';
							}else{
								var value = aux/1000;
								var src = 'https://www.youtube.com/embed/'+videoID+'?start='+value;
							}
							$('#videoModal').modal('show');
					        $('#videoModal iframe').attr('src', src);
			            }, 
			            
			            zoomCallback : function(min, max, range) {
			            	zoomGraphVT(min, max, range);
			            }
			            
	            	});
		  
		  graphVT.destroy();	
		  //datos = [];
		  datosVT = new Array();
		  
		  graphVT = new Dygraph(document.getElementById("RTVolumeTChart"), datosVT,
	            	{
						drawPoints: true,
						showRoller: true,
						colors:colors,
						//valueRange: [0.0, 1.2],
						labels: ['Time','Volume'],
						fillGraph: true,
						
						pointClickCallback: function(e, pt) {
							var src = '';
							var aux = (pt.xval-vstartDate)-fixedDelay;
							if(aux <= 0){
								aux =0;
								src = 'https://www.youtube.com/embed/'+videoID+'?start=0';
							}else{
								var value = aux/1000;
								var src = 'https://www.youtube.com/embed/'+videoID+'?start='+value;
							}
							$('#videoModal').modal('show');
					        $('#videoModal iframe').attr('src', src);
			            },	
			            
			            drawCallback: function(g, is_initial) {
			            	$("#loading-indicator").hide();
			            },
			            
			            zoomCallback : function(min, max, range) {
			            	zoomGraph(min, max, range);
			            }
	            	});

		  callVolumeUpdate(startDate,endDate,"minutes1");
	  }
	}
	//FIN
	
	function zoomGraph(min, max, range) {
    	graph.updateOptions({
    		dateWindow: [min, max]
    	});      
    }

    function zoomGraphVT(min, max, range) {
    	graphVT.updateOptions({
    		dateWindow: [min, max]
    	});
    }
    
	$(window).resize(function() {
		graph.resize();
	});
	
	$(function () {
		  $('#ddParticipants').dropdown('toggle');
	});
	
});

