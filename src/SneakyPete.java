/*
 * SneakyPete.java
 *
 * Michael Closson
 */

import java.awt.*;
import java.util.*;

class PointPair {
	Point a, b;
	public String toString() {
		return "("+a.x+","+a.y+"):("+b.x+","+b.y+")";
	}
}

class SneakyState {
	int cx;
	int cy;
	int nelx;
	int nely;
	int swlx;
	int swly;
	boolean nevc;
	boolean swvc;
	Vector vc;
}

/**
 * The SneakyPete class implements the Sneaky Pete computer
 * player algorithm.
 */

public class SneakyPete implements HexPlayer {

	Random rand = null;
	int cx = -1;
	int cy = -1;

	int nelx = -1;
	int nely = -1;

	int swlx = -1;
	int swly = -1;

	Vector vc = null;

	boolean nevc = false;
	boolean swvc = false;

	Stack undoStack = null;

	public SneakyPete() {
		rand = new Random();
		vc = new Vector();
		undoStack = new Stack();
	}

	private void saveState( SneakyState s ) {
		s.cx = cx; s.cy = cy;
		s.nelx = nelx; s.nely = nely;
		s.swlx = swlx; s.swly = swly;
		s.nevc = nevc;
		s.swvc = swvc;
		s.vc = (Vector) vc.clone();
	}

	public Point nextMove( GameBoard gb, GameState gs ) {
		SneakyState s = new SneakyState();
		Point p = null;

		// Save the internal state in case of an undo.

		saveState( s );
		undoStack.push( s );

		try {
			p = nextMove_s( gb, gs );
		}
		catch( Exception e ) {
			return randMove( gb, gs );
		}

		if( p.x < 0 ) p.x = 0;
		if( p.x >= gb.iRedWidth ) p.x = (gb.iRedWidth-1);
		if( p.y < 0 ) p.y = 0;
		if( p.y >= gb.iBlueWidth ) p.y = (gb.iBlueWidth-1);

		if( gb.isOccupied( p.x, p.y ) ) return randMove( gb, gs );

		return p;
	}

	private Point nextMove_s( GameBoard gb, GameState gs ) {

		// if this is the first move, choose the centre hex.
		if( gs.lastMove() == null ) {
			cx = (gb.iRedWidth)/2;
			cy = (gb.iBlueWidth)/2;
			nelx = swlx = cx;
			nely = swly = cy;
			return new Point( cx, cy );
		}

		// what was the last players move?
		Point p = gs.lastMove();
		System.out.println("other player move is "+p.x+", "+p.y);
		//System.out.println("centre is "+cx+", "+cy);

		// If player played in a virtual connection, play the other side.
		for( int ix = 0; ix < vc.size(); ix++ ) {
			PointPair vcp = (PointPair) vc.get(ix);
			if( p.x == vcp.a.x && p.y == vcp.a.y ) {
				// remove this vc pair from the vector and return the other point.
				vc.remove( ix );
				return vcp.b;
			}
			if( p.x == vcp.b.x && p.y == vcp.b.y ) {
				// remove this vc pair from the vector and return the other point.
				vc.remove( ix );
				return vcp.a;
			}
		}

		// have we made a sw virtual connection but not a ne vc?
		if( swvc && !nevc ) {
			System.out.println("I should work on the NE virtual.");
		}

		// have we made a ne virtual connection but not a sw vc?
		if( !swvc && nevc ) {
			System.out.println("I should work on the SW virtual.");
		}

		// do we have both virtual connections?
		if( swvc && nevc )
			return fillVcs( gb );

		// player played in sw hex.
		System.out.println("sw hex p:"+p+" swlx,swly:"+swlx+","+swly);
		if( p.x == swlx && p.y == (swly-1) && !swvc ) {
			swlx = swlx+1;
			swly = swly-1;
			// check if this spot is already occupied.
			if( gb.isOccupied( swlx, swly ) ) {
				System.out.println("Grrr.  I'm beat.");
				return randMove( gb, gs );
			}
			if( swly == 0 )
				swvc = true;
			return new Point( swlx, swly );
		}

		// player played in ne hex.
		System.out.println("ne hex p:"+p+" nelx,nely:"+nelx+","+nely);
		if( p.x == nelx && p.y == (nely+1) && !nevc ) {
			nelx = nelx-1;
			nely = nely+1;
			// check if this spot is already occupied.
			if( gb.isOccupied( nelx, nely ) ) {
				System.out.println("Grrr.  I'm beat.");
				return randMove( gb, gs );
			}
			if( nely == (gb.iBlueWidth-1) )
				nevc = true;
			return new Point( nelx, nely );
		}

		// Which vc should I extend, sw or ne?  It depends on where the other player plays.
		// If the other player plays in the sw half of the board, extend the sw vc.
		// o/w extend the ne vc.

		boolean extendsw = (p.y < cy);

		// if we want to extend the sw vc, but we already have a swvc, then extend the nevc.
		if( swvc && extendsw )
			extendsw = false;

		// the extend ne case is similar.
		if( nevc & !extendsw )
			extendsw = true;

		// player played in west pie
		System.out.println("w p:"+p+" swlx,swly:"+swlx+","+swly);
		if( extendsw && p.x <= swlx && !swvc )
			return westPie(gb);

		// player played in east pie
		System.out.println("e p:"+p+" nelx,nely:"+nelx+","+nely);
		if( !extendsw && p.x >= nelx && !nevc )
			return eastPie(gb);

		// player played in the south pie
		System.out.println("s p:"+p+" swlx,swly:"+swlx+","+swly);
		if( extendsw && p.x > swlx && !swvc )
			return southPie(gb);

		// player played in the north pie
		System.out.println("n p:"+p+" nelx,nely:"+nelx+","+nely);
		if( !extendsw && p.x < nelx && !nevc )
			return northPie(gb);

		System.out.println("GRRR!!!! Uhh, I give up, here is a random move.");
		return randMove( gb, gs );
	}

