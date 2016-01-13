package board;


import player.MachinePlayer;
import player.Move;
import player.Player;

/* SimpleBoard.java */

/**
 *  Simple class that implements an 8x8 game board with three possible values
 *  for each cell:  0, 1 or 2.
 *
 *  DO NOT CHANGE ANY PROTOTYPES IN THIS FILE.
 **/

public class SimpleBoard {
  private final static int DIMENSION = 8;
  private final static int Black = 2;
  private final static int White = 1;
  private int[][] grid;

  /**
   *  Invariants:  
   *  (1) grid.length == DIMENSION.
   *  (2) for all 0 <= i < DIMENSION, grid[i].length == DIMENSION.
   *  (3) for all 0 <= i, j < DIMENSION, grid[i][j] >= 0 and grid[i][j] <= 2.
   **/

  /**
   *  Construct a new board in which all cells are zero.
   */

  public SimpleBoard() {
    grid = new int[DIMENSION][DIMENSION];
    // set the four corners of the board to invalid value
    grid[0][7] = grid[7][7] = grid[0][0] = grid[7][0] = 5;
  }
  
  public boolean isValidMove(Move m, MachinePlayer player){
	  int tempX = m.x1;
	  int tempY = m.y2;
	  SimpleBoard tempGrid;
	  int chipValue = player.myName== "White"? White:Black;
	  
	  
	  int flag = 0;
	  boolean condition = false;
	  /*
	   * check : 1. no chip may be placed in a square that is already occupied
	   * 		 2. no chip may be placed in any of the four corners
	   */
	  if(elementAt(tempX,tempY) != 0){
		  return false;
	  }
	  /*
	   * check 3. no chip may be placed in a goal of the opposite color
	   * white goal area: x == 0 || x == 7
	   * black goal area: y == 0 || y == 7
	   */
	  if(player.myName == "White" && (tempY == 0 || tempY == 7)){
		  return false;
	  }
	  if(player.myName == "Black" && (tempX == 0 || tempX == 7)){
		  return false;
	  }
	  /*
	   * check 4. a player may not have more than two chips in a connected group
	   */
	  // tempGrid the player's current board
	  tempGrid = player.getBoard();
	  // assuming that this chip is placed at this grid
	  // add = 1;  step = 2;
	  if(m.moveKind == 2){
		 // if the move is step, we need to remove the old one
		 tempGrid.setElementAt(m.x2, m.y2, 0);
		 flag = 1;
	  }
	  	  
	  condition = !isNarrowConnected(tempX, tempY, player);
	  if(flag == 1){
		  tempGrid.setElementAt(m.x2, m.y2, chipValue);
	  }  
	  return condition;
  }
  
  public boolean isNarrowConnected(int x, int y, MachinePlayer player){
	  SimpleBoard board = new SimpleBoard();
	  int number = numNarrowConnected(x,y,player,board,1);
	  return number>2? true:false;
  }
  /**
   * isNarrowConnected() is to check whether there is three or more chips of the same color in a cluster
   * if that happens, return true;
   * @param x  the pointed to be check, x axis
   * @param y  the pointed to be check, y axis
   * @return true, is the given condition is true
   */
  public int numNarrowConnected(int x, int y, MachinePlayer player,SimpleBoard board,int number){
	  int chipValue = player.myName== "White"? White:Black;
	  //direction array includes 8 direction given the specific point
	  int[][] direction = {{-1,-1},{-1,0},{-1,1},{1,-1},{1,0},{1,1},{0,-1},{0,1}};
	  int posX; 
	  int posY;
	  int count = number;
	  for(int i = 0; i<8; i++){
		  posX = x + direction[i][0];
		  posY = y + direction[i][1];
		  if(posX >= 0 && posY >= 0 && posX < DIMENSION && posY < DIMENSION ){
			  if(elementAt(posX,posY) == chipValue && board.elementAt(posX, posY) == 0){
				  board.setElementAt(posX, posY, 1);
				  count+= numNarrowConnected(posX,posY,player,board,count);
			  }
		  }	  
	  }
	 return count;
  }
  
  
  
  
  
