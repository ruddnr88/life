<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="회원정보수정" />
<%@ include file="../part/head.jspf"%>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/js-sha256/0.9.0/sha256.min.js"></script>
<script>
	function MemberModifyForm__submit(form) {
		if (form.loginPw.value.length > 0) {
			if (form.loginPw.value.length == 0) {
				form.loginPw.focus();
				alert('로그인 비밀번호를 입력해주세요.');

				return;
			}

			if (form.loginPw.value.length < 4) {
				form.loginPw.focus();
				alert('로그인 비밀번호를 4자 이상 입력해주세요.');

				return;
			}

			if (form.loginPwConfirm.value.length == 0) {
				form.loginPwConfirm.focus();
				alert('로그인 비밀번호 확인을 입력해주세요.');

				return;
			}

			if (form.loginPw.value != form.loginPwConfirm.value) {
				form.loginPwConfirm.focus();
				alert('로그인 비밀번호 확인이 일치하지 않습니다.');

				return;
			}
		}

		form.name.value = form.name.value.trim();

		if (form.name.value.length == 0) {
			form.name.focus();
			alert('이름을 입력해주세요.');

			return;
		}

		form.nickname.value = form.nickname.value.trim();

		if (form.nickname.value.length == 0) {
			form.nickname.focus();
			alert('닉네임을 입력해주세요.');

			return;
		}

		form.email.value = form.email.value.trim();

		if (form.email.value.length == 0) {
			form.email.focus();
			alert('이메일을 입력해주세요.');

			return;
		}

		if (form.loginPw.value.length > 0) {
			form.loginPwReal.value = sha256(form.loginPw.value);
		}

		form.loginPw.value = '';
		form.submit();

	}
</script>
<!-- 메인컨텐츠 -->
<div class="con info-con">
	<form action="doModify" method="POST"
		class="table-box table-box-vertical form1 modi-form"
		onsubmit="MemberModifyForm__submit(this); return false;">
		<input type="hidden" name="redirectUri" value="/usr/home/main">
		<input type="hidden" name="loginPwReal">
		<table>
			<colgroup>
				<col width="120">
			</colgroup>
			<tbody>
				<tr>
					<th>아이디</th>
					<td>
						<div class="form-control-box">${loginedMember.loginId}</div>
					</td>
				</tr>
				<tr>
					<th>비밀번호변경(선택)</th>
					<td>
						<div class="form-control-box">
							<input type="password" placeholder="새 로그인 비밀번호를 입력해주세요."
								name="loginPw" maxlength="30" />
						</div>
					</td>
				</tr>
				<tr>
					<th>비밀번호 변경확인(선택)</th>
					<td>
						<div class="form-control-box">
							<input type="password" placeholder="새 로그인 비밀번호 확인을 입력해주세요."
								name="loginPwConfirm" maxlength="30" />
						</div>
					</td>
				</tr>
				<tr>
					<th>이름</th>
					<td>
						<div class="form-control-box">
							<input type="text" placeholder="이름을 입력해주세요." name="name"
								maxlength="20" value="${loginedMember.name.trim()}" />
						</div>
					</td>
				</tr>
				<tr>
					<th>닉네임</th>
					<td>
						<div class="form-control-box">
							<input type="text" placeholder="활동명 입력해주세요." name="nickname"
								maxlength="20" value="${loginedMember.nickname.trim()}" />
						</div>
					</td>
				</tr>
				<tr>
					<th>이메일</th>
					<td>
						<div class="form-control-box">
							<input type="email" placeholder="이메일 입력해주세요." name="email"
								maxlength="50" value="${loginedMember.email.trim()}" />
						</div>
					</td>
				</tr>
				<tr class="tr-do">
					<td colspan="2" style="text-align: center;">
						<button type="submit">수정</button>
						<button class="red-btn" type="button" onclick="history.back();">취소</button>
					</td>
				</tr>

			</tbody>
		</table>
		<div style="text-align: center;" onclick="if ( confirm('정말 탈퇴하시겠습니까?') == false ) return false;">
			<p class="message">
				회원탈퇴 하시겠습니까? <a href="doDelete"> 회원탈퇴</a>
			</p>
		</div>
	</form>
</div>

<%@ include file="../part/foot.jspf"%>
