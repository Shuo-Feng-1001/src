/* MachinePlayer.java */

package player;


import java.util.Random;

import board.SimpleBoard;
import list.DList;
import list.InvalidNodeException;
import list.List;
import list.SList;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {
  public static int SEARCHDEPTH = 2;
  private int machineChipsNum;
  private int oppoChipsNum;
  private SimpleBoard board;
  final private int turn;
  final private int opponent;
  private Chip[] machineChips;
  private Chip[] opponentChips;
  /*store the current potential networks*/
  private List machinePaths;
  private List opponentPaths;
  

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
  
  public final static int WHITE_FIRST = 1;
  public final static int BLACK_SECOND = 0;

  
  
  
  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int turn) {
	  this(turn,2);
  }
 
  
  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int turn, int searchDepth) {
	  if(turn == 0){
		  this.myName = "BLACK_SECOND";
	  }else{
		  this.myName = "WHITE_FIRST";
	  }
	  machineChipsNum = 0;
	  oppoChipsNum = 0;
	  board = new SimpleBoard();
	  this.turn = turn;
	  opponent = (turn + 1) % 2;
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
		Chip chip = new Chip(m,this.opponent);
		board.makeMove(m, opponent);
		// ADD
		if(m.moveKind == ADD){
			this.opponentChips[oppoChipsNum] = chip;
			oppoChipsNum++;
		}
		// STEP
		if(m.moveKind == STEP){
			Chip oldchip = new Chip(m.x2,m.y2,this.opponent);
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
	if(board.isValidMove(m, turn)){
		this.board.makeMove(m, turn);
		Chip chip = new Chip(m, turn);
		// ADD
		if(m.moveKind == ADD){
			this.machineChips[machineChipsNum] = chip;
			machineChipsNum++;
		}
		// STEP
		if(m.moveKind == STEP){
			Chip oldchip = new Chip(m.x2,m.y2,turn);
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
  
  /**
   * findNeighbor() method is to find all of the given chip's neighbors
   * (if the neighbor is legal)
   * @param chip
   * @return
   */
  public List findNeighbor(Chip chip, int color){
    List neighbors = new SList();
    int flag = chip.getDirect();
    boolean blackGoalArea = false;
    boolean whiteGoalArea = false;
//    if(chip.getChipValue() == BLACK){
//      if(chip.getY() == 0 || chip.getY() == DIMENSION-1){
//    	  blackGoalArea = true;
//      }
//    }
//    if(chip.getChipValue() == WHITE){
//      if(chip.getX() == 0 || chip.getX() == DIMENSION-1){
//        whiteGoalArea = true;
//      }
//    }
    if(flag != HORIZONTAL && (!blackGoalArea)){
      horizonCheck(chip,neighbors,color);
    }
    if(flag != VERTICAL && (!whiteGoalArea)){
      vertiCheck(chip,neighbors,color);
    }
    if(flag != DIAGONALF){
      diagnfCheck(chip,neighbors,color);
    }
    if(flag != DIAGONALB){
      diagnbCheck(chip,neighbors,color);
    }
    return neighbors;
  }
  
 /**
  * horizonCheck() checks the neighbor of the given chip 
  * in the horizontal direction, and insert neighbors into
  * the list
  * @param chip :the given chip
  * @param neighbors :the list to insert neighbors
  */
  public void horizonCheck(Chip chip,List neighbors,int color){
  	int x = chip.getX();
  	int y = chip.getY();
  	Chip[] chips;
  	if(color == turn){
  	  chips = this.machineChips;
  	}else{
  	  chips = this.opponentChips;
  	}
    int opponentValue = chip.getChipValue() == WHITE? BLACK:WHITE;
    boolean flag = true;
    for(int i=x-1; i>=0; i--){
  	  if(this.board.elementAt(i, y) == opponentValue ){
  		 flag = false;
  	  }
  	  if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
  		  Chip neighborChip = null;
  		  for(int k=0; k<chips.length;k++){
          if(chips[k]!=null){
            if(chips[k].getX() == i && chips[k].getY() == y && !chips[k].isVisited()){
              neighborChip = chips[k];
              neighborChip.setDirect(HORIZONTAL);
              neighbors.insertBack(neighborChip);
              break;
            }
          }
  		  }
  		  break;
  	  }
    }
    flag = true;
    for(int i=x+1; i<8; i++){
  	  if(this.board.elementAt(i, y) == opponentValue ){
        flag = false;
  	  }
  	  if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
        Chip neighborChip = null;
        for(int k=0; k<chips.length;k++){
          if(chips[k]!=null){
            if(chips[k].getX() == i && chips[k].getY() == y && !chips[k].isVisited()){
              neighborChip = chips[k];
              neighborChip.setDirect(HORIZONTAL);
              neighbors.insertBack(neighborChip);
              break;
            }
          }
        }
        break;
  	  }
    }
  }
  
  /**
   * vertiCheck() checks the neighbor of the given chip 
   * in the vertical direction, and insert neighbors into
   * the list
   * @param chip :the given chip
   * @param neighbors :the list to insert neighbors
   */
  public void vertiCheck(Chip chip,List neighbors,int color){
    int x = chip.getX();
    int y = chip.getY();
    Chip[] chips;
    if(color == turn){
      chips = this.machineChips;
    }else{
      chips = this.opponentChips;
    }
    int opponentValue = chip.getChipValue() == WHITE? BLACK:WHITE;
    boolean flag = true;
    for(int i=y-1; i>=0; i--){
    if(this.board.elementAt(x,i) == opponentValue ){
     flag = false;
    }
    if(flag && this.board.elementAt(x, i) == chip.getChipValue()){
      Chip neighborChip = null;
      for(int k=0; k<chips.length;k++){
        if(chips[k]!=null){
          if(chips[k].getX() == x && chips[k].getY() == i && !chips[k].isVisited()){
            neighborChip = chips[k];
            neighborChip.setDirect(VERTICAL);
            neighbors.insertBack(neighborChip);
            break;
          }
        }
      }
      break;
    }
	 }
	 flag = true;
	 for(int i=y+1; i<8; i++){
		  if(this.board.elementAt(x,i) == opponentValue ){
        flag = false;
		  }
		 if(flag && this.board.elementAt(x, i) == chip.getChipValue() ){
			  Chip neighborChip = null;
        for(int k=0; k<chips.length;k++){
          if(chips[k]!=null){
            if(chips[k].getX() == x && chips[k].getY() == i && !chips[k].isVisited()){
              neighborChip = chips[k];
              neighborChip.setDirect(VERTICAL);
              neighbors.insertBack(neighborChip);
              break;
            }
          }
        }
        break;
		 }
	 }
  }
  
  /**
   * diagnfCheck() checks the neighbor of the given chip 
   * in the diagonal direction, from up-left to bottom-right
   * and insert neighbors into the list
   * @param chip :the given chip
   * @param neighbors :the list to insert neighbors
   */
  public void diagnfCheck(Chip chip,List neighbors,int color){
    int x = chip.getX();
  	int y = chip.getY();
  	Chip[] chips;
  	if(color == turn){
  		chips = this.machineChips;
  	}else{
  		chips = this.opponentChips;
  	}
  	int opponentValue = chip.getChipValue() == WHITE? BLACK : WHITE;
  	boolean flag = true;
  	int i = x,j = y;
  	while(i > 0 && j > 0){
  		i--;
  		j--;
  		if(this.board.elementAt(i, j) == opponentValue ){
  			flag = false;
  		}
  		if(flag && this.board.elementAt(i, j) == chip.getChipValue()){
  			Chip neighborChip = null;
          for(int k=0; k<chips.length;k++){
            if(chips[k]!=null){
              if(chips[k].getX() == i && chips[k].getY() == j && !chips[k].isVisited()){
                neighborChip = chips[k];
                neighborChip.setDirect(DIAGONALF);
                neighbors.insertBack(neighborChip);
                break;
              }
            }
          }
  		  break;
  		}
  	}
  	flag = true;
  	i = x;
  	j = y;
  	while(i < DIMENSION - 1&& j < DIMENSION - 1){
  		i++;
  		j++;
  		if(this.board.elementAt(i, j) == opponentValue ){
  			flag = false;
  		}
  		if(flag && this.board.elementAt(i, j) == chip.getChipValue()){
  			Chip neighborChip = null;
  			for(int k=0; k<chips.length;k++){
  				if(chips[k]!=null){
            if(chips[k].getX() == i && chips[k].getY() == j && !chips[k].isVisited()){
    					neighborChip = chips[k];
    					neighborChip.setDirect(DIAGONALF);
    					neighbors.insertBack(neighborChip);
    					break;
    				}
          }
  			}
  			break;
  		}
  	}
  }
  
  /**
   * diagnbCheck() checks the neighbor of the given chip 
   * in the diagonal direction, from bottom-left to up-right
   * and insert neighbors into the list
   * @param chip :the given chip
   * @param neighbors :the list to insert neighbors
   */
  public void diagnbCheck(Chip chip, List neighbors, int color){
    int x = chip.getX();
    int y = chip.getY();
    Chip[] chips;
    if(color == turn){
      chips = this.machineChips;
    }else{
      chips = this.opponentChips;
    }
    int opponentValue = chip.getChipValue() == WHITE? BLACK : WHITE;
    boolean flag = true;
    int i = x,j = y;
    while(i < DIMENSION - 1&& j > 0){
      i++;
      j--;
      if(this.board.elementAt(i, j) == opponentValue ){
        flag = false;
      }
      if(flag && this.board.elementAt(i, j) == chip.getChipValue()){				 
        Chip neighborChip = null;
        for(int k=0; k<chips.length;k++){
          if(chips[k]!=null){
            if(chips[k].getX() == i && chips[k].getY() == j && !chips[k].isVisited()){
              neighborChip = chips[k];
              neighborChip.setDirect(DIAGONALB);
              neighbors.insertBack(neighborChip);
              break;
            }
          }
        }
        break;
      }
    }
    flag = true;
    i = x;
    j = y;
    while(i > 0 && j < DIMENSION - 1){
      i--;
      j++;
      if(this.board.elementAt(i, j) == opponentValue ){
        flag = false;
      }
      if(flag && this.board.elementAt(i, j) == chip.getChipValue()){
        Chip neighborChip = null;
        for(int k=0; k<chips.length;k++){
          if(chips[k]!=null){
            if(chips[k].getX() == i && chips[k].getY() == j && !chips[k].isVisited()){
              neighborChip = chips[k];
              neighborChip.setDirect(DIAGONALB);
              neighbors.insertBack(neighborChip);
              break;
            }
          }
        }
        break;
      }
    }
  }
  
  public boolean checkGoalArea(Chip chip){  
	  if(chip.getChipValue() == BLACK){
			 if(chip.getY() == 0 || chip.getY() == DIMENSION-1){
				 return true;
			 }
		 }
		 if(chip.getChipValue() == WHITE){
			 if(chip.getX() == 0 || chip.getX() == DIMENSION-1){
				 return true;
			 }
		 }
	  return false;
  }
  /**
   * findPath() is the method to find all of the potential network given the 
   * specific chip
   * 
   * @param chip
   */
  public void findPath(int color, Chip chip){
	  /* path store one possible path */
	  List path = new SList();
	  SList finalPath = new SList();
	  /* paths store all possible paths of given chips*/
	  List paths = null;
	  /* store each players chips which has already placed on the board*/
	  Chip[] chips = null;
	  /* store the given chip's neighbors*/
	  List neighbors = null;
	  Chip currentChip = null;
	  Chip neighChip = null;
//	  Connection conn = null;
	  boolean goalChip = false;
	  /* flag is the flag to tell when is to insert the path, like pop after push that should work*/
	  boolean flag = false;
	  boolean flag2 = false;
	  int pathLength = 0;
	  if(color == turn){
		  paths = machinePaths;
		  chips = this.machineChips;
	  }else if(color == opponent){
		  paths = opponentPaths; 
		  chips = this.opponentChips;
	  }
	  for(Chip curr : chips){
		  if(curr!=null){
			  curr.setVisited(false);
		  }
	  }	  
	  // -----------using stack-like method to find the path-------------
	  chip.setVisited(true);
	  path.insertBack(chip);
	  try {
		  while(!path.isEmpty()){
			flag2 = false;
			currentChip = (Chip)(path.back().item());
			if(checkGoalArea(currentChip)){
				goalChip = true;
			}
			neighbors = findNeighbor(currentChip,color);
			if(!neighbors.isEmpty()){
				neighChip = (Chip) ((SList)neighbors).pop();
				if(checkGoalArea(neighChip)){
					goalChip = true;
				}
				neighChip.setVisited(true);
				path.insertBack(neighChip);
				flag = true;
			}else{
				if(flag){
					if(goalChip || (path.length() > pathLength)){
						pathLength = path.length();
						finalPath = ((SList)path).Clone();
						paths.insertBack(finalPath);
						goalChip = false;
					}
				}
				((SList)path).pop();
				flag = false;
			}	
		  }
	  }catch (InvalidNodeException e) {
		  e.printStackTrace();
	  }
	  
	 
  }
  
  public void findPaths(int color){
	  Chip[] chips = null;
	  /*empty the current paths list*/
	  if(color == turn){
		  machinePaths = new DList();
		  chips = this.machineChips;
	  }else if(color == opponent){
		  opponentPaths = new DList(); 
		  chips = this.opponentChips;
	  }
	  for(Chip chip : chips){
		  if(chip != null){
			  chip.setDirect(0);
			  System.out.println(chip);
			  this.findPath(color, chip);
		  }
	  }
	  
	  
  }
  
  
  
  
  
	public static void main(String[] args) {
		MachinePlayer player = new MachinePlayer(WHITE_FIRST);
		System.out.println(player.board);
		Move m = null; 
		Move m2 = null;
		Random random = new Random();
//		for(int i = 0; i< 10; i++){
//			m = new Move(random.nextInt(8),random.nextInt(8));
//			m2 = new Move(random.nextInt(8),random.nextInt(8));
//			player.forceMove(m);
//			player.opponentMove(m2);
//		}
		m = new Move(0,3);
		player.forceMove(m);
		m = new Move(6,3);
		player.forceMove(m);
		m = new Move(6,4);
		player.forceMove(m);
		m = new Move(7,1);
		player.forceMove(m);
//		m = new Move(4,1);
//		player.forceMove(m);
//		m = new Move(4,3);
//		player.forceMove(m);
		m2 = new Move(2,1);
		player.opponentMove(m2);
		m2 = new Move(3,1);
		player.opponentMove(m2);
		m2 = new Move(5,2);
		player.opponentMove(m2);
		m2 = new Move(5,3);
		player.opponentMove(m2);
		m2 = new Move(5,5);
		player.opponentMove(m2);
		m2 = new Move(1,7);
		player.opponentMove(m2);
		m2 = new Move(2,4);
		player.opponentMove(m2);
		
		
		System.out.println(player.board);
		System.out.println("machine chips number: " + player.machineChipsNum);
		System.out.println("opponent chips number: " + player.oppoChipsNum);
//		System.out.println("chip x position: " + player.machineChips[0].getX() + " chip y position: " + player.machineChips[0].getY());
//		List list = player.findNeighbor(player.machineChips[5], player.turn);
//		System.out.println(list);
//		player.machinePaths = new SList();
//		player.findPath(player.turn, player.machineChips[5]);
//		System.out.println("paths: " + player.machinePaths);
//		System.out.println("pathsLength: " + player.machinePaths.length() );
		player.findPaths(player.turn);
		System.out.println("paths:" + player.machinePaths);
		System.out.println("pathsLength:" + player.machinePaths.length());
		
	}
  

}
















