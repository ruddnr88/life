<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="pageTitle" value="소개" />
<%@ include file="../part/head.jspf"%>

<!DOCTYPE html>
<html>
<head>
<style>
#todaysWeather {
width:50%;
height: 350px;
}
</style>
<script type="text/javascript"
	src="//dapi.kakao.com/v2/maps/sdk.js?appkey=a498e4602c5507aff78f05a92830f45f&libraries=services"></script>
<meta charset="UTF-8">
<script>
function parseWeather() {
	loadJSON(function(response)
	{
		var jsonData = JSON.parse(response);
		document.getElementById("todaysWeather").innerHTML = jsonData["list"][0]["weather"][0]["main"];
	});
}
function loadJSON(callback) //url의 json 데이터 불러오는 함수
	{
		var url = "http://api.openweathermap.org/data/2.5/weather?q=Daejeon&appid=6865f718422968f7d65db254c1bef24a";
		var request = new XMLHttpRequest();
		request.overrideMimeType("application/json");
		request.open('GET', url, true);
		request.onreadystatechange = function()
		{
			if (request.readyState == 4 && request.status == "200")
			{
				callback(request.responseText);
			}
		};
		request.send(null);
}
window.onload = function(){
	parseWeather();
}
</script>

</head>
<body>
	<div class="con">
		<h1>우리는 실시간 날씨가 필요합니다.</h1>
		<p style="color: white;">
			지구온난화로 인해 지구의 기상 변화는 시시각각 변합니다. <br> 최고의 조건과 기술력이 있다고 한들, 한시 앞도
			알아볼 수 없는 날씨의 변화, 실시간으로 현재 위치에 있는 사용자가 자신의 날씨를 공유해보세요.
		</p>
		<span id ="todaysWeather"></span>

