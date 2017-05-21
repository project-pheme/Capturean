var div = '<div class="form-group" id="form-group-1" style="outline: 2px solid #ddd;"> \
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
\
	<label class="col-sm-1 control-label" for="textinput">type</label> \
	<div class="col-sm-3"> \
		<select class="form-control"  name="sortedhistorical-1" id="sortedhistorical-1"> \
			<option value="sorted">Sorted</option> \
			<option value="historical">Historical</option> \
		</select> \
	</div> \
	\
	<label class="col-sm-2 control-label historicalcount-text" for="textinput" style="display: none;">historical count</label> \
	<div class="col-sm-2 historicalcount-value" style="display: none;"> \
		<input type="text" placeholder="count" id="historicalcount-1" \
			name="historicalcount-1" class="form-control"> \
	</div> \
</div>';

var div_reddit = '<div class="form-group" id="form-group-1" style="outline: 2px solid #ddd;"> \
	<label class="col-sm-1 control-label" for="textinput">Type</label> \
	<div class="col-sm-2"> \
		<input type="text" placeholder="reddit" id="type-1" name="type-1" class="form-control" \
			disabled> \
	</div> \
\
	<label class="col-sm-2 control-label" for="textinput">Keywords</label> \
	<div class="col-sm-7"> \
		<input type="text" placeholder="keywords"  id="redditkeywords-1" name="redditkeywords-1" \
			class="form-control"> \
	</div> \
	<label class="col-sm-2 control-label" for="textinput">Reddit Type</label> \
	<div class="col-sm-3"> \
		<select class="form-control"  name="reddittype-1" id="reddittype-1"> \
			<option value="subreddit" selected="selected">subreddit</option> \
			<option value="post">post</option> \
		</select> \
	</div> \
	<label class="col-sm-2 control-label redditsubreddits-text" for="textinput">Subreddits</label> \
	<div class="col-sm-5 redditsubreddits-value"> \
		<input type="text" placeholder="subreddits" id="redditsubreddits-1" \
			name="redditsubreddits-1" class="form-control"> \
	</div> \
	<label class="col-sm-2 control-label redditpost-text" for="textinput" style="display: none;">Post</label> \
	<div class="col-sm-5 redditpost-value" style="display: none;"> \
		<input type="text" placeholder="post" id="redditpost-1" \
			name="redditpost-1" class="form-control"> \
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
		elem.find("#sortedhistorical-1").attr("name", "sortedhistorical-" + newid);
		elem.find("#sortedhistorical-1").attr("id", "sortedhistorical-" + newid);
		elem.find("#historicalcount-1").attr("name", "historicalcount-" + newid);
		elem.find("#historicalcount-1").attr("id", "historicalcount-" + newid);
		
		elem.find("#sortedhistorical-" + newid).on('change', function(ev){
			//get value selected
			sortedhistorical = $(this).find(":selected").val();
			
			if (sortedhistorical == "sorted"){
				$(this).parent().parent().find(".historicalcount-value").hide();
				$(this).parent().parent().find(".historicalcount-text").hide();
			}
			else if (sortedhistorical == "historical"){
				$(this).parent().parent().find(".historicalcount-value").show();
				$(this).parent().parent().find(".historicalcount-text").show();
			}
		});
		
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
	
	$('#add-reddit-button').on('click', function (e) {
		newid = counter + 1;
		elem = $(div_reddit);
		elem.attr('id', 'form-group-' + (newid));
		elem.find("#type-1").attr("name", "type-" + newid);
		elem.find("#type-1").attr("id", "type-" + newid);
		elem.find("#redditkeywords-1").attr("name", "redditkeywords-" + newid);
		elem.find("#redditkeywords-1").attr("id", "redditkeywords-" + newid);
		elem.find("#reddittype-1").attr("name", "reddittype-" + newid);
		elem.find("#reddittype-1").attr("id", "reddittype-" + newid);
		elem.find("#redditsubreddits-1").attr("name", "redditsubreddits-" + newid);
		elem.find("#redditsubreddits-1").attr("id", "redditsubreddits-" + newid);
		elem.find("#redditpost-1").attr("name", "redditpost-" + newid);
		elem.find("#redditpost-1").attr("id", "redditpost-" + newid);
		
		elem.find("#reddittype-" + newid).on('change', function(ev){
			//get value selected
			reddittype = $(this).find(":selected").val();
			
			if (reddittype == "subreddit"){
				$(this).parent().parent().find(".redditsubreddits-value").show();
				$(this).parent().parent().find(".redditsubreddits-text").show();
				$(this).parent().parent().find(".redditpost-value").hide();
				$(this).parent().parent().find(".redditpost-text").hide();
			}
			else if (reddittype == "post"){
				$(this).parent().parent().find(".redditpost-value").show();
				$(this).parent().parent().find(".redditpost-text").show();
				$(this).parent().parent().find(".redditsubreddits-value").hide();
				$(this).parent().parent().find(".redditsubreddits-text").hide();
			}
		});
		
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
	
	$("select[id^=sortedhistorical]").on('change', function(ev){
		//get value selected
		sortedhistorical = $(this).find(":selected").val();
		
		if (sortedhistorical == "sorted"){
			$(this).parent().parent().find(".historicalcount-value").hide();
			$(this).parent().parent().find(".historicalcount-text").hide();
		}
		else if (sortedhistorical == "historical"){
			$(this).parent().parent().find(".historicalcount-value").show();
			$(this).parent().parent().find(".historicalcount-text").show();
		}
	});
	
	$("select[id^=reddittype]").on('change', function(ev){
		//get value selected
		reddittype = $(this).find(":selected").val();
		
		if (reddittype == "subreddit"){
			$(this).parent().parent().find(".redditsubreddits-value").show();
			$(this).parent().parent().find(".redditsubreddits-text").show();
			$(this).parent().parent().find(".redditpost-value").hide();
			$(this).parent().parent().find(".redditpost-text").hide();
		}
		else if (reddittype == "post"){
			$(this).parent().parent().find(".redditpost-value").show();
			$(this).parent().parent().find(".redditpost-text").show();
			$(this).parent().parent().find(".redditsubreddits-value").hide();
			$(this).parent().parent().find(".redditsubreddits-text").hide();
		}
	});
	
	
	
	console.info("loaded!");
	
});
