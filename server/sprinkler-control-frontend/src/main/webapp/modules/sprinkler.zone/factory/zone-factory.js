/**
 * zone.js
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

    angular.module('sprinkler-zone-module').factory('Zone',
            [ '$resource', '$cookies', function($resource, $cookies) {

                var urlOverride = $cookies.get('urlOverride');

                var prefix = "";
                if (urlOverride !== null && urlOverride !== "") {
                    prefix = urlOverride;
                }
                var Zone = $resource(prefix + '/services/zones/:zoneId', {
                    full : 'true',
                    zoneId : '@id'
                }, {
                    update : { method : 'PUT', isArray : false },
                    start : { method : 'PUT', isArray : false, url : prefix + '/services/zones/:zoneId/start' },
                });

                return Zone;
            } ])
})();
