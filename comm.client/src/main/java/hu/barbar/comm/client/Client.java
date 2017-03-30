package hu.barbar.comm.client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import hu.barbar.comm.util.BaseCommands;
import hu.barbar.comm.util.Msg;
import hu.barbar.util.logger.Log;

public abstract class Client extends Thread {

	public static final int versionCode = 200;
	public static final String version = "2.0.0";
	
	
	protected int TIMEOUT_WAIT_WHILE_INITIALIZED_IN_MS = 3000;
	private static final int DELAY_BETWEEN_CHECKS_FOR_INITIALIZED_STATE_IN_MS = 50;
	
	//private Client me;
	protected SenderThread sender = null;
    protected ReceiverThread receiver = null;
    
    private InputStream is = null;
    private OutputStream os = null;
    private ObjectInputStream objIn = null;
    private ObjectOutputStream objOut = null;
	
	private String host = null;
	private int port = 0;
	
	private Socket socket = null;
	private boolean initialized = false;
	private boolean connected = false;
	private boolean wantToDisconnect = false;
	

	public Client() {
		super();
		//me = Client.this;
	}
	
	public Client(String host, int port) {
		super();
		this.host = host;
		this.port = port;
		//me = Client.this;
	}
	
	public Client(String host, int port, int timeOutForIsOK) {
		super();
		this.host = host;
		this.port = port;
		//me = Client.this;
		this.TIMEOUT_WAIT_WHILE_INITIALIZED_IN_MS = timeOutForIsOK;
	}
	
	public void setLogLevels(String path, String filename, int levelOfStandardOutput, int levelOfFileLogs){
		Log.init(path, filename, levelOfStandardOutput, levelOfFileLogs);
	}/**/
	
	@Override
	public void run() {
		this.connect(host);
		super.run();
	}
	
	/**
     *  Connect to specified host
     * @param host
     */
	protected void connect(String host) {
		
		if(host == null){
			return;
		}
		
		this.wantToDisconnect = true;
		
		try {
            
        	/**
        	 *  Connect to Server
        	 */
			socket = new Socket(host, port);
			os = socket.getOutputStream();
			is = socket.getInputStream();
			objOut = new ObjectOutputStream(os);
			objIn = new ObjectInputStream(is);								
			
			Log.w("Connected to server " + host + " @ " + this.port);
			
		} catch (java.net.ConnectException ce){
			
			this.onConnectionRefused(host, port);
			return;
			/**/
        } catch (Exception ioe) {
				Log.w("Can not establish connection to " +  host + " @ " + port);
        	ioe.printStackTrace();
        	//System.exit(-1);
        	return;
        }
 
        /**
		 *  Create and start Receiver thread
		 */
		this.receiver = new ReceiverThread(objIn, Client.this) {
			@Override
			protected void handleMessage(Msg message) {
				handleRecievedMessage(message);
			}
			
		};
		receiver.start();
		
		/**
		 *  Create and start Sender thread
		 */
		sender = new SenderThread(objOut);
		Log.d("Sender created.");

        //sender.setDaemon(true);
        sender.start();
 

		
		initialized = true;
		
		//Maybe removable:
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		
		this.connected = true;
		this.onConnected(host, port);
		
		return;
	}

	public boolean isInitialized(){
		return this.initialized;
	}
	
	public void onConnected(String host, int port){}

	public void onDisconnected(String host, int port){}
	
	public void onConnectionRefused(String host, int port){}
	
	protected abstract void handleRecievedMessage(Msg message);
	
	
	protected abstract void showOutput(String text);


	public boolean isConnected(){
		return this.connected;
	}


	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}


	public boolean setHost(String host) {
		if(socket == null || !socket.isConnected()){
			this.host = host;
			return true;
		}
		return false;
	}

	public boolean setPort(int port) {
		if(socket == null || !socket.isConnected()){
			this.port = port;
			return true;
		}
		return false;
	}

	
	public static int getVersioncode() {
		return versionCode;
	}

	public static String getVersion() {
		return version;
	}
	
	
	public boolean waitWhileIsInitialized(){
		int maxTries = TIMEOUT_WAIT_WHILE_INITIALIZED_IN_MS / DELAY_BETWEEN_CHECKS_FOR_INITIALIZED_STATE_IN_MS;
		if(maxTries<1){
			maxTries = 1;
		}
		try {
			int attempCount = 0;
			while(this.isInitialized() == false && attempCount < maxTries){
				Thread.sleep(DELAY_BETWEEN_CHECKS_FOR_INITIALIZED_STATE_IN_MS);
				attempCount++;
			}
		} catch (InterruptedException e) {
			// Do nothing..
		}
		return this.initialized;
	}
	

	public boolean sendMessage(Msg msg) {
		if(msg == null)
			return false;
		if(initialized == false){
			System.out.println("Can not send message. Connection is NOT initialized.");
			return false;
		}
		sender.sendMsg(msg);
		return true;
	}

	public void disconnect() {
		
		this.wantToDisconnect = true;
		Msg byeMsg = new Msg(BaseCommands.CLIENT_EXIT, Msg.Types.COMMAND);
		this.sendMessage(byeMsg);
		
		try {
			receiver.interrupt();
		} catch (Exception e) {}
		try {
			sender.interrupt();
		} catch (Exception e) {}
		
		try {
			objIn.close();
		} catch (Exception e) {}
		try {
			objOut.close();
		} catch (Exception e) {}
		try {
			is.close();
		} catch (Exception e) {}
		try {
			os.close();
		} catch (Exception e) {}
		try {
			socket.close();
		} catch (Exception e) {}
		
		
		this.initialized = false;
		this.connected = false;
		
		this.onDisconnected(host, port);
		
	}
	
	public boolean getWantToDisconnect(){
		return this.wantToDisconnect;
	}

	public void setWantToDisconnect(boolean wantToDisconnect){
		this.wantToDisconnect = wantToDisconnect;
	}

}
