package com.nice.crawler.controller.web;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @FileName : OtherRatingWebController.java
 * @Description : web화면을 보여주는 controller
 */
@Controller
public class CrawlerWebController {

	// GET > http://localhost:9000/
	@GetMapping("/")
	public String indexPageGET() {
		return "index";
	}
	
	// GET > http://localhost:9000/login
	@GetMapping("login")
	public String otherRatingLoginGET() {
		return "OtherRatingLogin";
	}

	// GET > http://localhost:9000/join
	@GetMapping("join")
	public String otherRatingJoinGET(@RequestParam("userId") String userId, @RequestParam("userName") String userName, @RequestParam("userEmail") String userEmail, Model model) {
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("userEmail", userEmail);
		return "OtherRatingJoin";
	}

	// GET > http://localhost:9000/user
	@GetMapping("user/crawlerlist")
	public String otherRatingUserGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/login";
		} else if (session.getAttribute("role").equals("SYSTEM")) {
			return "redirect:admin/crawlerlist";
		} else if (session.getAttribute("role").equals("MANAGER")) {
			return "redirect:manager/crawlerlist";
		} else {
			String empNo = session.getAttribute("empNo").toString();
			String empName = session.getAttribute("empName").toString();
			String email = session.getAttribute("email").toString();
			String emailCheck = session.getAttribute("emailCheck").toString();
			
			model.addAttribute("userId", empNo);
			model.addAttribute("userName", empName);
			model.addAttribute("userEmail", email);
			model.addAttribute("userEmailCheck", emailCheck);
			return "user/crawlerList";
		}	
	}
	
	// GET > http://localhost:9000/admin/crawling
	@GetMapping("user/crawling")
	public String userCrawlingGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/login";
		} else if (session.getAttribute("role").equals("SYSTEM")) {
			return "redirect:admin/crawlerlist";
		} else if (session.getAttribute("role").equals("MANAGER")) {
			return "redirect:manager/crawlerlist";
		}else {
			String empNo = session.getAttribute("empNo").toString();
			String empName = session.getAttribute("empName").toString();
			String email = session.getAttribute("email").toString();
			String emailCheck = session.getAttribute("emailCheck").toString();
			
			model.addAttribute("userId", empNo);
			model.addAttribute("userName", empName);
			model.addAttribute("userEmail", email);
			if(emailCheck == "Y") {
				model.addAttribute("userEmailCheck", 1);
			}else {
				model.addAttribute("userEmailCheck", 0);
			}
			return "user/crawling";
		}
	}
	
	@GetMapping("user/dartlist")
	public String dartListUserGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/";
		} else if (session.getAttribute("role").equals("MANAGER")) {
			return "redirect:manager/crawlerlist";
		} else if (session.getAttribute("role").equals("SYSTEM")) {
			return "redirect:admin/crawlerlist";
		} else {
			// 세션에서 사용자 이름을 받아 화면으로 넘겨주기
			String empName = session.getAttribute("empName").toString();
			
			// 모델에 추가
			model.addAttribute("userName", empName);
			return "user/dartlist";
		}
	}
	
	// GET > http://localhost:9000/admin
	@GetMapping("manager/crawlerlist")
	public String otherRatingManagerGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/login";
		} else if (session.getAttribute("role").equals("USER")) {
			return "redirect:user/crawlerlist";
		} else if (session.getAttribute("role").equals("SYSTEM")) {
			return "redirect:admin/crawlerlist";
		} else {
			String empNo = session.getAttribute("empNo").toString();
			String empName = session.getAttribute("empName").toString();
			String email = session.getAttribute("email").toString();
			String emailCheck = session.getAttribute("emailCheck").toString();
			
			model.addAttribute("userId", empNo);
			model.addAttribute("userName", empName);
			model.addAttribute("userEmail", email);
			if(emailCheck == "Y") {
				model.addAttribute("userEmailCheck", 1);
			}else {
				model.addAttribute("userEmailCheck", 0);
			}
			return "manager/crawlerList";
		}
	}
	
	// GET > http://localhost:9000/admin/crawling
	@GetMapping("manager/crawling")
	public String managerCrawlingGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/login";
		} else if (session.getAttribute("role").equals("USER")) {
			return "redirect:user/crawlerlist";
		} else if (session.getAttribute("role").equals("SYSTEM")) {
			return "redirect:admin/crawlerlist";
		} else {
			String empNo = session.getAttribute("empNo").toString();
			String empName = session.getAttribute("empName").toString();
			String email = session.getAttribute("email").toString();
			String emailCheck = session.getAttribute("emailCheck").toString();
			
			model.addAttribute("userId", empNo);
			model.addAttribute("userName", empName);
			model.addAttribute("userEmail", email);
			if(emailCheck == "Y") {
				model.addAttribute("userEmailCheck", 1);
			}else {
				model.addAttribute("userEmailCheck", 0);
			}
			return "manager/crawling";
		}
	}
	
	@GetMapping("manager/dartlist")
	public String dartListManagerGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/";
		} else if (session.getAttribute("role").equals("USER")) {
			return "redirect:user/crawlerlist";
		} else if (session.getAttribute("role").equals("SYSTEM")) {
			return "redirect:admin/crawlerlist";
		} else {
			// 세션에서 사용자 이름을 받아 화면으로 넘겨주기
			String empName = session.getAttribute("empName").toString();
			
			// 모델에 추가
			model.addAttribute("userName", empName);
			return "manager/dartlist";
		}
	}
	
	// GET > http://localhost:9000/admin/Dartcrawling
	@GetMapping("manager/dartcrawling")
	public String managerDartCrawlingGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/";
		} else if (session.getAttribute("role").equals("USER")) {
			return "redirect:user/crawlerlist";
		} else if (session.getAttribute("role").equals("SYSTEM")) {
			return "redirect:admin/crawlerlist";
		} else {
			// 세션에서 사용자 이름을 받아 화면으로 넘겨주기
			String empName = session.getAttribute("empName").toString();
			
			// 모델에 추가
			model.addAttribute("userName", empName);
			return "manager/dartcrawling";
		}
	}

	// GET > http://localhost:9000/admin
	@GetMapping("admin/crawlerlist")
	public String otherRatingAdminGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/login";
		} else if (session.getAttribute("role").equals("USER")) {
			return "redirect:user/crawlerlist";
		} else if (session.getAttribute("role").equals("MANAGER")) {
			return "redirect:manager/crawlerlist";
		} else {
			String empNo = session.getAttribute("empNo").toString();
			String empName = session.getAttribute("empName").toString();
			String email = session.getAttribute("email").toString();
			String emailCheck = session.getAttribute("emailCheck").toString();
			
			model.addAttribute("userId", empNo);
			model.addAttribute("userName", empName);
			model.addAttribute("userEmail", email);
			if(emailCheck == "Y") {
				model.addAttribute("userEmailCheck", 1);
			}else {
				model.addAttribute("userEmailCheck", 0);
			}
			return "admin/crawlerList";
		}
	}
	
	// GET > http://localhost:9000/admin/crawling
	@GetMapping("admin/crawling")
	public String adminCrawlingGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/login";
		} else if (session.getAttribute("role").equals("USER")) {
			return "redirect:user/crawlerlist";
		} else if (session.getAttribute("role").equals("MANAGER")) {
			return "redirect:manager/crawlerlist";
		} else {
			String empNo = session.getAttribute("empNo").toString();
			String empName = session.getAttribute("empName").toString();
			String email = session.getAttribute("email").toString();
			String emailCheck = session.getAttribute("emailCheck").toString();
			
			model.addAttribute("userId", empNo);
			model.addAttribute("userName", empName);
			model.addAttribute("userEmail", email);
			if(emailCheck == "Y") {
				model.addAttribute("userEmailCheck", 1);
			}else {
				model.addAttribute("userEmailCheck", 0);
			}
			return "admin/crawling";
		}
	}
	
	@GetMapping("admin/dartlist")
	public String dartListAdminGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/";
		} else if (session.getAttribute("role").equals("USER")) {
			return "redirect:user/crawlerlist";
		} else if (session.getAttribute("role").equals("MANAGER")) {
			return "redirect:manager/crawlerlist";
		} else {
			// 세션에서 사용자 이름을 받아 화면으로 넘겨주기
			String empName = session.getAttribute("empName").toString();
			
			// 모델에 추가
			model.addAttribute("userName", empName);
			return "admin/dartList";
		}
	}
	
	// GET > http://localhost:9000/admin/Dartcrawling
	@GetMapping("admin/dartcrawling")
	public String adminDartCrawlingGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/";
		} else if (session.getAttribute("role").equals("USER")) {
			return "redirect:user/crawlerlist";
		} else if (session.getAttribute("role").equals("MANAGER")) {
			return "redirect:manager/crawlerlist";
		} else {
			// 세션에서 사용자 이름을 받아 화면으로 넘겨주기
			String empName = session.getAttribute("empName").toString();
			
			// 모델에 추가
			model.addAttribute("userName", empName);
			return "admin/dartcrawling";
		}
	}
	
	// GET > http://localhost:9000/admin/userlist
	@GetMapping("admin/userlist")
	public String adminUserListGET(Model model, HttpSession session) {
		if (session.getAttribute("accessToken") == null) {
			return "redirect:/login";
		} else if (session.getAttribute("role").equals("USER")) {
			return "redirect:user/crawlerlist";
		} else if (session.getAttribute("role").equals("MANAGER")) {
			return "redirect:manager/crawlerlist";
		} else {
			String empNo = session.getAttribute("empNo").toString();
			String empName = session.getAttribute("empName").toString();
			String email = session.getAttribute("email").toString();
			String emailCheck = session.getAttribute("emailCheck").toString();
			
			model.addAttribute("userId", empNo);
			model.addAttribute("userName", empName);
			model.addAttribute("userEmail", email);
			if(emailCheck == "Y") {
				model.addAttribute("userEmailCheck", 1);
			}else {
				model.addAttribute("userEmailCheck", 0);
			}
			return "admin/userList";
		}
	}
}
