package player;


public class Connection {
	private int score;
	private boolean isEnd;
	public Connection() {
		this.score = 0;
		this.isEnd = false;
	}
	public Connection(int score, boolean isEnd) {
		this.score = score;
		this.isEnd = isEnd;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public boolean isEnd() {
		return isEnd;
	}
	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
	

}
