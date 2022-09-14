package com.nice.crawler.service;

import com.nice.crawler.dto.CrawlerResultCountDTO;
import com.nice.crawler.dto.DartResultCountDTO;

public interface SlackBotService {

    public void sendSlackBot(CrawlerResultCountDTO crawlerResultCountDTO);
    
    public void sendSlackBotEnforce(CrawlerResultCountDTO crawlerResultCountDTO);
    
    public void sendDartSlackBot(DartResultCountDTO dartResultCountDTO);
    
}
