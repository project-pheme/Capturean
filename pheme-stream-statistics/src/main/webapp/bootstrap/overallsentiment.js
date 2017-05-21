//Volume Line Chart
$(document).ready(function() {
	
	var endDate;
	var startDate;
	var frecuency;
	var id=[];
	var clickedALL=false;
	var idsToUpdate=[];
	var circlesHeight=0;
	var circlesWidth=0;
	var circleRadius=0;
	//Morris chart values
	//var partNames = ['PP','PSOE', 'Podemos','Ciudadanos','UPyD','IU'];
	//var yKeysVol= ['volume0','volume1','volume2','volume3', 'volume4', 'volume5'];
	//var colors = ['#3366CC','#FF0000','#990099','#FF3300','#FF0099','#FF0000'];
	var partMPNames=[];
	var partONames=[];
	var colorsMP=[];
	var imagesMP=[];
	var idsMP=[];
	var idsO=[];
	var colorsO=[];
	var imagesO=[];
	var percentsMP=[];
	var percentsO=[];
	var sentMP=[];
	var sentO=[];
	var gageValue;	
	var c4=null;
	var cxt4=null;
	var selected=0;
	var colorSelected=0;
	var imageSelected=0;
	var nameSelected=0;
	var url;
	var graph3;	
	var imageObj0;
	var imageObj1;
	var imageObj2;
	var imageObj3;
	var cxt0=null;
	var cxt1=null;
	var cxt2=null;
	var isParties;
	var isDebate;
	var isPartiesG;
	
	if(document.getElementById('isParties'))
		isParties = document.getElementById('isParties').getAttribute('data-value');
	console.log("isParties="+isParties);
	
	if(document.getElementById('isPartiesG'))
		isPartiesG = document.getElementById('isPartiesG').getAttribute('data-value');
	console.log("isPartiesG="+isPartiesG);
	
	if(document.getElementById('isDebate'))
		isDebate = document.getElementById('isDebate').getAttribute('data-value');
	console.log("isDebate="+isDebate);
	
	//Last week, ending on yesterday
	var m = moment().subtract(1,'days');	
	var now = m.format("ddd MMM DD YYYY 23:59:59");
    var ini = m - (1000 * 3600 * 24 * 7);
    //var date= new Date(ini);
    var date = moment(ini).format("ddd MMM DD YYYY 00:00:00");
	
	//Party names
	for(var i=0; document.getElementById('partMPName'+i)!= null;i++){
		var aux = document.getElementById('partMPName'+i).getAttribute('data-value');
		console.log("Participante Principal " + i + " = " + aux);	
		partMPNames[i]=aux;
		//yKeysVol[i]="volume"+i;
	}

	for(var i=0; document.getElementById('partALLName'+i)!= null;i++){
		var aux = document.getElementById('partALLName'+i).getAttribute('data-value');
		var add = true;		
		for(var j=0; j<partMPNames.length ;j++){
			if(aux==partMPNames[j]){
				add=false;
			}
		}
		if(add){
			partONames[i]=aux;
			//yKeysVol[i]="volume"+i;
			console.log("Otros Participantes " + i + " = " + aux);
		}
	}

	
	//Party colors
	for(var i=0; document.getElementById('partMPCols'+i)!= null;i++){
		var aux = document.getElementById('partMPCols'+i).getAttribute('data-value');
		console.log("colorsMP" + i + " = " + aux);	
		colorsMP[i]=aux;		
	}
	
	for(var i=0; document.getElementById('partALLCols'+i)!= null;i++){
		var aux = document.getElementById('partALLCols'+i).getAttribute('data-value');
		var add = true;
		for(var j=0; j<colorsMP.length ;j++){
			if(aux==colorsMP[j]){
				add=false;
			}
		}
		if(add){
			colorsO[i]=aux;	
			console.log("colorO" + i + " = " + aux);
		}	
	}
	
	//Party images
	for(var i=0; document.getElementById('partMPImgs'+i)!= null;i++){
		var aux = document.getElementById('partMPImgs'+i).getAttribute('data-value');
		console.log("imagesMP" + i + " = " + aux);	
		imagesMP[i]=aux;		
	}
	
	for(var i=0; document.getElementById('partALLImgs'+i)!= null;i++){
		var aux = document.getElementById('partALLImgs'+i).getAttribute('data-value');
		var add = true;
		for(var j=0; j<imagesMP.length ;j++){
			if(aux==imagesMP[j]){
				add=false;
			}
		}
		if(add){
			imagesO[i]=aux;	
			console.log("imagesO" + i + " = " + aux);
		}	
	}
	
	
	//Ids
	for(var i=0; document.getElementById('idMP'+i)!= null;i++){
		var aux = document.getElementById('idMP'+i).getAttribute('data-value');
		console.log("idMP" + i + " = " + aux);	
		idsMP[i]=aux;		
	}
	idsToUpdate=idsMP;
	
	
	for(var i=0; document.getElementById('idALL'+i)!= null;i++){
		var aux = document.getElementById('idALL'+i).getAttribute('data-value');
		var add = true;
		for(var j=0; j<idsMP.length ;j++){
			if(aux==idsMP[j]){
				add=false;
			}
		}
		if(add){
			idsO[i]=aux;	
			console.log("idsO" + i + " = " + aux);
		}	
	}
	
	//Party percents
	for(var i=0; document.getElementById('partMPperc'+i)!= null;i++){
		var aux = document.getElementById('partMPperc'+i).getAttribute('data-value');
		console.log("percentajesMP" + i + " = " + aux);	
		percentsMP[i]=aux+"%";		
	}
	
//	for(var i=0; document.getElementById('partOperc'+i)!= null;i++){
//		var aux = document.getElementById('partOperc'+i).getAttribute('data-value');
//		console.log("percentajesO" + i + " = " + aux);	
//		percentsO[i]=aux+"%";		
//	}
	
	//Party sentiments
	for(var i=0; document.getElementById('partMPsent'+i)!= null;i++){
		var aux = document.getElementById('partMPsent'+i).getAttribute('data-value');
		console.log("sentMP" + i + " = " + aux);	
		sentMP[i]=aux;		
	}
	
	//CIRCLE
//	for(var i=0;i<colorsMP.length;i++){
//		var c=document.getElementById("circle"+i);
//	    var cxt=c.getContext("2d");	    
//	    
//	    //test image
//	    var imageObj = new Image();	
//	    var name = partMPNames[i];
//	    var value = percentsMP[i];
//	    imageObj.src = imagesMP[i]; 	    
//	    
//	    cxt.fillStyle =colorsMP[i];
//	    cxt.beginPath();
//	    //cxt.arc(60,60,50,0,Math.PI*2,true);
//	    cxt.arc(111,111,101,51,Math.PI*2,true);
//	    cxt.closePath();
//	    cxt.fill(); 
//	    
//	    cxt.fillStyle = "black"; // font color to write the text with    
//	    cxt.font = "32px 'Kaushan Script' cursive";
//	    
//	    imageObj.onload = function(){
//	         cxt.drawImage(imageObj, 120-90/4 ,190-90/2);
//	         // Move it down by half the text height and left by half the text width
//	         //cxt.textBaseline = "top";
//	 	     //cxt.fillText(name, 90-90/4 ,110-90/2);
//	 	     //cxt.textBaseline = "center";
//	 	     //cxt.fillText(value, 100-90/4 ,140-90/2);
//	     };
// 	    // Move it down by half the text height and left by half the text width 
//	    cxt.textBaseline = "top";
//	    cxt.fillText(partMPNames[i], 90-90/4 ,110-90/2);
//	    cxt.textBaseline = "center";
//	    cxt.fillText(percentsMP[i], 100-90/4 ,140-90/2);
//	}	
	
	//var style0 = getComputedStyle(document.getElementById("circle3"));   
		//	//CIRCLE1	
		//	var c0=document.getElementById("circle0");
		//	var cxt0=c0.getContext("2d");	   
		//
		//	//Will be used for all
		//	console.log("ANA=>c0="+c0);
		//	var style = window.getComputedStyle(c0);
		//	console.log("ANA=>style="+style);
		//    var height=parseInt(style.getPropertyValue("height"),10);//$(document).height();
		//    console.log("ANA=>height="+height);
		//    var width=parseInt(style.getPropertyValue("width"),10);
		//    console.log("ANA=>width="+width);
		//    var radius = Math.min(width, height) * 0.5;//0.5;
		//    console.log("ANA=>radius="+radius);
		//				    
		//    //test image
		//    var imageObj0 = new Image();    
		//    imageObj0.src = imagesMP[0]; 	    
		//    
		//    cxt0.fillStyle =colorsMP[0];
		//    cxt0.beginPath();
		//    //cxt.arc(60,60,50,0,Math.PI*2,true);
		//    //PRUEBA    
		//    //cxt0.arc(111,111,101,51,Math.PI*2,true);    
		//    console.log("ANA=>height="+height+" width="+width+" radius="+radius);
		//    cxt0.arc(width/2,height/2,radius,51,Math.PI*2,true);
		//    //FIN PRUEBA
		//    cxt0.closePath();
		//    cxt0.fill(); 
		//    
		//    cxt0.fillStyle = "black"; // font color to write the text with    
		//    cxt0.font = "32px 'Kaushan Script' cursive";
		//    
		//    imageObj0.onload = function(){
		//         cxt0.drawImage(imageObj0, 110-90/4 ,190-90/2);         
		//     };
		//	    // Move it down by half the text height and left by half the text width 
		//    cxt0.textBaseline = "top";
		//    cxt0.fillText(partMPNames[0],  100-90/2 ,110-90/2);
		//    cxt0.textBaseline = "center";
		//    cxt0.fillText(percentsMP[0], 100-90/4 ,140-90/2);
		//	
		//	//CIRCLE2
		//    var c1=document.getElementById("circle1");
		//    var cxt1=c1.getContext("2d");	    
		//    
		//    //test image
		//    var imageObj1 = new Image();    
		//    imageObj1.src = imagesMP[1]; 	    
		//    
		//    cxt1.fillStyle =colorsMP[1];
		//    cxt1.beginPath();
		//    //cxt.arc(60,60,50,0,Math.PI*2,true);
		//    //PRUEBA    
		//    //cxt0.arc(111,111,101,51,Math.PI*2,true);    
		//    console.log("ANA1=>height="+height+" width="+width+" radius="+radius);
		//    cxt1.arc(width/2,height/2,radius,51,Math.PI*2,true);
		//    //FIN PRUEBA
		//    
		//    cxt1.closePath();
		//    cxt1.fill(); 
		//    
		//    cxt1.fillStyle = "black"; // font color to write the text with    
		//    cxt1.font = "32px 'Kaushan Script' cursive";
		//    
		//    imageObj1.onload = function(){
		//         cxt1.drawImage(imageObj1, 110-90/4 ,190-90/2);
		//    };
		//	    // Move it down by half the text height and left by half the text width 
		//    cxt1.textBaseline = "top";
		//    cxt1.fillText(partMPNames[1],  100-90/2 ,110-90/2);
		//    cxt1.textBaseline = "center";
		//    cxt1.fillText(percentsMP[1], 100-90/4 ,140-90/2);
		//    
		//    //CIRCLE3
		//    var c2=document.getElementById("circle2");
		//    var cxt2=c2.getContext("2d");	    
		//    
		//    //test image
		//    var imageObj2 = new Image();    
		//    imageObj2.src = imagesMP[2]; 	    
		//    
		//    cxt2.fillStyle =colorsMP[2];
		//    cxt2.beginPath();
		//    
		//    //PRUEBA    
		//    //cxt0.arc(111,111,101,51,Math.PI*2,true);   
		//    console.log("ANA2=>height="+height+" width="+width+" radius="+radius);
		//    cxt2.arc(width/2,height/2,radius,51,Math.PI*2,true);
		//    //FIN PRUEBA
		//    
		//    cxt2.closePath();
		//    cxt2.fill(); 
		//    
		//    cxt2.fillStyle = "black"; // font color to write the text with    
		//    cxt2.font = "32px 'Kaushan Script' cursive";
		//    
		//    imageObj2.onload = function(){
		//         cxt2.drawImage(imageObj2, 110-90/4 ,190-90/2);
		//     };
		//	// Move it down by half the text height and left by half the text width 
		//    cxt2.textBaseline = "top";
		//    cxt2.fillText(partMPNames[2], 100-90/2 ,110-90/2);
		//    cxt2.textBaseline = "center";
		//    cxt2.fillText(percentsMP[2], 100-90/4 ,140-90/2);
		//	//END CIRCLES
  draw();

	//CIRCLE 4:does not shown on load
//	var c4=document.getElementById("circle3");
//    var cxt4=c4.getContext("2d");
//    cxt4.fillStyle =colorsO[0];
//    cxt4.beginPath();
//    //cxt.arc(60,60,50,0,Math.PI*2,true);
//    cxt4.arc(111,111,101,51,Math.PI*2,true);
//    cxt4.closePath();
//    cxt4.fill();
//    
//    cxt4.fillStyle = "black"; // font color to write the text with    
//    cxt4.font = "32px 'Kaushan Script' cursive"; 
//    // Move it down by half the text height and left by half the text width
//    cxt4.textBaseline = "top";
//    cxt4.fillText(partONames[0], 80-90/2 ,110-90/2);
//    cxt4.textBaseline = "center";
//    cxt4.fillText(percentsO[0], 90-90/4 ,140-90/2);

	//Last 24 hours with 1 slot => sizeSec=86400
	$('#olastday').on('click', function (e) {
		//var now = new Date;//Date.now();
		//var ini = now - (1000 * 3600 * 24); // 12 hour in millisecs
		//var date = new Date(ini);		
		var m = moment().subtract(1,'days');
		var now = m.format("ddd MMM DD YYYY 23:59:59");
	    var ini = m - (1000 * 3600 * 24);	   
	    var date = moment(ini).format("ddd MMM DD YYYY 00:00:00");
		
		idsToUpdate=idsMP;
		frecuency="hourly";
		if((selected!=0)&&(idsToUpdate[idsToUpdate.length-1]!=selected)){
			if(idsToUpdate.length==4){
				idsToUpdate[idsToUpdate.length-1]=selected;
			}else{
				idsToUpdate.push(selected);
			}
		}
		//callVolumeUpdate(idsMP.concat(idsO),idsToUpdate,new Date(ini).toISOString(),new Date(now).toISOString(),86400);
		$("#loading-indicator").show();		
		var aux=[];
		for(var i=0; i<3;i++){ //Just first 3
			aux[i]=idsMP[i];
		}
		console.log("idsO.lenght="+idsO.length+" idsO="+idsO);
		for(var i=0; i<idsO.length;i++){ //Just first 3
			if(idsO[i]!== "" && idsO[i]!== undefined){
				aux.push(idsO[i]);
			}
		}
		console.log("aux.concat(idsO)="+aux+" idsToUpdate="+idsToUpdate);
		setTimeout(function(){callVolumeUpdate(aux,idsToUpdate,date,now,frecuency)},1000); //Delay necessary to show the loading-indicator
		//callVolumeUpdate(idsMP.concat(idsO),idsToUpdate,date.toGMTString(),now.toGMTString(),"daily");//86400);
	});
	
	//Last 4 weeks with 1 slot => sizeSec=2629743.83
	$('#olastmonth').on('click', function (e) {
		//var now = new Date;//Date.now();
		//var ini = now - (1000 * 3600 * 24 * 7 * 4); //24=> 1 day, 7 => seven days= 1 week, 4 => 4 weeks 
		//var date = new Date(ini);
		var m = moment().subtract(1,'days');
		var now = m.format("ddd MMM DD YYYY 23:59:59");
	    var ini = m - (1000 * 3600 * 24 * 7 * 4);	   
	    var date = moment(ini).format("ddd MMM DD YYYY 00:00:00");
		
		//var idsToUpdate=idsMP;
		idsToUpdate=idsMP;
		frecuency="daily";
		if((selected!=0)&&(idsToUpdate[idsToUpdate.length-1]!=selected)){
			if(idsToUpdate.length==4){
				idsToUpdate[idsToUpdate.length-1]=selected;
			}else{
				idsToUpdate.push(selected);
			}
		}
		$("#loading-indicator").show();
		var aux=[];
		for(var i=0; i<3;i++){ //Just first 3
			aux[i]=idsMP[i];
		}		
		console.log("idsO.lenght="+idsO.length+" idsO="+idsO);
		for(var i=0; i<idsO.length;i++){ //Just first 3
			if(idsO[i]!== "" && idsO[i]!== undefined){
				aux.push(idsO[i]);
			}
		}
		console.log("aux.concat(idsO)="+aux+" idsToUpdate="+idsToUpdate);
		setTimeout(function(){callVolumeUpdate(aux,idsToUpdate,date,now,"daily")},1000); //Delay necessary to show the loading-indicator
		//callVolumeUpdate(idsMP.concat(idsO),idsToUpdate,new Date(ini).toISOString(),new Date(now).toISOString(),2629743.83);
		//callVolumeUpdate(idsMP.concat(idsO),idsToUpdate,date.toGMTString(),now.toGMTString(),"daily");
	});
	
	//Get selected other party
	$(document).on('click', '.dropdown-menu li a', function () {
		for (var i=0; i<partONames.length;i++){
			if(partONames[i] == $(this).text()){
				selected=idsO[i];
				colorSelected=colorsO[i];
				imageSelected=imagesO[i];
				nameSelected=partONames[i];
				if(c4==null){
					//paint Circle
					c4=document.getElementById("circle3");
				    cxt4=c4.getContext("2d");
				    
				    //test image
				    imageObj3 = new Image();				    
				    imageObj3.src = imageSelected; 	
				    
				    cxt4.fillStyle =colorsO[i];
				    cxt4.beginPath();
				    //cxt.arc(60,60,50,0,Math.PI*2,true);
				    console.log("ANA2=>height="+circlesHeight+" width="+circlesWidth+" radius="+circleRadius);
				    cxt4.arc(circlesWidth/2,circlesHeight/2,circleRadius,51,Math.PI*2,true);
				    //cxt4.arc(111,111,101,51,Math.PI*2,true);
				    
				    cxt4.closePath();
				    cxt4.fill();
		    
				    cxt4.fillStyle = "black"; // font color to write the text with    
				    cxt4.font = "32px Georgia cursive"; 
				    
				    imageObj3.onload = function(){
				         cxt4.drawImage(imageObj3, 120-90/4 ,190-90/2);
				     };
				     
				    // Move it down by half the text height and left by half the text width
				    cxt4.textBaseline = "top";
				    cxt4.fillText(partONames[i], 23,110-90/2);//100-90/2 ,110-90/2);
				    cxt4.textBaseline = "center";
				    //var now = new Date;
				    //var ini = now - (1000 * 3600 * 24);	
				    //86400=secs in one day
				    var aux=[];
					for(var j=0; j<3;j++){ //Just first 3
						aux[j]=idsMP[j];
					}
					console.log("idsO.lenght="+idsO.length+" idsO="+idsO);
					for(var n=0; n<idsO.length;n++){ //Just first 3
						if(idsO[n]!== "" && idsO[n]!== undefined){
							aux.push(idsO[n]);
						}
					}
					console.log("aux.concat(idsO)="+aux+" idsToUpdate="+idsToUpdate);
					
					//Descomentar cuando se cambie las fechas fijas para los partidos
				    //cxt4.fillText(getValue(aux, idsO[i],date,now,"daily")+"%", 100-90/4 ,140-90/2);
				    cxt4.fillText(getValue(aux, idsO[i],date,now,"daily",true)+"%", 100-90/4 ,140-90/2);
				    
				    //paint Sentiment Chart
				    //Descomentar cuando se cambie las fechas fijas para los partidos
				    //gageValue= getSent(idsO[i], date, now, "daily"); //86400
				    gageValue= getSent(idsO[i], date, now, "daily",true);
					graph3 = new JustGage({
					    id: "gauge3",
					    value: gageValue,// sentO[0],
					    //min: 0,
					    //max: 100,
					    min: -1,
					    max: 1,
					    relativeGaugeSize: true,
					    decimals:2,
					    title: "Sentimiento"
					  });
				}else{
					//update Circle
					cxt4.fillStyle =colorsO[i];
				    cxt4.beginPath();	
				    console.log("ANA2=>height="+circlesHeight+" width="+circlesWidth+" radius="+circleRadius);
				    cxt4.arc(circlesWidth/2,circlesHeight/2,circleRadius,51,Math.PI*2,true);
				    //cxt4.arc(111,111,101,51,Math.PI*2,true);
				    cxt4.closePath();
				    cxt4.fill();
				    
				    //test image
				    imageObj3 = new Image();				    
				    imageObj3.src = imageSelected; 	
				    
				    cxt4.fillStyle = "black"; // font color to write the text with    
				    cxt4.font = "32px Georgia cursive"; 
				    
				    imageObj3.onload = function(){
				         cxt4.drawImage(imageObj3, 120-90/4 ,190-90/2);
				     };
				    
				    // Move it down by half the text height and left by half the text width
				    cxt4.textBaseline = "top";
				    cxt4.fillText(partONames[i], 23 ,110-90/2);//100-90/2 ,110-90/2);
				    cxt4.textBaseline = "center";
				    //cxt4.fillText(percentsO[i], 100-90/4 ,140-90/2);
				    var aux=[];
					for(var j=0; j<3;j++){ //Just first 3
						aux[j]=idsMP[j];
					}
					console.log("idsO.lenght="+idsO.length+" idsO="+idsO);
					for(var n=0; n<idsO.length;n++){ //Just first 3
						if(idsO[n]!== "" && idsO[n]!== undefined){
							aux.push(idsO[n]);
						}
					}
					console.log("aux.concat(idsO)="+aux+" idsToUpdate="+idsToUpdate);
					
				    //Descomentar cuando quitemos fechas fijas para partidos
					//cxt4.fillText(getValue(aux, idsO[i],date,now,"daily")+"%", 100-90/4 ,140-90/2);
					cxt4.fillText(getValue(aux, idsO[i],date,now,"daily",true)+"%", 100-90/4 ,140-90/2);
					
					//Update sentiment chart
					//gageValue = sentO[i];	
					
					//Descomentar cuando quitemos fechas fijas para partidos
					//gageValue = getSent(idsO[i], date, now, "daily"); //86400
					gageValue = getSent(idsO[i], date, now, "daily",true); //86400
					graph3.refresh(gageValue);
				}
			}
		}
	});	
	
	
	//create graphs
//	for(var i=0;i<sentMP.length;i++){		
//		var graph = new JustGage({
//		    id: "gauge"+i,
//		    value: sentMP[i],
//		    min: 0,
//		    max: 100,
//		    title: "Sentimiento"
//		  });		
//	}
	
	var graph0 = new JustGage({
	    id: "gauge0",
	    value: sentMP[0],	    	    
	    min: -1,
	    max: 1,
	    relativeGaugeSize: true,
	    decimals:2,
	    title: "Sentimiento"	    
	  });
	
	var graph1 = new JustGage({
	    id: "gauge1",
	    value: sentMP[1],	    
	    min: -1,
	    max: 1,
	    relativeGaugeSize: true,
	    decimals:2,
	    title: "Sentimiento"
	  });
	
	if(partMPNames[2] != null && partMPNames[2] != ""){
		var graph2 = new JustGage({
		    id: "gauge2",
		    value: sentMP[2],    
		    min: -1,
		    max: 1,
		    relativeGaugeSize: true,
		    decimals:2,
		    title: "Sentimiento"
		  });
	}

	//create graphs=> Does not shown on load
//	gageValue= sentO[0];
//	partTitle= partONames[0]; //Initial value
//	var graph3 = new JustGage({
//	    id: "gauge3",
//	    value: gageValue,// sentO[0],
//	    min: 0,
//	    max: 100,
//	    title: "Sentiment"
//	  });
		
	// update the chart with the new data.
	function callVolumeUpdate(idsAll,idsToUPDT,startDate,endDate,frecuency){
	  //update circles
	  for(var i=0;i<idsToUPDT.length;i++){
 		 //update Circle
		 var name; 
		 var c=document.getElementById("circle"+i);
		 var cxt=c.getContext("2d");
		
		 if(i==3){
			 cxt.fillStyle=colorSelected;
			 name=nameSelected;			 
		 }
		 else{
			 cxt.fillStyle=colorsMP[i];
			 name=partMPNames[i];
	  	 }
		 //cxt.fillStyle =colorsMP[i];
		 cxt.beginPath();	
		 console.log("ANA2=>height="+circlesHeight+" width="+circleRadius+" radius="+circleRadius);
		 cxt.arc(circlesWidth/2,circlesHeight/2,circleRadius,51,Math.PI*2,true);
		 //cxt.arc(111,111,101,51,Math.PI*2,true);
		 cxt.closePath();
		 cxt.fill();
		    
		 cxt.fillStyle = "black"; // font color to write the text with    
		 cxt.font = "32px Georgia cursive"; 
		 // Move it down by half the text height and left by half the text width
		 cxt.textBaseline = "top";
		 cxt.fillText(name,  23 ,110-90/2);//100-90/2 ,110-90/2);
		 cxt.textBaseline = "center";
		 //Descomentar cuando quitemos fechas fijas de partidos
		 //cxt.fillText(getValue(idsAll, idsToUPDT[i], startDate, endDate, frecuency)+"%", 100-90/4 ,140-90/2);
		 cxt.fillText(getValue(idsAll, idsToUPDT[i], startDate, endDate, frecuency,false)+"%", 100-90/4 ,140-90/2);

		 //update Sentiment
		 //Descomentar cuando quitemos fechas fijas
		 //gageValue = getSent(idsToUPDT[i], startDate, endDate, frecuency);
		 gageValue = getSent(idsToUPDT[i], startDate, endDate, frecuency,false);
		 if(i==0){ 
			 graph0.refresh(gageValue);
			 imageObj0.src=imagesMP[0];
			 imageObj0.onload = function(){
		         cxt0.drawImage(imageObj0, 120-90/4 ,190-90/2);         
		     };
		 };
		 if(i==1){ 
			 graph1.refresh(gageValue);
			 imageObj1.src=imagesMP[1];
			 imageObj1.onload = function(){
		         cxt1.drawImage(imageObj1, 120-90/4 ,190-90/2);         
		     };
		  };
		 if(i==2){ 
			 graph2.refresh(gageValue);
			 imageObj2.src=imagesMP[2];
			 imageObj2.onload = function(){
		         cxt2.drawImage(imageObj2, 120-90/4 ,190-90/2);         
			 };
		 };	 
		 if(i==3){ 
			 graph3.refresh(gageValue);			 
			 imageObj3.src=imageSelected;
			 imageObj3.onload = function(){
		         cxt4.drawImage(imageObj3, 120-90/4 ,190-90/2);         
		     };
		  };
		 }
	  }

	
	function getValue(dcIdList, dcId, initDate, endDate, per, from4){
		var call="";
		
		//var call = 'volumeper?dcIdList='+dcIdList+'&dcId='+dcId+'&initDate='+initDate+'&endDate='+endDate+'&per='+per;
		
		if(from4){
			if(isParties=="true"){			
				var iniD = moment("Tue May 19 2015 00:00:00").format("ddd MMM DD YYYY 00:00:00");
				console.log("iniD="+iniD);
			    var endD = moment("Tue May 19 2015 23:59:59").format("ddd MMM DD YYYY 23:59:59");
			    console.log("endD="+endD);
			    call = 'volumeper?dcIdList='+dcIdList+'&dcId='+dcId+'&initDate='+iniD+'&endDate='+endD+'&per='+per;
			} else if(isPartiesG=="true"){			
				var iniD = moment("Wed Dec 09 2015 00:00:00").format("ddd MMM DD YYYY 00:00:00");
				console.log("iniD="+iniD);
			    var endD = moment("Thu Dec 31 2015 23:59:59").format("ddd MMM DD YYYY 23:59:59");
			    console.log("endD="+endD);
			    call = 'volumeper?dcIdList='+dcIdList+'&dcId='+dcId+'&initDate='+iniD+'&endDate='+endD+'&per='+per;
			} else if(isDebate=="true"){
				var iniD = moment("Mon, 26 Oct 2015 00:00:00").format("ddd MMM DD YYYY 00:00:00");
				console.log("iniD="+iniD);
			    var endD = moment("Mon, 02 Nov 2015 23:59:59").format("ddd MMM DD YYYY 23:59:59");
			    console.log("endD="+endD);
			    call = 'volumeper?dcIdList='+dcIdList+'&dcId='+dcId+'&initDate='+iniD+'&endDate='+endD+'&per='+per;
			}else{
				call = 'volumeper?dcIdList='+dcIdList+'&dcId='+dcId+'&initDate='+initDate+'&endDate='+endDate+'&per='+per;
			}
		}else{
			var call = 'volumeper?dcIdList='+dcIdList+'&dcId='+dcId+'&initDate='+initDate+'&endDate='+endDate+'&per='+per;
		}
		
		return data(call);
	}
	
	function getSent(dcList, initDate, endDate, per, from4){
		var call ="";
		
		//var call = 'sentper?dcList='+dcList+'&initDate='+initDate+'&endDate='+endDate+'&per='+per;

		if(from4){
			if(isParties=="true"){			
				var iniD = moment("Tue May 19 2015 00:00:00").format("ddd MMM DD YYYY 00:00:00");
				console.log("iniD="+iniD);
			    var endD = moment("Tue May 19 2015 23:59:59").format("ddd MMM DD YYYY 23:59:59");
			    console.log("endD="+endD);
				call = 'sentper?dcList='+dcList+'&initDate='+iniD+'&endDate='+endD+'&per='+per;
			}else if(isPartiesG=="true"){			
				var iniD = moment("Wed Dec 09 2015 00:00:00").format("ddd MMM DD YYYY 00:00:00");
				console.log("iniD="+iniD);
			    var endD = moment("Thu Dec 31 2015 23:59:59").format("ddd MMM DD YYYY 23:59:59");
			    console.log("endD="+endD);
				call = 'sentper?dcList='+dcList+'&initDate='+iniD+'&endDate='+endD+'&per='+per;
			} else if(isDebate=="true"){
				var iniD = moment("Mon, 26 Oct 2015 00:00:00").format("ddd MMM DD YYYY 00:00:00");
				console.log("iniD="+iniD);
			    var endD = moment("Mon, 02 Nov 2015 23:59:59").format("ddd MMM DD YYYY 23:59:59");
			    console.log("endD="+endD);
			    call = 'volumeper?dcIdList='+dcIdList+'&dcId='+dcId+'&initDate='+iniD+'&endDate='+endD+'&per='+per;
			}else{
				call = 'sentper?dcList='+dcList+'&initDate='+initDate+'&endDate='+endDate+'&per='+per;
			}
		}else{
			var call = 'sentper?dcList='+dcList+'&initDate='+initDate+'&endDate='+endDate+'&per='+per;
		}
		
		return data(call);
	}
	
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
	
	function draw(){
		//CIRCLE1	
		var c0=document.getElementById("circle0");		
		cxt0=c0.getContext("2d"); //var cxt0=c0.getContext("2d");	   

		//Will be used for all		
		//var style = window.getComputedStyle(c0);		
	    circlesHeight=c0.height;//parseInt(style.getPropertyValue("height"),10);//$(document).height();	    
	    circlesWidth=c0.width;//parseInt(style.getPropertyValue("width"),10);	    
	    circleRadius = Math.min(circlesWidth, circlesHeight) * 0.5;//0.5;	    
					    
	    //test image
	    imageObj0 = new Image(); //var imageObj0 = new Image();    
	    imageObj0.src = imagesMP[0]; 	    
	    
	    cxt0.fillStyle =colorsMP[0];
	    cxt0.beginPath();
	    //cxt.arc(60,60,50,0,Math.PI*2,true);
	    //PRUEBA    
	    //cxt0.arc(111,111,101,51,Math.PI*2,true);    
	    console.log("ANA=>height="+circlesHeight+" width="+circlesWidth+" radius="+circleRadius);
	    cxt0.arc(circlesWidth/2,circlesHeight/2,circleRadius,51,Math.PI*2,true);
	    //FIN PRUEBA
	    cxt0.closePath();
	    cxt0.fill(); 
	    
	    cxt0.fillStyle = "black"; // font color to write the text with    
	    cxt0.font = "32px Georgia cursive"; //'Kaushan Script' cursive";
	    
	    imageObj0.onload = function(){
	         cxt0.drawImage(imageObj0, 110-90/4 ,190-90/2);         
	     };
		    // Move it down by half the text height and left by half the text width 
	    cxt0.textBaseline = "top";
	    cxt0.fillText(partMPNames[0],  100-90/2 ,110-90/2);
	    cxt0.textBaseline = "center";
	    cxt0.fillText(percentsMP[0], 100-90/4 ,140-90/2);
		
		//CIRCLE2
	    var c1=document.getElementById("circle1");
	    cxt1=c1.getContext("2d"); //var cxt1=c1.getContext("2d");	    
	    
	    //test image
	    imageObj1 = new Image();    
	    imageObj1.src = imagesMP[1]; 	    
	    
	    cxt1.fillStyle =colorsMP[1];
	    cxt1.beginPath();
	    //cxt.arc(60,60,50,0,Math.PI*2,true);
	    //PRUEBA    
	    //cxt0.arc(111,111,101,51,Math.PI*2,true);    
	    console.log("ANA1=>height="+circlesHeight+" width="+circlesWidth+" radius="+circleRadius);
	    cxt1.arc(circlesWidth/2,circlesHeight/2,circleRadius,51,Math.PI*2,true);
	    //FIN PRUEBA
	    
	    cxt1.closePath();
	    cxt1.fill(); 
	    
	    cxt1.fillStyle = "black"; // font color to write the text with    
	    cxt1.font = "32px Georgia cursive";
	    
	    imageObj1.onload = function(){
	         cxt1.drawImage(imageObj1, 110-90/4 ,190-90/2);
	    };
		    // Move it down by half the text height and left by half the text width 
	    cxt1.textBaseline = "top";
	    cxt1.fillText(partMPNames[1],  23 ,110-90/2);//100-90/2 ,110-90/2);
	    cxt1.textBaseline = "center";
	    cxt1.fillText(percentsMP[1], 100-90/4 ,140-90/2);
	    
	    //CIRCLE3
	    if(partMPNames[2] != null && partMPNames[2] != ""){	    	
		    var c2=document.getElementById("circle2");
		    cxt2=c2.getContext("2d"); //var cxt2=c2.getContext("2d");	    
		    
		    //test image
		    imageObj2 = new Image();    
		    imageObj2.src = imagesMP[2]; 	    
		    
		    cxt2.fillStyle =colorsMP[2];
		    cxt2.beginPath();
		    
		    //PRUEBA    
		    //cxt0.arc(111,111,101,51,Math.PI*2,true);   
		    console.log("ANA2=>height="+circlesHeight+" width="+circlesWidth+" radius="+circleRadius);
		    cxt2.arc(circlesWidth/2,circlesHeight/2,circleRadius,51,Math.PI*2,true);
		    //FIN PRUEBA
		    
		    cxt2.closePath();
		    cxt2.fill(); 
		    
		    cxt2.fillStyle = "black"; // font color to write the text with    
		    cxt2.font = "32px Georgia cursive";
		    
		    imageObj2.onload = function(){
		         cxt2.drawImage(imageObj2, 110-90/4 ,190-90/2);
		     };
			// Move it down by half the text height and left by half the text width 
		    cxt2.textBaseline = "top";
		    cxt2.fillText(partMPNames[2], 100-90/2 ,110-90/2);
		    cxt2.textBaseline = "center";
		    cxt2.fillText(percentsMP[2], 100-90/4 ,140-90/2);
			//END CIRCLES
	    }	    
	}
	
	//FIN
	
});


