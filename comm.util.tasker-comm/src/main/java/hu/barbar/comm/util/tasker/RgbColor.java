package hu.barbar.comm.util.tasker;

public class RgbColor{
	
	private int r;
	private int g; 
	private int b;
	
	public RgbColor(int r, int g, int b){
		this.r = cribTo8bit(r);
		this.g = cribTo8bit(g);
		this.b = cribTo8bit(b);
	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}
	
	public static int cribTo8bit(int val){
		if(val < 0)
			return 0;
		if(val > 255)
			return 255;
		return val;
	}
	
}
