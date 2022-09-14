package com.nice.crawler.intercepter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserLoginIntercepter implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String requestUri = request.getRequestURI();
		log.info("인증 체크 인터셉터 실행 {}", requestUri);
		
		HttpSession session = request.getSession();
		
		Object accessToken = session.getAttribute("accessToken");
		
		// 세션에 저장된 정보가 없을 경우 로그인 페이지로 이동
		if(ObjectUtils.isEmpty(accessToken)) {
			log.info("미인증 사용자 요청");
			response.sendError(200, "session expire"); 
			return false;
		}
		
		return true;
	}
}
