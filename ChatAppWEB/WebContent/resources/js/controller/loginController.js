var app = angular.module('loginContr', []);

app.controller('loginController', ['$scope', function($scope){
	
	if(sessionStorage.getItem('user') != null){
		$rootProvider.$apply(function(){
			$location.path("/chat");
		});
	}
	
}]);