package player;

public class Chip {
	private int x;
	private int y;
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	public Chip(Move m) {
		this.x = m.x1;
		this.y = m.y1;
	}
	public Chip(int x, int y) {
		this.x = x;
		this.y = y;
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
			if(((Chip) chip).x == this.x &&  ((Chip) chip).y == this.y){
				return true;
			}
		}	
		return false;
	}
	
	
	
}
