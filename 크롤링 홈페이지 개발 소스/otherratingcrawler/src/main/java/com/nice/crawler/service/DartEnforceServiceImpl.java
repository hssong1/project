package com.nice.crawler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nice.crawler.dto.DartDTO;
import com.nice.crawler.dto.DartResultCountDTO;
import com.nice.crawler.gather.common.DateUtil;
import com.nice.crawler.mapper.DartMapper;
import com.nice.crawler.model.Dart;

@Service
public class DartEnforceServiceImpl implements DartEnforceService {
	
	private Logger logger = LoggerFactory.getLogger(DartEnforceServiceImpl.class);
	
	@Value("${dart.list.url}")
	  private String dartUrl;
	  
	  @Value("${dart.detail.url}")
	  private String dartDetailUrl;
	  
	  @Value("${dart.fileDown.url}")
	  private String dartFileDownUrl;

	  @Value("${dart.menuNo}")
	  private String menuNo;

	  @Value("${dart.searchCount}")
	  private String searchCount;

	  @Value("${dart.comapnyCode.nicerating}")
	  private String dartNiceratingCompanyCode;

	  @Value("${dart.pageSize}")
	  private int pageSize;
	  
	  @Autowired DartMapper dartMapper;
	  
	  SlackBotService slackBotService;
	  
	  
	  /* 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : dartListStart
	   *
	   * (non-Javadoc)
	   * @see com.dart.crawling.crawler.service.CrawlerService#dartListStart(java.lang.String, java.lang.String)
	   */
	  @Override
	  public void insertDartEvaluateData(DartDTO.Request request) throws Exception {
		  
	    String toDay = DateUtil.getToday();
	    String startDate = request.getStartDate();
	    String endDate = request.getEndDate();
	    String creaId = request.getCreaId();
	    String creaName = request.getCreaName();
	    
	    StringBuilder sb = new StringBuilder();
		sb.append(creaName).append("(").append(creaId).append(")");
		String creaUser = sb.toString();
	    
	    logger.error("=======================Crawling START :: " + toDay +" :: =====================================");
	    if (startDate.length() != 10 || endDate.length() != 10) {
	      logger.error("Crawling ERROR :: DATE ERROR");
	      return;
	    }
	  
	    Document document = readDartList(startDate, endDate, 1);
	    int totalCount = getDartListTotalCount(document);
	    int errCount   = 0;
	    if(totalCount == 0) {
	      logger.error("=======================Crawling END :: NO DATA =====================================");
	      return;
	    }
	    
	    int totalPages = getDartListTotalPage(totalCount);    
	    List<Dart> dartList = new ArrayList<>();
	    //페이지의 각 라인의 key값을 가져온다
	    for (int pageNumber = 1; pageNumber <= totalPages; pageNumber++) {
	      try {
	        document  = readDartList(startDate, endDate, pageNumber);
	        dartList.addAll(parsingDartListPage(document));
	        Thread.sleep((long) (Math.random() * 3000));
	      } catch (Exception e) {
	        logger.error("신용평가서목록 ERROR [pageNumber -> " + pageNumber + "] :: " + e.getMessage());     
	        continue;
	      }
	    }
	    
	    //상세값을 DB에 저장
	    for (Dart dart : dartList) {
	      try {
	        dartDetailView(dart.getSeq(), creaId);
	        Thread.sleep((long) (Math.random() * 3000));
	      } catch (Exception e) {
	        logger.error("신용평가서 상세정보 ERROR [sequence -> " + dart.getSeq() + "]  :: " + e.getMessage());
	        errCount++;
	        continue;
	      }
	    }
	    
	    DartResultCountDTO dartResultCountDTO = new DartResultCountDTO(startDate, endDate, totalCount, errCount, creaUser);
	    
	    slackBotService.sendDartSlackBot(dartResultCountDTO);
	    
	    logger.error("총건수 :: " + totalCount + "  실패 :: " + errCount + "  성공 :: " + (totalCount-errCount) + "  DB INSERT :: " + dartMapper.findDartList(request).size());
	    logger.error("=======================Crawling END :: " + toDay +" :: =====================================");
	  }
	  
