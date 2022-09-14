// GRID VARIABLE SETTING
let ratingGrid;

//GRID INIT SETTING
$(document.body).ready(function () {
    ratingGrid = new ax5.ui.grid();

    ratingGrid.setConfig({
    	target: $('[data-ax5grid="otherRating-grid"]'),
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
            {key: "series", label: "시리즈", sortable: true}
        ]
    });
});

//OTHERRATING CRAWLER HANDLING
function handling() {
	// 시작일, 종료일
	let startDate = $("#startDate_search").val().replaceAll("-", "").trim();
	let endDate = $("#endDate_search").val().replaceAll("-", "").trim();

	// 체크된 유형 배열에 넣기
	let crprvid = new Array();
	$("#crprvid:checked").each(function () {
		crprvid.push($(this).val());
	});
	
	if(crprvid.length == 0) {
		return alert("회사유형을 선택해주세요.");
	}
	
	// 시작일이 종료일보다 클 경우 알림
	if(startDate > endDate) {
		alert("시작일과 종료일을 올바르게 선택해주세요.");
		return;
	}
	
	// 요청을 보낼 데이터
	var data = {
		startDate: startDate,
		endDate: endDate,
		crprvid: crprvid
	}
	
    // 그리드 데이터 가져오기
    $.ajax({
    	url: "/admin/api/crawler",
		type: "POST",
		data: data,
		success: function(e) {
			if(e.message == 'session expire') {
				window.location.href = '/login';
				alert('세션이 만료되었습니다. 다시 로그인해주세요.');
			}else {
				ratingGrid.setData(e);
			}
		},
		error: function(e) {
			console.log(e);
		}
    });
}