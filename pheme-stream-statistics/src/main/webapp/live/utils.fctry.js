angular.module('liveView')
.factory('utils', ['$rootScope', function($rootScope){
  var inputLabelMap = {}, inputLabelArray = [];
  var COLOURS = ['#f7301d','#d3354c', '#ae3a7c', '#8a3fab', '#6544da'];
  //var COLOURS = ['#5cdef3','#398ae5', '#69d9d8', '#6447dc'];

  //public
  function guid() {
    function s4() {
      return Math.floor((1 + Math.random()) * 0x10000)
        .toString(16)
        .substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
      s4() + '-' + s4() + s4() + s4();
}

  //public
  function getInputLabels(){
    return inputLabelArray;
  }

  //public
  function getRandomInt (min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
  }

  //public
  function getNormalRandomInt (avg, std) {
    var result = Math.round(((Math.random() + Math.random() + Math.random() + Math.random() + Math.random() + Math.random()) - 3) * Math.SQRT2 * std + avg);
    result = result<0 ? 0 : result;
    return result;
  }
  //private
  function hexToRgb(hex) {
      var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
      return result ? {
          r: parseInt(result[1], 16),
          g: parseInt(result[2], 16),
          b: parseInt(result[3], 16)
      } : null;
  }

  //private
  function componentToHex(c) {
      var hex = c.toString(16);
      return hex.length == 1 ? "0" + hex : hex;
  }

  //private
  function rgbToHex(rgb) {
      return "#" + componentToHex(rgb.r) + componentToHex(rgb.g) + componentToHex(rgb.b);
  }

  //private
  function saturate(rgb, saturation){
    var gray = 0.2989*rgb.r + 0.5870*rgb.g + 0.1140*rgb.b;
    var saturated = {
      r: Math.round(gray*(1-saturation)+ rgb.r*saturation),
      g: Math.round(gray*(1-saturation)+ rgb.g*saturation),
      b: Math.round(gray*(1-saturation)+ rgb.b*saturation)
    };
    return saturated;
  }

  //private
  function hashInputLabel(inputLabel){
    if (typeof inputLabelMap[inputLabel] !== 'undefined'){
      return inputLabelMap[inputLabel];
    }

    var hash = inputLabelArray.length;
    inputLabelMap[inputLabel] = hash;
    inputLabelArray.push({inputLabel:inputLabel,hash:hash});
    $rootScope.$broadcast('inputLabel added', inputLabel);
    return hash;
  }

  //public
  function getIndexFromInputLabel(inputLabel) {
    return hashInputLabel(inputLabel);
  }

  //public
  function getHeightFromInputLabel(canvasHeight, inputLabel) {
    var index = getIndexFromInputLabel(inputLabel);
    return (canvasHeight-50)/inputLabelArray.length*(index+0.5)+25;
  }

  //public
  var CANVAS_HEIGHT = 400;
  var CANVAS_WIDTH = 70;
  function getCanvasSize() {
    return {
      width:CANVAS_WIDTH,
      height:CANVAS_HEIGHT
    };
  }

  //public
  function updateCanvasSize(width, height) {
    CANVAS_WIDTH = width;
    CANVAS_HEIGHT = height;
  }

  //public
  function getColorFromInputLabel(inputLabel, filtered) {
    var index = getIndexFromInputLabel(inputLabel);
    var relativeIndex = index/(inputLabelArray.length)*(COLOURS.length-1);

    var pre = hexToRgb(COLOURS[Math.floor(relativeIndex)]);
    var post = hexToRgb(COLOURS[Math.ceil(relativeIndex)]);

    var avg = {
      r: Math.round((pre.r+post.r)/2),
      g: Math.round((pre.g+post.g)/2),
      b: Math.round((pre.b+post.b)/2)
    };

    if (filtered === false){
      avg = {
        r:100,
        g:100,
        b:100
      };
    }

    var result = rgbToHex(avg);
    return result;
  }

  function updateColours(colours) {
      COLOURS = colours;
  }

  return {
    guid: guid,
    getInputLabels: getInputLabels,
    getRandomInt: getRandomInt,
    getNormalRandomInt: getNormalRandomInt,
    getIndexFromInputLabel: getIndexFromInputLabel,
    getColorFromInputLabel: getColorFromInputLabel,
    getHeightFromInputLabel: getHeightFromInputLabel,
    getCanvasSize: getCanvasSize,
    updateCanvasSize: updateCanvasSize,
    updateColours: updateColours
  };
}]);
