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
	// specify class variable for search depth
	public static int SEARCHDEPTH = 4;
	// record current chip numbers of machine player
	private int machineChipsNum;
	// record current chip numbers of opponent player
	private int oppoChipsNum;
	// record the current board
	private SimpleBoard board;
	// specify my turn (color), WHITE-FIRST or BLACK-SECOND
	final private int turn;
	// specify opponent's turn (color), WHITE-FIRST or BLACK-SECOND
	final private int opponent;
	// store current chips of machine player
	private Chip[] machineChips;
	// store current chips of opponent player
	private Chip[] opponentChips;
	/* store the current potential networks */
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

  
  
	// Creates a machine player with the given color. Color is either 0 (black)
	// or 1 (white). (White has the first move.)
	public MachinePlayer(int turn) {
		this(turn, SEARCHDEPTH);
	}
 
  
	// Creates a machine player with the given color and search depth. Color is
	// either 0 (black) or 1 (white). (White has the first move.)
	public MachinePlayer(int turn, int searchDepth) {
		if (turn == 0) {
			this.myName = "BLACK_SECOND";
		} else {
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

    /**
     * clone() creates a new player object, with exactly same field value
     * of the player being cloned. One exception is that
     * machinePaths & opponentPaths, these two fields would be renewed.
     */
	public MachinePlayer clone() {
		MachinePlayer player = new MachinePlayer(this.turn, MachinePlayer.SEARCHDEPTH);
		player.board = this.board.clone();
		player.machineChipsNum = this.machineChipsNum;
		player.oppoChipsNum = this.oppoChipsNum;
		player.myName = this.myName;
		Chip[] machineChips = player.machineChips;
		Chip[] opponentChips = player.opponentChips;
		for (int i = 0; i < this.machineChipsNum; i++) {
			machineChips[i] = this.machineChips[i];
		}
		for (int i = 0; i < this.oppoChipsNum; i++) {
			opponentChips[i] = this.opponentChips[i];
		}
		return player;
	}
  
	/**
	 * getBoard() returns the board field of the machine player
	 * @return board, the board of machine player
	 */
	public SimpleBoard getBoard() {
		return board;
	}
  
  
  
  
	// Returns a new move by "this" player. Internally records the move (updates
	// the internal game board) as a move by "this" player.
	public Move chooseMove() {
		Move m = null;
		if (this.machineChipsNum == 0) {
			Random random = new Random();
			if (this.turn == WHITE_FIRST) {
				m = new Move(0, random.nextInt(6) + 1);
			} else {
				m = new Move(random.nextInt(6) + 1, 0);
			}
		} else {
			BestMove bestMove = abtree(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, this.board, this.turn);
			m = bestMove.move;
			System.out.println(bestMove.score);
		}
		if (this.forceMove(m)) {
			return m;
		} else {
			System.out.println("Error: chooseMove method invalid!");
			return null;
		}
	}
  
   
	/**
	 * abtree() is the game tree to find the best move for chooseMove method,
	 * it uses alpha-beta pruning to speed up the process
	 * @param alpha, a score the computer knows with certainty it can achieve,
	 * begins with Integer.MIN_VALUE in chooseMove() method
	 * @param beta, the opponent can achieve a score of beta or better, begins with 
	 * Integer.MAX_VALUE in chooseMove() method
	 * @param searchDepth, the current depth level of recursion, begins with 0 in
	 * chooseMove() method 
	 * @param board, the board for performing potential moves, begins with this.board
	 * in chooseMove() method
	 * @param turn, the turn of the current recursion, begins with this.turn
	 * IN chooseMove() method
	 * @return myBest, a BestMove object to store the best move, with best score 
	 */
	public BestMove abtree(int alpha, int beta, int searchDepth, SimpleBoard board, int turn) {
		BestMove myBest = new BestMove();
		BestMove reply;
		Connection c1 = new Connection();
		Connection c2 = new Connection();
		// Compute the score of current board, as well as check if game ends
		this.findPaths(turn);
		c1 = this.checkPaths(turn);
		this.findPaths((turn + 1) % 2);
		c2 = this.checkPaths((turn + 1) % 2);
		Connection c = new Connection();
		c.setScore(c1.getScore() + c2.getScore());
		c.setEnd(c1.isEnd() || c2.isEnd());
		// If the game ends, or comes to the specified SEARCHDEPTH
		if (c.isEnd() || searchDepth == MachinePlayer.SEARCHDEPTH) {
			// Set the score to be current score, no extra move
			myBest.score = c.getScore();
			// Adjust the score according to winning chance and current depth
			if (c.isEnd()) {
				if (myBest.score > 0) {
					if (myBest.score - searchDepth > 0) {
						myBest.score -= searchDepth;
					} else {
						myBest.score = 5;
					}
				} else if (myBest.score < 0) {
					if (myBest.score + searchDepth < 0) {
						myBest.score += searchDepth;
					} else {
						myBest.score = -5;
					}
				}
			}
			return myBest;
		}

		if (turn == this.turn) {
			myBest.score = alpha;
		} else {
			myBest.score = beta;
		}
		// find all legal moves
		Move[] moves = this.findAllMoves(turn);
		myBest.move = moves[0];
		// for each legal move
		for (Move m : moves) {
			if (m == null) {
				break;
			}
			// record the current board
			MachinePlayer player = this.clone();
			// perform move m
			if (turn == player.turn) {
				player.forceMove(m);
			} else if (turn == player.opponent) {
				player.opponentMove(m);
			}
			// change the turn
			int nextTurn = turn == this.turn ? this.opponent : this.turn;
			// recursively call abtree for the next turn
			reply = player.abtree(alpha, beta, searchDepth + 1, player.board, nextTurn);
			// undo move m
			// If this turn is myTurn
			if ((turn == this.turn) && (reply.score > myBest.score)) {
				myBest.move = m;
				myBest.score = reply.score;
				alpha = reply.score;
			}
			// If this turn is opponent's turn
			else if (turn == this.opponent && reply.score < myBest.score) {
				myBest.move = m;
				myBest.score = reply.score;
				beta = reply.score;
			}
			// alpha - beta pruning
			if (alpha >= beta) {
				return myBest;
			}
		}
		return myBest;
	}
  
  
  
  /**
	 * findAllMovesStep() method is when the player play 10 chips and enter into step mode
	 * try to find the next possible move
	 * @param turn  current turn
	 * @param chip  current moving chip
	 * @return possible move array
	 */
	public Move[] findAllMovesStep(int turn) {
		Move[] moves = new Move[600];
		Chip[] chips = null;
		Move move = null;
		int count = 0;
		if (turn == this.turn) {
			chips = this.machineChips;
		} else {
			chips = this.opponentChips;
		}
		for (int i = 0; i < chips.length; i++) {
			for (int y = 0; y < DIMENSION; y++) {
				for (int x = 0; x < DIMENSION; x++) {
					move = new Move(x, y, chips[i].getX(), chips[i].getY());
					if (this.board.isValidMove(move, turn)) {
						moves[count] = move;
						count++;
					}
				}
			}
		}
		return moves;
	}
	
    /**
  	 * findAllMovesAdd() method is when the player doesn't play 10 chips
  	 * and find the next possible move
  	 * @param turn
  	 * @return
  	 */
	public Move[] findAllMovesAdd(int turn) {
		Move[] moves = new Move[60];
		Move move = null;
		int count = 0;
		for (int y = 0; y < DIMENSION; y++) {
			for (int x = 0; x < DIMENSION; x++) {
				move = new Move(x, y);
				if (this.board.isValidMove(move, turn)) {
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
	public Move[] findAllMoves(int turn) {
		// step condition,which at most has 400 possibilities
		Move[] stepMoves = new Move[400];
		// add condition, which at most has 60 possibilities
		Move[] addMoves = new Move[60];
		// tempary array
		Move[] tempMoves = new Move[40];
		int count = 0;
		if (turn == this.turn) {
			// step condition
			if (this.machineChipsNum == 10) {
				stepMoves = findAllMovesStep(turn);
				return stepMoves;
				// the add condition
			} else {
				addMoves = findAllMovesAdd(turn);
				return addMoves;
			}
		} else {
			// step condition
			if (this.oppoChipsNum == 10) {
				stepMoves = findAllMovesStep(turn);
				return stepMoves;
				// the add condition
			} else {
				addMoves = findAllMovesAdd(turn);
				return addMoves;
			}
		}
	}

	// If the Move m is legal, records the move as a move by the opponent
	// (updates the internal game board) and returns true. If the move is
	// illegal, returns false without modifying the internal state of "this"
	// player. This method allows your opponents to inform you of their moves.
	public boolean opponentMove(Move m) {
		if (board.isValidMove(m, opponent)) {
			Chip chip = new Chip(m, this.opponent);
			board.makeMove(m, opponent);
			// ADD
			if (m.moveKind == ADD) {
				this.opponentChips[oppoChipsNum] = chip;
				oppoChipsNum++;
			}
			// STEP
			if (m.moveKind == STEP) {
				Chip oldchip = new Chip(m.x2, m.y2, this.opponent);
				for (int i = 0; i < oppoChipsNum; i++) {
					if (opponentChips[i].equals(oldchip)) {
						opponentChips[i] = chip;
					}
				}
			}

			return true;
		}
		return false;
	}

	// If the Move m is legal, records the move as a move by "this" player
	// (updates the internal game board) and returns true. If the move is
	// illegal, returns false without modifying the internal state of "this"
	// player. This method is used to help set up "Network problems" for your
	// player to solve.
	public boolean forceMove(Move m) {
		if (board.isValidMove(m, turn)) {
			this.board.makeMove(m, turn);
			Chip chip = new Chip(m, turn);
			// ADD
			if (m.moveKind == ADD) {
				this.machineChips[machineChipsNum] = chip;
				machineChipsNum++;
			}
			// STEP
			if (m.moveKind == STEP) {
				Chip oldchip = new Chip(m.x2, m.y2, turn);
				for (int i = 0; i < machineChipsNum; i++) {
					if (machineChips[i].equals(oldchip)) {
						machineChips[i] = chip;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}  

  /**
   * findNeighbor() method is to find all of the given chip's neighbors
   * (if the neighbor is legal)
   * @param chip
   * @return
   */
	public List findNeighbor(Chip chip, int color) {
		List neighbors = new SList();
		int flag = chip.getDirect();
		if (flag != HORIZONTAL) {
			horizonCheck(chip, neighbors, color);
		}
		if (flag != VERTICAL) {
			vertiCheck(chip, neighbors, color);
		}
		if (flag != DIAGONALF) {
			diagnfCheck(chip, neighbors, color);
		}
		if (flag != DIAGONALB) {
			diagnbCheck(chip, neighbors, color);
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
	public void horizonCheck(Chip chip, List neighbors, int color) {
		int x = chip.getX();
		int y = chip.getY();
		Chip[] chips;
		if (color == turn) {
			chips = this.machineChips;
		} else {
			chips = this.opponentChips;
		}
		int opponentValue = chip.getChipValue() == WHITE ? BLACK : WHITE;
		boolean flag = true;
		for (int i = x - 1; i >= 0; i--) {
			if (this.board.elementAt(i, y) == opponentValue) {
				flag = false;
			}
			if (flag && this.board.elementAt(i, y) == chip.getChipValue()) {
				Chip neighborChip = null;
				for (int k = 0; k < chips.length; k++) {
					if (chips[k] != null) {
						if (chips[k].getX() == i && chips[k].getY() == y && !chips[k].isVisited()) {
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
		for (int i = x + 1; i < 8; i++) {
			if (this.board.elementAt(i, y) == opponentValue) {
				flag = false;
			}
			if (flag && this.board.elementAt(i, y) == chip.getChipValue()) {
				Chip neighborChip = null;
				for (int k = 0; k < chips.length; k++) {
					if (chips[k] != null) {
						if (chips[k].getX() == i && chips[k].getY() == y && !chips[k].isVisited()) {
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
	public void vertiCheck(Chip chip, List neighbors, int color) {
		int x = chip.getX();
		int y = chip.getY();
		Chip[] chips;
		if (color == turn) {
			chips = this.machineChips;
		} else {
			chips = this.opponentChips;
		}
		int opponentValue = chip.getChipValue() == WHITE ? BLACK : WHITE;
		boolean flag = true;
		for (int i = y - 1; i >= 0; i--) {
			if (this.board.elementAt(x, i) == opponentValue) {
				flag = false;
			}
			if (flag && this.board.elementAt(x, i) == chip.getChipValue()) {
				Chip neighborChip = null;
				for (int k = 0; k < chips.length; k++) {
					if (chips[k] != null) {
						if (chips[k].getX() == x && chips[k].getY() == i && !chips[k].isVisited()) {
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
		for (int i = y + 1; i < 8; i++) {
			if (this.board.elementAt(x, i) == opponentValue) {
				flag = false;
			}
			if (flag && this.board.elementAt(x, i) == chip.getChipValue()) {
				Chip neighborChip = null;
				for (int k = 0; k < chips.length; k++) {
					if (chips[k] != null) {
						if (chips[k].getX() == x && chips[k].getY() == i && !chips[k].isVisited()) {
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
	public void diagnfCheck(Chip chip, List neighbors, int color) {
		int x = chip.getX();
		int y = chip.getY();
		Chip[] chips;
		if (color == turn) {
			chips = this.machineChips;
		} else {
			chips = this.opponentChips;
		}
		int opponentValue = chip.getChipValue() == WHITE ? BLACK : WHITE;
		boolean flag = true;
		int i = x, j = y;
		while (i > 0 && j > 0) {
			i--;
			j--;
			if (this.board.elementAt(i, j) == opponentValue) {
				flag = false;
			}
			if (flag && this.board.elementAt(i, j) == chip.getChipValue()) {
				Chip neighborChip = null;
				for (int k = 0; k < chips.length; k++) {
					if (chips[k] != null) {
						if (chips[k].getX() == i && chips[k].getY() == j && !chips[k].isVisited()) {
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
		while (i < DIMENSION - 1 && j < DIMENSION - 1) {
			i++;
			j++;
			if (this.board.elementAt(i, j) == opponentValue) {
				flag = false;
			}
			if (flag && this.board.elementAt(i, j) == chip.getChipValue()) {
				Chip neighborChip = null;
				for (int k = 0; k < chips.length; k++) {
					if (chips[k] != null) {
						if (chips[k].getX() == i && chips[k].getY() == j && !chips[k].isVisited()) {
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
	public void diagnbCheck(Chip chip, List neighbors, int color) {
		int x = chip.getX();
		int y = chip.getY();
		Chip[] chips;
		if (color == turn) {
			chips = this.machineChips;
		} else {
			chips = this.opponentChips;
		}
		int opponentValue = chip.getChipValue() == WHITE ? BLACK : WHITE;
		boolean flag = true;
		int i = x, j = y;
		while (i < DIMENSION - 1 && j > 0) {
			i++;
			j--;
			if (this.board.elementAt(i, j) == opponentValue) {
				flag = false;
			}
			if (flag && this.board.elementAt(i, j) == chip.getChipValue()) {
				Chip neighborChip = null;
				for (int k = 0; k < chips.length; k++) {
					if (chips[k] != null) {
						if (chips[k].getX() == i && chips[k].getY() == j && !chips[k].isVisited()) {
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
		while (i > 0 && j < DIMENSION - 1) {
			i--;
			j++;
			if (this.board.elementAt(i, j) == opponentValue) {
				flag = false;
			}
			if (flag && this.board.elementAt(i, j) == chip.getChipValue()) {
				Chip neighborChip = null;
				for (int k = 0; k < chips.length; k++) {
					if (chips[k] != null) {
						if (chips[k].getX() == i && chips[k].getY() == j && !chips[k].isVisited()) {
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
  
	/**
	 * checkGoalArea() checks whether the specified chip is in goal area
	 * @param chip, the chip to be checked
	 * @return true if in goal area, otherwise false
	 */
	public boolean checkGoalArea(Chip chip) {
		if (chip.getChipValue() == BLACK) {
			if (chip.getY() == 0 || chip.getY() == DIMENSION - 1) {
				return true;
			}
		}
		if (chip.getChipValue() == WHITE) {
			if (chip.getX() == 0 || chip.getX() == DIMENSION - 1) {
				return true;
			}
		}
		return false;
	}
  
	/**
	 * checkGoalArea() checks whether the specified chip is in left/top goal area
	 * @param chip, the chip to be checked
	 * @return true, if a black chip in top goal area, or white chip in left goal area
	 * otherwise, false
	 */
	public boolean checkGoalAreaA(Chip chip) {
		if (chip.getChipValue() == BLACK) {
			if (chip.getY() == 0) {
				return true;
			}
		}
		if (chip.getChipValue() == WHITE) {
			if (chip.getX() == 0) {
				return true;
			}
		}
		return false;
	}
  
  
	/**
	 * checkGoalArea() checks whether the specified chip is in right/bottom goal area
	 * @param chip, the chip to be checked
	 * @return true, if a black chip in bottom goal area, or white chip in right goal area
	 * otherwise, false
	 */
	public boolean checkGoalAreaB(Chip chip) {
		if (chip.getChipValue() == BLACK) {
			if (chip.getY() == DIMENSION - 1) {
				return true;
			}
		}
		if (chip.getChipValue() == WHITE) {
			if (chip.getX() == DIMENSION - 1) {
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
	public void findPath(int color, Chip chip) {
		/* path store one possible path */
		List path = new SList();
		SList finalPath = new SList();
		/* paths store all possible paths of given chips */
		List paths = null;
		/* store each players chips which has already placed on the board */
		Chip[] chips = null;
		/* store the given chip's neighbors */
		List neighbors = null;
		Chip currentChip = null;
		Chip neighChip = null;
		boolean goalChip = false;
		/*
		 * flag is the flag to tell when is to insert the path, like pop after
		 * push that should work
		 */
		boolean flag = true;
		int pathLength = 0;
		if (color == turn) {
			paths = machinePaths;
			chips = this.machineChips;
		} else if (color == opponent) {
			paths = opponentPaths;
			chips = this.opponentChips;
		}
		for (Chip curr : chips) {
			if (curr != null) {
				curr.setVisited(false);
			}
		}
		// -----------using stack-like method to find the path-------------
		chip.setVisited(true);
		path.insertBack(chip);
		try {
			while (!path.isEmpty()) {
				currentChip = (Chip) (path.back().item());
				if (checkGoalArea(currentChip)) {
					goalChip = true;
				}
				neighbors = findNeighbor(currentChip, color);
				if (!neighbors.isEmpty()) {
					neighChip = (Chip) ((SList) neighbors).pop();
					if (checkGoalArea(neighChip)) {
						goalChip = true;
					}
					neighChip.setVisited(true);
					path.insertBack(neighChip);
					flag = true;
				} else {
					if (flag) {
						if (goalChip || (path.length() > pathLength)) {
							pathLength = path.length();
							finalPath = ((SList) path).Clone();
							paths.insertBack(finalPath);
							goalChip = false;
						}
					}
					((SList) path).pop();
					flag = false;
				}
			}
		} catch (InvalidNodeException e) {
			e.printStackTrace();
		}

	}
  
	/**
	 * findPaths find all potential paths for all current chips on the board
	 * with specified color
	 * @param color, the color to be specified
	 */
	public void findPaths(int color) {
		Chip[] chips = null;
		/* empty the current paths list */
		if (color == turn) {
			machinePaths = new SList();
			chips = this.machineChips;
		} else if (color == opponent) {
			opponentPaths = new SList();
			chips = this.opponentChips;
		}
		for (Chip chip : chips) {
			if (chip != null) {
				chip.setDirect(0);
				this.findPath(color, chip);
			}
		}
	}
  
	/**
	 * checkPaths() evaluates of all paths of specified color, computes the score
	 * for computer's confidence in winning the game, and checks whether the game
	 * comes to an end. It has to be called after findPaths.
	 * @param color, the specified color
	 * @return conn, a Connection object, record highest score of all paths, 
	 * and whether game comes to an end. 
	 */
	public Connection checkPaths(int color) {
		List paths = null;
		Connection conn = new Connection();
		/*
		 * count the number of chips in the goal area, like A is like the left
		 * area
		 */
		int countGoalAreaA = 0;
		int countGoalAreaB = 0;
		if (color == turn) {
			paths = machinePaths;
		} else if (color == opponent) {
			paths = opponentPaths;
		}
		int score;
		/* count the max score of the path */
		int maxScore = 0;
		ListNode node = paths.front();
		ListNode chipNode = null;
		List path = null;
		Chip chip = null;
		int count;
		int orderCount;
		try {
			while (node.isValidNode()) {
				path = (List) node.item();
				score = 0;
				countGoalAreaA = 0;
				countGoalAreaB = 0;
				chipNode = path.front();
				count = 0;
				orderCount = 0;
				while (chipNode.isValidNode()) {
					chip = (Chip) chipNode.item();
					count++;
					if (checkGoalAreaA(chip)) {
						countGoalAreaA++;
						score += 10;
						chipNode = chipNode.next();
						// check wheter this chip is the first chip or the last
						// chip
						if (count == 1 || count == path.length()) {
							orderCount++;
						}
						continue;
					}
					if (checkGoalAreaB(chip)) {
						countGoalAreaB++;
						score += 10;
						chipNode = chipNode.next();
						if (count == 1 || count == path.length()) {
							orderCount++;
						}
						continue;
					}
					score += 5;
					chipNode = chipNode.next();
				}
				if (countGoalAreaA > 1 || countGoalAreaB > 1) {
					score = 0;
				}
				if (countGoalAreaA == 1 && countGoalAreaB == 1 && orderCount == 2) {
					if (path.length() >= 6) {
						maxScore = 100;
						conn.setEnd(true);
						break;
					}
				}
				if (maxScore < score) {
					maxScore = score;
				}
				node = node.next();
			}
		} catch (InvalidNodeException e) {
			e.printStackTrace();
		}
		if (color == turn) {
			conn.setScore(maxScore);
		} else {
			conn.setScore(-maxScore);
		}
		return conn;

	}

}
















