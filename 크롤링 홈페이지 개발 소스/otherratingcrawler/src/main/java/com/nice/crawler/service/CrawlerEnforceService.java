package com.nice.crawler.service;

import java.util.List;
import java.util.Map;

import com.nice.crawler.model.Crawler;

public interface CrawlerEnforceService {

	public List<Crawler> insertEvaluateData(Map<String, Object> param) throws Exception;
}
