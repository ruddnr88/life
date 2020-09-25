<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="아이디/비번 찾기"></c:set>
<%@ include file="../part/head.jspf"%>

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/js-sha256/0.9.0/sha256.min.js"></script>
<script>
	var FindLoginIdForm__submitDone = false;
	function FindLoginIdForm__submit(form) {
		if (FindLoginIdForm__submitDone) {
			alert('처리중 입니다.');
			return;
		}

		form.name.value = form.name.value.trim();
		if (form.name.value.length == 0) {
			alert('이름을 입력해주세요.');
			form.name.focus();
			return;
		}
		form.email.value = form.email.value.trim();
		if (form.email.value.length == 0) {
			alert('이메일을 입력해주세요.');
			form.email.focus();
			return;
		}
		form.submit();
		FindLoginIdForm__submitDone = true;
	}

	var FindLoginPwForm__submitDone = false;
	function FindLoginPwForm__submit(form) {
		if (FindLoginPwForm__submitDone) {
			alert('처리중 입니다.');
			return;
		}
		form.loginId.value = form.loginId.value.trim();
		if (form.loginId.value.length == 0) {
			alert('아이디을 입력해주세요.');
			form.loginId.focus();
			return;
		}
		form.name.value = form.name.value.trim();
		if (form.name.value.length == 0) {
			alert('이름을 입력해주세요.');
			form.name.focus();
			return;
		}

		form.email.value = form.email.value.trim();
		if (form.email.value.length == 0) {
			alert('이메일을 입력해주세요.');
			form.email.focus();
			return;
		}
		form.submit();
		FindLoginPwForm__submitDone = true;
	}
</script>
<div class="tab_wrap con">
	<div class="tab_menu">
		<button class="tab_menu_btn on" type="button">아이디찾기</button>
		<button class="tab_menu_btn" type="button">비밀번호찾기</button>
	</div>
	<div class="tab_box_con">
		<div class="tab_box on">
			<form action="doFindLoginId" method="POST"
				class="table-box con form1"
				onsubmit="FindLoginIdForm__submit(this); return false;">
				<table>
					<colgroup>
						<col width="100">
					</colgroup>
					<tbody>
						<tr>
							<th>이름</th>
							<td>
								<div class="form-control-box">
									<input name="name" autofocus type="text"
										placeholder="이름을 입력해주세요." maxlength="30" autofocus="autofocus" />
								</div>
							</td>
						</tr>
						<tr>
							<th>이메일</th>
							<td>
								<div class="form-control-box">
									<input name="email" type="email" placeholder="이메일을 입력해주세요."
										maxlength="30" />
								</div>
							</td>
						</tr>
						<tr>
							<th>찾기</th>
							<td>
								<button class="btn btn-primary" type="submit">아이디찾기</button>
							</td>
						</tr>
					</tbody>
				</table>
			</form>
		</div>
		<div class="tab_box">
			<form action="doFindLoginPw" method="POST"
				class="table-box con form1"
				onsubmit="FindLoginPwForm__submit(this); return false;">
				<table>
					<colgroup>
						<col width="100">
					</colgroup>
					<tbody>
						<tr>
							<th>아이디</th>
							<td>
								<div class="form-control-box">
									<input name="loginId" autofocus type="text"
										placeholder="아이디를 입력해주세요." maxlength="30"
										autofocus="autofocus" />
								</div>
							</td>
						</tr>
		
						<tr>
							<th>이메일</th>
							<td>
								<div class="form-control-box">
									<input name="email" type="email" placeholder="이메일을 입력해주세요."
										maxlength="30" />
								</div>
							</td>
						</tr>
						<tr>
							<th>찾기</th>
							<td>
								<button class="btn btn-primary" type="submit">임시비번메일발송</button>
							</td>
						</tr>
					</tbody>
				</table>
			</form>
		</div>
	</div>
</div>
<script>
	$('.tab_menu_btn').on('click', function() {
		//버튼 색 제거,추가
		$('.tab_menu_btn').removeClass('on');
		$(this).addClass('on')

		//컨텐츠 제거 후 인덱스에 맞는 컨텐츠 노출
		var idx = $('.tab_menu_btn').index(this);

		$('.tab_box').hide();
		$('.tab_box').eq(idx).show();
	});
</script>


<%@ include file="../part/foot.jspf"%>