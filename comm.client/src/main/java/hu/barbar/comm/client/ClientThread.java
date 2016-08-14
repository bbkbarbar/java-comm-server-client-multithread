package hu.barbar.comm.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import hu.barbar.comm.util.Msg;

/**
 * This class made to handle communication with server
 * @author BA
 */
public abstract class ClientThread extends Thread {
	
	private Socket mySocket = null;
	private String host = null;
	private int port = 0;
	
	InputStream is = null;
	OutputStream os = null;
	private ObjectInputStream objIn = null;
	private ObjectOutputStream objOut = null;
	
	private boolean isInitialized = false;
	
	private boolean expectFurtherMessages = true;
	
	
	public ClientThread(Socket socket, String host, int port) {
		this.mySocket = socket;
		this.host = host;
		this.port = port;
	}
	
	/**
	 * @return True when everything is ready for communication.
	 */
	public boolean isOK(){
		return (isInitialized && mySocket != null && mySocket.isConnected());
	}
	
	
	@Override
	public void run() {
		
		Msg receivedText = null;
		try {
			mySocket = new Socket(host, port);
		
			if(mySocket.isClosed()){
				System.out.println("Socket is CLOSED.");
			}
		
			objIn = new ObjectInputStream(mySocket.getInputStream());
			objOut = new ObjectOutputStream(mySocket.getOutputStream());
			isInitialized = true;
			
			int exceptionCounter = 0;
			
			while( expectFurtherMessages ){
				try{
					receivedText = (Msg) objIn.readObject();
					if(receivedText != null)
						handleReceivedMessage(receivedText);
				}catch(Exception e){
					exceptionCounter++;
					//System.out.println("Exception while try to read incoming message.");
					if(exceptionCounter >= 5){
						disconnect();
					}
				}
			}
			
		} catch (IOException e1) {
			System.out.println(e1.toString());
			e1.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		
	}

	public void disconnect(){
		this.expectFurtherMessages = false;
		this.interrupt();
	}
	
	public boolean sendMessageToServer(Msg message){
		
		int numberOfAttemps = 0;
		while(!isInitialized && numberOfAttemps++ < 5){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
		}
		
		if (this.objOut != null) {
			try {
				objOut.writeObject(message);
				objOut.flush();
			} catch (IOException e) {
				return false;
			}
			return true;
		} else {
			System.out.println("ERROR: objOutput is NULL!");
			return false;
		}
	}
	
	public ObjectOutputStream getObjOut() {
		return this.objOut;
	}
	
	
	public abstract boolean handleReceivedMessage(Msg message);
	
}
