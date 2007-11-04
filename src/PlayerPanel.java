/*
 * PlayerPanel.java
 *
 * Michael Closson
 *
 */

import java.awt.*;
import javax.swing.*;

/**
 * The PlayerPanel class provides the choice dialogs to select
 * players and the label that indicates which players turn it is.
 */

public class PlayerPanel extends JPanel {
    
    JPanel RedPanel = null;
    JPanel BluePanel = null;

    JPanel ChoicePanel = null;
    JPanel TurnPanel = null;

    JComboBox RedChoice = null;
    JComboBox BlueChoice = null;

    JLabel TurnLabel = null;

    static String RPT = "It's the Red Players Turn.";
    static String BPT = "It's the Blue Players Turn.";

    public PlayerPanel() {
		RedPanel = new JPanel();
		BluePanel = new JPanel();

		//setup Red Panel choice.
		RedChoice = new JComboBox();
		RedChoice.addItem("Human Player");
		RedChoice.addItem("Random Player");
		RedChoice.addItem("SneakyPete");
		RedPanel.add( new JLabel("Red Player:"));
		RedPanel.add(RedChoice);

		//setup Blue choice.
		BlueChoice = new JComboBox();
		BlueChoice.addItem("Human Player");
		BlueChoice.addItem("Random Player");
		BluePanel.add( new JLabel("Blue Player:"));
		BluePanel.add(BlueChoice);

		//setup the ChoicePanel
		ChoicePanel = new JPanel();
		ChoicePanel.setLayout( new BorderLayout() );
		ChoicePanel.add( RedPanel, "West" );
		ChoicePanel.add( BluePanel, "East" );

		//setup the TurnPanel
		TurnPanel = new JPanel();
		TurnPanel.setLayout( new BorderLayout() );
		TurnLabel = new JLabel( RPT, JLabel.CENTER );
		TurnPanel.add( TurnLabel );

		//setup the main Panel
		this.setLayout( new GridLayout(2, 1) );
		this.add( TurnPanel );
		this.add( ChoicePanel );
    }

	/**
	 * Calling nextTurn() will switch the the text label that indicated
	 * which players turn it is, to the next player.
	 */

    public void nextTurn() {
		if( TurnLabel.getText().equals( RPT ) )
			TurnLabel.setText( BPT );
		else
			TurnLabel.setText( RPT );
	}

	/**
	 * The reset() method resets the player turn label back to it's
	 * initial state (i.e., the Red Players turn).
	 */

	public void reset() {
		TurnLabel.setText( RPT );
	}

	/**
	 * The gameWon() method is called to indicate through the JHex GUI
	 * that some player has won the game.
	 *
	 * @param i The player number of the winning players.  The red
	 * player is player number 1 and the blue player is player number
	 * 2.
	 */

	public void gameWon( int i ) {
		System.out.println("Game WON by "+i);
		TurnLabel.setText("The "+(i==1?"RED":"BLUE")+" player is the winner!");
	}

	/**
	 * The getRedPlayer() method returns a string indicating which
	 * player is currently selected in the red player choice.
	 */

	public String getRedPlayer() {
		return (String) RedChoice.getSelectedItem();
	}

	/**
	 * The getBluePlayer() method returns a string indicating which
	 * player is currently selected in the blue player choice.
	 */

	public String getBluePlayer() {
		return (String) BlueChoice.getSelectedItem();
	}


}



