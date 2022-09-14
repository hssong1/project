package com.nice.crawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Crawler {
	
	private Long seq;
	private String crprvid;
	private String rankConfDate;
	private String pubAnucDate;
	private String cmpCd;
	private String cmpNm;
	private String crTyp;
	private String secuTyp;
	private String bondTyp;
	private String rank;
	private String watch;
	private String outlook;
	private String issueNo;
	private String issueAmt;
	private String issueAmtTyp;
	private String issueDate;
	private String matuDate;
	private String ruleDate;
	private String series;
	private String exposeTyp;
	private String relCmpNm;
	private String creaDtme;
	private String creaId;
}
