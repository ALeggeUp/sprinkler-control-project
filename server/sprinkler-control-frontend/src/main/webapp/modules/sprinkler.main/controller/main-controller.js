/**
 * main-controller.js
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

    /**
     * @ngdoc function
     * @name test3App.controller:MainCtrl
     * @description # MainCtrl Controller of the test3App
     */
    angular.module('sprinkler-main', [ 'ui.bootstrap', 'ngRoute' ]).controller('MainController',
            [ '$scope', '$location', '$modal', '$cookies', '$log', function($scope, $location, $modal, $cookies, $log) {

                $scope.isLocation = function(location) {
                    return location === $location.path();
                };

                $scope.items = [ 'item1', 'item2', 'item3' ];

                $scope.debugMode = false;

                $scope.deleteEvent = function(id) {
                    window.alert('delete ' + id);
                };

                $scope.debug = function() {
                    $scope.debugMode = !$scope.debugMode;
                    var modalInstance = $modal.open({
                        templateUrl : 'myModalContent.tpl.html',
                        controller : 'DebugModeModalController',
                        resolve : {
                            items : function() {
                                return $scope.items;
                            }
                        }
                    });

                    modalInstance.result.then(function(urlOverride) {
                        // ok
                        $scope.debugMode = true;
                        $scope.urlOverride = urlOverride;
                        $cookies.put('urlOverride', $scope.urlOverride);
                    }, function() {
                        // cancel
                        $log.info('Modal dismissed at: ' + new Date());
                    });
                };

            } ])
})();
