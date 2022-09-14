package com.nice.crawler.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.nice.crawler.dto.UserDTO;
import com.nice.crawler.model.User;

@Repository
public interface UserMapper {
	
	public User getUser(@Param("empNo") String empNo);
	
	public int insertUser(Map<String, Object> request);
	
	public int updateUserRefreshToken(Map<String, Object> request);
	
	public int updateUser(Map<String, Object> request);
	
	public List<User> getUserList(UserDTO.Request request);
	
	public List<User> getUserListToSendMail();

	public User checkUserList(String companyNumber);	
	
	public int updateUserInformation(UserDTO.Request request);
}
