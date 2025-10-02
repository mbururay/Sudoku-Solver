import java.util.Scanner;

public class Test{
    static int solutionCount;
    static char[][] firstSolution;
    static int recursiveCalls;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read number of puzzles
        System.out.println("Enter number of puzzles:");
        int numPuzzles = Integer.parseInt(scanner.nextLine());

        // Read all puzzles at once
        String[] puzzleInput = new String[numPuzzles * 9];
        for (int i = 0; i < numPuzzles * 9; i++) {
            puzzleInput[i] = scanner.nextLine().replaceAll(" ", "");
        }

        // Process each puzzle
        for (int p = 0; p < numPuzzles; p++) {
            char[][] puzzle = new char[9][9];
            for (int r = 0; r < 9; r++) {
                puzzle[r] = puzzleInput[p * 9 + r].toCharArray();
            }

            System.out.println("\nPuzzle " + (p+1) + ":");
            solvePuzzle(puzzle);
        }
    }

    static void solvePuzzle(char[][] puzzle) {
        // Reset the variables for each puzzle
        solutionCount = 0;
        firstSolution = new char[9][9];
        recursiveCalls = 0;

        //Creates a copy of the arguement puzzle in order to avoid inteference from previous runs
        char[][] grid = deepCopy(puzzle);
        solve(grid);

        //Output of results depending on the number of solutions that are found
        if (solutionCount == 0) {
            System.out.println("There are no possible solutions.");
        } else if (solutionCount == 1) {
            System.out.println("There is one unique solution:");
            printGrid(firstSolution);
        } else {
            System.out.println("There are " + solutionCount +" possible solutions");
        }

    }


    //The recursive solve method
    static boolean solve(char[][] grid) {
        //Finds the first empty cell and assign its coordinates to an array
        int[] cell = findEmptyCell(grid);

        //If cell is null there are no empty spaces therefore the sudoku is solved
        if (cell == null) {
            //Creats a copy if it is the first solution encountered and assigns it to first solution
            if (solutionCount == 0) {
                firstSolution = deepCopy(grid);
            }
            solutionCount++;

            return solutionCount > 1;
        }

        //Assigns the column of the row and the column
        int row = cell[0], col = cell[1];

        for (int num = 1; num <= 9; num++) {
            if (isValid(grid, row, col, num)) {
                grid[row][col] = (char)('0' + num);
                if (solve(grid)) {
                    return true;
                }
                grid[row][col] = '_';
            }
        }
        return false;
    }

    private static boolean isValid(char[][] grid, int row, int col, int num) {
        // Check row and column
        for (int i = 0; i < 9; i++) {
            if (grid[row][i] == '0' + num || grid[i][col] == '0' + num) {
                return false;
            }
        }

        // Check 3x3 box using formulas to map the coordinates of the subarrays
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[boxRow + r][boxCol + c] == '0' + num) {
                    return false;
                }
            }
        }
        return true;
    }


    //Loops through the array to find an empty cell
    private static int[] findEmptyCell(char[][] grid) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (grid[r][c] == '_') {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    //Creates copies of arrays
    private static char[][] deepCopy(char[][] original) {
        char[][] copy = new char[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, 9);
        }
        return copy;
    }


    //Prints arrays
    private static void printGrid(char[][] grid) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                System.out.print(grid[r][c] + " ");
            }
            System.out.println();
        }
    }
}