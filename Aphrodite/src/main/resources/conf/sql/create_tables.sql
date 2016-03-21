drop table if exists host;
CREATE TABLE
    host
    (
        id INT(10) unsigned NOT NULL AUTO_INCREMENT,
        groups varchar(2000),
        teamplates varchar(2000),
        zabbix_hostid bigint unsigned,
        name VARCHAR(64) COLLATE utf8_bin NOT NULL,
        type VARCHAR(32),
        os_type VARCHAR(32),
        os_version VARCHAR(64),
        ip1 VARCHAR(32),
        ip2 VARCHAR(32),
        ip3 VARCHAR(32),
        ip4 VARCHAR(32),
        vendor VARCHAR(64),
        model VARCHAR(64),
        cpu VARCHAR(64),
        disk VARCHAR(64),
        memory VARCHAR(64),
        sn VARCHAR(64),
        description VARCHAR(256),
        is_monitor TINYINT DEFAULT '0',
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    

drop table if exists app;
CREATE TABLE
    app
    (
        id INT(10) unsigned NOT NULL AUTO_INCREMENT,
        name VARCHAR(128) COLLATE utf8_bin NOT NULL,
        type VARCHAR(32),
        version VARCHAR(128),
        description VARCHAR(256),
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
    
drop table if exists host_app;
CREATE TABLE
    host_app
    (
        id INT(10) unsigned NOT NULL AUTO_INCREMENT,
        host_id INT NOT NULL,
        app_id INT NOT NULL,
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
    
drop table if exists salt_job;
CREATE TABLE
    salt_job
    (
        id INT(10) unsigned NOT NULL AUTO_INCREMENT,
        job_id VARCHAR(32),
        title VARCHAR(256),
        host_name VARCHAR(128),
        fun VARCHAR(32),
        return_value MEDIUMTEXT,
        arg VARCHAR(256),
        param VARCHAR(256),
        status VARCHAR(32) DEFAULT 'wait',
        deploy_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (id),
        INDEX idx_host_name (host_name)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
    
-- ----------------------------
-- Table structure for `script`
-- ----------------------------
DROP TABLE IF EXISTS `script`;
CREATE TABLE `script` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL COMMENT '脚本名称',
  `path` varchar(256) CHARACTER SET cp1250 DEFAULT NULL COMMENT '脚本路径',
  `type` varchar(32) DEFAULT NULL COMMENT '脚本类型',
  `content` text COMMENT '脚本内容',
  `description` varchar(256) DEFAULT NULL COMMENT '脚本描述',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '脚本状态（0-正常 1-作废）',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '脚本创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_script_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for `script_template`
-- ----------------------------
DROP TABLE IF EXISTS `script_template`;
CREATE TABLE `script_template` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(256) DEFAULT NULL COMMENT '模板名称',
  `type` varchar(32) DEFAULT NULL COMMENT '脚本类型',
  `content` text COMMENT '模板内容',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `os` varchar(256) DEFAULT NULL COMMENT '操作系统',
  `createtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` int(11) DEFAULT '0' COMMENT '模板状态(0-正常 1-作废）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
