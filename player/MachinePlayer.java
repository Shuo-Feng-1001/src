/* MachinePlayer.java */

package player;

import board.SimpleBoard;
import list.List;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {
  public static int SEARCHDEPTH = 2;
  private List list;
  private int chips;
  private SimpleBoard board;
  final private int color;
  final private int opponent;
  
  
  
  
  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
	  this(color,2);
  }

 
  
  
  
  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
	  if(color == 0){
		  this.myName = "BLACK";
	  }else{
		  this.myName = "WHITE";
	  }
	  chips = 10;
	  board = new SimpleBoard();
	  this.color = color;
	  opponent = (color+1) % 2;
  }

  
  public SimpleBoard getBoard(){
	  return board;
  }
  
  
  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    return new Move();
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
	if(board.isValidMove(m, color)){
		board.makeMove(m, opponent);
		return true;
	}
	return false;
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
	if(board.isValidMove(m, color)){
		this.board.makeMove(m, color);
		return true;
	}else{
		return false;
	}   
  }
  
  
  

}
