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

	List<Article> getForPrintArticles(@Param("id") int id);

	Article getForPrintArticleById(int id);

	void write(Map<String, Object> param);

}
