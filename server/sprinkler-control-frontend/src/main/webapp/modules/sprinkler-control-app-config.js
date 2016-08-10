/**
 * sprinkler-control-app-config.js
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

(function() {

    'use strict';

    angular.module('sprinkler-control-app').config(['$stateProvider', '$urlRouterProvider',
                                                    function($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise('/zones');

        $stateProvider
        .state('register', {
            url: '/register',
            views: {
                'navigation': {
                    templateUrl: ''
                },
                'body': {
                    templateUrl: 'modules/sprinkler.login/view/register.html',
                    controller: 'RegisterController as vm'
                }
            }
        });

        $stateProvider
        .state('login', {
            url: '/login',
            views: {
                'navigation': {
                    templateUrl: ''
                },
                'body': {
                    templateUrl: 'modules/sprinkler.login/view/login.html',
                    controller: 'LoginController as vm'
                }
            }
        });

        $stateProvider
        .state('main', {
            url: '/main',
            views: {
                'navigation': {
                    templateUrl: 'modules/sprinkler.main/view/navigation.html',
                    controller: 'NavController'
                },
                'body': {
                    templateUrl: 'modules/sprinkler.main/view/main.html',
                    controller: 'MainController as vm'
                }
            }
        });

        $stateProvider
        .state('zones', {
            url:'/zones',
            views: {
                'navigation': {
                    templateUrl: 'modules/sprinkler.main/view/navigation.html',
                    controller: 'NavController'
                },
                'body': {
                    templateUrl: 'modules/sprinkler.zone/view/zones.html',
                    controller: 'ZoneController'
                }
            }
        })
        .state('zones.detail', {
            url: '/zone/:id',
            templateUrl: 'modules/sprinkler.zone/view/shows-detail.html',
            controller: 'ZoneDetailController'
        });

    }]);

})();
