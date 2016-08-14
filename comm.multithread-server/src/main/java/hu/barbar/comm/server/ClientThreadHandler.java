package hu.barbar.comm.server;

import java.util.ArrayList;

public class ClientThreadHandler {

	public ArrayList<ServerThreadForClient> clientThreads = null;
	
	
	public ClientThreadHandler() {
		this.clientThreads = new ArrayList<>();
	}
	
	public void add(ServerThreadForClient client){
		this.clientThreads.add(client);
	}
	
	/**
	 * Get client with specified Id.
	 * @param id
	 * @return ServerThreadForClient instance with the specified id or <br>
	 * null if there is no instance with specified id.
	 */
	public ServerThreadForClient get(Long id){
		for(int i=0; i<clientThreads.size(); i++){
			if(Long.valueOf(clientThreads.get(i).getId()) == Long.valueOf(id) ){
				return clientThreads.get(i);
			}
		}
		return null;
	}
	
	
	public int size(){
		if(clientThreads == null){
			return -1;
		}
		return clientThreads.size();
	}
	
	public ArrayList<Long> getIdList(){
		if(clientThreads == null){
			return null;
		}
		ArrayList<Long> list = new ArrayList<>();
		for(int i=0; i<clientThreads.size(); i++){
			list.add(clientThreads.get(i).getId());
		}
		
		return list;
	}

	/**
	 * @param id
	 * @return the <b>removed ServerThreadForClient -instance</b> what has been removed now or <br>
	 * <b>null</b> if item with specified id can not be found.
	 */
	public ServerThreadForClient remove(long id) {
		
		for(int i=0; i<clientThreads.size(); i++){
			if(clientThreads.get(i).getId() == id){
				ServerThreadForClient itemToRemove = clientThreads.get(i);
				clientThreads.remove(i);
				return itemToRemove;
			}
		}
		
		return null;
	}
	
}
