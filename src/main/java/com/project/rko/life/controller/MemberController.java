package com.project.rko.life.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.rko.life.dto.Member;
import com.project.rko.life.dto.ResultData;
import com.project.rko.life.service.MemberService;
import com.project.rko.life.util.Util;

@Controller
public class MemberController {
	@Autowired
	private MemberService memberService;

	// 회원가입이동
	@RequestMapping("/usr/member/join")
	public String showjoin() {
		return "/member/join";
	}

	// 회원가입
	@RequestMapping("/usr/member/doJoin")
	public String doJoin(@RequestParam Map<String, Object> param, Model model) {
		Util.changeMapKey(param, "loginPwReal", "loginPw");
		ResultData checkLoginIdJoinableResultData = memberService
				.checkLoginIdJoinable(Util.getAsStr(param.get("loginId")));

		if (checkLoginIdJoinableResultData.isFail()) {
			model.addAttribute("historyBack", true);
			model.addAttribute("alertMsg", checkLoginIdJoinableResultData.getMsg());
			return "/home/main";
		}

		int newMemberId = memberService.join(param);

		String redirectUri = (String) param.get("redirectUri");
		model.addAttribute("redirectUri", redirectUri);
		model.addAttribute("alertMsg", String.format("회원가입을 축하합니다."));

		return "/home/main";
	}

	// 아이디 중복체크~!
	@RequestMapping("/usr/member/getLoginIdDup")
	@ResponseBody
	private ResultData doLoginIdDup(HttpServletRequest request) {

		String isJoinableLoginId = request.getParameter("loginId");

		return memberService.checkLoginIdJoinable(isJoinableLoginId);
	}

	// 로그인이동
	@RequestMapping("/usr/member/login")
	public String showLogin() {
		return "/member/login";
	}

	// 로그인
	@RequestMapping("/usr/member/doLogin")
	public String doLogin(String loginId, String loginPwReal, String redirectUri, Model model, HttpSession session) {
		String loginPw = loginPwReal;
		Member member = memberService.getMemberByLoginId(loginId);

		if (member == null) {
			model.addAttribute("historyBack", true);
			model.addAttribute("alertMsg", "존재하지 않는 회원입니다.");
			return "common/redirect";
		}

		if (member.isDelStatus() == true) {
			model.addAttribute("historyBack", true);
			model.addAttribute("alertMsg", "탈퇴한 회원의 아이디입니다.");
			return "common/redirect";

		}

		if (member.getLoginPw().equals(loginPw) == false) {
			model.addAttribute("historyBack", true);
			model.addAttribute("alertMsg", "비밀번호가 일치하지 않습니다.");
			return "common/redirect";
		}

		// 로그인유지
		session.setAttribute("loginedMemberId", member.getId());

		boolean isNeedToChangePasswordForTemp = memberService.isNeedToChangePasswordForTemp(member.getId());
		if (isNeedToChangePasswordForTemp) {
			model.addAttribute("alertMsg", "현재 임시패스워드를 사용중입니다. 비밀번호를 변경해주세요.");
		}

		if (redirectUri == null || redirectUri.length() == 0) {

			redirectUri = "/usr/home/main";
		}

		model.addAttribute("redirectUri", redirectUri);
		model.addAttribute("alertMsg", String.format("%s님 반갑습니다.", member.getNickname()));

		return "common/redirect";
	}

	// 로그아웃
	@RequestMapping("/usr/member/doLogout")
	public String doLogout(HttpSession session, Model model, String redirectUri) {
		session.removeAttribute("loginedMemberId");

		if (redirectUri == null || redirectUri.length() == 0) {
			redirectUri = "/usr/home/main";
		}

		model.addAttribute("redirectUri", redirectUri);
		model.addAttribute("alertMsg", String.format("로그아웃되었습니다."));
		return "common/redirect";
	}

	// 아이디비번찾기로 이동
	@RequestMapping("/usr/member/findAccount")
	public String showfindAccount() {
		return "/member/findAccount";
	}

	// 아이디찾기
	@RequestMapping("/usr/member/doFindLoginId")
	public String doFindLoginId(String name, String email, HttpSession session, Model model, String redirectUri) {
		Member member = memberService.getMemberByNameAndEmail(name, email);

		if (member == null) {
			model.addAttribute("historyBack", true);
			model.addAttribute("alertMsg", "존재하지 않는 회원입니다.");
			return "common/redirect";
		}

		if (redirectUri == null || redirectUri.length() == 0) {
			redirectUri = "/usr/home/main";
		}

		model.addAttribute("redirectUri", redirectUri);
		model.addAttribute("alertMsg", String.format("일치하는 회원을 찾았습니다. \\n아이디 : " + member.getLoginId()));
		return "common/redirect";
	}

