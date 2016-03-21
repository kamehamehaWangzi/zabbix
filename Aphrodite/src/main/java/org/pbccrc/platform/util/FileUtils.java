package org.pbccrc.platform.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.pbccrc.platform.script.rest.ScriptRest;

public class FileUtils {
	private static final Logger log = Logger.getLogger(FileUtils.class);
	public static boolean writeFile(String content, File fileName)
			throws Exception {

		boolean flag = false;
		FileOutputStream o = null;
		try {

			o = new FileOutputStream(fileName);
			o.write(content.getBytes(Constant.SHELLFILE_ENCODE));
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (o != null) {
				o.close();
			}
		}
		return flag;
	}


	public static void saveFile(InputStream uploadedInputStream, File file) {
		
		try {
			OutputStream outpuStream = new FileOutputStream(file);
		   String content=	IOUtils.toString(uploadedInputStream);
		   if(content.indexOf("\r")!=-1){
			   log.debug("file contains char \\r");
			   
			   content=content.replaceAll("\r", "");
		   }
		   InputStream is = new ByteArrayInputStream(content.getBytes());
			int read = 0;
			byte[] bytes = new byte[1024];
			
			while ((read = is.read(bytes)) != -1) {
				
				outpuStream.write(bytes, 0, read);
			}
			outpuStream.flush();
			outpuStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readToString(File file) {
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(filecontent);
	}

	/**
	 * Convert byte[] to hex
	 * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src
	 *            byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString().toUpperCase();
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/** */
	/**
	 * 读文件到字节数组
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static byte[] readFile(File file) throws Exception {
		if (file.exists() && file.isFile()) {
			long fileLength = file.length();
			if (fileLength > 0L) {
				BufferedInputStream fis = new BufferedInputStream(
						new FileInputStream(file));
				byte[] b = new byte[(int) fileLength];
				while (fis.read(b) != -1) {
					for (int j = 0; j < b.length; j++) {
						// System.out.println(b[j]);
					}
				}
				fis.close();
				fis = null;

				return b;
			}
		} else {
			return null;
		}
		return null;
	}

	/** */
	/**
	 * 将字节数组写入文件
	 * 
	 * @param filePath
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static boolean writeBytes(String filePath, byte[] content)
			throws IOException {
		File file = new File(filePath);
		synchronized (file) {
			BufferedOutputStream fos = new BufferedOutputStream(
					new FileOutputStream(filePath));
			fos.write(content);
			fos.flush();
			fos.close();
		}
		return true;

	}

}
