# Sudoku Solver

## Description

A Java-based Sudoku solver that uses recursion and backtracking to find all valid solutions for a given puzzle.

This project focuses on algorithm design and problem-solving, particularly exploring how recursive strategies can efficiently navigate complex constraint-based problems.

## Tech Stack

* Java

## Features

* Solves standard 9x9 Sudoku puzzles
* Supports multiple solutions
* Validates puzzle constraints before solving
* Efficient backtracking algorithm

## How It Works

The solver uses a recursive backtracking approach. It iterates through empty cells, tries valid numbers based on Sudoku rules, and recursively continues until the puzzle is solved. If a conflict occurs, it backtracks and tries a different value.

## Challenges & Learning

* Designing an efficient backtracking algorithm
* Managing recursive state and base cases
* Validating constraints across rows, columns, and subgrids
* Understanding time complexity in recursive solutions

## How to Run

1. Clone the repository:

   ```
   git clone <repository-url>
   ```
2. Compile the program:

   ```
   javac SudokuSolver.java
   ```
3. Run the program:

   ```
   java SudokuSolver
   ```
