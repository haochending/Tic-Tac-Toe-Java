import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ToolBarView extends JPanel implements Observer {

	final static int SELECT_TURN = 0;
	final static int FIRST_MOVE = 1;
	final static int NUM_MOVES = 2;
	final static int ILLEGAL = 3;
	final static int WIN = 4;
	final static int DRAW = 5;

	// the model that this view is showing
	private Model model;
	// the button of the toolbar
	private JButton button;
	// the message of the toolbar
	private JLabel label;
	// the button to open and close PVE mode
	private JButton enhance;

	public ToolBarView(Model model_) {
		Font curFont;
		// create UI
		setBackground(Color.WHITE);
		setLayout(new FlowLayout(FlowLayout.LEFT, 20, 50));

		// the button to restart the game

		button = new JButton("New Game");
		curFont = button.getFont();
		button.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 11));
		button.setPreferredSize(new Dimension(110, 50));
		button.setFocusable(false);
		this.add(button);

		// the label used to display the game info message
		label = new JLabel("Select which player starts ", JLabel.CENTER);
		curFont = label.getFont();
		label.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 11));
		label.setAlignmentX(CENTER_ALIGNMENT);
		this.add(label);
		label.setPreferredSize(new Dimension(340, 50));

		// the button to change game mode
		enhance = new JButton("PVP");
		curFont = button.getFont();
		enhance.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 11));
		enhance.setPreferredSize(new Dimension(80, 50));
		enhance.setPreferredSize(new Dimension(60, 50));
		enhance.setFocusable(false);
		this.add(enhance);

		// set the model
		model = model_;

		// register the controllers
		this.registerController();
	}

	/* the controller part */

	void registerController() {
		this.button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				model.newGame();
			}
		});

		this.enhance.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (model.getStatus() == 0) {
					model.changeMode();
				}
			}
		});
	}

	// Observer interface
	@Override
	public void update(Observable o, Object arg) {
		// update the button indicate current mode
		if (model.getMode() == 0) {
			enhance.setText("PVP");
		} else {
			enhance.setText("PVE");
		}

		int gameStatus = model.getStatus();

		// set different message content according to the game status
		switch (gameStatus) {
		case SELECT_TURN:
			this.label.setText("Select which player starts ");
			break;
		case FIRST_MOVE:
			this.label
					.setText("Change which player starts, or make first move.");
			break;
		case NUM_MOVES:
			int moves = 9 - model.getBlanks();
			this.label.setText(moves + " moves.");
			break;
		case ILLEGAL:
			this.label.setText("Illegal move");
			break;
		case WIN:
			int playerTurn = model.getTurn();
			if (playerTurn == 0) {
				this.label.setText("O Wins");
			} else {
				this.label.setText("X Wins");
			}
			break;
		case DRAW:
			this.label.setText("Game over, no winner");
			break;

		default:
			break;
		}
	}

}
