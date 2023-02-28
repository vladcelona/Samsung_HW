package com.vladcelona.miniproject.games;

import com.vladcelona.miniproject.statistics.Statistics;

import java.util.*;
import java.lang.*;
import java.io.*;

public class BattleShips {
    private static int numRows = 10;
    private static int numCols = 10;
    private static int playerShips;
    private static int computerShips;
    private static String[][] grid = new String[numRows][numCols];
    private static int[][] missedGuesses = new int[numRows][numCols];

    public BattleShips() {
        System.out.println("**** Welcome to Battle Ships game ****");
        System.out.println("Right now, sea is empty\n");

        // Step 1 – Create the ocean map
        createOceanMap();

        // Step 2 – Deploy player’s ships
        deployPlayerShips();

        // Step 3 - Deploy computer's ships
        deployComputerShips();

        // Step 4 Battle
        do {
            Battle();
        } while (BattleShips.playerShips != 0 && BattleShips.computerShips != 0);

        // Step 5 - Game over
        gameOver();
    }

    public static void createOceanMap() {
        // First section of Ocean Map
        System.out.print("  ");
        for(int index = 0; index < numCols; index++) { System.out.print(index); }
        System.out.println();

        // Middle section of Ocean Map
        for(int index = 0; index < grid.length; index++) {
            for (int j = 0; j < grid[index].length; j++) {
                grid[index][j] = " ";
                if (j == 0) {
                    System.out.print(index + "|" + grid[index][j]);
                } else if (j == grid[index].length - 1) {
                    System.out.print(grid[index][j] + "|" + index);
                } else {
                    System.out.print(grid[index][j]);
                }
            }
            System.out.println();
        }

        // Last section of Ocean Map
        System.out.print("  ");
        for(int index = 0; index < numCols; index++) { System.out.print(index); }
        System.out.println();
    }

    public static void deployPlayerShips() {
        Scanner input = new Scanner(System.in);

        System.out.println("\nDeploy your ships:");
        // Deploying five ships for player
        BattleShips.playerShips = 5;
        for (int index = 1; index <= BattleShips.playerShips; ) {
            System.out.print("Enter X coordinate for your " + index + " ship: "); int x = input.nextInt();
            System.out.print("Enter Y coordinate for your " + index + " ship: "); int y = input.nextInt();

            if ((x >= 0 && x < numRows) && (y >= 0 && y < numCols) && (Objects.equals(grid[x][y], " "))) {
                grid[x][y] = "@"; index++;
            } else if((x >= 0 && x < numRows) && (y >= 0 && y < numCols) && Objects.equals(grid[x][y], "@")) {
                System.out.println("You can't place two or more ships on the same location");
            } else if((x < 0 || x >= numRows) || (y < 0 || y >= numCols)) {
                System.out.println("You can't place ships outside the " + numRows +
                        " by " + numCols + " grid");
            }
        }
        printOceanMap();
    }

    public static void deployComputerShips() {
        System.out.println("\nComputer is deploying ships");
        // Deploying five ships for computer
        BattleShips.computerShips = 5;
        for (int index = 1; index <= BattleShips.computerShips; ) {
            int x = (int)(Math.random() * 10);
            int y = (int)(Math.random() * 10);

            if ((x >= 0 && x < numRows) && (y >= 0 && y < numCols) && (Objects.equals(grid[x][y], " ")))  {
                grid[x][y] = "x";
                System.out.println(index + ". ship DEPLOYED");
                index++;
            }
        }
        printOceanMap();
    }

    public static void Battle() {
        playerTurn();
        computerTurn();

        printOceanMap();

        System.out.println();
        System.out.println("Your ships: " + BattleShips.playerShips +
                " | Computer ships: " + BattleShips.computerShips);
        System.out.println();
    }

    public static void playerTurn(){
        System.out.println("\nYOUR TURN");
        int x = -1, y = -1;
        do {
            Scanner input = new Scanner(System.in);
            System.out.print("Enter X coordinate: ");
            x = input.nextInt();
            System.out.print("Enter Y coordinate: ");
            y = input.nextInt();

            if ((x >= 0 && x < numRows) && (y >= 0 && y < numCols)) {
                if (Objects.equals(grid[x][y], "x"))  {
                    System.out.println("Boom! You sunk the ship!");
                    grid[x][y] = "!"; //Hit mark
                    --BattleShips.computerShips;
                } else if (Objects.equals(grid[x][y], "@")) {
                    System.out.println("Oh no, you sunk your own ship :(");
                    grid[x][y] = "x";
                    --BattleShips.playerShips;
                    ++BattleShips.computerShips;
                } else if (Objects.equals(grid[x][y], " ")) {
                    System.out.println("Sorry, you missed");
                    grid[x][y] = "-";
                }
            } else {
                System.out.println("You can't place ships outside the " + numRows
                        + " by " + numCols + " grid");
            }
        }while((x < 0 || x >= numRows) || (y < 0 || y >= numCols));  //keep re-prompting till valid guess
    }

    public static void computerTurn() {
        System.out.println("\nCOMPUTER'S TURN");
        // Guess coordinates
        int x = -1, y = -1;
        do {
            x = (int) (Math.random() * 10);
            y = (int) (Math.random() * 10);

            if ((x >= 0 && x < numRows) && (y >= 0 && y < numCols)) // Valid guess
            {
                if (Objects.equals(grid[x][y], "@")) // If player ship is already there; player loses ship
                {
                    System.out.println("The Computer sunk one of your ships!");
                    grid[x][y] = "x";
                    --BattleShips.playerShips;
                    ++BattleShips.computerShips;
                } else if (Objects.equals(grid[x][y], "x")) {
                    System.out.println("The Computer sunk one of its own ships");
                    grid[x][y] = "!";
                } else if (Objects.equals(grid[x][y], " ")) {
                    System.out.println("Computer missed");
                    // Saving missed guesses for computer
                    if (missedGuesses[x][y] != 1) { missedGuesses[x][y] = 1; }
                }
            }
        } while ((x < 0 || x >= numRows) || (y < 0 || y >= numCols));
    }

    public static void gameOver() {
        System.out.println("Your ships: " + BattleShips.playerShips + " | Computer ships: " + BattleShips.computerShips);
        if (BattleShips.playerShips > 0 && BattleShips.computerShips <= 0) {
            System.out.println("Hooray! You won the battle :)"); new Statistics().updateScore(2);
        } else {
            System.out.println("Sorry, you lost the battle"); new Statistics().updateScore(0);
        }
        System.out.println();
    }

    public static void printOceanMap() {
        System.out.println();
        // First section of Ocean Map
        System.out.print("  ");
        for (int index = 0; index < numCols; index++) { System.out.print(index); }
        System.out.println();

        // Middle section of Ocean Map
        for (int x = 0; x < grid.length; x++) {
            System.out.print(x + "|");

            for (int y = 0; y < grid[x].length; y++){
                System.out.print(grid[x][y]);
            }

            System.out.println("|" + x);
        }

        // Last section of Ocean Map
        System.out.print("  ");
        for (int index = 0; index < numCols; index++) { System.out.print(index); }
        System.out.println();
    }
}