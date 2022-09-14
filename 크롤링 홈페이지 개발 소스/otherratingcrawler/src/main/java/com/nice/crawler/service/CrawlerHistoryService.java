package com.nice.crawler.service;

import com.nice.crawler.dto.HistoryDTO;

public interface CrawlerHistoryService {

	public int updateStartTime(HistoryDTO.Request hrequest);
	
	public int updateEndTime(HistoryDTO.Request hrequest);
	
	public String checkEndTime(HistoryDTO.Request hrequest);
}
