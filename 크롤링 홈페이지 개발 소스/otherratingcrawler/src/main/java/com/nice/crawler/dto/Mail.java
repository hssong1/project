package com.nice.crawler.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Mail {
	
	private String companynumber;
    private String email;
    private String title;
    private String message;
    private int scriCount;
	private int krCount;
	private int kisCount;
	private int totalCount;
	private int errorCount;
    private String fileLocation;
}
