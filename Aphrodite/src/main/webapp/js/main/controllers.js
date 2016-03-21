/**
 * Aphrodite
 *
 * Main controller.js file
 * Define controllers with data used
 *
 */
function indexCtrl($scope, $http) {
	
	$http.get('r/identity/info').success(function(data) {
        $scope.current_user_name = data.name;
    });
	
	$scope.logout = function() {
		$.ajax({
			method: "POST",
			url: "r/identity/logout",
			success: function(data) {
				if(data == '200') {
					window.location.href = "login.html";
				}
			}
		});
	}
	
}

function appTabCtrl($scope, $http, $stateParams, $state) {
	$http({
		method: 'GET',
		url: 'r/app/info',
		params: {'appId': $stateParams.id}
	}).success(function(data){
		$scope.historyLocation = data.name;
		$scope.id = data.id;
		$scope.type = data.type;
	}).error(function(data){
		console.log(data);
	});
	
	$scope.toDeployHost = function() {
		$state.go('app_hosts', {id: $scope.id, type: $scope.type});
	}
}

/**
 *
 * Pass all functions into module
 */
angular
    .module('aphrodite')
    .controller('indexCtrl', indexCtrl)
    .controller('appTabCtrl', appTabCtrl);
