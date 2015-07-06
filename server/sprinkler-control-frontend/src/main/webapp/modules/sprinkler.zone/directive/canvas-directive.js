/**
 * canvas-directive.js
 *
 * Copyright 2015 [A Legge Up Consulting]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function() {

    'use strict';

    var app = angular.module('sprinkler-zone');

    app.directive('timer', function() {
        
        var ctx;
        var width = 0;
        var height = 0;
        var duration = 25;

        function link(scope, element, attrs) {
            var context = element[0].getContext('2d');

            var width = element[0].width;
            var height = element[0].height;
            var x = element[0].width / 2;
            var y = element[0].height / 2;
            var radius = 65;
            var startAngle = 0; // 0.5 * Math.PI;
            var endAngle = 0; // 1 * Math.PI;
            var counterClockwise = false;
            var zone = null;
            
            function updateTimer() {
                resetContext();
                drawTimerText();
                drawTimerBackground();
                drawTimer();
            }
            
            function resetContext() {
                context.clearRect(0, 0, width, height);
                context.globalAlpha = 1.0;
            }
            
            function drawTimerText() {
                context.font = '20pt sans-serif';
                context.textAlign = 'center';
                context.textBaseline = 'middle';
                context.fillStyle = '#428bca';
                context.fillText((zone.durationInSeconds/60) + 'min', x, y);
            }
            
            function drawTimerBackground() {
                context.beginPath();
                context.arc(x, y, radius, 0, 2 * Math.PI, counterClockwise);
                context.lineWidth = 25;
            
                context.globalAlpha = 0.7;

                context.strokeStyle = '#eeeeee';
                context.lineCap = 'round';
                context.stroke();
            }
            
            function drawTimer() {
                var sa = 0.5 * Math.PI;
                var ea = 2 * Math.PI * ( ( moment().valueOf() - zone.lastStart ) / 1000 ) / zone.durationInSeconds;
                
                context.beginPath();
                context.arc(x, y, radius, sa, sa + ea, counterClockwise);
                context.lineWidth = 15;

                // line color
                if (zone.state === 'RUNNING') {
                    context.strokeStyle = '#428bca';
                } else {
                    context.strokeStyle = '#ae0c0c';
                }
                context.lineCap = 'round';
                context.stroke();
            }
            
            scope.$watch(attrs.zone, function(value) {
                zone = value;
                updateTimer();
              });
            
            element.bind("click", function(e) {
                scope.$apply(function() {
                    zone.durationInSeconds = (zone.durationInSeconds % 3600) + 300;
                    updateTimer();
                    scope.update({zone : zone});
                });
                
                e.preventDefault();
                e.stopPropagation();
                return false;
            });
            
            element.bind("dblclick", function(e) {
                e.preventDefault();
                e.stopPropagation();
                return false;
            });
        }
        
        return {
            restrict : "EA",
            scope : {
                zone : '=zone',
                update : '&'
            },
            link : link
        }

    });
})();