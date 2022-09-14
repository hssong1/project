package com.nice.crawler.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nice.crawler.gather.common.Commons;
import com.nice.crawler.model.Crawler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CrawlerEnforceServiceImpl implements CrawlerEnforceService {
	@Value("${othercrawler.file.name}")
	private String directoryPath;

	@Override
	public List<Crawler> insertEvaluateData(Map<String, Object> param) throws Exception  {
		List<Crawler> crawlerList = new ArrayList<>();
		String[] crprvid = (String[]) param.get("crprvid");
		
		for(int seq = 0; seq < crprvid.length; seq++) {
			switch(crprvid[seq]) {
				case "NICE": {
					List<Crawler> crawlerListAboutNice = insertCrawlingNICEData(param);
					crawlerList.addAll(crawlerListAboutNice);
					break;		
				}
				case "KIS": {
					List<Crawler> crawlerListAboutKis = insertCrawlingKISData(param);
					crawlerList.addAll(crawlerListAboutKis);
					break;
				}
				case "KR": {
					List<Crawler> crawlerListAboutKr = insertCrawlingKRData(param);
					crawlerList.addAll(crawlerListAboutKr);
					break;
				}
				case "SCRI": {
					List<Crawler> crawlerListAboutScri = insertCrawlingSCRIData(param);
					crawlerList.addAll(crawlerListAboutScri);
					break;
				}
			}
		}
		
		return crawlerList;
	}
	
	/**
	 * @최근공시 : http://nicerating.co.kr/disclosure/dayRatingNews.do
	 * @상세주소 : http://nicerating.co.kr/company/companyGrade.do (회사채)
	 * @param : today, cmpCd, seriesNm, secuTyp, strDate, endDate
	 */
	public List<Crawler> insertCrawlingNICEData(Map param) throws Exception{
		
		log.info("==================NICE=================");
		List<Crawler> result = new ArrayList<Crawler>();

		//등급공시 리스트 확인
		Document document = null;
		for(int try_num=0; try_num<3; try_num++){
			try{
				document = Jsoup.connect("http://nicerating.co.kr/disclosure/dayRatingNews.do")
						.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
						.header("Accept-Encoding","gzip, deflate")
						.header("Accept-Language","ko-KR")
						.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
						.data("today", Commons.getNowDate())
						.data("cmpCd", "")
						.data("seriesNm", "")
						.data("secuTyp", "ALL")
						.data("strDate", (String)param.get("startDate"))
						.data("endDate", (String)param.get("endDate"))
						.maxBodySize(0)
						.timeout(600000)
						.get();
				break;
			}catch(Exception e){
				e.printStackTrace();
				Thread.sleep(3000);
			}
		}

		Elements body = document.select("body");

		// tbl1 : 회사채
		// tbl2 : 기업어음
		// tbl3 : 전자단기사채
		// tbl4 : 기업신용평가
		// tbl5 : 보험금지급능력평가
		// tbl6 : 자산유동화증권
		// tbl8 : 정부신용평가

		String[] secu_typ_list = {"1", "2", "3", "4", "5", "6", "8","9"};

		for(int secu_num=0; secu_num<=secu_typ_list.length-1; secu_num++){

			// 상세테이블 양식이 달라 바꾸어주어야함(맵핑)
			String return_type = "";
			switch(secu_typ_list[secu_num]){
				case "1" : return_type = "1"; break;
				case "2" : return_type = "2"; break;
				case "3" : return_type = "3"; break;
				case "4" : return_type = "4"; break;
				case "5" : return_type = "5"; break;
				case "6" : return_type = "2"; break;
				case "7" : return_type = "7"; break;
				case "9" : return_type = "9"; break;
			}

			Elements tr = body.select("#tbl" + secu_typ_list[secu_num] + " tbody tr");

			// tbl1 : 회사채
			// 회사채인 경우
			if(secu_typ_list[secu_num].equals("1")){
				List<Map> cmpList = new ArrayList<Map>();
				for(int num=0; num<tr.size(); num++){
					Map cmp = new HashMap();
					Elements td = tr.get(num).select("td");
					String a_href = td.get(0).select("a").attr("href");
					a_href = a_href.substring(a_href.indexOf(",") + 1);
					String CMP_CD = a_href.substring(0, a_href.indexOf(",")).replaceAll("'", "").trim();
					String CMP_NM = td.get(0).text();

					if(CMP_CD!=""){
						cmp.put(CMP_CD, CMP_NM);
						cmp.put("TYPE", secu_typ_list[secu_num]);
					}

					// thk값이 true이면 리스트 추가
					boolean chk = true;
					if(cmpList.size()>0){
						for(Map temp : cmpList){
							if(temp.get("cmpCd").equals(CMP_CD) && temp.get("type").equals(cmp.get("TYPE"))){
								chk = false;
								break;
							}
						}
					}

					// 기업 리스트화
					Set key = cmp.keySet();
					for (Iterator iterator = key.iterator(); iterator.hasNext();) {
						String keyName  = (String) iterator.next();
						if(!keyName.equals("TYPE")){
							String keyValue = (String) cmp.get(keyName);
							String keyType = (String) cmp.get("TYPE");
							Map obj = new HashMap();
							obj.put("cmpCd", keyName);
							obj.put("cmpNm", keyValue);
							obj.put("type", keyType);

							if(chk == true){
								cmpList.add(obj);
							}
						}
					}
				}

				//기업명을 토대로 상세 데이터 가져오기
				for(int num=0; num<cmpList.size(); num++){
					Document document_detail = null;
					for(int try_num=0; try_num<3; try_num++){
						try{
							document_detail  = Jsoup.connect("http://nicerating.co.kr/company/companyBondInfo.do")
									.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
									.header("Accept-Encoding","gzip, deflate")
									.header("Accept-Language","ko-KR")
									.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
									.data("cmpCd", (String)cmpList.get(num).get("cmpCd"))
									.maxBodySize(0)
									.timeout(600000)
									.get();
							break;
						}catch(Exception e){
							e.printStackTrace();
							Thread.sleep(3000);
						}
					}

					Elements body_detail = document_detail.select("body");
					Elements tr_d = body_detail.select("#tbl" + return_type + " tbody tr");

					for(int num_d=0; num_d<tr_d.size(); num_d++){
						try{
							Elements td = tr_d.get(num_d).select("td");
							if(td.size()>5){
								String CRPRVID      ="NICE";
								String RANK_CONF_DATE    =td.get(8).text().replaceAll("[.]", "").replaceAll("<", "").replaceAll("/", "");
								if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
									String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
									String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
									String CR_TYP       =td.get(2).text();
									String SECU_TYP     ="회사채";
									String BOND_TYP     =td.get(1).text();
									String RANK         =td.get(5).text().trim().replace("↑", "").replace("↓", "");
									String WATCH = "";
									if(td.get(5).text().indexOf("↑")>0){
										WATCH = td.get(5).text().substring(td.get(5).text().indexOf("↑")).trim();
									}else if(td.get(5).text().indexOf("↓")>0){
										WATCH = td.get(5).text().substring(td.get(5).text().indexOf("↓")).trim();
									}else{
										WATCH = "";
									}
									String OUTLOOK      =td.get(6).text();
									String ISSUE_NO     =td.get(0).text();
									String ISSUE_AMT    =td.get(11).text();
									String ISSUE_AMT_TYP= "";
									if(Commons.getNumberChk(td.get(11).text())){
										ISSUE_AMT_TYP= "억원";
									}
									String ISSUE_DATE   =td.get(9).text().replaceAll("[.]", "");
									String MATU_DATE    =td.get(10).text().replaceAll("[.]", "");
									String RULE_DATE    ="";
									String SERIES		= "";
									String EXPOSE_TYP	= "";
									String REL_CMP_NM	= "";

									// String to Map
									Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
									result.add(crawler);
								}
							}
						}catch(Exception e){
							String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
							String CMP_NM       =(String)cmpList.get(num).get("cmpNm");

							log.info("ERROR : CMP_CD - " + CMP_CD + ", CMP_NM - "+ CMP_NM + ", TYPE - " + secu_typ_list[secu_num]);
							log.info(tr_d.get(num_d).html());

							e.printStackTrace();
						}
					}
					Thread.sleep((int)(1000));
				}
			}else{

				// tbl2 : 기업어음
				// tbl3 : 전자단기사채
				// tbl4 : 기업신용평가
				// tbl5 : 보험금지급능력평가
				// tbl6 : 자산유동화증권
				// tbl8 : 정부신용평가
				// tbl9 : 커버드본드
				switch(secu_typ_list[secu_num]){

					case "2" : for(int num_d=0; num_d<tr.size(); num_d++){
						try{
							Elements td = tr.get(num_d).select("td");
							if(td.size()>5){
								String CRPRVID      ="NICE";
								String a_href = td.get(0).select("a").attr("href");
								a_href = a_href.substring(a_href.indexOf(",") + 1);
								String CMP_CD = a_href.substring(0, a_href.indexOf(",")).replaceAll("'", "").trim();
								String CMP_NM = td.get(0).text();
								String RANK_CONF_DATE    =td.get(5).text().replaceAll("[.]", "");
								String MATU_DATE    = "";
								String RULE_DATE    = "";
								if(!td.get(7).text().equals("")){
									MATU_DATE = td.get(7).text().replaceAll("[.]", "");
								}
								if(!td.get(6).text().equals("")){
									RULE_DATE = td.get(6).text().replaceAll("[.]", "");
								}

								if(RANK_CONF_DATE!="" && (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
									String CR_TYP       =td.get(1).text();
									String SECU_TYP     ="기업어음";
									String BOND_TYP     ="";
									String RANK         =td.get(3).text().trim().replace("↑", "").replace("↓", "");
									String WATCH = "";
									if(td.get(3).text().indexOf("↑")>0){
										WATCH = td.get(3).text().substring(td.get(3).text().indexOf("↑")).trim();
									}else if(td.get(3).text().indexOf("↓")>0){
										WATCH = td.get(3).text().substring(td.get(3).text().indexOf("↓")).trim();
									}else{
										WATCH = "";
									}
									String OUTLOOK      ="";
									String ISSUE_NO     ="";
									String ISSUE_AMT    ="";
									String ISSUE_AMT_TYP="";
									String ISSUE_DATE   ="";
									String SERIES		= "";
									String EXPOSE_TYP	= "";
									String REL_CMP_NM	= "";

									// String to Map
									Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
									result.add(crawler);
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}

						Double rand = Math.random();
						Thread.sleep((int)(rand*500));
					}
						break;
					case "3" : for(int num_d=0; num_d<tr.size(); num_d++){
						try{
							Elements td = tr.get(num_d).select("td");
							if(td.size()>5){
								String CRPRVID      ="NICE";
								String RANK_CONF_DATE    =td.get(5).text().replaceAll("[.]", "");
								String a_href = td.get(0).select("a").attr("href");
								a_href = a_href.substring(a_href.indexOf(",") + 1);
								String CMP_CD = a_href.substring(0, a_href.indexOf(",")).replaceAll("'", "").trim();
								String CMP_NM = td.get(0).text();
								if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
									String CR_TYP       =td.get(1).text();
									String SECU_TYP     ="전자단기사채";
									String BOND_TYP     ="";
									String RANK         =td.get(3).text().trim().replace("↑", "").replace("↓", "");
									String WATCH = "";
									if(td.get(3).text().indexOf("↑")>0){
										WATCH = td.get(3).text().substring(td.get(3).text().indexOf("↑")).trim();
									}else if(td.get(3).text().indexOf("↓")>0){
										WATCH = td.get(3).text().substring(td.get(3).text().indexOf("↓")).trim();
									}else{
										WATCH = "";
									}
									String OUTLOOK      ="";
									String ISSUE_NO     ="";
									String ISSUE_AMT    =td.get(7).text();
									String ISSUE_AMT_TYP= "";
									if(Commons.getNumberChk(td.get(7).text())){
										ISSUE_AMT_TYP= "억원";
									}
									String ISSUE_DATE   ="";
									String MATU_DATE    =td.get(8).text().replaceAll("[.]", "");
									String RULE_DATE    =td.get(6).text().replaceAll("[.]", "");
									String SERIES		= "";
									String EXPOSE_TYP	= "";
									String REL_CMP_NM	= "";

									// String to Map
									Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
									result.add(crawler);
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}

						Double rand = Math.random();
						Thread.sleep((int)(rand*500));
					}
						break;
					case "4" : for(int num_d=0; num_d<tr.size(); num_d++){
						try{
							Elements td = tr.get(num_d).select("td");
							if(td.size()>5){
								String CRPRVID      ="NICE";
								String RANK_CONF_DATE    =td.get(7).text().replaceAll("[.]", "");
								String a_href = td.get(0).select("a").attr("href");
								a_href = a_href.substring(a_href.indexOf(",") + 1);
								String CMP_CD = a_href.substring(0, a_href.indexOf(",")).replaceAll("'", "").trim();
								String CMP_NM = td.get(0).text();
								if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
									String CR_TYP       =td.get(1).text();
									String SECU_TYP     ="기업신용평가";
									String BOND_TYP     ="";
									String RANK         =td.get(4).text().trim().replace("↑", "").replace("↓", "");
									String WATCH = "";
									if(td.get(4).text().indexOf("↑")>0){
										WATCH = td.get(4).text().substring(td.get(4).text().indexOf("↑")).trim();
									}else if(td.get(4).text().indexOf("↓")>0){
										WATCH = td.get(4).text().substring(td.get(4).text().indexOf("↓")).trim();
									}else{
										WATCH = "";
									}
									String OUTLOOK      =td.get(5).text();
									String ISSUE_NO     ="";
									String ISSUE_AMT    ="";
									String ISSUE_AMT_TYP="";
									String ISSUE_DATE   ="";
									String MATU_DATE    ="";
									String RULE_DATE    ="";
									String SERIES		="";
									String EXPOSE_TYP	="";
									String REL_CMP_NM	="";

									// String to Map
									Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
									result.add(crawler);
								}

								Double rand = Math.random();
								Thread.sleep((int)(rand*500));
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
						break;
					case "5" : for(int num_d=0; num_d<tr.size(); num_d++){
						try{
							Elements td = tr.get(num_d).select("td");
							if(td.size()>5){
								String CRPRVID      ="NICE";
								String RANK_CONF_DATE    =td.get(7).text().replaceAll("[.]", "");
								String a_href = td.get(0).select("a").attr("href");
								a_href = a_href.substring(a_href.indexOf(",") + 1);
								String CMP_CD = a_href.substring(0, a_href.indexOf(",")).replaceAll("'", "").trim();
								String CMP_NM = td.get(0).text();
								if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
									String CR_TYP       =td.get(1).text();
									String SECU_TYP     ="보험금지급능력평가";
									String BOND_TYP     ="";
									String RANK         =td.get(4).text().trim().replace("↑", "").replace("↓", "");
									String WATCH = "";
									if(td.get(4).text().indexOf("↑")>0){
										WATCH = td.get(4).text().substring(td.get(4).text().indexOf("↑")).trim();
									}else if(td.get(4).text().indexOf("↓")>0){
										WATCH = td.get(4).text().substring(td.get(4).text().indexOf("↓")).trim();
									}else{
										WATCH = "";
									}
									String OUTLOOK      =td.get(5).text();
									String ISSUE_NO     ="";
									String ISSUE_AMT    ="";
									String ISSUE_AMT_TYP="";
									String ISSUE_DATE   ="";
									String MATU_DATE    =td.get(8).text().replaceAll("[.]", "");
									String RULE_DATE    ="";
									String SERIES		= "";
									String EXPOSE_TYP	= "";
									String REL_CMP_NM	= "";

									// String to Map
									Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
									result.add(crawler);
								}

								Double rand = Math.random();
								Thread.sleep((int)(rand*500));
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
						break;
					case "6" : List<Map> cmpList = new ArrayList<Map>();
						for(int num=0; num<tr.size(); num++){
							Map cmp = new HashMap();
							Elements td = tr.get(num).select("td");
							String a_href = td.get(0).select("a").attr("href");
							a_href = a_href.substring(a_href.indexOf(",") + 1);
							String CMP_CD = a_href.substring(0, a_href.indexOf(",")).replaceAll("'", "").trim();
							String CMP_NM = td.get(0).text();

							if(CMP_CD!=""){
								cmp.put(CMP_CD, CMP_NM);
								cmp.put("TYPE", secu_typ_list[secu_num]);
							}

							// thk값이 true이면 리스트 추가
							boolean chk = true;
							if(cmpList.size()>0){
								for(Map temp : cmpList){
									if(temp.get("cmpCd").equals(CMP_CD) && temp.get("type").equals(cmp.get("TYPE"))){
										chk = false;
										break;
									}
								}
							}

							// 기업 리스트화
							Set key = cmp.keySet();
							for (Iterator iterator = key.iterator(); iterator.hasNext();) {
								String keyName  = (String) iterator.next();
								if(!keyName.equals("TYPE")){
									String keyValue = (String) cmp.get(keyName);
									String keyType = (String) cmp.get("TYPE");
									Map obj = new HashMap();
									obj.put("cmpCd", keyName);
									obj.put("cmpNm", keyValue);
									obj.put("type", keyType);

									if(chk == true){
										cmpList.add(obj);
									}
								}
							}
						}

						//기업명을 토대로 상세 데이터 가져오기
						for(int num=0; num<cmpList.size(); num++){
							log.info("cmpCd : " + (String)cmpList.get(num).get("cmpCd") + ", cmpNm : " + (String)cmpList.get(num).get("cmpNm") + ", type : " + (String)cmpList.get(num).get("type"));

							Document document_detail = null;
							for(int try_num=0; try_num<3; try_num++){
								try{
									document_detail = Jsoup.connect("http://nicerating.co.kr/disclosure/securitizationCompanyGrade.do")
											.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
											.header("Accept-Encoding","gzip, deflate")
											.header("Accept-Language","ko-KR")
											.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
											.data("today", Commons.getNowDate())
											.data("cmpCd", (String)cmpList.get(num).get("cmpCd"))
											.data("seriesNm", "")
											.data("secuTyp", "")
											.data("strDate", Commons.getChangeDateString((String)param.get("startDate"), "-"))
											.data("endDate", Commons.getChangeDateString((String)param.get("endDate"), "-"))
											.maxBodySize(0)
											.timeout(600000)
											.get();
									break;
								}catch(Exception e){
									e.printStackTrace();
									Thread.sleep(3000);
								}
							}

							Elements body_detail = document_detail.select("body");
							Elements tr_d = body_detail.select("#tbl" + return_type + " tbody tr");

							for(int num_d=0; num_d<tr_d.size(); num_d++){
								try{
									Elements td = tr_d.get(num_d).select("td");
									if(td.size()>5){
										String CRPRVID      ="NICE";
										String RANK_CONF_DATE    =td.get(6).text().replaceAll("[.]", "");
										String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
										String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
										if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
											String CR_TYP       =td.get(2).text();
											String SECU_TYP     ="자산유동화증권";
											String BOND_TYP     =td.get(1).text();
											String RANK         =td.get(4).text().trim().replace("↑", "").replace("↓", "");
											String WATCH = "";
											if(td.get(4).text().indexOf("↑")>0){
												WATCH = td.get(4).text().substring(td.get(4).text().indexOf("↑")).trim();
											}else if(td.get(4).text().indexOf("↓")>0){
												WATCH = td.get(4).text().substring(td.get(4).text().indexOf("↓")).trim();
											}else{
												WATCH = "";
											}
											String OUTLOOK      ="";
											String ISSUE_NO     =td.get(0).text();
											String ISSUE_AMT    =td.get(9).text();
											String ISSUE_AMT_TYP= "";
											if(Commons.getNumberChk(td.get(9).text())){
												ISSUE_AMT_TYP= "억원";
											}
											String ISSUE_DATE   =td.get(7).text().replaceAll("[.]", "");
											String MATU_DATE    =td.get(8).text().replaceAll("[.]", "");
											String RULE_DATE    ="";
											String SERIES		= "";
											String EXPOSE_TYP	= "";
											String REL_CMP_NM	= "";

											// String to Map
											Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
											result.add(crawler);
										}
									}
								}catch(Exception e){
									e.printStackTrace();
								}
							}
							Thread.sleep((int)(1000));
						}
						break;
					case "9" : for(int num_d=0; num_d<tr.size(); num_d++){
						try{
							Elements td = tr.get(num_d).select("td");
							if(td.size()>5){
								String CRPRVID      ="NICE";
								String RANK_CONF_DATE    =td.get(7).text().replaceAll("[.]", "");
								String a_href = td.get(0).select("a").attr("href");
								a_href = a_href.substring(a_href.indexOf(",") + 1);
								String CMP_CD = a_href.substring(0, a_href.indexOf(",")).replaceAll("'", "").trim();
								String CMP_NM = td.get(0).text();
								if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
									String CR_TYP       =td.get(3).text();
									String SECU_TYP     ="커버드본드";
									String BOND_TYP     =td.get(2).text();
									String RANK         =td.get(5).text().trim().replace("↑", "").replace("↓", "");
									String WATCH = "";
									if(td.get(5).text().indexOf("↑")>0){
										WATCH = "↑";
									}else if(td.get(5).text().indexOf("↓")>0){
										WATCH = "↓";
									}else{
										WATCH = "";
									}
									String OUTLOOK      ="";
									String ISSUE_NO     =td.get(1).text();
									String ISSUE_AMT    =td.get(10).text();
									String ISSUE_AMT_TYP="";
									if(Commons.getNumberChk(td.get(10).text())){
										ISSUE_AMT_TYP= "억원";
									}
									String ISSUE_DATE   =td.get(8).text().replaceAll("[.]", "");
									String MATU_DATE    =td.get(9).text().replaceAll("[.]", "");
									String RULE_DATE    ="";
									String SERIES		= "";
									String EXPOSE_TYP	= "";
									String REL_CMP_NM	= "";

									// String to Map
									Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
									result.add(crawler);
								}
								Double rand = Math.random();
								Thread.sleep((int)(rand*500));
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					break;
				}
			}
		}

		log.info("=================NICE END================");
		return result;
	}
	
	/**
	 * @최근공시 : http://kisrating.co.kr/ratings/hot_disclosure.do
	 * @상세주소 : http://kisrating.co.kr/ratingsSearch/corp_overview.do
	 * @param : tabType, searchYn, startDt, endDt
	 */
	public List<Crawler> insertCrawlingKISData(Map param) throws Exception{

		log.info("==================KIS=================");
		List<Crawler> result = new ArrayList<>();

		Document document = null;
		for(int try_num=0; try_num<3; try_num++){
			try{
				document = Jsoup.connect("https://www.kisrating.com/ratings/hot_disclosure.do")
						.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
						.header("Accept-Encoding","gzip, deflate")
						.header("Accept-Language","ko-KR")
						.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
						.data("tabType", "0")
						.data("searchYn", "Y")
						.data("startDt", (String)param.get("startDate"))
						.data("endDt", (String)param.get("endDate"))
						.maxBodySize(0)
						.timeout(600000)
						.post();
				break;
			}catch(Exception e){
				e.printStackTrace();
				Thread.sleep(3000);
			}
		}
		
		String directory = "kisSnapShot";
		String fileStartDate = (String)param.get("startDate");
		String fileEndDate = (String)param.get("endDate");
		
		Timestamp s = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hh:mm:ss");
		
		// 공시된 건 파일 저장
		String fileName1 = fileStartDate + "~" + fileEndDate + "_" + sdf.format(s) + "_공시.html"; 
		String path1 = directoryPath + File.separator + directory;
		File filePath1 = new File(path1);
		
		if(!filePath1.exists()) {
			filePath1.mkdir();
		}
		
		File fileSave1 = new File(filePath1, fileName1);
		log.info(fileSave1.getAbsolutePath());
		
		BufferedWriter bufWriter1 = new BufferedWriter(new FileWriter(fileSave1));
		bufWriter1.write(document.getAllElements().toString());
		bufWriter1.close();

		Elements body = document.select("body");

		// 취소건 데이터
		Document document_cancel = null;
		for(int try_num=0; try_num<3; try_num++){
			try{
				document_cancel = Jsoup.connect("https://www.kisrating.com/ratings/hot_calloff.do")
						.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
						.header("Accept-Encoding","gzip, deflate")
						.header("Accept-Language","ko-KR")
						.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
						.timeout(600000)
						.post();
				break;
			}catch(Exception e){
				e.printStackTrace();
				Thread.sleep(3000);
			}
		}
		
		// 소멸된 건 파일 저장	
		String fileName2 = fileStartDate + "~" + fileEndDate + "_" + sdf.format(s) + "_소멸.html"; 
		String path2 = directoryPath + File.separator + directory;
		File filePath2 = new File(path2);
		
		if(!filePath2.exists()) {
			filePath2.mkdir();
		}
		
		File fileSave2 = new File(filePath2, fileName2);
		log.info(fileSave2.getAbsolutePath());
		
		BufferedWriter bufWriter2 = new BufferedWriter(new FileWriter(fileSave2));
		bufWriter2.write(document_cancel.getAllElements().toString());
		bufWriter2.close();

		Elements body_cancel = document_cancel.select("body");

		// view1 : 회사채
		// view2 : 기업어음
		// view3 : 전단채
		// view4 : 기업신용평가(Issuer Rating)
		// view5 : 보험금지급능력평가
		// view6 : 자산유동화증권
		// view7 : 유동화익스포져
		// view8 : 커버드본드
		String[] secu_typ_list = {"1", "2", "3", "4", "5", "6", "7", "8"};
		
		for(int secu_num=0; secu_num<=secu_typ_list.length-1; secu_num++){
			//유효 데이터 찾기
			Elements tr = body.select("#view" + secu_typ_list[secu_num] + " table tbody tr");
			//취소 데이터 찾기
			Elements tr_cancel = body_cancel.select("#view" + secu_typ_list[secu_num] + " table tbody tr");

			// 유효등급_기업
			List<Map> cmpList = new ArrayList<Map>();
			for(int num=0; num<tr.size(); num++){
				try{
					Map cmp = new HashMap();
					Elements td = tr.get(num).select("td");
					String CMP_CD = td.get(0).select("input").attr("kiscd");
					String CMP_NM = td.get(1).text();

					if(CMP_CD!=""){
						cmp.put(CMP_CD, CMP_NM);
						cmp.put("TYPE", secu_typ_list[secu_num]);
					}

					// thk값이 true이면 리스트 추가
					boolean chk = true;
					if(cmpList.size()>0){
						for(Map temp : cmpList){
							if(temp.get("cmpCd").equals(CMP_CD) && temp.get("type").equals(cmp.get("TYPE"))){
								chk = false;
								break;
							}
						}
					}

					// 기업 리스트화
					Set key = cmp.keySet();
					for (Iterator iterator = key.iterator(); iterator.hasNext();) {
						String keyName  = (String) iterator.next();
						if(!keyName.equals("TYPE")){
							String keyValue = (String) cmp.get(keyName);
							String keyType = (String) cmp.get("TYPE");
							Map obj = new HashMap();
							obj.put("cmpCd", keyName);
							obj.put("cmpNm", keyValue);
							obj.put("type", keyType);

							if(chk == true){
								cmpList.add(obj);
							}
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					log.info("ERROR: " + e.toString()+ ", TR_NUM" + tr.get(num).toString());
				}
			}

			//취소등급_기업
			for(int num=0; num<tr_cancel.size(); num++){
				try{
					Map cmp = new HashMap();
					Elements td = tr_cancel.get(num).select("td");
					if(td.size()<=1){
						continue;
					}
					String CMP_CD = td.get(0).select("input").attr("kiscd");
					String CMP_NM = td.get(1).text();

					if(CMP_CD!=""){
						cmp.put(CMP_CD, CMP_NM);
						cmp.put("TYPE", secu_typ_list[secu_num]);
					}

					// thk값이 true이면 리스트 추가
					boolean chk = true;
					if(cmpList.size()>0){
						for(Map temp : cmpList){
							if(temp.get("cmpCd").equals(CMP_CD) && temp.get("type").equals(cmp.get("TYPE"))){
								chk = false;
								break;
							}
						}
					}

					// 기업 리스트화
					Set key = cmp.keySet();
					for (Iterator iterator = key.iterator(); iterator.hasNext();) {
						String keyName  = (String) iterator.next();
						if(!keyName.equals("TYPE")){
							String keyValue = (String) cmp.get(keyName);
							String keyType = (String) cmp.get("TYPE");
							Map obj = new HashMap();
							obj.put("cmpCd", keyName);
							obj.put("cmpNm", keyValue);
							obj.put("type", keyType);

							if(chk == true){
								cmpList.add(obj);
							}
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					log.info("ERROR: " + e.toString()+ ", TR_NUM" + tr_cancel.get(num).toString());
				}
			}

			//기업명을 토대로 상세 데이터 가져오기
			for(int num=0; num<cmpList.size(); num++){
				Document document_detail = null;
				for(int try_num=0; try_num<3; try_num++){
					try{
						document_detail = Jsoup.connect("https://www.kisrating.com/ratingsSearch/search_corpTab1.json")
								.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
								.header("Accept-Encoding","gzip, deflate")
								.header("Accept-Language","ko-KR")
								.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
								.data("kiscd", (String)cmpList.get(num).get("cmpCd"))
								.data("spc", "")
								.data("searchName", (String)cmpList.get(num).get("cmpNm"))
								.data("searchYn", "Y")
								.data("radioType", "3")
								.data("startDt", "")
								.maxBodySize(0)
								.timeout(600000)
								.post();
						break;
					}catch(Exception e){
						e.printStackTrace();
						Thread.sleep(3000);
					}
				}

				Elements body_detail = document_detail.select("body");

				/* 1 : 회사채			 -> 1
				 * 2 : 기업어음 		 -> 3
				 * 3 : 전자단기사채		 -> 4
				 * 4 : 기업신용평가 		 -> 5
				 * 5 : 보험금지급능력평가	 -> 2
				 * 6 : 자산유동화증권	 -> 6
				 * 7 : 유동화익스포져	 -> 7
				 * 8 : 커버드본드	 -> 8
				 */

				String type = (String)cmpList.get(num).get("type");

				// 상세테이블 양식이 달라 바꾸어주어야함(맵핑)
				String return_type = "";
				switch(type){
					case "1" : return_type = "1"; break;
					case "2" : return_type = "3"; break;
					case "3" : return_type = "4"; break;
					case "4" : return_type = "5"; break;
					case "5" : return_type = "2"; break;
					case "6" : return_type = "6"; break;
					case "7" : return_type = "7"; break;
					case "8" : return_type = "8"; break;
				}

				Elements tr_d = body_detail.select("#tb" + return_type + " tbody tr");
				switch(type){
					case "1" : // 회사채
						for(int num_d=0; num_d<tr_d.size(); num_d++){
							try{
								Elements td = tr_d.get(num_d).select("td");
								if(td.size()>5){
									String CRPRVID      ="KIS";
									String RANK_CONF_DATE    =td.get(8).text().replaceAll("[.]", "");
									if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
										String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
										String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
										String CR_TYP       =td.get(5).text();
										String SECU_TYP     ="회사채";
										String BOND_TYP     =td.get(0).text();
										String RANK         =td.get(6).text().trim().replace("↑", "").replace("↓", "");
										String WATCH = "";
										if(td.get(6).text().indexOf("↑")>0){
											WATCH = td.get(6).text().substring(td.get(6).text().indexOf("↑")).trim();
										}else if(td.get(6).text().indexOf("↓")>0){
											WATCH = td.get(6).text().substring(td.get(6).text().indexOf("↓")).trim();
										}else{
											WATCH = "";
										}
										String OUTLOOK      =td.get(7).text();
										String ISSUE_NO     =td.get(1).text();
										String ISSUE_AMT    =td.get(2).text();
										String ISSUE_AMT_TYP= "";
										if(Commons.getNumberChk(td.get(2).text())){
											ISSUE_AMT_TYP= "억원";
										}
										String ISSUE_DATE   =td.get(3).text().replaceAll("[.]", "");
										String MATU_DATE    =td.get(4).text().replaceAll("[.]", "");
										String RULE_DATE    ="";
										String SERIES		= "";
										String EXPOSE_TYP	= "";
										String REL_CMP_NM	= "";

										// String to Map
										Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
										result.add(crawler);
									}
								}
							}catch(Exception e){
								e.printStackTrace();
								log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D" + tr_d.get(num_d));
							}
						}
						break;
					case "2" : // 기업어음
						for(int num_d=0; num_d<tr_d.size(); num_d++){
							try{
								Elements td = tr_d.get(num_d).select("td");
								if(td.size()>4){
									String CRPRVID      ="KIS";
									String RANK_CONF_DATE    =td.get(3).text().replaceAll("[.]", "");
									String MATU_DATE    = "";
									String RULE_DATE    = "";
									if(!td.get(4).text().equals("")){
										MATU_DATE = td.get(4).text().replaceAll("[.]", "");
									}
									if(!td.get(0).text().equals("")){
										RULE_DATE = td.get(0).text().replaceAll("[.]", "");
									}

									if(RANK_CONF_DATE!="" && (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
										String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
										String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
										String CR_TYP       =td.get(1).text();
										String SECU_TYP     ="기업어음";
										String BOND_TYP     ="";
										String RANK         =td.get(2).text().trim().replace("↑", "").replace("↓", "");
										String WATCH = "";
										if(td.get(2).text().indexOf("↑")>0){
											WATCH = td.get(2).text().substring(td.get(2).text().indexOf("↑"));
										}else if(td.get(2).text().indexOf("↓")>0){
											WATCH = td.get(2).text().substring(td.get(2).text().indexOf("↓"));
										}else{
											WATCH = "";
										}
										String OUTLOOK      ="";
										String ISSUE_NO     ="";
										String ISSUE_AMT    ="";
										String ISSUE_AMT_TYP="";
										String ISSUE_DATE   ="";
										String SERIES		= "";
										String EXPOSE_TYP	= "";
										String REL_CMP_NM	= "";

										// String to Map
										Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
										result.add(crawler);
									}
								}
							}catch(Exception e){
								e.printStackTrace();
								log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : "  + e.toString() + "TR_D" +tr_d.get(num_d));
							}
						}
						break;
					case "3" : // 전자단기사채
						for(int num_d=0; num_d<tr_d.size(); num_d++){
							try{
								Elements td = tr_d.get(num_d).select("td");
								if(td.size()>4){
									String CRPRVID      ="KIS";
									String RANK_CONF_DATE    =td.get(4).text().replaceAll("[.]", "");
									if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
										String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
										String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
										String CR_TYP       =td.get(2).text();
										String SECU_TYP     ="전자단기사채";
										String BOND_TYP     ="";
										String RANK         =td.get(3).text().trim().replace("↑", "").replace("↓", "");
										String WATCH = "";
										if(td.get(3).text().indexOf("↑")>0){
											WATCH = td.get(3).text().substring(td.get(3).text().indexOf("↑")).trim();
										}else if(td.get(3).text().indexOf("↓")>0){
											WATCH = td.get(3).text().substring(td.get(3).text().indexOf("↓")).trim();
										}else{
											WATCH = "";
										}
										String OUTLOOK      ="";
										String ISSUE_NO     ="";
										String ISSUE_AMT    =td.get(1).text();
										String ISSUE_AMT_TYP= "";
										if(Commons.getNumberChk(td.get(1).text())){
											ISSUE_AMT_TYP= "억원";
										}
										String ISSUE_DATE   ="";
										String MATU_DATE    =td.get(5).text().replaceAll("[.]", "");
										String RULE_DATE    =td.get(0).text().replaceAll("[.]", "");
										String SERIES		= "";
										String EXPOSE_TYP	= "";
										String REL_CMP_NM	= "";

										// String to Map
										Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
										result.add(crawler);
									}
								}
							}catch(Exception e){
								e.printStackTrace();
								log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D" +tr_d.get(num_d));
							}
						}
						break;
					case "4" : // 기업신용평가(Issuer Rating)
						for(int num_d=0; num_d<tr_d.size(); num_d++){
							try{
								Elements td = tr_d.get(num_d).select("td");
								if(td.size()>4){
									String CRPRVID      ="KIS";
									String RANK_CONF_DATE    =td.get(5).text().replaceAll("[.]", "");

									if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
										String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
										String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
										String CR_TYP       =td.get(1).text();
										String SECU_TYP     ="기업신용평가";
										String BOND_TYP     ="";
										String RANK         =td.get(2).text().trim().replace("↑", "").replace("↓", "");
										String WATCH = "";
										if(td.get(2).text().indexOf("↑")>0){
											WATCH = td.get(2).text().substring(td.get(2).text().indexOf("↑")).trim();
										}else if(td.get(2).text().indexOf("↓")>0){
											WATCH = td.get(2).text().substring(td.get(2).text().indexOf("↓")).trim();
										}else{
											WATCH = "";
										}
										String OUTLOOK      =td.get(3).text();
										String ISSUE_NO     ="";
										String ISSUE_AMT    ="";
										String ISSUE_AMT_TYP="";
										String ISSUE_DATE   ="";
										String MATU_DATE    ="";
										String RULE_DATE    ="";
										String SERIES		= "";
										String EXPOSE_TYP	= "";
										String REL_CMP_NM	= "";

										// String to Map
										Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
										result.add(crawler);
									}
								}
							}catch(Exception e){
								e.printStackTrace();
								log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D"  +tr_d.get(num_d));
							}
						}
						break;
					case "5" : // 보험금지급능력평가
						for(int num_d=0; num_d<tr_d.size(); num_d++){
							try{
								Elements td = tr_d.get(num_d).select("td");
								if(td.size()>4){
									String CRPRVID      ="KIS";
									String RANK_CONF_DATE    =td.get(5).text().replaceAll("[.]", "");
									if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
										String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
										String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
										String CR_TYP       =td.get(1).text();
										String SECU_TYP     ="보험금지급능력평가";
										String BOND_TYP     ="";
										String RANK         =td.get(2).text().trim().replace("↑", "").replace("↓", "");
										String WATCH = "";
										if(td.get(2).text().indexOf("↑")>0){
											WATCH = td.get(2).text().substring(td.get(2).text().indexOf("↑")).trim();
										}else if(td.get(2).text().indexOf("↓")>0){
											WATCH = td.get(2).text().substring(td.get(2).text().indexOf("↓")).trim();
										}else{
											WATCH = "";
										}
										String OUTLOOK      =td.get(3).text();
										String ISSUE_NO     ="";
										String ISSUE_AMT    ="";
										String ISSUE_AMT_TYP="";
										String ISSUE_DATE   ="";
										String MATU_DATE    =td.get(6).text().replaceAll("[.]", "");
										String RULE_DATE    =td.get(0).text().replaceAll("[.]", "");
										String SERIES		= "";
										String EXPOSE_TYP	= "";
										String REL_CMP_NM	= "";

										// String to Map
										Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
										result.add(crawler);
									}
								}
							}catch(Exception e){
								e.printStackTrace();
								log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D"  +tr_d.get(num_d));
							}
						}
						break;
					case "6" : // 자산유동화증권
						for(int num_d=0; num_d<tr_d.size(); num_d++){
							try{
								Elements td = tr_d.get(num_d).select("td");
								if(td.size()>4){
									String CRPRVID      ="KIS";
									String RANK_CONF_DATE    =td.get(7).text().replaceAll("[.]", "");
									if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
										String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
										String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
										String CR_TYP       =td.get(5).text();
										String SECU_TYP     ="자산유동화증권";
										String BOND_TYP     =td.get(0).text();
										String RANK         =td.get(6).text().trim().replace("↑", "").replace("↓", "");
										String WATCH = "";
										if(td.get(6).text().indexOf("↑")>0){
											WATCH = td.get(6).text().substring(td.get(6).text().indexOf("↑")).trim();
										}else if(td.get(6).text().indexOf("↓")>0){
											WATCH = td.get(6).text().substring(td.get(6).text().indexOf("↓")).trim();
										}else{
											WATCH = "";
										}
										String OUTLOOK      ="";
										String ISSUE_NO     =td.get(1).text();
										String ISSUE_AMT    =td.get(2).text();
										String ISSUE_AMT_TYP= "";
										if(Commons.getNumberChk(td.get(2).text())){
											ISSUE_AMT_TYP= "억원";
										}
										String ISSUE_DATE   =td.get(3).text().replaceAll("[.]", "");
										String MATU_DATE    =td.get(4).text().replaceAll("[.]", "");
										String RULE_DATE    ="";
										String SERIES		= "";
										String EXPOSE_TYP	= "";
										String REL_CMP_NM	= "";

										// String to Map
										Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
										result.add(crawler);
									}
								}
							}catch(Exception e){
								e.printStackTrace();
								log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D"  +tr_d.get(num_d));
							}
						}
						break;
					case "7" : // 유동화익스포져
						for(int num_d=0; num_d<tr_d.size(); num_d++){
							try{
								Elements td = tr_d.get(num_d).select("td");
								if(td.size()>4){
									String CRPRVID      ="KIS";
									String RANK_CONF_DATE    =td.get(7).text().replaceAll("[.]", "");
									if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
										String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
										String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
										String CR_TYP       =td.get(5).text();
										String SECU_TYP     ="유동화익스포져";
										String BOND_TYP     ="";
										String RANK         =td.get(6).text().trim().replace("↑", "").replace("↓", "");
										String WATCH = "";
										if(td.get(6).text().indexOf("↑")>0){
											WATCH = td.get(6).text().substring(td.get(6).text().indexOf("↑")).trim();
										}else if(td.get(6).text().indexOf("↓")>0){
											WATCH = td.get(6).text().substring(td.get(6).text().indexOf("↓")).trim();
										}else{
											WATCH = "";
										}
										String OUTLOOK      ="";
										String ISSUE_NO     ="";
										String ISSUE_AMT    =td.get(3).text();
										String ISSUE_AMT_TYP= "";
										if(Commons.getNumberChk(td.get(3).text())){
											ISSUE_AMT_TYP= "억원";
										}
										String ISSUE_DATE   ="";
										String MATU_DATE    =td.get(4).text().replaceAll("[.]", "");
										String RULE_DATE    ="";
										String SERIES		=td.get(0).text();
										String EXPOSE_TYP	=td.get(1).text();
										String REL_CMP_NM	=td.get(2).text();

										// String to Map
										Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
										result.add(crawler);
									}
								}
							}catch(Exception e){
								e.printStackTrace();
								log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D"  +tr_d.get(num_d));
							}
						}
						break;
					case "8" : // 커버드본드
						for(int num_d=0; num_d<tr_d.size(); num_d++){
							try{
								Elements td = tr_d.get(num_d).select("td");
								if(td.size()>4){
									String CRPRVID      ="KIS";
									String RANK_CONF_DATE    =td.get(7).text().replaceAll("[.]", "");
									if(RANK_CONF_DATE=="" || (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
										String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
										String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
										String CR_TYP       =td.get(5).text();
										String SECU_TYP     ="커버드본드";
										String BOND_TYP     ="Covered Bond";
										String RANK         =td.get(6).text().trim().replace("↑", "").replace("↓", "");
										String WATCH = "";
										if(td.get(6).text().indexOf("↑")>0){
											WATCH = td.get(6).text().substring(td.get(6).text().indexOf("↑")).trim();
										}else if(td.get(6).text().indexOf("↓")>0){
											WATCH = td.get(6).text().substring(td.get(6).text().indexOf("↓")).trim();
										}else{
											WATCH = "";
										}
										String OUTLOOK      ="";
										String ISSUE_NO     =td.get(0).text() + " "+ td.get(1).text();
										String ISSUE_AMT    =td.get(2).text();
										String ISSUE_AMT_TYP= "";
										if(Commons.getNumberChk(td.get(2).text())){
											ISSUE_AMT_TYP= "억원";
										}
										String ISSUE_DATE   =td.get(3).text().replaceAll("[.]", "");
										String MATU_DATE    =td.get(4).text().replaceAll("[.]", "");
										String RULE_DATE    ="";
										String SERIES		= "";
										String EXPOSE_TYP	= "";
										String REL_CMP_NM	= "";

										// String to Map
										Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
										result.add(crawler);
									}
								}
							}catch(Exception e){
								e.printStackTrace();
								log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D"  +tr_d.get(num_d));
							}
						}
						break;
				}
				Double rand = Math.random();
				Thread.sleep((int)(rand*500));
			}
		}
		log.info("=================KIS END================");
		return result;
	}

	// KR데이터 크롤링
	//
	/* 모수선택
	 * 최근공시 : http://rating.co.kr/disclosure/QDisclosure002.do
	 * parameter :
		companyCode
		evalNm
		svctyNm
		evalDt
		svcty
	 *
	 * 상세정보
	 * parameter : http://rating.co.kr/disclosure/QDisclosure004.do > http://rating.co.kr/disclosure/QDisclosure005.do(2차상세페이지)
	 	svctyCd
		pageIndex
		menuParam1
		menuParam2
		menuParam3
		svctyNm
		companyCode
		fromDt
		toDt
	 */
	public List<Crawler> insertCrawlingKRData(Map param) throws Exception{

		log.info("==================KR=================");
		log.info("param : " + param);

		List<Crawler> result = new ArrayList<>();

		String startDate = ((String)param.get("startDate"));
		String endDate = (String)param.get("endDate");

		// KR은 조회 조건이 공시일 기준임.
		// 이전에 평가한 데이터도 있을 수 있기 때문에 무조건 한달치를 추출하여 그중 등급확정일을 구해야함 .
		String evalDt = "1";

		if(startDate.equals(Commons.getNowDate().replaceAll("-", ""))){
			evalDt = "0";
		}else if(Integer.parseInt(Commons.getNowDate().replaceAll("-", ""))-Integer.parseInt(startDate)<7){
			evalDt = "1";
		}else if(Integer.parseInt(Commons.getNowDate().replaceAll("-", ""))-Integer.parseInt(startDate) >= 7 && Integer.parseInt(Commons.getNowDate().replaceAll("-", ""))-Integer.parseInt(startDate)<14){
			evalDt = "2";
		}else if(Integer.parseInt(Commons.getNowDate().replaceAll("-", ""))-Integer.parseInt(startDate) >= 14){
			evalDt = "3";
		} 

		// tb32 : 회사채 			-> 1
		// tb31 : 기업어음			-> 3
		// tb3A : 전단채			-> 4
		// tb33 : 자산유동화증권		-> 5
		// tb38 : 프로젝트 파이낸스	-> 6(사용안함)
		// tb39 : 파생결합증권		-> 7
		// tb3B : 펀드			-> 	(사용안함)
		// tb36 : 유동화익스포져		-> 9
		// tb34 : 기업신용평가		-> 10
		// tb35 : 보험금지급능력평가	-> 11
		// tb3C : 커버드본드                      -> 16
		String[] secu_typ_list = {"32", "31", "3A", "33", "34", "35", "36", "39", "3C"};

		// 유효등급 건 조회
		Document document = null;
		for(int try_num=0; try_num<3; try_num++){
			try{
				document = Jsoup.connect("http://rating.co.kr/disclosure/QDisclosure002.do")
						.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
						.header("Accept-Encoding","gzip, deflate")
						.header("Accept-Language","ko-KR")
						.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
						.data("companyCode", "")
						.data("evalNm", "")
						.data("svctyNm", "")
						.data("evalDt", evalDt)
						.data("svcty", "00")
						.maxBodySize(0)
						.timeout(600000)
						.get();
				break;
			}catch(Exception e){
				e.printStackTrace();
				Thread.sleep(3000);
			}
		}

		Elements body = document.select("body");

		for(int secu_num=0; secu_num<=secu_typ_list.length-1; secu_num++){
			Elements tr = body.select("#tb"+secu_typ_list[secu_num] + " tbody tr");

			// QDisclosure002만 봐도 되는 채권유형인 경우(기업신용평가)
			if(secu_typ_list[secu_num].equals("34")){

				for(int num_d=0; num_d<tr.size(); num_d++){
					try{
						// 기업신용평가(ICR)
						Elements td = tr.get(num_d).select("td");
						if(td.size()>4){
							String CRPRVID   = "KR";
							String PUB_ANUC_DATE = Commons.checkNullOfString(td.get(7).text().replaceAll("[.]", ""));

							// 공시일 기준 조회영역에 해당하는 데이터 값만 추출
							if(!PUB_ANUC_DATE.equals("") && (Integer.parseInt(PUB_ANUC_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE)<=Integer.parseInt((String)param.get("endDate")))){
								String a_href = td.get(0).select("a").attr("href");
								String CMP_CD = "";
								if(a_href!=null&&a_href!=""&&a_href.indexOf("(")>0){
									CMP_CD	= a_href.substring(a_href.indexOf("(")+1, a_href.indexOf(")")).replaceAll("'","");
								}
								String CMP_NM = td.get(0).text();
								String CR_TYP       =td.get(1).text();
								String SECU_TYP     ="기업신용평가";
								String BOND_TYP     ="";
								String RANK         =td.get(4).text().trim().replace("↑", "").replace("↓", "");
								String WATCH = "";
								if(td.get(4).text().indexOf("↑")>0){
									WATCH = td.get(4).text().substring(td.get(4).text().indexOf("↑")).trim();
								}else if(td.get(4).text().indexOf("↓")>0){
									WATCH = td.get(4).text().substring(td.get(4).text().indexOf("↓")).trim();
								}else{
									WATCH = "";
								}
								String OUTLOOK      =td.get(5).text();
								String ISSUE_NO     ="";
								String ISSUE_AMT    ="";
								String ISSUE_AMT_TYP= "";
								String ISSUE_DATE   ="";
								String MATU_DATE    ="";
								String RULE_DATE    ="";
								String SERIES		="";
								String EXPOSE_TYP	="";
								String REL_CMP_NM	="";
								String RANK_CONF_DATE = td.get(6).text().replaceAll("[.]", "");

								// String to Map
								Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, PUB_ANUC_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
								result.add(crawler);
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}

			// 세부페이지까지 봐야하는 경우
			else{
				List<Map> cmpList = new ArrayList<Map>();
				for(int num_d=0; num_d<tr.size(); num_d++){
					Map cmp = new HashMap();
					Elements td = tr.get(num_d).select("td");
					String a_href = td.get(0).select("a").attr("href");
					String CMP_CD = "";
					if(a_href!=null&&a_href!=""&&a_href.indexOf("(")>0){
						CMP_CD	= a_href.substring(a_href.indexOf("(")+1, a_href.indexOf(")")).replaceAll("'","");
					}
					String CMP_NM = td.get(0).text();

					if(CMP_CD!=""){
						cmp.put(CMP_CD, CMP_NM);
						cmp.put("TYPE", secu_typ_list[secu_num]);
					}

					// thk값이 true이면 리스트 추가
					boolean chk = true;
					String PUB_ANUC_DATE = "";

					if(td.size()>1){

						switch(secu_typ_list[secu_num]){
							/* 원본
							case "32" : RATE_DATE = td.get(7).text().replaceAll("[.]", ""); break;
							case "31" : RATE_DATE = td.get(5).text().replaceAll("[.]", ""); break;
							case "3A" :	RATE_DATE = td.get(7).text().replaceAll("[.]", ""); break;
							case "33" : RATE_DATE = td.get(7).text().replaceAll("[.]", ""); break;
							case "34" :	RATE_DATE = td.get(6).text().replaceAll("[.]", ""); break;
							case "35" : RATE_DATE = td.get(6).text().replaceAll("[.]", ""); break;
							case "36" :	RATE_DATE = td.get(8).text().replaceAll("[.]", ""); break;
							case "39" : RATE_DATE = td.get(7).text().replaceAll("[.]", ""); break;
							case "3C" : RATE_DATE = td.get(7).text().replaceAll("[.]", ""); break;
							*/
							
							case "32" : PUB_ANUC_DATE = Commons.checkNullOfString(td.get(8).text().replaceAll("[.]", "")); break;
							case "31" : PUB_ANUC_DATE = Commons.checkNullOfString(td.get(6).text().replaceAll("[.]", "")); break;
							case "3A" :	PUB_ANUC_DATE = Commons.checkNullOfString(td.get(8).text().replaceAll("[.]", "")); break;
							case "33" : PUB_ANUC_DATE = Commons.checkNullOfString(td.get(8).text().replaceAll("[.]", "")); break;
							case "34" :	PUB_ANUC_DATE = Commons.checkNullOfString(td.get(7).text().replaceAll("[.]", "")); break;
							case "35" : PUB_ANUC_DATE = Commons.checkNullOfString(td.get(7).text().replaceAll("[.]", "")); break;
							case "36" :	PUB_ANUC_DATE = Commons.checkNullOfString(td.get(9).text().replaceAll("[.]", "")); break;
							case "39" : PUB_ANUC_DATE = Commons.checkNullOfString(td.get(8).text().replaceAll("[.]", "")); break;
							case "3C" : PUB_ANUC_DATE = Commons.checkNullOfString(td.get(8).text().replaceAll("[.]", "")); break;
						}


						if(!(Integer.parseInt(PUB_ANUC_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE)<=Integer.parseInt((String)param.get("endDate")))){
							chk = false;
						}

						if(cmpList.size()>0){
							for(Map temp : cmpList){
								if(temp.get("cmpCd").equals(CMP_CD) && temp.get("type").equals(secu_typ_list[secu_num])){
									chk = false;
								}
							}
						}

						// 공시한 기업에 대한 상세 리스트 확인
						Set key = cmp.keySet();
						for (Iterator iterator = key.iterator(); iterator.hasNext();) {
							String keyName  = (String) iterator.next();
							if(!keyName.equals("TYPE")){
								String keyValue = (String) cmp.get(keyName);
								String keyType = (String) cmp.get("TYPE");
								Map obj = new HashMap();
								obj.put("cmpCd", keyName);
								obj.put("cmpNm", keyValue);
								obj.put("type", keyType);
								if(chk == true){
									cmpList.add(obj);
								}
							}
						}
					}
				}

				//기업명을 토대로 상세 데이터 가져오기
				for(int num=0; num<cmpList.size(); num++){

					log.info("cmpCd : " + (String)cmpList.get(num).get("cmpCd") + ", cmpNm : " + (String)cmpList.get(num).get("cmpNm") + ", type : " + (String)cmpList.get(num).get("type"));

					Document document_detail = null;
					for(int try_num=0; try_num<3; try_num++){
						try{
							document_detail = Jsoup.connect("http://rating.co.kr/disclosure/QDisclosure004.do")
									.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
									.header("Accept-Encoding","gzip, deflate")
									.header("Accept-Language","ko-KR")
									.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
									.data("svctyCd", secu_typ_list[secu_num])
									.data("pageIndex", "")
									.data("menuParam1", "2")
									.data("menuParam2", "1")
									.data("menuParam3", "4")
									.data("svctyNm", "")
									.data("companyCode", (String)cmpList.get(num).get("cmpCd"))
									.data("fromDt", Commons.getChangeDateString(startDate, "-"))
									.data("toDt", Commons.getChangeDateString(endDate, "-"))
									.maxBodySize(0)
									.timeout(600000)
									.get();
							break;
						}catch(Exception e){
							e.printStackTrace();
							Thread.sleep(3000);
						}
					}

					Elements body_detail = document_detail.select("body");

					// return_type = sortTable[Number]
					// sortTable1 : 회사채
					// sortTable2 : X(없음)
					// sortTable3 : 기업어음
					// sortTable4 : 전자단기사채
					// sortTable5 : 자산유동화증권(ABS)
					// sortTable6 : 프로젝트파이낸스
					// sortTable7 : 파생결합증권
					// sortTable9 : 유동화익스포져(CFR)
					// sortTable10 : 기업신용평가(ICR)
					// sortTable11 : 보험금지급능력평가(IFSR)
					// sortTable16 : 커버드본드
					// 상세테이블 양식이 달라 바꾸어주어야함(맵핑)
					String type = secu_typ_list[secu_num];
					String return_type = "";
					String mapping_type = "";
					switch(type){
						case "32" : return_type = "1"; break;
						case "31" : return_type = "3"; break;
						case "3A" : return_type = "4"; break;
						case "33" : return_type = "5"; break;
						//case "34" : return_type = "10"; type = "38"; break;
						case "35" : return_type = "11"; mapping_type = "39"; break;
						case "36" : return_type = "9"; break;
						case "39" : return_type = "7"; break;
						case "3C" : return_type = "16"; break;
					}

					Elements tr_d = body_detail.select("#sortTable" + return_type +" tbody tr");

					switch(type){
						case "32" : // 회사채

							Document document_cancel = Jsoup.connect("http://www.rating.co.kr/disclosure/QDisclosure006.do")
									.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
									.header("Accept-Encoding","gzip, deflate")
									.header("Accept-Language","ko-KR")
									.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
									.data("svctyCd", "")
									.data("srvno", "")
									.data("trancheSer", "")
									.data("companyCode", (String)cmpList.get(num).get("cmpCd"))
									.data("tabFlag", "2")
									.maxBodySize(0)
									.timeout(600000)
									.get();

							Elements body_cancel = document_cancel.select("body");
							Elements tr_cancel = body_cancel.select("#sortTable" + return_type +" tbody tr");
							for(int num_cancel=0; num_cancel<tr_cancel.size(); num_cancel++){
								try{
									// 회사채(취소)
									Elements td_cancel = tr_cancel.get(num_cancel).select("td");

									String CRPRVID      ="KR";
									String PUB_ANUC_DATE    =Commons.checkNullOfString(td_cancel.get(7).text().replaceAll("[.]", ""));
									
									// 공시일 기준 조회영역에 해당하는 데이터 값만 추출
									if(!PUB_ANUC_DATE.equals("") && (Integer.parseInt(PUB_ANUC_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE)<=Integer.parseInt((String)param.get("endDate")))){
										String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
										String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
										String CR_TYP       =td_cancel.get(5).text();
										String SECU_TYP     ="회사채";
										String BOND_TYP     =td_cancel.get(1).text();
										String RANK         =td_cancel.get(8).text().trim().replace("↑", "").replace("↓", "");
										String WATCH = "";
										if(td_cancel.get(8).text().indexOf("↑")>0){
											WATCH = td_cancel.get(8).text().substring(td_cancel.get(8).text().indexOf("↑")).trim();
										}else if(td_cancel.get(8).text().indexOf("↓")>0){
											WATCH = td_cancel.get(8).text().substring(td_cancel.get(8).text().indexOf("↓")).trim();
										}else{
											WATCH = "";
										}
										String OUTLOOK      =td_cancel.get(9).text();
										String ISSUE_NO     =td_cancel.get(0).text();
										String ISSUE_AMT    =td_cancel.get(2).text();
										String ISSUE_AMT_TYP= "";
										if(Commons.getNumberChk(td_cancel.get(2).text())){
											ISSUE_AMT_TYP= "억원";
										}
										String ISSUE_DATE   =td_cancel.get(3).text().replaceAll("[.]", "");
										String MATU_DATE    =td_cancel.get(4).text().replaceAll("[.]", "");
										String RULE_DATE    ="";
										String SERIES		= "";
										String EXPOSE_TYP	= "";
										String REL_CMP_NM	= "";
										String RANK_CONF_DATE = td_cancel.get(6).text().replaceAll("[.]", "");

										// String to Map
										Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, PUB_ANUC_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
										result.add(crawler);
									}
								}catch(Exception e){
									e.printStackTrace();
									log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_CANCEL"  +tr_cancel.get(num_cancel));
								}
							}

							for(int num_d=0; num_d<tr_d.size(); num_d++){
								try{
									// 회사채
									Elements td = tr_d.get(num_d).select("td");
									if(td.size()>4){

										String CRPRVID      ="KR";
										String PUB_ANUC_DATE =Commons.checkNullOfString(td.get(7).text().replaceAll("[.]", ""));

										// 등급확정일 기준 조회영역에 해당하는 데이터 값만 추출
										if(!PUB_ANUC_DATE.equals("") && (Integer.parseInt(PUB_ANUC_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE)<=Integer.parseInt((String)param.get("endDate")))){
											String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
											String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
											String CR_TYP       =td.get(5).text();
											String SECU_TYP     ="회사채";
											String BOND_TYP     =td.get(1).text();
											String RANK         =td.get(8).text().trim().replace("↑", "").replace("↓", "");
											String WATCH = "";
											if(td.get(8).text().indexOf("↑")>0){
												WATCH = td.get(8).text().substring(td.get(8).text().indexOf("↑")).trim();
											}else if(td.get(8).text().indexOf("↓")>0){
												WATCH = td.get(8).text().substring(td.get(8).text().indexOf("↓")).trim();
											}else{
												WATCH = "";
											}
											String OUTLOOK      =td.get(9).text();
											String ISSUE_NO     =td.get(0).text();
											String ISSUE_AMT    =td.get(2).text();
											String ISSUE_AMT_TYP= "";
											if(Commons.getNumberChk(td.get(2).text())){
												ISSUE_AMT_TYP= "억원";
											}
											String ISSUE_DATE   =td.get(3).text().replaceAll("[.]", "");
											String MATU_DATE    =td.get(4).text().replaceAll("[.]", "");
											String RULE_DATE    ="";
											String SERIES		= "";
											String EXPOSE_TYP	= "";
											String REL_CMP_NM	= "";
											String RANK_CONF_DATE = td.get(6).text().replaceAll("[.]", "");

											// String to Map
											Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, PUB_ANUC_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
											result.add(crawler);
										}
									}
								}catch(Exception e){
									e.printStackTrace();
									log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D" +tr_d.get(num_d));
								}
							}
							break;
						case "31" : // 기업어음
							for(int num_d=0; num_d<tr_d.size(); num_d++){
								try{
									Elements td = tr_d.get(num_d).select("td");
									if(td.size()>4){
										String PUB_ANUC_DATE_1    =Commons.checkNullOfString(td.get(2).text().replaceAll("[.]", ""));
										String PUB_ANUC_DATE_2    =Commons.checkNullOfString(td.get(5).text().replaceAll("[.]", ""));
										String PUB_ANUC_DATE_3    =Commons.checkNullOfString(td.get(8).text().replaceAll("[.]", ""));
										String a_href = td.get(0).select("a").attr("href");
										String srvno		  = "";
										if(a_href.indexOf("(")>0){
											a_href 	= a_href.substring(a_href.indexOf(",")+1);
											srvno	= a_href.substring(0, a_href.indexOf(",")).replaceAll("'", "").trim();
										}

										boolean chk = false;
										if(PUB_ANUC_DATE_1!=""){
											if((Integer.parseInt(PUB_ANUC_DATE_1)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE_1)<=Integer.parseInt((String)param.get("endDate")))){
												chk=true;
											}
										}
										if(!PUB_ANUC_DATE_2.trim().equals("") && chk==false){
											if((Integer.parseInt(PUB_ANUC_DATE_2)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE_2)<=Integer.parseInt((String)param.get("endDate")))){
												chk=true;
											}
										}
										if(!PUB_ANUC_DATE_3.trim().equals("") && chk==false){
											if((Integer.parseInt(PUB_ANUC_DATE_3)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE_3)<=Integer.parseInt((String)param.get("endDate")))){
												chk=true;
											}
										}


										if(chk==true){
											//기업어음의 경우 2차 상세페이지 존재
											/* http://rating.co.kr/disclosure/QDisclosure005.do
											 * PARAMETER :
											 * svctyCd
												srvno
												trancheSer
												companyCode
												tabFlag
											*/

											Document document_detail_2 = null;
											for(int try_num=0; try_num<3; try_num++){
												try{
													document_detail_2 = Jsoup.connect("http://rating.co.kr/disclosure/QDisclosure005.do")
															.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
															.header("Accept-Encoding","gzip, deflate")
															.header("Accept-Language","ko-KR")
															.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
															.data("svctyCd", type)
															.data("srvno", srvno)
															.data("trancheSer", "1")
															.data("companyCode", (String)cmpList.get(num).get("cmpCd"))
															.data("tabFlag", "")
															.maxBodySize(0)
															.timeout(600000)
															.get();
													break;
												}catch(Exception e){
													e.printStackTrace();
													Thread.sleep(3000);
												}
											}

											Elements tr_d_2 = document_detail_2.select("#sortTable1 tbody tr");

											for(int num_d_2=0; num_d_2<tr_d_2.size(); num_d_2++){

												Elements td_2 = tr_d_2.get(num_d_2).select("td");
												String CRPRVID      ="KR";
												String PUB_ANUC_DATE    =Commons.checkNullOfString(td_2.get(3).text().replaceAll("[.]", ""));
												String MATU_DATE    = "";
												String RULE_DATE    = "";
												if(!td_2.get(5).text().equals("")){
													MATU_DATE = td_2.get(5).text().replaceAll("[.]", "");
												}
												if(!td_2.get(0).text().equals("")){
													RULE_DATE = td_2.get(0).text().replaceAll("[.]", "");
												}

												if(!PUB_ANUC_DATE.equals("") && (Integer.parseInt(PUB_ANUC_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE)<=Integer.parseInt((String)param.get("endDate")))){
													String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
													String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
													String CR_TYP       =td_2.get(1).text();
													String SECU_TYP     ="기업어음";
													String BOND_TYP     ="";
													String RANK         =td_2.get(4).text().trim().replace("↑", "").replace("↓", "");
													String WATCH = "";
													if(td_2.get(4).text().indexOf("↑")>0){
														WATCH = td_2.get(4).text().substring(td_2.get(4).text().indexOf("↑")).trim();
													}else if(td_2.get(4).text().indexOf("↓")>0){
														WATCH = td_2.get(4).text().substring(td_2.get(4).text().indexOf("↓")).trim();
													}else{
														WATCH = "";
													}
													String OUTLOOK      ="";
													String ISSUE_NO     ="";
													String ISSUE_AMT    ="";
													String ISSUE_AMT_TYP="";
													String ISSUE_DATE   ="";
													String SERIES		= "";
													String EXPOSE_TYP	= "";
													String REL_CMP_NM	= "";
													String RANK_CONF_DATE = td_2.get(2).text().replaceAll("[.]", "");

													// String to Map
													Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, PUB_ANUC_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
													result.add(crawler);
												}
											}
										}
									}
								}catch(Exception e){
									e.printStackTrace();
									log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D" +tr_d.get(num_d));
								}
								Double rand = Math.random();
								Thread.sleep((int)(rand*500));
							}
							break;
						case "3A" : // 전단채
							for(int num_d=0; num_d<tr_d.size(); num_d++){
								try{
									Elements td = tr_d.get(num_d).select("td");
									if(td.size()>4){
										String PUB_ANUC_DATE_1    =Commons.checkNullOfString(td.get(4).text().replaceAll("[.]", ""));
										String PUB_ANUC_DATE_2    =Commons.checkNullOfString(td.get(7).text().replaceAll("[.]", ""));
										String PUB_ANUC_DATE_3    =Commons.checkNullOfString(td.get(10).text().replaceAll("[.]", ""));
										String a_href = td.get(0).select("a").attr("href");
										String srvno		  = "";
										if(a_href.indexOf("(")>0){
											a_href 	= a_href.substring(a_href.indexOf(",")+1);
											srvno	= a_href.substring(0, a_href.indexOf(",")).replaceAll("'", "").trim();
										}

										boolean chk = false;
										if(PUB_ANUC_DATE_1!=""){
											if((Integer.parseInt(PUB_ANUC_DATE_1)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE_1)<=Integer.parseInt((String)param.get("endDate")))){
												chk=true;
											}
										}
										if(!PUB_ANUC_DATE_2.trim().equals("") && chk==false){
											if((Integer.parseInt(PUB_ANUC_DATE_2)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE_2)<=Integer.parseInt((String)param.get("endDate")))){
												chk=true;
											}
										}
										if(!PUB_ANUC_DATE_3.trim().equals("") && chk==false){
											if((Integer.parseInt(PUB_ANUC_DATE_3)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE_3)<=Integer.parseInt((String)param.get("endDate")))){
												chk=true;
											}
										}


										if(chk==true){
											//전단채의 경우 2차 상세페이지 존재
											/* http://rating.co.kr/disclosure/QDisclosure005.do
											 * PARAMETER :
											 * svctyCd
												srvno
												trancheSer
												companyCode
												tabFlag
											*/

											Document document_detail_2 = null;
											for(int try_num=0; try_num<3; try_num++){
												try{
													document_detail_2 = Jsoup.connect("http://rating.co.kr/disclosure/QDisclosure005.do")
															.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
															.header("Accept-Encoding","gzip, deflate")
															.header("Accept-Language","ko-KR")
															.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
															.data("svctyCd", type)
															.data("srvno", srvno)
															.data("trancheSer", "1")
															.data("companyCode", (String)cmpList.get(num).get("cmpCd"))
															.data("tabFlag", "")
															.maxBodySize(0)
															.timeout(600000)
															.get();
													break;
												}catch(Exception e){
													e.printStackTrace();
													Thread.sleep(3000);
												}
											}

											Elements tr_d_2 = document_detail_2.select("#sortTable1 tbody tr");

											for(int num_d_2=0; num_d_2<tr_d_2.size(); num_d_2++){

												Elements td_2 = tr_d_2.get(num_d_2).select("td");
												String CRPRVID      ="KR";
												String PUB_ANUC_DATE    =Commons.checkNullOfString(td_2.get(5).text().replaceAll("[.]", ""));
												String MATU_DATE    = "";
												String RULE_DATE    = "";
												if(!td_2.get(7).text().equals("")){
													MATU_DATE = td_2.get(7).text().replaceAll("[.]", "");
												}
												if(!td_2.get(2).text().equals("")){
													RULE_DATE = td_2.get(2).text().replaceAll("[.]", "");
												}

												if(!PUB_ANUC_DATE.equals("") && (Integer.parseInt(PUB_ANUC_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE)<=Integer.parseInt((String)param.get("endDate")))){
													String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
													String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
													String CR_TYP       =td_2.get(3).text();
													String SECU_TYP     ="전자단기사채";
													String BOND_TYP     ="";
													String RANK         =td_2.get(6).text().trim().replace("↑", "").replace("↓", "");
													String WATCH = "";
													if(td_2.get(6).text().indexOf("↑")>0){
														WATCH = td_2.get(6).text().substring(td_2.get(6).text().indexOf("↑")).trim();
													}else if(td_2.get(6).text().indexOf("↓")>0){
														WATCH = td_2.get(6).text().substring(td_2.get(6).text().indexOf("↓")).trim();
													}else{
														WATCH = "";
													}
													String OUTLOOK      ="";
													String ISSUE_NO     ="";
													String ISSUE_AMT    =td_2.get(1).text();
													String ISSUE_AMT_TYP= "";
													if(Commons.getNumberChk(td_2.get(1).text())){
														ISSUE_AMT_TYP= "억원";
													}
													String ISSUE_DATE   ="";
													String SERIES		= "";
													String EXPOSE_TYP	= "";
													String REL_CMP_NM	= "";
													String RANK_CONF_DATE = td_2.get(4).text().replaceAll("[.]", "");

													// String to Map
													Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, PUB_ANUC_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
													result.add(crawler);
												}
											}
										}
									}
								}catch(Exception e){
									e.printStackTrace();
									log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D" +tr_d.get(num_d));
								}
								Double rand = Math.random();
								Thread.sleep((int)(rand*500));
							}
							break;
						case "33" : // 자산유동화증권(ABS)
							for(int num_d=0; num_d<tr_d.size(); num_d++){
								try{

									//자산유동화의 경우 2차 상세페이지 존재
									/* http://rating.co.kr/disclosure/QDisclosure005.do
									 * PARAMETER :
									 * svctyCd
										srvno
										trancheSer
										companyCode
										tabFlag
									*/

									Elements td = tr_d.get(num_d).select("td");
									String PUB_ANUC_DATE_1    =Commons.checkNullOfString(td.get(7).text().replaceAll("[.]", ""));

									if(PUB_ANUC_DATE_1!=""){
										if((Integer.parseInt(PUB_ANUC_DATE_1)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE_1)<=Integer.parseInt((String)param.get("endDate")))){

											String a_href = td.get(0).select("a").attr("href");
											String srvno		  = "";
											String trancheSer		  = "";
											if(a_href.indexOf("(")>0){
												a_href 	= a_href.substring(a_href.indexOf(",")+1);
												srvno	= a_href.substring(0, a_href.indexOf(",")).replaceAll("'", "").trim();
											}

											a_href = td.get(0).select("a").attr("href");
											if(a_href.indexOf("(")>0){
												a_href 	= a_href.substring(a_href.lastIndexOf(",")+1);
												trancheSer	= a_href.substring(0, a_href.indexOf(")")).replaceAll("'", "").trim();
											}

											Document document_detail_2 = null;
											for(int try_num=0; try_num<3; try_num++){
												try{
													document_detail_2 = Jsoup.connect("http://rating.co.kr/disclosure/QDisclosure005.do")
															.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
															.header("Accept-Encoding","gzip, deflate")
															.header("Accept-Language","ko-KR")
															.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
															.data("svctyCd", type)
															.data("srvno", srvno)
															.data("trancheSer", trancheSer)
															.data("companyCode", (String)cmpList.get(num).get("cmpCd"))
															.data("tabFlag", "")
															.maxBodySize(0)
															.timeout(600000)
															.get();
													break;
												}catch(Exception e){
													e.printStackTrace();
													Thread.sleep(3000);
												}
											}

											Elements tr_d_2 = document_detail_2.select("#sortTable1 tbody tr");

											for(int num_d_2=0; num_d_2<tr_d_2.size(); num_d_2++){

												Elements td_2 = tr_d_2.get(num_d_2).select("td");
												String CRPRVID      ="KR";
												String PUB_ANUC_DATE    =Commons.checkNullOfString(td_2.get(7).text().replaceAll("[.]", ""));

												if(!PUB_ANUC_DATE.equals("") && (Integer.parseInt(PUB_ANUC_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE)<=Integer.parseInt((String)param.get("endDate")))){
													String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
													String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
													String CR_TYP       =td_2.get(5).text();
													String SECU_TYP     ="자산유동화증권";
													String BOND_TYP     =td_2.get(1).text();
													String RANK         =td_2.get(8).text().trim().replace("↑", "").replace("↓", "");
													String WATCH = "";
													if(td_2.get(8).text().indexOf("↑")>0){
														WATCH = td_2.get(8).text().substring(td_2.get(8).text().indexOf("↑")).trim();
													}else if(td_2.get(8).text().indexOf("↓")>0){
														WATCH = td_2.get(8).text().substring(td_2.get(8).text().indexOf("↓")).trim();
													}else{
														WATCH = "";
													}
													String OUTLOOK      ="";
													String ISSUE_NO     =td_2.get(0).text();
													String ISSUE_AMT    =td_2.get(2).text();
													String ISSUE_AMT_TYP= "";
													if(Commons.getNumberChk(td_2.get(2).text())){
														ISSUE_AMT_TYP= "억원";
													}
													String ISSUE_DATE   =td_2.get(3).text().replaceAll("[.]", "");
													String MATU_DATE    =td_2.get(4).text().replaceAll("[.]", "");
													String RULE_DATE    ="";
													String SERIES		= "";
													String EXPOSE_TYP	= "";
													String REL_CMP_NM	= "";
													String RANK_CONF_DATE = td_2.get(6).text().replaceAll("[.]", "");
													

													// String to Map
													Crawler crawler= convertMap(CRPRVID, RANK_CONF_DATE, PUB_ANUC_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
													result.add(crawler);
												}
											}
										}
									}
								}catch(Exception e){
									e.printStackTrace();
									log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D"  +tr_d.get(num_d));
								}
							}
							break;
						case "35" : // 보험금지급능력평가(IFRS)
							for(int num_d=0; num_d<tr_d.size(); num_d++){
								try{
									Elements td = tr_d.get(num_d).select("td");
									if(td.size()>4){
										String PUB_ANUC_DATE_1 = Commons.checkNullOfString(td.get(2).text().replaceAll("[.]", ""));
										String PUB_ANUC_DATE_2 = Commons.checkNullOfString(td.get(6).text().replaceAll("[.]", ""));
										String PUB_ANUC_DATE_3 = Commons.checkNullOfString(td.get(10).text().replaceAll("[.]", ""));
										
										String a_href = td.get(0).select("a").attr("href");
										String srvno		  = "";
										if(a_href.indexOf("(")>0){
											a_href 	= a_href.substring(a_href.indexOf(",")+1);
											srvno	= a_href.substring(0, a_href.indexOf(",")).replaceAll("'", "").trim();
										}

										boolean chk = false;
										if(PUB_ANUC_DATE_1!=""){
											if((Integer.parseInt(PUB_ANUC_DATE_1)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE_1)<=Integer.parseInt((String)param.get("endDate")))){
												chk=true;
											}
										}
										if(!PUB_ANUC_DATE_2.trim().equals("") && chk==false){
											if((Integer.parseInt(PUB_ANUC_DATE_2)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE_2)<=Integer.parseInt((String)param.get("endDate")))){
												chk=true;
											}
										}
										if(!PUB_ANUC_DATE_3.trim().equals("") && chk==false){
											if((Integer.parseInt(PUB_ANUC_DATE_3)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE_3)<=Integer.parseInt((String)param.get("endDate")))){
												chk=true;
											}
										}

										if(chk==true){
											//보험금지급능력평가의 경우 2차 상세페이지 존재
											/* http://rating.co.kr/disclosure/QDisclosure005.do
											 * PARAMETER :
											 * svctyCd
												srvno
												trancheSer
												companyCode
												tabFlag
											*/

											Document document_detail_2 = null;
											for(int try_num=0; try_num<3; try_num++){
												try{
													document_detail_2 = Jsoup.connect("http://rating.co.kr/disclosure/QDisclosure005.do")
															.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
															.header("Accept-Encoding","gzip, deflate")
															.header("Accept-Language","ko-KR")
															.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
															.data("svctyCd", mapping_type)
															.data("srvno", srvno)
															.data("trancheSer", "1")
															.data("companyCode", (String)cmpList.get(num).get("cmpCd"))
															.data("tabFlag", "")
															.maxBodySize(0)
															.timeout(600000)
															.get();
													break;
												}catch(Exception e){
													e.printStackTrace();
													Thread.sleep(3000);
												}
											}

											Elements tr_d_2 = document_detail_2.select("#sortTable1 tbody tr");

											for(int num_d_2=0; num_d_2<tr_d_2.size(); num_d_2++){
												Elements td_2 = tr_d_2.get(num_d_2).select("td");
												String CRPRVID      ="KR";
												String PUB_ANUC_DATE    =Commons.checkNullOfString(td_2.get(3).text().replaceAll("[.]", ""));
												String MATU_DATE    = "";
												String RULE_DATE    = "";
												if(!td_2.get(6).text().equals("")){
													MATU_DATE = td_2.get(6).text().replaceAll("[.]", "");
												}
												if(!td_2.get(0).text().equals("")){
													RULE_DATE = td_2.get(0).text().replaceAll("[.]", "");
												}

												if(!PUB_ANUC_DATE.equals("") && (Integer.parseInt(PUB_ANUC_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE)<=Integer.parseInt((String)param.get("endDate")))){
													String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
													String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
													String CR_TYP       =td_2.get(1).text();
													String SECU_TYP     ="보험금지급능력평가";
													String BOND_TYP     ="";
													String RANK         =td_2.get(4).text().trim().replace("↑", "").replace("↓", "");
													String WATCH = "";
													if(td_2.get(4).text().indexOf("↑")>0){
														WATCH = td_2.get(4).text().substring(td_2.get(4).text().indexOf("↑")).trim();
													}else if(td_2.get(4).text().indexOf("↓")>0){
														WATCH = td_2.get(4).text().substring(td_2.get(4).text().indexOf("↓")).trim();
													}else{
														WATCH = "";
													}
													String OUTLOOK      =td_2.get(5).text();
													String ISSUE_NO     ="";
													String ISSUE_AMT    ="";
													String ISSUE_AMT_TYP="";
													String ISSUE_DATE   ="";
													String SERIES		= "";
													String EXPOSE_TYP	= "";
													String REL_CMP_NM	= "";
													String RANK_CONF_DATE = td_2.get(2).text().replaceAll("[.]", "");

													// String to Map
													Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, PUB_ANUC_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
													result.add(crawler);
												}

											}
										}
									}
								}catch(Exception e){
									e.printStackTrace();
									log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : "  + e.toString() + "TR_D" + tr_d.get(num_d));
								}

								Double rand = Math.random();
								Thread.sleep((int)(rand*500));
							}
							break;
						case "36" : // 유동화익스포져(CFR)
							for(int num_d=0; num_d<tr_d.size(); num_d++){
								try{
									// 유동화익스포져(CFR)
									Elements td = tr_d.get(num_d).select("td");
									if(td.size()>4){
										String CRPRVID      ="KR";
										String PUB_ANUC_DATE    =Commons.checkNullOfString(td.get(8).text().replaceAll("[.]", ""));

										// 등급확정일 기준 조회영역에 해당하는 데이터 값만 추출
										if(!PUB_ANUC_DATE.equals("") && (Integer.parseInt(PUB_ANUC_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE)<=Integer.parseInt((String)param.get("endDate")))){
											String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
											String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
											String CR_TYP       =td.get(6).text();
											String SECU_TYP     ="유동화익스포져";
											String BOND_TYP     ="";
											String RANK         =td.get(9).text().trim().replace("↑", "").replace("↓", "");
											String WATCH = "";
											if(td.get(9).text().indexOf("↑")>0){
												WATCH = td.get(9).text().substring(td.get(9).text().indexOf("↑")).trim();
											}else if(td.get(9).text().indexOf("↓")>0){
												WATCH = td.get(9).text().substring(td.get(9).text().indexOf("↓")).trim();
											}else{
												WATCH = "";
											}
											String OUTLOOK      ="";
											String ISSUE_NO     ="";
											String ISSUE_AMT    =td.get(3).text();
											String ISSUE_AMT_TYP= "";
											if(Commons.getNumberChk(td.get(3).text())){
												ISSUE_AMT_TYP= "억원";
											}
											String ISSUE_DATE   ="";
											String MATU_DATE    =td.get(5).text().replaceAll("[.]", "");
											String RULE_DATE    ="";
											String SERIES		=td.get(2).text();
											String EXPOSE_TYP	=td.get(0).text();
											String REL_CMP_NM	=td.get(1).text();
											String RANK_CONF_DATE = td.get(7).text().replaceAll("[.]", "");

											// String to Map
											Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, PUB_ANUC_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
											result.add(crawler);
										}
									}
								}catch(Exception e){
									e.printStackTrace();
									log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D"  +tr_d.get(num_d));
								}
							}
							break;
						case "39" : // 파생결합증권(회사채)
							for(int num_d=0; num_d<tr_d.size(); num_d++){
								try{
									// 회사채
									Elements td = tr_d.get(num_d).select("td");
									if(td.size()>4){
										String CRPRVID      ="KR";
										String PUB_ANUC_DATE    =Commons.checkNullOfString(td.get(7).text().replaceAll("[.]", ""));

										// 등급확정일 기준 조회영역에 해당하는 데이터 값만 추출
										if(!PUB_ANUC_DATE.equals("") && (Integer.parseInt(PUB_ANUC_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE)<=Integer.parseInt((String)param.get("endDate")))){
											String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
											String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
											String CR_TYP       =td.get(5).text();
											String SECU_TYP     ="회사채";
											String BOND_TYP     =td.get(1).text();
											String RANK         =td.get(8).text().trim().replace("↑", "").replace("↓", "");
											String WATCH = "";
											if(td.get(8).text().indexOf("↑")>0){
												WATCH = td.get(8).text().substring(td.get(8).text().indexOf("↑")).trim();
											}else if(td.get(8).text().indexOf("↓")>0){
												WATCH = td.get(8).text().substring(td.get(8).text().indexOf("↓")).trim();
											}else{
												WATCH = "";
											}
											String OUTLOOK      =td.get(9).text();
											String ISSUE_NO     =td.get(0).text();
											String ISSUE_AMT    =td.get(2).text();
											String ISSUE_AMT_TYP= "";
											if(Commons.getNumberChk(td.get(2).text())){
												ISSUE_AMT_TYP= "억원";
											}
											String ISSUE_DATE   =td.get(3).text().replaceAll("[.]", "");
											String MATU_DATE    =td.get(4).text().replaceAll("[.]", "");
											String RULE_DATE    ="";
											String SERIES		= "";
											String EXPOSE_TYP	= "";
											String REL_CMP_NM	= "";
											String RANK_CONF_DATE = td.get(6).text().replaceAll("[.]", "");

											// String to Map
											Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, PUB_ANUC_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
											result.add(crawler);
										}
									}
								}catch(Exception e){
									e.printStackTrace();
									log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D"  +tr_d.get(num_d));
								}
							}
							break;
						case "3C" : // 커버드본드
							for(int num_d=0; num_d<tr_d.size(); num_d++){
								try{
									// 회사채
									Elements td = tr_d.get(num_d).select("td");
									if(td.size()>4){
										String CRPRVID      = "KR";
										String PUB_ANUC_DATE    = Commons.checkNullOfString(td.get(7).text().replaceAll("[.]", ""));

										// 등급확정일 기준 조회영역에 해당하는 데이터 값만 추출
										if(!PUB_ANUC_DATE.equals("") && (Integer.parseInt(PUB_ANUC_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(PUB_ANUC_DATE)<=Integer.parseInt((String)param.get("endDate")))){
											String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
											String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
											String CR_TYP       =td.get(5).text();
											String SECU_TYP     ="커버드본드";
											String BOND_TYP     =td.get(1).text();
											String RANK         =td.get(8).text().trim().replace("↑", "").replace("↓", "");
											String WATCH = "";
											if(td.get(8).text().indexOf("↑")>0){
												WATCH = td.get(8).text().substring(td.get(8).text().indexOf("↑")).trim();
											}else if(td.get(8).text().indexOf("↓")>0){
												WATCH = td.get(8).text().substring(td.get(8).text().indexOf("↓")).trim();
											}else{
												WATCH = "";
											}
											String OUTLOOK      ="";
											String ISSUE_NO     =td.get(0).text();
											String ISSUE_AMT    =td.get(2).text();
											String ISSUE_AMT_TYP= "";
											if(Commons.getNumberChk(td.get(2).text())){
												ISSUE_AMT_TYP= "억원";
											}
											String ISSUE_DATE   =td.get(3).text().replaceAll("[.]", "");
											String MATU_DATE    =td.get(4).text().replaceAll("[.]", "");
											String RULE_DATE    ="";
											String SERIES		= "";
											String EXPOSE_TYP	= "";
											String REL_CMP_NM	= "";
											String RANK_CONF_DATE = td.get(6).text().replaceAll("[.]", "");

											// String to Map
											Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, PUB_ANUC_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
											result.add(crawler);
										}
									}
								}catch(Exception e){
									e.printStackTrace();
									log.info("CMP_NM : " + cmpList.get(num).get("cmpNm") + ", CMP_CD : " + cmpList.get(num).get("cmpCd") + ", ERROR : " + e.toString() + "TR_D"  +tr_d.get(num_d));
								}
							}
							break;
					}

					Double rand = Math.random();
					Thread.sleep((int)(rand*500));
				}
				Double rand = Math.random();
				Thread.sleep((int)(rand*500));
			}
		}
		log.info("=================KR END================");
		return result;
	}
	
	// 서신평데이터 크롤링
	/*
	 * 최근공시 : http://www.scri.co.kr/Grade_Notice/Date.jsp
	 * parameter :
		cmd
		npage
		mode
		stopCount
		COMPNO
		compNM
		srchFrom
		srchTo
	 */
	public List<Crawler> insertCrawlingSCRIData(Map param) throws Exception{
		
		log.info("==================SCRI=================");
		List<Crawler> result = new ArrayList<>();

		String directory = "scriSnapShot";
		
		Document document = null;
		
		log.info((String)param.get("startDate"));
		log.info((String)param.get("endDate"));
		
		for(int try_num=0; try_num<3; try_num++){
			try{
				document = Jsoup.connect("http://www.scri.co.kr/sub/Grade_Notice/Date.jsp") // 주소 수정
						.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
						.header("Accept-Encoding","gzip, deflate")
						.header("Accept-Language","ko-KR")
						.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
						.data("cmd", "dateInfoList")
						.data("npage", "")
						.data("mode", "retrieve")
						.data("stopCount", "")
						.data("COMPNO", "")
						.data("compNM", "")
						.data("srchFrom", Commons.getChangeDateString((String)param.get("startDate"), "."))
						.data("srchTo", Commons.getChangeDateString((String)param.get("endDate"), "."))
						.maxBodySize(0)
						.timeout(600000)
						.post();
				break;
			}catch(Exception e){
				e.printStackTrace();
				Thread.sleep(3000);
			}
		}
		
		Elements body = document.select("body");

		log.info(Integer.toString(body.select("div.scri_content_area").select("table").size()));

		if(body.select("div.scri_content_area").select("table").size()>=5){
			Element table_title = body.select("div.scri_content_area").select("table").get(0);
			String s_title = table_title.select("td.sub_tit").text().replaceAll("[(ABS)]", "").replaceAll("[(SER)]", "").trim();
			result = insertDataUsingType(result, body, s_title, 1, param);
		}
		if(body.select("div.scri_content_area").select("table").size()>=9){
			Element table_title = body.select("div.scri_content_area").select("table").get(4);
			String s_title = table_title.select("td.sub_tit").text().replaceAll("[(ABS)]", "").replaceAll("[(SER)]", "").trim();
			result = insertDataUsingType(result, body, s_title, 5, param);
		}
		if(body.select("div.scri_content_area").select("table").size()>=13){
			Element table_title = body.select("div.scri_content_area").select("table").get(8);
			String s_title = table_title.select("td.sub_tit").text().replaceAll("[(ABS)]", "").replaceAll("[(SER)]", "").trim();
			result = insertDataUsingType(result, body, s_title, 9, param);
		}
		if(body.select("div.scri_content_area").select("table").size()>=17){
			Element table_title =body.select("div.scri_content_area").select("table").get(12);
			String s_title = table_title.select("td.sub_tit").text().replaceAll("[(ABS)]", "").replaceAll("[(SER)]", "").trim();
			result = insertDataUsingType(result, body, s_title, 13, param);
		}

		log.info("=================SCRI END================");
		return result;
	}


	// SCRI리스트 추출
	// parameter : 리스트 위치
	public List<Crawler> insertDataUsingType(List<Crawler> result, Elements body, String type, int seq, Map param)  throws Exception{

		Elements tr = body.select("div.scri_content_area").select("table").get(seq).select("tbody tr");

		if(type.equals("기업어음")){

			for(int tr_d_num=0; tr_d_num<tr.size(); tr_d_num++){
				Elements td = tr.get(tr_d_num).select("td");
				String CRPRVID      ="SCRI";
				String RANK_CONF_DATE    =td.get(7).text().replaceAll("[.]", "");

				String MATU_DATE    = "";
				String RULE_DATE    = td.get(4).text().replaceAll("[.]", "");

				if(RANK_CONF_DATE!="" && (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
					String dbl_func = td.get(1).select("a").attr("href");
					String CMP_CD = "";
					if(dbl_func.indexOf("(")>0){
						CMP_CD = dbl_func.substring(dbl_func.indexOf("(")+1, dbl_func.indexOf(",")).replaceAll("'", "");
					}
					String CMP_NM       =td.get(1).text().replaceAll("\u00a0", "").trim();
					String CR_TYP       =td.get(3).text().replace("평정","").replaceAll("\u00a0", "").trim();
					String SECU_TYP     ="기업어음";
					String BOND_TYP     ="";
					String RANK         =td.get(6).text().replace("↑", "").replace("↓", "").replaceAll("\u00a0", "").trim();
					String WATCH = "";
					if(td.get(6).text().indexOf("↑")>0){
						WATCH = "↑";
					}else if(td.get(6).text().indexOf("↓")>0){
						WATCH = "↓";
					}else{
						WATCH = "";
					}

					String OUTLOOK      ="";
					String ISSUE_NO     ="";
					String ISSUE_AMT    ="";
					String ISSUE_AMT_TYP="";
					String ISSUE_DATE   ="";
					String SERIES		= "";
					String EXPOSE_TYP	= "";
					String REL_CMP_NM	= "";

					// String to Map
					Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
					result.add(crawler);
				}
			}
		}else if(type.equals("전자단기사채")){

			for(int tr_d_num=0; tr_d_num<tr.size(); tr_d_num++){
				Elements td = tr.get(tr_d_num).select("td");
				String CRPRVID      ="SCRI";
				String RANK_CONF_DATE    =td.get(7).text().replaceAll("[.]", "").replaceAll("\u00a0", "");

				String MATU_DATE    = "";
				String RULE_DATE    = td.get(4).text().replaceAll("[.]", "").replaceAll("\u00a0", "");

				if(RANK_CONF_DATE!="" && (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){
					String dbl_func = td.get(1).select("a").attr("href");
					String CMP_CD = "";
					if(dbl_func.indexOf("(")>0){
						CMP_CD = dbl_func.substring(dbl_func.indexOf("(")+1, dbl_func.indexOf(",")).replaceAll("'", "");
					}
					String CMP_NM       =td.get(1).text().replaceAll("\u00a0", "").trim();
					String CR_TYP       =td.get(3).text().replace("평정","").replaceAll("\u00a0", "").trim();
					String SECU_TYP     ="전자단기사채";
					String BOND_TYP     ="";
					String RANK         =td.get(6).text().replace("↑", "").replace("↓", "").replaceAll("\u00a0", "").trim();
					String WATCH = "";
					if(td.get(6).text().indexOf("↑")>0){
						WATCH = "↑";
					}else if(td.get(6).text().indexOf("↓")>0){
						WATCH = "↓";
					}else{
						WATCH = "";
					}
					String OUTLOOK      ="";
					String ISSUE_NO     ="";
					String ISSUE_AMT    =td.get(2).text().replaceAll("\u00a0", "").trim();
					String ISSUE_AMT_TYP= "";
					if(Commons.getNumberChk(td.get(2).text().replaceAll("\u00a0", "").trim())){
						ISSUE_AMT_TYP= "억원";
					}
					String ISSUE_DATE   ="";
					String SERIES		= "";
					String EXPOSE_TYP	= "";
					String REL_CMP_NM	= "";

					// String to Map
					Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
					result.add(crawler);
				}
			}

		} else if(type.equals("자산유동화증권")){

			//기업리스트 추출 & 취소 건 추출
			List<Map> cmpList = new ArrayList<Map>();
			for(int num=0; num<tr.size(); num++){
				Map cmp = new HashMap();
				Elements td = tr.get(num).select("td");
				String dbl_func = td.get(1).select("a").attr("href");
				String CMP_CD = "";
				if(dbl_func.indexOf("(")>0){
					CMP_CD = dbl_func.substring(dbl_func.indexOf("(")+1, dbl_func.indexOf(",")).replaceAll("'", "");
				}
				String CMP_NM = td.get(1).text().replaceAll("\u00a0", "").trim();

				if(CMP_CD!=""){
					cmp.put(CMP_CD, CMP_NM);
					cmp.put("TYPE", type);
				}

				// chk값이 true이면 리스트 추가
				boolean chk = true;
				if(cmpList.size()>0){
					for(Map temp : cmpList){
						if(temp.get("cmpCd").equals(CMP_CD) && temp.get("type").equals(cmp.get("TYPE"))){
							chk = false;
							break;
						}
					}
				}

				// 기업 리스트화
				Set key = cmp.keySet();
				for (Iterator iterator = key.iterator(); iterator.hasNext();) {
					String keyName  = (String) iterator.next();
					if(!keyName.equals("TYPE")){
						String keyValue = (String) cmp.get(keyName);
						String keyType = (String) cmp.get("TYPE");
						Map obj = new HashMap();
						obj.put("cmpCd", keyName);
						obj.put("cmpNm", keyValue);
						obj.put("type", keyType);

						if(chk == true){
							cmpList.add(obj);
						}
					}
				}

				//취소 건 추출
				if(td.get(7).text().replaceAll("\u00a0", "").trim().equals("취소")){

					try{
						String CRPRVID      ="SCRI";
						String RANK_CONF_DATE    =td.get(8).text().replaceAll("[.]", "").replaceAll("\u00a0", "");
						if(RANK_CONF_DATE!="" && (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){

							String CR_TYP       =td.get(3).text().replace("평정","").replaceAll("\u00a0", "").trim();
							String SECU_TYP     ="자산유동화증권";
							String BOND_TYP     =td.get(2).text().replaceAll("\u00a0", "").trim();
							String RANK         =td.get(7).text().replace("↑", "").replace("↓", "").replaceAll("\u00a0", "").trim();
							String WATCH = "";
							if(td.get(7).text().indexOf("↑")>0){
								WATCH = "↑";
							}else if(td.get(7).text().indexOf("↓")>0){
								WATCH = "↓";
							}else{
								WATCH = "";
							}

							String OUTLOOK      ="";
							String ISSUE_NO     =td.get(4).text().replaceAll("\u00a0", "").trim();
							String ISSUE_AMT    =td.get(5).text().replaceAll("\u00a0", "").trim();
							String ISSUE_AMT_TYP= "";
							if(Commons.getNumberChk(td.get(5).text().replaceAll("\u00a0", "").trim())){
								ISSUE_AMT_TYP= "억원";
							}
							String ISSUE_DATE   = "";
							String MATU_DATE    = "";
							String RULE_DATE    = "";
							String SERIES		= "";
							String EXPOSE_TYP	= "";
							String REL_CMP_NM	= "";

							// String to Map
							Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
							result.add(crawler);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}

			//기업명을 토대로 상세 데이터 가져오기
			for(int num=0; num<cmpList.size(); num++){
				log.info("cmpCd : " + (String)cmpList.get(num).get("cmpCd") + ", cmpNm : " + (String)cmpList.get(num).get("cmpNm") + ", type : " + (String)cmpList.get(num).get("type"));

				Document document_detail = null;
				for(int try_num=0; try_num<3; try_num++){
					try{
						document_detail = Jsoup.connect("http://www.scri.co.kr/sub/Grade_Notice/Enterprise.jsp")
								.header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
								.header("Accept-Encoding","gzip, deflate")
								.header("Accept-Language","ko-KR")
								.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
								.data("cmd", "compInfoList")
								.data("npage", "")
								.data("mode", "")
								.data("stopCount", "")
								.data("COMPNO", (String)cmpList.get(num).get("cmpCd"))
								.data("compNM", (String)cmpList.get(num).get("cmpNm"))
								.data("srchFrom", Commons.getChangeDateString((String)param.get("startDate"), "."))
								.data("srchTo", Commons.getChangeDateString((String)param.get("endDate"), "."))
								.maxBodySize(0)
								.timeout(600000)
								.post();
						break;
					}catch(Exception e){
						e.printStackTrace();
						Thread.sleep(3000);
					}
				}

				Elements body_detail = document_detail.select("body").select("table.sub_list");
				Elements tr_d = body_detail.select("tbody tr");

				if(tr_d!=null){
					for(int num_d=0; num_d<tr_d.size(); num_d++){
						try{
							Elements td = tr_d.get(num_d).select("td");
							String CRPRVID      ="SCRI";
							String RANK_CONF_DATE    =td.get(7).text().replaceAll("[.]", "").replaceAll("\u00a0", "");
							String CMP_CD       =(String)cmpList.get(num).get("cmpCd");
							String CMP_NM       =(String)cmpList.get(num).get("cmpNm");

							if(RANK_CONF_DATE!="" && (Integer.parseInt(RANK_CONF_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RANK_CONF_DATE)<=Integer.parseInt((String)param.get("endDate")))){

								String CR_TYP       =td.get(6).text().replace("평정","").replaceAll("\u00a0", "").trim();
								String SECU_TYP     ="자산유동화증권";
								String BOND_TYP     =td.get(2).text().replaceAll("\u00a0", "").trim();
								String RANK         =td.get(8).text().replace("↑", "").replace("↓", "").replaceAll("\u00a0", "").trim();
								String WATCH = "";
								if(td.get(8).text().indexOf("↑")>0){
									WATCH = "↑";
								}else if(td.get(8).text().indexOf("↓")>0){
									WATCH = "↓";
								}else{
									WATCH = "";
								}
								String OUTLOOK      ="";
								String ISSUE_NO     =td.get(1).text().replaceAll("\u00a0", "").trim();
								String ISSUE_AMT    =td.get(3).text().replaceAll("\u00a0", "").trim();
								String ISSUE_AMT_TYP= "";
								if(Commons.getNumberChk(td.get(3).text().replaceAll("\u00a0", "").trim())){
									ISSUE_AMT_TYP= "억원";
								}
								String ISSUE_DATE   = td.get(4).text().replaceAll("[.]", "").replaceAll("\u00a0", "");
								String MATU_DATE    = td.get(5).text().replaceAll("[.]", "").replaceAll("\u00a0", "");
								String RULE_DATE    = "";
								String SERIES		= "";
								String EXPOSE_TYP	= "";
								String REL_CMP_NM	= "";

								// String to Map
								Crawler crawler = convertMap(CRPRVID, RANK_CONF_DATE, RANK_CONF_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
								result.add(crawler);
							}
						}catch(Exception e){
							String CMP_CD       =(String)cmpList.get(num).get("cmpCd"); // CMP_CD, CMP_NM은 어떤 의미를 갖고 있을까?
							String CMP_NM       =(String)cmpList.get(num).get("cmpNm");
							e.printStackTrace();
						}
					}
				}
			}
		}else if(type.equals("유동화익스포져")){
//			for(int tr_d_num=0; tr_d_num<tr.size(); tr_d_num++){
//				Elements td = tr.get(tr_d_num).select("td");
//				String CRPRVID      ="SCRI";
//				String RATE_DATE    =td.get(9).text().replaceAll("[.]", "").replaceAll("\u00a0", "");
//
//				String MATU_DATE    = "";
//				String RULE_DATE    = "";
//
//				if(RATE_DATE!="" && (Integer.parseInt(RATE_DATE)>=Integer.parseInt((String)param.get("startDate")) && Integer.parseInt(RATE_DATE)<=Integer.parseInt((String)param.get("endDate")))){
//					String dbl_func = td.get(1).select("a").attr("href");
//					String CMP_CD = "";
//					if(dbl_func.indexOf("(")>0){
//						CMP_CD = dbl_func.substring(dbl_func.indexOf("(")+1, dbl_func.indexOf(",")).replaceAll("'", "");
//					}
//					String CMP_NM       =td.get(1).text().replaceAll("\u00a0", "").trim();
//					String CR_TYP       =td.get(3).text().replace("평정","").replaceAll("\u00a0", "").trim();
//					String SECU_TYP     ="유동화익스포져";
//					String BOND_TYP     ="";
//					String RANK         =td.get(8).text().replace("↑", "").replace("↓", "").replaceAll("\u00a0", "").trim();
//					String WATCH = "";
//					if(td.get(8).text().indexOf("↑")>0){
//						WATCH = "↑";
//					}else if(td.get(8).text().indexOf("↓")>0){
//						WATCH = "↓";
//					}else{
//						WATCH = "";
//					}
//
//					String OUTLOOK      ="";
//					String ISSUE_NO     =td.get(4).text().replaceAll("\u00a0", "").trim();
//					String ISSUE_AMT    =td.get(5).text().replaceAll("\u00a0", "").trim();
//					String ISSUE_AMT_TYP= "";
//					if(Commons.getNumberChk(td.get(5).text().replaceAll("\u00a0", "").trim())){
//						ISSUE_AMT_TYP= "억원";
//					}
//					String ISSUE_DATE   ="";
//					String SERIES		= td.get(8).text().replaceAll("\u00a0", "").trim(); //
//					String EXPOSE_TYP	= td.get(4).text().replaceAll("\u00a0", "").trim(); //
//					String REL_CMP_NM	= td.get(6).text().replaceAll("\u00a0", "").trim();
//
//					// String to Map
//					Map obj = convertMap(CRPRVID, RATE_DATE, CMP_CD, CMP_NM, CR_TYP, SECU_TYP, BOND_TYP, RANK, WATCH, OUTLOOK, ISSUE_NO, ISSUE_AMT, ISSUE_AMT_TYP, ISSUE_DATE, MATU_DATE, RULE_DATE, SERIES, EXPOSE_TYP, REL_CMP_NM);
//					result.add(obj);
//
//				}
//			}
		}
		return result;
	}	
	
	public Crawler convertMap(String crprvid
						     ,String rank_conf_date
				  			 ,String pub_anuc_date
				  			 ,String cmp_cd
				  			 ,String cmp_nm
				  			 ,String cr_typ
				  			 ,String secu_typ
				  			 ,String bond_typ
				  			 ,String rank
				  			 ,String watch
				  			 ,String outlook
				  			 ,String issue_no
				  			 ,String issue_amt
				  			 ,String issue_amt_typ
				  			 ,String issue_date
				  			 ,String matu_date
				  			 ,String rule_date
				  			 ,String series
				  			 ,String expose_typ
				  			 ,String rel_cmp_nm) {
		
		Crawler crawler = new Crawler();
		crawler.setCrprvid(crprvid);
		crawler.setRankConfDate(rank_conf_date);
		crawler.setPubAnucDate(pub_anuc_date);
		crawler.setCmpCd(cmp_cd);
		crawler.setCmpNm(cmp_nm);
		crawler.setCrTyp(cr_typ);
		crawler.setSecuTyp(secu_typ);
		crawler.setBondTyp(Commons.getNullChange(bond_typ));
		crawler.setRank(rank);
		crawler.setWatch(watch);
		crawler.setOutlook(outlook);
		crawler.setIssueNo(Commons.getNullChange(issue_no));
		crawler.setIssueAmt(issue_amt);
		crawler.setIssueAmtTyp(issue_amt_typ);
		crawler.setIssueDate(issue_date);
		crawler.setMatuDate(matu_date);
		crawler.setRuleDate(rule_date);
		crawler.setSeries(series);
		crawler.setExposeTyp(expose_typ);
		crawler.setRelCmpNm(rel_cmp_nm);
		
		return crawler;
	}
}
