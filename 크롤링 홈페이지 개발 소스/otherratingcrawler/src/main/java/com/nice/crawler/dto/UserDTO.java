package com.nice.crawler.dto;

import java.util.ArrayList;
import java.util.List;

import com.nice.crawler.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserDTO {
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class info {
		private String empNo;
		private String empName;
		private String email;
		private String emailDtme;
		private String emailCheck;
		private String role;
		private String refreshToken;
		
		public static UserDTO.info of(User user) {
			UserDTO.info dto = new UserDTO.info();
			if(user == null) return dto;
			dto.setEmpNo(user.getEmpNo());
			dto.setEmpName(user.getEmpName());
			dto.setEmail(user.getEmail());
			dto.setEmailDtme(user.getEmailDtme());
			dto.setEmailCheck(user.getEmailCheck());
			dto.setRole(user.getRole());
			dto.setRefreshToken(user.getRefreshToken());

			return dto;
		}
		
		public static List<UserDTO.info> of(List<User> userList) {
			List<UserDTO.info> dtoList = new ArrayList<>();
			if(userList == null || userList.size() < 1) return dtoList;
			for(User user : userList) {
				dtoList.add(UserDTO.info.of(user));
			}
			return dtoList;
		}
	}

	@Getter
	@Setter
	public static class Request {
		private String[] empNoList;
		private String[] roleList;
		private String[] emailCheckList;
		private String empNo;
		private String empName;
		private String email;
		private String emailCheck;
		private String searchType;
		private String keyword;
	}
	
	@Getter
	@Setter
	public static class Response {
		private String[] empNo;
		private String[] role;
		private String[] emailCheck;
		private String searchType;
		private String keyword;
	}
}