	private Point randMove( GameBoard gb, GameState gs ) {
		int a, b;

		// pull random numbers until an empty space is found.

		while( true ) {
			a = rand.nextInt( gb.getRedWidth() );
			b = rand.nextInt( gb.getBlueWidth() );

			if( ! gb.isOccupied( a, b ) ) {
				return new Point( a, b );
			}
		}
	}

	private Point westPie(GameBoard gb) {
		System.out.println("Playing in west pie");
		// SneekyPete is trying to make a new virtual. But, if one of the
		// spots is already filled, grab the other one.
		//if( gb.isOccupied( swlx,   swly-1 ) ) { swlx++;swly--; return new Point( swlx, swly ); }
		if( gb.isOccupied( swlx, swly-1 ) ) {
			swly++; swly++;
			if( gb.isOccupied( swlx, swly ) ) { System.out.println("I'm confused."); return randMove( gb, null ); }
			return new Point( swlx, swly );
		}
		//if( gb.isOccupied( swlx+1, swly-1 ) ) { swly--;        return new Point( swlx, swly ); }
		if( gb.isOccupied( swlx+1,   swly-1 ) ) {
			swly++;
			if( gb.isOccupied( swlx, swly ) ) { System.out.println("I'm confused."); return randMove( gb, null ); }
			return new Point( swlx, swly );
		}
		// move the end of our path closer to the sw side of the board.
		swlx = swlx + 1;
		swly = swly - 2;
		// track virtual connection incase player plays there.
		PointPair pp = new PointPair();
		pp.a = new Point( swlx-1, swly+1 );
		pp.b = new Point( swlx, swly+1 );
		vc.add( pp );
		System.out.println("New vc pair "+pp);
		// check if this move connects us to the sw side.
		// case 1: we are touching the sw side.
		if( swly == 0 )
			swvc = true;
		// case 2: we are virtually touching the sw side.
		if( swly == 1 ) {
			// add the vc
			PointPair pp2 = new PointPair();
			pp2.a = new Point( swlx, 0 );
			pp2.b = new Point( swlx+1, 0 );
			vc.add( pp2 );
			swvc = true;
		}
		// return the move that sets up a new virtual connection.
		return new Point( swlx, swly );
	}

	private Point eastPie(GameBoard gb) {
		System.out.println("Playing in east pie");
		// SneekyPete is trying to make a new virtual. But, if one of the
		// spots is already filled, grab the other one.
		if( gb.isOccupied( nelx-1, nely+1 ) ) {
			nely++;
			if( gb.isOccupied( nelx, nely ) ) { System.out.println("I'm confused."); return randMove( gb, null ); }
			return new Point( nelx, nely );
		}
		if( gb.isOccupied( nelx,   nely+1 ) ) {
			nelx--; nely++;
			if( gb.isOccupied( nelx, nely ) ) { System.out.println("I'm confused."); return randMove( gb, null ); }
			return new Point( nelx, nely );
		}
		// move the end of our path closer to the sw side of the board.
		nelx = nelx - 1;
		nely = nely + 2;
		System.out.println("vc points NOT occupied, returning "+(nelx)+","+(nely));
		// track virtual connection incase player plays there.
		PointPair pp = new PointPair();
		pp.a = new Point( nelx+1, nely-1 );
		pp.b = new Point( nelx, nely-1 );
		vc.add( pp );
		System.out.println("New vc pair "+pp);
		// check if this move connects us to the sw side.
		// case 1: we are touching the sw side.
		if( nely == (gb.iBlueWidth-1) )
			nevc = true;
		// case 2: we are virtually touching the sw side.
		if( nely == (gb.iBlueWidth-2) ) {
			// add the vc
			PointPair pp2 = new PointPair();
			pp2.a = new Point( nelx, (gb.iBlueWidth-1) );
			pp2.b = new Point( nelx-1, (gb.iBlueWidth-1) );
			vc.add( pp2 );
			nevc = true;
		}
		// return the move that sets up a new virtual connection.
		return new Point( nelx, nely );
	}

