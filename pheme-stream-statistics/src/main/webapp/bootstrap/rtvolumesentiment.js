//Volume Area and Sentiment Line Chart
$(document).ready(function() {
	
	var endDate;
	var startDate;
	var frecuency;
	var vstartDate;
	var idS=[];
	var clickedALLV=false;
	var clickedALLS=false;
	var partNames=[];
	var partNamesLabels=['Time'];
	var yKeysVol=[];
	var yKeysSent=[];
	var colors=[];
	var isBrands;
	var datosV = [];
	var datosS = [];
	var fixedDelay = 180000; //3 mins
	var videoID;//="GV9K7jK-BL4";
	
	videoID= document.getElementById('idVideo').getAttribute('data-value');
	
	var numIds= document.getElementById('numIds').getAttribute('data-value');
	console.log("numIds="+numIds);
	
	if(document.getElementById('isBrands'))
		isBrands = document.getElementById('isBrands').getAttribute('data-value');
	console.log("isBrands="+isBrands);
	
	//Party names
	for(var i=0; document.getElementById('partName'+i)!= null;i++){
		var aux = document.getElementById('partName'+i).getAttribute('data-value');
		console.log("Participante" + i + " = " + aux);	
		partNames[i]=aux;
		yKeysVol[i]="volume"+i;  //PRUEBA ANA
		yKeysSent[i]="sentiment"+i;
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
		idS[i]=aux;
	}
	
	//Debate de 21:00 a 23:59, hora española
	//endDate = moment("2015-12-07T23:59:59").utc().format("ddd MMM DD YYYY HH:mm:ss z");
	//startDate = moment("2015-12-07T21:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");	
		
	//$("#sfromDate").children("input").val(moment("2015-12-07T21:00:00").format("YYYY-MM-DD HH:mm:ss"));
	//$("#stoDate").children("input").val(moment("2015-12-07T23:59:59").format("YYYY-MM-DD HH:mm:ss"));
	
	
	if(document.getElementById('isRTCara')){
		endDate = moment("2015-12-15T02:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		startDate = moment("2015-12-14T20:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		vstartDate = moment("2015-12-14T22:00:00")
			
		$("#sfromDate").children("input").val(moment("2015-12-14T20:00:00").format("YYYY-MM-DD HH:mm:ss"));
		$("#stoDate").children("input").val(moment("2015-12-15T02:00:00").format("YYYY-MM-DD HH:mm:ss"));
	}else{
		endDate = moment("2015-12-07T23:59:59").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		startDate = moment("2015-12-07T21:00:00").utc().format("ddd MMM DD YYYY HH:mm:ss z");
		vstartDate = moment("2015-12-07T22:00:00"); //Hora inicio debate
			
		$("#sfromDate").children("input").val(moment("2015-12-07T21:00:00").format("YYYY-MM-DD HH:mm:ss"));
		$("#stoDate").children("input").val(moment("2015-12-07T23:59:59").format("YYYY-MM-DD HH:mm:ss"));
		
	}

		
	frecuency="minutes1";
	
	//frecuency="hourly";
	callVolumeUpdate(startDate,endDate,frecuency);//1800); //sizeSec=1800(30 mins)
	//callSentimentUpdate(startDate,endDate,frecuency);//1800);

	
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
			  
			  var dI = new moment(startDate);
			  var dF = new moment(dI + (60*1000))			 
			  var end = new moment(endDate);
			  var now = new moment();
		 
			  if(dF>now){
				  dF=now;
			  }
			  
			  //First time
		      console.log('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);
		      var yV = dataRT('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);//Math.random();
		      
		      console.log('rtsentimentdata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);
		      var yS = dataRT('rtsentimentdata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);//Math.random();
		      
		      x=new Date(dI);
			  console.log("x="+x+" yV="+yV);			  
			  var dV=[x];
			  var kV=1;
			  var keyV;
			  for(keyV in yV) {
				  dV[kV]=yV[keyV];
				  kV++;
			  }
			  datosV.push(dV);	
			  
			  console.log("x="+x+" yS="+yS);
			  var dS=[x];
			  var kS=1;
			  var keyS;
			  for(keyS in yS) {
				  dS[kS]=yS[keyS];
				  kS++;
			  }			  
			  datosS.push(dS);
			  
		      //datos.push([x, 50]);
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
		    		  var yV = dataRT('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);//Math.random();
		    		 
				      console.log('rtsentimentdata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);
				      var yS = dataRT('rtsentimentdata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);//Math.random();

		    		  x=new Date(dI);
		    		  var dV=[x];
		    		  var kV=1;
		    		  var keyV;
		    		  for(keyV in yV) {
		    			  dV[kV]=yV[keyV];
		    			  kV++;
		    		  }
		    		  
		    		  var dS=[x];
					  var kS=1;
					  var keyS;
					  for(keyS in yS) {
						  dS[kS]=yS[keyS];
						  kS++;
					  }	
		    		  
		    		  datosV.push(dV);
		    		  datosS.push(dS);
		    		  
		    		  graphVol.updateOptions( { 'file': datosV } );
		    		  graphSent.updateOptions( { 'file': datosS } );
		    		  
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
			graphVol.setData([ {
			  date: 0,
			  volume : 0
			}]);
			graphSent.setData([ {
				  date: 0,
				  volume : 0
			}]);
		}
	}

//	function callSentimentUpdate(fIni, fFin,sec){
//		console.log("callSentimentUpdate");
//		if((idS.length>0)&&(sec!=null)&&(sec!=undefined)){			  
//			  var iD="";
//			  for (var i=0; i < idS.length; i++) {
//				  if(""==iD)
//					  iD=idS[i];
//				  else
//					  iD=iD+','+idS[i]; 
//			  }
//			  console.log("iDS="+iD);	
//			  
//			  
//			  var dI = new moment(startDate);
//			  var dF = new moment(dI + (60*1000))			 
//			  var end = new moment(endDate);
//			  var now = new moment();
//			  
//			  
//			  //First time
//		      console.log('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);
//		      var y = dataRT('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);//Math.random();
//		      x=new Date(dI);
//			  console.log("x="+x+" y="+y);
//			  var d=[x];
//			  var k=1;
//			  var key;
//			  for(key in y) {
//				  d[k]=y[key];
//				  k++;
//			  }
//			  datos.push(d);	
//		      //datos.push([x, 50]);
//		      dI=dF;
//		      dF=new moment(dF + (60*1000));
//
//		      window.intervalId = setInterval(function() {
////			  while(dF <= end){
////			      console.log('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);
////			      var y = data('rtvolumedata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);//Math.random();
////			      x=new Date(dI);
////				  var d=[x];
////				  var k=1;
////				  var key, count = 0;
////				  for(key in y) {
////					  d[k]=y[key];
////					  k++;
////				  }
////				  datos.push(d);
////			      dI=dF;
////			      dF=new moment(dF + (60*1000));
////			  }
//		    	  
//		    	  if(dF <= end){
//		    		  console.log('rtsentimentdata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);
//		    		  var y = dataRT('rtsentimentdata?id='+iD+'&fInit='+moment(dI).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&fEnd='+moment(dF).utc().format("ddd MMM DD YYYY HH:mm:ss z")+'&sizeSec='+"minutes1"+'&numIds='+numIds);//Math.random();
//		    		  x=new Date(dI);
//		    		  var d=[x];
//		    		  var k=1;
//		    		  var key;
//		    		  for(key in y) {
//		    			  d[k]=y[key];
//		    			  k++;
//		    		  }
//		    		  datos.push(d);
//		    		  graphSent.updateOptions( { 'file': datos } );
//		    		  dI=dF;
//		    		  dF=new moment(dF + (60*1000));
//		          }else{
//		        	  clearInterval(window.intervalId);
//		          }  
//		      }, 1000);
//		}else{
//			BootstrapDialog.alert({
//				type: BootstrapDialog.TYPE_WARNING,
//	            title: 'Warning!!',
//	            message: 'Please select a party first'		            	
//	        });
//			
//			graphSent.setData([ {
//				  date: 0,
//				  sentiment : 0
//			}]);
//
//		}
//	}
	
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
	});
	//Hide when date selected
	$("#sfromDate").on('changeDate', function(ev){
	    $(this).datetimepicker('hide');	
	});	
	
	//refresh chart when clicked
	$("#refresh").on('click', function(ev){		
		updateVolume();
		//updateSentiment();
	});
	
	//Erase start date 
	$("#removeSfromDate").on('click', function(ev){
		startDate=null;
	});

	//Erase start date 
	$("#removeStoDate").on('click', function(ev){
		endDate=null;
	});

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
	
	var graphVol = new Dygraph(document.getElementById("RTVolumeAreaChart"), datosV,
			{
				drawPoints: true,
				showRoller: true,
				colors:colors,
				//valueRange: [0.0, 1.2],
				labels: partNamesLabels,
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
				
				zoomCallback: function(min, max, range) {
		           	zoomGraphSent(min, max, range);
		        },
			});

	var graphSent = new Dygraph(document.getElementById("RTSentimentChart"), datosS,
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
			            	zoomGraphVol(min, max, range);
			            },
        			});

	// update the volumen chart with the new data.
	function updateVolume() {
	  if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')){//&&
				 //(frecuency!=null&&frecuency!='undefined')){
		  clearInterval(window.intervalId);
		  graphVol.destroy();	
		  //datosV = [];
		  datosV = new Array();
		  
		  graphSent.destroy();	
		  //datosS = [];
		  datosS = new Array();
		  
		  graphVol = new Dygraph(document.getElementById("RTVolumeAreaChart"), datosV,
	            	{
						drawPoints: true,
						showRoller: true,
						colors:colors,
						//valueRange: [0.0, 1.2],
						labels: partNamesLabels,
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
			            
			            zoomCallback: function(min, max, range) {
			            	zoomGraphSent(min, max, range);
			            },
	            	});
		  graphSent = new Dygraph(document.getElementById("RTSentimentChart"), datosS,
	            	{
						drawPoints: true,
						//showRoller: true,
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
			            	zoomGraphVol(min, max, range);
			            },
	            	});
		  callVolumeUpdate(startDate,endDate,"minutes1");//frecuency);
		  
	  }else{
		  BootstrapDialog.alert({
				type: BootstrapDialog.TYPE_DANGER,
	            title: 'AVISO!!',
	            message: 'Fecha de inicio, fecha de fin y/o frecuencia no han sido definidos'
	       });
	  }
	}
	// update the volumen chart with the new data.
//	function updateSentiment() {
//	  if((startDate!=null&&startDate!='undefined')&&(endDate!=null&&endDate!='undefined')&&
//				 (frecuency!=null&&frecuency!='undefined')){	
//		  callSentimentUpdate(startDate,endDate,frecuency);
//	  }else{
//		  BootstrapDialog.alert({
//				type: BootstrapDialog.TYPE_DANGER,
//	            title: 'ERROR!!',
//	            message: 'Fecha de inicio, fecha de fin y/o frecuencia no han sido definidos'
//	      });
//	  }
//	}

    function zoomGraphVol(min, max, range) {
    	graphVol.updateOptions({
    		dateWindow: [min, max]
    	});      
    }

    function zoomGraphSent(min, max, range) {
    	graphSent.updateOptions({
    		dateWindow: [min, max]
    	});
    }
	
	
	$(window).resize(function() {
		graphVol.resize();
		graphSent.resize();
	});
	
	//FIN
});
