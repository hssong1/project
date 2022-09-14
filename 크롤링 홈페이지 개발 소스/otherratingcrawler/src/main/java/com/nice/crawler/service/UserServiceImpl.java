package com.nice.crawler.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nice.crawler.dto.UserDTO.Request;
import com.nice.crawler.mapper.UserMapper;
import com.nice.crawler.model.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserMapper userMapper;
	
	@Override
	public User getUser(String empNo) {
		return userMapper.getUser(empNo);
	}
	
	@Override
	public int updateUserRefreshToken(Map<String, Object> request) {
		int updateRefreshTokenCount = 0;
		
		try {
			updateRefreshTokenCount = userMapper.updateUserRefreshToken(request);
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return updateRefreshTokenCount;
	}
	
	@Override
	public int insertUser(Map<String, Object> request) {		
		int insertUserCount = 0;
		
		try {
			insertUserCount = userMapper.insertUser(request);
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return insertUserCount;
	}
	
	@Override
	public int updateUsers(String[] empNoList, String[] roleList, String[] emailCheckList) {
		int updateUsersCount = 0;
		
		for(int i = 0; i < empNoList.length; i++) {
			try {
				Map<String, Object> request = new HashMap<>();
				request.put("empNo", empNoList[i]);
				request.put("role", roleList[i]);
				request.put("emailCheck", emailCheckList[i]);

				int updateUserCount = userMapper.updateUser(request);
				updateUsersCount += updateUserCount;
			} catch (Exception error) {
				System.err.println(error);
			}
		}
		
		return updateUsersCount;
	}
	
	@Override
	public int updateUser(Map<String, Object> request) {
		int updateUserCount = 0;
		
		try {
			updateUserCount = userMapper.updateUser(request);
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return updateUserCount;
	}
	
	@Override
	public List<User> getUserList(Request request) {
		return userMapper.getUserList(request);
	}

	@Override
	public List<User> getUserListToSendMail() {
		return userMapper.getUserListToSendMail();
	}

	@Override
	public int updateUserInformation(Request request) {
		int updateUserCount = 0;
		
		try {
			updateUserCount = userMapper.updateUserInformation(request);
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return updateUserCount;
	}
}
