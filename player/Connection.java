package player;

public class Connection {
	private int connectedNum;
	private boolean isEnd;
	public Connection() {
		this.connectedNum = 0;
		this.isEnd = false;
	}
	public Connection(int connectedNum, boolean isEnd) {
		this.connectedNum = connectedNum;
		this.isEnd = isEnd;
	}
	
	public int getConnectedNum() {
		return connectedNum;
	}
	public void setConnectedNum(int connectedNum) {
		this.connectedNum = connectedNum;
	}
	public boolean isEnd() {
		return isEnd;
	}
	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
	

}
