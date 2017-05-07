var app = angular.module('registerContr', []);

app.controller('registerController', ['$scope', '$rootScope', '$location', '$timeout', function($scope, $rootScope, $location, $timeout){
	var url = window.location;
	
	var wsaddress = "ws://"+url.hostname+":"+url.port+"/ChatApp/webchat";
	
	$scope.show = false;
	
	try{
		socket = new WebSocket(wsaddress);
		
		socket.onopen = function(){
			console.log("socket opened!");
		}
		
		socket.onclose = function(){
			socket = null;
			console.log("socket closed!");
		}
		
		socket.onmessage = function(message){
			var socketMessage = JSON.parse(message.data);
			switch(socketMessage.messageType){
			case 	   'REGISTER' : $timeout(function(){
										$rootScope.$apply(function(){
											$location.path("/login");
										}); }, 2000);
			break;
			case 'USERNAME_EXISTS': warning(); break;
			}
		}
		
	}catch(exception){
		alert("Error opening socket!");
	}
	
	$scope.register = function(){
		validation($scope.username, $scope.password);
		var socketMessage = {
			username: $scope.username,
			password: $scope.password,
			messageType: 'REGISTER'
		};
		socket.send(JSON.stringify(socketMessage));
		console.log("Message sent");
	}
	
	var validation = function(username, password){
		if(username == "" || username == undefined || password == "" || password == undefined || (username == "" && password == "")){
			alert("That's bad habit!");
			return;
		}
	}
	
	var warning = function(){
		$scope.userinput = "border-color: red";
		$scope.passinput = "border-color: red";
		$scope.show      = true;
	}
}]);