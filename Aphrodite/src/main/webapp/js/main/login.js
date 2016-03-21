var loginApp = angular.module('loginApp', []);

loginApp.controller('loginCtrl', function($scope, $http, $location) {
	$scope.user = {
			name: 'admin',
			password: '123456',
	};
	
	$scope.userLogin = function() {
		$http.post('r/identity/login', $scope.user).success(function(data) {
			if(data == '200') {
				var href = window.location.href.split("\#");
                var extra = href[1] || "";
                var page = "index.html";
                url =  page + "#" + extra;
                
                window.location.href = url;
			}
		});
		
	};
});