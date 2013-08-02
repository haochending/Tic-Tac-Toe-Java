import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

class PlayerView extends JPanel implements Observer {

	// the model that this view is showing
	private Model model;

	private JRadioButton player1;
	private JRadioButton player2;
	private ButtonGroup group;

	PlayerView(Model model_) {
		// create UI
		setBackground(Color.WHITE);
		setLayout(new FlowLayout(FlowLayout.LEFT, 80, 0));

		// set the model
		model = model_;

		// radio button of the player O
		player1 = new JRadioButton("Player O");
		player1.setPreferredSize(new Dimension(200, 150));
		Font curFont = player1.getFont();
		player1.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 22));
		player1.setFocusable(false);

		// radio button of the player X
		player2 = new JRadioButton("Player X");
		player2.setPreferredSize(new Dimension(200, 150));
		player2.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 22));
		player2.setFocusable(false);

		// create the radio button group
		group = new ButtonGroup();
		group.add(player1);
		group.add(player2);

		this.add(player1);
		this.add(player2);

		// register the controller
		this.registerController();
	}

	/* the controller part */

	void registerController() {
		this.player1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// if game not start, set the player turn and change status to
				// first move
				if (model.getStatus() == 0) {
					player1.setSelected(true);
					model.setStatus(1);
					model.setTurn(0);
				}

				// if status is first move, only set the player turn
				if (model.getStatus() == 1) {
					model.setTurn(0);
				}

				// if other status disable the selection
				if (model.getStatus() != 0 || model.getStatus() != 1) {
					if (model.getTurn() == 0) {
						player1.setSelected(true);
					} else {
						player2.setSelected(true);
					}
				}
			}
		});

		this.player2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// if game not start, set the player turn and change status to
				// first move
				if (model.getStatus() == 0) {
					model.setStatus(1);
					model.setTurn(1);
				}

				// if status is first move, only set the player turn
				if (model.getStatus() == 1) {
					model.setTurn(1);

					// if in PVE mode, let AI move immediately for simplicity
					if (model.getMode() == 1) {
						// emulate the AI thinking process
						player2.setText("AI thinking...");

						// delay for AI thinking process
						new Thread(new Runnable() {
							public void run() {
								try {
									Thread.sleep(1500);
								} catch (InterruptedException ignore) {
								}
								model.AIMove();
							}
						}).start();
					}
				}

				// if other status disable the selection
				if (model.getStatus() != 0 || model.getStatus() != 1) {
					if (model.getTurn() == 0) {
						player1.setSelected(true);
					} else {
						player2.setSelected(true);
					}
				}
			}
		});
	}

	// Observer interface
	@Override
	public void update(Observable o, Object arg) {
		// change the player name according to the mode
		if (model.getMode() == 0) {
			player2.setText("Player X");
		} else {
			player2.setText("AI X");
		}

		// if new game, clear the player turn
		if (model.getStatus() == 0) {
			group.clearSelection();
		}

		// if game starts, update the player turn
		if (model.getStatus() == 2) {
			if (model.getTurn() == 0) {
				player1.setSelected(true);
			} else {
				if (model.getMode() == 1)
					// emulate the AI thinking process
					player2.setText("AI thinking...");
				player2.setSelected(true);
			}
		}
	}
}
