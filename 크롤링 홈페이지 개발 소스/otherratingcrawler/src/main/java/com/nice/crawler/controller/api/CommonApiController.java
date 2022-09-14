package com.nice.crawler.controller.api;

import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.nice.crawler.dto.CrawlerDTO;
import com.nice.crawler.gather.common.DateUtil;
import com.nice.crawler.service.CrawlerService;

/**
 * 
 * @FileName : CommonApiController.java
 * @Description : 공통 기능을 위한 Api Controller
 */
@RestController
@RequestMapping(value = "/api")
public class CommonApiController {
	
	private final CrawlerService crawlerService;
	
	public CommonApiController(CrawlerService crawlerService) {
		this.crawlerService = crawlerService;
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/api/excel
	 * @Description : 엑셀파일 생성을 위한 기능
	 */
	@RequestMapping("/excel")
	public void createOtherRatingExcelFile(@RequestParam(value = "startDate_search") Date startDateSearch, @RequestParam(value = "endDate_search") Date endDateSearch, HttpServletResponse response) throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String startDate = simpleDateFormat.format(startDateSearch);
		String endDate = simpleDateFormat.format(endDateSearch);
		
		CrawlerDTO.Request request = new CrawlerDTO.Request();
		request.setStartDate(startDate);
		request.setEndDate(endDate);
		request.setType("excel");

		if (!startDate.equals("") && !endDate.equals("")) {
			HSSFWorkbook workbook = crawlerService.createCrawlingExcelFile(request);
			String filename = URLEncoder.encode("주간 3사 평정자료", "UTF-8");
			response.setContentType("application/vnd.ms-excel; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename="+ filename + "_" + startDate + "~" + endDate + ".xls");
			workbook.write(response.getOutputStream());
		} else {
			System.out.println("엑셀 파일 저장 실패");
		}
	}
	
	/**
	 * 
	 * @HttpMethod : POST > http://localhost:9000/api/after/excel
	 * @Description : 엑셀파일 생성을 위한 기능
	 */
	@RequestMapping("/after/excel")
	public void createAfterOtherRatingExcelFile(@RequestParam(value = "startDate_search") Date startDateSearch, @RequestParam(value = "endDate_search") Date endDateSearch, HttpServletResponse response) throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String startDate = simpleDateFormat.format(startDateSearch);
		String endDate = simpleDateFormat.format(endDateSearch);
		
		CrawlerDTO.Request request = new CrawlerDTO.Request();
		request.setStartDate(startDate);
		request.setEndDate(endDate);
		request.setType("afterExcel");
	
		if (!startDate.equals("") && !endDate.equals("")) {
			HSSFWorkbook workbook = crawlerService.createCrawlingExcelFile(request);
			String filename = URLEncoder.encode("주간 3사 평정자료 수동처리", "UTF-8");
			response.setContentType("application/vnd.ms-excel; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename="+ filename + "_" + startDate + "~" + endDate + ".xls");
			workbook.write(response.getOutputStream());
		} else {
			System.out.println("엑셀 파일 저장 실패");
		}
	}
}
