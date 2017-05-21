angular.module('liveView',['btford.socket-io', 'ngSanitize'])

.service('liveViewService',['$http', function($http){
  var liveViewService = {};

  return liveViewService;
}])

.factory('socket', ['socketFactory', function(socketFactory){
  var socket = socketFactory();
  socket.forward(['stats', 'aggregated', 'samples', 'filter']);
  return socket;
}])

.factory('FPSCounter', function(){
  function FPSCounter(){
    this.fps = 62;
    this.fpsFilter = 100;
    this.lastUpdate = (new Date())*1;
  }

  FPSCounter.prototype.tick = function(){
    var thisFrameFPS = 1000 / ((now=new Date()) - this.lastUpdate);
    if (now!=this.lastUpdate){
      this.fps += (thisFrameFPS - this.fps) / this.fpsFilter;
      this.lastUpdate = now;
    }
  };

  FPSCounter.prototype.get = function(){
    return this.fps;
  };
  return FPSCounter;
})
.filter('highlight', function($sce) {
  return function(str, termsToHighlight,sampleLimit) {


    if (_.isObject(str)){
      str = JSON.stringify(str, undefined, 2);
    }


    //substirng but show the match
    if (sampleLimit){
      var iStart = str.indexOf(termsToHighlight)-30;
      if (iStart < 0)
        iStart=0;
      str = str.substring(iStart,iStart+sampleLimit);
    }


    if (typeof termsToHighlight === "undefined" || termsToHighlight === ""){
      return str;
    }

    // Regex to simultaneously replace terms
    var regex = new RegExp('(' + termsToHighlight + ')', 'g');
    return $sce.trustAsHtml(str.replace(regex, '<span class="highlight">$&</span>'));
  };
})
.filter('prettify', function () {
  function syntaxHighlight(json) {
    if (typeof json === "string"){
      try{
        json = JSON.parse(json);
      } catch(err){
        console.log("Error parsing string to json",err);
      }
    }

    json = JSON.stringify(json, undefined, 2);
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
      var cls = 'number';
      if (/^"/.test(match)) {
        if (/:$/.test(match)) {
            cls = 'key';
        } else {
            cls = 'string';
        }
    } else if (/true|false/.test(match)) {
        cls = 'boolean';
    } else if (/null/.test(match)) {
        cls = 'null';
      }
      return '<span class="' + cls + '">' + match + '</span>';
    });
  }

  return syntaxHighlight;
})
.filter('eps', function(){
  function epsFilter(eps){
    if (eps===0){
      return '0';
    }
    if (eps<0.5){
      return '>0';
    }
    return String(Math.round(eps));
  }

  return epsFilter;
})
.controller('liveViewController', ['$scope', '$window', '$location', 'liveViewService', 'socket', 'utils', function($scope, $window, $location, liveViewService, socket, utils){
  $scope.samples = [];
  $scope.maxSamples = 30;
  $scope.view = "side";
  $scope.sampleLimit = 215;

  $scope.clientName = "gofundme";
  $scope.$on('inputLabel added', function(){
    var inputLabels = utils.getInputLabels();
    $scope.inputs = [];
    inputLabels.forEach(function(inputLabel){
      var input = {
        inputLabel: inputLabel.inputLabel,
        color: utils.getColorFromInputLabel(inputLabel.inputLabel),
        throughput: 0,
        avgEventSize: utils.getRandomInt(100, 10000)
      };
      $scope.inputs.push(input);
    });
  });

  var T = 3;
  $scope.$on('socket:aggregated', function(ev,counts){
    for (var index in $scope.inputs){
      var input = $scope.inputs[index];
      var count = 0;
      if (counts['all'][input.inputLabel]) {
        count = counts['all'][input.inputLabel];
      }
      input.throughput = input.throughput*(T-1)/T+count/T;
    }
    });

  var timeouts = [];
  function addSample(sample){
    timeouts.pop();

    if (stopSamples){
      return;
    }

    if ($scope.pause||$scope.hoverPause||$scope.showEventPause){
      return;
    }

    if ($scope.filter && (sample.filter!=$scope.filter)){
      return;
    }

    $scope.$apply(function(){
      if ($scope.samples.length >= $scope.maxSamples){
        $scope.samples.pop();
      }
      $scope.samples.unshift(sample);
    });
    $scope.getColorFromInputLabel = utils.getColorFromInputLabel;
  }

  var stopSamples = false;
  var inProgress = false;

  $scope.changeView = function(view){
    $scope.view = view;

    if ($scope.view == 'side'){
      utils.updateCanvasSize(70,400);
      $scope.sampleLimit = 215;
    } else if ($scope.view == 'bottom'){
      utils.updateCanvasSize(100,180);
      $scope.sampleLimit = 4096;
    }
    $(window).trigger('resize');
  };

  $scope.getSamplesHeight = function(){
    if ($scope.view == 'side'){
      return  $window.innerHeight-61;
    } else if ($scope.view == 'bottom'){
      return $window.innerHeight-61-180-75;
    }
  };


  $scope.togglePause = function(){
    if ($scope.pause || $scope.showEventPause){
      return $scope.startSamples();
    }
    $scope.pauseSamples();
  };

  $scope.pauseSamples = function(){
    $scope.pause = true;
  };

  $scope.startSamples = function(){
    $scope.pause = false;
    $scope.hoverPause = false;
    $scope.showEventPause = false;
  };

  $scope.hoverPauseSamples = function(){
    if ($scope.samples.length){
      $scope.hoverPause = true;
    }
  };

  $scope.hoverStartSamples = function(){
    $scope.hoverPause = false;
  };

  $scope.showEventPauseSamples = function(){
      $scope.showEventPause = true;
  };

  $scope.showEventStartSamples = function(){
      $scope.showEventPause = false;
  };

  $scope.updateFilter = function(dontEmit){
    if (inProgress){
      stopSamples = true;
    }

    timeouts.forEach(function(timeout){
      clearTimeout(timeout);
    });

    $scope.pause = false;
    $scope.showEventPause = false;
  };

  //TODO setTimeout
  $scope.connected = true;
  //$scope.topic = $location.search().topic || 'pheme_capture';
  $scope.topic = topic_name; // || 'pheme_capture';

  $scope.updateFilter = function(dontEmit){
    if (inProgress){
      stopSamples = true;
    }

    timeouts.forEach(function(timeout){
      clearTimeout(timeout);
    });

    if (!dontEmit){
      socket.emit('filter', $scope.filter);
    }
    $scope.pause = false;
    $scope.showEventPause = false;
  };

  socket.on('connect', function(){
    $scope.connected = true;
    //$scope.topic = 'pheme_capture';
    $scope.topic = topic_name;
    socket.emit('topic', $scope.topic);
  });

  socket.on('disconnect', function(){
    $scope.connected = false;
  });

  $scope.$on('socket:samples', function(ev, samples){
    inProgress = true;
    samples.forEach(function(sample, i){
        if (!stopSamples){
          var timeout = setTimeout(addSample.bind(this, sample), i/samples.length*1000);
          timeouts.push(timeout);
        }
    });
    inProgress = false;
    stopSamples = false;
  });
}]);
