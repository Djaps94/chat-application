var app = angular.module('chatContr', []);

app.controller('chatController',['$scope','$location', '$rootScope', function($scope, $location, $rootScope){
	
	if(sessionStorage.getItem('user') == null){
		$location.path("/login");
	}
	
	$scope.objectList = {
			activeUsers : []
	};
	
	var url = window.location;
	var username = "";
	var wsaddress = "ws://"+url.hostname+":"+url.port+"/ChatApp/chat";
	
	try{
		socket = new WebSocket(wsaddress);
		
		socket.onopen = function(){
			console.log("Open chat socket");
			var user = JSON.parse(sessionStorage.getItem('user'));
			username = user.username;
			var socketMessage = {
					username : user.name,
					password : user.pass,
					messageType : 'ACTIVE_USERS'
			};
			socket.send(JSON.stringify(socketMessage));
		}
		
		socket.onclose = function(){
			socket.close();
			console.log("Closing chat socket");
		}
		
		socket.onmessage = function(message){
			var users = JSON.parse(message.data);
			$scope.$apply(function(){
				$scope.objectList.activeUsers = users;
			});
			console.log("Cao onMessage");
		}
		
		
		
	}catch(exception){
		console.log('Error opening socket');
	}

}]);
