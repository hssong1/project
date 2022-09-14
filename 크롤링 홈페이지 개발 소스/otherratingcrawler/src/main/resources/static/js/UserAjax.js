// 사번 값 초기 세팅
$(document).ready(function() {
	// 저장된 쿠키값을 가져와서 ID 칸에 넣어준다.
    var key = getUserIdFromCookie("key");
    $("#userId").val(key); 
     
    // 그 전에 사번 저장해서 처음 페이지 로딩 시, 입력 칸에 저장된 사번이 표시된 상태라면,
    if($("#userId").val() != ""){ 
        $("#rememberUserId").attr("checked", true); // 사번 저장하기를 체크 상태로 두기.
    }
     
    $("#rememberUserId").change(function() { // 체크박스에 변화가 있다면,
        if($("#rememberUserId").is(":checked")){ // 사번 저장하기 체크했을 때,
            setUserIdToCookie("key", $("#userId").val(), 1); // 1일 동안 쿠키 보관
        }else{ // 사번 저장하기 체크 해제 시,
            deleteUserIdFromCookie("key");
        }
    });
     
    // 사번 저장하기를 체크한 상태에서 ID를 입력하는 경우, 이럴 때도 쿠키 저장.
    $("#userId").keyup(function() { // 사번 입력 칸에 사번을 입력할 때,
        if($("#rememberUserId").is(":checked")){ // 사번 저장하기를 체크한 상태라면,
            setUserIdToCookie("key", $("#userId").val(), 1); // 1일 동안 쿠키 보관
        }
    });
});

/*
 * @Function : 회원가입
 * @Author : 고지훈
 * @HttpMethod : POST > http://localhost:9000/api/join
 * @Description : 통합평가시스템(사번, 이름), 그 외의 정보 기입(이메일, 이메일 수신 여부)
 * */
function handleJoin() {
	let empNo = $("#userNumber").val();
	let empName = $("#userName").val();
	let email = $("#userEmail").val();
	let emailCheck = $("#userEmailCheck").is(':checked');
	
	if(emailCheck === true) {
		emailCheck = 'Y';
	}else if(emailCheck === false) {
		emailCheck = 'N';
	}
	
	let data = {
		"empNo": empNo,
		"empName": empName,
		"email": email,
		"emailCheck": emailCheck
	};
	
	$.ajax({
		url: "/api/join",
		type: "POST",
		headers: {'Content-Type': 'application/json'},
		data: JSON.stringify(data)
	}).done(function(data) {
		alert(data.message);
		return window.location.href="/login";
	});
}

/*
 * @Function : 로그인
 * @Author : 고지훈
 * @HttpMethod : POST > http://localhost:9000/api/login
 * @Description : 통합평가시스템 API를 사용한 로그인 시스템
 * */
function handleLogin() {
	let userId = $("#userId").val();
	let userPassword = $("#userPassword").val();
	if(userId == "") {
		return alert("사번을 입력해주세요.");
	}else if(userPassword == "") {
		return alert("비밀번호를 입력해주세요.");
	}
	
	let userIp = this.getUserIp();
	let userUuid = this.getUserUuid();
	var obj = {
		"header": {
			"txCode": "login",
		    "txId": userUuid,
		    "txId2": "",
		    "userId": "SYSTEM",
		    "ip": userIp,
		    "screenId": "",
		    "source": "OTHERRATINGCRAWLER",
		    "destination": "NCR",
		    "messageCode": 0,
		    "message": ""
		},
		"data": {
			"id": userId,
			"pw": userPassword
		}
	};
	
	var result = null;
	$.ajax({
		url: "/api/login",
		type: "POST",
		async: false,
		headers: {'Content-Type': 'application/json'},
		data: JSON.stringify(obj)
	}).done(function(data) {
		if(data.message == "정상적으로 처리되었습니다") {
			result = data;
		}else {
			alert(data.message);
			return window.location.href= "/login";
		}
	});
	
	if(result.ret == "fail") {
		alert(result.error);
		return window.location.href= "/login";
	}else if(result.ret == "success") {
		if(result.userRole == "USER") {
			return window.location.href= "/user/crawlerlist";
		}else if(result.userRole == "SYSTEM") {
			return window.location.href= "/admin/crawlerlist";
		}else if(result.userRole == "MANAGER") {
			return window.location.href= "/manager/crawlerlist";
		}
	}
}

