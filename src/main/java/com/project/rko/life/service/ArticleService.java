package com.project.rko.life.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.rko.life.dao.ArticleDao;
import com.project.rko.life.dto.Article;
import com.project.rko.life.dto.Board;
import com.project.rko.life.dto.File;
import com.project.rko.life.dto.Member;
import com.project.rko.life.dto.ResultData;
import com.project.rko.life.util.Util;

@Service
public class ArticleService {
	@Autowired
	private ArticleDao articleDao;
	@Autowired
	private FileService fileService;

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

		updateForPrintInfo(actor, article);

		List<File> files = fileService.getFiles("article", article.getId(), "common", "attachment");

		Map<String, File> filesMap = new HashMap<>();

		for (File file : files) {
			filesMap.put(file.getFileNo() + "", file);
		}

		Util.putExtraVal(article, "file__common__attachment", filesMap);

		return article;
	}

	private void updateForPrintInfo(Member actor, Article article) {
		Util.putExtraVal(article, "actorCanDelete", actorCanDelete(actor, article));
		Util.putExtraVal(article, "actorCanModify", actorCanModify(actor, article));

	}

	// 액터가 해당 댓글을 수정할 수 있는지 알려준다.
	public boolean actorCanModify(Member actor, Article article) {
		return actor != null && actor.getId() == article.getMemberId() ? true : false;
	}

	// 액터가 해당 댓글을 삭제할 수 있는지 알려준다.
	public boolean actorCanDelete(Member actor, Article article) {
		return actorCanModify(actor, article);
	}

	// 글쓰기
	public int write(Map<String, Object> param) {
		articleDao.write(param);
		int id = Util.getAsInt(param.get("id"));

		String fileIdsStr = (String) param.get("fileIdsStr");

		if (fileIdsStr != null && fileIdsStr.length() > 0) {
			fileIdsStr = fileIdsStr.trim();

			if (fileIdsStr.startsWith(",")) {
				fileIdsStr = fileIdsStr.substring(1);
			}
		}
		if (fileIdsStr != null && fileIdsStr.length() > 0) {
			fileIdsStr = fileIdsStr.trim();

			if (fileIdsStr.equals(",")) {
				fileIdsStr = "";
			}
		}
		if (fileIdsStr != null && fileIdsStr.length() > 0) {
			List<Integer> fileIds = Arrays.asList(fileIdsStr.split(",")).stream().map(s -> Integer.parseInt(s.trim()))
					.collect(Collectors.toList());

			// 파일이 먼저 생성된 후에, 관련 데이터가 생성되는 경우에는, file의 relId가 일단 0으로 저장된다.
			// 그것을 뒤늦게라도 이렇게 고처야 한다.
			for (int fileId : fileIds) {
				fileService.changeRelId(fileId, id);
			}
		}

		return id;

	}

	public boolean actorCanModify(Member actor, int id) {
		Article article = articleDao.getArticleById(id);

		return actorCanModify(actor, article);
	}

	public ResultData checkActorCanModify(Member actor, int id) {
		boolean actorCanModify = actorCanModify(actor, id);

		if (actorCanModify) {
			return new ResultData("S-1", "가능합니다.", "id", id);
		}

		return new ResultData("F-1", "권한이 없습니다.", "id", id);
	}

	// 글수정
	public void modify(Map<String, Object> param) {
		articleDao.modify(param);

		int id = Util.getAsInt(param.get("id"));

		String fileIdsStr = (String) param.get("fileIdsStr");

		if (fileIdsStr != null && fileIdsStr.length() > 0) {
			fileIdsStr = fileIdsStr.trim();

			if (fileIdsStr.startsWith(",")) {
				fileIdsStr = fileIdsStr.substring(1);
			}
		}

		if (fileIdsStr != null && fileIdsStr.length() > 0) {
			List<Integer> fileIds = Arrays.asList(fileIdsStr.split(",")).stream().map(s -> Integer.parseInt(s.trim()))
					.collect(Collectors.toList());

			// 파일이 먼저 생성된 후에, 관련 데이터가 생성되는 경우에는, file의 relId가 일단 0으로 저장된다.
			// 그것을 뒤늦게라도 이렇게 고처야 한다.
			for (int fileId : fileIds) {
				fileService.changeRelId(fileId, id);
			}
		}

	}

}
