package hu.barbar.comm.util.tasker;

import java.util.ArrayList;

import hu.barbar.comm.util.Msg;

/**
 * 
 * @author Barbar
 */
public class RGBMessageMultipleZone extends Msg {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7166555696543543430L;
	
	private ArrayList<RgbColor> colors = null;
	
	public RGBMessageMultipleZone(String content, ArrayList<RgbColor> colorList) {
		super(content, Msg.Types.RGB_COMMAND);

		colors = new ArrayList<RgbColor>();
		
		for(int i=0; i<colorList.size(); i++){
			colors.add(colorList.get(i));
		}
		
	}
	
	public static RGBMessageMultipleZone createInstance(String line){
		//TODO: !!!!!!
		return null;
	}

	public String getInstanceAsLine(){
		//TODO: !!!!
		return null;
	}
	
	public boolean equals(RGBMessageMultipleZone otherInstance){
		//TODO: !!!!
		return false;
	}

}
