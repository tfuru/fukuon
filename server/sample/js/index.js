var app = angular.module('App', []);

app.controller('Ctrl', function ($scope,$http) {
	//送信ボタンクリック時
	$scope.clickBtnSend = function(){
		var url = "http://localhost:3000/user";
		var data = {
			name:'うーろん',
			photo:'http://192.168.1.178:8080/3.png',
			show_name:"うーろん！"
		};
		$http.post(url, data)
			.success(function(data, status, headers, config) {
				//DB保存成功
				console.log("success");
  			})
  			.error(function(data, status, headers, config) {
  				console.log("error");		
			});
	};
	
});