package org.pbccrc.platform.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * zabbix agent 部署工具类 zhp 2016.06.14
 */
@Service
public class DeployAgentUtil {

	@Autowired
	private HttpServletRequest request;
	
	private int port = 22;
	private Connection conn = null;
	private Session session = null;
	
	// 生成一个待压缩的文件夹downfile，待返回链接供前端下载
	
	/**
	 * 私有方法->直供自己调用 在远程客户端执行commStr命令
	 * 
	 * @param commonStr 待执行的命令行
	 * @throws IOException
	 */
	private void runCommonByConsole(String commonStr) throws IOException {

		// 执行远程命令
		session = conn.openSession();
		session.execCommand(commonStr);

		// 获取远程Terminal屏幕上的输出并打印出来
		InputStream is = new StreamGobbler(session.getStdout());
		BufferedReader brs = new BufferedReader(new InputStreamReader(is));

		while (true) {
			String line = brs.readLine();
			if (line == null) {
				break;
			}
			System.out.println(line);
		}

		// 关闭连接对象
		if (session != null) {
			session.close();
		}

	}

	/**
	 * zabbix agent 部署方法 在当前远程环境，部署zabbix_agent
	 * 
	 * @param hostIp
	 *            客户机IP
	 * @param port
	 *            客户机端口默认 22
	 * @param userName
	 *            客户机用户
	 * @param password
	 *            客户机密码
	 */
	public void deployAgent(String hostIp, String userName, String password) {

		String path = request.getSession().getServletContext().getRealPath(Constant.ZABBIX_SOURCE_PATH);
		try {
			// 连接到主机
			conn = new Connection(hostIp, port);
			conn.connect();
			boolean isconn = conn.authenticateWithPassword(userName, password);
			if (!isconn) {
				System.out.println("用户名或者密码不正确！");
			} else {

				/* 步骤1. 将zabbix.tar.gz文件发送到agent服务器 */
				System.out.println("开始传送zabbix安装包……");
				SCPClient clt = conn.createSCPClient();
				clt.put(path+"/zabbix-2.2.6.tar.gz", "/home/");

				/* 步骤2. 配置执行命令，zabbix 编译 -> 安装 -> 配置 */
				System.out.println("开始执行zabbix的编译，安装，配置……");
				String installStr = "cd /home;groupadd zabbix&&useradd zabbix -g zabbix -s /bin/false;"
						+ "yum -y install gcc gcc-c++ kernel-devel;"
						+ "cd /home;tar -zxvf zabbix-2.2.6.tar.gz && cd zabbix-2.2.6/;"
						+ "./configure --prefix=/usr/local/zabbix --enable-agent && make install;"
						+ "cd /usr/local/zabbix && mkdir logdata && cd logdata&&touch zabbix_agentd.log";
				runCommonByConsole(installStr);

				/* 步骤3. 将配置文件agentd.conf覆盖到agent服务器 */
				System.out.println("开始覆盖客户机的配置文件……");
				clt.put(path+"/zabbix_agentd.conf", "/usr/local/zabbix/etc/");

				/* 步骤4. 配置端口，serverIP, 启动agent监控 */
				System.out.println("启动zabbix agent 的服务");
				String startStr = "cd /usr/local/;" + "chmod -R 777 zabbix/;" + "cd /usr/local/zabbix/sbin;"
						+ "./zabbix_agentd -c /usr/local/zabbix/etc/zabbix_agentd.conf;" + "iptables -F;";
				runCommonByConsole(startStr);
				System.out.println("********************************************");
				System.out.println("***       zabbix agent 启动完成！                  ***");
				System.out.println("********************************************");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

//	public static void main(String[] args) {
//		DeployAgentUtil util = new DeployAgentUtil();
//		util.deployAgent("192.168.62.164", "root", "root123");
//	}
}
