var app = angular.module('registerContr', []);

app.controller('registerController', ['$scope', function($scope){
	var url = window.location;
	
	var wsaddress = "ws://"+url.hostname+":"+url.port+"/ChatApp/webchat";
	
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
			alert(message);
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
}]);