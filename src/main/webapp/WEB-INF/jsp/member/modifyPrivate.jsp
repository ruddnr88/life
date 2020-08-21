<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../part/head.jspf"%>
<script	src="https://cdnjs.cloudflare.com/ajax/libs/js-sha256/0.9.0/sha256.min.js"></script>
<script>
	function ModifyPrivateForm__submit(form) {
		form.loginPw.value = form.loginPw.value.trim();
		if (form.loginPw.value.length == 0) {
			alert('비밀번호를 입력해주세요.');
			form.loginPw.focus();
			return;
		}
		form.loginPwConfirm.value = form.loginPwConfirm.value.trim();
		if (form.loginPwConfirm.value.length == 0) {
			alert('비밀번호 확인을 입력해주세요.');
			form.loginPwConfirm.focus();
			return;
		}
		if (form.loginPw.value != form.loginPwConfirm.value) {
			alert('비밀번호가 일치하지 않습니다.');
			form.loginPwConfirm.focus();
			return;
		}
		
		form.loginPwReal.value = sha256(form.loginPw.value);
		form.loginPw.value = '';
		form.submit();

	}
</script>
<!-- 메인컨텐츠 -->
<div class="con info-con">
	<h1>회원정보</h1>
	<form action="doModifyForPrivate" method="POST" class="sign-form form1"
		onsubmit="ModifyPrivateForm__submit(this); return false;">
		<div class="member_info">
			<div class="a">아이디 : ${loginedMember.loginId}</div>
			<input type="hidden" name="authCode" value="${param.authCode}"/>
			<input type="hidden" name="loginPwReal">
			<div class="form-row">
				<div class="label">새 비밀번호 : </div>
				<div class="input">
					<input name="loginPw" class="box-form" type="password"
						placeholder="비밀번호를 입력해주세요." />
				</div>
			</div>
			<div class="form-row">
				<div class="label">새 비번 확인 : </div>
				<div class="input">
					<input name="loginPwConfirm" class="box-form" type="password"
						placeholder="비밀번호 확인을 입력해주세요." />
				</div>
			</div>
			
			<div class="a">이름 : ${loginedMember.name}</div>
			<div class="a">닉네임 : ${loginedMember.nickname}</div>
			<div class="a">가입날짜 : ${loginedMember.regDate}</div>
			<div class="a">이메일 : ${loginedMember.email}</div>
		</div>
		<div class="con_butt sign_butt" style="margin-top: 50px; width:100%;">
			<div class="input">
				<input type="submit" style="right:18%;" value="확인" class="login_but lb_2"/>
				<a href="${pageContext.request.contextPath}/s/home/main" class="login_but lb_3">취소</a>
			</div>
		</div>
	</form>

</div>

<%@ include file="../part/foot.jspf"%>
