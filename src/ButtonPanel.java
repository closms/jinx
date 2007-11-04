
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * The ButtonPanel class is located at the top of the main JHex frame.
 * The ButtonPanel contains the textfields for the width of the red and
 * blue sides of the hex board.  The ButtonPanel also contains the "New Game",
 * "Undo" and "BUMMER!" buttons.
 * <p>
 * The New Game button starts a new game.
 * <p>
 * The Undo button undoes the last human move.  If both players are computer players,
 * the Undo button has no effect.
 * <p>
 * The BUMMER! button exists the program.  When running as an applet, the java Security
 * Manager prevents the applet code from calling the System.exit() function.  Therefore,
 * when running as an applet, the BUMMER! button has no effect.
 * <p>
 * When the "New Game" or "Undo" buttons are called, the ButtonPanel class will call the
 * newGame() and undo() functions of the JHex class.
 */

public class ButtonPanel
	extends JPanel
	implements ActionListener
{
	JHex jhex;
	JButton bNewGame;
	JButton bUndo;
	JButton bExit;

	JTextField tfRedWidth, tfBlueWidth;

	public ButtonPanel(JHex j)
	{
		jhex = j;

		tfRedWidth  = new JTextField("5", 3);
		tfBlueWidth = new JTextField("5", 3);
		bNewGame = new JButton("New Game");
		bNewGame.addActionListener( this );
		bUndo = new JButton("Undo");
		bUndo.addActionListener( this );
		bExit = new JButton("BUMMER!");
		bExit.addActionListener( this );

		this.add( tfRedWidth );
		this.add( new Label("x"));
		this.add( tfBlueWidth );
		this.add( new Label(" -- ") );
		this.add( bNewGame );
		this.add( bUndo );
		this.add( bExit );
	}


	public void actionPerformed( ActionEvent ae ) {
		if( ae.getSource() == bExit ) {
			System.exit(0);
		}
		if( ae.getSource() == bUndo ) {
			jhex.undo();
		}
		if( ae.getSource() == bNewGame ) {
			int r, b;
			r = Integer.parseInt( tfRedWidth.getText() );
			b = Integer.parseInt( tfBlueWidth.getText() );
			System.out.println("Starting new game. "+r+" x "+b );
			jhex.newGame( r, b );
		}
	}


}


