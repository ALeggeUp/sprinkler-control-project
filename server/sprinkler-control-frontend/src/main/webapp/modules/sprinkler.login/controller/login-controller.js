/**
 * schedule-controller.js
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

(function () {
    'use strict';
 
    angular.module('sprinkler-login-module').controller('LoginController', LoginController);
 
    LoginController.$inject = ['$scope', '$location', 'AuthenticationService'];

    function LoginController($scope, $location, AuthenticationService) {
        $scope.viewClass = "loginView";
        var vm = this;
 
        vm.login = login;
 
        (function initController() {
            // reset login status
            AuthenticationService.ClearCredentials();
        })();
 
        function login() {
            vm.dataLoading = true;
            AuthenticationService.Login(vm.username, vm.password, function (response) {
                if (response.success) {
                    AuthenticationService.SetCredentials(vm.username, vm.password);
                    $location.path('/');
                } else {
                	console.log(response.message);
                    // FlashService.Error(response.message);
                    vm.dataLoading = false;
                }
            });
        };
        
        return vm;
    }

})();
