package com.nice.crawler.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DartResultCountDTO {

    private String startDate;
    private String endDate;
    private int totalCount;
    private int errCount;
    private String creaUser;

    public int succesCount () {
        return this.totalCount - this.errCount;
    }

    public String countMessage() {
        return "성공 : "+ this.succesCount() + "개 실패 : " + this.errCount + "개 총 : " + this.totalCount + "개";
    }

    public String periodMessage() {
        return "기간 : " + this.startDate + "~" + this.endDate;
    }
    
    public String IdMessage() {
    	return this.creaUser + "님이 돌렸습니다.";
    }

    public DartResultCountDTO(String startDate, String endDate, int totalCount, int errCount, String creaUser) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCount = totalCount;
        this.errCount = errCount;
        this.creaUser = creaUser;
    }
}
