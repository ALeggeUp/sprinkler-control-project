/**
 * app.js
 *
 * Copyright 2015-2016 [A Legge Up Consulting]
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

/*jshint strict:false */

'use strict';

/**
 * @ngdoc overview
 * @name sprinkler-control
 * @description # Sprinkler Control Webapp
 * 
 * Main module of the application.
 */
var sprinklerApp = angular.module('sprinkler-control',
        [ 'ngCookies', 'ngTouch', 'ngResource', 'ngRoute', 'sprinkler-main', 'sprinkler-zone' ]);

sprinklerApp.config([ '$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(false);
    
    $routeProvider.when('/zones', {
        templateUrl : 'views/zones.html',
        controller : 'ZoneController'
    }).when('/zone/:zoneId', {
        templateUrl : 'views/zone.html',
        controller : 'ZoneController'
    }).when('/schedules', {
        templateUrl : 'views/schedules.html',
        controller : 'ScheduleController'
    }).otherwise({
        redirectTo : '/zones'
    });
} ]);
