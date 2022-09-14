package com.nice.crawler.controller.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.nice.crawler.dto.UserDTO;
import com.nice.crawler.model.User;
import com.nice.crawler.service.UserService;

/**
 * 
 * @FileName : LoginApiController.java
 * @Description : 로그인 관련 기능을 위한 Api Controller
 */
@RestController
@RequestMapping(value = "/api")
public class LoginApiController {
	
	private final UserService userService;

	@Value("${nxtncrUrl}")
	private String nxtncrUrl;
	
	public LoginApiController(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/api/login
	 * @Description : 통합평가시스템API를 사용하여 시스템 로그인 → DB를 조회하여 일치하는 회원이 없을 경우, 추가정보 기입화면으로 이동         
	 */
	@PostMapping("/login")
	public Map<String, Object> postLogin(@RequestBody Map<String, Object> param, HttpSession session) throws ParseException {

		HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(param);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = null;



		try {
			response = restTemplate.exchange(nxtncrUrl+"/login", HttpMethod.POST, entity, String.class);
		} catch (HttpStatusCodeException e) {
			response = ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
		}
	
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody().toString());
		JSONObject headerObject = (JSONObject) jsonObject.get("header");
		JSONObject dataObject = (JSONObject) jsonObject.get("data");

		Map<String, Object> result = new HashMap<>();
		
		String loginResult = headerObject.get("message").toString();
		if (loginResult.equals("정상적으로 처리되었습니다")) {
			result.put("ret", "success");
			result.put("message", loginResult);

			JSONObject userObject = (JSONObject) dataObject.get("user");
			JSONObject tokensObject = (JSONObject) dataObject.get("tokens");
			
			User user = userService.getUser(userObject.get("userId").toString());
	
			if (user == null) {
				String empNo = userObject.get("userId").toString();
				String empName = userObject.get("userName").toString();
				String email = userObject.get("email").toString();
				String emailCheck = "Y";
				
				Map<String, Object> request = new HashMap<>();
				request.put("empNo", empNo);
				request.put("empName", empName);
				request.put("email", email);
				request.put("emailCheck", emailCheck);
				
				int insertUserCount = userService.insertUser(request);
				if(insertUserCount < 0) {
					result.put("ret", "fail");
					result.put("error", "로그인 중 문제가 발생하였습니다. 다시 로그인해주세요.");
				}
			}
			
			User userInfo = userService.getUser(userObject.get("userId").toString());
			
			Map<String, Object> request = new HashMap<>();
			request.put("empNo", userObject.get("userId").toString());
			request.put("refreshToken", tokensObject.get("refreshToken").toString());
			result.put("userRole", userInfo.getRole());

			int updateRefreshTokenCount = userService.updateUserRefreshToken(request);
			if(updateRefreshTokenCount < 0) {
				result.put("ret", "fail");
				result.put("error", "로그인 중 문제가 발생하였습니다. 다시 로그인해주세요.");
			}

			session.setAttribute("empNo", userInfo.getEmpNo());
			session.setAttribute("empName", userInfo.getEmpName());
			session.setAttribute("email", userInfo.getEmail());
			session.setAttribute("emailCheck", userInfo.getEmailCheck());
			session.setAttribute("role", userInfo.getRole());
			session.setAttribute("accessToken", tokensObject.get("accessToken").toString());
		}else {
			result.put("ret", "fail");
			result.put("message", loginResult);
		}

		return result; 
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/api/logout
	 * @Description : 로그인한 사용자의 세션 정보를 삭제하고 로그인 페이지로 리다이렉트
	 */
	@PostMapping("/logout")
	public Map<String, Object> postLogout(HttpSession session) {
		session.removeAttribute("accessToken");
		
		Map<String, Object> result = new HashMap<>();
		result.put("ret", "success");
		result.put("message", "로그아웃이 되었습니다.");
		
		return result;
	}

	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/api/modify/information
	 * @Description : 내정보수정 기능을 위한 API
	 */
	@PostMapping("/modify/information")
	public Map<String, Object> postModifyInformation(@RequestBody UserDTO.Request request) {
		Map<String, Object> result = new HashMap<>();
		
		try {
			int updateUserCount = userService.updateUserInformation(request);
			if(updateUserCount <= 0) {
				result.put("ret", "fail");
				result.put("message", "회원정보 수정을 실패했습니다.");
			}else {
				result.put("ret", "success");
				result.put("message", "회원정보 수정을 완료했습니다.");
			}
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return result;
	}
	
	/**
	 * @추가일시 : 2022.06.28
	 * @HttpMethod : GET > http://localhost:9000/api/myinformation?empNo=사번
	 * @Description : 내정보수정 기능을 위한 API
	 */
	@GetMapping("/myinformation")
	public UserDTO.info getMyInformation(@RequestParam String empNo) {
		User user = null;
		
		try {
			user = userService.getUser(empNo);
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return UserDTO.info.of(user);
	}
}
