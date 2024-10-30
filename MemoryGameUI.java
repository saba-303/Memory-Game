import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class MemoryGameUI extends JFrame {
    private JTextField[] numberFields;
    private JLabel numberLabel, scoreLabel;
    private Timer displayTimer;
    private int[] sequence;
    private int currentScore = 0;
    private JTable leaderboardTable;
    private List<String> players;
    private int currentPlayerIndex = 0;
    private long startTime;
    private String[][] leaderboardData;
    private String[] leaderboardColumns = { "Player", "Score", "Time" };
    private JComboBox<String> difficultyComboBox;
    private JButton startButton;
    private JPanel inputPanel;
    private int numFields = 6;

    public MemoryGameUI() {
        setTitle("Memory Game - Multiplayer");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left side (Game Area)
        JPanel gamePanel = new JPanel(new GridLayout(6, 1));
        add(gamePanel, BorderLayout.CENTER);

        // Display Level and Score
        JPanel levelPanel = new JPanel(new GridLayout(1, 1));
        String[] difficultyLevels = { "Easy", "Medium", "Hard" };
        difficultyComboBox = new JComboBox<>(difficultyLevels);
        levelPanel.add(difficultyComboBox);
        scoreLabel = new JLabel("Score: 0", JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        levelPanel.add(scoreLabel);
        gamePanel.add(levelPanel);

        // Number Display
        numberLabel = new JLabel("Click 'Start' to begin the game", JLabel.CENTER);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gamePanel.add(numberLabel);

        // Input Fields for user entry
        inputPanel = new JPanel(new GridLayout(1, numFields));
        gamePanel.add(inputPanel);

        // Start Button
        startButton = new JButton("Start");
        gamePanel.add(startButton);
        startButton.addActionListener(e -> startGame());

        // Submit Button
        JButton submitButton = new JButton("Submit");
        gamePanel.add(submitButton);
        submitButton.addActionListener(e -> checkInput());

        // Right side (Leaderboard)
        JPanel leaderboardPanel = new JPanel(new BorderLayout());
        add(leaderboardPanel, BorderLayout.EAST);

        JLabel leaderboardLabel = new JLabel("Leaderboard", JLabel.CENTER);
        leaderboardPanel.add(leaderboardLabel, BorderLayout.NORTH);

        leaderboardData = new String[3][3];
        leaderboardTable = new JTable(leaderboardData, leaderboardColumns);
        leaderboardPanel.add(new JScrollPane(leaderboardTable), BorderLayout.CENTER);

        // Ask players for their names
        players = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String playerName = JOptionPane.showInputDialog("Enter name for Player " + (i + 1) + ": ");
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player " + (i + 1); // Default name if not provided
            }
            players.add(playerName);
            leaderboardData[i][0] = playerName;
        }

        setVisible(true);
    }

    private void startGame() {
        startButton.setEnabled(false);
        currentPlayerIndex = 0;
        resetPlayerScores();
        nextTurn();
    }

    private void nextTurn() {
        if (currentPlayerIndex < players.size()) {
            String currentPlayer = players.get(currentPlayerIndex);
            JOptionPane.showMessageDialog(this, currentPlayer + "'s Turn");

            // Start game for the current player
            String difficulty = (String) difficultyComboBox.getSelectedItem();
            switch (difficulty) {
                case "Easy":
                    numFields = 5;
                    break;
                case "Medium":
                    numFields = 6;
                    break;
                case "Hard":
                    numFields = 7;
                    break;
            }
            updateInputFields();
            generateSequence(2);
            displaySequence(5000);

            startTime = System.currentTimeMillis();
        } else {
            // End game after all players have played
            JOptionPane.showMessageDialog(this, "Game Over! Final Leaderboard is displayed.");
            startButton.setEnabled(true);
        }
    }

    private void updateInputFields() {
        inputPanel.removeAll();
        numberFields = new JTextField[numFields];
        inputPanel.setLayout(new GridLayout(1, numFields));
        for (int i = 0; i < numFields; i++) {
            numberFields[i] = new JTextField(2);
            inputPanel.add(numberFields[i]);
        }
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void generateSequence(int numDigits) {
        sequence = new int[numFields];
        Random rand = new Random();
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = rand.nextInt(0,9);
        }
        StringBuilder sb = new StringBuilder();
        for (int num : sequence) {
            sb.append(num).append(" ");
        }
        numberLabel.setText(sb.toString());
    }

    private void displaySequence(int time) {
        displayTimer = new Timer(time, e -> numberLabel.setText(""));
        displayTimer.setRepeats(false);
        displayTimer.start();
    }

    private void checkInput() {
        boolean correct = true;
        for (int i = 0; i < numberFields.length; i++) {
            try {
                int enteredNumber = Integer.parseInt(numberFields[i].getText());
                if (enteredNumber != sequence[i]) {
                    correct = false;
                    break;
                }
            } catch (NumberFormatException ex) {
                correct = false;
                break;
            }
        }

        if (correct) {
            currentScore = 10; // Fixed score for correct answer
            scoreLabel.setText("Score: " + currentScore);
            JOptionPane.showMessageDialog(this, "Correct Sequence!");
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect Sequence!");
            currentScore = 0; // No score for incorrect answer
        }
        updateLeaderboard();
        currentPlayerIndex++;
        nextTurn();
    }

    private void updateLeaderboard() {
        long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
        leaderboardData[currentPlayerIndex][1] = String.valueOf(currentScore);
        leaderboardData[currentPlayerIndex][2] = timeTaken + " sec";
        leaderboardTable.setModel(new javax.swing.table.DefaultTableModel(leaderboardData, leaderboardColumns));
    }

    private void resetPlayerScores() {
        for (int i = 0; i < players.size(); i++) {
            leaderboardData[i][1] = "0";
            leaderboardData[i][2] = "0 sec";
        }
    }

    public static void main(String[] args) {
        new MemoryGameUI();
    }
}
