/**
 * 项目模块JS配置文件，主要完成该模块的加载数据，新增项目，删除项目和修改项目信息
 * @param $scope
 * @param $http
 * @param $state
 * zhp 2016.3.30
 */
function ProjectListController($scope, $http, $state) {
	
	$scope.currentPage = 1;
	$scope.pageSize = 10;
	
	$scope.search = function() {
		$http({
			method: 'GET',
			url: 'r/project',
			params: {'param': $scope.param, 'currentPage': $scope.currentPage, 'pageSize': $scope.pageSize}
		}).success(function(data){
			$scope.items = data.result;
			$scope.totalItems = data.totalCount;
		}).error(function(data){
			console.log(data);
		});
	}
    
	//加载执行,展示项目列表
    $scope.search();
    
    //加载Host(主机)信息
    $scope.loadHost = function() {
		$http({
			method: 'GET',
			url: 'r/project/getHost'
		}).success(function(data) {
			if(data) {
				var temps = new Array();
				$.each(data, function(index, item) {
					obj = {
					    label : item.name,
					    value : item.id
					};
					temps.push(obj); //载入数组，之后会赋值给select,页面html循环加载
				});
				
				//设置Select下拉框样式
				$('#hosts').multiselect({
					buttonClass: 'btn btn-sm btn-primary',
					enableFiltering: true,
			        includeSelectAllOption: true,
			        buttonWidth: '100%'
				});
				if(temps.length == 0) {
					$("#hosts").multiselect('disable');
				} else {
					$("#hosts").multiselect('dataprovider', temps);
				}
				
			}
		}).error(function(data){
			console.log(data);
		});
		
	}
	
    // ？？？ 尚不了解
	$scope.toInfo = function(id, historyLocation) {
		$state.go('project_detail.info', {id: id, historyLocation: historyLocation});
	}
	
	//提交表单，新增新项目，传入参数$scope.project
	$scope.addProject = function() {
		$http({
			async: false,
			method: 'POST',
			url: 'r/project',
			params: {'project': $scope.project}
		}).success(function(data){
			if(data == '200') {
				$('#addProjectModal').modal('hide');
				//加载、清空
				$scope.search();
				$scope.project = {};
			}
		}).error(function(data){
			console.log(data);
		});
	}
	
	//通过遍历checkbox，提交后台删除选中项
	$scope.delProject = function() {
		$scope.ids = '';
		$("input[name='selectId']:checked").each(function() {
			$scope.ids += ',' + $(this).val();
		});
		
		if($scope.ids != '') {
			//弹框提示，确认后提交删除
			swal({
	            title: "确定删除?",
	            text: "确定删除该任务",
	            type: "warning",
	            showCancelButton: true,
	            confirmButtonColor: "#DD6B55",
	            cancelButtonText: "取消",
	            confirmButtonText: "删除",
	            closeOnConfirm: false
	        }, function () {
	        	$http({
	    			method: 'DELETE',
	    			url: 'r/project',
	    			params: {'ids': $scope.ids}
	    		}).success(function(data) {
	    			if(data == '200') {
	    				swal("Deleted!", "项目已删除.", "success");
	    				$scope.search();
	    			}
	    		}).error(function(data){
	    			console.log(data);
	    		});
	        });
		}else{
			swal("","请选择要删除的项目","warning");
		}
    }
	
	//项目修改的响应函数
	$scope.loadProjectInfo = function() {
		
		$scope.id = '';
		var count = 0;
		
		//遍历选中修改项
		$("input[name='selectId']:checked").each(function() {
			$scope.id = $(this).val();
			count += 1;
		});
		if (count > 1) {
			swal("", "请选择一个项目进行修改", "warning");
			return false;
		}
		
		if ($scope.id != '') {
			//载入主机信息list
			$http({
				method: 'GET',
				url: 'r/project/getHost'
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
					
					//设置复选框样式
					$('#hostss').multiselect({
						buttonClass: 'btn btn-sm btn-primary',
						enableFiltering: true,
				        includeSelectAllOption: true,
				        buttonWidth: '100%'
					});
					if(temps.length == 0) {
						$("#hostss").multiselect('disable');
					} else {
						$("#hostss").multiselect('dataprovider', temps);
					}
				}
				
			}).error(function(data){
				console.log(data);
			});
			
			//加载修改前的项目数据
			$http({
				method : 'GET',
				url : 'r/project/info',
				params : {
					'id' : $scope.id
				}
			}).success(function(data) {
				//赋值给前台
				$scope.modifyproject = data; 
				console.log(data);
			}).error(function(data) {
				console.log(data);
			});
			
			$('#modifyProjectModal').modal('show');
			
		} else {
			swal("", "请选择要修改的项目", "warning");
		}
	}
	
	//提交修改后的项目信息
	$scope.modifyProject = function(){
		$http({
			async: false,
			method: 'PUT',
			url: 'r/project',
			params: {'project': $scope.modifyproject
			}
		}).success(function(data){
			if(data == '200') {
				$('#modifyProjectModal').modal('hide');
				$scope.search();
				$scope.modifyproject = {};
			}
		}).error(function(data){
			console.log(data);
		});
		
	}
	
}