package com.nice.crawler.mapper;

import org.springframework.stereotype.Repository;

import com.nice.crawler.dto.HistoryDTO;

@Repository
public interface CrawlerHistoryMapper {
	
	public int updateStartTime(HistoryDTO.Request hrequest);
	
	public int updateEndTime(HistoryDTO.Request hrequest);
	
	public String checkEndTime(HistoryDTO.Request hrequest);
}
