package com.project.rko.life.service;

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
		return memberDao.getMemberByNameAndEmail(name,email);
	}

	public void notifyTempLoginPw(Member member) {
		String to = member.getEmail();
		String tempPasswordOrigin = Util.getTempPassword(6);
		String tempPassword = Util.sha256(tempPasswordOrigin);

		passWordmodify(member.getId(), tempPassword);
		attrService.setValue("member", member.getId(), "extra", "useTempPassword", "1",Util.getDateStrLater(60 * 60));

		String title = String.format("[%s]님 임시패스워드 발송", member.getName());
		String body = String.format("<div>임시 패스워드 : %s</div>\n", tempPasswordOrigin);
		mailService.send(to, title, body);
		
	}

	private void passWordmodify(int actorId, String loginPw) {
		memberDao.passWordmodify(actorId, loginPw);

		attrService.remove("member", actorId, "extra", "useTempPassword");
		
	}


	public String genCheckPasswordAuthCode(int actorId) {
		String authCode = UUID.randomUUID().toString();
		attrService.setValue("member__" + actorId + "__extra__modifyPrivateAuthCode", authCode, Util.getDateStrLater(60 * 60));

		return authCode;
	}

	public ResultData checkValidCheckPasswordAuthCode(int actorId, String checkPasswordAuthCode) {
		if (attrService.getValue("member__" + actorId + "__extra__modifyPrivateAuthCode").equals(checkPasswordAuthCode)) {
			return new ResultData("S-1", "유효한 키 입니다.");
		}

		return new ResultData("F-1", "유효하지 않은 키 입니다.");
	}

	public void infoModify(Map<String, Object> param) {
		memberDao.infoModify(param);
		
	}

	public void memberdelete(int id) {
		memberDao.delete(id);
		
	}

	public boolean isJoinableLoginId(String loginId) {
		return memberDao.isJoinableLoginId(loginId);
	}



}
