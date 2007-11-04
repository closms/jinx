/*
 * HumanPlayer.java
 *
 * Michael Closson
 *
 */

import java.awt.*;
import java.awt.event.*;

/**
 * The HumanPlayer class implements the Human Player, when nextMove is called
 * the HumanPlayer class blocks until the player selects a valid cell.
 */

public class HumanPlayer
	implements HexPlayer, MouseListener
{
    boolean waiting;
    int x, y;
    HexCanvas hexCanvas;

	/**
	 * create a new HumanPlayer class, the HexCanvas is used to track mouse clicks
	 * when the HumanPlayer's nextMove method is called.
	 */

    public HumanPlayer( HexCanvas hc ) {
		hc.addMouseListener( this );
		hexCanvas = hc;
    }

	/**
	 * Block until the player clicks a valid hex cell.  The selected cell
	 * is returned.
	 */

    public synchronized Point nextMove( GameBoard gb, GameState gs ) {
		Point p = null;
		int a, b;

		// Block until a button is pressed.
		waiting = true;
		while( waiting ) {
			try {
				Thread.sleep( 100 );
			}
			catch( Exception e ) {}
		}
	
		return hexCanvas.xy2Point(x, y);
	}

	/**
	 * The HumanPlayer class doesn't track any state, so it's undo
	 * method is a noop.
	 */

	public void undo() {}

	public void mouseEntered( MouseEvent me ) {}
	public void mouseExited( MouseEvent me ) {}
	public void mouseClicked( MouseEvent me ) {
		if( me.getButton() != MouseEvent.BUTTON1 )
			return;
		x = me.getX();
		y = me.getY();
		waiting = false;
    }
    public void mousePressed( MouseEvent me ) {}
    public void mouseReleased( MouseEvent me ) {}


}