	// 비밀번호찾기(임시비밀번호메일)
	@RequestMapping("/usr/member/doFindLoginPw")
	public String doFindLoginPw(String loginId, String name, String email, HttpSession session, Model model,
			String redirectUri) {
		Member member = memberService.getMemberByLoginId(loginId);

		if (member == null || member.getEmail().equals(email) == false) {
			model.addAttribute("historyBack", true);
			model.addAttribute("alertMsg", "존재하지 않는 회원입니다.");
			return "common/redirect";
		}

		if (redirectUri == null || redirectUri.length() == 0) {
			redirectUri = "/usr/home/main";
		}

		memberService.notifyTempLoginPw(member);

		model.addAttribute("redirectUri", redirectUri);
		model.addAttribute("alertMsg", String.format("가입하신 메일로 임시 패스워드가 발송되었습니다."));
		return "common/redirect";
	}

	// 정보수정가기전 비밀번호 재확인으로 이동
	@RequestMapping("/usr/member/checkPassword")
	public String showPasswordForPrivate() {
		return "/member/checkPassword";
	}

	// 비밀번호 재확인(정보수정)
	@RequestMapping("/usr/member/doCheckPassword")
	public String doCheckPassword(String loginPwReal, HttpServletRequest req, Model model, String redirectUri) {
		String loginPw = loginPwReal;
		Member loginedMember = (Member) req.getAttribute("loginedMember");

		if (loginedMember.getLoginPw().equals(loginPw) == false) {
			model.addAttribute("historyBack", true);
			model.addAttribute("alertMsg", "비밀번호가 일치하지 않습니다.");
			return "common/redirect";
		}
		String authCode = memberService.genCheckPasswordAuthCode(loginedMember.getId());

		if (redirectUri == null || redirectUri.length() == 0) {
			redirectUri = "/usr/member/modify";
		}
		redirectUri = Util.getNewUri(redirectUri, "checkPasswordAuthCode", authCode);

		model.addAttribute("redirectUri", redirectUri);

		return "common/redirect";

	}

	// 정보수정이동
	@RequestMapping("/usr/member/modify")
	public String showModify(HttpSession session, Model model, HttpServletRequest req, String checkPasswordAuthCode) {
		int loginedMemberId = (int) req.getAttribute("loginedMemberId");
		ResultData checkValidCheckPasswordAuthCodeResultData = memberService
				.checkValidCheckPasswordAuthCode(loginedMemberId, checkPasswordAuthCode);

		if (checkPasswordAuthCode == null || checkPasswordAuthCode.length() == 0) {
			model.addAttribute("historyBack", true);
			model.addAttribute("alertMsg", "비밀번호 체크 인증코드가 없습니다.");
			return "common/redirect";
		}

		if (checkValidCheckPasswordAuthCodeResultData.isFail()) {
			model.addAttribute("historyBack", true);
			model.addAttribute("alertMsg", checkValidCheckPasswordAuthCodeResultData.getMsg());
			return "common/redirect";
		}

		return "member/modify";
	}

	// 내정보수정하기
	@RequestMapping("/usr/member/doModify")
	public String doModify(@RequestParam Map<String, Object> param, Model model, HttpServletRequest req,
			HttpSession session) {
		Util.changeMapKey(param, "loginPwReal", "loginPw");

		int loginedMemberId = (int) req.getAttribute("loginedMemberId");
		param.put("id", loginedMemberId);
		memberService.infoModify(param);

		String redirectUri = (String) param.get("redirectUri");
		model.addAttribute("redirectUri", redirectUri);

		session.removeAttribute("loginedMemberId");

		if (redirectUri == null || redirectUri.length() == 0) {
			redirectUri = "/usr/home/main";
		}

		model.addAttribute("redirectUri", redirectUri);
		model.addAttribute("alertMsg", String.format("회원정보가 수정되어 로그아웃되었습니다. 다시 로그인해주세요."));

		return "common/redirect";
	}

	// 회원탈퇴
	@RequestMapping("/usr/member/doDelete")
	public String doDelete(HttpSession session, Model model, String redirectUri, HttpServletRequest req) {
		int memberId = (int) req.getAttribute("loginedMemberId");
		session.removeAttribute("loginedMemberId");
		memberService.memberdelete(memberId);

		if (redirectUri == null || redirectUri.length() == 0) {
			redirectUri = "/usr/home/main";
		}
		model.addAttribute("redirectUri", redirectUri);
		model.addAttribute("alertMsg", String.format("회원탈퇴 되었습니다."));
		return "common/redirect";
	}

}
