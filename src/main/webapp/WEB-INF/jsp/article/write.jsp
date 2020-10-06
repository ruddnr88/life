<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="pageTitle" value="${board.name} 게시물 작성" />
<%@ include file="../part/head.jspf"%>
<%@ include file="../part/toastuiEditor.jspf"%>

<style>
.map_wrap {
	position: relative;
	width: 100%;
}
#addr {
	color: white;
	display: block;
	margin-top: 2px;
	font-weight: normal;
	display: block;
}
</style>

<script>
	function ArticleWriteForm__submit(form) {
		if (isNowLoading()) {
			alert('처리중입니다.');
			return;
		}

		form.address.value = form.address.value.trim();
		form.title.value = form.title.value.trim();
		if (form.title.value.length == 0) {
			form.title.focus();
			alert('제목을 입력해주세요.');

			return;
		}

		var bodyEditor = $(form).find('.toast-editor.input-body').data(
				'data-toast-editor');

		var body = bodyEditor.getMarkdown().trim();

		if (body.length == 0) {
			bodyEditor.focus();
			alert('내용을 입력해주세요.');

			return;
		}

		form.body.value = body;

		var maxSizeMb = 50;
		var maxSize = maxSizeMb * 1024 * 1024 //50MB

		if (form.file__article__0__common__attachment__1.value) {
			if (form.file__article__0__common__attachment__1.files[0].size > maxSize) {
				alert(maxSizeMb + "MB 이하의 파일을 업로드 해주세요.");
				return;
			}
		}

		if (form.file__article__0__common__attachment__2.value) {
			if (form.file__article__0__common__attachment__2.files[0].size > maxSize) {
				alert(maxSizeMb + "MB 이하의 파일을 업로드 해주세요.");
				return;
			}
		}

		if (form.file__article__0__common__attachment__3.value) {
			if (form.file__article__0__common__attachment__3.files[0].size > maxSize) {
				alert(maxSizeMb + "MB 이하의 파일을 업로드 해주세요.");
				return;
			}
		}

		var startUploadFiles = function(onSuccess) {
			var needToUpload = form.file__article__0__common__attachment__1.value.length > 0;

			if (!needToUpload) {
				needToUpload = form.file__article__0__common__attachment__2.value.length > 0;
			}

			if (!needToUpload) {
				needToUpload = form.file__article__0__common__attachment__3.value.length > 0;
			}

			if (needToUpload == false) {
				onSuccess();
				return;
			}

			var fileUploadFormData = new FormData(form);

			$.ajax({
				url : './../file/doUploadAjax',
				data : fileUploadFormData,
				processData : false,
				contentType : false,
				dataType : "json",
				type : 'POST',
				success : onSuccess
			});
		}

		startLoading();
		startUploadFiles(function(data) {
			var fileIdsStr = '';

			if (data && data.body && data.body.fileIdsStr) {
				fileIdsStr = data.body.fileIdsStr;
			}

			form.fileIdsStr.value = fileIdsStr;
			form.file__article__0__common__attachment__1.value = '';
			form.file__article__0__common__attachment__2.value = '';
			form.file__article__0__common__attachment__3.value = '';

			if (bodyEditor.inBodyFileIdsStr) {
				form.fileIdsStr.value += bodyEditor.inBodyFileIdsStr;
			}

			form.submit();
		});
	}
</script>

