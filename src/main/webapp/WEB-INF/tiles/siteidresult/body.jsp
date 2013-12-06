<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@include file="/WEB-INF/base.jsp"%>

<!-- DatePicker Module -->
<script type="text/javascript" src="${context}/datepicker/moment.js"></script>
<script type="text/javascript" src="${context}/datepicker/daterangepicker.js"></script>
<link rel="stylesheet" type="text/css" href="${context}/datepicker/daterangepicker-bs3.css" />

<!-- HighCharts Charting Libary -->
<script src="${context}/highcharts/highstock.js"></script>
<script src="${context}/highcharts/exporting.js"></script>

<!-- Our HighCharts Themes -->
<script src="${context}/highcharts/chartthemes.js"></script>

<!-- Openlayers -->
<script src="${context}/openlayers/OpenLayers.js"></script>

<!-- Setting the chart start and end times -->
<script type="text/javascript">
	/* Chart Globals */
	var chart;
	var _start = '${phenomtime.beginTimeQueryString}';
	var _end = '${phenomtime.endTimeQueryString}';	
	var _startFormatted = '${phenomtime.beginTimePickerString}';
	var _endFormatted = '${phenomtime.endTimePickerString}';	
	var __loading__ = 0;
	
	/* Map Globals */
	//var locationMap, locationLayer;
</script>

