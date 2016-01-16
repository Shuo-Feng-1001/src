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

	private final static int BLACK = 2;
	private final static int WHITE = 1;
	
	
	/*   Constructor   */
	public Chip(Move m,int turn) {
		this(m.x1,m.y1,turn);
	}
	public Chip(int x, int y,int turn) {
		this.chipValue = turn == MachinePlayer.BLACK_SECOND? BLACK:WHITE;
		this.x = x;
		this.y = y;
		this.visited = false;
		this.direct = 0;
	}
	public Chip(){
		this(0,0,0);
		this.chipValue = 0;
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
	@Override
	public String toString() {
		String v,d,c;
		switch(this.chipValue){
		case WHITE:
			c = "o";
			break;
		case BLACK:
			c = ".";
			break;
		default:
			c = "no";
		}
		
		if(this.visited){
			v = "v";
		} else{
			v = "x";
		}
		
		switch(this.direct){
		case HORIZONTAL:
			d = "--";
			break;
		case VERTICAL:
			d = "|";
			break;
		case DIAGONALF:
			d = "\\";
			break;
		case DIAGONALB:
			d = "/";
			break;
		default:
			d = new String();
			break;
		}
		return  "(" + x + "," + y + ")" + " chip=" + c + " " + "dir=" + d + " " + "visited? " + v;
	}
	

}
