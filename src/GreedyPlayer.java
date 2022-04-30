/* *****************************************************************************
 * Title:            (Greedy Player)
 * Files:            (list of source files)
 * Semester:         Spring 2022
 * 
 * Author:           (Kenzie Braun, mabr5772@colorado.edu)
 * 
 * Description:		 (This program creates a new AI  player)
 * 
 * Written:       	 (04/25/22)
 * 
 * Credits:          (Abby Hakewill, Apoorva Kanekal)
 **************************************************************************** */
public class GreedyPlayer implements Player {
	private int cols;
	private int id;
	private int enemyId;

	
	private Move[] possibleMoves;

	
	public String name() {
		return "Greedy";
	}

	public  void init(int id, int rows, int cols, int msecPerMove) {
		enemyId = 3-id;
		this.cols = cols;
		this.id = id;

	}

	public void calcMove(Connect4Board board, int oppMoveCol, Arbitrator arb) throws TimeUpException{
		if (board.isFull())
			throw new Error("The board is full");

		possibleMoves = new Move[cols];

		for(int c = 0; c < cols; c++ ) {
			if(board.isValidMove(c)) {
				board.move(c ,id);
				int moveValue = evaluateBoard(board, id, enemyId);
				possibleMoves[c] = new Move(c, moveValue);
				board.unmove(c, id);
				
				
			}
	}
		
		Move bestMove = null;
		for (int i = 0; i < possibleMoves.length; i++) {
			if(bestMove == null)
				bestMove = possibleMoves[i];

			else if (possibleMoves[i] != null && bestMove.compareTo(possibleMoves[i]) < 0)
				bestMove = possibleMoves[i];
		
		}
		arb.setMove(bestMove.column);
	}


	private int evaluateBoard(Connect4Board board, int enemyId, int myId) {
		int enemyScore = calcScore(board, enemyId);
		int myScore = calcScore(board, myId);
		
		return myScore - enemyScore;
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

	private class Move implements Comparable<Move>{
		private int value;
		private int column;

		
		public Move(int column, int value) {
			this.value = value;
			this.column = column;
		}

		public int compareTo(Move other) {
			return Integer.compare(this.value, other.value);
		}
	}
}