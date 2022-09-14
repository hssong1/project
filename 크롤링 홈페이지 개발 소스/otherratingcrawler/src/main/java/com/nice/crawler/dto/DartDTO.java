package com.nice.crawler.dto;

import java.util.ArrayList;
import java.util.List;

import com.nice.crawler.model.Dart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class DartDTO {
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class info {
		private Long seq;
		private String ratgCom;
		private String ratgTrgtCom;
		private String corpRgno;
		private String bizNo;
		private String ratgMthd;
		private String ratgTrgtKind;
		private String isueAmt;
		private String isueDate;
		private String expiDate;
		private String ctrtCntcDate;
		private String ctrtExprFeeDate;
		private String lastExpiDate;
		private String ratgDivd;
		private String rankConfDate;
		private String pubAnucDate;
		private String fincShetStndDate;
		private String rankVadtDate;
		private String prevRatgRank;
		private String ratgRank;
		private String crdtWatch;
		private String crdtOtlk;
		private String atchFileUrl;
		private String atchFileName;
		private String rmks;
		private String crReptSeq;
		private String creaId;
		private String creaDtme;
		
		public static DartDTO.info of(Dart dart) {
			DartDTO.info dto = new DartDTO.info();
			if(dart == null) return dto;
			dto.setSeq(dart.getSeq());
			dto.setRatgCom(dart.getRatgCom());
			dto.setRatgTrgtCom(dart.getRatgTrgtCom());
			dto.setCorpRgno(dart.getCorpRgno());
			dto.setBizNo(dart.getBizNo());
			dto.setRatgMthd(dart.getRatgMthd());
			dto.setRatgTrgtKind(dart.getRatgTrgtKind());
			dto.setIsueAmt(dart.getIsueAmt());
			dto.setIsueDate(dart.getIsueDate());
			dto.setExpiDate(dart.getExpiDate());
			dto.setCtrtCntcDate(dart.getCtrtCntcDate());
			dto.setCtrtExprFeeDate(dart.getCtrtExprFeeDate());
			dto.setLastExpiDate(dart.getLastExpiDate());
			dto.setRatgDivd(dart.getRatgDivd());
			dto.setRankConfDate(dart.getRankConfDate());
			dto.setPubAnucDate(dart.getPubAnucDate());
			dto.setFincShetStndDate(dart.getFincShetStndDate());
			dto.setRankVadtDate(dart.getRankVadtDate());
			dto.setPrevRatgRank(dart.getPrevRatgRank());
			dto.setRatgRank(dart.getRatgRank());
			dto.setCrdtWatch(dart.getCrdtWatch());
			dto.setCrdtOtlk(dart.getCrdtOtlk());
			dto.setAtchFileUrl(dart.getAtchFileUrl());
			dto.setAtchFileName(dart.getAtchFileName());
			dto.setRmks(dart.getRmks());
			dto.setCrReptSeq(dart.getCrReptSeq());
			dto.setCreaId(dart.getCreaId());
			dto.setCreaDtme(dart.getCreaDtme());

			return dto;
		}
		
		public static List<DartDTO.info> of(List<Dart> dartList) {
			List<DartDTO.info> dtoList = new ArrayList<>();
			if(dartList == null || dartList.size() < 1) return dtoList;
			for(Dart dart : dartList) {
				dtoList.add(DartDTO.info.of(dart));
			}
			return dtoList;
		}
	}
	
	@Getter
	@Setter
	public static class Request {
		private String startDate;
		private String endDate;
		private String type;
		private String creaId;
		private String creaName;
	}
	
	@Getter
	@Setter
	public static class Response {
		private Long seq;
		private String ratgCom;
		private String ratgTrgtCom;
		private String corpRgno;
		private String bizNo;
		private String ratgMthd;
		private String ratgTrgtKind;
		private String isueAmt;
		private String isueDate;
		private String expiDate;
		private String ctrtCntcDate;
		private String ctrtExprFeeDate;
		private String lastExpiDate;
		private String ratgDivd;
		private String rankConfDate;
		private String pubAnucDate;
		private String fincShetStndDate;
		private String rankVadtDate;
		private String prevRatgRank;
		private String ratgRank;
		private String crdtWatch;
		private String crdtOtlk;
		private String atchFileUrl;
		private String atchFileName;
		private String rmks;
		private String crReptSeq;
		private String creaId;
		private String creaDtme;
	}
}
