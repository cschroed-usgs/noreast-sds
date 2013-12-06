<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@include file="/WEB-INF/base.jsp"%>

<div id="main_page" class="page_body_content">
	<div class="row">
		<div class="col-xs-2"></div>
		<div class="col-xs-8">
			<img src="${context}/img/noreast_logo_e.png" class="img-responsive noreast_logo" alt="NorEaST">
			<div class="input-group siteid_input_field">
			  <span class="input-group-addon">SiteID</span>
			  <input type="text" id="siteid" class="form-control" placeholder="SiteID">
			  <span class="input-group-btn">
			  	<button id="siteid_search" class="btn btn-default" type="button">
			  		<span class="glyphicon glyphicon-play"></span>
			  	</button>
			  </span>
			</div>
		</div>
		<div class="col-xs-2"></div>
	</div>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		$('#siteid').keypress(function(event) {
			if (event.keyCode == 13) {
				getSiteID();
			}
		});
		
		$('body').on('click', '#siteid_search', function (e) {
			getSiteID();
		});
	});
	
	var getSiteID = function() {
		var siteid = encodeURIComponent($('#siteid').val());
		
		if(siteid) {
			document.location.href='${context}/siteid/' + siteid;
		}
	};
</script>

