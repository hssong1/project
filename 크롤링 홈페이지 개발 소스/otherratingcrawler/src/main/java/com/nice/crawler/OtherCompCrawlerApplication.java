package com.nice.crawler;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@MapperScan(basePackages="com.nice.crawler.mapper")
public class OtherCompCrawlerApplication { 
	public static void main(String[] args) throws Exception {
		SpringApplication.run(OtherCompCrawlerApplication.class, args);
		System.out.println("START SERVER!!");
	}
}