<div id="main_page" class="page_body_content">
	<!-- EXPORT DATA MODAL -->
	<!-- ================================================================== -->
	<div class="modal fade" id="downloadDataModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="false">
		<div class="modal-dialog">
			<div class="modal-content" style="min-width: 436px;">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Export Chart Data</h4>
				</div>
				<div class="modal-body" style="margin-left: auto; margin-right: auto; display: table;">
					<p>
						Export data for Site ID: <b>${siteid}</b>
					</p>
					<dl class="dl-horizontal">
						<dt>Series:</dt>
						<dd id="modalSeriesName"></dd>
						<dt>Period:</dt>
						<dd id="modalSeriesTimePeriod"></dd>
					</dl>
					<div style="min-width: 403px">
						<button type="button" class="btn btn-primary" id="downloadCSV">Download CSV</button>
						<button type="button" class="btn btn-primary" id="downloadXML">Download XML</button>
						<button type="button" class="btn btn-primary" id="downloadJSON">Download JSON</button>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<!-- ================================================================== -->
	
	
	<div class="row">
		<div class="col-xs-4" style="min-width: 240px; margin-bottom: 5px; padding-right: 10px; margin-right: -10px;">
			<div class="row" style="margin-bottom: -15px;">
				<div class="col-xs-12" style="padding-left: 10px; padding-right: 10px;">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">Site Parameters</h3>
						</div>
						<div class="panel-body" id="seriesCheckboxes">
							<h6 style="margin-bottom: 15px; margin-top: 0px;">${siteid}</h6>
							<c:forEach var="result" items="${results}" varStatus="status">
								<div class="checkbox" style="font-size: 11px; margin-bottom: 12px;">
									<label>
										<input type="checkbox" id="checkbox_${status.index}" style="margin-top: 2px;" checked>
										${result.observedProperty} - ${result.offering}
										<br/>
										<span class="date_entry">${result.phenomenonTime.beginTimeString} - ${result.phenomenonTime.endTimeString}</span>
										<script type="text/javascript">
											$('body').on('click', '#checkbox_${status.index}', function (e) {
												toggleSeries('${result.observedProperty} - ${result.offering}');
											});
										</script>
									</label>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12" style="padding-left: 10px; padding-right: 10px; margin-bottom: -20px;">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">Site Location</h3>
						</div>
						<div class="panel-body" style="min-height: 273px;">
							<div style="font-size: 12px; font-weight: bold; margin-top: -5px;">
								${location.featureTitle}
							</div>
							<div style="font-size: 8px; font-style: italic; margin-left: 5px; margin-bottom: 5px;">
								${location.pointId}
							</div>
							<div id="locationMap" style="height: 214px;">
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="col-xs-8" style="min-width: 480px; padding-left: 10px; padding-right: 0px;">
			<div class="row" style="margin-bottom: -10px;">
				<div class="col-xs-12">
					<div class="panel panel-default" style="margin-bottom: 15px; min-width: 470px;">
						<div class="panel-heading">
							<h3 class="panel-title">
								Date Range
								<img id="loading" style="float: right; margin: -8px; padding: 0px; margin-left: 6px" src="${context}/img/loading.gif"/>
								<span style="font-size: 12px; font-style: italic; float: right; margin-top: 5px;">(default 1 week)</span>
							</h3>
						</div>
						<div class="panel-body" style="padding-left: 9px; padding-right: 9px;">
							<span class="glyphicon glyphicon-calendar"></span>
							<input type="text" style="width: 268px; font-size: 11px;" name="dateselection" id="dateselection"/>
							<button id="buildChart" type="button" class="btn btn-primary btn-xs" style="font-size: 11px; margin-top: -2px; margin-left: 0px;">Build Chart</button>
							<button id="resetChart" type="button" class="btn btn-default btn-xs" style="font-size: 11px; margin-top: -2px; margin-left: 0px;">Reset Chart</button>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12">
					<div id="chart_body" style="min-width: 470px"></div>
				</div>			
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		$('#dateselection').daterangepicker(
		    {
		      ranges: {
		         'Last 7 Days': [moment().subtract('days', 6), moment()],
		         'Last 30 Days': [moment().subtract('days', 29), moment()],
		         'This Month': [moment().startOf('month'), moment().endOf('month')],
		         'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')],
		         'Past Year': [moment().subtract('year', 1), moment()],
		         'Past 2 Years': [moment().subtract('year', 2), moment()],
		         'Past 5 Years': [moment().subtract('year', 5), moment()],
		         'Past 10 Years': [moment().subtract('year', 10), moment()]
		      }
		    },
		    function(start, end) {
		        $('#dateselection').val(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
		        
		        _start = start.format('YYYY-MM-DD[T]HH:mm:ss');
		        _end = end.format('YYYY-MM-DD[T]HH:mm:ss');
		        
		        _startFormatted = start.format('MM/DD/YYYY');
		        _endFormatted = end.format('MM/DD/YYYY');
		    }
		);
		
		/* With the date range picker in place, lets put the start and end dates in */
		$('#dateselection').val('${phenomtime.beginTimePickerString} - ${phenomtime.endTimePickerString}');
		
		/* Register our build chart button */
		$('body').on('click', '#buildChart', function (e) {
			$("#chart_body").empty();
			$("#loading").show();
			
			executeChart();
		});
		
		/* Register our reset chart button */
		$('body').on('click', '#resetChart', function (e) {
			$('#seriesCheckboxes').find(':checkbox').prop('checked', true);
			$("#chart_body").empty();
			$("#loading").show();
			
			_start = '${phenomtime.beginTimeQueryString}';
			_end = '${phenomtime.endTimeQueryString}';
			$('#dateselection').val('${phenomtime.beginTimePickerString} - ${phenomtime.endTimePickerString}');
			
			executeChart();
		});
		
		/* Build the location map */		
		createMap();
	});
	
	function createMap() {
		var locationMap, locationLayer, markerLayer;
				
		locationMap = new OpenLayers.Map('locationMap');
		
		/* BlueMarble looks awesome but not high enough resolution */
		/*
		locationLayer = new OpenLayers.Layer.WMS(
                "Global Imagery",
                "http://maps.opengeo.org/geowebcache/service/wms",
                {layers: "bluemarble"}
          );
		*/
		
		locationLayer = new OpenLayers.Layer.OSM("", null, {
										transitionEffect: "resize",
										attribution: ""
		});
		
		markerLayer = new OpenLayers.Layer.Markers(
			'SiteMarkers', {
				projection: new OpenLayers.Projection("EPSG:26949")
			}
		);
		
		var layersToAdd = [];
		layersToAdd.push(locationLayer);
		layersToAdd.push(markerLayer);
		
		locationMap.addLayers(layersToAdd);
		
		var siteLonLat = new OpenLayers.LonLat(${location.longitude}, ${location.latitude}).transform(
			new OpenLayers.Projection("EPSG:4326"), locationMap.getProjectionObject()
		);
		
		var makeIcon = function() {
			var size = new OpenLayers.Size(21,25);
			var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
			var icon = new OpenLayers.Icon('${context}/openlayers/img/marker.png', size, offset);
		
			return icon;
		};
		
		markerLayer.addMarker(new OpenLayers.Marker(siteLonLat, makeIcon()));
		locationMap.setCenter(siteLonLat, 10);
	}
	
	function maintainLoading(increment) {
		if(increment) {
			__loading__++;
		} else {
			__loading__--;
			
			if(__loading__ == 0) {
				$("#loading").hide();
			}
		}
	}
	
	function toggleSeries(seriesName) {
		var series = ($.grep(chart.series, function(e){ return e.name === seriesName; }))[0];
		
		if(series.visible) {
			series.hide();
		} else {
			series.show();
		}
	}
	
	function maintainSeriesViewable(seriesName, seriesIndex) {
		var series = ($.grep(chart.series, function(e){ return e.name === seriesName; }))[0];
		
		if($('#checkbox_' + seriesIndex).is(':checked')) {
			series.show();
		} else {
			series.hide();
		}
	}
	
	function downloadSeriesData(seriesName, seriesURI, startDate, endDate) {
		$('#modalSeriesName').text(seriesName);
		$('#modalSeriesTimePeriod').text(startDate + ' - ' + endDate);		
		$('#downloadDataModal').modal('show');
		
		$('#downloadCSV').unbind('click');
		$('#downloadXML').unbind('click');
		$('#downloadJSON').unbind('click');
		
		$('body').on('click', '#downloadCSV', function (e) {
			window.open(seriesURI + "csv?download=true&numeric=false");
		});
		
		$('body').on('click', '#downloadXML', function (e) {
			window.open(seriesURI + "xml?download=true&numeric=false");
		});
		
		$('body').on('click', '#downloadJSON', function (e) {
			window.open(seriesURI + "json?download=true&numeric=false");
		});
	}
	
	/* Temporary placement for our chart code */
	function executeChart() {		
		$(function() {
			var _title = 'Site Summary Data - ${siteid}';
			var options = {
				chart: {
					renderTo: 'chart_body'
				},
				
				title : {
					text : _title
				}
			};
	
			chart = new Highcharts.StockChart(Highcharts.merge(options, bluetheme));
			
			/* We have to see if we already added the export options as resetting */
			/* or rebuilding the chart does not clear them */
			var exports = $.grep(chart.options.exporting.buttons.contextButton.menuItems, function(e){ return (typeof e.chartid != 'undefined') });
			if(exports.length > 0) {
				$.each(exports, function(i, item) {
					//chart.options.exporting.buttons.contextButton.menuItems.splice(item);
					chart.options.exporting.buttons.contextButton.menuItems.splice( $.inArray(item, chart.options.exporting.buttons.contextButton.menuItems), 1 );
				});
			} else {
				chart.options.exporting.buttons.contextButton.menuItems.push({
					separator:	true
				});
			}
			
			var queries = [];
			<c:forEach var="result" items="${results}" varStatus="status">
				var queryName = '${result.observedProperty} - ${result.offering}';
				var queryURI = '${context}/dataproxy/${siteid}/${result.observedProperty}/' +
				  	  		'${result.offering}/' + _start +
				  	  		'/' + _end + '/';
				queries.push(
					{
						title: 	queryName,
						queryURI:	queryURI + 'xml',
						index:	${status.index}
					}
				);
				
				chart.options.exporting.buttons.contextButton.menuItems.push({
					chartid: queryURI,
					text: 'Export "' + queryName + '" data',
					onclick: function () {
				        downloadSeriesData(queryName, queryURI, _startFormatted, _endFormatted);
				    }
				});
			</c:forEach>
			
			chart.showLoading();
			$("#loading").show();
			
			$.each(queries, function(i, query) {
				maintainLoading(true);
				
				var resultdata = {
					name: query.title,
					data: []
				};
				
				var queryData = [];
				
				$.get(query.queryURI, function(data) {
					// Split the lines
				    	var $xml = $(data);
									    	
					$xml.find('result').each(function(i, measurement) {
					    var tvResult = [
					            parseInt($(measurement).find('time').text()),
					            parseInt($(measurement).find('value').text())
					    ];
					    queryData.push(tvResult);
					});
					
					resultdata.data = queryData.reverse();
					chart.hideLoading();
					
					chart.addSeries(resultdata);
				}).done(function() {
					maintainLoading(false);
					maintainSeriesViewable(query.title, query.index);
				});
			});			
	    	});
	}
	
	executeChart();
</script>