package hu.barbar.comm.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class BaseCommandsTest {

	@Test
	public void basicCommandsDefined(){
		assertNotNull(BaseCommands.CLIENT_EXIT);
		assertNotNull(BaseCommands.GET_CLIENT_COUNT);
		assertNotNull(BaseCommands.GET_CLIENT_LIST);
		assertNotNull(BaseCommands.STOP_SERVER);
	}
	
}
