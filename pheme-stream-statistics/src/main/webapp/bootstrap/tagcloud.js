//Volume Line Chart
$(document).ready(function() {
	
	var startDate;
	var id=[];	
	var partNames=[];
	var yKeysVol=[];
	var colors=[];
	var isBrands;
	var isDebate;
	var isPartiesG;
	
	//var url= document.getElementById('myUrl').getAttribute('data-value');
	//console.log("myUrl="+url);
	if(document.getElementById('isBrands'))
		isBrands = document.getElementById('isBrands').getAttribute('data-value');
	console.log("isBrands="+isBrands);
	
	if(document.getElementById('isPartiesG'))
		isPartiesG = document.getElementById('isPartiesG').getAttribute('data-value');
	console.log("isPartiesG="+isPartiesG);
	
	if(document.getElementById('isDebate'))
		isDebate = document.getElementById('isDebate').getAttribute('data-value');
	console.log("isDebate="+isDebate);
	
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
//	var now = new Date;//Date.now();
//	//var ini = now - (1000 * 3600 * 24); //24=> 1 day
//	var date= new Date(now);//ini);
//	date.setHours(02, 00, 00);//+2 por GMT+2
//	console.log("ON LOAD: Last hours: now="+now+" to GMT="+ date.toGMTString());
//	console.log("ON LOAD:ids="+id);	
//	
//	if(isBrands=="true"){
//		startDate=moment("2015-10-11T02:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
//	}else{
//		startDate="Fri, 26 Jun 2015 02:00:00 GMT";//PRUEBA QUITO "date.toGMTString();" y pongo la fecha de hoy
//	}
	
	
	if(isBrands=="true"){
		var m = moment().subtract(1,'days');		
	    startDate = m.format("ddd MMM DD YYYY 00:00:00 z");	      
	    
	    $("#tagFromDate").children("input").val(m.format("YYYY-MM-DD 00:00:00 z"));	   
	    
	}else if(isDebate=="true"){		
		startDate = moment("2015-11-02T02:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		
		$("#tagFromDate").children("input").val(moment("2015-11-02T00:00:00").format("YYYY-MM-DD 00:00:00"));
	    
	}
	else if(isPartiesG=="true"){
		startDate = moment("2015-12-09T02:00:00").utc().format("ddd MMM DD YYYY 00:00:00 z");
		
		$("#tagFromDate").children("input").val(moment("2015-12-09T00:00:00").format("YYYY-MM-DD 00:00:00"));	   
		
	} 
	else{		
		startDate = moment("2015-05-26T02:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		
		$("#tagFromDate").children("input").val(moment("2015-05-25T00:00:00").format("YYYY-MM-DD 00:00:00"));	   
		
	}
	
	
	
	callTagUpdate();
	//FIN

	//Zoom 1 => Last hour with sizeSec=300(5 mins)
//	$('#taglasthour').on('click', function (e) {
//		console.log("Entra aqui");
//		var now = new Date;//Date.now();
//		console.log("now="+now);
//		var ini = now - (1000 * 3600); // 1 hour in millisecs
//		console.log("Last hour: now="+new Date(now).toISOString()+" ini="+new Date(ini).toISOString());		
//		callVolumeUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),300);
//	});
	
	//Zoom 2 => Last hours. Last 12 hours with sizeSec=1800(30 mins)
//	$('#taglasthours').on('click', function (e) {
//		var now = new Date;//Date.now();
//		var ini = now - (1000 * 3600 * 12); // 12 hour in millisecs
//		console.log("Last hours: now="+new Date(now).toISOString()+" ini="+new Date(ini).toISOString());
//		callVolumeUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),1800);
//	});
//	
	//Zoom 3 => Last weeks. Last 3 weeks with sizeSec=14400 (4 hours)
//	$('#taglastweeks').on('click', function (e) {
//		var now = new Date;//Date.now();
//		var ini = now - (1000 * 3600 * 24 * 7 * 3); //24=> 1 day, 7 => seven days= 1 week, 3 => 3 weeks 
//		console.log("Last hours: now="+now+" ini="+ini);
//		callVolumeUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),14400);
//	});
	
	//Last 24 hours with sizeSec=1800(30 mins)
	$('#taglastday').on('click', function (e) {
		//CLEAN DATE
		$('#tagFromDate').find('input:text').val('');
		
		var now = moment(); //new Date;//Date.now();
		var ini = now - (1000 * 3600 * 24); // 24 hour in millisecs		
		date=moment(ini);//new Date(ini);
		//date.setHours(02, 00, 00);
		startDate=date.utc().format("ddd MMM DD YYYY 02:00:00 z");//date.toGMTString();		
		console.log("Last hours:  date="+startDate);
		
		$("#tagFromDate").children("input").val(date.format("YYYY-MM-DD 00:00:00"));
		
		callTagUpdate();//startDate);
	});
	
	//Last 4 weeks with sizeSec=86400 (24 hours=86400 millisecs)
	$('#taglastmonth').on('click', function (e) {
		//CLEAN DATE
		$('#tagFromDate').find('input:text').val('');
		
		var now = moment().subtract(1,'days'); // new Date;//Date.now();
		var ini = now - (1000 * 3600 * 24 * 7 * 4); //24=> 1 day, 7 => seven days= 1 week, 4 => 4 weeks		
		date= moment(ini); //new Date(ini);
		//date.setHours(02, 00, 00);
		//startDate=date.toGMTString();
		startDate=date.utc().format("ddd MMM DD YYYY 02:00:00 z");//.
		
		console.log("Last hours: date="+startDate);
		
		$("#tagFromDate").children("input").val(date.format("YYYY-MM-DD 00:00:00"));
		
		callTagUpdate();//startDate);
	});
	
	
	//$("input[type='checkbox']").on('ifChanged',function() {
	$("input[type='checkbox'][name='tagparticipant']").on('ifClicked',function() {		
		var selected = $(this).val();
		var index = id.indexOf(selected); // If selected is on list of selecteds ids
        if($(this).iCheck('check')){ //is(':checked')){
        	$("input[type='checkbox'][name='tagparticipant']").iCheck('uncheck');
        	$(this).iCheck('check');
        	if (index <= -1) { // if it is not, add it
        		//id[id.length]=selected;
        		id[0]=selected;
        		console.log("Id seleccionado="+selected);
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
		//updateTagCloud();		
	});
	
	function updateTag(tagsSizes){
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
		  },'tags');
	}
	
//	function parseDate(input) {
//		  var parts = input.match(/(\d+)/g);		  
//		  return new Date(parts[0], parts[1]-1, parts[2]); // months are 0-based
//	}
	
	
//	$(document).ready(function() {
//        $('#tagfromDate').datetimepicker({
//            format: 'MM/dd/yyyy hh:mm:ss',
//            language: 'en',
//            pick12HourFormat: true
//        });
//    });		
	
	//$('#tagFromDate').datepicker({clearBtn:true})
	$('#tagFromDate').datetimepicker()
	.on('changeDate', function(ev){
		if(ev.date != null && (typeof ev.date != 'undefined')){
			//ev.date.getSeconds() => put 00
			startDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),'02', '00', '00'); //-2, considering it GMT+2
            var auxDate = new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),'00', '00', '00'); //to show
			console.log("startDate="+startDate+" auxDate="+auxDate);
			$('#tagFromDate').datetimepicker('setDate', auxDate);
			startDate=moment(startDate).utc().format("ddd MMM DD YYYY 00:00:00 UTC"); //format	
			console.log("startDate(to server)="+startDate);
			
		}
	});
    
	//PRUEBA
	
	//PRUEBA
	
	//$("#toDate").datepicker()
