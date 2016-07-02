package org.pbccrc.platform.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
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
	 * @return -1:用户名密码不正确；  1部署成功
	 */
	public int deployAgent(String hostIp, String userName, String password) {

		String path = request.getSession().getServletContext().getRealPath(Constant.ZABBIX_SOURCE_PATH);
		try {
			// 连接到主机
			conn = new Connection(hostIp, port);
			conn.connect();
			boolean isconn = conn.authenticateWithPassword(userName, password);
			if (!isconn) {
				System.out.println("用户名或者密码不正确！");
				return -1; //用户名密码不正确
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
						+ "./zabbix_agentd -c /usr/local/zabbix/etc/zabbix_agentd.conf;pwd;"
						+ "firewall-cmd --zone=public --add-port=10050/tcp --permanent;" //针对CentOs7
						+ "firewall-cmd --reload;"
						+ "/sbin/iptables -I INPUT -p tcp --dport 10050 -j ACCEPT;"//针对CentOs6.5
						+ "/etc/rc.d/init.d/iptables save;"
						+ "ls;";
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
		return 1;
	}

	@Test
	public void testPort(){
		
		int result = this.testIsUserdPort("192.168.62.111", "root", "root123", "10050");
		Assert.assertEquals(1, result);
	}
	
	/**
	 * 检测远程机器端口是否被占用
	 * @param hostIp
	 * @param userName
	 * @param password
	 * @param zabbixAgentPort
	 * @return -1：用户名密码不正确；0：端口未被占用；1：端口被占用
	 */
	public int testIsUserdPort(String hostIp, String userName, String password, String zabbixAgentPort){
		int result = 0;
		Connection conn = new Connection(hostIp, port);
		try {
			conn.connect();
			boolean auth = conn.authenticateWithPassword(userName, password);
			if(!auth){
				System.out.println("用户名或者密码不正确！");
				return -1;
			}else{
				// 执行远程命令
				String testStr = "netstat -anp | grep '"+zabbixAgentPort+"'";
				Session testSession = conn.openSession();
				testSession.execCommand(testStr);

				// 获取远程Terminal屏幕上的输出并打印出来
				InputStream is = new StreamGobbler(testSession.getStdout());
				BufferedReader brs = new BufferedReader(new InputStreamReader(is));

				while (true) {
					String line = brs.readLine();
					if (line == null) {
						break;
					}
//					if(line.contains("zabbix")){
					result = 1;
//					}
					System.out.println(line);
				}

				// 关闭连接对象
				if (testSession != null) {
					testSession.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		return result;
	}
	
	/**
	 * 修改配置文件的端口
	 * @param filePath
	 * @param fileName
	 * @param port
	 * @return
	 */
	public String modifyAgent(String filePath, String fileName,String port){
		
		String outFile = fileName+".modify";
		File testIn = new File(filePath+fileName);
		File testOut = new File(filePath+outFile);
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		
		try {
			
			reader = new BufferedReader(new FileReader(testIn));
			writer = new BufferedWriter(new FileWriter(testOut));
			String tempString = null;
			int line = 1;
			//一次读入一行，知道读入null为文件结束
			while((tempString = reader.readLine())!=null){
				
				if(!tempString.replace(" ", "").startsWith("#") && tempString.replace(" ", "").contains("ListenPort=") ){
					writer.write("ListenPort="+port);
				} else {
					writer.write(tempString);
				}
				
				line++;
				writer.newLine();
			} 
			writer.flush();
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader!=null){
				try{
					reader.close();
				}catch(IOException e1){
				}
			}
		}
		
		return outFile;
	}
	
//	public static void main(String[] args) {
//		DeployAgentUtil util = new DeployAgentUtil();
//		util.deployAgent("192.168.62.164", "root", "root123");
//	}
}
