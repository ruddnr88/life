<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="pageTitle" value="메인" />
<%@ include file="../part/head.jspf"%>

<!DOCTYPE html>
<html>
<head>
<script type="text/javascript"
	src="//dapi.kakao.com/v2/maps/sdk.js?appkey=a498e4602c5507aff78f05a92830f45f&libraries=services"></script>
<meta charset="UTF-8">
<style>
.map_wrap {
	position: relative;
	width: 100%;
	height: 350px;
}

.title {
	font-weight: bold;
	display: block;
}

.hAddr {
	position: absolute;
	left: 10px;
	top: 10px;
	border-radius: 2px;
	background: #fff;
	background: rgba(255, 255, 255, 0.8);
	z-index: 1;
	padding: 5px;
}

#centerAddr {
	display: block;
	margin-top: 2px;
	font-weight: normal;
}

.bAddr {
	padding: 5px;
	text-overflow: ellipsis;
	overflow: hidden;
	white-space: nowrap;
}
</style>
<script>
	$(function() {
		// Geolocation API에 액세스할 수 있는지를 확인
		if (navigator.geolocation) {
			//위치 정보를 얻기
			navigator.geolocation.getCurrentPosition(function(pos) {
				var lat = pos.coords.latitude, // 위도
				lon = pos.coords.longitude; // 경도
			});
		} else {
			alert("이 브라우저에서는 Geolocation이 지원되지 않습니다.")
		}

		var geocoder = new kakao.maps.services.Geocoder();

		var callback = function(result, status) {
			if (status === kakao.maps.services.Status.OK) {

				console.log('지역 명칭 : ' + result[0].address_name);
				console.log('행정구역 코드 : ' + result[0].code);
			}
		};

		geocoder.coord2RegionCode(lat, lon, callback);
	});
</script>
</head>
<body>
	<div class="con">
		<ul>
			<li>위도: <span id="lat"></span></li>
			<li>경도: <span id="lon"></span></li>
			<span id="centerAddr"></span>
		</ul>