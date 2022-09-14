package com.nice.crawler.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.nice.crawler.dto.CrawlerDTO;
import com.nice.crawler.model.Crawler;

@Repository
public interface CrawlerMapper {
	
	public List<Crawler> findCrawlerList(CrawlerDTO.Request request);
	
	public List<Crawler> findAfterCrawlerList(CrawlerDTO.Request request);
	
	public void InsertCrawlerList(Crawler crawler);
	
	public void InsertAfterCrawlerList(Crawler crawler);
	
	public int deleteCrawlerData(Map<String, Object> param);
	
	public int deleteAfterCrawlerData(Map<String, Object> param);
}
