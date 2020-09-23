<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="pageTitle" value="${board.name} 게시판" />
<%@ include file="../part/head.jspf"%>

<!-- PC용 -->
<div class="con">
<a class="btn btn-primary" href="./${board.code}-list">목록</a>
	<div class="serchbox flex flex-jc-sb">
	<div class= "tocount" ><p>총 게시물수 : ${totalCount}<p></div>
	<div class="flex w_bnt_sbar">
		<div class="searchbar">
			<form action="" name="searchForm">
				<select name="searchType" class="search sch">
					<option value="title">제목</option>
					<option value="body">내용</option>
					<option value="titleAndBody">제목+내용</option>
				</select>
				<script>
					if ( typeof param.searchType == 'undefined' ) {
						param.searchType = 'title';
					}
					
					$('form[name="searchForm"] select[name="searchType"]').val(param.searchType);
				</script>
				<input type="hidden" name="page" value="1" />
				<input type="text" name="searchKeyword" value="${param.searchKeyword}" placeholder="검색어를 입력하세요" class="search">
				<button class="search-btn">
					<i class="fa fa-search"></i>
				</button>
			</form>
		</div>
	</div>
	</div>
	
</div>
<div class="table-box con visible-on-md-up">

	<table>
		<colgroup>
			<col width="100" />
			<col width="200" />
		</colgroup>
		<thead>
			<tr>
				<th style="text-align: center;">번호</th>
				<th>날짜</th>
				<th>제목</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${articles}" var="article">
				<tr>
					<td style="text-align: center;">${article.id}</td>
					<td>${article.regDate}</td>
					<td>
						<a href="${article.getDetailLink(board.code)}">${article.forPrintTitle}</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
		
</div>

<!-- 모바일 용 -->
<div class="table-box con visible-on-sm-down">
	<table>
		<thead>
			<tr>
				<th style="display: inline-block;">번호</th>
				<th style="display: inline-block;" >제목</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${articles}" var="article">
				<tr>
					<td style="font-weight: bold;">${article.id}</td>
					<td>
						<a href="${article.getDetailLink(board.code)}">${article.forPrintTitle}</a>
						<br />
						날짜 : ${article.regDate}
						<br />
						작성 : ${article.extra.writer}
						
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
<ul class="flex flex-jc-c listing">
			<c:forEach var="i" begin="1" end="${totalPage}" step="1">
			<li class="${i == cPage ? 'current' : ''}"><a
				href="?page=${i}"  >${i}</a></li>
			</c:forEach>

		</ul>
<div class="btn-box con margin-top-20 flex flex-jc-sb">
	
	<a class="btn btn-primary" href="./${board.code}-write">글쓰기</a>
	
</div>

<%@ include file="../part/foot.jspf"%>