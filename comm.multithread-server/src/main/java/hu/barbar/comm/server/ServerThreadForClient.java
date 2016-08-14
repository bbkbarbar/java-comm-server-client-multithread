package hu.barbar.comm.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import hu.barbar.comm.util.BaseCommands;
import hu.barbar.comm.util.Msg;
import hu.barbar.util.logger.Log;

public class ServerThreadForClient extends Thread {
	
	public static boolean DEBUG_MODE = false;

	public static final Long ID_UNDEFINED = -1l;
	
	private MultiThreadServer myServer = null;
	private Long myId = ServerThreadForClient.ID_UNDEFINED;

	private Socket client = null;
	//private BufferedReader in = null;
	private ObjectInputStream objIn = null;
	//private PrintWriter out = null;
	private ObjectOutputStream objOut = null;
	
	
	public ServerThreadForClient(Socket client, Long id, MultiThreadServer server) {
		this.myServer = server;
		this.client = client;
		this.myId = id;
	}
	
	public ServerThreadForClient(Socket client, Long id, MultiThreadServer server, boolean debugMode) {
		this.myServer = server;
		this.client = client;
		this.myId = id;
		ServerThreadForClient.DEBUG_MODE = debugMode;
	}
	
	@Override
	public long getId() {
		return this.myId;
	}
	
	@Override
	public void run() {
		
		try {
			//Channel channel = new Channel( client.getInputStream() );

			//in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			objIn = new ObjectInputStream(client.getInputStream());
			//TODO HERE
			//objIn = new ObjectInputStream(Channels.newInputStream(channel));
			//out = new PrintWriter(new PrintStream(client.getOutputStream()), true);
			objOut = new ObjectOutputStream(client.getOutputStream());
			
			if(objIn == null){
				myServer.showOutput("Client (" + myId + "): IN is NULL.");
			}
			
			Msg receivedMsg = null;
			try{
				while ((receivedMsg = (Msg) objIn.readObject()) != null){
					if(receivedMsg.getType()==Msg.Types.COMMAND && receivedMsg.getContent().equals(BaseCommands.CLIENT_EXIT)){
						myServer.dropClient(myId);
						myServer.onClientExit(myId);
					}else{
						myServer.handleInput(receivedMsg, myId);
					}
				}
			}catch(NullPointerException npe){
				myServer.showOutput("Client (" + myId + "): NullPointerException cought");
			}catch(ClassNotFoundException cnfe){
				myServer.showOutput("ClassNotFoundException cought");
			}

		} catch (IOException ioExc) {
			//TODO: handleExceptionIOWhenServerGetMessage(ioExc);
		}
		
		super.run();
	}

	@SuppressWarnings("deprecation")
	public void drop() {
		try{
			objIn.close();
		}catch(Exception e){}
		try{
			objOut.close();
		}catch(Exception e){}
		try{
			client.shutdownInput();
			client.close();
		}catch(Exception e){
			//TODO:
		}finally {
			try{
				ServerThreadForClient.this.interrupt();
			}catch(Exception e){}
			try{
				ServerThreadForClient.this.stop();
			}catch(Exception e){}
		}
	}

	public boolean sendMessage(Msg message) {
		if(DEBUG_MODE){
			Log.d("Send to client: [" + myId + "]: " + message.getContent());
		}
		if(objOut == null){
			return false;
		}
		try{
			objOut.writeObject(message);
			objOut.flush();
		}catch(Exception e){
			Log.w("ServerThreadForClient.sendMessage :: Exception cought: " + e.toString()); 
			return false;
		}
		return true;
	}

	
	
}
