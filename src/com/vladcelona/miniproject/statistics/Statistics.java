package com.vladcelona.miniproject.statistics;

public class Statistics {

    private static int gameScore = 0;
    private static int loseCount = 0;
    private static int drawCount = 0;
    private static int winCount = 0;
    private static int maxPacmanScore = 0;
    private static int previousPacmanScore = 0;
    private static int pacmanGamesPlayed = 0;
    public void scoreRules() {
        System.out.println("The rules of getting game scores: ");
        System.out.println("You get 0 score for losing a game");
        System.out.println("You get 1 score for a draw");
        System.out.println("You get 2 scores for winning a game");
    }

    public void updateScore(int gameCode) {
        gameScore += gameCode;
        if (gameCode == 0) { loseCount++; }
        else if (gameCode == 1) { drawCount++; }
        else { winCount++; }
    }

    public void updatePacman(int score) {
        maxPacmanScore = Math.max(maxPacmanScore, score);
        previousPacmanScore = score; pacmanGamesPlayed++;
    }

    public void info() {
        System.out.println("-----------------------------");
        System.out.println("Overall games played: " + (loseCount + drawCount + winCount + pacmanGamesPlayed));
        System.out.println("Games lost: " + loseCount);
        System.out.println("Games ended with draw: " + drawCount);
        System.out.println("Games won: " + winCount);
        System.out.println("-----------------------------");
        System.out.println("Your game score: " + gameScore);
        System.out.println("-----------------------------");
        System.out.println("-----------------------------");
        System.out.println("Pacman stats: ");
        System.out.println("Pacman games played: " + pacmanGamesPlayed);
        System.out.println("Max score: " + maxPacmanScore);
        System.out.println("Previous score: " + previousPacmanScore);
        System.out.println("-----------------------------");
    }
}
