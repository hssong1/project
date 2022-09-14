package com.nice.crawler.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nice.crawler.dto.CrawlerResultCountDTO;
import com.nice.crawler.dto.DartResultCountDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlackBotSeviceImpl implements SlackBotService {

    @Value("${slackbotUrl}")
    private String slackbotUrl;

    @Override
    public void sendSlackBot(CrawlerResultCountDTO crawlerResultCountDTO) {
        RestTemplate restTemplate = new RestTemplate();

        String periodMessage = crawlerResultCountDTO.periodMessage();
        String countMessage = crawlerResultCountDTO.countMessage();
        String idMessage = crawlerResultCountDTO.IdMessage();

        StringBuffer sb = new StringBuffer();
        sb.append(periodMessage).append("\n");
        sb.append(countMessage).append("\n");
        sb.append(idMessage).append("\n");
        String html = sb.toString();

        // slack에 보낼 메세지 설정
        Map<String, Object> request = new HashMap<>();
        request.put("username", "타사 크롤링 봇");
        request.put("text", html);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(request);

        // WebHook URL
        restTemplate.exchange(slackbotUrl, HttpMethod.POST, entity, String.class);
    }

	@Override
	public void sendSlackBotEnforce(CrawlerResultCountDTO crawlerResultCountDTO) {
		RestTemplate restTemplate = new RestTemplate();

        String periodMessage = crawlerResultCountDTO.periodMessage();
        String countMessage = crawlerResultCountDTO.countMessage();
        String idMessage = crawlerResultCountDTO.IdMessage();

        StringBuffer sb = new StringBuffer();
        sb.append("====수동처리====").append("\n");
        sb.append(periodMessage).append("\n");
        sb.append(countMessage).append("\n");
        sb.append(idMessage).append("\n");
        String html = sb.toString();

        // slack에 보낼 메세지 설정
        Map<String, Object> request = new HashMap<>();
        request.put("username", "타사 크롤링 봇");
        request.put("text", html);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(request);

        // WebHook URL
        restTemplate.exchange(slackbotUrl, HttpMethod.POST, entity, String.class);
	}
	
    @Override
    public void sendDartSlackBot(DartResultCountDTO dartResultCountDTO) {
        RestTemplate restTemplate = new RestTemplate();

        String periodMessage = dartResultCountDTO.periodMessage();
        String countMessage = dartResultCountDTO.countMessage();
        String idMessage = dartResultCountDTO.IdMessage();

        StringBuffer sb = new StringBuffer();
        sb.append(periodMessage).append("\n");
        sb.append(countMessage).append("\n");
        sb.append(idMessage).append("\n");
        String html = sb.toString();

        // slack에 보낼 메세지 설정
        Map<String, Object> request = new HashMap<>();
        request.put("username", "금감원 크롤링 봇");
        request.put("text", html);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(request);

        // WebHook URL
        restTemplate.exchange(slackbotUrl, HttpMethod.POST, entity, String.class);
    }	
	
}
