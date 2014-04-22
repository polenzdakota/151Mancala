import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Board {
	
	/* Diagram of the array representation of the board.
	 * Numbers are array indices.
	 *  _------------------------------_
	 * | B  [13][12][11][10][ 9][ 8]  A |
	 * | 0  [ 1][ 2][ 3][ 4][ 5][ 6]  7 |
	 *  -==============================-
	 *  A is player 1's Mancala. B is Player 2's.
	 */
	
	int[] board;
	boolean player1Turn;
	ArrayList<ChangeListener> changeListeners;
	
	/*
	 * Constructor for new Board objects.
	 */
	Board() {
		board = new int[14];
		changeListeners = new ArrayList<ChangeListener>();
		setNewGame();
	}
	
	/*
	 * Sets or initializes the instance variables to a fresh board.
	 */
	public void setNewGame() {
		for (int i = 0; i < 14; i++) {
			if ((i != 0) && (i != 7))
				board[i] = 3; //Every pit except the Mancalas gets 3 stones.
			else
				board[i] = 0; //The Mancalas start empty.
		}
		player1Turn = true;
	}
	
	/*
	 * Plays a move for the active player.
	 * @param pit An index for the pit to begin with. Should be in the range 0-5 inclusive.
	 * @return true if the move was successful, false if the move is invalid. Invalid can
	 */
	public boolean playMove(int pit) {
		if (pit < 0 || pit > 5) return false; //Invalid move. Index should be 0-5 inclusive.
		//I use a lot of ternary operators. You can look them up if they're confusing!
		int startPosition = player1Turn?board[pit+1]:board[pit+8];
		int hand = board[startPosition];
		if (hand == 0) return false; //Invalid move. Starting pit must contain stones.
		//Anything past this point is part of a valid move.
		board[hand] = 0;
		boolean doneTurn = false;
		
		/*
		 * We know at this point that hand is at least 1.
		 * The smallest move one can make is to pick up one stone and move it over 1.
		 */
		int i; //We want to check i after the loop to find our last played position.
		//Modular arithmetic to keep us looping within the game board.
		for (i = startPosition + 1; !doneTurn; i = (i+1)%14) {
			//We don't place stones in the opponent's Mancala.
			if (i != (player1Turn?0:7)) {
				board[i]++;
				hand--; //Move one stone from our hand to a pit.
			}
			if (hand == 0) doneTurn = true;
		}
		/*
		 * At this point we're done the looping around phase.
		 * If we played our last stone in an empty pit, we take it and the opposite stones.
		 * We then put those stones in our Mancala.
		 */
		//This checks that we landed in our own empty pit.
		if (board[i] == 1 && (i-7 > 0) == !player1Turn) {
			//(14-i)%14 is the index of the pit opposite i, or i if i is a Mancala index.
			//It means if we steal from our own Mancala, everything is put back.
			int stolenStones = board[(14-i)%14];
			board[i]--; //This is the stone from the last pit that we landed in.
			board[(14-i)%14] -= stolenStones; //Steal from the other pit!
			board[player1Turn?7:0] += 1 + stolenStones; //Put in our Mancala.
		}
		//If we landed in our own Mancala, it's still our turn. Otherwise it flips.
		if (i != (player1Turn?7:0))
			player1Turn = !player1Turn;
		updateChangeListeners();
		return true;
	}
	
	/*
	 * Returns a copy of the current board state.
	 */
	public int[] getCurrentBoardState() {
		return board.clone();
	}
	
	/*
	 * Tells which player's turn it is.
	 * @return true if it is player 1's turn. False otherwise.
	 */
	public boolean player1Turn() {
		return player1Turn();
	}
	
	/*
	 * Registers a new ChangeListener.
	 * @param c The new ChangeListener to register.
	 */
	public void addChangeListener(ChangeListener c) {
		changeListeners.add(c);
	}
	
	/*
	 * Updates all registered views. Typically called after a state change of some sort.
	 */
	public void updateChangeListeners() {
		for (ChangeListener c : changeListeners)
			c.stateChanged(new ChangeEvent(this));
	}
}