	  /**
	   * <pre>
	   *  History
	   *  2022. 2. 7., KimHJ, 1.0, 변경사항
	   * </pre>
	   * 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : dartListInterface
	   * @Param : 
	   * @Return : Document 
	   * @Description : dart의 페이지에서 데이터(html)을 읽음
	   */
	  private Document readDartList(String startDate, String endDate, int page) throws Exception {
	    return Jsoup
	        .connect(dartUrl)
	        .data("menuNo", menuNo)
	        .data("searchCompany", dartNiceratingCompanyCode)
	        .data("searchCnd", searchCount)
	        .data("pageIndex", String.valueOf(page))
	        .data("sdate", startDate)
	        .data("edate", endDate)
	        .timeout(5000)
	        .get();
	  }
	  
	  /**
	   * <pre>
	   *  History
	   *  2022. 2. 7., KimHJ, 1.0, 변경사항
	   * </pre>
	   * 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : readDartDetailView
	   * @Param : 
	   * @Return : Document 
	   * @Description : dart의 페이지에서 상세데이터(html)를 읽음
	   */
	  private Document readDartDetailView(Long seq) throws Exception {
	    return Jsoup
	            .connect(dartDetailUrl)
	            .data("menuNo", menuNo)
	            .data("seq", String.valueOf(seq))
	            .timeout(5000)
	            .get();
	  }
	  
	  /**
	   * <pre>
	   *  History
	   *  2022. 2. 7., KimHJ, 1.0, 변경사항
	   * </pre>
	   * 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : getDartListTotalCount
	   * @Param : Document
	   * @Return : int 
	   * @Description : 공시된 총 건수
	   */
	  private int getDartListTotalCount(Document document) {
	    try {
	      return Integer.parseInt(document.select("#content > div.count-total > em:nth-child(1)").text());
	    } catch (Exception e) {
	        //알람 필요. (왜냐하면 기본 html의 건수가 없으므로 뭔가 에러가 난 것이다.)
	        return 0;
	    }
	  }
	  
	  /**
	   * <pre>
	   *  History
	   *  2022. 2. 7., KimHJ, 1.0, 변경사항
	   * </pre>
	   * 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : getDartListTotalPage
	   * @Param : 
	   * @Return : int 
	   * @Description : 총건수
	   */
	  private int getDartListTotalPage(int totalCount) {
	    return (totalCount / pageSize) + ((totalCount % pageSize) > 0 ? 1 : 0);
	  }
	  
	  /**
	   * <pre>
	   *  History
	   *  2022. 2. 7., KimHJ, 1.0, 변경사항
	   * </pre>
	   * 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : parsingDartListPage
	   * @Param : Document 
	   * @Return : List<Dart> 
	   * @Description : 리스트 테이블을 parsing
	   */
	  private List<Dart> parsingDartListPage(Document document) {
	    Elements tbodyInTable = document.select("#content > div.bd-list.ovx > table > tbody");
	    Elements rows = tbodyInTable.select("tr");
	    List<Dart> list = new ArrayList<>();
	    for (Element row : rows) {
	      Elements cols = row.select("td");      
	      String link = cols.get(3).select("a").attr("href");
	      Long sequence = Long.parseLong(getParameterMap(link).get("seq"));      
	      list.add(new Dart(sequence));      
	    }
	    return list;
	  }
	  
	  /**
	   * <pre>
	   *  History
	   *  2022. 2. 7., KimHJ, 1.0, 변경사항
	   * </pre>
	   * 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : getParameterMap
	   * @Param : 
	   * @Return : Map<String,String> 
	   * @Description : url주소의 parameter를 map으로 변환
	   */
	  public Map<String, String> getParameterMap(String parameter){
	    Map<String, String> map = new HashMap<String, String>();  
	    if(parameter == null || "".equals(parameter)) return map;
	    
	    String link = parameter.substring(parameter.indexOf("?") + 1);
	    String[] params = link.split("&");  
	    for (String param : params){  
	      String [] p = param.split("=");
	      String name = p[0];  
	      if(p.length>1)  {
	        String value = p[1];  
	        map.put(name, value);
	      }  
	    }  
	    return map;  
	  } 
	  
	  
	  /**
	   * <pre>
	   *  History
	   *  2022. 2. 7., KimHJ, 1.0, 변경사항
	   * </pre>
	   * 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : dartDetailView
	   * @Param : 
	   * @Return : void 
	   * @Description : 상세 폐이지의 값을 가져옴
	   */
	  private void dartDetailView(Long sequence, String creaId) throws Exception {
		  
		 if (sequence == null || sequence < 1) return;
		  
		 Dart list = dartMapper.checkDartList(sequence);
		 
		 if(list == null) {
		 
		     Document document = readDartDetailView(sequence);
		     Dart dartDetail = parsingDartDetailView(document, sequence, creaId);
		     saveDartDetail(dartDetail);
	     
		 }
	  }
	  
