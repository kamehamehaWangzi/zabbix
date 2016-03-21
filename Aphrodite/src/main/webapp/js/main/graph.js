
/**
 * render graph by echarts
 * @param params 
 * {
 *  id: 'hostid',	
 *  graphDiv: 'chart render here', 
 *  theme: 'echarts theme', 
 *  title: 'graph title', 
 *  subTitle: 'graph subtitle', 
 *  key: ['item key1', 'item key2'], 
 *  likeSearch: true,
 *  formatter: 'yAxis value format',
 *  scaler: 1024,
 *  graphType: 'line'|'bar'|'stack'|'tiled'|'force'|'chord'|'pie'|'funnel',
 *  defaultDateRange: 1,
 *  dateRangeId: ''
 * }
 * 
 */
function renderGraph(params) {
	
	params.title = params.title ? params.title : '';
	params.theme = params.theme ? params.theme : 'macarons';
	params.subTitle = params.subTitle ? params.subTitle : '';
	params.likeSearch = params.likeSearch == undefined ? true : params.likeSearch;
	params.formatter = params.formatter ? params.formatter : '';
	params.scaler = params.scaler ? params.scaler : '';
	params.graphType = params.graphType ? params.graphType : 'line';
	params.startDate = params.startDate ? params.startDate : '';
	params.endDate = params.endDate ? params.endDate : '';
	params.defaultDateRange = params.defaultDateRange ? params.defaultDateRange : 1;
	
	
	$.ajax({
		url: 'r/graph',
		type: 'GET',
		dataType: "json",
		data: 'id=' + params.id + '&key=' + JSON.stringify(params.key) + '&likeSearch=' + params.likeSearch + '&graphType=' + params.graphType 
			+ '&scaler=' + params.scaler + '&startDate=' + params.startDate + '&endDate=' + params.endDate + '&defaultDateRange=' + params.defaultDateRange,
		success: function(data) {
			var myChart = echarts.init(document.getElementById(params.graphDiv), params.theme);
			
			var option = {
				    title : {
				        text: params.title,
				        subtext: params.subTitle
				    },
				    tooltip : {
				        trigger: 'axis'
				    },
				    legend: {
				    	y:'bottom',
				        data:data.legend
				    },
				    toolbox: {
				        show : true,
				        feature : {
				            mark : {show: true},
				            dataView : {show: true, readOnly: false},
				            magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
				            restore : {show: true},
				            saveAsImage : {show: true},
				            dataZoom : {
				                show : true,
				                title : {
				                    dataZoom : '区域缩放',
				                    dataZoomReset : '区域缩放后退'
				                }
				            },
				            dataView : {
				                show : true,
				                title : '数据视图',
				                readOnly: false,
				                lang: ['数据视图', '关闭', '刷新']
				            },
				        }
				    },
				    xAxis : [
				        {
				            type : 'category',
				            boundaryGap : false,
				            data : data.xAxis
				        }
				    ],
				    yAxis : [
				        {
				            type : 'value',
				            axisLabel : {
				                formatter: '{value} ' + params.formatter
				            }
				        }
				    ],
				    series : data.yAxis
				};
			
			myChart.setOption(option);
		},
		error: function(data) {
			console.log(data);
			alert('error');
		}
	});
	
	
	if(params.dateRangeId && params.dateRangeId != '') {
		var currentTime = new Date().getTime();
		
		$('#' + params.dateRangeId).daterangepicker({
			timePicker: true,
			timePicker24Hour: true,
			autoUpdateInput: true,
			showDropdowns: true,
			timePickerIncrement: 1,
			startDate: new Date(currentTime - (params.defaultDateRange * 3600 * 1000)).Format("yyyy-MM-dd hh:mm"),
			endDate: new Date(currentTime).Format("yyyy-MM-dd hh:mm"),
			locale: {
	            format: 'YYYY-MM-DD HH:mm'
	        }
		}, function(start, end, label) {
			params.startDate = start.format('YYYY-MM-DD HH:mm');
			params.endDate = end.format('YYYY-MM-DD HH:mm');
			renderGraph(params);
		});
	}
	
}

Date.prototype.Format = function(fmt) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    
    for (var k in o)
    	if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    
    return fmt;
}
