package org.pbccrc.platform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Component;

@Component
public class FTPClientUtil {
	
	
	private Log log = LogFactory.getLog(getClass());

	private FTPClient ftpClient = null;



	public void connect() throws Exception {
		try {
			connect(Constant.SALT_FTP_HOST, Constant.SALT_FTP_USERNAME,
					Constant.SALT_FTP_PASSWD);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("can't connect to FTP server", e);
			throw new Exception(e);
		}
	}

	public void connect(String host, String username, String password)
			throws Exception {
		ftpClient = new FTPClient();

		try {
			ftpClient.connect(host);
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				log.debug("refuse to connect to ftp server");
				throw new Exception(" refuse to connect to ftp server");
			}

			log.debug("connect to ftp server success");
			if (username != null) {
				if (!ftpClient.login(username, password)) {
					ftpClient.disconnect();
					log.debug("ftp server login fail，please check username and password");
					throw new Exception(
							"ftp server login fail，please check username and password");
				}
				log.debug("ftp server login success！");
			}
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
		} catch (SocketException e) {
			e.printStackTrace();
			log.error("can't connect to FTP server", e);
			throw new Exception(e);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("ftp server login fail", e);
			throw new Exception(e);
		}
	}

	public void close() throws Exception {
		if (ftpClient != null && ftpClient.isConnected()) {
			try {
				if (ftpClient.logout()) {// 退出FTP服务器
					log.debug("FTP server logout success");
				}
			} catch (IOException e) {
				e.printStackTrace();
				log.error("FTP server logout fail");
				throw new Exception(e);
			} finally {
				try {
					ftpClient.disconnect();// 关闭FTP服务器的连接
					log.debug("close FTP server connect success！");
				} catch (IOException e) {
					e.printStackTrace();
					log.error("close ftp server connect fail！");
					throw new Exception(e);
				}
			}
		}
	}

	public FTPFile[] getFTPFiles(String pathname) throws Exception {
		try {

			changeWorkingDirectory(pathname);
			FTPFile[] ftpFiles = ftpClient.listFiles(pathname);
			return ftpFiles;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new Exception(e);
		}
	}

	public boolean changeWorkingDirectory(String pathname) throws Exception {
		try {
			pathname = new String(pathname.getBytes(),
					ftpClient.getControlEncoding());
			return ftpClient.changeWorkingDirectory(pathname);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new Exception(e);
		}
	}

	public boolean delRemoteFile(String pathname,String filename) throws Exception {
		try {
			connect();
			
			FTPFile[] ftpfiles=getFTPFiles(pathname);
			
			for(FTPFile ftpfile:ftpfiles){
				if(ftpfile.getName().equals(filename)){
					String ftpFileName = new String(ftpfile.getName().getBytes(),
							ftpClient.getControlEncoding());
					
					boolean res = ftpClient.deleteFile(ftpFileName);
					if (res) {
						log.debug("delete file：" + pathname+"/"+ftpfile.getName() + " success");
					} else {
						log.error("delete file：" + pathname+"/"+ftpfile.getName() +" fail");
						throw new Exception("delete file：" + pathname +"/"+ftpfile.getName() +" fail");
					}
				}
				
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new Exception(e);
		} finally {
			close();
		}
		return false;
	}

	public void uploadFile(String uploadpath, File file) throws Exception {
		boolean flag=false;
		try {
			connect();
			flag=ftpClient.makeDirectory(uploadpath);
			if(flag){
				log.debug("ftp server create directory: "+uploadpath);
			}else{
				log.debug("ftp server directory: "+uploadpath+" already exists");
			}
			
			upload(uploadpath + "/" + file.getName(), file);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new Exception(e);
		} finally {
			close();
		}
	}

	public void upload(String pathname, File file) throws Exception {
		InputStream input = null;
		try {

			input = new FileInputStream(file);
			boolean res = ftpClient.storeFile(new String(pathname.getBytes(),
					ftpClient.getControlEncoding()), input);
			if (res) {
				log.debug("upload file：" + pathname + " success");
			} else {
				log.error("upload file：" + pathname + " fail");
				throw new Exception("upload file：" + pathname + " fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			throw new Exception(e);
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

}
