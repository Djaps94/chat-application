var app = angular.module('loginContr', []);

app.controller('loginController', ['$scope', '$rootScope', '$location', function($scope, $rootScope, $location){
	
	if(sessionStorage.getItem('user') != null){
		$location.path("/chat");
	}
	
	$scope.errorMessage = "";
	
	$scope.logInfo = {
			show : false,
			username : "",
			password : ""
	};
	
	
	var url = window.location;
	
	var wslocation = "ws://"+url.hostname+":"+url.port+"/ChatApp/webchat";
	
	try{
		socket = new WebSocket(wslocation);
		
		socket.onopen = function(){
			console.log("Socket opened!");
		}
		
		socket.onclose = function(){
			socket.close();
			console.log("Socket closed");
		}
		
		socket.onmessage = function(message){
			var msg = JSON.parse(message.data);
			switch(msg.messageType){
			case 		  'LOGIN' : $rootScope.$apply(function(){
										$rootScope.logButton = true;	
									});
									getToChat(msg.user);
									break;
			case  'ALREADY_LOGED' : $location.path("/chat");
									$scope.errorMessage = "Already logged";
								    break;
			case 'NOT_REGISTERED' : { warning(); }; 
									break;
			}
		}
		
				
	}catch(exception){
		alert("Error opening socket");
	}
	
	$scope.login = function(){
		if(!validation($scope.logInfo.username, $scope.logInfo.password))
			return;
		var socketMessage = {
				username : $scope.logInfo.username,
				password : $scope.logInfo.password,
				hostAddress: "127.0.0.1"+":"+window.location.port,
				messageType : 'LOGIN'
		};
		socket.send(JSON.stringify(socketMessage));
		console.log('Message sent');
	}
	
	
	var validation = function (username, password){
		if(username == "" || username == undefined || password == "" || password == undefined || (username == "" && password == "")){
			$scope.errorMessage = "Username and/or password can't be blank";
			$scope.logInfo.show = true;
			return false;
		}
		return true;
	}
	
	var getToChat = function(user){
		sessionStorage.setItem('user', JSON.stringify(user));
		$rootScope.$apply(function(){
			$location.path("/chat");
		});
	}
	
	var warning = function(){
		$scope.logInfo.show      = true;
		$scope.logInfo.username  = "";
		$scope.logInfo.password  = "";
		$scope.errorMessage = "Your are not registered."
		$scope.$apply();
	}
	
}]);