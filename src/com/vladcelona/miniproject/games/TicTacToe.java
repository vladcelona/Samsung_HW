package com.vladcelona.miniproject.games;

import com.vladcelona.miniproject.main.SelectGame;
import com.vladcelona.miniproject.statistics.Statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TicTacToe {

    private static ArrayList<Integer> playerPositions = new ArrayList<Integer>();
    private static ArrayList<Integer> aiPositions = new ArrayList<Integer>();

    public TicTacToe() {
        ticTacToe();
    }

    private static void ticTacToe() {

        char[][] gameBoard = {
                {' ','|',' ','|',' '},
                {'-','+','-','+','-'},
                {' ','|',' ','|',' '},
                {'-','+','-','+','-'},
                {' ','|',' ','|',' '}
        };

        // 5x5 if we account of symbols that make the board look like a board, positions => [0/2/4][0/2/4]

        System.out.println("Play against CPU or Human? \n(0 for CPU and 1 for Human)");
        Scanner scan = new Scanner(System.in);

        oppTypeCheck: {
            try {
                int opponent = scan.nextInt();

                printGameBoard(gameBoard);

                boolean currentPlayer = Math.random() <= 0.5;

                while (true) {
                    if (currentPlayer) {
                        System.out.println("Player 'X' enter your position (1-9): ");

                        typeCheck1:
                        // to handle bad inputs
                        try {
                            int playerPos = scan.nextInt();

                            while(playerPos > 9 || playerPos < 1) {
                                System.out.println("Invalid position! Enter valid position: ");
                                playerPos = scan.nextInt();
                            }
                            while(playerPositions.contains(playerPos) || aiPositions.contains(playerPos)) {
                                System.out.println("Position taken! Enter valid position: ");
                                playerPos = scan.nextInt();
                            }

                            placePiece(gameBoard, playerPos, "player");
                            playerPositions.add(playerPos); printGameBoard(gameBoard);
                            checkWinner(); currentPlayer = !currentPlayer;
                        }
                        catch(InputMismatchException err) {
                            System.out.println("You've entered a bad input, please try again!");
                            break typeCheck1;
                        }
                    } else {
                        if (opponent == 0) {
                            System.out.println("CPU's turn!");
                            Random rand = new Random();
                            int aiPos = rand.nextInt(9) + 1;
                            while (playerPositions.contains(aiPos) || aiPositions.contains(aiPos))  {
                                aiPos = rand.nextInt(9) + 1;
                            }
                            placePiece(gameBoard, aiPos, "ai");
                            aiPositions.add(aiPos); printGameBoard(gameBoard);
                            checkWinner(); currentPlayer = !currentPlayer;
                        } else {
                            System.out.println("Player 'O', enter your position (1-9): ");

                            typeCheck2:
                            try {
                                int aiPos = scan.nextInt();
                                while (aiPos > 9 || aiPos < 1) {
                                    System.out.println("Invalid position! Enter valid position: ");
                                    aiPos = scan.nextInt();
                                }
                                while (playerPositions.contains(aiPos) || aiPositions.contains(aiPos)) {
                                    System.out.println("Position taken! Enter valid position: ");
                                    aiPos = scan.nextInt();
                                }

                                placePiece(gameBoard, aiPos, "ai");
                                aiPositions.add(aiPos); printGameBoard(gameBoard);
                                checkWinner(); currentPlayer = !currentPlayer;
                            }
                            catch(InputMismatchException err) {
                                System.out.println("You've entered a bad input, please try again!");
                                break typeCheck2;
                            }
                        }
                    }
                }
            }

            catch(InputMismatchException err) {
                System.out.println("You've entered a bad input, please try again!");
                break oppTypeCheck;
            }
        }
    }


    private static void printGameBoard(char[][] gameBoard) {

        for(char[] row : gameBoard) {
            System.out.print("\t");
            for(char col : row) { System.out.print(col); }
            System.out.println();
        }

        System.out.println("\n-----------------------------");;
    }


    private static void placePiece(char[][] gameBoard, int pos, String user) {
        char turn = ' ';
        if(user.equals("player")) {
            turn = 'X';
        } else if(user.equals("ai")) {
            turn = 'O';
        }

        switch (pos) {
            case 1 -> gameBoard[0][0] = turn;
            case 2 -> gameBoard[0][2] = turn;
            case 3 -> gameBoard[0][4] = turn;
            case 4 -> gameBoard[2][0] = turn;
            case 5 -> gameBoard[2][2] = turn;
            case 6 -> gameBoard[2][4] = turn;
            case 7 -> gameBoard[4][0] = turn;
            case 8 -> gameBoard[4][2] = turn;
            case 9 -> gameBoard[4][4] = turn;
        }
    }


    private static void checkWinner() {
        List<Integer> topRow = Arrays.asList(1,2,3);
        List<Integer> midRow = Arrays.asList(4,5,6);
        List<Integer> botRow = Arrays.asList(7,8,9);
        List<Integer> leftCol = Arrays.asList(1,4,7);
        List<Integer> midCol = Arrays.asList(2,5,8);
        List<Integer> rightCol = Arrays.asList(3,6,9);
        List<Integer> cross1 = Arrays.asList(1,5,9);
        List<Integer> cross2 = Arrays.asList(7,5,3);

        List<List<Integer>> winConditions = Arrays.asList(topRow, midRow, botRow, leftCol,
                midCol, rightCol, cross1, cross2);

        for(List<Integer> list : winConditions) {
            if (playerPositions.containsAll(list)) {
                System.out.println("Congratulations, you win!!");
                System.out.println("-----------------------------");
                aiPositions.clear(); playerPositions.clear();
//                playAgain();
                new Statistics().updateScore(2); new SelectGame();
            }
            else if (aiPositions.containsAll(list)) {
                System.out.println("CPU wins, better luck next time :(");
                System.out.println("-----------------------------");
                aiPositions.clear(); playerPositions.clear();
//                playAgain();
                new Statistics().updateScore(0); new SelectGame();
            }
        }

        if (playerPositions.size() + aiPositions.size() == 9) {
            System.out.println("It's a TIE!");
            System.out.println("-----------------------------");
            aiPositions.clear(); playerPositions.clear();
//            playAgain();
            new Statistics().updateScore(1); new SelectGame();
        }
    }


    private static void playAgain() {
        System.out.println("Play again? (y/n)");
        Scanner ans = new Scanner(System.in);
        String answer = ans.next();
        if(answer.contains("y") || answer.contains("Y")) {
            aiPositions.clear(); playerPositions.clear();
            ticTacToe();
        } else {
            System.out.println("You have exited the TicTacToe game");
        }
    }

}