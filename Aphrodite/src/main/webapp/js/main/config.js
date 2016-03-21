/**
 * Aphrodite
 *
 * Aphrodite theme use AngularUI Router to manage routing and views
 * Each view are defined as state.
 * Initial there are written state for all view in theme.
 *
 */
function config($stateProvider, $urlRouterProvider, $ocLazyLoadProvider, $controllerProvider, IdleProvider, KeepaliveProvider) {
	
	$controllerProvider.allowGlobals();
	
    // Configure Idle settings
    IdleProvider.idle(5); // in seconds
    IdleProvider.timeout(120); // in seconds
    
    $urlRouterProvider.otherwise("/monitor");
    
    $ocLazyLoadProvider.config({
        // Set to true if you want to see what and when is dynamically loaded
        debug: true
    });
    
    $stateProvider
    
	    .state('monitor', {
			url: '/monitor',
			templateUrl: 'overview/dashboard.html'
		})
    
	    .state('machine', {
			url: '/machine',
			templateUrl: 'monitor/machine/list.html',
			resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/bootstrap-multiselect.js', 'js/libs/sweetalert/sweetalert.min.js' , 'js/libs/icheck/icheck.min.js']
                        }
                    ]);
                }
            }
		})
	
		.state('machine_detail', {
			url: '/machine/:id/:hostId',
			templateUrl : 'monitor/machine/tab.html',
			resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js', 'js/libs/echarts/echarts-all.js', 'js/main/graph.js' ]
                        }
                    ]);
                }
            }
		})
		
		.state('machine_detail.info', {
	    	url: '/info',
	    	views: {
	            'machine': {
	                templateUrl: 'monitor/machine/info.html'
	            }
	    	}
	    })
	    .state('machine_detail.cpu', {
        	url: '/cpu',
        	views: {
	            'machine': {
	                templateUrl: 'monitor/machine/cpu.html'
	            }
        	}
        })
        
        .state('machine_detail.memory', {
        	url: '/memory',
        	views: {
	            'machine': {
	                templateUrl: 'monitor/machine/memory.html'
	            }
        	}
        })
        
        .state('machine_detail.disk', {
        	url: '/disk',
        	views: {
	            'machine': {
	                templateUrl: 'monitor/machine/disk.html'
	            }
        	}
        })
        
        .state('machine_detail.swap', {
        	url: '/swap',
        	views: {
	            'machine': {
	                templateUrl: 'monitor/machine/swap.html'
	            }
        	}
        })
        
        .state('machine_detail.network', {
        	url: '/network',
        	views: {
	            'machine': {
	                templateUrl: 'monitor/machine/network.html'
	            }
        	}
        })
        
        .state('machine_detail.app', {
        	url: '/app',
        	views: {
	            'machine': {
	                templateUrl: 'monitor/machine/app.html'
	            }
        	}
        })
        
        .state('template', {
			url: '/template',
			templateUrl: 'monitor/template/list.html',
			resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/bootstrap-multiselect.js', 'js/libs/sweetalert/sweetalert.min.js' , 'js/libs/icheck/icheck.min.js']
                        }
                    ]);
                }
            }
		})
		
		.state('template_trigger', {
        	url: '/template/:id/trigger',
        	templateUrl: 'monitor/template/trigger.html',
        	resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/bootstrap-multiselect.js', 'js/libs/sweetalert/sweetalert.min.js', 'js/libs/icheck/icheck.min.js' ]
                        }
                    ]);
                }
            }
        })
		
        .state('template_host', {
        	url: '/template/:id/host',
        	templateUrl: 'monitor/template/host.html',
        	resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/bootstrap-multiselect.js', 'js/libs/sweetalert/sweetalert.min.js', 'js/libs/icheck/icheck.min.js' ]
                        }
                    ]);
                }
            }
        })
		
		.state('network', {
			url: '/network',
            templateUrl : 'monitor/network/list.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/bootstrap-multiselect.js', 'js/libs/sweetalert/sweetalert.min.js' , 'js/libs/icheck/icheck.min.js']
                        }
                    ]);
                }
            }
        })
        
        .state('network_route', {
        	url: '/network/route/:id/:hostId',
            templateUrl : 'monitor/network/route/tab.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js', 'js/libs/echarts/echarts-all.js', 'js/main/graph.js' ]
                        }
                    ]);
                }
            }
        })
        
        .state('network_switch', {
        	url: '/network/switch/:id/:hostId',
            templateUrl : 'monitor/network/switch/tab.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js', 'js/libs/echarts/echarts-all.js', 'js/main/graph.js' ]
                        }
                    ]);
                }
            }
        })
        
        .state('network_firewall', {
        	url: '/network/firewall/:id/:hostId',
            templateUrl: 'monitor/network/firewall/tab.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js', 'js/libs/echarts/echarts-all.js', 'js/main/graph.js' ]
                        }
                    ]);
                }
            }
        })
         .state('network_loadbalance', {
        	url: '/network/loadbalance/:id/:hostId',
            templateUrl: 'monitor/network/loadbalance/tab.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js', 'js/libs/echarts/echarts-all.js', 'js/main/graph.js' ]
                        }
                    ]);
                }
            }
        })
        
        .state('network_storage', {
        	url: '/network/storage/:id/:hostId',
            templateUrl : 'monitor/network/storage/tab.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js', 'js/libs/echarts/echarts-all.js', 'js/main/graph.js' ]
                        }
                    ]);
                }
            }
        })
        
        .state('app', {
        	url: '/app',
            templateUrl : 'monitor/application/list.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/sweetalert/sweetalert.min.js', 'js/libs/icheck/icheck.min.js' ]
                        }
                    ]);
                }
            }
        })
        .state('app_hosts', {
        	url: '/app/:type/:id',
            templateUrl : 'monitor/application/host_list.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/bootstrap-multiselect.js', 'js/libs/sweetalert/sweetalert.min.js', 'js/libs/icheck/icheck.min.js' ]
                        }
                    ]);
                }
            }
        })
        .state('app_mysql', {
        	url: '/app/mysql/:id/:hostId',
            templateUrl : 'monitor/application/mysql/tab.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js', 'js/libs/echarts/echarts-all.js', 'js/main/graph.js' ]
                        }
                    ]);
                }
            }
        })
        .state('app_oracle', {
        	url: '/app/oracle/:id/:hostId',
            templateUrl : 'monitor/application/oracle/tab.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js', 'js/libs/echarts/echarts-all.js', 'js/main/graph.js' ]
                        }
                    ]);
                }
            }
        })
        .state('app_tomcat', {
        	url: '/app/tomcat/:id/:hostId',
            templateUrl : 'monitor/application/tomcat/tab.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js', 'js/libs/echarts/echarts-all.js', 'js/main/graph.js' ]
                        }
                    ]);
                }
            }
        })
        .state('app_weblogic', {
        	url: '/app/weblogic/:id/:hostId',
            templateUrl : 'monitor/application/weblogic/tab.html',
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js', 'js/libs/echarts/echarts-all.js', 'js/main/graph.js' ]
                        }
                    ]);
                }
            }
        })
        
        .state('app_mysql.info', {
	    	url: '/info',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/info.html'
	            }
	    	}
	    })
        .state('app_mysql.bandwidth', {
	    	url: '/bandwidth',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/mysql/bandwidth.html'
	            }
	    	}
	    })
	    .state('app_mysql.operation', {
	    	url: '/operation',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/mysql/operation.html'
	            }
	    	}
	    })
	    
	    .state('app_oracle.info', {
	    	url: '/info',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/info.html'
	            }
	    	}
	    })
	    .state('app_oracle.io', {
	    	url: '/io',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/oracle/io.html'
	            }
	    	}
	    })
	    .state('app_oracle.hit', {
	    	url: '/hit',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/oracle/hit.html'
	            }
	    	}
	    })
	    .state('app_oracle.session', {
	    	url: '/session',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/oracle/session.html'
	            }
	    	}
	    })
	    .state('app_oracle.memory', {
	    	url: '/memory',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/oracle/memory.html'
	            }
	    	}
	    })
	    .state('app_oracle.other', {
	    	url: '/other',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/oracle/other.html'
	            }
	    	}
	    })
	    
	    .state('app_tomcat.info', {
	    	url: '/info',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/info.html'
	            }
	    	}
	    })
	    .state('app_tomcat.classLoader', {
	    	url: '/classLoader',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/tomcat/class_loader.html'
	            }
	    	}
	    })
	    .state('app_tomcat.heapMemory', {
	    	url: '/heapMemory',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/tomcat/heap_memory.html'
	            }
	    	}
	    })
	    .state('app_tomcat.thread', {
	    	url: '/thread',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/tomcat/thread.html'
	            }
	    	}
	    })
	    .state('app_tomcat.session', {
	    	url: '/session',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/tomcat/session.html'
	            }
	    	}
	    })
	    .state('app_tomcat.gc', {
	    	url: '/gc',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/tomcat/gc.html'
	            }
	    	}
	    })
	    .state('app_tomcat.accesslog', {
	    	url: '/accesslog',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/tomcat/accesslog.html'
	            }
	    	},
	    	resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js']
                        }
                    ]);
                }
            }
	    })
	    .state('app_tomcat.errorlog', {
	    	url: '/errorlog',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/tomcat/errorlog.html'
	            }
	    	},
	    	resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js']
                        }
                    ]);
                }
            }
	    })
	    
	    .state('app_weblogic.info', {
	    	url: '/info',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/info.html'
	            }
	    	}
	    })
	    .state('app_weblogic.gc', {
	    	url: '/gc',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/weblogic/gc.html'
	            }
	    	}
	    })
        .state('app_weblogic.thread', {
	    	url: '/thread',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/weblogic/thread.html'
	            }
	    	}
	    })
	    .state('app_weblogic.classLoader', {
	    	url: '/classLoader',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/weblogic/class_loader.html'
	            }
	    	}
	    })
	    .state('app_weblogic.connectionPool', {
	    	url: '/connection_pool',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/weblogic/connection_pool.html'
	            }
	    	}
	    })
	    .state('app_weblogic.memoryPool', {
	    	url: '/memory_pool',
	    	views: {
	            'app': {
	                templateUrl: 'monitor/application/weblogic/memory_pool.html'
	            }
	    	}
	    })
	    
	     .state('deploy', {
			url: '/deploy',
			templateUrl: 'deploy/deploy_task.html'
		})
	    .state('alarm', {
			url: '/alarm',
			templateUrl: 'monitor/alert/alarm_status.html'
		})				
		
		.state('alarm_action', {
			url: '/alarm_action',
			templateUrl: 'monitor/alert/action.html'
		})
		
	    
		.state('script_execute', {
			url: '/script_execute',
			templateUrl: 'script/script_execute.html',
			resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/bootstrap-multiselect.js', 'js/libs/sweetalert/sweetalert.min.js', 'js/libs/icheck/icheck.min.js','js/libs/codemirror/codemirror.js','js/libs/codemirror/mode/shell/shell.js' ]
                        }
                    ]);
                }
            }
		})
		.state('script_template', {
			url: '/script_template',
			templateUrl: 'script/script_template.html',
			resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/bootstrap-multiselect.js', 'js/libs/sweetalert/sweetalert.min.js', 'js/libs/icheck/icheck.min.js' ]
                        }
                    ]);
                }
            }
		})
		.state('cobbler_system', {
			url: '/cobbler_system',
			templateUrl: 'cobbler/cobbler_system.html',
			resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/bootstrap-multiselect.js', 'js/libs/sweetalert/sweetalert.min.js', 'js/libs/icheck/icheck.min.js' ]
                        }
                    ]);
                }
            }
		})
		
		.state('network_firewall.info', {
	    	url: '/info',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/firewall/info.html'
	            }
	    	}
	    })
		.state('network_firewall.cpu', {
	    	url: '/cpu',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/firewall/cpu.html'
	            }
	    	}
	    })
	    .state('network_firewall.memory', {
	    	url: '/memory',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/firewall/memory.html'
	            }
	    	}
	    })
	    .state('network_firewall.session', {
	    	url: '/session',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/firewall/session.html'
	            }
	    	}
	    })
	    .state('network_firewall.temperature', {
	    	url: '/temperature',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/firewall/temperature.html'
	            }
	    	}
	    })
	    .state('network_firewall.port', {
	    	url: '/port',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/port.html'
	            }
	    	}
	    })
	    .state('network_firewall.octets', {
	    	url: '/octets',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/octets.html'
	            }
	    	}
	    })
	    .state('network_firewall.package', {
	    	url: '/package',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/package.html'
	            }
	    	}
	    })
	    	.state('network_loadbalance.info', {
	    	url: '/info',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/loadbalance/info.html'
	            }
	    	}
	    })
		.state('network_loadbalance.cpu', {
	    	url: '/cpu',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/loadbalance/cpu.html'
	            }
	    	}
	    })
	    .state('network_loadbalance.memory', {
	    	url: '/memory',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/loadbalance/memory.html'
	            }
	    	}
	    })
	     .state('network_loadbalance.connection', {
	    	url: '/connection',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/loadbalance/connection.html'
	            }
	    	}
	    })
	    .state('network_loadbalance.port', {
	    	url: '/port',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/port.html'
	            }
	    	}
	    })
	    .state('network_loadbalance.octets', {
	    	url: '/octets',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/octets.html'
	            }
	    	}
	    })
	    .state('network_loadbalance.package', {
	    	url: '/package',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/package.html'
	            }
	    	}
	    })
	    
	    .state('network_switch.info', {
	    	url: '/info',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/switch/info.html'
	            }
	    	}
	    })
	    
	    .state('network_switch.cpu', {
	    	url: '/cpu',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/switch/cpu.html'
	            }
	    	}
	    })
	    
	    .state('network_switch.package', {
	    	url: '/package',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/package.html'
	            }
	    	}
	    })
	    
	    .state('network_switch.memory', {
	    	url: '/memory',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/switch/memory.html'
	            }
	    	}
	    })	    	   
	    
	    .state('network_switch.port', {
	    	url: '/port',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/port.html'
	            }
	    	}
	    })
	    
	    .state('network_switch.deviceinfo', {
	    	url: '/deviceinfo',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/switch/deviceinfo.html'
	            }
	    	}
	    })
	    
	    .state('network_switch.octets', {
	    	url: '/octets',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/octets.html'
	            }
	    	}
	    })
	    
	    .state('network_route.info', {
	    	url: '/info',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/route/info.html'
	            }
	    	}
	    })	    	    
	    
	    .state('network_route.cpu', {
	    	url: '/cpu',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/route/cpu.html'
	            }
	    	}
	    })
	    
	    .state('network_route.memory', {
	    	url: '/memory',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/route/memory.html'
	            }
	    	}
	    })
	    
	    .state('network_route.temperature', {
	    	url: '/temperature',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/route/temperature.html'
	            }
	    	}
	    })
	    
	    .state('network_route.octets', {
	    	url: '/octets',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/octets.html'
	            }
	    	}
	    })
	    
	    .state('network_route.package', {
	    	url: '/package',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/package.html'
	            }
	    	}
	    })
	    
	    .state('network_route.port', {
	    	url: '/port',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/port.html'
	            }
	    	}
	    })
	    
	    .state('network_storage.info', {
	    	url: '/info',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/storage/info.html'
	            }
	    	}
	    })
	    
	    .state('network_storage.disk', {
	    	url: '/disk',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/storage/disk.html'
	            }
	    	}
	    }) 
    
	    .state('network_storage.controller', {
	    	url: '/controller',
	    	views: {
	            'network': {
	                templateUrl: 'monitor/network/storage/controller.html'
	            }
	    	}
	    })  
    
	    .state('project', {
			url: '/project',
			templateUrl: 'project/project/list.html',
			resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/bootstrap-multiselect.js', 'js/libs/sweetalert/sweetalert.min.js' , 'js/libs/icheck/icheck.min.js']
                        }
                    ]);
                }
            }
		})
		
		.state('project_detail', {
			url: '/project/:id',
			templateUrl : 'project/project/tab.html',
			resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/moment.min.js', 'js/libs/bootstrap/daterangepicker.js', 'js/libs/echarts/echarts-all.js', 'js/main/graph.js' ]
                        }
                    ]);
                }
            }
		})
		
		.state('project_detail.info', {
	    	url: '/info',
	    	views: {
	            'project': {
	                templateUrl: 'project/project/info.html'
	            }
	    	}
	    })
	    
	    .state('project_detail.host', {
	    	url: '/info',
	    	views: {
	            'project': {
	                templateUrl: 'project/project/host.html'
	            }
	    	}
	    })
	    
	     .state('task', {
			url: '/task',
			templateUrl: 'project/task/list.html',
			resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            files: [ 'js/libs/bootstrap/bootstrap-multiselect.js', 'js/libs/sweetalert/sweetalert.min.js' , 'js/libs/icheck/icheck.min.js']
                        }
                    ]);
                }
            }
		})
    
}
angular
    .module('aphrodite')
    .config(config)
    .run(function($rootScope, $state) {
        $rootScope.$state = $state;
    });
