package com.nice.crawler.dto;

import lombok.Getter;
import lombok.Setter;

public class HistoryDTO {
	
	@Getter
	@Setter
	public static class Request {
		private String startTime;
		private String endTime;
		private String gubun;
		private String state;
		private String creaId;
	}
}
