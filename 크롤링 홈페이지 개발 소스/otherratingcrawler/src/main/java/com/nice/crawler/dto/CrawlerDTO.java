package com.nice.crawler.dto;

import java.util.ArrayList;
import java.util.List;

import com.nice.crawler.model.Crawler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CrawlerDTO {
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class info {
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
		private String issueAmtTyp;
		private String issueAmt;
		private String issueDate;
		private String matuDate;
		private String ruleDate;
		private String series;
		private String exposeTyp;
		private String relCmpNm;
		private Long totalCount;
		private Long krCount;
		private Long kisCount;
		private Long scriCount;
		
		public static CrawlerDTO.info of(Crawler crawler) {
			CrawlerDTO.info dto = new CrawlerDTO.info();
			if(crawler == null) return dto;
			dto.setSeq(crawler.getSeq());
			dto.setCrprvid(crawler.getCrprvid());
			dto.setRankConfDate(crawler.getRankConfDate());
			dto.setPubAnucDate(crawler.getPubAnucDate());
			dto.setCmpCd(crawler.getCmpCd());
			dto.setCmpNm(crawler.getCmpNm());
			dto.setCrTyp(crawler.getCrTyp());
			dto.setSecuTyp(crawler.getSecuTyp());
			dto.setBondTyp(crawler.getBondTyp());
			dto.setRank(crawler.getRank());
			dto.setOutlook(crawler.getOutlook());
			dto.setIssueNo(crawler.getIssueNo());
			dto.setIssueAmtTyp(crawler.getIssueAmtTyp());
			dto.setIssueAmt(crawler.getIssueAmt());
			dto.setIssueDate(crawler.getIssueDate());
			dto.setMatuDate(crawler.getMatuDate());
			dto.setRuleDate(crawler.getRuleDate());
			dto.setSeries(crawler.getSeries());
			dto.setExposeTyp(crawler.getExposeTyp());
			dto.setRelCmpNm(crawler.getRelCmpNm());
			
			return dto;
		}
		
		public static List<CrawlerDTO.info> of(List<Crawler> crawlerList) {
			List<CrawlerDTO.info> dtoList = new ArrayList<>();
			if(crawlerList == null || crawlerList.size() < 1) return dtoList;
			for(Crawler crawler : crawlerList) {
				dtoList.add(CrawlerDTO.info.of(crawler));
			}
			return dtoList;
		}
	}
	
	@Getter
	@Setter
	public static class Request {
		private String[] crprvid;
		private String startDate;
		private String endDate;
		private String company;
		private String secuTyp;
		private String type;
		private String creaId;
		private String creaName;
	}
	
	@Getter
	@Setter
	public static class Response {
		private Long seq;
		private String crprvid;
		private String rateDate;
		private String cmpCd;
		private String cmpNm;
		private String crTyp;
		private String secuTyp;
		private String bondTyp;
		private String rank;
		private String outlook;
		private String issueNo;
		private String issueAmtTyp;
		private String issueAmt;
		private String issueDate;
		private String matuDate;
		private String ruleDate;
		private String series;
		private String exposeTyp;
		private String relCmpNm;
		private int totalCount;
		private int krCount;
		private int kisCount;
		private int scriCount;
	}
}
