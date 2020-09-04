package com.project.rko.life.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.project.rko.life.dao.MemberDao;
import com.project.rko.life.dto.Member;
import com.project.rko.life.dto.ResultData;
import com.project.rko.life.util.Util;

@Service
public class MemberService {
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private MailService mailService;
	@Autowired
	private AttrService attrService;
	@Value("${custom.siteMainUri}")
	private String siteMainUri;
	@Value("${custom.siteName}")
	private String siteName;

	public Member getMemberById(int id) {
		return memberDao.getMemberById(id);
	}

	public ResultData checkLoginIdJoinable(String loginId) {
		int count = memberDao.getLoginIdDupCount(loginId);
		if (count == 0) {
			return new ResultData("S-1", "가입가능한 로그인 아이디 입니다.", "loginId", loginId);
		}
		return new ResultData("F-1", "이미 사용중인 로그인 아이디 입니다.", "loginId", loginId);

	}

	public int join(Map<String, Object> param) {
		memberDao.join(param);

		sendJoinCompleteMail((String) param.get("email"));

		return Util.getAsInt(param.get("id"));
	}

	private void sendJoinCompleteMail(String email) {
		String mailTitle = String.format("[%s] 가입이 완료되었습니다.", siteName);

		StringBuilder mailBodySb = new StringBuilder();
		mailBodySb.append("<h1>가입이 완료되었습니다.</h1>");
		mailBodySb.append(String.format("<p><a href=\"%s\" target=\"_blank\">%s</a>로 이동</p>", siteMainUri, siteName));

		mailService.send(email, mailTitle, mailBodySb.toString());

	}

	public Member getMemberByLoginId(String loginId) {
		return memberDao.getMemberByLoginId(loginId);
	}

	public Member getMemberByNameAndEmail(String name, String email) {
		return memberDao.getMemberByNameAndEmail(name, email);
	}

	public String genCheckPasswordAuthCode(int actorId) {
		String authCode = UUID.randomUUID().toString();
		attrService.setValue("member__" + actorId + "__extra__modifyPrivateAuthCode", authCode,
				Util.getDateStrLater(60 * 60));

		return authCode;
	}

	public ResultData checkValidCheckPasswordAuthCode(int actorId, String checkPasswordAuthCode) {
		if (attrService.getValue("member__" + actorId + "__extra__modifyPrivateAuthCode")
				.equals(checkPasswordAuthCode)) {
			return new ResultData("S-1", "유효한 키 입니다.");
		}

		return new ResultData("F-1", "유효하지 않은 키 입니다.");
	}

	// 회원정보변경
	public void infoModify(Map<String, Object> param) {
		memberDao.infoModify(param);

		if (param.get("loginPw") != null) {
			setNotUsingTempPassword(Util.getAsInt(param.get("id")));
		}

	}

	private void setNotUsingTempPassword(int id) {
		attrService.remove("member", id, "extra", "usingTempPassword");
	}

	public void memberdelete(int id) {
		memberDao.delete(id);

	}

	public boolean isNeedToChangePasswordForTemp(int id) {
		String value = attrService.getValue("member", id, "extra", "usingTempPassword");

		if (value == null || value.equals("1") == false) {
			return false;
		}

		return true;
	}

	public Member getMemberByIdForSession(int actorId) {
		Member member = getMemberById(actorId);

		boolean isNeedToChangePasswordForTemp = isNeedToChangePasswordForTemp(member.getId());
		member.getExtra().put("isNeedToChangePasswordForTemp", isNeedToChangePasswordForTemp);

		return member;
	}

	public boolean isJoinableLoginId(String loginId) {

		return memberDao.isJoinableLoginId(loginId);
	}

	// 임시패스워드발송(쌤꺼)
	public ResultData sendTempLoginPwToEmail(Member actor) {
		String title = "[" + siteName + "] 임시 패스워드 발송";
		String tempPassword = Util.getTempPassword(6);
		String body = "<h1>임시 패스워드 : " + tempPassword + "</h1>";
		body += "<a href=\"" + siteMainUri + "/usr/member/login\" target=\"_blank\">로그인 하러가기</a>";

		ResultData sendResultData = mailService.send(actor.getEmail(), title, body);

		if (sendResultData.isFail()) {
			return sendResultData;
		}
		setTempPassword(actor, tempPassword);

		return new ResultData("S-1", "계정의 이메일주소로 임시 패스워드가 발송되었습니다.");
	}

	private void setTempPassword(Member actor, String tempPassword) {

		Map<String, Object> modifyParam = new HashMap<>();
		modifyParam.put("id", actor.getId());
		modifyParam.put("loginPw", Util.sha256(tempPassword));
		infoModify(modifyParam);

		setUsingTempPassword(actor.getId());
	}

	public boolean usingTempPassword(int id) {
		String value = attrService.getValue("member", id, "extra", "usingTempPassword");

		if (value == null || value.equals("1") == false) {
			return false;
		}

		return true;
	}

	private void setUsingTempPassword(int id) {
		attrService.setValue("member", id, "extra", "usingTempPassword", "1", null);
	}

}
