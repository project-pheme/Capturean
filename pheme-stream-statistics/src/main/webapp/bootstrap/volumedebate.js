//Volume Line Chart
$(document).ready(function() {
	
	var startDate;
	var frecuency=null;
	var id=[];
	var clickedALL=false;	
	var partNames=[];
	var yKeysVol=[];
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
	//startDate = moment("2015-11-02T02:00:00").utc().format("ddd DD MMM YYYY");	//"Tue 03 Nov 2015";
	startDate = moment("2015-11-02T21:00:00").utc().format("ddd DD MMM YYYY HH:mm:ss z");	//"Tue 03 Nov 2015";
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
			  setTimeout(function(){graph.setData((data(call)).sort(predicatBy("hour")))},1000); //Delay necessary to show the loading-indicator
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
			//ev.date.getSeconds() => put 00
			//startDate=new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),'02', '00', '00'); //-2, considering it GMT+2
            //var auxDate = new Date(ev.date.getFullYear(),ev.date.getMonth(),ev.date.getDate(),'00', '00', '00'); //to show
			//console.log("startDate="+startDate+" auxDate="+auxDate);
			//$('#fromDate').datetimepicker('setDate', auxDate);			
			//startDate=moment(startDate).utc().format("ddd DD MMM YYYY HH:mm:ss z");
			//console.log("startDate(to server)="+startDate);
			
			var TimeZoned = new Date(ev.date.setTime(ev.date.getTime() + (ev.date.getTimezoneOffset() * 60000)));			
			$('#fromDate').datetimepicker('setDate', TimeZoned);			
			startDate = moment(TimeZoned).utc().format("ddd DD MMM YYYY HH:mm:ss z");	
		}
	});
	
	//refresh chart when clicked
	$("#refresh").on('click', function(ev){
		updateVolume();
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
	    data: data("").sort(predicatBy("hour")),
	    xkey: 'hour',
	    ykeys: yKeysVol,
	    labels: partNames,
	    //xLabels: "hour",
	    parseTime: false,
	    lineColors:colors,
	    smooth: false,
	    stacked: true,
        hideHover: 'auto',
        pointSize: 0,
	    resize: true,
	    redraw: true
	});
	
	//Order json by hour
	function predicatBy(prop){
	   return function(a,b){
	      if( a[prop] > b[prop]){
	          return 1;
	      }else if( a[prop] < b[prop] ){
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
	//FIN
	
	$(window).resize(function() {
		graph.redraw();
	});
	
	$(function () {
		  $('#ddParticipants').dropdown('toggle');
	});
	
});

