package hu.barbar.comm.util.tasker;

import hu.barbar.comm.util.Msg;

public class StateMsg extends Msg {

	private static final long serialVersionUID = 3035266283582947262L;

	public static class OutputType{
		
		public static final int UNDEFINED = -1,
								INVALID = 0,
								IO = 1,
								PWM = 2;
		
		public static String getNameOf(int type){
			switch (type) {
				case UNDEFINED:
					return "Undefined";
				case IO:
					return "IO";
				case PWM:
					return "PWM";
				default:
					return "Invalid";
			}
		}
		
		public static int getOutputTypeFrom(int input){
			if(input < OutputType.UNDEFINED || input > OutputType.PWM){
				return OutputType.INVALID;
			}else{
				return input;
			}
		}
		
	}
	
	private int outputType;
	
	private int value;
	
	public StateMsg(String name, int val, int outputType) {
		super(name, Msg.Types.RESPONSE_STATE);
		this.value = val;
		this.outputType = OutputType.getOutputTypeFrom(outputType);
		
	}
	
	public String toString(){
		return "Name: " + this.getName()
			+ " Type: " + OutputType.getNameOf(this.getOutputType())
			+ " Value: " + this.getValue();
	}
	
	public String getName(){
		return this.content; 
	}
	
	public int getValue(){
		return this.value;
	}
	
	public int getOutputType(){
		return this.outputType;
	}

}
