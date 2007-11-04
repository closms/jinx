/*
 * GameManager.java
 *
 * Michael Closson
 *
 */

import java.lang.*;
import java.awt.*;

/**
 * The GameManager class drives the game.  The GameManager controls
 * which player plays next and calls the nextMove() method of the
 * appropriate player.  The GameManager also handles the requests to
 * undo a move.
 * <p>
 * There is one GameManager per game.  When a new game is started, the
 * old game manager is stopped and a new game manager is created.
 */

public class GameManager extends Thread {

	HexPlayer RedPlayer = null;
	HexPlayer BluePlayer = null;
	HexPlayer CurrentPlayer = null;

	HexCanvas hexCanvas = null;

	GameState gameState = null;

	PlayerPanel playerPanel = null;

	boolean won = false;

	/**
	 * Create a new game manager.
	 *
	 * The game manager needs a reference to the HexCanvas so it can
	 * pass the HexCanvas to a HumanPlayer object, if one is needed.
	 * The game manager also needs a reference to the PlayerPanel
	 * so it can show whos turn it is and who was won the game.
         *
	 * @param hc The HexCanvas object.  The HexCanvas object is created
         *           in the JHex class.
	 * @param pp The PlayerPanel object.  The PlayerPanel object is
         *            created in the JHex class.
	 */

	public GameManager( HexCanvas hc, PlayerPanel pp ) {
		playerPanel = pp;
		hexCanvas = hc;
		String rp = pp.getRedPlayer();
		String bp = pp.getBluePlayer();

		if( rp.equals("Human Player") )
			RedPlayer = new HumanPlayer( hexCanvas );
		else if( rp.equals("Random Player") )
			RedPlayer = new RandomPlayer();
		else if( rp.equals("SneakyPete") )
			RedPlayer = new SneakyPete();
		else
			System.out.println("HUH!  I don't know how to handle "
                                           + "red player " +rp );

		if( bp.equals("Human Player") )
			BluePlayer = new HumanPlayer( hexCanvas );
		else if( bp.equals("Random Player") )
			BluePlayer = new RandomPlayer();
		else
			System.out.println("HUH!  I don't know how to handle "
                                           + "blue player " +bp );

		CurrentPlayer = RedPlayer;

		gameState = new GameState();

		playerPanel.reset();

		won = false;
    }

	/**
	 * The undo method undoes the last move made by a human player.
	 * 
	 * The undo method in the GameManager class is called by the undo
	 * method in the JHex class.  The undo method in the JHex class is
	 * called by the ButtonPanel class.  The ButtonPanel class does not
	 * call the GameManager class's undo method directly because the
	 * GameManager changes with each game.
	 */

	public void undo() {
		System.out.println("GameManager::undo()");

		// If there is 2 human players, undo the last move.

		System.out.println("RedPlayer is "+RedPlayer.getClass().getName());
		System.out.println("BluePlayer is "+BluePlayer.getClass().getName());
		if( RedPlayer.getClass().getName().equals("HumanPlayer") &&
			BluePlayer.getClass().getName().equals("HumanPlayer") )
		{
			// Get last move and fix gameState
			Point p = gameState.lastMove();
			if( p == null )
				return;

			gameState.clearLastMove();

			// Clear board position.

			Globals.gameBoard.move(p.x, p.y, 0);
			hexCanvas.markCell( p.x, p.y, 0 );

			// Fix current player.
			if( CurrentPlayer == RedPlayer ) {
				CurrentPlayer = BluePlayer;
			}
			else {
				CurrentPlayer = RedPlayer;
			}
			won = false;
			playerPanel.nextTurn();
		}
		else if( won ) { // FIXME: if won and winner is human player.
			Point p = gameState.lastMove();
			if( p == null )
				return;
			gameState.clearLastMove();
		}
		else {
			// If there is 1 human player, what to do depends on if
			// the computer player has gone yet.
			
			// Assume that the computer has gone, undo 2 moves.
			System.out.println("Undoing computer move.");
			Point p = gameState.lastMove();
			if( p == null )
				return;
			gameState.clearLastMove();
			// Clear board position.
			Globals.gameBoard.move(p.x, p.y, 0);
			hexCanvas.markCell( p.x, p.y, 0 );
			System.out.println("Undoing human move.");
			p = gameState.lastMove();
			if( p == null )
				return;
			gameState.clearLastMove();
			// Clear board position.
			Globals.gameBoard.move(p.x, p.y, 0);
			hexCanvas.markCell( p.x, p.y, 0 );
		}
	}

	/**
	 * The run method is the main processing loop of the GameManager.
	 *
	 * The run method calls the nextMove method of the two players,
	 * updates the HexCanvas and the PlayerPanel's label.
	 * The run method also checks for a winner and halts the game
	 * when someone has own.
	 */

    public void run() {
		Point p;
		while( true ) {
			if( won ) {
				try {
					Thread.sleep( 10 );
				}
				catch( InterruptedException ie ) {}
				continue;
			}
			// Get a move.
			p = CurrentPlayer.nextMove( Globals.gameBoard,
                                                    gameState );
			if( p == null ) {
				System.out.println("nextMove returned NULL.");
				continue;
			}
			// check if current move is valid.
			if( Globals.gameBoard.isOccupied( p.x, p.y ) ) {
				continue;
			}

			// Change player.
			if( CurrentPlayer == RedPlayer ) {
				CurrentPlayer = BluePlayer;
				hexCanvas.markCell( p.x, p.y, 1 );
				Globals.gameBoard.move(p.x, p.y, 1);
			}
			else {
				CurrentPlayer = RedPlayer;
				hexCanvas.markCell( p.x, p.y, 2 );
				Globals.gameBoard.move(p.x, p.y, 2);
			}
			gameState.addMove( p );

			playerPanel.nextTurn();

			// Check for a win.
			int i;
			if( (i=Globals.gameBoard.checkForWin()) != 0 ) {
				System.out.println("WIN!");
				playerPanel.gameWon( i );
				won = true;
			}

		}
	}


}


