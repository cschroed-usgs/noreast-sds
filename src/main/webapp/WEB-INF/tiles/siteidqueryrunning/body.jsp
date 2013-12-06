<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@include file="/WEB-INF/base.jsp"%>

<div id="main_page" class="page_body_content">
	<div class="row">
		<div class="col-xs-4"></div>
		<div class="col-xs-4">
			<div class="progress progress-striped active" style="margin-top: 180px;">
				<div class="progress-bar" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">
					<span style="margin-left: auto; margin-right: auto;">Query Running</span>
				</div>
			</div>
		</div>
		<div class="col-xs-4"></div>
	</div>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		// Check status every second  
		var timer = setInterval(function() {
			//document.location.href='${context}/query/${uuid}';
			$.get( "${context}/query/${siteid}", function( data ) {
				if(data === 'running') {
					// do nothing
				}
				else {
					// Query not running so lets redirect to where it tells us
					clearInterval(timer);
					document.location.href='${context}' + data;
				}
			});
		}, 1000);
	});
</script>