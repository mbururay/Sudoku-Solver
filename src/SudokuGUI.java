import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;

public class SudokuGUI extends JFrame {
    private JTextField[][] cells;
    private JButton solveButton;
    private JButton clearButton;
    private JTextArea resultArea;
    private JLabel statusLabel;
    private Color defaultTextColor = Color.GRAY;

    public SudokuGUI() {
        setTitle("Sudoku Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeComponents();
        setupLayout();
        setupEventHandlers();

        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 700));
    }

    private void initializeComponents() {
        // Initialize the 9x9 grid of text fields
        cells = new JTextField[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col] = new JTextField(2);
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Monospaced", Font.BOLD, 16));
                cells[row][col].setForeground(defaultTextColor);

                // Set document filter to allow only single digits
                ((PlainDocument) cells[row][col].getDocument()).setDocumentFilter(new DigitFilter());
            }
        }

        solveButton = new JButton("Solve Sudoku");
        clearButton = new JButton("Clear Grid");

        resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createTitledBorder("Results"));

        statusLabel = new JLabel("Enter a Sudoku puzzle (use 0 or leave blank for empty cells)");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void setupLayout() {
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create Sudoku grid panel
        JPanel gridPanel = new JPanel(new GridLayout(9, 9, 1, 1));
        gridPanel.setBorder(BorderFactory.createTitledBorder("Sudoku Grid"));

        // Add cells to grid with thicker borders for 3x3 boxes
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                JPanel cellPanel = new JPanel(new BorderLayout());
                cellPanel.setBorder(createCellBorder(row, col));
                cellPanel.add(cells[row][col], BorderLayout.CENTER);
                gridPanel.add(cellPanel);
            }
        }

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(solveButton);
        buttonPanel.add(clearButton);

        // Create result panel with scroll pane
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setPreferredSize(new Dimension(500, 150));

        // Add all components to main panel
        mainPanel.add(statusLabel, BorderLayout.NORTH);
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(resultScrollPane, BorderLayout.SOUTH);
    }

    private Border createCellBorder(int row, int col) {
        int top = (row % 3 == 0) ? 2 : 1;
        int left = (col % 3 == 0) ? 2 : 1;
        int bottom = (row == 8) ? 2 : ((row % 3 == 2) ? 2 : 1);
        int right = (col == 8) ? 2 : ((col % 3 == 2) ? 2 : 1);

        return BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
    }

    private void setupEventHandlers() {
        solveButton.addActionListener(e -> solveSudoku());
        clearButton.addActionListener(e -> clearGrid());

        // Add Enter key listener to solve on Enter press
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                final int currentRow = row; // Create final copies
                final int currentCol = col;

                cells[row][col].addActionListener(e -> solveSudoku());

                // Change text color to black when user starts typing
                cells[row][col].getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) { updateTextColor(currentRow, currentCol); }
                    public void removeUpdate(DocumentEvent e) { updateTextColor(currentRow, currentCol); }
                    public void insertUpdate(DocumentEvent e) { updateTextColor(currentRow, currentCol); }

                    private void updateTextColor(int row, int col) {
                        SwingUtilities.invokeLater(() -> {
                            JTextField source = cells[row][col];
                            if (!source.getText().trim().isEmpty()) {
                                source.setForeground(Color.BLACK);
                            } else {
                                source.setForeground(defaultTextColor);
                            }
                        });
                    }
                });
            }
        }
    }

    private void solveSudoku() {
        try {
            char[][] puzzle = readPuzzleFromGUI();

            // Reset solution count and first solution
            Lab3.solutionCount = 0;
            Lab3.firstSolution = null;

            // Solve the puzzle using your existing logic
            Lab3.solvePuzzleGUI(puzzle);

            // Display results
            displayResults();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error reading puzzle: " + ex.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private char[][] readPuzzleFromGUI() {
        char[][] puzzle = new char[9][9];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = cells[row][col].getText().trim();
                if (text.isEmpty() || text.equals("0")) {
                    puzzle[row][col] = '_';
                } else if (text.length() == 1 && Character.isDigit(text.charAt(0))) {
                    int num = Integer.parseInt(text);
                    if (num >= 1 && num <= 9) {
                        puzzle[row][col] = text.charAt(0);
                    } else {
                        puzzle[row][col] = '_';
                    }
                } else {
                    throw new IllegalArgumentException("Invalid input at row " + (row+1) + ", column " + (col+1));
                }
            }
        }

        return puzzle;
    }

    private void displayResults() {
        StringBuilder result = new StringBuilder();

        if (Lab3.solutionCount == 0) {
            result.append("There are no possible solutions.\n");
            statusLabel.setText("No solutions found");
        } else if (Lab3.solutionCount == 1) {
            result.append("There is one unique solution:\n\n");
            result.append(gridToString(Lab3.firstSolution));
            statusLabel.setText("Found one unique solution");

            // Display the solution in the grid
            displaySolution(Lab3.firstSolution);
        } else {
            result.append("There are ").append(Lab3.solutionCount).append(" possible solutions\n");
            result.append("First solution found:\n\n");
            result.append(gridToString(Lab3.firstSolution));
            statusLabel.setText("Found " + Lab3.solutionCount + " solutions");

            // Display the first solution in the grid
            displaySolution(Lab3.firstSolution);
        }

        resultArea.setText(result.toString());
    }

    private void displaySolution(char[][] solution) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setText(String.valueOf(solution[row][col]));
                String originalText = cells[row][col].getText().trim();
                if (originalText.isEmpty() || originalText.equals("0") || originalText.equals("_")) {
                    cells[row][col].setBackground(new Color(220, 255, 220));
                    cells[row][col].setForeground(new Color(0, 100, 0));
                } else {
                    cells[row][col].setBackground(Color.WHITE);
                    cells[row][col].setForeground(Color.BLACK); // Black for original numbers
                }
            }
        }
    }

    private String gridToString(char[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                sb.append(grid[row][col]).append(" ");
                if (col == 2 || col == 5) {
                    sb.append("| ");
                }
            }
            sb.append("\n");
            if (row == 2 || row == 5) {
                sb.append("------+-------+------\n");
            }
        }
        return sb.toString();
    }

    private void clearGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                // Reset background to white
                cells[row][col].setBackground(Color.WHITE);

                // Keep the numbers but change them to grey color
                String currentText = cells[row][col].getText().trim();
                if (!currentText.isEmpty() && !currentText.equals("0")) {
                    cells[row][col].setForeground(defaultTextColor); // Set to grey
                } else {
                    cells[row][col].setText(""); // Clear empty cells
                }
            }
        }
        resultArea.setText("");
        statusLabel.setText("Enter a Sudoku puzzle (use 0 or leave blank for empty cells)");
    }

    // Document filter to allow only single digits
    private static class DigitFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string.matches("[0-9]") && fb.getDocument().getLength() == 0) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text.matches("[0-9]") && fb.getDocument().getLength() - length + text.length() <= 1) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to set FlatDarkLaf look and feel");
        }

        SwingUtilities.invokeLater(() -> {
            new SudokuGUI().setVisible(true);
        });
    }
}