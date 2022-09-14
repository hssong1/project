// GRID VARIABLE SETTING
let userGrid;

$(document.body).ready(function () {
	userGrid = new ax5.ui.grid();

    userGrid.setConfig({
    	target: $('[data-ax5grid="user-grid"]'),
        showRowSelector: true,
		rowSelectorColumnWidth: 27,
		multipleSelect : true,
        columns: [
            {key: "empNo", label: "사번", sortable: true},
            {key: "empName", label: "이름", sortable: true},
            {key: "email", label: "이메일", sortable: true},
            {key: "emailDtme", label: "메일 전송시간", sortable: true, formatter: function(){if(this.value!==null){return this.value.substring(0,4)+"년 "+this.value.substring(4,6)+"월 "+this.value.substring(6,8)+"일 "+this.value.substring(8,10)+"시 "+this.value.substring(10,12)+"분 "+this.value.substring(12,14)+"초 "} }},
            {key: "role", label: "권한", sortable: true, editor: { type: "select", config: { columnKeys: { optionText: "CD", optionValue: "TF" }, options: [ { CD: "USER", TF: "USER" }, { CD: "MANAGER", TF: "MANAGER" }, { CD: "SYSTEM", TF: "SYSTEM" }] } } },
			{key: "emailCheck", label: "이메일 송신 여부", sortable: true, editor: { type: "select", config: { columnKeys: { optionText: "CD", optionValue: "TF" }, options: [ { CD: "Y", TF: "Y" }, { CD: "N", TF: "N" } ] } } }
        ]
    });

	$.ajax({
    	url: "/admin/api/user/search",
		type: "POST",
		success: function(e) {
			if(e.message == 'session expire') {
				window.location.href = '/login';
				alert('세션이 만료되었습니다. 다시 로그인해주세요.');
			}else {
				userGrid.setData(e);
			}
		}
    })
});

function usersearch(){
	let type = $("#usersearch").val();
	let keyword = $("#userkeyword").val();
	
	var data = {
		searchType: type,
		keyword: keyword
	};
		
	$.ajax({
		url: "/admin/api/user/search",
		type: "POST",
		data: data,
		success: function(e) {
			if(e.message == 'session expire') {
				window.location.href = '/login';
				alert('세션이 만료되었습니다. 다시 로그인해주세요.');
			}else {
				userGrid.setData(e);
			}
		}
	});
}

function userupdate(){
	let users = userGrid.getList("selected");
	let empNoList = new Array();
	let roleList = new Array();
	let emailCheckList = new Array();
	
	if(users.length == 0){
		alert("변경할 회원이 없습니다.");
		return;
	}
	
	for(let i = 0; i < users.length; i++){
		empNoList.push(users[i].empNo);
		roleList.push(users[i].role);
		emailCheckList.push(users[i].emailCheck);		
	}
	
	var data = {
		empNoList: empNoList,
		roleList: roleList,
		emailCheckList: emailCheckList
	}

    $.ajax({
    	url: "/admin/api/user/update",
		type: "POST",
		data: data
    }).done(function() {
		alert("회원정보 변경 완료");
		location.reload();
	}).fail(function() {
		alert("회원정보 변경 실패");
	});
}