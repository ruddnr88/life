package com.project.rko.life.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.rko.life.dto.Article;
import com.project.rko.life.dto.Board;

@Mapper
public interface ArticleDao {

	Board getBoardByCode(String boardCode);
	
	//게시판 보드 아이디 받아온거임
	List<Article> getForPrintArticles(Map<String, Object> param);
	
//	List<Article> getForPrintArticles(@Param("id") int id);


	Article getForPrintArticleById(int id);

	void write(Map<String, Object> param);

	Article getArticleById(int id);

	void modify(Map<String, Object> param);

	void delete(int id);

	int getTotalCount(Map<String, Object> param);

}
