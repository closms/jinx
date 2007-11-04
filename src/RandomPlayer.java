/*
 * RandomPlayer.java
 *
 * Michael Closson
 */

import java.awt.*;
import java.util.*;


public class RandomPlayer implements HexPlayer {

	Random rand = null;

	public RandomPlayer() {
		rand = new Random();
	}


	public Point nextMove( GameBoard gb, GameState gs ) {
		int a, b;

		// pull random numbers until an empty space is found.

		while( true ) {
			a = rand.nextInt( gb.getRedWidth() );
			b = rand.nextInt( gb.getBlueWidth() );

			if( ! gb.isOccupied( a, b ) )
				return new Point( a, b );
		}
	}

	public void undo() {}

}


