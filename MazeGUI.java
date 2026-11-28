package edu.txst.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class MazeGUI extends JFrame {
	private Board originalBoard;
	private Board currentBoard;
	private GameEngine engine;
	private GamePanel gamePanel;
	private InfoPanel infoPanel;
	private JMenuItem resetItem;
	private int stepCounter;  // local variable to track steps taken
	private int coinCounter;  // local variable to store the number of coins collected
	private String[] options = {"Yes", "No"};

	public MazeGUI() {
		setTitle("16-Bit Maze");
		setSize(640, 480); // Adjusted for 10x5 grid with scaling
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		initMenu();

		infoPanel = new InfoPanel();
		gamePanel = new GamePanel();
		add(infoPanel, BorderLayout.NORTH);
		add(gamePanel, BorderLayout.CENTER);

		// Handle Keyboard Input
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (engine == null)
					return;

				switch (e.getKeyCode()) {
					case KeyEvent.VK_UP -> engine.movePlayer(-1, 0);
					case KeyEvent.VK_DOWN -> engine.movePlayer(1, 0);
					case KeyEvent.VK_LEFT -> engine.movePlayer(0, -1);
					case KeyEvent.VK_RIGHT -> engine.movePlayer(0, 1);
				}
				stepCounter = currentBoard.stepCounter.getSteps();  // pull step counter from stepcounter class
				infoPanel.setInfoSteps(stepCounter);				// update the info panel with the current step count
				coinCounter = engine.getCoins();  					// pull coin counter directly from engine class
				infoPanel.setInfoCoins(coinCounter);				// update the info panel with the current coin count

				gamePanel.repaint();

				// Check for victory
				// FIXED: Score calculation: score = (steps * -1) + (coins * 5)
				if (engine.playerWins()) {
					int totalPoints = (infoPanel.getInfoSteps() * -1) + (infoPanel.getInfoCoins() * 5);
					JOptionPane.showMessageDialog(MazeGUI.this,
							"Congratulations! You found the exit.\nYou got " + totalPoints + "points",
							"Level Complete", JOptionPane.INFORMATION_MESSAGE);
					// Optional: Disable engine to prevent movement after win

					int choice = JOptionPane.showOptionDialog(MazeGUI.this,"Would you like to restart?","Level Complete",JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[0]
					);

					if (choice == 0){
						resetGame();
					}
					else if (choice == 1){
					engine = null;
					currentBoard = null;		// extra cleaning, not necessarily necessary but may prevent duplication errors.

					resetItem.setEnabled(false);

					gamePanel.clearBoard();     // new method in gamePanel used to clear the board

					infoPanel.setInfoSteps(0);  // resets steps to 0
					infoPanel.setInfoCoins(0);	   // resets coins to 0

					}
				}
			}
		});
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");

		JMenuItem openItem = new JMenuItem("Open");
		resetItem = new JMenuItem("Reset");
		resetItem.setEnabled(false); // Disabled by default

		openItem.addActionListener(e -> openFile());
		resetItem.addActionListener(e -> resetGame());

		gameMenu.add(openItem);
		gameMenu.add(resetItem);
		menuBar.add(gameMenu);
		setJMenuBar(menuBar);
	}

	private void openFile() {
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		int result = fileChooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			CSVBoardLoader loader = new CSVBoardLoader();

			// Load and Store
			originalBoard = loader.load(selectedFile.getAbsolutePath());
			currentBoard = originalBoard.clone();
			engine = new GameEngine(currentBoard);

			resetItem.setEnabled(true);
			gamePanel.setBoard(currentBoard);
			gamePanel.repaint();
			// Reset coins
			infoPanel.setInfoCoins(0);
		}
	}

	private void resetGame() {
		if (originalBoard != null) {
			currentBoard = originalBoard.clone();
			engine = new GameEngine(currentBoard);
			gamePanel.setBoard(currentBoard);
			gamePanel.repaint();
			// Reset Coins
			infoPanel.setInfoCoins(0);
		}
	}

	// Inner class for information panel
	private class InfoPanel extends JPanel {
		private JLabel infoSteps;
		private JLabel infoCoins;

		public InfoPanel() {
			this.setLayout(new FlowLayout());
			this.add(new JLabel("Steps: "));
			// infoRemainingSteps is a label which value can be changed using its method
			// called
			// setText
			infoSteps = new JLabel("0");
			this.add(infoSteps);
			this.add(new JLabel("Coins: "));
			// infoCoins is a label which value can be changed using its method called
			// setText
			infoCoins = new JLabel("0");
			this.add(infoCoins);
		}

		public void setInfoSteps(int remainingSteps) {
			this.infoSteps.setText(Integer.toString(remainingSteps));
		}

		public int getInfoSteps() {
			return Integer.parseInt(this.infoSteps.getText());
		}

		public void setInfoCoins(int infoCoins) {
			this.infoCoins.setText(Integer.toString(infoCoins));
		}

		public int getInfoCoins() {
			return Integer.parseInt(this.infoCoins.getText());
		}
	}

	// Inner class for custom rendering
	private class GamePanel extends JPanel {
		private Board board;
		private final int TILE_SIZE = 64; // Scale up for visibility

		public void setBoard(Board board) {
			this.board = board;
		}

		public void clearBoard(){
			this.board = null;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (board == null)
				return;

			for (int r = 0; r < 6; r++) {
				for (int c = 0; c < 10; c++) {
					int cell = board.getCell(r, c);
					drawTile(g, cell, c * TILE_SIZE, r * TILE_SIZE);
				}
			}
		}

		private void drawTile(Graphics g, int type, int x, int y) {
			// Placeholder colors until you link the sprite loading logic
			switch (type) {
				case 0 -> g.setColor(Color.LIGHT_GRAY); // Floor
				case 1 -> g.setColor(Color.DARK_GRAY); // Wall
				case 2 -> g.setColor(Color.YELLOW); // Coin
				case 5 -> g.setColor(Color.MAGENTA); // Exit
				case 6 -> g.setColor(Color.BLUE); // Player
				case 7 -> g.setColor(Color.RED); // Hacker
				default -> g.setColor(Color.BLACK);
			}
			g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
			g.setColor(Color.WHITE);
			g.drawRect(x, y, TILE_SIZE, TILE_SIZE); // Grid lines
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MazeGUI().setVisible(true));
	}
}
