//OTHERRATING CRAWLER HANDLING
function dartHandling() {
	// 시작일, 종료일
	let startDate = $("#startDate_search").val();
	let endDate = $("#endDate_search").val();
	
	
	if(startDate == ''){
  	  alert("시작일을 입력해주세요."); return;
    }
    if(endDate == ''){
        alert("종료일을 입력해주세요."); return;
    }
	// 시작일이 종료일보다 클 경우 알림
	if(startDate > endDate) {
		alert("시작일과 종료일을 올바르게 선택해주세요.");
		return;
	}
	
	// 요청을 보낼 데이터
	var data = {
		startDate: startDate,
		endDate: endDate
	}
	
	// POST > http://localhost:8080/admin/api/enforce/after/crawler
	$.ajax({
		url: "/admin/api/enforce/dart/crawler",
		type: "POST",
		data: data,
		success: function(e) {
			if(e.message == 'session expire') {
				window.location.href = '/login';
				alert('세션이 만료되었습니다. 다시 로그인해주세요.');
				return false;
			}
		},
	    complete: function(){
	        alert('금감원 크롤링을 시작합니다. 추후에 조회버튼을 눌러 확인해주세요.');
	    },
		error: function(e) {
			alert("금감원 크롤링 실패");
		}
	})
}

function checkEndTime(){
	var data = {
		type: "Dart"
	}
	
	$.ajax({
		url: "/admin/api/check/endDarttime",
		type: "POST",
		data: data,
		success: function(e) {
			if(e.ret == "success") {
				return alert("수동처리가 완료 되었습니다. 금감원 크롤링 조회 메뉴에 들어가서 조회해주세요.");
			}else if(e.ret == "fail"){
				return alert("크롤링이 완료된 후 조회해주세요.");
			}
			
		}
	})
}

function checkAvailable(){
	
	var data = {
		type: "Dart"
	}
	
	$.ajax({
		url: "/admin/api/check/available",
		type: "POST",
		data: data,
		success: function(e) {
			if(e.ret == "success") {
				dartHandling();
			}else if(e.ret == "fail") {
				return alert("수동처리 중입니다. 나중에 다시 확인해주세요.");
			}
		}
	})
}

function excelExport(e) {
    e.preventDefault();
    let startDate = $("#startDate_search").val().replaceAll("-", "").trim();
	let endDate = $("#endDate_search").val().replaceAll("-", "").trim();
    dartGrid.exportExcel("금융감독원_수동처리" + startDate + "~" + endDate + ".xls");
  }