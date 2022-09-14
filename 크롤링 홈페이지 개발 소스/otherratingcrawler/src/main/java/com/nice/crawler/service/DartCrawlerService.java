package com.nice.crawler.service;

import java.util.List;
import java.util.Map;

import com.nice.crawler.dto.DartDTO;
import com.nice.crawler.model.Dart;

public interface DartCrawlerService {

	public List<Dart> getDartList(DartDTO.Request request);
	
	public Map<String, Object> insertDataFromDartCrawlerResult(DartDTO.Request request) throws Exception;
	
	public String checkEndTime();
}
