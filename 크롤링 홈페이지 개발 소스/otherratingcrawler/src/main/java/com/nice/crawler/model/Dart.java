package com.nice.crawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dart {
	
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
	
	public Dart(Long seq) {
	    this.seq = seq;
	}
}
