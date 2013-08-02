import java.util.Observable;
import java.util.Random;

public class Model extends Observable {
	// the blank space left on the board
	private int blank;
	// indicate which player's turn
	private int playerTurn;
	// indicate the game status
	private int status;
	// indicate the winning type
	private int winType;
	// indicate the winning position
	private int winPos;
	// the 2D array used to record the tiles status
	private int tiles[][];
	// indicate the changed tile's position
	private int changedX;
	private int changedY;

	// indicate the mode of game, 0 for PVP, 1 for PVE
	private int mode;

	Model() {
		playerTurn = 0;
		blank = 9;
		status = 0;

		tiles = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				tiles[i][j] = -1;
			}
		}

		mode = 0;

		setChanged();
	}

	public int getChangedX() {
		return changedX;
	}

	public int getChangedY() {
		return changedY;
	}

	public int getChangedValue() {
		return tiles[changedX][changedY];
	}

	public int getTurn() {
		return playerTurn;
	}

	public int getBlanks() {
		return blank;
	}

	public int getStatus() {
		return status;
	}

	public int getWinType() {
		return winType;
	}

	public int getWinPos() {
		return winPos;
	}

	public int getMode() {
		return mode;
	}

	public void setTurn(int t) {
		playerTurn = t;
		setChanged();
		notifyObservers();
	}

	public void setStatus(int s) {
		status = s;
		setChanged();
		notifyObservers();
	}

	public void changeMode() {
		if (mode == 0) {
			mode++;
		} else {
			mode--;
		}
		setChanged();
		notifyObservers();
	}

	public void playerMove(int i, int j) {

		if (status == 0) {
			return;
		}

		if (status == 4 || status == 5 && blank == 0) {
			return;
		}

		int oldTurn = playerTurn;
		// player place X or O in cell[i][j]
		if (playerTurn == 0) {
			if (tiles[i][j] == -1) {
				tiles[i][j] = 0;
				playerTurn++;
				blank--;
				status = 2;
			} else {
				status = 3;
			}
		} else {
			// in PVE mode, never get here
			if (tiles[i][j] == -1) {
				tiles[i][j] = 1;
				playerTurn--;
				blank--;
				status = 2;
			} else {
				status = 3;
			}
		}

		win(oldTurn);

		changedX = i;
		changedY = j;

		setChanged();
		notifyObservers();
		
		// delay for AI thinking process
	    new Thread(new Runnable()
	    {
	        public void run()
	        {
	            try {
	                Thread.sleep(1500);
	            }catch (InterruptedException ignore){}
	    		if (mode == 1 && status == 2)
	    			AIMove();
	        }
	    }).start();		
		// if (mode == 1 && status == 2)
		// AIMove();
	}

	private void win(int turn) {
		// judge if anyone wins
		int result = -1;

		for (int i = 0; i < 3; i++) {
			if (tiles[i][0] == tiles[i][1] && tiles[i][1] == tiles[i][2]
					&& tiles[i][0] != -1) {
				result = 0;
				winType = 1;
				winPos = i;
			}
		}

		for (int j = 0; j < 3; j++) {
			if (tiles[0][j] == tiles[1][j] && tiles[1][j] == tiles[2][j]
					&& tiles[0][j] != -1) {
				result = 0;
				winType = 2;
				winPos = j;
			}
		}

		if (tiles[0][0] == tiles[1][1] && tiles[1][1] == tiles[2][2]
				&& tiles[0][0] != -1) {
			result = 0;
			winType = 3;
		}

		if (tiles[0][2] == tiles[1][1] && tiles[1][1] == tiles[2][0]
				&& tiles[0][2] != -1) {
			result = 0;
			winType = 4;
		}

		if (result == 0)
			playerTurn = turn;

		if (result == 0) {
			// win
			status = 4;
		} else if (result != 0 && blank == 0) {
			// draw
			status = 5;
		}
	}

	public void newGame() {
		// start or restart the game
		playerTurn = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				tiles[i][j] = -1;
			}
		}
		blank = 9;
		status = 0;
		setChanged();
		notifyObservers();
	}
	
	
	/* the AI's algorithm part */

	public void AIMove() {
		int placed = 0; // indicate if AI place the tile
		int toWin = 0; // indicate if AI only 1 step left to win

		Random generater = new Random();

		if (tiles[1][1] == -1) {
			changedX = 1;
			changedY = 1;
			placed = 1;
		} else {

			// case 1
			for (int i = 0; i < 3; i++) {
				if (tiles[i][0] == tiles[i][1] && tiles[i][0] != -1
						&& tiles[i][2] == -1) {
					changedX = i;
					changedY = 2;
					placed = 1;
					if (tiles[i][0] == 1) {
						toWin = 1;
						break;
					}
				}
				if (tiles[i][0] == tiles[i][2] && tiles[i][0] != -1
						&& tiles[i][1] == -1) {
					changedX = i;
					changedY = 1;
					placed = 1;
					if (tiles[i][0] == 1) {
						toWin = 1;
						break;
					}
				}
				if (tiles[i][1] == tiles[i][2] && tiles[i][0] == -1
						&& tiles[i][2] != -1) {
					changedX = i;
					changedY = 0;
					placed = 1;
					if (tiles[i][2] == 1) {
						toWin = 1;
						break;
					}
				}
			}

			// case 2
			for (int i = 0; i < 3; i++) {
				if (toWin == 1)
					break;
				if (tiles[0][i] == tiles[1][i] && tiles[0][i] != -1
						&& tiles[2][i] == -1) {
					changedX = 2;
					changedY = i;
					placed = 1;
					if (tiles[0][i] == 1) {
						toWin = 1;
						break;
					}
				}
				if (tiles[0][i] == tiles[2][i] && tiles[0][i] != -1
						&& tiles[1][i] == -1) {
					changedX = 1;
					changedY = i;
					placed = 1;
					if (tiles[0][i] == 1) {
						toWin = 1;
						break;
					}
				}
				if (tiles[1][i] == tiles[2][i] && tiles[0][i] == -1
						&& tiles[2][i] != -1) {
					changedX = 0;
					changedY = i;
					placed = 1;
					if (tiles[2][i] == 1) {
						toWin = 1;
						break;
					}
				}
			}

			// case 3
			if (tiles[0][0] == tiles[1][1] && tiles[0][0] != -1
					&& tiles[2][2] == -1 && toWin != 1) {
				changedX = 2;
				changedY = 2;
				placed = 1;
				if (tiles[1][1] == 1)
					toWin = 1;
			}
			if (tiles[0][0] == tiles[2][2] && tiles[0][0] != -1
					&& tiles[1][1] == -1 && toWin != 1) {
				changedX = 1;
				changedY = 1;
				placed = 1;
				if (tiles[2][2] == 1)
					toWin = 1;
			}
			if (tiles[1][1] == tiles[2][2] && tiles[0][0] == -1
					&& tiles[2][2] != -1) {
				changedX = 0;
				changedY = 0;
				placed = 1;
				if (tiles[1][1] == 1)
					toWin = 1;
			}

			// case 4
			if (tiles[0][2] == tiles[1][1] && tiles[0][2] != -1
					&& tiles[2][0] == -1) {
				changedX = 2;
				changedY = 0;
				placed = 1;
				if (tiles[0][2] == 1)
					toWin = 1;
			}
			if (tiles[0][2] == tiles[2][0] && tiles[0][2] != -1
					&& tiles[1][1] == -1) {
				changedX = 1;
				changedY = 1;
				placed = 1;
				if (tiles[0][2] == 1)
					toWin = 1;
			}
			if (tiles[1][1] == tiles[2][0] && tiles[0][2] == -1
					&& tiles[2][0] != -1) {
				changedX = 0;
				changedY = 2;
				placed = 1;
				if (tiles[1][1] == 1)
					toWin = 1;
			}

			// if not placed, just pick a blank cell to place
			while (placed == 0) {
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						if (tiles[i][j] == -1) {
							// take the blank cell randomly
							int randomInt = generater.nextInt(10);

							if (randomInt < 3) {
								changedX = i;
								changedY = j;
								placed = 1;
								break;
							}
						}
					}
					if (placed == 1)
						break;
				}
			}
		}
		int oldTurn = playerTurn;

		tiles[changedX][changedY] = 1;
		playerTurn--;
		blank--;
		status = 2;

		win(oldTurn);

		setChanged();
		notifyObservers();

	}

}
