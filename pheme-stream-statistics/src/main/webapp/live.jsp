<html ng-app="liveView">
<head>
	<meta charset="UTF-8">
	<title>Pheme pipeline real-time monitor</title>
	<meta name="og:title" content="Kafka Real-time Visualization">
	<meta name="description" content="Pipeline live monitor.">
	<meta name="og:description" content="Pipeline live monitor.">

	<meta name="og:type" content="article">
	<link rel="stylesheet" href="./live/style.css">
	<link
		href="https://fonts.googleapis.com/css?family=Roboto:100,400,300,700"
		rel="stylesheet" type="text/css">
	<script type="text/javascript">
		var topic_name = "${topic}";
	</script>

</head>
<body>
	<div ng-controller="liveViewController" ng-cloak="">
		<div ng-class="{'viewBottom':view==='bottom'}" class="containerParty">
			<div style="width: 100%; float: left;" class="header">
				<div class="headerButtonsLeft">
					<div class="Inline">
						<div style="font-size: 10px; margin-top: 2px;">
							<div ng-show="connected">
								<div class="circle green"></div>
								Connected
							</div>
							<div ng-show="!connected">
								<div class="circle red"></div>
								Connecting...
							</div>
						</div>
						<span>{{topic}}</span>
					</div>
					<div class="sep"></div>
					<div class="Inline icons">
						<img
							ng-src="{{view === 'bottom' ? './live/img/barondownon.svg' : './live/img/barondownoff.svg'}}"
							ng-click="changeView('bottom')" style="height: 15px;">&nbsp;&nbsp;<img
							ng-src="{{view === 'side' ? './live/img/baronrighton.svg' : './live/img/baronrightoff.svg'}}"
							ng-click="changeView('side')" style="height: 15px;">
					</div>
					<div class="sep"></div>
					<!-- 
					<div class="Inline icons">
						<a href="/blog/kafka-realtime-visualization"
							style="color: #818181; text-decoration: none;" target="_blank">What
							am I looking at?</a>
					</div>
					 -->
					<div class="sep"></div>
				</div>
				<div class="headerButtonsRight"></div>
				<div style="position: absolute; top: 50px; width: 100%;">
					<div style="overflow-y: auto;"
						ng-style="{'height': getSamplesHeight() + 'px'}">
						<flow-chart style="width:100px;"></flow-chart>
						<div class="barchart">
							<bar-chart ng-show="inputs.length &gt; 0">
							<div id="barchartTitle" ng-class="{'viewBottom':view==='bottom'}">Incoming
								events</div>
							<div id="barchartTooltip" class="arrow_box"></div>
							<div id="barchartBars" ng-class="{'viewBottom':view==='bottom'}"></div>
							</bar-chart>
						</div>
						<div ng-hide="view==='bottom'" class="inputs">
							<div ng-repeat="input in inputs" class="input">
								<div class="inputTitle">
									<div style="margin-right: 3px;"
										ng-style="{'border-color':input.color}" class="doughnut"></div>
									<span>{{input.inputLabel}}</span>
								</div>
								<div style="margin-top: 15px;">
									<div class="statsTitle">Average Throughput</div>
									<div class="statsNumber">⚡ {{input.throughput | eps}} eps</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div ng-class="{'viewBottom':view==='bottom'}"
			style="position: relative;" class="containerSamples">
			<div
				style="width: 100%; float: right; white-space: nowrap; text-align: center;"
				class="header">
				<input ng-model="filter" placeholder="Filter"
					ng-change="updateFilter()" type="text"
					ng-class="{'viewBottom':view==='bottom'}" class="inputFilter">
				<div ng-click="togglePause()"
					style="font-size: 12px; padding-bottom: 5px; position: relative; left: -60px; top: -3px;"
					class="Inline smallBtn">
					<span
						ng-show="!pause &amp;&amp; !hoverPause &amp;&amp; !showEventPause">||
						pause</span><span ng-show="pause || hoverPause || showEventPause"
						style="font-size: 11px;">▶ play</span>
				</div>
			</div>
			<div ng-mouseover="hoverPauseSamples()"
				ng-mouseleave="hoverStartSamples()" ng-class="{'samplesOn':filter}"
				ng-init="preStyle='line'" class="samples full-height">
				<div style="text-align: center; margin-top: 50px; font-size: 15px;"
					ng-show="samples.length === 0">
					<span ng-show="filter">Looking for events matching your
						filter...</span><span ng-hide="filter">Waiting for events...</span>
				</div>
				<div style="overflow-y: auto;"
					ng-style="{'height': getSamplesHeight() + 'px'}">
					<div ng-repeat="sample in samples | filter: {filter: filter}"
						ng-class="{'sampleOpened':showPretty}"
						ng-click="showPretty = true;showEventPauseSamples()"
						class="sample">
						<div style="width: 90%; margin: 0 auto; padding: 10px 0;">
							<span ng-show="!showPretty"
								ng-bind-html="sample.event | highlight:filter:sampleLimit"
								class="Pointer"></span>
							<div
								ng-click="showPretty = false;showEventStartSamples(); $event.stopPropagation();"
								ng-show="showPretty" class="Inline sampleClose">Close X</div>
							<div ng-if="showPretty" class="prettyContainer">
								<pre ng-class="json"
									ng-bind-html="sample.event | prettify | highlight:filter"></pre>
							</div>
							<div style="margin-top: 8px;">
								<div class="Inline sampleInputLabel">
									<div style="margin-right: 2px;"
										ng-style="{'border-color': getColorFromInputLabel(sample.event.input_label)}"
										class="doughnutSmall"></div>
									{{sample.event.input_label}}
								</div>
								<div ng-click="showPretty = true;showEventPauseSamples()"
									ng-style="{'display':showPretty?'none':''}"
									class="Inline sampleShow">Show Event {...}</div>
								<div class="Inline sampleTimestamp">{{sample.event["@timestamp"]}}</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<link rel="stylesheet" href="./live/bower_components/c3/c3.min.css">
	<link rel="stylesheet" href="./live/c3.css">
	<script src="./live/bower_components/jquery/dist/jquery.min.js"></script>
	<script src="./live/bower_components/angular/angular.min.js"></script>
	<script
		src="./live/bower_components/angular-sanitize/angular-sanitize.min.js"></script>
	<script src="./live/bower_components/lodash/dist/lodash.min.js"></script>
	<script src="./socket.io/socket.io.js"></script>
	<script src="./live/bower_components/angular-socket-io/socket.min.js"></script>
	<script src="./live/app.js"></script>
	<script src="./live/utils.fctry.js"></script>
	<script src="./live/flowChart.drctv.js"></script>
	<script src="./live/barChart.drctv.js"></script>
	<script src="./live/cubic_spline_min.js"></script>
	<script src="./live/bower_components/sketch.js/js/sketch.js"></script>
	<script src="./live/demo.js"></script>
	
</body>
</html>