	  /**
	   * <pre>
	   *  History
	   *  2022. 2. 7., KimHJ, 1.0, 변경사항
	   * </pre>
	   * 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : parsingDartDetailView
	   * @Param : 
	   * @Return : DartDetail 
	   * @Description : 상세페이지의 값을 Model 담음
	   */
	  private Dart parsingDartDetailView(Document document, Long sequence, String creaId) {
	    Elements tbodyInTable = document.select("#content > div.bd-view");
	    Elements dts = tbodyInTable.select("dt");
	    Elements dds = tbodyInTable.select("dd");
	    boolean flag = false;
	    //약정체결일 또는 약정만료일이 있는지 확인
	    for (int i = 0; i < dts.size(); i++) {
	      Element dt = dts.get(i);
	      String text = dt.text();
	      if("약정체결일".equals(text) || "약전만료일".equals(text)) {
	        flag = true;
	        break;
	      }
	    }
	    if(flag) {
	      return beingContractContractingDate(dds, sequence, creaId);
	    }else {
	      return unBeingContractContractingDate(dds, sequence, creaId);
	    }
	  }    
	  
	  /**
	   * <pre>
	   *  History
	   *  2022. 2. 14., KimHJ, 1.0, 변경사항
	   * </pre>
	   * 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : beingContractContractingDate
	   * @Param : Elements
	   * @Return : DartDetail 
	   * @Description : 약정체결관련 항목이 존재
	   */
	  private Dart beingContractContractingDate(Elements elements, Long sequence, String creaId) {    
		Dart dartDetail = new Dart();
	    for (int i = 0; i < elements.size(); i++) {    
	      Element dd = elements.get(i);
	      String text = dd.text();
	      dartDetail.setSeq(sequence);
	      dartDetail.setCreaId(creaId);
	      if(text == null || "".equals(text)) continue;      
	      switch (i) {
	        case 0: //평가회사
	       	  dartDetail.setRatgCom(text);
	          break;
	        case 1:  //법인등록번호
	          dartDetail.setCorpRgno(text);
	          break;
	        case 2:  //사업자번호
	          dartDetail.setBizNo(text);
	          break;
	        case 3:  //평가대상회사
	          dartDetail.setRatgTrgtCom(text);
	          break;
	        case 4:  //평가방법
	          dartDetail.setRatgMthd(text);
	          break;
	        case 5:  //평가대상종류
	          dartDetail.setRatgTrgtKind(text);
	          break;
	        case 6:  //발행금액
	          dartDetail.setIsueAmt(text);
	          break;  
	        case 7:  //약정체결일
	          dartDetail.setCtrtCntcDate(text);
	          break;
	        case 8:  //약정만료일
	          dartDetail.setCtrtExprFeeDate(text);
	          break;
	        case 9:  //최종만기일
	          dartDetail.setLastExpiDate(text);
	          break;  
	        case 10:  //평가구분
	          dartDetail.setRatgDivd(text);
	          break;
	        case 11:  //공시일자
	          dartDetail.setPubAnucDate(text);
	          break;
	        case 12:  //재무제표기준일
	          dartDetail.setFincShetStndDate(text);
	          break;  
	        case 13:  //등급평정일
	          dartDetail.setRankConfDate(text);
	          break;
	        case 14:  //등급유효기간
	          dartDetail.setRankVadtDate(text);
	          break;
	        case 15:  //평가등급
	          dartDetail.setRatgRank(text);
	          break;
	        case 16:  //직전 평가등급
	          dartDetail.setPrevRatgRank(text);
	          break;  
	        case 17:  //등급감시
	          dartDetail.setCrdtWatch(text);
	          break;
	        case 18:  //등급 전망
	          dartDetail.setCrdtOtlk(text);
	          break;
	        case 19:  //첨부파일          
	          String link = dd.select("a").attr("href");
	          dartDetail.setAtchFileUrl(dartFileDownUrl + link);
	          dartDetail.setAtchFileName(text);
	          break;    
	        case 20:  //비고
	          dartDetail.setRmks(text);
	          break;
	        case 21:  //등급취소전 신용평가서 등등.
//	          String seqLink = dd.select("a").attr("href");
//	          Long seq = Long.parseLong(getParameterMap(seqLink).get("seq")); 
//	          dartDetail.setCreditRatingReport(seq);
	          break;     
	      }
	    }
	    return dartDetail;
	  }
	  