  /**
   *  Set the cell (x, y) in the board to the given value mod 3.
   *  @param value to which the element should be set (normally 0, 1, or 2).
   *  @param x is the x-index.
   *  @param y is the y-index.
   *  @exception ArrayIndexOutOfBoundsException is thrown if an invalid index
   *  is given.
   **/
  
  public void setElementAt(int x, int y, int value) {
    grid[x][y] = value % 3;
    if (grid[x][y] < 0) {
      grid[x][y] = grid[x][y] + 3;
    }
  }

  /**
   *  Get the valued stored in cell (x, y).
   *  @param x is the x-index.
   *  @param y is the y-index.
   *  @return the stored value (between 0 and 2).
   *  @exception ArrayIndexOutOfBoundsException is thrown if an invalid index
   *  is given.
   */

  public int elementAt(int x, int y) {
    return grid[x][y];
  }

  /**
   *  Returns true if "this" SimpleBoard and "board" have identical values in
   *    every cell.
   *  @param board is the second SimpleBoard.
   *  @return true if the boards are equal, false otherwise.
   */

  public boolean equals(Object board) {
    // Replace the following line with your solution.  Be sure to return false
    //   (rather than throwing a ClassCastException) if "board" is not
    //   a SimpleBoard.
	if(board instanceof SimpleBoard){
		for(int y=0; y < DIMENSION; y++){
			for(int x=0; x < DIMENSION; x++){
				if(this.elementAt(x, y) != ((SimpleBoard)board).elementAt(x, y)){
					return false;
				}
			}
		}
		return true;
	}
    return false;
  }

  /**
   *  Returns a hash code for this SimpleBoard.
   *  each cell as a digit of a base-3 number(with 64 digits), and convert that base-3 number
   *  to a single int
   *  we assume that (0,0) is the greatest significant digit 
   *  (7,7) is the least significant digit
   *  @return a number between Integer.MIN_VALUE and Integer.MAX_VALUE.
   */

  public int hashCode() {
	  int sum = 0;
	  for(int y=0; y < DIMENSION; y++){
			for(int x=0; x < DIMENSION; x++){
				sum = 3 * sum + this.elementAt(x, y);
			}
		}
	  return sum;
  }
  
  
  @Override
	public String toString() {
		String str= "";
		for(int y = 0; y< DIMENSION; y++ ){
			for(int x = 0; x < DIMENSION; x++){
				str += " [ " + this.elementAt(x, y) + " ] ";	
			}
			str += "\n";
		}
		return str;
	}

  public static void main(String[] args) {
	    SimpleBoard board = new SimpleBoard();
//	    System.out.println("hash code of new board should be 0, is: "+board.hashCode());
//	    board.setElementAt(7,7,2);
//	    System.out.println("hash code of new board should be 2, is: "+board.hashCode());
//	    board.setElementAt(6,7,1);
//	    System.out.println("hash code of new board should be 5, is: "+board.hashCode());
//	    double fval = Math.random() * 12;
//		int value = (int) fval;
//		System.out.println(value);
	    MachinePlayer player = new MachinePlayer(0);
	    System.out.println("the player is: " + player.myName);
	    Move move = new Move(7,2);
	    System.out.println(board.isValidMove(move, player));
	    player.getBoard().setElementAt(1, 1, 2);
	    player.getBoard().setElementAt(1, 2, 2);
	    board.setElementAt(1, 1, 2);
	    board.setElementAt(1, 2, 2);
	    System.out.println(player.getBoard());
	    System.out.println("the forth condition: " + board.isNarrowConnected(1,0,player));
	    SimpleBoard board2 = null;
		try {
			board2 = (SimpleBoard) board.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    board2.setElementAt(0, 0, 2);
	    System.out.println(board2);
//	    for(int y = 0; y< DIMENSION; y++ ){
//			for(int x = 0; x < DIMENSION; x++){
//				if(board.elementAt(x, y) == 0){
//					if((player.myName == "White" && y != 0 && y != 7 ) || (player.myName == "Black" && x != 0 && x != 7)){
////						System.out.println("x: " + x + " y: " + y);
//						if(!board.isNarrowConnected(x, y, player)){
//							player.getBoard().setElementAt(x, y, 2);
//							board.setElementAt(x, y, 2);
//						}
//					
//					}
//				}
//			}
//	    }
	    System.out.println(board);
	    
	    
	    
	  }

}