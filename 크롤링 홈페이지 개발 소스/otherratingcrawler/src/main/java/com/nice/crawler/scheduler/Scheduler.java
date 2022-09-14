package com.nice.crawler.scheduler;

import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nice.crawler.dto.CrawlerDTO;
import com.nice.crawler.dto.CrawlerResultCountDTO;
import com.nice.crawler.gather.common.Commons;
import com.nice.crawler.service.CrawlerService;
import com.nice.crawler.service.DartCrawlerService;
import com.nice.crawler.service.MailService;
import com.nice.crawler.service.SlackBotService;
import com.nice.crawler.service.UserService;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class Scheduler {
	
	private final CrawlerService crawlerService;
	private final MailService mailService;
	private final UserService userService;
	private final SlackBotService slackBotService;
	private final DartCrawlerService dartCrawlerService;
	

//	@Scheduled(cron = "0 30 22 * * 6") // 초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7)
//	public void otherRatingCrawler() throws Exception {
//		String startDate = Commons.getCurMonday();
//		String endDate = Commons.getCurFriday();
//		String type = "excel";
//		String[] crprvid = { "KR", "KIS", "SCRI" };
//		String creaId = "WEEK";
//		
//		CrawlerDTO.Request request = new CrawlerDTO.Request();
//		request.setCrprvid(crprvid);
//		request.setStartDate(startDate);
//		request.setEndDate(endDate);
//		request.setCreaId(creaId);
//		request.setType(type);
//		Map<String, Object> result = crawlerService.insertDataFromCrawlerResult(request);
//
//		CrawlerResultCountDTO crawlerResultCountDTO = new CrawlerResultCountDTO(startDate, endDate, (int) result.get("kisCount"), (int) result.get("krCount"), (int) result.get("scriCount"), creaId);
//		slackBotService.sendSlackBot(crawlerResultCountDTO);
//	}
	
	
	// 원복 필요
	@Scheduled(cron = "0 30 22 * * 6") // 초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7)
	public void otherRatingCrawler() throws Exception {
		String startDate = Commons.getCurMonday();
		String endDate = Commons.getCurFriday();
		String type = "excel";
		String[] crprvid = {"KIS", "SCRI" };
		String creaId = "WEEK";
		
		CrawlerDTO.Request request = new CrawlerDTO.Request();
		request.setCrprvid(crprvid);
		request.setStartDate(startDate);
		request.setEndDate(endDate);
		request.setCreaId(creaId);
		request.setType(type);
		Map<String, Object> result = crawlerService.insertDataFromCrawlerResult(request);

		CrawlerResultCountDTO crawlerResultCountDTO = new CrawlerResultCountDTO(startDate, endDate, (int) result.get("kisCount"), (int) result.get("krCount"), (int) result.get("scriCount"), creaId);
		slackBotService.sendSlackBot(crawlerResultCountDTO);
	}
	
//	@Scheduled(cron = "0 30 22 * * 0,1,2,3,4,5") // 초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7)
//	public void otherRatingCrawlerDaily() throws Exception {
//		String startDate = Commons.getBeforeSixDate();
//		String endDate = Commons.getNowDateExceptSign();
//		String type = "excel";
//		String[] crprvid = { "KR", "KIS", "SCRI" };
//		String creaId = "DAILY";
//		
//		CrawlerDTO.Request request = new CrawlerDTO.Request();
//		request.setCrprvid(crprvid);
//		request.setStartDate(startDate);
//		request.setEndDate(endDate);
//		request.setCreaId(creaId);
//		request.setType(type);
//		Map<String, Object> result = crawlerService.insertDataFromCrawlerResult(request);
//
//		CrawlerResultCountDTO crawlerResultCountDTO = new CrawlerResultCountDTO(startDate, endDate, (int) result.get("kisCount"), (int) result.get("krCount"), (int) result.get("scriCount"), creaId);
//		slackBotService.sendSlackBot(crawlerResultCountDTO);
//	}
	
	//원복 필요, 토요일에 KR 없애기
	@Scheduled(cron = "0 30 22 * * 0,1,2,3,4,5") // 초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7)
	public void otherRatingCrawlerDaily() throws Exception {
		String startDate = Commons.getBeforeSixDate();
		String endDate = Commons.getNowDateExceptSign();
		String type = "excel";
		String[] crprvid = { "KIS", "SCRI" };
		String creaId = "DAILY";
		
		CrawlerDTO.Request request = new CrawlerDTO.Request();
		request.setCrprvid(crprvid);
		request.setStartDate(startDate);
		request.setEndDate(endDate);
		request.setCreaId(creaId);
		request.setType(type);
		Map<String, Object> result = crawlerService.insertDataFromCrawlerResult(request);

		CrawlerResultCountDTO crawlerResultCountDTO = new CrawlerResultCountDTO(startDate, endDate, (int) result.get("kisCount"), (int) result.get("krCount"), (int) result.get("scriCount"), creaId);
		slackBotService.sendSlackBot(crawlerResultCountDTO);
	}
	
//	@Scheduled(cron = "0 0 1 * * *")    //초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7)   
//	public void dartCrawler() throws Exception {
//	    String startDate = Commons.getBeforeDate();
//	    String endDate   = Commons.getBeforeDate();
//	    String creaId = "BATCH";
//	    
//	    DartDTO.Request request = new DartDTO.Request();
//	    request.setStartDate(startDate);
//	    request.setEndDate(endDate);
//	    request.setCreaId(creaId);
//	    
//	    dartCrawlerService.insertDataFromDartCrawlerResult(request);
//	}
}