<form method="POST" name="article-write-form"
	class="table-box table-box-vertical con form1"
	action="${board.code}-doWrite"
	onsubmit="ArticleWriteForm__submit(this); return false;">
	<input type="hidden" name="fileIdsStr" /> <input type="hidden"
		name="body" /> <input type="hidden" name="address"> <input
		type="hidden" name="redirectUri"
		value="/usr/article/${board.code}-detail?id=#id">

	<table>
		<colgroup>
			<col class="table-first-col">
			<col />
		</colgroup>
		<tbody>
			<tr>
				<th>제목</th>
				<td>
					<div class="form-control-box">
						<input type="text" placeholder="제목을 입력해주세요." name="title"
							maxlength="100" />
					</div>
				</td>
			</tr>
			<tr>
				<th>현재주소</th>
				<td>
					<div class="map_wrap">
						<div id="map"
							style="width: 100%; height: 150%; position: relative; overflow: hidden;"></div>
						<div class="hAddr">
							<span id="address" name="address"></span>
						</div>
					</div> <script type="text/javascript"
						src="//dapi.kakao.com/v2/maps/sdk.js?appkey=a498e4602c5507aff78f05a92830f45f&libraries=services"></script>
					<script>
						var mapContainer = document.getElementById('map'), // 지도를 표시할 div 
						mapOption = {
							center : new kakao.maps.LatLng(37.566826,
									126.9786567), // 지도의 중심좌표
							level : 2
						// 지도의 확대 레벨
						};
						if (navigator.geolocation) {
							// GeoLocation을 이용해서 접속 위치를 얻어옵니다
							navigator.geolocation
									.getCurrentPosition(function(position) {
										var lat = position.coords.latitude, // 위도
										lon = position.coords.longitude; // 경도
										var locPosition = new kakao.maps.LatLng(
												lat, lon), // 마커가 표시될 위치를 geolocation으로 얻어온 좌표로 생성합니다
										message = '<div style="padding:5px;"여기에 계신가요?!></div>'; // 인포윈도우에 표시될 내용입니다
										// 마커와 인포윈도우를 표시합니다
										displayMarker(locPosition, message);
									});

						} else { // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정합니다

							var locPosition = new kakao.maps.LatLng(33.450701,
									126.570667), message = 'geolocation을 사용할수 없어요..'

							displayMarker(locPosition, message);
						}

						// 지도를 생성합니다    
						var map = new kakao.maps.Map(mapContainer, mapOption);

						// 지도에 마커와 인포윈도우를 표시하는 함수입니다
						function displayMarker(locPosition, message) {

							// 지도 중심좌표를 접속위치로 변경합니다
							map.setCenter(locPosition);
						}

						// 주소-좌표 변환 객체를 생성합니다
						var geocoder = new kakao.maps.services.Geocoder();

						var marker = new kakao.maps.Marker(), // 클릭한 위치를 표시할 마커입니다
						infowindow = new kakao.maps.InfoWindow({
							zindex : 1
						}); // 클릭한 위치에 대한 주소를 표시할 인포윈도우입니다

						// 현재 지도 중심좌표로 주소를 검색해서 지도 좌측 상단에 표시합니다
						searchAddrFromCoords(map.getCenter(), displayCenterInfo);
						// 중심 좌표나 확대 수준이 변경됐을 때 지도 중심 좌표에 대한 주소 정보를 표시하도록 이벤트를 등록합니다
						kakao.maps.event.addListener(map, 'idle', function() {
							searchAddrFromCoords(map.getCenter(),
									displayCenterInfo);
						});

						function searchAddrFromCoords(coords, callback) {
							// 좌표로 행정동 주소 정보를 요청합니다
							geocoder.coord2RegionCode(coords.getLng(), coords
									.getLat(), callback);
						}

						function searchDetailAddrFromCoords(coords, callback) {
							// 좌표로 법정동 상세 주소 정보를 요청합니다
							geocoder.coord2Address(coords.getLng(), coords
									.getLat(), callback);
						}

						// 지도 좌측상단에 지도 중심좌표에 대한 주소정보를 표출하는 함수입니다
						function displayCenterInfo(result, status) {
							if (status === kakao.maps.services.Status.OK) {
								var infoDiv = document
										.getElementById('address');

								for (var i = 0; i < result.length; i++) {
									// 행정동의 region_type 값은 'H' 이므로
									if (result[i].region_type === 'H') {
										infoDiv.innerHTML = result[i].address_name;
										document["article-write-form"].address.value = result[i].address_name;
										break;
									}
								}
							}
						}
					</script>
				</td>
			</tr>

			<tr>
				<th>내용</th>
				<td>
					<div class="form-control-box">
						<script type="text/x-template">
# 제목
![img](https://placekitten.com/200/287)
이미지는 이렇게 씁니다.

# 유투브 동영상 첨부

아래와 같이 첨부할 수 있습니다.

```youtube
https://youtu.be/pjaIu6LcdqI
```
                        </script>
						<div data-relTypeCode="article" data-relId="0"
							class="toast-editor input-body"></div>
					</div>
				</td>
			</tr>
			<c:forEach var="i" begin="1" end="3" step="1">
				<c:set var="fileNo" value="${String.valueOf(i)}" />
				<c:set var="fileExtTypeCode"
					value="${appConfig.getAttachmentFileExtTypeCode('article', i)}" />
				<tr>
					<th>첨부${fileNo}
						${appConfig.getAttachmentFileExtTypeDisplayName('article', i)}</th>
					<td>
						<div class="form-control-box">
							<input type="file"
								accept="${appConfig.getAttachemntFileInputAccept('article', i)}"
								name="file__article__0__common__attachment__${fileNo}">
						</div>
					</td>
				</tr>
			</c:forEach>
			<tr class="tr-do">
				<th>작성</th>
				<td>
					<button class="btn btn-primary" type="submit">작성</button> <a
					class="btn btn-info" href="${listUrl}">리스트</a>
				</td>
			</tr>
		</tbody>
	</table>
</form>
<%@ include file="../part/foot.jspf"%>