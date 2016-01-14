package player;

import list.List;

public class Connection {
	private List path;
	private boolean hasGoal;
	public Connection() {
		this.path = null;
		this.hasGoal = false;
	}
	public Connection(List path, boolean hasGoal) {
		this.path = path;
		this.hasGoal = hasGoal;
	}
	
	public List getpath() {
		return path;
	}
	public void setpath(List path) {
		this.path = path;
	}
	public boolean isEnd() {
		return hasGoal;
	}
	public void setEnd(boolean hasGoal) {
		this.hasGoal = hasGoal;
	}
	

}
