package com.nice.crawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @FileName : User.java
 * @TableName : TOREP01M
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	private String empNo;
	private String empName;
	private String email;
	private String emailDtme;
	private String emailCheck;
	private String role;
	private String refreshToken;
}