	private Point southPie(GameBoard gb) {
		System.out.println("Playing in south pie");
		// SneekyPete is trying to make a new virtual. But, if one of the
		// spots is already filled, grab the other one.
		//if( gb.isOccupied( swlx-1, swly   ) ) { swly--; return new Point( swlx, swly ); }
		if( gb.isOccupied( swlx-1, swly ) ) {
			swly--;
			if( gb.isOccupied( swlx, swly ) ) { System.out.println("I'm confused."); return randMove( gb, null ); }
			return new Point( swlx, swly );
		}
		//if( gb.isOccupied( swlx,   swly-1 ) ) { swlx--; return new Point( swlx, swly ); }
		if( gb.isOccupied( swlx,   swly-1 ) ) {
			swlx--;
			if( gb.isOccupied( swlx, swly ) ) { System.out.println("I'm confused."); return randMove( gb, null ); }
			return new Point( swlx, swly );
		}
		// move the end of our path closer to the sw side of the board.
		swlx = swlx - 1;
		swly = swly - 1;
		// track virtual connection incase player plays there.
		PointPair pp = new PointPair();
		pp.a = new Point( swlx, swly+1 );
		pp.b = new Point( swlx+1, swly );
		vc.add( pp );
		System.out.println("New vc pair "+pp);
		// check if this move connects us to the sw side.
		// case 1: we are touching the sw side.
		if( swly == 0 )
			swvc = true;
		// case 2: we are virtually touching the sw side.
		if( swly == 1 ) {
			// add the vc
			PointPair pp2 = new PointPair();
			pp2.a = new Point( swlx, 0 );
			pp2.b = new Point( swlx+1, 0 );
			vc.add( pp2 );
			swvc = true;
		}
		// return the move that sets up a new virtual connection.
		return new Point( swlx, swly );
	}

	private Point northPie(GameBoard gb) {
		System.out.println("Playing in north pie");
		// SneekyPete is trying to make a new virtual. But, if one of the
		// spots is already filled, grab the other one.
		//if( gb.isOccupied( nelx,   nely+1 ) ) { nelx++; return new Point( nelx, nely ); }
		if( gb.isOccupied( nelx, nely+1 ) ) {
			nely++;
			if( gb.isOccupied( nelx, nely ) ) { System.out.println("I'm confused."); return randMove( gb, null ); }
			return new Point( nelx, nely );
		}
		//if( gb.isOccupied( nelx+1, nely   ) ) { nely++; return new Point( nelx, nely ); }
		if( gb.isOccupied( nelx+1, nely ) ) {
			nely++;
			if( gb.isOccupied( nelx, nely ) ) { System.out.println("I'm confused."); return randMove( gb, null ); }
			return new Point( nelx, nely );
		}
		// move the end of our path closer to the sw side of the board.
		nelx = nelx + 1;
		nely = nely + 1;
		// track virtual connection incase player plays there.
		PointPair pp = new PointPair();
		pp.a = new Point( nelx, nely-1 );
		pp.b = new Point( nelx-1, nely );
		vc.add( pp );
		System.out.println("New vc pair "+pp);
		// check if this move connects us to the sw side.
		// case 1: we are touching the sw side.
		if( nely == (gb.iBlueWidth-1) )
			nevc = true;
		// case 2: we are virtually touching the sw side.
		if( nely == (gb.iBlueWidth-2) ) {
			// add the vc
			PointPair pp2 = new PointPair();
			pp2.a = new Point( nelx, (gb.iBlueWidth-1) );
			pp2.b = new Point( nelx-1, (gb.iBlueWidth-1) );
			vc.add( pp2 );
			nevc = true;
		}
		// return the move that sets up a new virtual connection.
		return new Point( nelx, nely );
	}

	private Point fillVcs( GameBoard gb ) {
		// yes we do, we won, fill in the remaining hexes.
		if( vc.size() == 0 ) {
			System.out.println("HUH!! IMPOSSIBLE.  I have vc to both sides, but the vc vector is empty!");
			return randMove(gb, null);
		}
		PointPair pp = (PointPair) vc.get(0);
		// check if both spots are free.
		if( gb.isOccupied( pp.a.x, pp.a.y ) ) {
			System.out.println("ERROR:  I have a virtual (a) that is occupied!");
		}
		if( gb.isOccupied( pp.b.x, pp.b.y ) ) {
			System.out.println("ERROR:  I have a virtual (b) that is occupied!");
		}
		if( gb.isOccupied( pp.a.x, pp.a.y ) && gb.isOccupied( pp.b.x, pp.b.y ) ) {
			System.out.println("ERROR: Both virtuals are occupied, something is BROKEN.");
			return randMove(gb, null);
		}
		if( !gb.isOccupied( pp.a.x, pp.a.y ) ) {
			vc.remove( 0 );
			return pp.a;
		}

		vc.remove(0);
		return pp.b;
	}

	public void undo() {
		SneakyState s = null;
		if( undoStack.empty() )
			return;
		s = (SneakyState) undoStack.pop();

		cx = s.cx; cy = s.cy;
		nelx = s.nelx; nely = s.nely;
		swlx = s.swlx; swly = s.swly;
		nevc = s.nevc;
		swvc = s.swvc;
		vc = s.vc;


	}


}


