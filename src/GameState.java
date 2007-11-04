/*
 * GameState.java
 *
 * Michael Closson
 *
 */

import java.util.*;
import java.awt.*;

/**
 * The GameState class keeps track of each move.
 *
 * The principle purpose of the GameState class is to facilitate undoing
 * moves.
 */

public class GameState {

	Vector moves;

	/**
	 * Create a new GameState object.
	 */

	public GameState() {
		moves = new Vector();
	}

	/**
	 * The GameManager calls addMove() after each move.
	 *
	 * @param p The location of the move.
	 */

	public void addMove( Point p ) {
		moves.add( p );
	}

	/**
	 * lastMove returns the last move made or null is no moves have been made.
	 */

	public Point lastMove() {
		if( moves.size() == 0 )
			return null;
		return (Point) moves.get( moves.size()-1 );
	}

	/**
	 * clearLastMove removes the last move made fromt the move stack.
	 */

	public void clearLastMove() {
		if( moves.size() == 0 )
			return;
		moves.removeElementAt( moves.size()-1 );
		return;
	}

}


