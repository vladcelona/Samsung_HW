package com.vladcelona.miniproject.main;

import com.vladcelona.miniproject.games.BattleShips;
import com.vladcelona.miniproject.games.Pacman;
import com.vladcelona.miniproject.games.TicTacToe;
import com.vladcelona.miniproject.statistics.Statistics;

import java.util.*;
import java.lang.*;

public class SelectGame {

    private static final Scanner scan = new Scanner(System.in);

    public SelectGame() {
        chooseGame();
    }

    private void chooseGame() {

        label:
        while (true) {
            System.out.println("Choose the game from thee list below: ");
            System.out.println("1. Battle Ships game");
            System.out.println("2. TicTacToe game");
            System.out.println("3. Pacman game");
            System.out.println();
            System.out.println("If you'd like to exit from the menu, then press 0");
            System.out.println("If you want to see your game statistics, then write 'stats'");

            System.out.print("Enter you choice: ");
            String choice = scan.nextLine();
            switch (choice) {
                case "1":
                    System.out.println();
                    new BattleShips();
                    break;
                case "2":
                    System.out.println();
                    new TicTacToe();
                    break;
                case "3":
                    System.out.println();
                    Pacman.launchGame();
                    break;
                case "0":
                    System.out.println("The process has been terminated");
                    break label;
                case "stats":
                    new Statistics().info();
                    break;
            }
        }

        System.out.println();
    }
}
