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
  final private int turn;
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
  
  
  public List findNeighbor(Chip chip){
	 List neighbors = new SList();
	 int flag = chip.getDirect();
	 if(flag != HORIZONTAL){
		 horizonCheck(chip,neighbors);
	 }
	 if(flag != VERTICAL){
		 vertiCheck(chip,neighbors);
	 }
	 if(flag != DIAGONALF){
		 diagnfCheck(chip,neighbors);
	 }
	 if(flag != DIAGONALB){
		 diagnbCheck(chip,neighbors);
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
  public void horizonCheck(Chip chip,List neighbors){
	 int x = chip.getX();
	 int y = chip.getY();

	 int opponentValue = chip.getChipValue() == WHITE? BLACK:WHITE;
	 boolean flag = true;
	 for(int i=x-1; i>=0; i--){
		 if(this.board.elementAt(i, y) == opponentValue ){
			 flag = false;
		 }
		 if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
			 Chip neighborChip = new Chip(i,y,this.turn);
			 neighborChip.setDirect(HORIZONTAL);
			 neighbors.insertBack(neighborChip);
		 }
	 }
	 flag = true;
	 for(int i=x+1; i<8; i++){
		 if(this.board.elementAt(i, y) == opponentValue ){
			 flag = false;
		 }
		 if(flag && this.board.elementAt(i, y) == chip.getChipValue()){
			 Chip neighborChip = new Chip(i,y,this.turn);
			 neighborChip.setDirect(HORIZONTAL);
			 neighbors.insertBack(neighborChip);
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
  public void vertiCheck(Chip chip,List neighbors){
	 int x = chip.getX();
	 int y = chip.getY();
	 int opponentValue = chip.getChipValue() == WHITE? BLACK:WHITE;
	 boolean flag = true;
	 for(int i=y-1; i>=0; i--){
		 if(this.board.elementAt(x,i) == opponentValue ){
			 flag = false;
		 }
		 if(flag && this.board.elementAt(x, i) == chip.getChipValue()){
			 
			 Chip neighborChip = new Chip(x,i,this.turn);
			 neighborChip.setDirect(VERTICAL);
			 neighbors.insertBack(neighborChip);
		 }
	 }
	 flag = true;
	 for(int i=y+1; i<8; i++){
		 if(this.board.elementAt(x,i) == opponentValue ){
			 flag = false;
		 }
		 if(flag && this.board.elementAt(x, i) == chip.getChipValue()){
			 Chip neighborChip = new Chip(x,i,this.turn);
			 neighborChip.setDirect(VERTICAL);
			 neighbors.insertBack(neighborChip);
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
  public void diagnfCheck(Chip chip,List neighbors){
	  	int x = chip.getX();
		 int y = chip.getY();
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
				 Chip neighborChip = new Chip(i,j,this.turn);
				 neighborChip.setDirect(DIAGONALF);
				 neighbors.insertBack(neighborChip);
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
				 Chip neighborChip = new Chip(i,j,this.turn);
				 neighborChip.setDirect(DIAGONALF);
				 neighbors.insertBack(neighborChip);
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
  public void diagnbCheck(Chip chip, List neighbors){
	  	 int x = chip.getX();
		 int y = chip.getY();
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
				 Chip neighborChip = new Chip(i,j,this.turn);
				 neighborChip.setDirect(DIAGONALB);
				 neighbors.insertBack(neighborChip);
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
				 Chip neighborChip = new Chip(i,j,this.turn);
				 neighborChip.setDirect(DIAGONALB);
				 neighbors.insertBack(neighborChip);
			 }
		 }
  }

  public Connection findNetwork(){
    return null;
    
  }
  
  
	public static void main(String[] args) {
		List neighbor = new SList();
		MachinePlayer p1 = new MachinePlayer(WHITE_FIRST);
		p1.board.setElementAt(1, 1, WHITE);
		p1.board.setElementAt(1, 2, WHITE);
		p1.board.setElementAt(1, 3, WHITE);
		System.out.println("now the board looks like:");
		System.out.println(p1.board);
		
		Chip c1 = new Chip(1,1,WHITE_FIRST);
		p1.vertiCheck(c1, neighbor);
		System.out.println("check the vertical neighbors of (1,1):");
		System.out.println("now the neighbor list should look like:");
		System.out.println("[  (1,2) chip=o dir=| visited? x  (1,3) chip=o dir=| visited? x  ]");
		System.out.println("and this is your neighbor list:");
		System.out.println(neighbor);
		
		System.out.println("");
		System.out.println("check the vertical neighbors of (1,2):");
		Chip c2 = new Chip(1,2,WHITE_FIRST);
		neighbor = new SList();
		p1.vertiCheck(c2, neighbor);
		System.out.println("now the neighbor list should look like:");
		System.out.println("[  (1,1) chip=o dir=| visited? x  (1,3) chip=o dir=| visited? x  ]");
		System.out.println("and this is your neighbor list:");
		System.out.println(neighbor);
		System.out.println("");
		
		p1.board.setElementAt(0, 1, WHITE);
		p1.board.setElementAt(2, 1, WHITE);
		p1.board.setElementAt(5, 1, WHITE);
		p1.board.setElementAt(7, 1, WHITE);
		System.out.println("now the board looks like:");
		System.out.println(p1.board);
		System.out.println("check the horizontal neighbors of (1,1):");
		neighbor = new SList();
		p1.horizonCheck(c1, neighbor);
		System.out.println("now the neighbor list should look like:");
		System.out.println("[  (0,1) chip=o dir=-- visited? x  (2,1) chip=o dir=-- visited? x  (5,1) chip=o dir=-- visited? x  (7,1) chip=o dir=-- visited? x  ]");
		System.out.println("and this is your neighbor list:");
		System.out.println(neighbor);
		System.out.println("");
		
		p1.board.setElementAt(1, 6, BLACK);
		p1.board.setElementAt(1, 7, WHITE);
		p1.board.setElementAt(6, 1, BLACK);
		p1.board.setElementAt(2, 3, BLACK);
		p1.board.setElementAt(3, 4, WHITE);
		System.out.println("now the board looks like:");
		System.out.println(p1.board);
		
		System.out.println("check the horizontical neighbors of (1,1):");
		neighbor = new SList();
		p1.horizonCheck(c1, neighbor);
		System.out.println("now the neighbor list should look like:");
		System.out.println("[  (0,1) chip=o dir=-- visited? x  (2,1) chip=o dir=-- visited? x  (5,1) chip=o dir=-- visited? x  ]");
		System.out.println("and this is your neighbor list:");
		System.out.println(neighbor);
		System.out.println("");
		
		System.out.println("check the veritical neighbors of (1,2):");
		neighbor = new SList();
		p1.vertiCheck(c2, neighbor);
		System.out.println(neighbor);
		System.out.println("now the neighbor list should look like:");
		System.out.println("[  (1,1) chip=o dir=| visited? x  (1,3) chip=o dir=| visited? x  ]");
		System.out.println("and this is your neighbor list:");
		System.out.println(neighbor);
		System.out.println("");
		
		System.out.println("check the up-left to bottom-right neighbors of (1,2):");
		neighbor = new SList();
		p1.diagnfCheck(c2, neighbor);
		System.out.println("now the neighbor list should look like:");
		System.out.println("[  (0,1) chip=o dir=\\ visited? x  ]");
		System.out.println("and this is your neighbor list:");
		System.out.println(neighbor);
		System.out.println("");
		
		p1.board.setElementAt(2, 1, BLACK);
		p1.board.setElementAt(3, 0, WHITE);
		p1.board.setElementAt(0, 3, WHITE);
		System.out.println("now the board looks like:");
		System.out.println(p1.board);
		
		System.out.println("check the up-right to bottom-left neighbors of (1,2):");
		neighbor = new SList();
		p1.diagnbCheck(c2, neighbor);
		System.out.println(neighbor);
		System.out.println("now the neighbor list should look like:");
		System.out.println("[  (0,3) chip=o dir=/ visited? x  ]");
		System.out.println("and this is your neighbor list:");
		System.out.println(neighbor);
		System.out.println("");
		
		System.out.println("check all neighbors of (1,2):");
		System.out.println("order: | -- \\  /");
		neighbor = new SList();
		p1.vertiCheck(c2, neighbor);
		p1.horizonCheck(c2, neighbor);
		p1.diagnfCheck(c2, neighbor);
		p1.diagnbCheck(c2, neighbor);
		System.out.println("now the neighbor list should look like:");
		System.out.println("[  (1,1) chip=o dir=| visited? x  (1,3) chip=o dir=| visited? x  (0,1) chip=o dir=\\ visited? x  (0,3) chip=o dir=/ visited? x  ]");
		System.out.println("and this is your neighbor list:");
		System.out.println(neighbor);
		
		
		
	}
  

}
















