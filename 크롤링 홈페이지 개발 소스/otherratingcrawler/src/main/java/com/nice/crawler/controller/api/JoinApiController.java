package com.nice.crawler.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nice.crawler.service.UserService;

/**
 * 
 * @FileName : JoinApiController.java
 * @Description : 회원가입 관련 기능을 위한 Api Controller
 */
@RestController
@RequestMapping(value = "/api")
public class JoinApiController {
	
	private final UserService userService;
	
	public JoinApiController(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/api/join
	 * @Description : 추가정보 기입 화면으로 이메일, 이메일 수신 여부를 추가로 기입할 수 있음
	 */
	@PostMapping("/join")
	public Map<String, Object> postJoin(@RequestBody Map<String, Object> request) {
		Map<String, Object> result = new HashMap<>();

		try {
			int insertUserCount = userService.insertUser(request);
			
			if (insertUserCount > 0) {
				result.put("ret", "success");
				result.put("message", "회원가입을 성공했습니다. 다시 로그인 해주세요.");
			} else {
				result.put("ret", "fail");
				result.put("message", "회원가입을 실패했습니다. 다시 로그인부터 진행해주세요.");
			}
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return result;
	}
}
