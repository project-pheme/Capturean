//Volume Line Chart
$(document).ready(function() {
	
	var startDate;
	var frecuency=null;
	var id=[];
	var clickedALL=false;	
	var partNames=[];
	var yKeysVol=[];
	var yKeysSent=[];
	var colors=[];	
	var dates=[];
	var idsT=[];
	
//	var url= document.getElementById('myUrl').getAttribute('data-value');
//	console.log("myUrl="+url);
	
	//var numIds= document.getElementById('numIds').getAttribute('data-value');
	//console.log("numIds="+numIds);
	
	//Party names
	for(var i=0; document.getElementById('partName'+i)!= null;i++){
		var aux = document.getElementById('partName'+i).getAttribute('data-value');
		console.log("Participante" + i + " = " + aux);	
		partNames[i]=aux;
		yKeysVol[i]="volume"+i;
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
	
	//Ids totales colors
	for(var i=0; document.getElementById('idsT'+i)!= null;i++){
		var aux = document.getElementById('idsT'+i).getAttribute('data-value');
		console.log("ids" + i + " = " + aux);	
		idsT[i]=aux;		
	}
	console.log("idsT="+idsT);
	
	//PRUEBA
	startDate = moment("2015-11-02T21:00:00").utc().format("ddd DD MMM YYYY HH:mm:ss z");
	//startDate = moment("2015-11-02T02:00:00").utc().format("ddd DD MMM YYYY");	//"Tue 03 Nov 2015";
	//$("#fromDate").children("input").val(moment("2015-11-02T02:00:00").format("YYYY-MM-DD 00:00:00"));
	$("#fromDate").children("input").val(moment("2015-11-02T21:00:00").format("YYYY-MM-DD HH:mm:ss z"));
	frecuency="minutes5";//"hourly";
	//FIN PRUEBA

	
	//INICIO: On Load
	for(var i=0; document.getElementById('idsSel'+i)!= null;i++){
		var aux = document.getElementById('idsSel'+i).getAttribute('data-value');
		var aux2 = startDate;
		console.log("ids Selected" + i + " = " + aux);	
		id[i]=aux;
		dates[i]=aux2;
	}

	callVolumeUpdate(); //1800
	callSentimentUpdate();
	
	function callVolumeUpdate(){
		if((id.length>0)&&(frecuency!=null)&&(frecuency!=undefined)){			  
			  var iD="";
			  for (var i=0; i < id.length; i++) {
				  if(""==iD)
					  iD=id[i];
				  else
					  iD=iD+','+id[i]; 
			  }
			  console.log("iD="+iD);
			  console.log("id.length="+id.length);
			  console.log("dates="+dates);
			  var call = 'volumedebatedata?ids='+iD+'&dates='+dates+'&sizeSec='+frecuency+'&numIds='+id.length;
			  console.log(call);
			  $("#loading-indicator").show();
			  setTimeout(function(){graph.setData((data(call,"v")).sort(predicatBy("hour")))},1000); //Delay necessary to show the loading-indicator
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
	
	function callSentimentUpdate(){
		console.log("callSentimentUpdate");
		//if((idS.length>0)&&(sec!=null)&&(sec!=undefined)){			  
		if((id.length>0)&&(frecuency!=null)&&(frecuency!=undefined)){
			  var iD="";
			  //for (var i=0; i < idS.length; i++) {
			  for (var i=0; i < id.length; i++) {		  
				  if(""==iD){
					  //iD=idS[i];
					  iD=id[i];
				  }else{
					  //iD=iD+','+idS[i];
					  iD=iD+','+id[i];
				  }
			  }
			  console.log("iDS="+iD);			  
			  //var call = 'http://localhost:8080/volumedata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec;
			  //var call = url+'sentimentdata?id='+iD+'&fInit='+fIni+'&fEnd='+fFin+'&sizeSec='+sec;
			  var call = 'sentimentdebatedata?ids='+iD+'&dates='+dates+'&sizeSec='+frecuency+'&numIds='+id.length;
			  console.log(call);
			  $("#loading-indicator").show();			  
			  setTimeout(function(){graphSent.setData(data(call,"s").sort(predicatBy("hour")))},1000); //Delay necessary to show the loading-indicator			  
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
	
	$("input[type='checkbox'][name='participant']").on('ifClicked',function() {		
		var selected = $(this).val();
		var date = startDate;
		var index = id.indexOf(selected); // If selected is on list of selecteds ids		
        if($(this).iCheck('check')){ //is(':checked')){        	
        	$(this).iCheck('check');
			if(!date==""){
				if($(this).iCheck('check')){ //is(':checked')){
		        	if (index <= -1) { // if it is not, add it
		        		id[id.length]=selected;
		        		dates[dates.length]=date;		        		
		        	}
		        }				
			}else{
				BootstrapDialog.alert({
					type: BootstrapDialog.TYPE_WARNING,
		            title: 'Aviso!!',
		            message: 'Debe elegir una fecha'
		        });
			}
        }
        if($(this).iCheck('uncheck')){//else{
        	if (index > -1) { // if it is, remove it
        		id.splice(index, 1);
        		dates.splice(index, 1);        		
			}        	
        }
        
        $text = $('<div></div>');
		for(var i=0;i<id.length;i++){
			for(var j=0;j<idsT.length;j++){
				if(id[i]==idsT[j]){
					var d = (new Date(dates[i])).toString();
					$text.append('<tr><td><b> '+partNames[j]+' </b></td><td>'+': '+'</td><td> '+d.substring(0,d.indexOf("GMT"))+' </td></tr>');
					//$text.append('11');
				}
			}
		}				
		BootstrapDialog.show({					
			title: 'Seleccionados',
            message: $text,
        });
        console.log("IDS:");
		for(var i=0, n=id.length;i<n;i++) {
			console.log("Id="+id[i]);
		}
	});
		

//	$(document).ready(function() {
//        //$('#fromDate').datetimepicker({        	
//		$('#fromDate').datepicker({
//        	//format: 'MM/dd/yyyy HH:ii:00P',        	
//        	format: 'yyyy-mm-dd',
//            language: 'en',
//            pick12HourFormat: true,
//            showTodayButton: true  
//        });
//    });
	
//	$('#fromDate').datepicker({clearBtn:true})
//	//$('#fromDate').datetimepicker()
//	.on('changeDate', function(ev){
//		if(typeof ev.date != 'undefined'){
//			//startDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),02,00,00);//ev.date.getHours(), ev.date.getMinutes(), ev.date.getSeconds());			
//			startDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate());
//			console.log("ANA=>(changeDate)startDate="+startDate);
//			startDate=startDate.toGMTString();//toISOString(); //format			
//			console.log("ANA=>(changeDate)startDate GMT format="+startDate);
//		}
//	});
	
//	$('#fromDate').datepicker({clearBtn:true})
//	.on('changeDate', function(ev){
//		if(typeof ev.date != 'undefined'){			
//			startDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),02,00,00);//ev.date.getHours(), ev.date.getMinutes(), ev.date.getSeconds());
//			console.log("ANA=>(changeDate)startDate="+startDate);
//			startDate=startDate.toGMTString();//toISOString(); //format			
//			console.log("ANA=>(changeDate)startDate GMT format="+startDate)
//		}
//	});
	
	$('#fromDate').datetimepicker()
	.on('changeDate', function(ev){
		if(ev.date != null && (typeof ev.date != 'undefined')){
			//startDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),'02', '00', '00'); //-2, considering it GMT+2
            //var auxDate = new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),'00', '00', '00'); //to show
			//console.log("startDate="+startDate+" auxDate="+auxDate);
			//$('#fromDate').datetimepicker('setDate', auxDate);			
			//startDate=moment(startDate).utc().format("ddd DD MMM YYYY"); //format	
			//console.log("startDate(to server)="+startDate);		
			
			var TimeZoned = new Date(ev.date.setTime(ev.date.getTime() + (ev.date.getTimezoneOffset() * 60000)));			
			$('#fromDate').datetimepicker('setDate', TimeZoned);			
			startDate = moment(TimeZoned).utc().format("ddd DD MMM YYYY HH:mm:ss z");	
		}
	});
	
	//refresh chart when clicked
	$("#refresh").on('click', function(ev){
		updateVolume();
		updateSentiment();
	});
	
	
	//Hide when date selected
//	$("#fromDate").on('changeDate', function(ev){
//		$(this).datepicker('hide');
//	});	
	
	$("#fromDate").on('changeDate', function(ev){
		$(this).datetimepicker('hide');
	});	
	
	//Clear button event
//	$("#fromDate").datepicker()
//    .on('clearDate', function(e){
//    	startDate=null;
//    });
	
	$("#fromDate").datetimepicker()
    .on('clearDate', function(e){
    	startDate=null;
    });
	
	function data(url,value) {
		var json;
		console.log("En data url="+url);
		if(url!=""){
			//$('#myModal').modal('show'); //PRUEBA 
			console.log("Entramos en data");
			$.ajax({
				'type': 'GET',
				'async': false,
				'global': false,
				'url': url, 
				'dataType': "json",				
				'success': function (data) {
					console.log("success data="+data);
					if(url.indexOf("sentimentdebatedata") > -1){ //When sentiment(the second) chart is shown
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
	var graph = Morris.Area({
	    element: 'VolumeAreaChart',
	    data: data("").sort(predicatBy("hour")),
	    xkey: 'hour',
	    ykeys: yKeysVol,
	    labels: partNames,
	    parseTime: false,
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
	    data: data("").sort(predicatBy("hour")),
	    xkey: 'hour',
	    ykeys: yKeysSent,
	    labels: partNames,
	    parseTime: false,
	    lineColors:colors,
	    smooth: false,
	    stacked: false, //true,
        hideHover: 'auto',
	    resize: true,
	    redraw: true,
	    fillOpacity: 0.6,
	    pointSize: 0,
        behaveLikeLine: true
	});
	
	//Order json by hour
	function predicatBy(prop){		
	   return function(a,b){
		  //alert("a="+a[prop]+" b="+b[prop]);  
	      if( a[prop] > b[prop]){
	    	  //alert("a mayor que b");
	          return 1;
	      }else if( a[prop] < b[prop] ){
	    	  //alert("b mayor que a");
	          return -1;
	      }
	      return 0;
	   }
	}
	
	
	// update the chart with the new data.
	function updateVolume() {
	  //if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')&&
	  //			 (frecuency!=null&&frecuency!='undefined')){
	  if(startDate!=null&&startDate!='undefined'){
		  callVolumeUpdate();
	  }
	}
	
	// update the volumen chart with the new data.
	function updateSentiment() {
	  //if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')&&
	  //			 (frecuency!=null&&frecuency!='undefined')){
	  if(startDate!=null&&startDate!='undefined'){
		  callSentimentUpdate();
	  }
	}
	//FIN
	
//	$(window).resize(function() {
//		graph.redraw();
//	});
	
	$(function () {
		  $('#ddParticipants').dropdown('toggle');
	});
	
});

