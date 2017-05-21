angular.module('liveView')
.directive('barChart', ['utils', function(utils){
  var CHART_HEIGHT = 50;
  var MAX_POINTS = 180;

  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      var barChart = elm.find("#barchartBars");
      var maxPointValue = 1;
      var arrPoints = [];
      function drawStackedBarChart(stats){
        if (!stats)
          return;

        if (arrPoints.length === 0){
          stats.forEach(addPointToStackedChart);
        }
        else {
          addPointToStackedChart(stats[stats.length-1]);
        }
      }

      function addPointToStackedChart(point){
        getPointStackedBars(point)
        .appendTo(barChart);

        if (arrPoints.length > MAX_POINTS){
          $(".stackedBar").first().remove();
          arrPoints.shift();
        }

        if (maxPointValue != _.max(arrPoints)){
          maxPointValue = _.max(arrPoints);
          updateBarsHeight();
        }
      }

      function sortObject(obj) {
        return Object.keys(obj).sort().reduce(function (result, key) {
            result[key] = obj[key];
            return result;
        }, {});
      }

      function getPointStackedBars(point){
        var container = $("<div>").addClass("stackedBar");
        var barValue = 0;
        point = sortObject(point);

        _.forOwn(point, function(value, input_label){
          barValue += value;
          $("<div>")
          .addClass("pointBar")
          .css({
            "background": utils.getColorFromInputLabel(input_label),
            height: (value/maxPointValue*CHART_HEIGHT)
          })
          .data({'value':value,"input_label":input_label})
          .appendTo(container);
        });

        arrPoints.push(barValue);

        // container.hover(handleEnter.bind(this,$(this)),handleLeave.bind(this,$(this)));
        container.hover(function(){
          handleEnter($(this));
        },function(){
          handleLeave($(this));
        });
        return container;
      }

      var tooltip = $("#barchartTooltip");
      //tooltip.css("bottom")
      function handleEnter(_this){
        _this.addClass("stackedBarHover");

        if (_this.find(".pointBar").length === 0)
          return false;

        tooltip.css({
          opacity:1,
          left:calcLeft(_this)
        }).show();

        tooltip.html(buildTooltipHtml(_this));

        tooltip.css({
          "margin-top":calcTop(tooltip)
        });
      }
      function handleLeave(_this){
        _this.removeClass("stackedBarHover");
        tooltip.css({
          opacity:0
        });
      }
      function buildTooltipHtml(_this){
        var container = $("<div>");
        _this.find(".pointBar").each(function(i,bar){
          var $bar = $(bar);
          $("<div>").addClass("doughnut Inline").css({"border-color":$bar.css("background-color"),"margin-right":"6px","top":"1px"}).appendTo(container);
          $("<div style='font-weight:100;'>").addClass("Inline").text($bar.data("input_label") + ": " +$bar.data("value") + " events").appendTo(container);
          $("<hr>").appendTo(container);
        });
        return container.html();
      }

      function calcLeft(_this){
        var left = _this.position().left-(tooltip.outerWidth()/2);
        if (left+tooltip.outerWidth() > $(window).width()){
          left = $(window).width()-tooltip.outerWidth();
        }
        //todo: handle right side
        return left;
      }

      function calcTop(_this){
        console.log(_this.height());
        return (_this.height()*-1)-40;
      }

      function updateBarsHeight(){
        $(".pointBar").each(function(i,bar){
          var $bar = $(bar);
          $bar.css({
            height: ($bar.data("value")/maxPointValue)*CHART_HEIGHT
          });
        });
      }

      scope.$on('socket:stats', function(ev, stats){
        drawStackedBarChart(stats['all']);
      });
    }
  };
}]);
