var app = angular.module('logoutContr', []);

app.controller('logoutController', ['$scope','$location','$rootScope', function($scope, $location, $rootScope){
	
	var url = window.location;
	var wsaddr = "ws://"+url.hostname+":"+url.port+"/ChatApp/webchat";
	
	$rootScope.logButton = false;
	
	if(sessionStorage.getItem('user') != null){
		$rootScope.logButton = true;
	}
	
	try{
		socketLogout = new WebSocket(wsaddr);
		
		socketLogout.onopen = function(){
			console.log("Open logout socket");
		}
		
		socketLogout.onclose = function(){
			socket.close();
			console.log("Close logout socket");
		}
		
		socketLogout.onmessage = function(message){
			var msg = JSON.parse(message.data);
			switch(msg.messageType){
			case 	 'LOGOUT' : sessionStorage.removeItem('user'); 
								$rootScope.$apply(function(){
									$location.path('/register');
									$scope.logButton = false;
								}); break;
			case 'NOT_LOGOUT' : socket.close(); break;
			
			}
		}
		
	}catch(exception){
		console.log("Error opening socket");
	}
	
	$scope.logout = function(){
		var userSend = JSON.parse(sessionStorage.getItem('user'));
		var socketMessage = {
				user        : userSend,
				messageType : 'LOGOUT'
		};
		
		socketLogout.send(JSON.stringify(socketMessage));
	};
	
	window.onbeforeunload = function(){
		$scope.logout();
		sessionStorage.removeItem('user');
	};
	
}]);