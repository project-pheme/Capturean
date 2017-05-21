var div = '<div class="form-group" id="form-group-1"> \
	<label class="col-sm-1 control-label" for="textinput">Type</label> \
	<div class="col-sm-2"> \
		<input type="text" placeholder="Twitter" id="type-1" name="type-1" class="form-control" \
			disabled> \
	</div> \
\
	<label class="col-sm-2 control-label" for="textinput">Keywords</label> \
	<div class="col-sm-7"> \
		<input type="text" placeholder="keywords"  id="keywords-1" name="keywords-1" \
			class="form-control"> \
	</div> \
</div>';

//counter = 1;

$(document).ready(function() {
	//set counter
	counter = $(".datasource").length;

	$('#delete-button').off().click(function () {
		var r = window.confirm("Are you sure you want to remove this data channel?")
		if (r == true) {
			return true;		
		} else {
			return false;
		}
	});
	
	//PRUEBA ANA
//	var el = document.getElementById('iS');
//	el.onclick = showIS;
//	
//	var windowObjectReferenceIS;
//	var strWindowFeatures = "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes";
//
//	function showIS() {
//	  windowObjectReferenceIS = window.open("http://163.117.135.92:3838/capture/graph1/", "Indice de Sentimiento por Hora", strWindowFeatures);
//	}
//	
//	var el = document.getElementById('vT');
//	el.onclick = showVT;
//	
//	var windowObjectReferenceVT;
//	var strWindowFeatures = "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes";
//
//	function showVT() {
//		windowObjectReferenceVT = window.open("http://163.117.135.92:3838/capture/graph2/", "Volumen Twitters", strWindowFeatures);
//	}
//	
//	var el = document.getElementById('tC');
//	el.onclick = showTC;
//	
//	var windowObjectReferenceTC;
//	var strWindowFeatures = "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes";
//
//	function showTC() {
//		windowObjectReferenceTC = window.open("http://163.117.135.92:3838/capture/graph3/", "Tag Cloud", strWindowFeatures);
//	}

//	var endDate;
//	var startDate;
//	var frecuency;
//	var id=[];
//	var clickedALL=false;
//	
//	//Zoom 1 => Last hour with sizeSec=300(5 mins)
//	$('#lasthour').on('click', function (e) {
//		var now = Date.now();
//		var ini = now - (1000 * 3600); // 1 hour in millisecs
//		console.log("Last hours: now="+new Date(now).toISOString()+" ini="+new Date(ini).toISOString());
//		callUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),300);
//	});
//	
//	//Zoom 2 => Last hours. Last 12 hours with sizeSec=1800(30 mins)
//	$('#lasthours').on('click', function (e) {
//		var now = Date.now();
//		var ini = now - (1000 * 3600 * 12); // 12 hour in millisecs
//		console.log("Last hours: now="+new Date(now).toISOString()+" ini="+new Date(ini).toISOString());
//		callUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),1800);
//	});
//	
//	//Zoom 3 => Last weeks. Last 3 weeks with sizeSec=14400 (4 hours)
//	$('#lastweeks').on('click', function (e) {
//		var now = Date.now();
//		var ini = now - (1000 * 3600 * 24 * 7 * 3); //24=> 1 day, 7 => seven days= 1 week, 3 => 3 weeks 
//		console.log("Last hours: now="+now+" ini="+ini);
//		callUpdate(new Date(ini).toISOString(),new Date(now).toISOString(),14400);
//	});
//	
//	function callUpdate(fIni, fFin,sec){
//		if(id.length>0){	
//			  var iD="";
//			  for (var i=0; i < id.length; i++) {
//				  if(""==iD)
//					  iD=id[i];
//				  else
//					  iD=iD+','+id[i]; 
//			  }
//			  console.log("iD="+iD);			  
//			  var call = 'http://localhost:8080/volumedata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec;
//			  console.log(call);
//			  graph.setData(data(call));
//		}else{
//			BootstrapDialog.alert({
//				type: BootstrapDialog.TYPE_WARNING,
//	            title: 'Warning!!',
//	            message: 'Please select a party first'		            	
//	        });
//			graph.setData([ {
//			  date: 0,
//			  volume : 0
//			}]);
//		}		
//	}
//	
//	$('#ALL').on('ifClicked',function(event) {
//		clickedALL=true;
//		console.log("clickedALL true");
//	});
//	
//	//If ALL is checked/unchecked, check/uncheck all 
//	$('#ALL').on('ifChanged',function() {
//		if(clickedALL){
//			checkboxes = document.getElementsByName('participant');
//			console.log("checkboxes="+checkboxes.length);
//			for(var i=0, n=checkboxes.length;i<n;i++) {
//				if($('#ALL').is(':checked')){
//					//checkboxes[i].checked = true;	
//					$("input[type='checkbox']").iCheck('check'); 
//					var value=checkboxes[i].value; // add checkbox id to id's array
//					var index = id.indexOf(value);
//					if (index <= -1) { // if it is not, add it
//			       		id[id.length]=value;
//			       	}
//				}else{
//					//checkboxes[i].checked = false;
//					$("input[type='checkbox']").iCheck('uncheck');
//					var index = id.indexOf(checkboxes[i].value); //remove checkbox id from id's array
//					if (index > -1) {
//						id.splice(index, 1);
//					}
//				}
//			}		
//			console.log("IDS:");
//			for(var i=0, n=id.length;i<n;i++) {
//				console.log("Id="+id[i]);
//			}
//			update();
//		}
//	});
//
//	//$("input[type='checkbox']").on('ifChanged',function() {
//	$("input[type='checkbox'][name='participant']").on('ifClicked',function() {
//		console.log("Entra en input not ALL!");
//		clickedALL=false;
//		if($("input[type='checkbox'][name='isALL']").iCheck('check')){
//			$("input[type='checkbox'][name='isALL']").iCheck('uncheck');
//		}
//		
//		var selected = $(this).val();
//		var index = id.indexOf(selected); // If selected is on list of selecteds ids
//        if($(this).iCheck('check')){ //is(':checked')){
//        	if (index <= -1) { // if it is not, add it
//        		id[id.length]=selected;
//        	}
//        }
//        if($(this).iCheck('uncheck')){//else{
//        	if (index > -1) { // if it is, remove it
//        		id.splice(index, 1);
//			}
//        }
//      
//        console.log("IDS:");
//		for(var i=0, n=id.length;i<n;i++) {
//			console.log("Id="+id[i]);
//		}
//		update();
//	});
//	
//	
////	$("input[type='checkbox']").not('#ALL').change( function() {
////		//If ALL is checked, when another checkbox is unchecked, uncheck ALL too
////        $('#ALL').removeAttr('checked'); 
////        
////        var selected = $(this).val();
////        var index = id.indexOf(selected); // If selected is on list of selecteds ids
////        if($(this).is(':checked')){
////        	if (index <= -1) { // if it is not, add it
////        		id[id.length]=selected;
////        	}
////        }else{
////        	if (index > -1) { // if it is, remove it
////			    id.splice(index, 1);
////			}
////        }
////        
////        console.log("IDS:");
////		for(var i=0, n=id.length;i<n;i++) {
////			console.log("Id="+id[i]);
////		}
////		update();
////    });	
//	
//	function parseDate(input) {
//		  var parts = input.match(/(\d+)/g);		  
//		  return new Date(parts[0], parts[1]-1, parts[2]); // months are 0-based
//	}
//	
//	//Get frequency selected
//	$(document).on('click', '.dropdown-menu li a', function () {
//	   console.log("Entra aqui="+$(this).text());
//	   if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')){
//		   console.log(endDate);
//		   console.log(startDate);		   
//		   var timeDiff = Math.abs((new Date(endDate)).getTime() - (new Date(startDate)).getTime());
//		   console.log(timeDiff);
//		   var diffDays = timeDiff / (1000 * 3600 * 24);
//		   console.log(diffDays);
//		   var diffWeeks = timeDiff / (1000 * 3600 * 24 * 7);
//		   console.log(diffWeeks);
//		   var diffMonths = timeDiff / (1000 * 3600 * 24 * 30);
//		   console.log(diffMonths);
//		   var diffQuarts = timeDiff / (1000 * 3600 * 24 * 30 * 3);
//		   console.log(diffQuarts);
//		   var diffYears = timeDiff / (1000 * 3600 * 24 * 365);
//		   console.log(diffYears);
//	   
//		   switch (true) {
//		    case /Daily/.test($(this).text())://Daily
//		    	$(".btn:first-child").text($(this).text());//Show selected
//		        $(".btn:first-child").val($(this).text());
//		        if(diffDays >= 1){
//		        	frecuency = "86400"; //secs in one day
//		        }else{
//		        	BootstrapDialog.alert({
//						type: BootstrapDialog.TYPE_WARNING,
//			            title: 'Warning!!Option not avaliable',
//			            message: 'The difference between Start Date and End Date is less than one day'		            	
//			        });
//		        	$('#fromDate').find('input:text').val('');
//					$('#toDate').find('input:text').val('');
//					$(".btn:first-child").text("Frecuency"); // Initial value
//					$(".btn:first-child").val("");
//		        }
//		        break;
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
//			}
//		   update();
//	   }else{
//		   BootstrapDialog.alert({
//				type: BootstrapDialog.TYPE_WARNING,
//	            title: 'Warning!!',
//	            message: 'Please, first select a start and end date!'		            	
//	        });
//	   }
//	});	
//	
//	$(document).ready(function() {
//        $('#fromDate').datetimepicker({
//            format: 'MM/dd/yyyy hh:mm:ss',
//            language: 'en',
//            pick12HourFormat: true
//        });
//    });	
//	
//	//$('#fromDate').datepicker()
//	$('#fromDate').datetimepicker()
//	.on('changeDate', function(ev){
//		startDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),ev.date.getHours(), ev.date.getMinutes(), ev.date.getSeconds());
//		startDate=startDate.toISOString(); //format		
//		if(endDate!=null&&endDate!='undefined'){
//			if(endDate<startDate){
//				//alert("End Date is less than Start Date");
//				BootstrapDialog.alert({
//					type: BootstrapDialog.TYPE_WARNING,
//		            title: 'Warning!!',
//		            message: 'End Date is less than Start Date'
//		        });
//				$('#fromDate').find('input:text').val('');
//				$('#toDate').find('input:text').val('');
//				startDate=null;
//				endDate=null;
//			}
//		}
//	});
//    
//	
//	//$("#toDate").datepicker()
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
//	
//	//Hide when date selected
//	$("#toDate").on('changeDate', function(ev){
//	    $(this).datetimepicker('hide');
//	    //   if(startDate!=null&&startDate!='undefined'){
//	    //   	$('#VolumeChart').html(''); //Clean chart
//	    //   	_chart.setData(json.setData(startDate,));	    	
//	    //   }
//    	update();
//	});
//	//Hide when date selected
//	$("#fromDate").on('changeDate', function(ev){
//	    $(this).datetimepicker('hide');	
//	    //if(endDate!=null&&endDate!='undefined'){
//	    //	$('#VolumeChart').html(''); //Clean chart
//	    //	_chart.setData(json.setData());
//	    //}
//    	update();
//	});
//	
//	function data(url) {
//		var json;
//		if(url!=""){
//			$.ajax({
//				'type': 'GET',
//				'async': false,
//				'global': false,
//				'url': url, 
//				'dataType': "json",				
//				'success': function (data) {
//					json =data
//					//json =[{"volume":0,"volume1":1,"date":"2015-04-22"},{"volume":1,"volume1":2,"date":"2015-04-05"},{"volume":2,"volume1":3,"date":"2015-04-10"},{"volume":3,"volume1":4,"date":"2015-04-11"},{"volume":4,"volume1":5,"date":"2015-04-06"},{"volume":5,"volume1":6,"date":"2015-04-18"},{"volume":6,"volume1":7,"date":"2015-04-09"},{"volume":7,"volume1":8,"date":"2015-04-16"},{"volume":8,"volume1":9,"date":"2015-04-20"},{"volume":9,"volume1":10,"date":"2015-04-04"},{"volume":10,"volume1":11,"date":"2015-04-02"},{"volume":11,"volume1":12,"date":"2015-04-12"},{"volume":12,"volume1":13,"date":"2015-04-03"},{"volume":13,"volume1":14,"date":"2015-03-31"},{"volume":14,"volume1":15,"date":"2015-04-13"},{"volume":15,"volume1":16,"date":"2015-04-17"},{"volume":16,"volume1":17,"date":"2015-04-21"},{"volume":17,"volume1":18,"date":"2015-04-14"},{"volume":18,"volume1":19,"date":"2015-04-15"},{"volume":19,"volume1":20,"date":"2015-04-08"},{"volume":20,"volume1":25,"date":"2015-04-07"},{"volume":21,"volume1":24,"date":"2015-04-19"},{"volume":22,"volume1":23,"date":"2015-04-01"}]
//				},
//				'error': function(jqXHR, textStatus, errorThrown){
//					alert("Error loading chart:"+errorThrown);
//				}
//			});
//		}else{
//			//json=[{"volume":0,"date":"2015-04-22"},{"volume":1,"date":"2015-04-05"},{"volume":2,"date":"2015-04-10"},{"volume":3,"date":"2015-04-11"},{"volume":4,"date":"2015-04-06"},{"volume":5,"date":"2015-04-18"},{"volume":6,"date":"2015-04-09"},{"volume":7,"date":"2015-04-16"},{"volume":8,"date":"2015-04-20"},{"volume":9,"date":"2015-04-04"},{"volume":10,"date":"2015-04-02"},{"volume":11,"date":"2015-04-12"},{"volume":12,"date":"2015-04-03"},{"volume":13,"date":"2015-03-31"},{"volume":14,"date":"2015-04-13"},{"volume":15,"date":"2015-04-17"},{"volume":16,"date":"2015-04-21"},{"volume":17,"date":"2015-04-14"},{"volume":18,"date":"2015-04-15"},{"volume":19,"date":"2015-04-08"},{"volume":20,"date":"2015-04-07"},{"volume":21,"date":"2015-04-19"},{"volume":22,"date":"2015-04-01"}];
//			json= [ {
//				date : '0',
//				volume : 0,
//			}];
//		}
//		//alert(json); //BORRAR
//		return json;
//	}
//	
//	// create the morris chart. 
//	var graph = Morris.Line({
//	    element: 'VolumeChart',
//	    data: data(""),
//	    xkey: 'date',
//	    ykeys: ['volume0','volume1','volume2','volume3'],
//	    labels: ['PP','PSOE', 'Podemos','Ciutadans'],
//	    lineColors:['#3366CC','#FF0000','#990099','#FF3300'],
//	    smooth: false
//	});
//	
//	// update the chart with the new data.
//	function update() {
//	  if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')&&
//				 (frecuency!=null&&frecuency!='undefined')){	
//		  callUpdate(startDate,endDate,frecuency);
////		  if(id.length>0){	
////			  var iD="";
////			  for (var i=0; i < id.length; i++) {
////				  if(""==iD)
////					  iD=id[i];
////				  else
////					  iD=iD+','+id[i]; 
////			  }
////			  console.log("iD="+iD);
////			  // called on the returned Morris.Line object.
//////			  if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')&&
//////				 (frecuency!=null&&frecuency!='undefined')){			  
////				  //var call = 'http://95.211.84.95:8080/CaptureREST/rest/datachannel/'+id[i]+'/twitter?fInit='+startDate+'&fEnd='+endDate+'&sizeSec='+frecuency;
////				  var call = 'http://localhost:8080/volumedata?id='+iD+'&fInit='+startDate+'&fEnd='+endDate+'&sizeSec='+frecuency;
////				  console.log(call);
////				  graph.setData(data(call));
////				  //graph.setData(data('https://dashboard-citypulse.atosresearch.eu/admin/datachannel/rest/datachannel/898a50ab-ca98-4284-9fcc-82723f2d7edf/twitter?fInit='+startDate+'&fEnd='+endDate+'&sizeSec='+frecuency));
////				  //graph.setData(data('https://dashboard-citypulse.atosresearch.eu/admin/datachannel/rest/datachannel/898a50ab-ca98-4284-9fcc-82723f2d7edf/twitter?fInit=2015-03-29T12:23:00.000Z&fEnd=2015-03-30T12:23:00.000Z&sizeSec=3600'));
////			  //}
////			}else{ //id.length<=0 => clean
//////				if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')&&
//////						 (frecuency!=null&&frecuency!='undefined')){
////					graph.setData([ {
////						date: 0,
////						volume : 0
////					}]);
////				//}
////			}
//	  }
//	}
	
	//FIN PRUEBA
	
	$('#remove-button').on('click', function (e) {
		if (counter > 1) {
			
		    //console.info("remove" + '#form-group-' + counter);
			
			$('#form-group-' + counter).remove();
			counter--;
			
		}
		
		return false;
	});
	
	$('#set-date').on('click', function (e) {
		var d = new Date();
		
		var year = d.getFullYear();
		var month = "0" + (d.getMonth()+1);
		
		var day = "0" + (d.getDate());
		var hour = "0" + (d.getHours());
		var minute = "0" + (d.getMinutes());
		var second = "0" + (d.getSeconds());
		
		var output = "" + year + "-" + month.substr(-2) + "-" + day.substr(-2) + " " + hour.substr(-2) + ":" + minute.substr(-2) + ":" + second.substr(-2) + ".000";
		
		$("input#dc-startdate").val(output);
		
		return false;
	});
	

	$('#add-button').on('click', function (e) {
		newid = counter + 1;
		elem = $(div);
		elem.attr('id', 'form-group-' + (newid));
		elem.find("#type-1").attr("name", "type-" + newid);
		elem.find("#type-1").attr("id", "type-" + newid);
		elem.find("#keywords-1").attr("name", "keywords-" + newid);
		elem.find("#keywords-1").attr("id", "keywords-" + newid);
		
	    //console.info("add" + '#form-group-' + counter);
		if ($('#form-group-' + counter).size() == 0) {
			//in unlikely event of no data sources, we need to create first row and place it in the form
			var index = $('.form-group').size() - 2;
			$($('.form-group')[index]).after(elem);
			
		} else {
			$('#form-group-' + counter).after(elem);
		}
		counter++;
		return false;
	});
	
	console.info("loaded!");
	
});
