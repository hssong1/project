package com.nice.crawler.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nice.crawler.dto.CrawlerDTO;
import com.nice.crawler.dto.DartDTO;
import com.nice.crawler.dto.UserDTO;
import com.nice.crawler.model.Crawler;
import com.nice.crawler.model.Dart;
import com.nice.crawler.model.User;
import com.nice.crawler.service.CrawlerService;
import com.nice.crawler.service.DartCrawlerService;
import com.nice.crawler.service.UserService;

/**
 * 
 * @FileName : AdminApiController.java
 * @Description : 관리자 관련 기능을 위한 Api Controller
 */
@RestController
@RequestMapping(value = "/admin/api")
public class AdminApiController {
	
	private final CrawlerService crawlerService;
	private final UserService userService;
	private final DartCrawlerService dartCrawlerService;
	
	public AdminApiController(UserService userService, CrawlerService crawlerService, DartCrawlerService dartCrawlerService) {
		this.crawlerService = crawlerService;
		this.userService = userService;
		this.dartCrawlerService = dartCrawlerService;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/admin/api/crawler
	 * @Description : 크롤링된 타사 데이터를 조회하기 위한 기능
	 */
	@PostMapping("/crawler")
	public List<CrawlerDTO.info> getCrawlerList(CrawlerDTO.Request request) {
		List<Crawler> crawlerList = null;
		
		try {
			crawlerList = crawlerService.getCrawlerList(request);
		}catch(Exception error) {
			System.err.println(error);
		}
		
		return CrawlerDTO.info.of(crawlerList);
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/admin/api/after/crawler
	 * @Description : 수동처리 후 크롤링된 타사 데이터를 조회하기 위한 기능
	 */
	@PostMapping("/after/crawler")
	public List<CrawlerDTO.info> getAfterCrawlerList(CrawlerDTO.Request request) {
		List<Crawler> crawlerList = null;
		
		try {
			crawlerList = crawlerService.getAfterCrawlerList(request);
		}catch(Exception error) {
			System.err.println(error);
		}
	
		return CrawlerDTO.info.of(crawlerList);
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/admin/api/enforce/crawler
	 * @Description : 타사 크롤링 수행기능
	 */
	@PostMapping("/enforce/crawler")
	public Map<String, Object> postEnforceCrawler(CrawlerDTO.Request request) {
		Map<String, Object> result = new HashMap<>();
		String startDate = request.getStartDate();
		String endDate = request.getEndDate();
		String creaId = request.getCreaId();
		
		if(creaId == "" || creaId == null) {
			creaId = "MANUAL";
		}
		
		request.setCreaId(creaId);
		
		try {
			if(!startDate.equals("") && !endDate.equals("")) {
				result = crawlerService.insertDataFromCrawlerResult(request);
			}else {
				result.put("ret", "fail");
				result.put("message", "시작 날짜와 종료 날짜를 선택해주세요.");
			}
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/admin/api/enforce/after/crawler
	 * @Description : 타사 크롤링 수동처리
	 */
	@PostMapping("/enforce/after/crawler")
	public Map<String, Object> postEnforceAfterCrawler(CrawlerDTO.Request request, HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		String startDate = request.getStartDate();
		String endDate = request.getEndDate();
		String creaId = null;
		String creaName = null;
		
		if(session.getAttribute("empNo").toString() != "") {
			creaId = session.getAttribute("empNo").toString();
			creaName = session.getAttribute("empName").toString();
		}else {
			creaId = "MANUAL";
		}
		
		request.setCreaId(creaId);
		request.setCreaName(creaName);
		
		try {
			if(!startDate.equals("") && !endDate.equals("")) {
				result = crawlerService.insertDataFromAfterCrawlerResult(request);
			}else {
				result.put("ret", "fail");
				result.put("message", "시작 날짜와 종료 날짜를 선택해주세요.");
			}
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/admin/api/check/available
	 * @Description : 타사크롤링, 금감원 크롤링이 수동처리 중인지 체크하기 위한 기능 
	 */
	@PostMapping("/check/available")
	public Map<String, Object> checkAvailable(CrawlerDTO.Request request) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String type = request.getType();
        
        try {
        	String endTimeFlag = crawlerService.checkEndTime(type);
        	if(endTimeFlag.equalsIgnoreCase("C")) {
        		result.put("ret", "fail");
        	}else {
        		result.put("ret", "success");
        	}
		} catch (Exception error) {
			System.err.println(error);
		}
        
        return result;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/admin/api/check/endtime
	 * @Description : 타사크롤링, 금감원 크롤링이 종료되었는지 체크하기 위한 기능 
	 */
	@PostMapping("/check/endtime")
	public Map<String, Object> checkendTime(CrawlerDTO.Request request) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String type = request.getType();
        
        try {
        	String endTimeFlag = crawlerService.checkEndTime(type);
        	if(endTimeFlag.equalsIgnoreCase("E")) {
        		result.put("ret", "success");
        	}else {
        		result.put("ret", "fail");
        	}
		} catch (Exception error) {
			System.err.println(error);
		}
        
        return result;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/admin/api/dartlist
	 * @Description : 크롤링된 금감원 데이터를 조회하기 위한 기능
	 */
	@PostMapping("/dartlist")
	public List<DartDTO.info> getDartList(DartDTO.Request request) {
		List<Dart> dartList = null;
		
		try {
			dartList = dartCrawlerService.getDartList(request);
		}catch(Exception error) {
			System.err.println(error);
		}
		
		return DartDTO.info.of(dartList);
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/admin/api/enforce/dart/crawler
	 * @Description : 금감원 크롤링 수동처리
	 */
	@PostMapping("/enforce/dart/crawler")
	public Map<String, Object> postEnforceDartCrawler(DartDTO.Request request, HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		String startDate = request.getStartDate();
		String endDate = request.getEndDate();
		String creaId = request.getCreaId();
		
		if(creaId==null) {
			if(session.getAttribute("empNo").toString() != "") {
				creaId = session.getAttribute("empName")+"("+session.getAttribute("empNo").toString()+")";
			}else {
				creaId = "MANUAL";
			}
		}
		
		request.setCreaId(creaId);
		
		try {
			if(!startDate.equals("") && !endDate.equals("")) {
				result = dartCrawlerService.insertDataFromDartCrawlerResult(request);
			}else {
				result.put("ret", "fail");
				result.put("message", "시작 날짜와 종료 날짜를 선택해주세요.");
			}
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/admin/api/check/endDarttime
	 * @Description : 금감원크롤링이 종료되었는지 체크하기 위한 기능 
	 */
	@PostMapping("/check/endDarttime")
	public Map<String, Object> checkendDartTime(DartDTO.Request request) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String type = request.getType();
        
        try {
        	String endTimeFlag = crawlerService.checkEndTime(type);
        	if(endTimeFlag.equalsIgnoreCase("E")) {
        		result.put("ret", "success");
        	}else {
        		result.put("ret", "fail");
        	}
		} catch (Exception error) {
			System.err.println(error);
		}
        
        return result;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/admin/api/user/search
	 * @Description : 모든 사용자를 조회하는 기능
	 */
	@PostMapping("/user/search")
	public List<User> getUserList(UserDTO.Request request) {
		List<User> userList = null;
		
		try {
			userList = userService.getUserList(request);
		} catch (Exception error) {
			System.err.println(error);
		}

		return userList;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/admin/api/user/update
	 * @Description : 선택한 사용자의 정보를 수정하는 기능
	 */
	@PostMapping("/user/update")
	public Map<String, Object> updateUser(UserDTO.Request request) {
		Map<String, Object> result = new HashMap<>();
		String[] empNoList = request.getEmpNoList();
		String[] roleList = request.getRoleList();
		String[] emailCheckList = request.getEmailCheckList();
				
		try {
			 int updateUsersCount = userService.updateUsers(empNoList, roleList, emailCheckList);
			 if(updateUsersCount > 0) {
				 result.put("ret", "success");
				 result.put("message", "회원정보 업데이트가 완료되었습니다.");
			 }else {
				 result.put("ret", "fail");
				 result.put("message", "회원정보 업데이트가 실패되었습니다.");
			 }
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return result;
	}	 
}