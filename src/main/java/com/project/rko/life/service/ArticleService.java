package com.project.rko.life.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.rko.life.dao.ArticleDao;
import com.project.rko.life.dto.Article;
import com.project.rko.life.dto.Board;
import com.project.rko.life.dto.Member;
import com.project.rko.life.util.Util;

@Service
public class ArticleService {
	@Autowired
	private ArticleDao articleDao;

	// 게시판 코드 네임 가져오기
	public Board getBoardByCode(String boardCode) {
		return articleDao.getBoardByCode(boardCode);
	}

	// 게시물 목록 가져오기
	public List<Article> getForPrintArticles(int id) {
		List<Article> articles = articleDao.getForPrintArticles(id);

		return articles;
	}

	// 게시물 아이디로 가져오기(상세보기)
	public Article getForPrintArticleById(Member actor, int id) {
		Article article = articleDao.getForPrintArticleById(id);
		return article;
	}

	// 글쓰기
	public int write(Map<String, Object> param) {
		articleDao.write(param);
		int id = Util.getAsInt(param.get("id"));

		return id;
	}

}
