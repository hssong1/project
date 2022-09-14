package com.nice.crawler.service;

import org.springframework.stereotype.Service;

import com.nice.crawler.dto.HistoryDTO;
import com.nice.crawler.mapper.CrawlerHistoryMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CrawlerHistoryServiceImpl implements CrawlerHistoryService {
	
	private final CrawlerHistoryMapper crawlerHistoryMapper;
	
	@Override
	public int updateStartTime(HistoryDTO.Request hrequest) {
		return crawlerHistoryMapper.updateStartTime(hrequest);
	}

	@Override
	public int updateEndTime(HistoryDTO.Request hrequest) {
		return crawlerHistoryMapper.updateEndTime(hrequest);
	}

	@Override
	public String checkEndTime(HistoryDTO.Request hrequest) {
		return crawlerHistoryMapper.checkEndTime(hrequest);
	}
}
