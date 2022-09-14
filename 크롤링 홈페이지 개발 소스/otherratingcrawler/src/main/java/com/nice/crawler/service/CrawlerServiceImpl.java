package com.nice.crawler.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nice.crawler.dto.CrawlerDTO;
import com.nice.crawler.dto.CrawlerResultCountDTO;
import com.nice.crawler.dto.HistoryDTO;
import com.nice.crawler.gather.common.Commons;
import com.nice.crawler.gather.common.ExcelStyle;
import com.nice.crawler.mapper.CrawlerMapper;
import com.nice.crawler.model.Crawler;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {

	private final CrawlerMapper crawlerMapper;
	private final CrawlerEnforceService crawlerEnforceService;
	private final CrawlerHistoryService crawlerHistoryService;
	private final SlackBotService slackBotService;
	
	public List<Crawler> getCrawlerList(CrawlerDTO.Request request) {
		return crawlerMapper.findCrawlerList(request);
	}
	
	public List<Crawler> getAfterCrawlerList(CrawlerDTO.Request request) {
		return crawlerMapper.findAfterCrawlerList(request);
	}

	@Override
	public Map<String, Object> insertDataFromCrawlerResult(CrawlerDTO.Request request) throws Exception {
		LocalDateTime start = LocalDateTime.now();
		String startTime = start.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String creaId = request.getCreaId();
		
		HistoryDTO.Request hrequest = new HistoryDTO.Request();
		hrequest.setStartTime(startTime);
		hrequest.setCreaId(creaId);
		hrequest.setGubun("OTHER");
		hrequest.setState("C");
		
		int startTimeUpdateCount = crawlerHistoryService.updateStartTime(hrequest);
		
		Map<String, Object> result = new HashMap<>();
		int kisRatingSuccessCount = 0;
		int krRatingSuccessCount = 0;
		int scriRatingSuccessCount = 0;
		
		if(startTimeUpdateCount < 1) {
			System.err.println("CrawlerHistoryService의 updateStartTime 실행 에러");
			result.put("ret","fail");
			return result;
		}
		
		String startDate = request.getStartDate();
		String endDate = request.getEndDate();
		String[] crprvid = request.getCrprvid();
		
		Map<String, Object> param = new HashMap<>();
		
		if(!startDate.equals("") && !endDate.equals("")) {
			param.put("startDate", startDate);
			param.put("endDate", endDate);
			param.put("crprvid", crprvid);
		}else {
			param.put("startDate", Commons.getCurMonday());
			param.put("endDate", Commons.getCurFriday());
			param.put("crprvid", crprvid);
		}
		
		int deleteCrawlerDataCount = crawlerMapper.deleteCrawlerData(param);
		/*
		if(deleteCrawlerDataCount < 1) {
			System.err.println("CrawlerMapper의 deleteCrawlerData 실행 에러");
			result.put("ret","fail");
			return result;
		}
		*/
		
		List<Crawler> crawlerList = crawlerEnforceService.insertEvaluateData(param);
		
		for(Crawler crawler : crawlerList) {
			try {
				crawler.setCreaId(creaId);
				crawlerMapper.InsertCrawlerList(crawler);
				
				if(crawler.getCrprvid() == "KIS") {
					kisRatingSuccessCount++;
				}else if(crawler.getCrprvid() == "KR") {
					krRatingSuccessCount++;
				}else if(crawler.getCrprvid() == "SCRI") {
					scriRatingSuccessCount++;
				}
			} catch (Exception error) {
				System.err.println(error);
			}
		}
		
		LocalDateTime end = LocalDateTime.now();
		String endTime = end.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		
		hrequest.setEndTime(endTime);
		hrequest.setGubun("OTHER");
		hrequest.setState("E");
		
		int endTimeUpdateCount = crawlerHistoryService.updateEndTime(hrequest);
		
		result.put("ret", "success");
		result.put("kisCount", kisRatingSuccessCount);
		result.put("krCount", krRatingSuccessCount);
		result.put("scriCount", scriRatingSuccessCount);
				
		return result;
	}
	
	@Async
	@Override
	public Map<String, Object> insertDataFromAfterCrawlerResult(CrawlerDTO.Request request) throws Exception {
		LocalDateTime start = LocalDateTime.now();
		String startTime = start.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String creaId = request.getCreaId();
		String creaName = request.getCreaName();
		
		StringBuilder sb = new StringBuilder();
		sb.append(creaName).append("(").append(creaId).append(")");
		String creaUser = sb.toString();
		
		HistoryDTO.Request hrequest = new HistoryDTO.Request();
		hrequest.setStartTime(startTime);
		hrequest.setCreaId(creaId);
		hrequest.setGubun("OTHER");
		hrequest.setState("C");
		
		int startTimeUpdateCount = crawlerHistoryService.updateStartTime(hrequest);
		
		Map<String, Object> result = new HashMap<>();
		int kisRatingSuccessCount = 0;
		int krRatingSuccessCount = 0;
		int scriRatingSuccessCount = 0;
		
		if(startTimeUpdateCount < 1) {
			System.err.println("CrawlerHistoryService의 updateStartTime 실행 에러");
			result.put("ret","fail");
			return result;
		}
		
		String startDate = request.getStartDate();
		String endDate = request.getEndDate();
		String[] crprvid = request.getCrprvid();
		
		Map<String, Object> param = new HashMap<>();
		
		if(!startDate.equals("") && !endDate.equals("")) {
			param.put("startDate", startDate);
			param.put("endDate", endDate);
			param.put("crprvid", crprvid);
		}else {
			param.put("startDate", Commons.getCurMonday());
			param.put("endDate", Commons.getCurFriday());
			param.put("crprvid", crprvid);
		}
		
		int deleteAfterCrawlerDataCount = crawlerMapper.deleteAfterCrawlerData(param);
		/*
		if(deleteCrawlerDataCount < 1) {
			System.err.println("CrawlerMapper의 deleteCrawlerData 실행 에러");
			result.put("ret","fail");
			return result;
		}
		*/
		
		List<Crawler> crawlerList = crawlerEnforceService.insertEvaluateData(param);
		
		for(Crawler crawler : crawlerList) {
			try {
				crawler.setCreaId(creaId);
				crawlerMapper.InsertAfterCrawlerList(crawler);
				
				if(crawler.getCrprvid() == "KIS") {
					kisRatingSuccessCount++;
				}else if(crawler.getCrprvid() == "KR") {
					krRatingSuccessCount++;
				}else if(crawler.getCrprvid() == "SCRI") {
					scriRatingSuccessCount++;
				}
			} catch (Exception error) {
				System.err.println(error);
			}
		}
		
		LocalDateTime end = LocalDateTime.now();
		String endTime = end.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		
		hrequest.setEndTime(endTime);
		hrequest.setGubun("OTHER");
		hrequest.setState("E");
		
		int endTimeUpdateCount = crawlerHistoryService.updateEndTime(hrequest);
		
		result.put("ret", "success");
		result.put("kisCount", kisRatingSuccessCount);
		result.put("krCount", krRatingSuccessCount);
		result.put("scriCount", scriRatingSuccessCount);
		
		CrawlerResultCountDTO crawlerResultCountDTO = new CrawlerResultCountDTO(startDate, endDate, kisRatingSuccessCount, krRatingSuccessCount, scriRatingSuccessCount, creaUser);
		slackBotService.sendSlackBotEnforce(crawlerResultCountDTO);
		
		return result;
	}
	
	public HSSFWorkbook createCrawlingExcelFile(CrawlerDTO.Request request) throws Exception {
		Map<String, Object> param = new HashMap<>();
		String startDate = request.getStartDate();
		String endDate = request.getEndDate();
		String type = request.getType();
		String[] crprvid = { "KIS","KR","SCRI" };
		String[] secutyp = { "기업어음", "전자단기사채", "회사채", "자산유동화증권", "기업신용평가", "보험금지급능력평가", "커버드본드" };
		
		if (!startDate.equals("") && !endDate.equals("")) {
			param.put("startDate", startDate);
			param.put("endDate", endDate);
		} else {
			param.put("startDate", Commons.getCurMonday());
			param.put("endDate", Commons.getCurFriday());
		}

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle crprvid_style = ExcelStyle.getCrprvidStyle(workbook);
		HSSFCellStyle secu_style = ExcelStyle.getSecuTypStyle(workbook);
		HSSFCellStyle dataheader_style = ExcelStyle.getDataHeaderStyle(workbook);
		HSSFCellStyle data_style = ExcelStyle.getDataStyle(workbook);

		for (int crprvid_num = 0; crprvid_num < crprvid.length; crprvid_num++) {
			int rownum = 0;

			HSSFSheet sheet = workbook.createSheet(crprvid[crprvid_num]); // Sheet명 설정
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
			HSSFRow row = sheet.createRow(rownum);
			switch (crprvid[crprvid_num]) {
				case "NICE":
					row.createCell(0).setCellValue("NICE신용평가");
					row.getCell(0).setCellStyle(crprvid_style);
					rownum = 2;
					break;
				case "KIS":
					row.createCell(0).setCellValue("한국신용평가");
					row.getCell(0).setCellStyle(crprvid_style);
					rownum = 2;
					break;
				case "KR":
					row.createCell(0).setCellValue("한국기업평가");
					row.getCell(0).setCellStyle(crprvid_style);
					rownum = 2;
					break;
				case "SCRI":
					row.createCell(0).setCellValue("서울신용평가");
					row.getCell(0).setCellStyle(crprvid_style);
					rownum = 2;
					break;
				default:
					rownum = 2;
					break;
			}

			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
			row = sheet.createRow(rownum);
			row.createCell(0).setCellValue("기간 : " + Commons.getChangeDateString((String) param.get("startDate"), ".")
					+ " ~ " + Commons.getChangeDateString((String) param.get("endDate"), "."));
			rownum++;
			rownum++;

			System.out.println("secutyp.length : " + secutyp.length);
			for (int secutyp_num = 0; secutyp_num < secutyp.length; secutyp_num++) {
				row = sheet.createRow(rownum);
				for (int secu_cell_num = 0; secu_cell_num <= 10; secu_cell_num++) { // 채권종류 타이틀
					row.createCell(secu_cell_num);
					row.getCell(secu_cell_num).setCellStyle(secu_style);
				}
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 10));
				row.getCell(0).setCellValue("[" + secutyp[secutyp_num] + "]");

				List<Crawler> resultList = new ArrayList<Crawler>();
				
				request.setCompany(crprvid[crprvid_num]);
				request.setSecuTyp(secutyp[secutyp_num]);
				request.setStartDate(startDate);
				request.setEndDate(endDate);

				// 파라미터에 평가사, 채권종류 적용
				param.put("crprvid", crprvid[crprvid_num]);
				param.put("secutyp", secutyp[secutyp_num]);

				try {
					if(type == "excel") {
						resultList = crawlerMapper.findCrawlerList(request);
					}else if(type == "afterExcel") {
						resultList = crawlerMapper.findAfterCrawlerList(request);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 칼럼 그리기
				rownum++;
				rownum++;
				row = sheet.createRow(rownum);

				int col_num = 0;
				switch (secutyp[secutyp_num]) {
					case "기업어음":
						row.createCell(0).setCellValue("종류");
						row.createCell(1).setCellValue("업체명");
						row.createCell(2).setCellValue("기준일");
						row.createCell(3).setCellValue("등급");
						row.createCell(4).setCellValue("등급확정일");
						row.getCell(0).setCellStyle(dataheader_style);
						row.getCell(1).setCellStyle(dataheader_style);
						row.getCell(2).setCellStyle(dataheader_style);
						row.getCell(3).setCellStyle(dataheader_style);
						row.getCell(4).setCellStyle(dataheader_style);
						break;
					case "전자단기사채":
						row.createCell(0).setCellValue("종류");
						row.createCell(1).setCellValue("업체명");
						row.createCell(2).setCellValue("기준일");
						row.createCell(3).setCellValue("발행한도(억원)");
						row.createCell(4).setCellValue("등급");
						row.createCell(5).setCellValue("등급확정일");
						row.getCell(0).setCellStyle(dataheader_style);
						row.getCell(1).setCellStyle(dataheader_style);
						row.getCell(2).setCellStyle(dataheader_style);
						row.getCell(3).setCellStyle(dataheader_style);
						row.getCell(4).setCellStyle(dataheader_style);
						row.getCell(5).setCellStyle(dataheader_style);
						break;
					case "회사채":
						row.createCell(0).setCellValue("종류");
						row.createCell(1).setCellValue("업체명");
						row.createCell(2).setCellValue("사채명");
						row.createCell(3).setCellValue("회차");
						row.createCell(4).setCellValue("발행액(억원)");
						row.createCell(5).setCellValue("발행일");
						row.createCell(6).setCellValue("만기일");
						row.createCell(7).setCellValue("등급");
						row.createCell(8).setCellValue("Outlook");
						row.createCell(9).setCellValue("등급확정일");
						row.getCell(0).setCellStyle(dataheader_style);
						row.getCell(1).setCellStyle(dataheader_style);
						row.getCell(2).setCellStyle(dataheader_style);
						row.getCell(3).setCellStyle(dataheader_style);
						row.getCell(4).setCellStyle(dataheader_style);
						row.getCell(5).setCellStyle(dataheader_style);
						row.getCell(6).setCellStyle(dataheader_style);
						row.getCell(7).setCellStyle(dataheader_style);
						row.getCell(8).setCellStyle(dataheader_style);
						row.getCell(9).setCellStyle(dataheader_style);
						break;
					case "자산유동화증권":
						row.createCell(0).setCellValue("종류");
						row.createCell(1).setCellValue("업체명");
						row.createCell(2).setCellValue("사채명");
						row.createCell(3).setCellValue("회차");
						row.createCell(4).setCellValue("발행액(억원)");
						row.createCell(5).setCellValue("발행일");
						row.createCell(6).setCellValue("만기일");
						row.createCell(7).setCellValue("등급");
						row.createCell(8).setCellValue("등급확정일");
						row.getCell(0).setCellStyle(dataheader_style);
						row.getCell(1).setCellStyle(dataheader_style);
						row.getCell(2).setCellStyle(dataheader_style);
						row.getCell(3).setCellStyle(dataheader_style);
						row.getCell(4).setCellStyle(dataheader_style);
						row.getCell(5).setCellStyle(dataheader_style);
						row.getCell(6).setCellStyle(dataheader_style);
						row.getCell(7).setCellStyle(dataheader_style);
						row.getCell(8).setCellStyle(dataheader_style);
						break;
					case "기업신용평가":
						row.createCell(0).setCellValue("종류");
						row.createCell(1).setCellValue("업체명");
						row.createCell(2).setCellValue("등급");
						row.createCell(3).setCellValue("Outlook");
						row.createCell(4).setCellValue("등급확정일");
						row.getCell(0).setCellStyle(dataheader_style);
						row.getCell(1).setCellStyle(dataheader_style);
						row.getCell(2).setCellStyle(dataheader_style);
						row.getCell(3).setCellStyle(dataheader_style);
						row.getCell(4).setCellStyle(dataheader_style);
						break;
					case "보험금지급능력평가":
						row.createCell(0).setCellValue("종류");
						row.createCell(1).setCellValue("업체명");
						row.createCell(2).setCellValue("등급");
						row.createCell(3).setCellValue("Outlook");
						row.createCell(4).setCellValue("등급확정일");
						row.getCell(0).setCellStyle(dataheader_style);
						row.getCell(1).setCellStyle(dataheader_style);
						row.getCell(2).setCellStyle(dataheader_style);
						row.getCell(3).setCellStyle(dataheader_style);
						row.getCell(4).setCellStyle(dataheader_style);
						break;
					case "커버드본드":
						row.createCell(0).setCellValue("종류");
						row.createCell(1).setCellValue("업체명");
						row.createCell(2).setCellValue("회차");
						row.createCell(3).setCellValue("발행액(억원)");
						row.createCell(4).setCellValue("발행일");
						row.createCell(5).setCellValue("만기일");
						row.createCell(6).setCellValue("등급");
						row.createCell(7).setCellValue("등급확정일");
						row.getCell(0).setCellStyle(dataheader_style);
						row.getCell(1).setCellStyle(dataheader_style);
						row.getCell(2).setCellStyle(dataheader_style);
						row.getCell(3).setCellStyle(dataheader_style);
						row.getCell(4).setCellStyle(dataheader_style);
						row.getCell(5).setCellStyle(dataheader_style);
						row.getCell(6).setCellStyle(dataheader_style);
						row.getCell(7).setCellStyle(dataheader_style);
						break;
				}
				col_num = row.getLastCellNum();

				// 데이터 → 엑셀로 출력
				if (resultList.size() > 0) {
					for (int table_num = 0; table_num < resultList.size(); table_num++) {
						rownum++;
						row = sheet.createRow(rownum);
						switch (secutyp[secutyp_num]) {
							case "기업어음":
								row.createCell(0).setCellValue((String) resultList.get(table_num).getCrTyp());
								row.createCell(1).setCellValue((String) resultList.get(table_num).getCmpNm());
								row.createCell(2).setCellValue((String) resultList.get(table_num).getRuleDate());
								row.createCell(3).setCellValue((String) resultList.get(table_num).getRank());
								row.createCell(4).setCellValue((String) resultList.get(table_num).getRankConfDate());
								row.getCell(0).setCellStyle(data_style);
								row.getCell(1).setCellStyle(data_style);
								row.getCell(2).setCellStyle(data_style);
								row.getCell(3).setCellStyle(data_style);
								row.getCell(4).setCellStyle(data_style);
								break;
							case "전자단기사채":
								row.createCell(0).setCellValue((String) resultList.get(table_num).getCrTyp());
								row.createCell(1).setCellValue((String) resultList.get(table_num).getCmpNm());
								row.createCell(2).setCellValue((String) resultList.get(table_num).getRuleDate());
								row.createCell(3).setCellValue((String) resultList.get(table_num).getIssueAmt());
								row.createCell(4).setCellValue((String) resultList.get(table_num).getRank());
								row.createCell(5).setCellValue((String) resultList.get(table_num).getRankConfDate());
								row.getCell(0).setCellStyle(data_style);
								row.getCell(1).setCellStyle(data_style);
								row.getCell(2).setCellStyle(data_style);
								row.getCell(3).setCellStyle(data_style);
								row.getCell(4).setCellStyle(data_style);
								row.getCell(5).setCellStyle(data_style);
								break;
							case "회사채":
								row.createCell(0).setCellValue((String) resultList.get(table_num).getCrTyp());
								row.createCell(1).setCellValue((String) resultList.get(table_num).getCmpNm());
								row.createCell(2).setCellValue((String) resultList.get(table_num).getBondTyp());
								row.createCell(3).setCellValue((String) resultList.get(table_num).getIssueNo());
								row.createCell(4).setCellValue((String) resultList.get(table_num).getIssueAmt());
								row.createCell(5).setCellValue((String) resultList.get(table_num).getIssueDate());
								row.createCell(6).setCellValue((String) resultList.get(table_num).getMatuDate());
								row.createCell(7).setCellValue((String) resultList.get(table_num).getRank());
								row.createCell(8).setCellValue((String) resultList.get(table_num).getOutlook());
								row.createCell(9).setCellValue((String) resultList.get(table_num).getRankConfDate());
								row.getCell(0).setCellStyle(data_style);
								row.getCell(1).setCellStyle(data_style);
								row.getCell(2).setCellStyle(data_style);
								row.getCell(3).setCellStyle(data_style);
								row.getCell(4).setCellStyle(data_style);
								row.getCell(5).setCellStyle(data_style);
								row.getCell(6).setCellStyle(data_style);
								row.getCell(7).setCellStyle(data_style);
								row.getCell(8).setCellStyle(data_style);
								row.getCell(9).setCellStyle(data_style);
								break;
							case "자산유동화증권":
								row.createCell(0).setCellValue((String) resultList.get(table_num).getCrTyp());
								row.createCell(1).setCellValue((String) resultList.get(table_num).getCmpNm());
								row.createCell(2).setCellValue((String) resultList.get(table_num).getBondTyp());
								row.createCell(3).setCellValue((String) resultList.get(table_num).getIssueNo());
								row.createCell(4).setCellValue((String) resultList.get(table_num).getIssueAmt());
								row.createCell(5).setCellValue((String) resultList.get(table_num).getIssueDate());
								row.createCell(6).setCellValue((String) resultList.get(table_num).getMatuDate());
								row.createCell(7).setCellValue((String) resultList.get(table_num).getRank());
								row.createCell(8).setCellValue((String) resultList.get(table_num).getRankConfDate());
								row.getCell(0).setCellStyle(data_style);
								row.getCell(1).setCellStyle(data_style);
								row.getCell(2).setCellStyle(data_style);
								row.getCell(3).setCellStyle(data_style);
								row.getCell(4).setCellStyle(data_style);
								row.getCell(5).setCellStyle(data_style);
								row.getCell(6).setCellStyle(data_style);
								row.getCell(7).setCellStyle(data_style);
								row.getCell(8).setCellStyle(data_style);
								break;
							case "기업신용평가":
								row.createCell(0).setCellValue((String) resultList.get(table_num).getCrTyp());
								row.createCell(1).setCellValue((String) resultList.get(table_num).getCmpNm());
								row.createCell(2).setCellValue((String) resultList.get(table_num).getRank());
								row.createCell(3).setCellValue((String) resultList.get(table_num).getOutlook());
								row.createCell(4).setCellValue((String) resultList.get(table_num).getRankConfDate());
								row.getCell(0).setCellStyle(data_style);
								row.getCell(1).setCellStyle(data_style);
								row.getCell(2).setCellStyle(data_style);
								row.getCell(3).setCellStyle(data_style);
								row.getCell(4).setCellStyle(data_style);
								break;
							case "보험금지급능력평가":
								row.createCell(0).setCellValue((String) resultList.get(table_num).getCrTyp());
								row.createCell(1).setCellValue((String) resultList.get(table_num).getCmpNm());
								row.createCell(2).setCellValue((String) resultList.get(table_num).getRank());
								row.createCell(3).setCellValue((String) resultList.get(table_num).getOutlook());
								row.createCell(4).setCellValue((String) resultList.get(table_num).getRankConfDate());
								row.getCell(0).setCellStyle(data_style);
								row.getCell(1).setCellStyle(data_style);
								row.getCell(2).setCellStyle(data_style);
								row.getCell(3).setCellStyle(data_style);
								row.getCell(4).setCellStyle(data_style);
								break;
							case "커버드본드":
								row.createCell(0).setCellValue((String) resultList.get(table_num).getCrTyp());
								row.createCell(1).setCellValue((String) resultList.get(table_num).getCmpNm());
								row.createCell(2).setCellValue((String) resultList.get(table_num).getIssueNo());
								row.createCell(3).setCellValue((String) resultList.get(table_num).getIssueAmt());
								row.createCell(4).setCellValue((String) resultList.get(table_num).getIssueDate());
								row.createCell(5).setCellValue((String) resultList.get(table_num).getMatuDate());
								row.createCell(6).setCellValue((String) resultList.get(table_num).getRank());
								row.createCell(7).setCellValue((String) resultList.get(table_num).getRankConfDate());
								row.getCell(0).setCellStyle(data_style);
								row.getCell(1).setCellStyle(data_style);
								row.getCell(2).setCellStyle(data_style);
								row.getCell(3).setCellStyle(data_style);
								row.getCell(4).setCellStyle(data_style);
								row.getCell(5).setCellStyle(data_style);
								row.getCell(6).setCellStyle(data_style);
								row.getCell(7).setCellStyle(data_style);
								break;
						}
					}
					rownum++;
					rownum++;
				} else {
					rownum++;
					row = sheet.createRow(rownum);
					for (int num = 0; num < col_num; num++) {
						row.createCell(num);
						row.getCell(num).setCellStyle(data_style);
					}
					sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, col_num - 1));
					row.getCell(0).setCellValue("해당사항 없음");
					rownum++;
					rownum++;
				}

				for (int num = 0; num < col_num; num++) {
					sheet.autoSizeColumn(num);
				}
			}
		}
		return workbook;
	}

	@Override
	public String checkEndTime(String type) {
		
		String endTimeFlag = null;
		
		HistoryDTO.Request hrequest = new HistoryDTO.Request();
		
		hrequest.setGubun(type);
		
		try {
			endTimeFlag = crawlerHistoryService.checkEndTime(hrequest);
			if(endTimeFlag == null) {
				endTimeFlag = "E";
			}
		} catch (Exception error) {
			System.err.println(error);
		}
		
		return endTimeFlag;
	}
}
