/* MachinePlayer.java */

package player;

import board.SimpleBoard;
import list.List;
import list.SList;

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

  public static final int ADD = 1;
  public static final int STEP = 2; 
  
  public final static int HORIZONTAL = 1;
  public final static int VERTICAL = 2;
  public final static int DIAGONALF = 3;
  public final static int DIAGONALB = 4;
  
  private final static int BLACK = 2;
  private final static int WHITE = 1;
  private final static int EMPTY = 0;
  private final static int DIMENSION = 8;

  
  
  
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
		if(m.moveKind == ADD){
			this.opponentChips[oppoChipsNum] = chip;
			oppoChipsNum++;
		}
		// STEP
		if(m.moveKind == STEP){
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
		if(m.moveKind == ADD){
			this.machineChips[machineChipsNum] = chip;
			machineChipsNum++;
		}
		// STEP
		if(m.moveKind == STEP){
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
  
  
  public List findNeighbor(Chip chip){
	 List neighbors = new SList();
	 
	 
	 
	 return neighbors;
	 
  }
 
  public void horizonCheck(Chip chip,List neighbors){
	 int x = chip.getX();
	 int y = chip.getY();

	 int opponentValue = chip.getChipValue() == 1? 2:1;
	 boolean flag = true;
	 for(int i=x-1; i>=0; i--){
		 if(this.board.elementAt(i, y) == opponentValue ){
			 flag = false;
		 }
		 if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
			 chip.setDirect(HORIZONTAL);
			 neighbors.insertBack(chip);
		 }
	 }
	 flag = true;
	 for(int i=x+1; i<8; i++){
		 if(this.board.elementAt(i, y) == opponentValue ){
			 flag = false;
		 }
		 if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
			 chip.setDirect(HORIZONTAL);
			 neighbors.insertBack(chip);
		 }
	 }
  }
  public void vertiCheck(Chip chip,List neighbors){
	 int x = chip.getX();
	 int y = chip.getY();
	 int opponentValue = chip.getChipValue() == 1? 2:1;
	 boolean flag = true;
	 for(int i=y-1; i>=0; i--){
		 if(this.board.elementAt(i, y) == opponentValue ){
			 flag = false;
		 }
		 if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
			 chip.setDirect(VERTICAL);
			 neighbors.insertBack(chip);
		 }
	 }
	 flag = true;
	 for(int i=y+1; i<8; i++){
		 if(this.board.elementAt(i, y) == opponentValue ){
			 flag = false;
		 }
		 if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
			 chip.setDirect(VERTICAL);
			 neighbors.insertBack(chip);
		 }
	 }
  }
  public void diagnfCheck(Chip chip,List neighbors){
	 int x = chip.getX();
	 int y = chip.getY();
	 int opponentValue = chip.getChipValue() == 1? 2:1;
	 boolean flag = true;
	 flag = true;
	 for(int i=x-1; i>=0; i--){
		 if(this.board.elementAt(i, y) == opponentValue ){
			 flag = false;
		 }
		 if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
			 chip.setDirect(HORIZONTAL);
			 neighbors.insertBack(chip);
		 }
	 }
	 for(int i=x+1; i<8; i++){
		 if(this.board.elementAt(i, y) == opponentValue ){
			 flag = false;
		 }
		 if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
			 chip.setDirect(HORIZONTAL);
			 neighbors.insertBack(chip);
		 }
	 }
  }
  public void diagnbCheck(Chip chip, List neighbors){
	  	int x = chip.getX();
		 int y = chip.getY();
		 int opponentValue = chip.getChipValue() == WHITE? BLACK : WHITE;
		 boolean flag = true;
		 int i = x,j = y;
		 while(i < DIMENSION && j < DIMENSION){
			 i++;
			 j++;
			 if(this.board.elementAt(i, j) == opponentValue ){
				 flag = false;
			 }
			 if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
				 chip.setDirect(DIAGONALB);
				 neighbors.insertBack(chip);
			 }
		 }
		 flag = true;
		 i = x;
		 j = y;
		 while(i > 0 && j > 0){
			 i--;
			 j--;
			 if(this.board.elementAt(i, y) == opponentValue ){
				 flag = false;
			 }
			 if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
				 chip.setDirect(DIAGONALB);
				 neighbors.insertBack(chip);
			 }
		 }
  }

  public Connection findNetwork(){
    return null;
    
  }
  
  

}
