/*
 * @Function : 사번 쿠키에 저장하기
 * @Author : 고지훈
 * @Description : 사번 쿠키에 저장하기 기능
 * */
function setUserIdToCookie(cookieName, value, days) {
	let exdate = new Date();
	exdate.setDate(exdate.getDate() + days);
	
	let cookieValue = escape(value) + ((days==null) ? "" : "; expires=" + exdate.toGMTString());
	document.cookie = cookieName + "=" + cookieValue;
}

/*
 * @Function : 쿠키에서 사번 가져오기
 * @Author : 고지훈
 * @Description : 쿠키에서 사번 가져요기 기능
 * */
function getUserIdFromCookie(cookieName) {
	cookieName = cookieName + "=";
	let cookieData = document.cookie;
	let cookieValue = "";
	let start = cookieData.indexOf(cookieName);
	
	if(start != -1) {
		start += cookieName.length;
		
		let end = cookieData.indexOf(";", start);
		if(end == -1) {
			end = cookieData.length;
		}
		
		cookieValue = cookieData.substring(start, end);
	}
	return unescape(cookieValue);
}

/*
 * @Function : 쿠키 값 지우기
 * @Author : 고지훈
 * @Description : 쿠키에서 값 삭제
 * */
function deleteUserIdFromCookie(cookieName) {
	let expireDate = new Date();
	expireDate.setDate(expireDate.getDate() -1);
	document.cookie = cookieName + "= " + "; expires=" + expireDate.toGMTString();
}

/*
 * @Function : 로그아웃
 * @Author : 고지훈
 * @Description : 타사 크롤링 사이트 로그아웃 기능
 * */
function handleLogout() {
	sessionStorage.clear();
	
	$.ajax({
		url: "/api/logout",
		type: "POST",
		async: false,
		headers: {'Content-Type': 'application/json'}
	}).done(function(data) {
		if(data.ret == "success") {
			return window.location.href="/";
		}
	});	
}

/*
 * @Function : 로그인한 사용자의 ip를 획득하기 위한 함수
 * @Author : 고지훈
 * @Description : 사용자 ip획득
 * */
function getUserIp() {
	$.ajax({
		url: "https://api.ipify.org?format=jsonp@callback=?",
		type: "GET",
		async: false,
		headers: {'Content-Type' : 'application/json'}
	}).done(function(data) {
		return data;
	})
}

/*
 * @Function : UUID 생성을 위한 함수
 * @Author : 고지훈
 * @Description : UUID 생성
 * */
function getUserUuid() {
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
		return v.toString(16);
	});
}

/*
 * @추가일시 : 2022.06.28
 * @Function : 내정보수정 조회 기능
 * @Author : 고지훈
 * @Description : 내정보수정 클릭시 로그인한 사용자의 정보를 가져오기 위한 기능
 * */
function handleOpenMyInformation() {
	let empNo = $("#userNumber").val();
	
	$.ajax({
		url: "/api/myinformation?empNo=" + empNo,
		type: "GET",
		headers: {'Content-Type': 'application/json'},
	}).done(function(fragment) {
		$("#userNumber").val(fragment.empNo);
		$("#userName").val(fragment.empName);
		$("#userEmail").val(fragment.email);
		$("#userEmailCheck").val(fragment.emailCheck);
	});
}

/*
 * @Function : 내정보수정을 위한 기능
 * @Author : 고지훈
 * @Description : 이메일 변경, 이메일 수신여부 변경 가능
 * */
function handleMyInformation() {
	let empNo = $("#userNumber").val();
	let email = $("#userEmail").val();
	let emailCheck = $("#userEmailCheck").is(':checked');
	
	if(emailCheck === true) {
		emailCheck = 'Y';
	}else if(emailCheck === false) {
		emailCheck = 'N';
	}
	
	let data = {
		"empNo": empNo,
		"email": email,
		"emailCheck": emailCheck
	};
	
	$.ajax({
		url: "/api/modify/information",
		type: "POST",
		headers: {'Content-Type': 'application/json'},
		data: JSON.stringify(data)
	}).done(function(data) {
		if(data.ret == "fail") {
			return alert(data.message);
		}else {
			$('#MyInformationModal').modal('hide');
		}
	});
}