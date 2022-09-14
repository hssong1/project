package com.nice.crawler.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.nice.crawler.dto.CrawlerDTO;
import com.nice.crawler.model.Crawler;

public interface CrawlerService {
	
	public List<Crawler> getCrawlerList(CrawlerDTO.Request request);
	
	public List<Crawler> getAfterCrawlerList(CrawlerDTO.Request request);
	
	public Map<String, Object> insertDataFromCrawlerResult(CrawlerDTO.Request request) throws Exception;
	
	public Map<String, Object> insertDataFromAfterCrawlerResult(CrawlerDTO.Request request) throws Exception;
	
	public HSSFWorkbook createCrawlingExcelFile(CrawlerDTO.Request request) throws Exception;
	
	public String checkEndTime(String type);
}