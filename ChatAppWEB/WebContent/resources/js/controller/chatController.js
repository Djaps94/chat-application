var app = angular.module('chatContr', []);

app.controller('chatController',['$scope','$location', function($scope, $location){
	
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
			username = user;
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
			users.forEach(function(element){
				if($scope.objectList.activeUsers.indexOf(element) == -1 && element.username !== username) {
					$scope.$apply(function() {
						$scope.objectList.activeUsers.push(element);
					})
				}		
			});
		}
		
		
		
	}catch(exception){
		console.log('Error opening socket');
	}

}]);
