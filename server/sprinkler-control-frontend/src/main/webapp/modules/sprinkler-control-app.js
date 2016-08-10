/**
 * sprinkler-control-app.js
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
var sprinklerApp = angular.module('sprinkler-control-app',
        [ 'ngCookies', 'ngTouch', 'ngResource', 'ui.router',
          'sprinkler-login-module',
          'sprinkler-main-module',
          'sprinkler-schedule-module',
          'sprinkler-zone-module'
          ]
);

sprinklerApp.run(['$rootScope', '$location', '$cookieStore', '$http', '$state', '$stateParams',
                  function($rootScope, $location, $cookieStore, $http, $state, $stateParams) {
    $rootScope.$state = $state;
    $rootScope.$stateParams = $stateParams;

    // keep user logged in after page refresh
    $rootScope.globals = $cookieStore.get('globals') || {};
    if ($rootScope.globals.currentUser) {
        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata; // jshint ignore:line
    }

    $rootScope.$on('$locationChangeStart', function (event, next, current) {
        // redirect to login page if not logged in and trying to access a restricted page
        var restrictedPage = $.inArray($location.path(), ['/login', '/register']) === -1;
        var loggedIn = $rootScope.globals.currentUser;
        if (restrictedPage && !loggedIn) {
            $location.path('/login');
        }
    });
} ]);
