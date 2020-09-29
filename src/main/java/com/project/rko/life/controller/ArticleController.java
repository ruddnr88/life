package com.project.rko.life.controller;

import java.util.HashMap;
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
	public String showList(Model model, @PathVariable("boardCode") String boardCode, String searchKeyword, String searchType,
			@RequestParam(value = "page", defaultValue = "1") int page, HttpServletRequest request) {
		Board board = articleService.getBoardByCode(boardCode);
		model.addAttribute("board", board);
		
		if (searchType != null) {
			searchType = searchType.trim();
		}

		if (searchKeyword != null) {
			searchKeyword = searchKeyword.trim();
		}
		
		Map<String, Object> getForPrintArticlesByParam = new HashMap();
		getForPrintArticlesByParam.put("searchKeyword", searchKeyword);
		getForPrintArticlesByParam.put("searchType", searchType);
		getForPrintArticlesByParam.put("boardCode", boardCode);
		getForPrintArticlesByParam.put("id", board.getId());
		
		int itemsInAPage = 10;
		int limitCount = itemsInAPage;
		int limitFrom = (page - 1) * itemsInAPage;
		getForPrintArticlesByParam.put("limitCount", limitCount);
		getForPrintArticlesByParam.put("limitFrom", limitFrom);
		int totalCount = articleService.getTotalCount(getForPrintArticlesByParam);
		int totalPage = (int) Math.ceil(totalCount / (double) itemsInAPage);
		
		List<Article> articles = articleService.getForPrintArticles(getForPrintArticlesByParam);
		//List<Article> articles = articleService.getForPrintArticles(board.getId());
		//원래는 보드Id만 가져왔었음.

		model.addAttribute("articles", articles);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalCount", totalCount);
		
		int pageBoundSize = 5;
		int pageStartsWith = page - pageBoundSize;
		if (pageStartsWith < 1) {
			pageStartsWith = 1;
		}
		int pageEndsWith = page + pageBoundSize;
		if (pageEndsWith > totalPage) {
			pageEndsWith = totalPage;
		}

		model.addAttribute("pageStartsWith", pageStartsWith);
		model.addAttribute("pageEndsWith", pageEndsWith);

		boolean beforeMorePages = pageStartsWith > 1;
		boolean afterMorePages = pageEndsWith < totalPage;

		model.addAttribute("beforeMorePages", beforeMorePages);
		model.addAttribute("afterMorePages", afterMorePages);
		model.addAttribute("pageBoundSize", pageBoundSize);

		model.addAttribute("needToShowPageBtnToFirst", page != 1);
		model.addAttribute("needToShowPageBtnToLast", page != totalPage);

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
		System.out.println("확인하기!!!!!!!!!:"+ param);
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
	
	//게시물 삭제
	@RequestMapping("/usr/article/{boardCode}-doDelete")
	public String doDelete(HttpServletRequest req, int id, @PathVariable("boardCode") String boardCode, Model model) {
		Board board = articleService.getBoardByCode(boardCode);
		model.addAttribute("board", board);
		Member loginedMember = (Member)req.getAttribute("loginedMember");
		
		ResultData checkActorCanDeleteResultData = articleService.checkActorCanModify(loginedMember, id);
		
		if (checkActorCanDeleteResultData.isFail() ) {
			model.addAttribute("alertMsg", checkActorCanDeleteResultData.getMsg());
			
			return "common/redirect";
		}
		
		articleService.delete(id);
		
		return "redirect:{boardCode}-list";
	}


	
}

