package com.nice.crawler.service;

import java.util.List;
import java.util.Map;

import com.nice.crawler.dto.UserDTO;
import com.nice.crawler.model.User;

public interface UserService {
	
	public User getUser(String empNo);
	
	public int updateUserRefreshToken(Map<String, Object> request);
	
	public int insertUser(Map<String, Object> request);
	
	public int updateUsers(String[] empNoList, String[] roleList, String[] emailCheckList);
	
	public int updateUser(Map<String, Object> request);
		
	public List<User> getUserList(UserDTO.Request request);
	
	public List<User> getUserListToSendMail();
	
	public int updateUserInformation(UserDTO.Request request);
}
