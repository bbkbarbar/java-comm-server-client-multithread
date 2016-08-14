package hu.barbar.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import hu.barbar.tasker.Log;
//import hu.barbar.tasker.util.OutputConfig;

public abstract class FileHandler {
	
	public static final String DEFAULT_CONFIG_SEPARATOR = "=";
	
	public static final int RESULT_EXISTS = 0,
							RESULT_CREATED = 1,
							RESULT_CAN_NOT_CREATED = -1;
	
	
	/**
	 * Need to override
	 * @param text
	 */
	protected static void handleWarningLog(String text){}
	
	/**
	 * Need to override
	 * @param text
	 */
	protected static void handleErrorLog(String text){}
	
	public static ArrayList<String> readLines(String path){
		return readLines(path, false);
	}
	
	public static ArrayList<String> readLines(String path, boolean needTrim){
		
		if(path == null || path.trim().equals("")){
			return null;
		}
		
		ArrayList<String> lines = new ArrayList<>();
		String currentLine;
		BufferedReader br;
		
		try {
			
			br = new BufferedReader(new FileReader(path));
			while ((currentLine = br.readLine()) != null) {
				if( !needTrim || (needTrim && !currentLine.trim().equals(""))){
					lines.add(currentLine);
				}
			}
			
		} catch (FileNotFoundException e) {
			//TODO: megcsinlani h ezek a warningok / error-ok ertelmesen legyenek kezelve
			FileHandler.handleWarningLog("ReadFile :: File not found: " + path);
		} catch (IOException e) {
			FileHandler.handleErrorLog("ReadFile :: IOException while try to read file: \n" + path);
		}
		
		return lines;
		
	}
	
	public static boolean writeToFile(String destinationFile, ArrayList<String> content) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter(destinationFile));
	 
			for (int i = 0; i < content.size(); i++) {
				pw.write(content.get(i) + "\n");
			}
		 
			pw.close();
		} catch (IOException e) {
			FileHandler.handleErrorLog("writeToFile :: (File: " + destinationFile + ") IOException cought: " + e.toString());
			return false;
		}
		return true;
	}
	
	public static boolean appendToFile(String destinationFile, String line){
		PrintWriter  output;
		
		if(!fileExists(destinationFile)){
			ArrayList<String> contentOfNewFile = new ArrayList<>();
			contentOfNewFile.add(line);
			return writeToFile(destinationFile, contentOfNewFile);
		}else{
			try {
				output = new PrintWriter(new BufferedWriter(new FileWriter(destinationFile, true)));
				output.append(line + "\n");
				output.flush();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
				FileHandler.handleErrorLog("appendToFile :: IOException cought: " + e.toString());
				return false;
			}
			return true;
		}
			
	}
	
	
	
	public static HashMap<String, String> readConfig(String path){
		return readConfig(path, FileHandler.DEFAULT_CONFIG_SEPARATOR);
	}
	
	
	
	public static HashMap<String, String> readConfig(String path, String separator){
		
		ArrayList<String> lines = FileHandler.readLines(path);
		if(lines == null){
			return null;
		}
		
		HashMap<String, String> result = new HashMap<>();
		
		for(int i=0; i<lines.size(); i++){
			String[] arr = lines.get(i).split(separator);
			
			// Drop line if not contains separator "="
			
			if(arr.length == 2){
				result.put(arr[0].trim(), arr[1].trim());
			}
			/*
			else
			if(arr.length > 2){
				String value = "";
				for(int j=1; j<arr.length; j++){
					value += arr[j];
					if(j<arr.length-1){
						value += separator;
					}
				}
				result.put(arr[0], value);
			}/**/
		}
		
		return result;
		
	}
	
	public static boolean fileExists(String filePath){ 
		File f = new File(filePath);
		if(f.exists() && !f.isDirectory()) { 
		    return true;
		}
		return false;
	}
	
	/**
	 * Check that folder exists or not.
	 * @param path
	 * @return FileHandler.RESULT_EXISTS OR <br>
	 *         FileHandler.RESULT_CREATED if not exists but could be created now OR <br>
	 *         FileHandler.RESULT_CAN_NOT_CREATED if does not exists and can not be created.
	 */
	public static int createFolderIfNotExists(String path){
		File theDir = new File(path);

		if (theDir.exists()) {
			return RESULT_EXISTS;
		}else{
			// if the directory does not exist, create it
		    try{
		        theDir.mkdir();
		        return RESULT_CREATED;
		    } 
		    catch(SecurityException se){
		        //handle it
		    	FileHandler.handleErrorLog("createFolderIfNotExists :: SecurityException catched: " + se.toString());
		    }
		    return RESULT_CAN_NOT_CREATED;
		}
	}
	
	
	/**
	 * Read JSON file
	 * @param filePath
	 * @return JSONObject instance <br> null in case of any problem occurred.
	 */
	public static JSONObject readJSON(String filePath){
		
		if( !fileExists(filePath)){
			FileHandler.handleWarningLog("Tried to read NON-existing JSON: " + filePath);
			return null;
		}
		
		JSONObject json = null;
		JSONParser parser = new JSONParser();
		
		try {
			json = (JSONObject) parser.parse(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			json = null;
		} catch (IOException e) {
			e.printStackTrace();
			json = null;
		}
		catch (ParseException e) {
			e.printStackTrace();
			json = null;
		}
		
		if(json == null){
			FileHandler.handleWarningLog("Exception cought while tried to read JSON: " + filePath);
		}
		
		return json;
	}
	
	public static boolean storeJSON(String destinationFile, org.json.simple.JSONObject json){
		
		try {
			FileWriter fw = new FileWriter(destinationFile);
			json.writeJSONString(fw);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
}
