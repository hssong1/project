/*function checkUserSession(url, data, callback) {
	$.ajax({
		url: url,
		type: "POST",
		async: false,
		headers: {'Content-Type' : 'application/json'},
		data: JSON.stringify(data)
	}).done(function(data) {
		if(data.message == 'session expire') {
			console.log("안녕하세요);
		}else {
			if(callback == 'undefined') {
				callback();
			}
		}
	}).fail(function(e)) {
		alert(e.message);
	}
}*/

export function test() {
	console.log("테스트입니다.");
}