	  /**
	   * <pre>
	   *  History
	   *  2022. 2. 14., KimHJ, 1.0, 변경사항
	   * </pre>
	   * 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : unBeingContractContractingDate
	   * @Param : Elements, sequence
	   * @Return : DartDetail 
	   * @Description : 약정체결관련 항목이 미존재
	   */
	  private Dart unBeingContractContractingDate(Elements elements, Long sequence, String creaId) {    
		  Dart dartDetail = new Dart();
	    for (int i = 0; i < elements.size(); i++) {    
	      Element dd = elements.get(i);
	      String text = dd.text();
	      dartDetail.setSeq(sequence);
	      dartDetail.setCreaId(creaId);
	      if(text == null || "".equals(text)) continue;      
	      switch (i) {
	      case 0: //평가회사
	       	  dartDetail.setRatgCom(text);
	          break;
	        case 1:  //법인등록번호
	          dartDetail.setCorpRgno(text);
	          break;
	        case 2:  //사업자번호
	          dartDetail.setBizNo(text);
	          break;
	        case 3:  //평가대상회사
	          dartDetail.setRatgTrgtCom(text);
	          break;
	        case 4:  //평가방법
	          dartDetail.setRatgMthd(text);
	          break;
	        case 5:  //평가대상종류
	          dartDetail.setRatgTrgtKind(text);
	          break;
	        case 6:  //발행금액
	          dartDetail.setIsueAmt(text);
	          break;
	        case 7:  //발행일
	          dartDetail.setIsueDate(text);
	          break;
	        case 8:  //만기일
	          dartDetail.setExpiDate(text);
	          break;  
	        case 9:  //평가구분
	          dartDetail.setRatgDivd(text);
	          break;
	        case 10:  //공시일자
	          dartDetail.setPubAnucDate(text);
	          break;
	        case 11:  //재무제표기준일
	          dartDetail.setFincShetStndDate(text);
	          break;  
	        case 12:  //등급평정일
	          dartDetail.setRankConfDate(text);
	          break;
	        case 13:  //등급유효기간
	          dartDetail.setRankVadtDate(text);
	          break;
	        case 14:  //평가등급
	          dartDetail.setRatgRank(text);
	          break;
	        case 15:  //직전 평가등급
	          dartDetail.setPrevRatgRank(text);
	          break;  
	        case 16:  //등급감시
	          dartDetail.setCrdtWatch(text);
	          break;
	        case 17:  //등급 전망
	          dartDetail.setCrdtOtlk(text);
	          break;
	        case 18:  //첨부파일          
		      String link = dd.select("a").attr("href");
		      dartDetail.setAtchFileUrl(dartFileDownUrl + link);
		      dartDetail.setAtchFileName(text);
		      break;    
		    case 19:  //비고
		      dartDetail.setRmks(text);
		      break;
	        case 20:  //등급취소전 신용평가서 등등.
//	          String seqLink = dd.select("a").attr("href");
//	          Long seq = Long.parseLong(getParameterMap(seqLink).get("seq")); 
//	          dartDetail.setCreditRatingReport(seq);
	          break;     
	      }
	    }
	    return dartDetail;
	  }
	  
	  /**
	   * <pre>
	   *  History
	   *  2022. 2. 7., KimHJ, 1.0, 변경사항
	   * </pre>
	   * 
	   * @FileName : CrawlerServiceImpl.java
	   * @MethodName : saveDartDetail
	   * @Param : DartDetail
	   * @Return : N/A 
	   * @Description : 가저온 값을 저장
	   */
	  private void saveDartDetail(Dart dartDetail) {
		  dartMapper.insertDartList(dartDetail);
	  }
}
