package com.nice.crawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrawlerHistory {
	
	private Long seq;
	private String startTime;
	private String endTime;
	private String flag;
	private String reason;
}
