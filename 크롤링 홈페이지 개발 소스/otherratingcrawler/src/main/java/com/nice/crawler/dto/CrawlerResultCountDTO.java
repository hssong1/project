package com.nice.crawler.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CrawlerResultCountDTO {

    private String startDate;
    private String endDate;
    private int kisCount;
    private int krCount;
    private int scriCount;
    private String creaId;

    public int totalCount () {
        return this.kisCount + this.krCount + this.scriCount;
    }

    public String countMessage() {
        return "KIS : "+ this.kisCount + "개 KR : " + this.krCount + "개 SCRI : " + this.scriCount + "개 총 : " + this.totalCount() + "개";
    }

    public String periodMessage() {
        return "기간 : " + this.startDate + "~" + this.endDate;
    }
    
    public String IdMessage() {
    	return this.creaId + "님이 돌렸습니다.";
    }

    public CrawlerResultCountDTO(String startDate, String endDate, int kisCount, int krCount, int scriCount, String creaId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.kisCount = kisCount;
        this.krCount = krCount;
        this.scriCount = scriCount;
        this.creaId = creaId;
    }
}
