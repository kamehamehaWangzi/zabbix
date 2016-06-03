package org.pbccrc.platform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @description: zip解压缩文件的工具类，文件可以是多级目录结构
 * （使用JDK的ZipEntru存在文件名中文乱码问题，用Apache-tools的则不会）
 * @author zhp
 *
 */
public class ZipUtil {

	/**
	 * @description 压缩文件操作
	 * @param filePath 要压缩的文件路径
	 * @param descDir 压缩文件保存的路径
	 */
	public static void zipFile(String filePath, String descDir){
		ZipOutputStream zos = null;
		//创建一个Zip输出流
		try {
			zos = new ZipOutputStream(new FileOutputStream(descDir));
			//启动压缩
			makeZipFile(zos,"",filePath);
			
			System.out.println("***********************压缩完毕************");
		} catch (IOException e) {
			//压缩失败，则删除创建的文件
			File zipFile = new File(descDir);
			if(zipFile.exists()){
				zipFile.delete();
			}
			System.out.println("************************压缩失败***********");
			e.printStackTrace();
		} finally{
			try{
				if(zos != null){
					zos.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * @description 对目录中所有的文件递归遍历进行压缩
	 * @param zos ZIP压缩输出流
	 * @param oppositePath 在zip文件中的相对路径
	 * @param sourceDir 要压缩的文件的路径
	 * @throws IOException
	 */
	public static void makeZipFile(ZipOutputStream zos, String oppositePath, String sourceDir)
				throws IOException{
		File file = new File(sourceDir);
		
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(int i = 0; i<files.length; i++){
				File childFile = files[i];
				if(childFile.isDirectory()){
					//如果是目录，修改相对地址
					String newOppositePath = oppositePath + childFile.getName() + "/";
					//压缩目录，这是关键，创建一个目录的条目是，需要在目录后面多加一个"/"
					ZipEntry entry = new ZipEntry(newOppositePath);
					zos.putNextEntry(entry);
					zos.closeEntry();
					//进行递归
					makeZipFile(zos, newOppositePath, childFile.getPath());
				}else{
					//如果不是目录，直接调用方法进行压缩
					makeSingleZipFile(zos, oppositePath, childFile);
				}
			}
		}else{
			//如果不是目录，直接调用方法进行压缩
			makeSingleZipFile(zos, oppositePath, file);
		}
	}
	
	/**
	 * @description 压缩单个文件到目录中
	 * @param zos zip输出流
	 * @param oppositePath 在zip文件中的相对位置
	 * @param file 要压缩的文件
	 */
	public static void makeSingleZipFile(ZipOutputStream zos, String oppositePath,
			File file){
		//创建一个Zip条目，每个Zip条目都是必须相对于根路径
		InputStream input = null;
		
		try{
			ZipEntry entry = new ZipEntry(oppositePath+file.getName());
			zos.putNextEntry(entry);
			//从文件输入流 中 读取数据，并将数据写入输出流当中
			input = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int length = 0;
			while((length=input.read(bytes))!=-1){
				zos.write(bytes, 0, length);
			}
			zos.closeEntry();
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			try{
				if(input!=null){
					input.close();
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		
	}
	
	//******************************************************
	
	public static void unZipFiles(String zipFilePath, String descDir){
		File zipFile = new File(zipFilePath);
		File pathFile = new File(descDir);
		
		if(!pathFile.exists()){
			pathFile.mkdirs();
		}
		
		ZipFile zip = null;
		InputStream in = null;
		OutputStream out = null;
		
		try {
			zip = new ZipFile(zipFile);
			for(Enumeration<? extends ZipEntry> entries = zip.entries();entries.hasMoreElements();){
				ZipEntry entry = entries.nextElement();
				String zipEntryName = entry.getName();
				in = zip.getInputStream(entry);
				
				String outPath = (descDir+"/"+zipEntryName).replaceAll("\\*", "/");
				//判断路径是否存在，不存在则创建文件路径
				File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
				
				if(!file.exists()){
					file.mkdirs();
				}
				//判断文件全路径是否为文件夹，如果是在上面已经创建，不需要解压
				if(new File(outPath).isDirectory()){
					continue;
				}
				out = new FileOutputStream(outPath);
				byte[] byte1 = new byte[4*1024];
				int len;
				while((len=in.read(byte1))>0){
					out.write(byte1, 0, len);
				}
				in.close();
				out.close();
			}
			System.out.println("********************解压完毕*******************");
		} catch (IOException e) {
			pathFile.delete();
			System.out.println("*********************解压失败******************");
			e.printStackTrace();
		} finally{
			try{
				if(zip!=null){
					zip.close();
				}
				if(in!=null){
					in.close();
				}
				if(out!=null){
					out.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
