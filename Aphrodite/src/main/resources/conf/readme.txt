1. init cmdb database
2. zabbix trends API support code patch
3. zabbix-server install orabbix, java-gateway plugin

/etc/salt/master:
job_cache: True
keep_jobs: 24

salt token saved to ${cachedir}/tokens/    ex: /var/cache/salt/master/tokens/

mysql远程连接缓慢:
[mysqld]
#禁止域名解析
skip-name-resolve