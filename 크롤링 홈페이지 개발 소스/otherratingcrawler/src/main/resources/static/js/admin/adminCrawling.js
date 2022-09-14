// GRID VARIABLE SETTING
let afterRatingGrid;
let beforeRatingGrid;

//GRID INIT SETTING
$(document.body).ready(function () { 
    beforeRatingGrid = new ax5.ui.grid();
    afterRatingGrid = new ax5.ui.grid();
    
    beforeRatingGrid.setConfig({
    	target: $('[data-ax5grid="otherRating-before-grid"]'),
        header: {
            align: "center"
        },
        columns: [
        	{key: "crprvid", label: "평가사", sortable: true},
        	{key: "bondTyp", label: "사채종류", sortable: true},
            {key: "cmpCd", label: "기업코드", sortable: true},
            {key: "cmpNm", label: "기업명", sortable: true},
            {key: "crTyp", label: "평가종류", sortable: true},
            {key: "exposeTyp", label: "익스포져유형", sortable: true},
            {key: "issueAmt", label: "발행액", sortable: true},
            {key: "issueAmtTyp", label: "발행단위", sortable: true},
            {key: "issueDate", label: "발행일", sortable: true},
            {key: "issueNo", label: "회차명", sortable: true},
            {key: "matuDate", label: "만기일", sortable: true},
            {key: "outlook", label: "OUTLOOK", sortable: true},
            {key: "rank", label: "등급", sortable: true},
            {key: "rankConfDate", label: "등급확정일", sortable: true},
            {key: "relCmpNm", label: "관계기관", sortable: true},
            {key: "ruleDate", label: "기준일", sortable: true},
            {key: "secuTyp", label: "평가대상", sortable: true},
            {key: "series", label: "시리즈", sortable: true},
        ]
    }); 
    
    afterRatingGrid.setConfig({
    	target: $('[data-ax5grid="otherRating-after-grid"]'),
        header: {
            align: "center"
        },
        columns: [
        	{key: "crprvid", label: "평가사", sortable: true},
        	{key: "bondTyp", label: "사채종류", sortable: true},
            {key: "cmpCd", label: "기업코드", sortable: true},
            {key: "cmpNm", label: "기업명", sortable: true},
            {key: "crTyp", label: "평가종류", sortable: true},
            {key: "exposeTyp", label: "익스포져유형", sortable: true},
            {key: "issueAmt", label: "발행액", sortable: true},
            {key: "issueAmtTyp", label: "발행단위", sortable: true},
            {key: "issueDate", label: "발행일", sortable: true},
            {key: "issueNo", label: "회차명", sortable: true},
            {key: "matuDate", label: "만기일", sortable: true},
            {key: "outlook", label: "OUTLOOK", sortable: true},
            {key: "rank", label: "등급", sortable: true},
            {key: "rankConfDate", label: "등급확정일", sortable: true},
            {key: "relCmpNm", label: "관계기관", sortable: true},
            {key: "ruleDate", label: "기준일", sortable: true},
            {key: "secuTyp", label: "평가대상", sortable: true},
            {key: "series", label: "시리즈", sortable: true},
        ]
    }); 
});

//OTHERRATING CRAWLER HANDLING
function otherRatingHandling() { 
	let startDate = $("#startDate_search").val();
	let endDate = $("#endDate_search").val();	
	let convertStartDate = startDate.replaceAll("-", "").trim();
	let convertEndDate = endDate.replaceAll("-", "").trim();
	
//	let curDate = new Date();
//	let toDate = curDate.toISOString().split('T')[0];
//	curDate.setDate(curDate.getDate() - 25);
//	let fromDate = curDate.toISOString().split('T')[0];
//	let convertFromDate = fromDate.replaceAll("-", "").trim();
//	
//	if(convertFromDate > convertStartDate || convertFromDate > convertEndDate) {
//		return alert("수동처리시 가능한 기간은: " + fromDate + " ~ " + toDate + "사이입니다." );
//	}
	
	let crprvid = new Array();
	let type = $("#crprvid:checked").each(function(idx) {
		// 체크 박스의 값 가져오기
		crprvid.push($(this).val());
	});
	
	if(crprvid.length == 0) {
		return alert("회사유형을 선택해주세요.");
	}
	
	// 요청을 보낼 데이터
	var data = {
		startDate: convertStartDate,
		endDate: convertEndDate,
		crprvid: crprvid
	}
	
	// POST > http://localhost:8080/admin/api/enforce/after/crawler
	$.ajax({
		url: "/admin/api/enforce/after/crawler",
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
	        alert('타사크롤링을 시작합니다. 추후에 조회버튼을 눌러 확인해주세요.');
	    },
		error: function(e) {
			alert("타사 데이터 크롤링 실패");
		}
	})
}

function checkAvailable(){
	
	var data = {
		type: "OTHER"
	}
	
	$.ajax({
		url: "/admin/api/check/available",
		type: "POST",
		data: data,
		success: function(e) {
			if(e.ret == "success") {
				otherRatingHandling();
			}else if(e.ret == "fail") {
				return alert("수동처리 중입니다. 나중에 다시 확인해주세요.");
			}
		}
	})
}

function checkEndTime(){
	
	var data = {
		type: "OTHER"
	}
	
	$.ajax({
		url: "/admin/api/check/endtime",
		type: "POST",
		data: data,
		success: function(e) {
			if(e.ret == "success") {
				beforeaftersearch();	
			}else if(e.ret == "fail") {
				return alert("크롤링이 완료된 후 조회해주세요.");
			}
		}
	})
}

function beforeaftersearch(){
	let startDate = $("#startDate_search").val().replaceAll("-", "").trim();
	let endDate = $("#endDate_search").val().replaceAll("-", "").trim();
	
	let crprvid = new Array();
	let type = $("#crprvid:checked").each(function(idx) {
		crprvid.push($(this).val());
	});
	
	if(crprvid.length == 0) {
		return alert("기업유형을 선택해주세요.");
	}
	
	var data = {
		startDate: startDate,
		endDate: endDate,
		crprvid: crprvid
	}
	
	$.ajax({
    	url: "/admin/api/crawler",
		type: "POST",
		data: data,
		success: function(res) {
			beforeRatingGrid.setData(res);
		}
    })
 
    $.ajax({
    	url: "/admin/api/after/crawler",
		type: "POST",
		data: data,
		success: function(res) {
			afterRatingGrid.setData(res);
		}
    })
}

function dateToString(date) {
	let year = date.getFullYear();
	let month = (date.getMonth() + 1);
	let day = date.getDate();
	
	month = (month < 10) ? "0" + String(month) : month;
	day = (day < 10) ? "0" + String(day) : day;
	
	return year + month + day;
}

function today() {
	let resultDay = new Date();
	
	return dateToString(resultDay);
}

function lastWeek() {
	let resultDay = new Date();
	let dayOfMonth = (resultDay.getDate() - 7);
	
	resultDay.setDate(dayOfMonth);
	
	return dateToString(resultDay);
}

function lastTwoWeek() {
	let resultDay = new Date();
	let dayOfMonth = (resultDay.getDate() - 14);
	
	resultDay.setDate(dayOfMonth);
	
	return dateToString(resultDay);
}

function lastMonth() {
	let resultDay = new Date();
	let monthOfYear = (resultDay.getMonth()-1);
	
	resultDay.setMonth(monthOfYear);
	
	return dateToString(resultDay);
}