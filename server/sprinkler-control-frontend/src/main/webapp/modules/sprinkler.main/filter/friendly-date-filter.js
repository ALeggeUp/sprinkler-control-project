/**
 * friendly-date-filter.js
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

    var app = angular.module('sprinkler-main-module');

    app.filter('friendlydate', function() {
        // Setup format
        moment.locale('en', {
            calendar : {
                lastDay : '[Yesterday at] LT',
                sameDay : '[Today at] LTS',
                nextDay : '[Tomorrow at] LT',
                lastWeek : 'dddd [at] LT',
                nextWeek : '[On] dddd [at] LT',
                sameElse : 'dddd, MMMM Do YYYY [at] h:mm:ss A'
            },
            relativeTime : {
                future : 'in %s',
                past : '%s ago',
                s  : 'seconds',
                m  : 'About a minute',
                mm : 'Around %d minutes',
                h  : 'Around an hour',
                hh : 'About %d hours',
                d  : 'a day',
                dd : '%d days',
                M  : 'a month',
                MM : '%d months',
                y  : 'a year',
                yy : '%d years'
            }
        });

        return function(dateString) {
            var hoursAgo = moment().subtract(6, 'hours');
            if (moment(dateString).isBefore(moment('2010-01-01'))) {
                return 'Never';
            } else if (moment(dateString).isBefore(hoursAgo)) {
                return moment(dateString).calendar();
            } else {
                return moment(dateString).fromNow();
            }
        };
    });
})();
