var app = angular.module('logoutContr', []);

app.controller('logoutController', ['$scope','$location','$rootScope', function($scope, $location, $rootScope){
	
	var url = window.location;
	var wsaddress = "ws://"+url.hostname+":"+url.port+"/ChatApp/webchat";
	
	$scope.logButton = false;
	
	if(sessionStorage.getItem('user') != null){
		$scope.logButton = true;
	}
	
	try{
		socket = new WebSocket(wsaddress);
		
		socket.onopen = function(){
			console.log("Open logout socket");
		}
		
		socket.onclose = function(){
			socket.close();
			console.log("Close logout socket");
		}
		
		socket.onmessage = function(message){
			var msg = JSON.parse(message.data);
			switch(msg.messageType){
			case 	 'LOGOUT' : sessionStorage.clear(); 
								$rootScope.$apply(function(){
									$location.path('/register');
								}); break;
			case 'NOT_LOGOUT' : socket.close(); break;
			
			}
		}
		
	}catch(exception){
		console.log("Error opening socket");
	}
	
	
	
	$scope.logout = function(){
		out();
	};
	
	
	var out = function(){
		var user = JSON.parse(sessionStorage.getItem('user'));
		var socketMessage = {
				username : user.name,
				password : user.pass,
				messageType : 'LOGOUT'
		};
		
		socket.send(JSON.stringify(socketMessage));
	}
	
}]);