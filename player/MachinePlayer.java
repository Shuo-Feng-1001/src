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
  private int machineChipsNum;
  private int oppoChipsNum;
  private SimpleBoard board;
  final private int color;
  final private int opponent;
  private Chip[] machineChips;
  private Chip[] opponentChips;
  
  
  
  
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
	  machineChipsNum = 0;
	  oppoChipsNum = 0;
	  board = new SimpleBoard();
	  this.color = color;
	  opponent = (color+1) % 2;
	  machineChips = new Chip[10];
	  opponentChips = new Chip[10];
	  
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
	if(board.isValidMove(m, opponent)){
		Chip chip = new Chip(m);
		board.makeMove(m, opponent);
		// ADD
		if(m.moveKind == 1){
			this.opponentChips[oppoChipsNum] = chip;
			oppoChipsNum++;
		}
		// STEP
		if(m.moveKind == 2){
			Chip oldchip = new Chip(m.x2,m.y2);
			for(int i=0; i<oppoChipsNum; i++){
				if(opponentChips[i].equals(oldchip)){
					opponentChips[i] = chip;
				}
			}
		}
		
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
		Chip chip = new Chip(m);
		// ADD
		if(m.moveKind == 1){
			this.machineChips[machineChipsNum] = chip;
			machineChipsNum++;
		}
		// STEP
		if(m.moveKind == 2){
			Chip oldchip = new Chip(m.x2,m.y2);
			for(int i=0; i<machineChipsNum; i++){
				if(machineChips[i].equals(oldchip)){
					machineChips[i] = chip;
				}
			}
		}
		return true;
	}else{
		return false;
	}   
  }
  
  
  public Chip[] findNeighbor(Chip chip){
	 Chip[] neighbors = new Chip[10];
	 
	 
	 return null;
	 
  }
 
  public void horizonCheck(Chip chip){
	 int x = chip.getX();
	 int y = chip.getY();
	 for(int i=x-1; i>=0; i--){
		 if(this.board.elementAt(i, y)) 
	 }
	 for(int i=x+1; i<8; i++){
		 if(board)
	 }
	 
  }
  public void vertiCheck(Chip chip){
	 
  }
  public void diagnfCheck(Chip chip){
	 
  }
  public void diagnbCheck(Chip chip){

  }

  public Connection findNetwork(){
    return null;
    
  }
  
  

}
















