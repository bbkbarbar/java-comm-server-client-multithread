package hu.barbar.util.filehandler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hu.barbar.util.FileHandler;
import junit.framework.TestSuite;

public class FileHandlerTest extends TestSuite {

	@Test
	public void guaranteePathSeparatorAtEndOf_Test(){

		if(FileHandler.runningOnLinux()){
			String inputPath1 = "/home/pi/taskerData";
			assertEquals("/home/pi/taskerData/", FileHandler.guaranteePathSeparatorAtEndOf(inputPath1));
		}else{
			String inputPath1 = "c:\\Some\\folder\\taskerData";
			assertEquals("c:\\Some\\folder\\taskerData\\", FileHandler.guaranteePathSeparatorAtEndOf(inputPath1));
			
			String inputPath2 = "c:\\Some\\other folder\\taskerData\\";
			assertEquals("c:\\Some\\other folder\\taskerData\\", FileHandler.guaranteePathSeparatorAtEndOf(inputPath2));
		}

	}

}