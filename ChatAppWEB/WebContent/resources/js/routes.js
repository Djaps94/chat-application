var app = angular.module('routes', ['ngRoute']);

app.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider){
	
	$routeProvider
		.when('/',{
			templateUrl: 'resources/html/main_page.html'
		})
		.when('/register',{
			templateUrl: 'resources/html/register.html'
		})
		.when('/login', {
			templateUrl: 'resources/html/login.html'
		})
	
	if(window.history && window.history.pushState){
		$locationProvider.html5Mode({
			enable: true,
			requireBase: false
		})
	}
	
}]);

