angular.module('liveView')
.directive('flowChart', ['$window', 'utils', 'FPSCounter', function($window, utils, FPSCounter){
  var COLOURS = ['#f7301d','#d3354c', '#ae3a7c', '#8a3fab', '#6544da'];
  var MAX_PARTICLES = 2500;
  var CIRCLE_RADIUS = 7;
  var CANVAS_HEIGHT = 400;
  var wanderCurve = new MonotonicCubicSpline([0,0.01,0.1,0.6,0.8, 1],[0,5, 5,3,0, 0]);
  var gravityCurve = new MonotonicCubicSpline([0,0.2, 0.4,0.6,1],[0,0.3,0.8,3,1]);
  var radiusCurve = new MonotonicCubicSpline([0,0.2,0.6,1],[0,2,1,1]);

  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      var particles = [];
      var pool = [];

      // ----------------------------------------
      // Particle
      // ----------------------------------------

      function Particle( x, y, radius ) {
        this.init( x, y, radius );
      }

      Particle.prototype = {

        init: function( x, y, radius ) {

            this.alive = true;

            this.wander = 0.15;
            this.theta = random( TWO_PI/4, TWO_PI/4+0.01 );
            this.drag = 0.92;
            this.color = '#fff';

            this.x = x || 0.0;
            this.y = y || 0.0;

            this.vx = 0.0;
            this.vy = 0.0;
        },

        move: function() {

            this.x += this.vx*62/fpsCounter.get();
            this.y += this.vy*62/fpsCounter.get();

            this.vx *= this.drag * 0.6;
            this.vy *= this.drag * 0.6;

            this.theta = TWO_PI/4;//random( -0.001 + TWO_PI/4, 0.001 + TWO_PI/4) * this.wander * this.radius()/100; //TWO_PI/4;//+= random( -0.001, 0.001 ) * this.wander;
            this.vx += sin( this.theta ) * 7;
            this.vy += cos( this.theta ) * 25 -  (this.y-demo.canvas.height/2-this.yOffset-10*this.offsetWander*this.wanderFunction())/1000*5*this.vx*this.gravityFunction();

            this.alive =  this.x < demo.canvas.width;
        },

        draw: function( ctx ) {

            ctx.beginPath();
            ctx.arc( this.x, this.y, this.radius(), 0, TWO_PI );
            ctx.fillStyle = this.color;
            ctx.fill();
        },

        relX: function(){
          return (this.x-75*$window.devicePixelRatio)/(demo.canvas.width-75*$window.devicePixelRatio);
        },

        wanderFunction: function(){
          return wanderCurve.interpolate(this.relX());
        },
        gravityFunction: function(){
          return gravityCurve.interpolate(this.relX());
        },
        radius: function(){
            return 1.5*window.devicePixelRatio*radiusCurve.interpolate(this.relX());
        }
      };

      var demo = Sketch.create({
        container: elm.get(0),
        autopause: false,
        retina: true
      });

      function adjustSize(){
        var canvas = demo.canvas;
        var canvasSize = utils.getCanvasSize();
        canvas.height = canvasSize.height;
        canvas.width = $window.innerWidth * (canvasSize.width/100);
        if ($window.devicePixelRatio > 1) {
          var canvasWidth = canvas.width;
          var canvasHeight = canvas.height;

          canvas.width = canvasWidth * $window.devicePixelRatio;
          canvas.height = canvasHeight * $window.devicePixelRatio;
          canvas.style.width = canvasWidth + "px";
          canvas.style.height = canvasHeight + "px";

          demo.scale($window.devicePixelRatio, $window.devicePixelRatio);
        }
      }

      angular.element($window).resize(adjustSize);
      adjustSize();

      //simulate();
      demo.setup = function() {
      };

      demo.spawn = function( x, y, radius, color, index, msg ) {

        if ( particles.length >= MAX_PARTICLES )
            pool.push( particles.shift() );

        particle = pool.length ? pool.pop() : new Particle();
        particle.init( x, y, radius || random( 5, 40 ) );

        particle.wander = random( 0.5, 2.0 );
        particle.offsetWander = random (-0.5,0.5);
        particle.color = color || COLOURS[0];
        particle.drag = random( 0.9, 0.99 );

        particle.yOffset = index * 10 * window.devicePixelRatio;
        theta = random( -0.01, 0.01);
        force = random( 2, 8 );

        particle.vx = cos( theta ) * force;
        particle.vy = sin( theta ) * force;

        particle.msg = msg;
        particles.push( particle );
      };

      demo.update = function() {
        var i, particle;

        for ( i = particles.length - 1; i >= 0; i-- ) {

            particle = particles[i];

            if ( particle.alive ) particle.move();
            else pool.push( particles.splice( i, 1 )[0] );
        }
      };

      function drawCircle(inputLabel){
        var ctx = demo.canvas.getContext('2d');
        var radius = CIRCLE_RADIUS*$window.devicePixelRatio;
        var centerX = 50*$window.devicePixelRatio;
        var centerY = utils.getHeightFromInputLabel(demo.canvas.height, inputLabel);
        var grd=ctx.createLinearGradient(centerX-CIRCLE_RADIUS,centerY-CIRCLE_RADIUS,centerX+CIRCLE_RADIUS,centerY+CIRCLE_RADIUS*4);
        grd.addColorStop(0, utils.getColorFromInputLabel(inputLabel));
        grd.addColorStop(1, "black");
        ctx.beginPath();
        ctx.arc(centerX, centerY, radius, 0, 2 * Math.PI, false);
        ctx.lineWidth = 3.5*$window.devicePixelRatio;
        ctx.strokeStyle = grd;
        ctx.stroke();
        ctx.fillStyle = grd;
      }

      function drawInputLabel (inputLabel){
        var fontSize = Math.round(12.5*$window.devicePixelRatio);
        var margin = Math.round(27.5*$window.devicePixelRatio);
        var centerX = 50*$window.devicePixelRatio;
        var ctx = demo.canvas.getContext('2d');
        ctx.textAlign = 'center';
        ctx.font = '100 '+fontSize+'px Roboto';
        ctx.fillStyle = '#ddd';
        ctx.fillText(inputLabel, centerX, utils.getHeightFromInputLabel(demo.canvas.height, inputLabel)+margin);
        drawCircle(inputLabel);
      }

      var fpsCounter = new FPSCounter();
      demo.draw = function() {
        var inputLabelArray = utils.getInputLabels();
        demo.globalCompositeOperation  = 'lighter';

        for (var i = particles.length - 1; i >= 0; i--) {
            particles[i].draw( demo );
        }

        for (var j =0 ; j<inputLabelArray.length;j++){
          drawInputLabel(inputLabelArray[j].inputLabel);
        }

        fpsCounter.tick();
      };

      function createBall(msg) {
        var index =utils.getIndexFromInputLabel(msg.l);
        var color = utils.getColorFromInputLabel(msg.l, msg.f);
        var randomX = Math.round(75*$window.devicePixelRatio);
        var randomY = utils.getHeightFromInputLabel(demo.canvas.height, msg.l);
        demo.spawn(randomX, randomY, Math.log(msg.s), color, index, msg);
      }

      scope.$on('socket:aggregated', function(ev, counts){
          counts['unfiltered'] = {};
          _.forOwn(counts['all'], function(count, input_label){
            if (counts['filter']){
              counts['unfiltered'][input_label] = counts['all'][input_label]-(counts['filter'][input_label]||0);
            }
            else {
              counts['unfiltered'] = counts['all'];
            }
          });

          _.forOwn(counts['filter'], function(count, input_label){
            for (var i=0;i<count;i++){
              setTimeout(createBall.bind(this, {l:input_label,s:30,f:true}),
                utils.getRandomInt(0,3000));
            }
          });

          _.forOwn(counts['unfiltered'], function(count, input_label){
            for (var i=0;i<count;i++){
              setTimeout(createBall.bind(this, {l:input_label,s:30,f:false}),
                utils.getRandomInt(0,3000));
            }
          });

      });
    }
  };
}]);
