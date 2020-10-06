package com.project.rko.life.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.rko.life.dto.Member;
import com.project.rko.life.dto.Reply;
import com.project.rko.life.dto.ResultData;
import com.project.rko.life.service.ReplyService;
import com.project.rko.life.util.Util;


@Controller
public class ReplyController {
	@Autowired
	private ReplyService replyService;
	
	//댓글리스트
	@RequestMapping("/usr/reply/getForPrintReplies")
	@ResponseBody
	public ResultData getForPrintReplies(@RequestParam Map<String, Object> param, HttpServletRequest req) {
		Member loginedMember = (Member) req.getAttribute("loginedMember");
		Map<String, Object> rsDataBody = new HashMap<>();

		param.put("relTypeCode", "article");
		Util.changeMapKey(param, "articleId", "relId");

		param.put("actor", loginedMember);
		List<Reply> replies = replyService.getForPrintReplies(param);
		rsDataBody.put("replies", replies);

		return new ResultData("S-1", String.format("%d개의 댓글을 불러왔습니다.", replies.size()), rsDataBody);
	}
	
	//댓글 작성
	@RequestMapping("/usr/reply/doWriteReplyAjax")
	@ResponseBody
	public ResultData doWriteReplyAjax(@RequestParam Map<String, Object> param, HttpServletRequest request) {
		Map<String, Object> rsDataBody = new HashMap<>();

		param.put("memberId", request.getAttribute("loginedMemberId"));

		int newReplyId = replyService.writeReply(param);
		rsDataBody.put("replyId", newReplyId);

		return new ResultData("S-1", String.format("%d번 댓글이 생성되었습니다.", newReplyId), rsDataBody);
	}
	
	
	//댓글 삭제
	@RequestMapping("/usr/reply/doDeleteReplyAjax")
	@ResponseBody
	public ResultData doDeleteReplyAjax(int id, HttpServletRequest req) {
		Member loginedMember = (Member) req.getAttribute("loginedMember");
		Member memberId = (Member)req.getAttribute("loginedMemberId");
		
		Reply reply = replyService.getForPrintReplyById(id);

		if (replyService.actorCanDelete(loginedMember, reply) == false ) {
			return new ResultData("F-1", String.format("%d번 댓글을 삭제할 권한이 없습니다.", id));
		}

		replyService.deleteReply(id);

		return new ResultData("S-1", String.format("%d번 댓글을 삭제하였습니다.", id));
	}
	
	//댓글수정
	@RequestMapping("/usr/reply/doModifyReplyAjax")
	@ResponseBody
	public ResultData doModifyReplyAjax(@RequestParam Map<String, Object> param, HttpServletRequest req, int id) {
		Member loginedMember = (Member) req.getAttribute("loginedMember");
		Reply reply = replyService.getForPrintReplyById(id);

		if (replyService.actorCanModify(loginedMember, reply) == false) {
			return new ResultData("F-1", String.format("%d번 댓글을 수정할 권한이 없습니다.", id));
		}

		Map<String, Object> modfiyReplyParam = Util.getNewMapOf(param, "id", "body");
		ResultData rd = replyService.modfiyReply(modfiyReplyParam);

		return rd;
	}


}
