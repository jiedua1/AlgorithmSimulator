import java.awt.Color;
import java.awt.Graphics;

public class Square {
	private int type; //0 = default, 1 = obstacle, 2 = explored, 3 = start square, 4 = end square, 5 = good path
	public int x,y; //holds positions
	private int size; //holds size of square
	private int tempType;
	public static final int STARTTYPE = 3;
	public static final int ENDTYPE = 4;
	public static final int GOODPATH = 5;
	
	public Square() {
		type = 0;
		x = 0;
		y = 0;
		size = 10;
	}
	public Square(int x, int y, int type, int size) {
		this.size = size;
		this.type = type;
		setTempType(type);
		this.x = x;
		this.y = y;
	}
	public void setState(int s) {
		type = s;
		tempType = s;
	}
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(x*size, y*size, size, size);
		if(type == 0) {
			g.setColor(Color.GRAY);
		}
		else if(type == 1) {
			g.setColor(Color.darkGray);
		} else if (type == 2) {
			g.setColor(Color.LIGHT_GRAY);
		} else if (type == 3) {
			g.setColor(Color.RED);
		} else if (type == 4) {
			g.setColor(Color.GREEN);
		} else if (type == 5) {
			g.setColor(Color.YELLOW);
		}
		g.fillRect(x*size+1, y*size+1, size-2, size-2);
	}
	//toggles a square from obstacle to non obstacle
	public void activate() {
		type = 1;
		tempType = 1;
	}
	public void deactivate() {
		type = 0;
		tempType = 0;
	}
	public void toggle() {
		type = 1-type;
		tempType = type;
	}
	public int getState() {
		return type;
	}
	public void changeState(int i) {
		type = i;
		tempType = i;
	}
	//used to store temporary changes that aren't drawn yet
	public void activateTemp() {
		setTempType(1);
	}
	public int getTempType() {
		return tempType;
	}
	public void setTempType(int tempType) {
		this.tempType = tempType;
	}
}
