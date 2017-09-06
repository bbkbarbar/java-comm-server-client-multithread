package hu.barbar.comm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import hu.barbar.comm.util.Msg;
import hu.barbar.util.logger.Log;

public abstract class MultiThreadServer extends Thread {

	public static final int DEFAULT_PORT_VALUE = 10710;
	
	public static final int ID_FOR_ALL_CLIENT = -1;

	private boolean SERVER_ACCEPT_NEW_CONNECTIONS = true;

	private ServerSocket myServerSocket;

	//private HashMap<Integer, ServerThreadForClient> clients = null;
	private ClientThreadHandler clientThreadHandler = null;
	
	private Long nextClientId = 0l;
	
	private int port = DEFAULT_PORT_VALUE;
	
	
	public MultiThreadServer(int port) {
		//clients = new HashMap<>();
		clientThreadHandler = new ClientThreadHandler();
		nextClientId = 0l;
		this.port = port;
	}
	
	
	@Override
	public void run() {
		Socket client = null;
		
		try {
			myServerSocket = new ServerSocket(port);
		} catch (IOException e) {
			String errorMessage = "IOException catched while try initialize ServerSocket(" + port + ")";
			showOutput(errorMessage);
			Log.e(errorMessage);
			Log.d("Interrupt server thread..");
			MultiThreadServer.this.interrupt();
		}
		
		Log.i("Server wait for clients on port: " + port + "..");
		while (SERVER_ACCEPT_NEW_CONNECTIONS) {
			try{
				client = myServerSocket.accept();
				ServerThreadForClient clientThread = new ServerThreadForClient(client, nextClientId, this);
				//clients.put(Integer.valueOf(nextClientId), clientThread);
				clientThreadHandler.add(clientThread);
				
				nextClientId++;
				clientThread.start();
			} catch (IOException e) {
				showOutput("IOException catched while try to accept new connection.");
				Log.e("IOException catched while try to accept new connection.");
			}
		}
		
		super.run();
	}
	
	public boolean dropClient(long id){
		
		Log.d("Drop client (id: " + id + ")");
		
		//if(clients == null){
		if(clientThreadHandler == null){
			Log.d("MultiThreadServer :: Try to drop client but clientThreadHandler is null");
			return false;
		}
		
		try{
			//clients.get(Integer.valueOf(id)).drop();
			clientThreadHandler.get(id).drop();
		}catch(Exception e){
			Log.d("MultiThreadServer :: Try to drop client, but something went wrong: " + e.toString());
		}
		
		try{
			//if(clients.get(Integer.valueOf(id)) == null){
			if(clientThreadHandler.get(id) == null){
				Log.e("Can not get client item to remove (id: "+ id + ").");
			}
			//clients.remove(Integer.valueOf(id));
			clientThreadHandler.remove(id);
			Log.i("Client disconnected: " + id);
			onClientExit(id);
			return true;
		}catch(Exception e){
			Log.w("MultiThreadServer :: Try to drop client, but something went wrong: " + e.toString());
			return false;
		}
		
	}
	
	
	public boolean sendToClient(Msg msg, long clientId){
		
		if(clientThreadHandler == null){
			Log.e("Try to send message to a client but clients map is NULL.");
			return false;
		}
		
		if(clientId == ID_FOR_ALL_CLIENT){
			ArrayList<Long> clientIds = clientThreadHandler.getIdList();
			
		    Log.d("Send to all client.");
		    Log.d("Client count: " + clientIds.size());
			
			for(int i=0; i<clientIds.size(); i++){
				Log.d("Send to client: (" + clientIds.get(i) +") \n" + msg.toString());
				sendToClient(msg, clientIds.get(i));
			}
			return true;
		}
		
		ServerThreadForClient addressee = clientThreadHandler.get(clientId);
		if( addressee == null ){
			//TODO:
			Log.e("sendToClient: addressee == null");
			return false;
		}
		
		return addressee.sendMessage(msg);
		
	}
	
	
	protected void showOutput(String text){}
	
	protected abstract boolean handleInput(Msg msg, Long clientId);

	protected abstract void onClientExit(Long clientId);

	public int getActiveClientCount(){
		//if(clients == null){
		if(clientThreadHandler == null){
			return -1;
		}else{
			//return clients.size();
			return clientThreadHandler.size();
		}
	}


	public int getPort() {
		return this.port;
	}

}
