package com.vladcelona.miniproject.games;

import com.vladcelona.miniproject.utils.Board;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Pacman extends JFrame {

    public Pacman() {
        initUI();
    }

    private void initUI() {
        add(new Board());
        setTitle("Pacman"); setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(380, 420); setLocationRelativeTo(null);
    }

    public static void launchGame() {
        EventQueue.invokeLater(() -> {
            var executeGame = new Pacman(); executeGame.setVisible(true);
        });
    }
}