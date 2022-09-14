package com.nice.crawler.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nice.crawler.dto.DartDTO;
import com.nice.crawler.dto.HistoryDTO;
import com.nice.crawler.mapper.DartMapper;
import com.nice.crawler.model.Dart;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DartCrawlerServiceImpl implements DartCrawlerService {
	
	private final DartMapper dartMapper;
	private final DartEnforceService dartEnforceService;
	private final CrawlerHistoryService crawlerHistoryService;

	public List<Dart> getDartList(DartDTO.Request request){
		return dartMapper.findDartList(request);
	}
	
	@Async
	public Map<String, Object> insertDataFromDartCrawlerResult(DartDTO.Request request) throws Exception{
		
		LocalDateTime start = LocalDateTime.now();
		String startTime = start.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String creaId = request.getCreaId();
		
		HistoryDTO.Request hrequest = new HistoryDTO.Request();
		hrequest.setStartTime(startTime);
		hrequest.setCreaId(creaId);
		hrequest.setGubun("Dart");
		hrequest.setState("C");
		
		int startTimeUpdateCount = crawlerHistoryService.updateStartTime(hrequest);
		
		Map<String, Object> result = new HashMap<>();
		dartEnforceService.insertDartEvaluateData(request);
		
		LocalDateTime end = LocalDateTime.now();
		String endTime = end.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		
		hrequest.setEndTime(endTime);
		hrequest.setGubun("Dart");
		hrequest.setState("E");
		
		int endTimeUpdateCount = crawlerHistoryService.updateEndTime(hrequest);
		
		result.put("ret", "success");
		
		return result;
	}
	
	@Override
	public String checkEndTime() {
		
		String endTimeFlag = null;
		
		HistoryDTO.Request hrequest = new HistoryDTO.Request();
		
		hrequest.setGubun("Dart");
		
		try {
			endTimeFlag = crawlerHistoryService.checkEndTime(hrequest);
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return endTimeFlag;
	}
}
