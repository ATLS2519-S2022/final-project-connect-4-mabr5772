/* *****************************************************************************
 * Title:            (Alipha Beta Player)
 * Files:            (list of source files)
 * Semester:         Spring 2022
 * 
 * Author:           (Kenzie Braun, mabr5772@colorado.edu)
 * 
 * Description:		 (This program creates a new player who avoids unnecessary nodez)
 * 
 * Written:       	 (04/25/22)
 * 
 * Credits:          (Abby Hakewill, Apoorva Kanekal, https://www.youtube.com/watch?v=xBXHtz4Gbdo)
 **************************************************************************** */


import java.util.ArrayList;


public class AlphaBetaPlayer implements Player {
	private int enemyId;
	private int id;
	private int cols;


	public String name() {
		return "alphaBeta";
	}

	public void init(int id,  int rows, int cols, int msecPerMove) {
		this.enemyId = 3 - id;
		this.cols = cols;
		this.id = id;

	}

	
	
	public void calcMove(Connect4Board board, int oppMoveCol, Arbitrator arb) throws TimeUpException{
		if(board.isFull())
			throw new Error ("The board is full!");

		GameTree root = new GameTree(-1, board);

		
		for(int i = 0; i < cols; i++) {
			if(!board.isColumnFull(i)){
				board.move(i, id);
				root.addChild(i, new Connect4Board(board));
				board.unmove(i, id);
			}
		}

		int searchDepth = 1;

		
		while (!arb.isTimeUp() && searchDepth <= board.numEmptyCells()) {
			alphabeta(root, searchDepth, true, Integer.MIN_VALUE, Integer.MAX_VALUE , arb);
			arb.setMove(root.chosenMove);
			searchDepth++;
		
		}
	}

	
	private int alphabeta(GameTree node, int depth, boolean maxminizingPlayer, int alpha, int beta, Arbitrator arb) {
		if (depth == 0 || node.isTerminal() || arb.isTimeUp()) {
			node.value = evaluateNode(node);
			return node.value;
		}

		if (node.isLeaf()) {

			int moveId = maxminizingPlayer ? id : enemyId;

			for(int i = 0; i < cols; i++) {
				if(!node.board.isColumnFull(i)) {
					node.board.move(i, moveId);

					node.addChild(i, new Connect4Board(node.board));
					node.board.unmove(i, moveId);

				}
			}
		}

		if(maxminizingPlayer) {
			int value = Integer.MIN_VALUE;
			for(GameTree child: node.children) {
				int newVal = alphabeta(child, depth -1, false, alpha, beta, arb);
				if(newVal > value) {
					value = newVal;
					node.value = value;
					node.chosenMove = child.move;
				}
				else if(newVal == value) {
					int currMoveDistFromCenter = Math.abs(cols/2 - node.chosenMove);
					int newMoveDistFromCenter = Math.abs(cols/2 - child.move);
					if(newMoveDistFromCenter < currMoveDistFromCenter)
						node.chosenMove = child.move;
				}
				if (alpha < value) {
					alpha = value;
				}
				if(alpha >= beta) {
					break;
				}
			}
			return value;
		}
		else {
			int value = Integer.MAX_VALUE;
			for(GameTree child: node.children) {
				int newVal = alphabeta(child, depth -1, true, alpha, beta, arb);
				if(newVal < value) {
					value = newVal;
					node.value = value;
					node.chosenMove = child.move;
				}
				else if (newVal == value) {
					int currMoveDistFromCenter = Math.abs(cols/2 - node.chosenMove);
					int newMoveDistFromCenter = Math.abs(cols/2 - child.move);
					if(newMoveDistFromCenter < currMoveDistFromCenter)
						node.chosenMove = child.move;
				
				}
				if (beta > value) {
					beta = value;
				}
				if (alpha >= beta) {
					break;
				}

			}
			return value;
		}

	}


	
	private int evaluateNode(GameTree node) {
		int myScore = calcScore(node.board, id);
		int oppScore = calcScore(node.board, enemyId);
		return myScore - oppScore;
	}


	private int calcScore(Connect4Board board, int id) {
		final int rows = board.numRows();
		final int cols = board.numCols();
		int score = 0;

		// check vertically
		for (int c = 0; c < cols; c++) {
			for (int r = 0; r <= rows - 4; r++) {
				if(board.get(r + 0, c) != id) continue;
				if(board.get(r + 1, c) != id) continue;
				if(board.get(r + 2, c) != id) continue;
				if(board.get(r + 3, c) != id) continue;
				score++;
			}
		}

		//check horizontally
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c <= cols - 4; c++) {
				if(board.get(r, c + 0) != id) continue;
				if(board.get(r, c + 1) != id) continue;
				if(board.get(r, c + 2) != id) continue;
				if(board.get(r, c + 3) != id) continue;
				score++;
			}
		}

		// check diagonally 
		for (int c = 0; c <= cols - 4; c++) {
			for (int r = 0; r <= rows - 4; r++) {
				if(board.get(r + 0, c + 0) != id) continue;
				if(board.get(r + 1, c + 1) != id) continue;
				if(board.get(r + 2, c + 2) != id) continue;
				if(board.get(r + 3, c + 3) != id) continue;
				score++;
			}
		}

		// check diagonally
		for (int c = 0; c <= cols - 4; c++) {
			for (int r = rows -1; r >= 3; r--) {
				if (board.get(r - 0, c + 0) != id) continue;
				if (board.get(r - 1, c + 1) != id) continue;
				if (board.get(r - 2, c + 2) != id) continue;
				if (board.get(r - 3, c + 3) != id) continue;
				score++;
			}
	}
		
		return score;

	}

	
	
	private class GameTree{
		private Connect4Board board;
		private int move;
		private ArrayList<GameTree> children;
		private int chosenMove;
		private int value;

		public GameTree(int move, Connect4Board board) {
			this.board = board;
			this.move = move;
			children = new ArrayList<GameTree>();
		}

	
		public void addChild(int move, Connect4Board board) {
			children.add(new GameTree(move,board));
		}

		public boolean isLeaf() {
			return children.size() == 0;
		}

		
		public boolean isTerminal() {
			return board.isFull();
			
		}
	}
}
