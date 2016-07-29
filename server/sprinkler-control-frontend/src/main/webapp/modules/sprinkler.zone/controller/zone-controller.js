/**
 * zone-controller.js
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

(function() {

    'use strict';

    var app = angular.module('sprinkler-zone');

    app.controller('ZoneController', [ '$scope', '$http', '$location', '$interval', 'Zone',
            function($scope, $http, $location, $interval, Zone) {

                $scope.viewClass = 'zoneView';

                $scope.go = function(path) {
                    $location.path('/zone/' + path);
                };

                $scope.loadZones = function() {
                    $scope.isLoading = true;
                    var zones = Zone.query().$promise.then(function(zones) {
                        $scope.zones = zones;
                        $scope.isLoading = false;
                    });
                };

                $scope.loadZone = function(zone) {
                    Zone.get({
                        zoneId : zone.id
                    }, zone).$promise.then(function(updated) {
                        $scope.zones[$scope.zones.indexOf(zone)] = updated;
                    });
                };

                $scope.updateZone = function(zone) {
                    Zone.update({
                        zoneId : zone.id
                    }, zone).$promise.then(function(updated) {
                        $scope.zones[$scope.zones.indexOf(zone)] = updated;
                    });
                };

                $scope.startZone = function(zone) {
                    Zone.start({
                        zoneId : zone.id
                    }, zone).$promise.then(function(updated) {
                        $scope.zones[$scope.zones.indexOf(zone)] = updated;
                    });
                };

                $scope.isDisabled = function(zone) {
                    return zone.state === 'DISABLED';
                };

                $scope.zoneActionLabel = function(zone) {
                    var actionLabel = '';
                    switch (zone.state) {
                        case 'NOT_RUNNING':
                            actionLabel = "Start";
                            break;
                        case 'RUNNING':
                            actionLabel = "Stop";
                            break;
                        default:
                            actionLabel = "Start";
                    }

                    return actionLabel;
                };

                $scope.loadZones();

                $interval(function() {
                    for (var i = 0; i < $scope.zones.length; i++) {
                        if ($scope.zones[i].state === 'RUNNING') {
                            $scope.loadZone($scope.zones[i]);
                        }
                    }
                }, 5000);
            }]);
})();