//	$('#toDate').datetimepicker()
//	.on("changeDate", function(ev){
//		endDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),ev.date.getHours(), ev.date.getMinutes(), ev.date.getSeconds());
//		endDate=endDate.toISOString(); //format	
//		if(startDate!=null&&startDate!='undefined'){
//			if(endDate<startDate){
//				//alert("End Date is less than Start Date");
//				BootstrapDialog.alert({
//					type: BootstrapDialog.TYPE_WARNING,
//		            title: 'Warning!!',
//		            message: 'End Date is less than Start Date'
//		        });
//				$("#fromDate").find('input:text').val("");
//				$('#toDate').find('input:text').val('');
//				startDate=null;
//				endDate=null;
//			}
//		}
//	});
	
	//Hide when date selected
//	$("#toDate").on('changeDate', function(ev){
//	    $(this).datetimepicker('hide');
//	    //   if(startDate!=null&&startDate!='undefined'){
//	    //   	$('#VolumeChart').html(''); //Clean chart
//	    //   	_chart.setData(json.setData(startDate,));	    	
//	    //   }
//    	updateVolume();
//	});
	
	//Hide when date selected
	$("#tagFromDate").on('changeDate', function(ev){
		//$(this).datepicker('hide');
		$(this).datetimepicker('hide');
    	//updateTagCloud(); PRUEBA BOTÓN
	});	
	
	//refresh chart when clicked
	$("#refresh").on('click', function(ev){
		updateTagCloud();
	});
	
	//Clear button event
	//$("#tagFromDate").datepicker()
	$("#tagFromDate").datetimepicker()
    .on('clearDate', function(e){
    	startDate=null;
    });
	
	$(document).ready(function() {
        $('#tagFromDate').datetimepicker({	      
        	format: 'yyyy-mm-dd HH:mm:ss',
            language: 'en',	           
            pick12HourFormat: true
       });
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
					json =data;
					console.log("ALP:"+json);
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
	
	// update the chart with the new data.
	function updateTagCloud() {
console.log("(updateTagCloud)startDate="+startDate);		
	  if((startDate!=null&&startDate!='undefined')){	
		  callTagUpdate();//startDate);
	  }
	}
	
	function callTagUpdate(){//(date){
		if(id.length>0){			  
			  var iD="";
			  for (var i=0; i < id.length; i++) {
				  if(""==iD)
					  iD=id[i];
				  else
					  iD=iD+','+id[i]; 
			  }
			  console.log("iD="+iD);	
			  //console.log("url="+url);			 
			  //var call = url+'tagdata?id='+iD+'&date='+date;
			  var call = 'tagdata?id='+iD+'&date='+startDate;//date;
			  console.log(call);
			  $("#loading-indicator").show();
			  setTimeout(function(){updateTag(data(call))},1000); //Delay necessary to show the loading-indicator
			  //graph.setData(data(call));
		}else{
			BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_WARNING,
	            title: 'Warning!!',
	            message: 'Please select a party first'		            	
	        });			
		}
	}
	
	$(function () {
		  $('#ddParticipants').dropdown('toggle');
	});
	
});

