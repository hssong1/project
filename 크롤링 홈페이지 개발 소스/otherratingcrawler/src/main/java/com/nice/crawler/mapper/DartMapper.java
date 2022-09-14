package com.nice.crawler.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.nice.crawler.dto.DartDTO;
import com.nice.crawler.model.Dart;

@Repository
public interface DartMapper {
	
	public List<Dart> findDartList(DartDTO.Request request);
	
	public Dart checkDartList(Long sequence);
	
	public void insertDartList(Dart dartDetail);
	
}
