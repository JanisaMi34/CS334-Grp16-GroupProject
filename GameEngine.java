package edu.txst.project;

public class GameEngine {
	private Board board;
	private int playerRow;
	private int playerCol;
	private int exitRow;
	private int exitCol;
	// Added coins field variable
	private int coins = 0;

	// Cell Type Constants
	private static final int FLOOR = 0;
	private static final int WALL = 1;
	private static final int COIN = 2;
	private static final int EXIT = 5;
	private static final int PLAYER = 6;
	private static final int HACKER = 7;

	public GameEngine(Board board) {
		this.board = board;
		findPlayer();
		findExit();
	}

	public boolean playerWins() {
		if(playerRow == exitRow && playerCol == exitCol){
			return true;
		}
		return false;
	}

	private void findPlayer() {
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 10; c++) {
				if (board.getCell(r, c) == PLAYER) {
					playerRow = r;
					playerCol = c;
					return;
				}
			}
		}
	}

	private void findExit() {
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 10; c++) {
				if (board.getCell(r, c) == EXIT) {
					System.out.println("test");
					exitRow = r;
					exitCol = c;
					return;
				}
			}
		}
	}

	/**
	 * Attempts to move the player.
	 * 
	 * @param dRow Change in row (-1, 0, 1)
	 * @param dCol Change in column (-1, 0, 1)
	 */
	public void movePlayer(int dRow, int dCol) {
		int targetRow = playerRow + dRow;
		int targetCol = playerCol + dCol;
		int targetCell = board.getCell(targetRow, targetCol);

		// Check for Walls or Out of Bounds
		if (targetCell == WALL || targetCell == -1) {
			return; // Movement blocked
		}

		// Added check to see if target cell has a coin
		if (targetCell == COIN){
			coins++;
			// System.out.println("Coins: " + coins);  // Testing code for seeing if the system is counting coins correctly in the engine
		}

		if (targetCell == HACKER) {

			board.stepCounter.decreaseSteps(2);
		}

		// Move the Player
		// Current position becomes Floor (or Goal if player was standing on one)
		// Note: For simplicity, this engine assumes player replaces the cell.
		// If you want "Player on Goal", you'd add a 6th constant.
		board.setCell(playerRow, playerCol, FLOOR);

		playerRow = targetRow;
		playerCol = targetCol;
		board.setCell(playerRow, playerCol, PLAYER);
		board.stepCounter.increaseSteps();

	}

	/**
 	* Returns the total number of coins collected by the player in the current game.
 	*
 	* @return the current coin count
 	*/
	public int getCoins(){
		return coins;
	}
}
