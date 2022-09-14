package com.nice.crawler.controller.api;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nice.crawler.dto.CrawlerDTO;
import com.nice.crawler.model.Crawler;
import com.nice.crawler.service.CrawlerService;

/**
 * 
 * @FileName : UserApiController.java
 * @Description : 사용자 관련 기능을 위한 Api Controller
 */
@RestController
@RequestMapping(value = "/user/api")
public class UserApiController {
	
	private final CrawlerService crawlerService;
	
	public UserApiController(CrawlerService crawlerService) {
		this.crawlerService = crawlerService;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/user/api/crawler
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
}