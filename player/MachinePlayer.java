/* MachinePlayer.java */

package player;


import java.util.Random;

import board.SimpleBoard;
import list.*;

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

  
  public MachinePlayer clone(){
	  MachinePlayer player = new MachinePlayer(this.turn,MachinePlayer.SEARCHDEPTH);
	  player.board = this.board.clone();
	  player.machineChipsNum = this.machineChipsNum;
	  player.oppoChipsNum = this.oppoChipsNum;
	  player.myName = this.myName;
	  Chip[] machineChips = player.machineChips;
	  Chip[] opponentChips = player.opponentChips;
	  for(int i=0; i<this.machineChipsNum;i++){
		  machineChips[i] = this.machineChips[i]; 
	  }
	  for(int i=0; i<this.oppoChipsNum;i++){
		  opponentChips[i] = this.opponentChips[i];
	  }	  
	  return player;
  }
  

  public SimpleBoard getBoard(){
	  return board;
  }
  
  
  
  
  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
	Move m = null;
	if(this.machineChipsNum == 0){
		Random random = new Random();
		if(this.turn == WHITE_FIRST){
			m = new Move(0,random.nextInt(6)+1);
		}else{
			m = new Move(random.nextInt(6)+1,0);
		}
	}else{
		BestMove bestMove = abtree(Integer.MIN_VALUE,Integer.MAX_VALUE,0,this.board,this.turn);
		m = bestMove.move;
	}
	if(this.forceMove(m)){	
		return m;
	}
	else{
		System.out.println("Error: chooseMove method invalid!");
		return null;
	}   	
  }
  
   
  
  public BestMove abtree(int alpha,int beta,int searchDepth,SimpleBoard board,int turn){
	  
	  
	  BestMove myBest = new BestMove();
	  BestMove reply;
	  
//	  System.out.println("Hi I'm in the depth:" + searchDepth);
	  
	  this.findPaths(turn);
	  
//	  System.out.println("I found all paths!"); 
	  
	  Connection c = new Connection();
	  //Compute the score of current board, as well as check if game ends
	  this.findPaths(turn);
	  c = this.checkPaths(turn);
	  
//	  System.out.println("I checked all paths!");
	  
	  //If the game ends, or comes to the specified SEARCHDEPTH
	  if(c.isEnd() || searchDepth == MachinePlayer.SEARCHDEPTH){
		  // Set the score to be current score, no extra move
		  myBest.score = c.getScore();
		  //Adjust the score according to winning chance and current depth
		  if(myBest.score > 0){
			  if(myBest.score - 5 * searchDepth > 0){
				  myBest.score  -= 5 * searchDepth;
			  } else{
				  myBest.score = 5;
			  }
		  } else if(myBest.score < 0){
			  if(myBest.score + 5 * searchDepth < 0){
				  myBest.score  += 5 * searchDepth;
			  } else{
				  myBest.score = -5;
			  }
		  }
//		  System.out.println("Search ends here and I would return!");
		  return myBest;
	  }
	  
	  if(turn == this.turn){
		  myBest.score = alpha;
	  } else{
		  myBest.score = beta;
	  }
	  //find all legal moves
	  Move[] moves = this.findAllMoves(turn);
	  myBest.move = moves[0];
	  //for each legal move
	  for(Move m: moves){
		  if(m != null){
			  //record the current board
			  MachinePlayer player = this.clone();
			//perform move m
			  if(turn == player.turn){
				  player.forceMove(m);
			  }
			  else if(turn == player.opponent){
				  player.opponentMove(m);
			  }
			  //change the turn
			  int nextTurn = turn == this.turn? this.opponent:this.turn;
			  //recursively call abtree for the next turn
			  reply = player.abtree(alpha,beta,searchDepth + 1,player.board,nextTurn);
			  //undo move m
			  //If this turn is myTurn
			  if((turn == this.turn)&&(reply.score > myBest.score)){
				  myBest.move = m;
				  myBest.score = reply.score;
				  alpha = reply.score;
			  }
			  //If this turn is opponent's turn
			  else if(turn == this.opponent && reply.score < myBest.score){
				  myBest.move = m;
				  myBest.score = reply.score;
				  beta = reply.score;
			  }
			  //alpha - beta pruning
			  if(alpha >= beta){
				  return myBest;
			  }
		  }
	  }
	  return myBest;
  }
  
  
  
  	/**
  	 * findAllMovesAdd() method is when the player doesn't play 10 chips
  	 * and find the next possible move
  	 * @param turn
  	 * @return
  	 */
    public Move[] findAllMovesAdd(int turn){
    	Move[] moves= new Move[60];
//    	int chipValue = turn == MachinePlayer.WHITE_FIRST? WHITE:BLACK;
    	Move move = null;
    	int count = 0;
    	for(int y =0; y<DIMENSION; y++){
    		for(int x=0; x<DIMENSION; x++){
    			move = new Move(x,y);
    			if(this.board.isValidMove(move, turn)){
    				moves[count] = move;
    				count++;
    			}
    		}
    	}
		return moves;
    }
    /**
     * findAllMoves() method is to find the next possible moves
     * in thiss method, it includes the step and add conditions
     * @param turn
     * @return
     */
    public Move[] findAllMoves(int turn){
    	Move[] moves = new Move[60];
    	Move move = null;
    	int[][] direction = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }, { 0, -1 }, { 0, 1 } };
    	int count = 0;
    	if(turn == this.turn){
    		// step condition
    		if(this.machineChipsNum == 10){
    			for(int i= 0; i<10; i++){
    				for (int j = 0; j < 8; j++) {
    					move = new Move(this.machineChips[i].getX()+direction[j][0],this.machineChips[i].getY()+direction[j][1],this.machineChips[i].getX(),this.machineChips[i].getY());
    					move.moveKind =STEP;
    					if(this.board.isValidMove(move, turn)){
    						moves[count] = move;
    	    				count++;
    					}
    				}	
    			}
    		// the add condition
    		}else{
    			moves = findAllMovesAdd(turn);
    		}
    	}else{
    		// step condition
    		if(this.oppoChipsNum == 10){
    			for(int i= 0; i<10; i++){
    				for (int j = 0; j < 8; j++) {
    					move = new Move(this.opponentChips[i].getX()+direction[j][0],this.opponentChips[i].getY()+direction[j][1],this.opponentChips[i].getX(),this.opponentChips[i].getY());
    					move.moveKind =STEP;
    					if(this.board.isValidMove(move, turn)){
    						moves[count] = move;
    	    				count++;
    					}
    				}	
    			}
    		// the add condition
    		}else{
    			moves = findAllMovesAdd(turn);
    		}
    	}
    	
    	return moves;	
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
    if(flag != HORIZONTAL){
      horizonCheck(chip,neighbors,color);
    }
    if(flag != VERTICAL){
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
  
  
  public boolean checkGoalAreaA(Chip chip){
	  if(chip.getChipValue() == BLACK){
			 if(chip.getY() == 0){
				 return true;
			 }
		 }
		 if(chip.getChipValue() == WHITE){
			 if(chip.getX() == 0){
				 return true;
			 }
		 }
	  return false;
  }
  
  
  
  public boolean checkGoalAreaB(Chip chip){
	  if(chip.getChipValue() == BLACK){
			 if(chip.getY() == 0){
				 return true;
			 }
		 }
		 if(chip.getChipValue() == WHITE){
			 if(chip.getX() == 0){
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
	  boolean goalChip = false;
	  /* flag is the flag to tell when is to insert the path, like pop after push that should work*/
	  boolean flag = true;
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
		  machinePaths = new SList();
		  chips = this.machineChips;
	  }else if(color == opponent){
		  opponentPaths = new SList(); 
		  chips = this.opponentChips;
	  }
	  for(Chip chip : chips){
		  if(chip != null){
			  chip.setDirect(0);
			  this.findPath(color, chip);
		  }
	  }	  
  }
  
  
  public Connection checkPaths(int color){
	  List paths = null;
	  Connection conn = new Connection();
	  /*  count the number of chips in the goal area, like A is like the left area*/
	  int countGoalAreaA = 0;
	  int countGoalAreaB = 0;
	  if(color == turn){
		  paths = machinePaths;
	  }else if(color == opponent){
		  paths = opponentPaths; 
	  }
	  int score;
	  /* count the max score of the path*/
	  int maxScore = 0;
	  ListNode node = paths.front();
	  ListNode chipNode = null;
	  List path = null;
	  Chip chip = null;
	  try{
		  while(node.isValidNode()){
			  path = (List)node.item();
			  score = 0;
			  countGoalAreaA = 0;
			  countGoalAreaB = 0;
			  chipNode = path.front();
			  while(chipNode.isValidNode()){
				  chip = (Chip)chipNode.item();
				  if(checkGoalAreaA(chip)){
					  countGoalAreaA++;
					  score += 10;
					  chipNode = chipNode.next();
					  continue;
				  }
				  if(checkGoalAreaB(chip)){
					  countGoalAreaB++;
					  score += 10;
					  chipNode = chipNode.next();
					  continue;
				  }
				  score += 5;
				  chipNode = chipNode.next();
			  }
			  if(countGoalAreaA > 1 || countGoalAreaB > 1){
				  score = 0;
			  }
			  if(countGoalAreaA == 1 && countGoalAreaB == 1){
				  if(path.length() >= 6){
					  score = 100;
					  conn.setEnd(true);
					  break;
				  }
			  }
			  if(maxScore < score){
				  maxScore = score;  
			  }	
			  node = node.next();
		  } 
	  }catch(InvalidNodeException e){
		  e.printStackTrace();
	  }
	  if(color == turn){
		  conn.setScore(maxScore);
	  }else{
		  conn.setScore(-maxScore);
	  }
	  return conn;	  
	  
  }
  
  
  
  
  
  
	public static void main(String[] args) {
		MachinePlayer player = new MachinePlayer(WHITE_FIRST);
		System.out.println(player.board);
		Move m = null; 
		Move m2 = null;
//		Random random = new Random();
//		while(player.machineChipsNum<10){

//			m = new Move(random.nextInt(8),random.nextInt(8));
//			m2 = new Move(random.nextInt(8),random.nextInt(8));
//			player.forceMove(m);
//			player.opponentMove(m2);
//		}	
		m = new Move(0,2);
		player.forceMove(m);
		m = new Move(3,5);
		player.opponentMove(m);
		m = new Move(1,1);
		player.forceMove(m);
		m = new Move(2,0);
		player.opponentMove(m);
		m = new Move(3,1);
		player.forceMove(m);
		m = new Move(4,5);
		player.opponentMove(m);
		m = new Move(3,2);
		player.forceMove(m);
		m = new Move(3,0);
		player.opponentMove(m);
		m = new Move(1,4);
		player.forceMove(m);
		m2 = new Move(6,5);
		player.opponentMove(m2);
		m2 = new Move(2,4);
		player.forceMove(m2);
		m2 = new Move(3,3);
		player.opponentMove(m2);
		m2 = new Move(2,6);
		player.forceMove(m2);
		m2 = new Move(6,6);
		player.opponentMove(m2);
		m2 = new Move(1,6);
		player.forceMove(m2);
		m2 = new Move(1,2);
		player.opponentMove(m2);
		m2 = new Move(5,1);
		player.forceMove(m2);
		m2 = new Move(1,3);
		player.opponentMove(m2);
		m2 = new Move(6,1);
		player.forceMove(m2);
		m2 = new Move(4,3);
		player.opponentMove(m2);
		m = new Move(0,1,1,1);
		player.forceMove(m);
		m2 = new Move(2,7,3,0);
		player.opponentMove(m2);
		m = new Move(2,2,3,1);
		player.forceMove(m);
		m2 = new Move(3,7,3,5);
		player.opponentMove(m2);
		
		
		System.out.println(player.board);
		System.out.println("machine chips number: " + player.machineChipsNum);
		System.out.println("opponent chips number: " + player.oppoChipsNum);
		m2 = player.chooseMove();
		System.out.println(m2);
		
		

//		System.out.println("chip x position: " + player.machineChips[0].getX() + " chip y position: " + player.machineChips[0].getY());
//		List list = player.findNeighbor(player.machineChips[5], player.turn);
//		System.out.println(list);
//		player.machinePaths = new SList();
//		player.findPath(player.turn, player.machineChips[5]);
//		System.out.println("paths: " + player.machinePaths);
//		System.out.println("pathsLength: " + player.machinePaths.length() );
//		player.findPaths(player.turn);
//		System.out.println("paths:" + player.machinePaths);
//		System.out.println("pathsLength:" + player.machinePaths.length());
//		Connection conn = player.checkPaths(player.turn);
//		player.checkPaths(player.turn);
//		if(conn != null){
//			System.out.println("max score: " + conn.getScore() + " , is End? " + conn.isEnd());
//		}
//		Move[] moves = player.findAllMoves(player.turn);
//		for(Move move : moves){
//			if(move!= null){
//				System.out.println(move);
//			}
//		}
//		if(moves[0] == null){
//			System.out.println("fuckyou");
//		}
//		
//		//----------------------------
//	
	}

}
















