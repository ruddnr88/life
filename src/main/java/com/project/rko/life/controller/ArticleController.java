package com.project.rko.life.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.rko.life.dto.Article;
import com.project.rko.life.dto.Board;
import com.project.rko.life.dto.Member;
import com.project.rko.life.dto.ResultData;
import com.project.rko.life.service.ArticleService;
import com.project.rko.life.util.Util;

@Controller
public class ArticleController {
	@Autowired
	private ArticleService articleService;
	
	//리스트보기
	@RequestMapping("/usr/article/{boardCode}-list")
	public String showList(Model model, @PathVariable("boardCode") String boardCode) {
		Board board = articleService.getBoardByCode(boardCode);
		model.addAttribute("board", board);
		
		List<Article> articles = articleService.getForPrintArticles(board.getId());

		model.addAttribute("articles", articles);

		return "article/list";
	}
	
	//게시물 상세
	@RequestMapping("/usr/article/{boardCode}-detail")
	public String showDetail(Model model, @RequestParam Map<String, Object> param, HttpServletRequest req, @PathVariable("boardCode") String boardCode, String listUrl) {
		if ( listUrl == null ) {
			listUrl = "./" + boardCode + "-list";
		}
		model.addAttribute("listUrl", listUrl);
		
		Board board = articleService.getBoardByCode(boardCode);
		model.addAttribute("board", board);
		
		int id = Integer.parseInt((String) param.get("id"));
		
		Member loginedMember = (Member)req.getAttribute("loginedMember");

		Article article = articleService.getForPrintArticleById(loginedMember, id);

		model.addAttribute("article", article);

		return "article/detail";
	}
	//글쓰기이동
	@RequestMapping("/usr/article/{boardCode}-write")
	public String showWrite(@PathVariable("boardCode") String boardCode, Model model, String listUrl) {
		if ( listUrl == null ) {
			listUrl = "./" + boardCode + "-list";
		}
		model.addAttribute("listUrl", listUrl);
		Board board = articleService.getBoardByCode(boardCode);
		model.addAttribute("board", board);
		
		return "article/write";
	}
	//글작성
	@RequestMapping("/usr/article/{boardCode}-doWrite")
	public String doWrite(@RequestParam Map<String, Object> param, HttpServletRequest req, @PathVariable("boardCode") String boardCode, Model model) {
		Board board = articleService.getBoardByCode(boardCode);
		model.addAttribute("board", board);
		
		Map<String, Object> newParam = Util.getNewMapOf(param, "title", "body", "fileIdsStr");
		int loginedMemberId = (int)req.getAttribute("loginedMemberId");
		newParam.put("boardId", board.getId());
		newParam.put("memberId", loginedMemberId);
		int newArticleId = articleService.write(newParam);

		String redirectUri = (String) param.get("redirectUri");
		redirectUri = redirectUri.replace("#id", newArticleId + "");

		return "redirect:" + redirectUri;
	}
	
	@RequestMapping("/usr/article/{boardCode}-modify")
	public String showModify(Model model, @RequestParam Map<String, Object> param, HttpServletRequest req, @PathVariable("boardCode") String boardCode, String listUrl) {
		model.addAttribute("listUrl", listUrl);
		
		Board board = articleService.getBoardByCode(boardCode);
		model.addAttribute("board", board);
		
		int id = Integer.parseInt((String) param.get("id"));
		
		Member loginedMember = (Member)req.getAttribute("loginedMember");
		Article article = articleService.getForPrintArticleById(loginedMember, id);

		model.addAttribute("article", article);

		return "article/modify";
	}
	@RequestMapping("/usr/article/{boardCode}-doModify")
	public String doModify(@RequestParam Map<String, Object> param, HttpServletRequest req, int id, @PathVariable("boardCode") String boardCode, Model model) {
		Board board = articleService.getBoardByCode(boardCode);
		model.addAttribute("board", board);
		Map<String, Object> newParam = Util.getNewMapOf(param, "title", "body", "fileIdsStr", "articleId", "id");
		Member loginedMember = (Member)req.getAttribute("loginedMember");
		
		ResultData checkActorCanModifyResultData = articleService.checkActorCanModify(loginedMember, id);
		
		if (checkActorCanModifyResultData.isFail() ) {
			model.addAttribute("historyBack", true);
			model.addAttribute("alertMsg", checkActorCanModifyResultData.getMsg());
			
			return "common/redirect";
		}
		
		articleService.modify(newParam);
		
		String redirectUri = (String) param.get("redirectUri");

		return "redirect:" + redirectUri;
	}


	
}
