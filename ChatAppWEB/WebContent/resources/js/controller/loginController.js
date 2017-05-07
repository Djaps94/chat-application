var app = angular.module('loginContr', []);

app.controller('loginController', ['$scope', '$rootScope', '$location', function($scope, $rootScope, $location){
	
	if(sessionStorage.getItem('user') != null){
		$location.path("/chat");
	}
	
	$scope.show = false;
	
	var url = window.location;
	
	var wslocation = "ws://"+url.hostname+":"+url.port+"/ChatApp/webchat";
	
	try{
		var socket = new WebSocket(wslocation);
		
		socket.onopen = function(){
			console.log("Socket opened!");
		}
		
		socket.onclose = function(){
			socket = null;
			console.log("Socket closed");
		}
		
		socket.onmessage = function(message){
			var msg = JSON.parse(message.data);
			switch(msg.messageType){
			case 		  'LOGIN' : getToChat(msg.username, msg.password); 
									break;
			case  'ALREADY_LOGED' : $location.path("/chat");
								    break;
			case 'NOT_REGISTERED' : warning(); 
									break;
			}
		}
		
				
	}catch(exception){
		alert("Error opening socket");
	}
	
	$scope.login = function(){
		validation($scope.username, $scope.password);
		var socketMessage = {
				username : $scope.username,
				password : $scope.password,
				messageType : 'LOGIN'
		};
		socket.send(JSON.stringify(socketMessage));
		console.log('Message sent');
	}
	
	
	var validation = function (username, password){
		if(username == "" || username == undefined || password == "" || password == undefined || (username == "" && password == "")){
			$scope.errorMessage = "Username and/or password can't be blank";
			$scope.show = true;
			return;
		}
	}
	
	var getToChat = function(username, password){
		var user = {
				name : username,
				pass : password,
		};
		sessionStorage.setItem('user', JSON.stringify(user));
		$rootScope.$apply(function(){
			$location.path("/chat");
		});
	}
	
	var warning = function(){
		$scope.userinput = "border-color: red";
		$scope.passinput = "border-color: red";
		$scope.show      = true;
		$scope.username  = "";
		$scope.password  = "";
		$scope.errorMessage = "Your are not registered."
	}
	
}]);