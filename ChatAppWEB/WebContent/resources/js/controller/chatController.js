var app = angular.module('chatContr', []);

app.controller('chatController',['$scope','$location', '$rootScope', function($scope, $location, $rootScope){
	
	if(sessionStorage.getItem('user') == null){
		$location.path("/login");
	}
	
	$scope.objectList = {
			activeUsers : []
	};
	
	$scope.messageList = {
			messages : []
	};
	
	$scope.messageContent = "";
	
	var url = window.location;
	var username = JSON.parse(sessionStorage.getItem('user')).username;
	var wsaddress = "ws://"+url.hostname+":"+url.port+"/ChatApp/chat";
	var wsmessage = "ws://"+url.hostname+":"+url.port+"/ChatApp/messages/"+username;
	
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
			var users 	 = JSON.parse(message.data);
			var username = JSON.parse(sessionStorage.getItem('user')).username
			if(users.hasOwnProperty('date'))
				return;
			$scope.$apply(function(){
				var arr = users.filter(element => element.username !== username);
				$scope.objectList.activeUsers = arr;
			});
			console.log("Cao onMessage");
		}
		
		var messageSocket = new WebSocket(wsmessage);
		
		messageSocket.onopen = function(){
			console.log("Message socket open");
		}
		
		messageSocket.onclose = function(){
			messageSocket.close();
			console.log("Message socket closed");
		}
		
		messageSocket.onmessage = function(message){
			var msg = JSON.parse(message.data);
			if(msg instanceof Array)
				return;
			console.log("Message recieved");
			if(msg != null || msg != undefined){
				$scope.$apply(function(){
					$scope.messageList.messages.push(msg);
					$scope.messageContent = "";
				});
			}
		}
		
		$scope.sendMessage = function(){
			if($scope.messageContent === "" || $scope.messageContent == undefined)
				return;
			
			var user = JSON.parse(sessionStorage.getItem('user'));
			var d = new Date;
			
			if($scope.messageContent.indexOf("<private>:") != -1){
				var params = $scope.messageContent.split("<private>:");
				var userTo = {
						username : params[0].trim()
				};
				
				var m      = {
							 content : params[1].trim(),
							 date    : d,
							 subject : "",
							 to      : userTo,
							 from    : user
				};
			}else{
				var m = {
						content : $scope.messageContent,
						date    : d,
						subject : "",
						to      : null,
						from    : user
				};
			}			
			messageSocket.send(JSON.stringify(m));
			$scope.messageContent = "";
			
		}
		
	}catch(exception){
		console.log('Error opening socket');
	}

}]);
