package inroids.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * File/Folder manage
 * @author Sealy
 */
public abstract class FileManage {
	private static final String strTag="IRLibrary";
	/**
	 * Create a folder
	 * @param sFolder a folder path. 
	 * @return true or false.
	 */
	public static boolean createFolder(String sFolder){
		try{
			File filT=new File(sFolder);
			if(!filT.exists()){
				return filT.mkdirs();
			}
		}catch (Exception e) {
			MyLog.e(strTag, "FileManage.createFolder:"+e.toString());
  		}
		return false;
	}
		
	/**
	 * rename file
	 * @param sOldFile a folder path. 
	 * @return true or false
	 */
	public static boolean reName(String sOldFile,String sNewFile){
		try{
			File filT=new File(sOldFile);
			if (filT.exists()) {
				if (filT.isFile()) { 
					File filN=new File(sNewFile);
					return filT.renameTo(filN);
					//return filT.delete();
				}
			}
		}
	    catch (Exception e) {
	    	MyLog.e(strTag, "FileManage.reName:"+ e.toString());
	    }
		return false;
	}
	
	/**
	 * Delete a file
	 * @param file a folder path. 
	 * @return true or false
	 */
	public static boolean delFile(String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				if (filT.isFile()) { 
					return filT.delete();
				}
			}
		}
	    catch (Exception e) {
	    	MyLog.e(strTag, "FileManage.delFile:"+ e.toString());
	    }
		return false;
	}
	
	/**
	 * Delete all file from folder
	 * @param folder a folder path. 
	 * @return true or false.
	 */
	public static boolean delAllFile(String folder){
		try{
			File filT=new File(folder);
			if (filT.exists()) { 
				if (filT.isDirectory()) {
					File files[] = filT.listFiles(); 	//get file list
					for (int i = 0; i < files.length; i++) {
						if(files[i].isFile()){
							
							files[i].delete();
						}
					}
					return true;
				}
			}
		}
	    catch (Exception e) {  
	    	MyLog.e(strTag, "FileManage.delAllFile:"+ e.toString());
	    }
		return false;
	}

	/**
	 * Check that file exists
	 * @param file a file path. 
	 * @return the boolean.
	 */
	public static boolean isExistsFile(String sFilePath){
		try{
			File filT=new File(sFilePath);
			return filT.exists() && filT.isFile();
		}
	    catch (Exception e) { 
	    	MyLog.e(strTag, "FileManage.isExistsFile:"+ e.toString());
	    }
		return false;
	}
	
	/**
	 * Check that folder exists
	 * @param folder a folder path. 
	 * @return the boolean.
	 */
	public static boolean isExistsFolder(String folder){
		try{
			File filT=new File(folder);
			return filT.exists()&& filT.isDirectory();
		}catch (Exception e) { 
			MyLog.e(strTag, "FileManage.isExistsFolder:"+ e.toString());
		   }
		return false;
	}

	/**
	 * read string from file
	 * @param file a file path. 
	 * @return the string.
	 */
    public static String getStringFromFile(String sFilePath){
        StringBuffer fileData = new StringBuffer(100000);
        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(sFilePath));
			char[] buf = new char[1024];
	        int numRead=0;
	        try {
				while((numRead=reader.read(buf)) != -1){
				    String readData = String.valueOf(buf, 0, numRead);
				    fileData.append(readData);
				}
			} catch (IOException e) {				
				MyLog.w(strTag, "FileManage.getStringFromFile:"+ e.toString());
			}
	        try {
				reader.close();
			} catch (IOException e) {
				MyLog.w(strTag, "FileManage.getStringFromFile:"+ e.toString());
			}
	        return fileData.toString();
		} catch (FileNotFoundException e) {
			MyLog.e(strTag, "FileManage.getStringFromFile:"+ e.toString());
		}
		
		return null;
        
    }
    
    /**
	 * save string to file
	 * @param file a file path. 
	 * @return the string.
	 */
    public static void saveStringToFile(String sFilePath,String toSaveString){
    	try{
    		File saveFile = new File(sFilePath);
    		if (!saveFile.exists()){
    			File dir = new File(saveFile.getParent());
    			dir.mkdirs();
    			saveFile.createNewFile();
    		}
    		FileOutputStream outStream = new FileOutputStream(saveFile);
    		outStream.write(toSaveString.getBytes());
    		outStream.close();
    	} catch (FileNotFoundException e){
    		MyLog.e(strTag, "FileManage.saveStringToFile:"+ e.toString());
    	} catch (IOException e){
    		MyLog.e(strTag, "FileManage.saveStringToFile:"+ e.toString());
    	}
   }
     
    /**
	 * read fileName from file path
	 * @param file a file path. 
	 * @return the string.
	 */
    public static String getFileNameFromPath(String path){
        String str[]=path.split("/");
        return str[str.length-1];
    }
    
    
    /**
	 * Delete is't exists file in strList
	 * @param folder a folder path. 
	 * @return true or false
	 */
	public static boolean delFileIsNotExists(String folder,String strList){
		try{
			File filT=new File(folder);
			if (filT.exists()) { 
				if (filT.isDirectory()) {
					File files[] = filT.listFiles(); 	//get file list
					for (int i = 0; i < files.length; i++) {
						if(files[i].isFile()){
							if(!strList.contains(","+files[i].getName()+",")){
								//MyLog.e(strTag,"FileManage.delFileIsNotExists:"+ files[i].getName()+" is deleted!");
								files[i].delete();
							}				
						}
					}
					return true;
				}
			}
		}
	    catch (Exception e) {    
	    	MyLog.e(strTag,"FileManage.delFileIsNotExists:"+ e.toString());
	    }
		return false;
	}
	
}
