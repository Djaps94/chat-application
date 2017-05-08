var app = angular.module('registerContr', []);

app.controller('registerController', ['$scope', '$rootScope', '$location', '$timeout', function($scope, $rootScope, $location, $timeout){
	var url = window.location;
	
	var wsaddress = "ws://"+url.hostname+":"+url.port+"/ChatApp/webchat";
	
	$scope.userInputs = {
			username: "",
			password: "",
			show    : false,
			
	};
	
	try{
		socket = new WebSocket(wsaddress);
		
		socket.onopen = function(){
			console.log("Socket register opened!");
		}
		
		socket.onclose = function(){
			socket.close();
			console.log("Socket register closed!");
		}
		
		socket.onmessage = function(message){
			var socketMessage = JSON.parse(message.data);
			console.log("Message recieved");
			console.log(socketMessage.messageType);
			switch(socketMessage.messageType){
			case 	   'REGISTER' : $timeout(function(){
										$rootScope.$apply(function(){
											$location.path("/login");
										}); }, 2000);
									break;
			case 'USERNAME_EXISTS': notif(); 
									break;
			}
		}
		
	}catch(exception){
		alert("Error opening socket!");
	}
	
	$scope.register = function(){
		if(!validation($scope.userInputs.username, $scope.userInputs.password))
			return;
		var socketMessage = {
			username: $scope.userInputs.username,
			password: $scope.userInputs.password,
			messageType: 'REGISTER'
		};
		socket.send(JSON.stringify(socketMessage));
		console.log("Message sent");
	}
	
	var validation = function(username, password){
		if(username === "" || username == undefined || password === "" || password == undefined || (username === "" && password === "")){
			return false;
		}
		return true;
	}
	
	var notif = function(){
		$scope.userInputs.show      = true;
		$scope.userInputs.username  = "";
		$scope.userInputs.password  = "";
	}
}]);