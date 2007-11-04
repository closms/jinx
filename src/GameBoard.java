/* * GameBoard.java
 *
 * Michael Closson
 *
 */

/**
 * The GameBoard class represents the Hex game board.  The GameBoard class
 * serves two main functions.  First, it is passed to computer players so
 * thay can examine the board and decide what move to make.  Second, its
 * checkForWin() method is used to determine if the game has been won.
 * The checkForWin() method is called by the GameManager after each move.
 */

public class GameBoard {

	int iRedWidth;
	int iBlueWidth;

	private int[][] board;
	public double[][] voltage;
	private boolean[][] seen;

	/**
	 * Construct a new GameBoard, the dimensions of the board are the parameters.
	 *
	 * @param r The width of the red players side of the board.
	 * @param b The width of the blue players side of the board.
	 */

	public GameBoard( int r, int b ) {
		resize( r, b );
	}

	/**
	 * resize the game board.  The pieces on the old board are cleared.
	 *
	 * @param r The width of the red players side.
	 * @param b The width of the blue players side.
	 */

	public void resize( int r, int b ) {
		iRedWidth = r;
		iBlueWidth = b;
		board = new int[r][b];
		seen = new boolean[r][b];
		voltage = new double[r+2][b+2];
		for(int i = 0; i < r; i++ ) {
			for(int j = 0; j < b; j++ ) {
				seen[i][j] = false;
			}
		}

		// set voltages at edges of the board.
		// red player is -1,
		// blue player is +1 voltage.

		for(int i = 0; i < r+2; i++ ) {
			for(int j = 0; j < b+2; j++ ) {
				voltage[i][j] = 0;
			}
		}

		for(int i = 0; i < r+2; i++ ) {
			voltage[i][0] = -1;
			voltage[i][b+1] = -1;
		}

		for(int i = 0; i < b+2; i++ ) {
			voltage[0][i] = 1;
			voltage[r+1][i] = 1;
		}

		updateVoltages();


	}
	
	/**
	 * Marks a hex cell as occupied by a bead.  Player 1 is the red player, player 2 is the blue player.
	 * Calling move() with player number zero will clear the hex cell.
	 *
	 * @param x The x location of the cell.
	 * @param y The y location of the cell.
	 * @param player The number of the player who is occupying this cell.  Player number 0 means the cell is unoccupied.
	 */

	public void move( int x, int y, int player ) {
		board[x][y] = player;

		if( player == 0 ) {
			System.err.println("BROKEN\n");
		}
		else if( player == 1 ) {
			// Red Player
			voltage[x+1][y+1] = -1;
		}
		else if( player == 2 ) {
			// Blue Player
			voltage[x+1][y+1] = 1;
		}


		// scan over board computing the averages of the neighbours.
		updateVoltages();

	}

	/**
	 * Returns the width of the red players side of the board.
	 */

	public int getRedWidth() {
		return iRedWidth;
	}

	/**
	 * Returns the width of the blue players side of the board.
	 */

	public int getBlueWidth() {
		return iBlueWidth;
	}

	/**
	 * Returns true if the passed location is occupied by a players bead.
	 *
	 * @param a The x location of the cell.
	 * @param b the y location of the cell.
	 */

	public boolean isOccupied( int a, int b ) {
		if( a < 0 || a >= iRedWidth || b < 0 || b >= iBlueWidth )
			return true;
		return board[a][b] != 0;
	}

	/**
	 * Checks the board for a winner.  Returns 0 for no winner, 1 for Red and 2 for Blue.
	 */

	public int checkForWin() {
		int i;

		// check red win.
		for( i = 0; i < iRedWidth; i++ )
			if( cfw_r( i, 0, -1, -1, 1 ) ) return 1;
		// check blue win.
		for( i = 0; i < iBlueWidth; i++ )
			if( cfw_r( 0, i, -1, -1, 2 ) ) return 2;

		return 0;
	}

	private boolean cfw_r( int sx, int sy, int px, int py, int p ) {
		int nx, ny;


		if( sx < 0 || sy < 0 || sx >= iRedWidth || sy >= iBlueWidth ) { return false; }
		if( seen[sx][sy] == true ) return false;
		if( board[sx][sy] != p ) { return false; }
		if( p == 1 && sy == iBlueWidth-1 ) { return true; }
		if( p == 2 && sx == iRedWidth-1 ) { return true; }

		seen[sx][sy] = true;

		// check up.
		nx = sx-1; ny = sy+1;
		if( (nx!=px||ny!=py)&&cfw_r( nx, ny, sx, sy, p ) ) {
			seen[sx][sy] = false;
			return true;
		}
		// check ne.
		nx = sx; ny = sy+1;
		if( (nx!=px||ny!=py)&&cfw_r( nx, ny, sx, sy, p ) ) {
			seen[sx][sy] = false;
			return true;
		}
		// check se.
		nx = sx+1; ny = sy;
		if( (nx!=px||ny!=py)&&cfw_r( nx, ny, sx, sy, p ) ) {
			seen[sx][sy] = false;
			return true;
		}
		// check down.
		nx = sx+1; ny = sy-1;
		if( (nx!=px||ny!=py)&&cfw_r( nx, ny, sx, sy, p ) ) {
			seen[sx][sy] = false;
			return true;
		}
		// check sw.
		nx = sx; ny = sy-1;
		if( (nx!=px||ny!=py)&&cfw_r( nx, ny, sx, sy, p ) ) {
			seen[sx][sy] = false;
			return true;
		}
		// check nw.
		nx = sx-1; ny = sy;
		if( (nx!=px||ny!=py)&&cfw_r( nx, ny, sx, sy, p ) ) {
			seen[sx][sy] = false;
			return true;
		}

		seen[sx][sy] = false;
		return false;
		
	}

	private void updateVoltages()
	{
		double volt = 0;

		for( int a = 0; a < 50; a++ ) {

			for(int i = 1; i < iRedWidth+1; i++ ) {
				for(int j = 1; j < iBlueWidth+1; j++ ) {
				
					if( board[i-1][j-1] == 0 ) {
						volt = voltage[i-1][j+1]
						     + voltage[i  ][j+1]
							 + voltage[i+1][j  ]
							 + voltage[i+1][j-1]
							 + voltage[i  ][j-1]
							 + voltage[i-1][j  ];
	
						volt /= 6;
						voltage[i][j] = volt;
					}
				}
			}

		}

	}



}


