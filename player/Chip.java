package player;

public class Chip {
	private int x;
	private int y;
	private boolean visited;
	private int direct;
	private int chipValue;
	public final static int HORIZONTAL = 1;
	public final static int VERTICAL = 2;
	public final static int DIAGONALF = 3;
	public final static int DIAGONALB = 4;
	
	
	/*   Constructor   */
	public Chip(Move m,int color) {
		this.chipValue = color==0? 2:1;
		this.x = m.x1;
		this.y = m.y1;
		this.visited = false;
		this.direct = 0;
	}
	public Chip(int x, int y,int color) {
		this.chipValue = color==0? 2:1;
		this.x = x;
		this.y = y;
		this.visited = false;
		this.direct = 0;
	}
	public Chip(){
		this.x = 0;
		this.y = 0;
		this.visited = false;
		this.direct = 0;
		this.chipValue =0;
	}
	
	
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	public int getDirect() {
		return direct;
	}
	public void setDirect(int direct) {
		this.direct = direct;
	}
	public int getChipValue() {
		return chipValue;
	}
	public void setChipValue(int chipValue) {
		this.chipValue = chipValue;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	public boolean equals(Object chip) {
		if(chip instanceof Chip){
			if(((Chip) chip).x == this.x &&  ((Chip) chip).y == this.y && ((Chip) chip).chipValue == this.chipValue){
				return true;
			}
		}	
		return false;
	}
	
	
	
}
