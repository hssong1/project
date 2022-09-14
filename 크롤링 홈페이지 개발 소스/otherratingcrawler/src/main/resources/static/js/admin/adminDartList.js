// GRID VARIABLE SETTING
let dartGrid;

//GRID INIT SETTING
$(document.body).ready(function () {
    dartGrid = new ax5.ui.grid();
    
    dartGrid.setConfig({
    	target: $('[data-ax5grid="dart-grid"]'),
        header: {
            align: "center"
        },
        columns: [
            {key: "seq", "label": "seq", width: 90, align:"center"},
            {key: "ratgCom", "label": "평가사", width: 90,align:"center"},
            {key: "ratgTrgtCom", "label": "기업명", width: 200,align:"left"},
            {
                key: "corpRgno", "label": "법인번호", formatter: function () {
                    return this.value.substr(0, 6) + '-' + this.value.substr(6, 7);
                },align:"center"
            },
            {
                key: "bizNo", "label": "사업자번호", formatter: function () {
                    return this.value.substr(0, 3) + '-' + this.value.substr(3, 2) + '-' + this.value.substr(5, 6);
                },align:"center"
            },
            {key: "ratgMthd", "label": "평가방법론", width: 200},
            {key: "ratgTrgtKind", "label": "평가대상 종류", width: 200},
            {key: "isueAmt", "label": "발행액",align:"right"},
            {key: "isueDate", "label": "발행일",align:"center"},
            {key: "expiDate", "label": "만기일",align:"center"},
            {key: "ctrtCntcDate", "label": "약정체결일",align:"center"},
            {key: "ctrtExprFeeDate", "label": "약정만료일",align:"center"},
            {key: "lastExpiDate", "label": "최종만기일",align:"center"},
            {key: "ratgDivd", "label": "평가대상 구분",align:"center"},
            {key: "rankConfDate", "label": "등급평정일",align:"center"},
            {key: "pubAnucDate", "label": "공시일",align:"center"},
            {key: "fincShetStndDate", "label": "재무제표기준일",align:"center"},
            {key: "rankVadtDate", "label": "등급유효일",align:"center"},
            {key: "prevRatgRank", "label": "이전등급",align:"center"},
            {key: "ratgRank", "label": "등급",align:"center"},
            {key: "crdtWatch", "label": "watch",align:"center"},
            {key: "crdtOtlk", "label": "outlook",align:"center"},
            {key: "atchFileName", "label": "파일명",align:"center"},
            {key: "rmks", "label": "비고"},
          ]
    });
});

//OTHERRATING CRAWLER HANDLING
function dartSearch() {
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
	
    // 그리드 데이터 가져오기
    $.ajax({
    	url: "/admin/api/dartlist",
		type: "POST",
		data: data,
		success: function(e) {
			if(e.message == 'session expire') {
				window.location.href = '/login';
				alert('세션이 만료되었습니다. 다시 로그인해주세요.');
			}else {
				dartGrid.setData(e);
			}
		},
		error: function(e) {
			console.log(e);
		}
    });
}

function excelExport(e) {
    e.preventDefault();
    let startDate = $("#startDate_search").val().replaceAll("-", "").trim();
	let endDate = $("#endDate_search").val().replaceAll("-", "").trim();
    dartGrid.exportExcel("금융감독원_" + startDate + "~" + endDate + ".xls");
}