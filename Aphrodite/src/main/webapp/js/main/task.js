/**
 * 项目模块JS配置文件，主要完成该模块的加载数据，新增项目，删除项目和修改项目信息
 * @param $scope
 * @param $http
 * @param $state
 * zhp 2016.3.30
 */
function TaskListController($scope, $http, $state) {
	
	$scope.currentPage = 1;
	$scope.pageSize = 10;
	
	$scope.task = {};
	
	//发送任务查询请求
	$scope.search = function() {
		$http({
			method: 'GET',
			url: 'r/task',
			params: {'param': $scope.param, 'currentPage': $scope.currentPage, 'pageSize': $scope.pageSize}
		}).success(function(data){
			$scope.items = data.result;
			$scope.totalItems = data.totalCount;
		}).error(function(data){
			console.log(data);
		});
	}
	
	//加载执行,展示任务列表
	$scope.search();
	
	//加载项目信息
    $scope.loadProject = function() {
		$http({
			method: 'GET',
			url: 'r/task/getProject'
		}).success(function(data) {
			if(data) {
				var temps = new Array();
				$.each(data, function(index, item) {
					obj = {
					    label : item.name,
					    value : item.id
					};
					temps.push(obj);
				});
				
				$scope.projects = temps;
				$scope.task.project = temps[0].value;
				
				$('#hosts').multiselect({
					buttonClass: 'btn btn-sm btn-primary',
					enableFiltering: true,
			        includeSelectAllOption: true,
			        buttonWidth: '100%'
				});
				$scope.loadHost('addModel','');
			}
		}).error(function(data){
			console.log(data);
		});
	}
    
  //根据项目Id($scope.task.project),加载Host(主机)信息
	$scope.loadHost = function(loadClass,preHostsId) {
		console.log(loadClass);
		
		var proValue = '';
		if(loadClass == 'addModel'){
			proValue = $scope.task.project;
		}else if(loadClass == 'modifyModel'){
			proValue = $scope.modifyTask.project;
			if(preHostsId=='' || preHostsId==null || preHostsId=='[null]'){
				preHostsId = $scope.modifyTask.hosts;
			}
		}
		
    	$http({
			method: 'GET',
			params: {'projectId': proValue},
			url: 'r/task/getHost'
		}).success(function(data) {
			if(data) {
				var temps = new Array();
				$.each(data, function(index, item) {
					obj = {
					    label : item.name,
					    value : item.id
					};
					temps.push(obj);
				});
				
				if(loadClass == 'addModel'){
					if(temps.length == 0) {
						$("#hosts").multiselect('disable');
					} else {
						$("#hosts").multiselect('dataprovider', temps);
					}
				}else if(loadClass == 'modifyModel'){
					if(temps.length == 0) {
						$("#hostss").multiselect('disable');
					} else {
						$("#hostss").multiselect('dataprovider', temps);
					}
				}
			}
		}).error(function(data){
			console.log(data);
		});
    	
    }
    
    
    //修改-加载要修改的任务信息
    $scope.loadTaskInfo = function(){
    	$scope.id = '';
    	var count = 0;
    	$("input[name='selectId']:checked").each(function(){
    		$scope.id = $(this).val();
    		count += 1;
    	});
    	if(count>1){
    		swal("","请选择一个任务进行修改","warning");
    		return false;
    	}
    	if($scope.id != ''){
	    	
    		//暂存选中project信息，方便在select选中
    		var preProjectId = ''; 
    		var preHostsId = '';
    		//请求task信息
    		$http({
	    		method:"GET",
	    		url:'r/task/info',
	    		params:{"taskId": $scope.id}
	    	}).success(function(data){
	    		$scope.modifyTask = data;
	    		
	    		preProjectId = $scope.modifyTask.project;
	    		preHostsId = $scope.modifyTask.hosts;
	    		
	    		//修改页面“项目”下拉框，数据加载
	    		$http({
	    			method: 'GET',
	    			url: 'r/task/getProject'
	    		}).success(function(data) {
	    			if(data) {
	    				var temps = new Array();
	    				$.each(data, function(index, item) {
	    					obj = {
	    							label : item.name,
	    							value : item.id
	    					};
	    					temps.push(obj);
	    				});
	    				
	    				$scope.projectss = temps;
	    				//设置选中值
	    				$.each(temps,function(index,item){
	    					if(item.value==preProjectId){
	    						$scope.modifyTask.project = item.value;
	    					}
	    				});

	    				//设置select的样式
	    				$('#hostss').multiselect({
	    					buttonClass: 'btn btn-sm btn-primary',
	    					enableFiltering: true,
	    					includeSelectAllOption: true,
	    					buttonWidth: '100%'
	    				});
	    				
	    				//根据项目信息，加载该项目的主机信息
	    				$scope.loadHost('modifyModel',preHostsId);
	    				
	    			}
	    		}).error(function(data){
	    			console.log(data);
	    		});
	    	});
	    	
	    	$('#modifyTaskModal').modal('show');
    	}else{
    		swal("", "请选择要修改的任务", "warning");
    	}
    }
    
    //增加任务提交函数
    $scope.addTask = function() {
		$http({
			async: false,
			method: 'POST',
			url: 'r/task',
			params: {'task': $scope.task}
		}).success(function(data){
			if(data == '200') {
				$('#addTaskModal').modal('hide');
				$scope.search();
				$scope.task = {};
			}
		}).error(function(data){
			console.log(data);
		});
	}
    
    //提交修改的项目信息
    $scope.modifyTaskInfo = function() {
		$http({
			async: false,
			method: 'PUT',
			url: 'r/task',
			params: {'task': $scope.modifyTask}
		}).success(function(data){
			if(data == '200') {
				$('#modifyTaskModal').modal('hide');
				$scope.search();
				$scope.modifyTask = {};
			}
		}).error(function(data){
			console.log(data);
		});
	}
    		
    //删除任务信息
	$scope.delTask = function() {
		$scope.ids = '';
		$("input[name='selectId']:checked").each(function() {
			$scope.ids += ',' + $(this).val();
		});
		
		if($scope.ids != '') {
			swal({
	            title: "确定删除?",
	            text: "删除项目及项目下所有任务",
	            type: "warning",
	            showCancelButton: true,
	            confirmButtonColor: "#DD6B55",
	            cancelButtonText: "取消",
	            confirmButtonText: "删除",
	            closeOnConfirm: false
	        }, function () {
	        	$http({
	    			method: 'DELETE',
	    			url: 'r/task',
	    			params: {'ids': $scope.ids}
	    		}).success(function(data) {
	    			if(data == '200') {
	    				swal("Deleted!", "任务已删除.", "success");
	    				$scope.search();
	    			}
	    		}).error(function(data){
	    			console.log(data);
	    		});
	        });
		}
    }
	
}