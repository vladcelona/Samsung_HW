package com.vladcelona.miniproject.utils;

import com.vladcelona.miniproject.statistics.Statistics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private Dimension d;
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

    private Image ii;
    private final Color dotColor = new Color(192, 192, 0);
    private Color mazeColor;

    private boolean inGame = false;
    private boolean dying = false;

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int PAC_ANIM_DELAY = 2;
    private final int PACMAN_ANIM_COUNT = 4;
    private final int MAX_GHOSTS = 12;
    private final int PACMAN_SPEED = 6;

    private int pacAnimCount = PAC_ANIM_DELAY;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    private int N_GHOSTS = 6;
    private int pacsLeft, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Image ghost;
    private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private Image pacman3up, pacman3down, pacman3left, pacman3right;
    private Image pacman4up, pacman4down, pacman4left, pacman4right;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy, view_dx, view_dy;

    private final short levelData[] = {
            19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,
            25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21,
            1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
            1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21,
            1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
            9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
    };

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 6;

    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    public Board() {
        loadImages();
        initVariables();
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.black);
    }

    private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        mazeColor = new Color(5, 100, 5);
        d = new Dimension(400, 400);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];

        timer = new Timer(40, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        initGame();
    }

    private void doAnim() {

        pacAnimCount--;

        if (pacAnimCount <= 0) {
            pacAnimCount = PAC_ANIM_DELAY;
            pacmanAnimPos = pacmanAnimPos + pacAnimDir;

            if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
    }

    private void playGame(Graphics2D graphics2D) {
        if (dying) {
            death(); 
            System.out.println();
        } else {
            movePacman(); drawPacman(graphics2D);
            moveGhosts(graphics2D); checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D graphics2D) {

        graphics2D.setColor(new Color(0, 32, 48));
        graphics2D.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
        graphics2D.setColor(Color.white);
        graphics2D.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

        String s = "Press s to start.";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        graphics2D.setColor(Color.white);
        graphics2D.setFont(small);
        graphics2D.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, SCREEN_SIZE / 2);
    }

    private void drawScore(Graphics2D graphics) {
        String s = "Score: " + score;
        graphics.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for (int i = 0; i < pacsLeft; i++) {
            graphics.drawImage(pacman3left, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }

    private void checkMaze() {

        short i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {
            if ((screenData[i] & 48) != 0) { finished = false; }
            i++;
        }

        if (finished) {

            score += 50;
            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    private void death() {
        pacsLeft--;
        if (pacsLeft == 0) {
            inGame = false;
            new Statistics().updatePacman(score);
        }
        continueLevel();
    }

    private void moveGhosts(Graphics2D graphics2D) {

        int position; int count;

        for (int i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                position = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[position] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[position] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[position] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[position] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {
                    if ((screenData[position] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {
                    count = (int) (Math.random() * count);
                    if (count > 3) { count = 3; }
                    
                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }
            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(graphics2D, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {
                dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D graphics2D, int x, int y) {
        graphics2D.drawImage(ghost, x, y, this);
    }

    private void movePacman() {
        int position; short ch;

        if (req_dx == -pacmand_x && req_dy == -pacmand_y) {
            pacmand_x = req_dx;
            pacmand_y = req_dy;
            view_dx = pacmand_x;
            view_dy = pacmand_y;
        }

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            position = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[position];

            if ((ch & 16) != 0) {
                screenData[position] = (short) (ch & 15);
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                    view_dx = pacmand_x;
                    view_dy = pacmand_y;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void drawPacman(Graphics2D graphics2D) {
        if (view_dx == -1) {
            drawPacmanLeft(graphics2D);
        } else if (view_dx == 1) {
            drawPacmanRight(graphics2D);
        } else if (view_dy == -1) {
            drawPacmanUp(graphics2D);
        } else {
            drawPacmanDown(graphics2D);
        }
    }

    private void drawPacmanUp(Graphics2D graphics2D) {

        switch (pacmanAnimPos) {
            case 1:
                graphics2D.drawImage(pacman2up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                graphics2D.drawImage(pacman3up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                graphics2D.drawImage(pacman4up, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                graphics2D.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanDown(Graphics2D graphics2D) {

        switch (pacmanAnimPos) {
            case 1:
                graphics2D.drawImage(pacman2down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                graphics2D.drawImage(pacman3down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                graphics2D.drawImage(pacman4down, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                graphics2D.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanLeft(Graphics2D graphics2D) {
        switch (pacmanAnimPos) {
            case 1:
                graphics2D.drawImage(pacman2left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                graphics2D.drawImage(pacman3left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                graphics2D.drawImage(pacman4left, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                graphics2D.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D graphics2D) {

        switch (pacmanAnimPos) {
            case 1:
                graphics2D.drawImage(pacman2right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                graphics2D.drawImage(pacman3right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                graphics2D.drawImage(pacman4right, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                graphics2D.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawMaze(Graphics2D graphics2D) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                graphics2D.setColor(mazeColor);
                graphics2D.setStroke(new BasicStroke(2));

                if ((screenData[i] & 1) != 0) {
                    graphics2D.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    graphics2D.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    graphics2D.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    graphics2D.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    graphics2D.setColor(dotColor);
                    graphics2D.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    private void initGame() {

        pacsLeft = 3;
        score = 0;
        initLevel();
        N_GHOSTS = 6;
        currentSpeed = 3;
    }

    private void initLevel() {

        System.arraycopy(levelData, 0, screenData, 0, N_BLOCKS * N_BLOCKS);
        continueLevel();
    }

    private void continueLevel() {

        int dx = 1; int random;

        for (int i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 4 * BLOCK_SIZE;
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) { random = currentSpeed; }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacman_x = 7 * BLOCK_SIZE;
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0;
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        view_dx = -1;
        view_dy = 0;
        dying = false;
    }

    private void loadImages() {
        ghost = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/ghost.png").getImage();
        pacman1 = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/pacman.png").getImage();
        pacman2up = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/up1.png").getImage();
        pacman3up = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/up2.png").getImage();
        pacman4up = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/up3.png").getImage();
        pacman2down = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/down1.png").getImage();
        pacman3down = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/down2.png").getImage();
        pacman4down = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/down3.png").getImage();
        pacman2left = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/left1.png").getImage();
        pacman3left = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/left2.png").getImage();
        pacman4left = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/left3.png").getImage();
        pacman2right = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/right1.png").getImage();
        pacman3right = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/right2.png").getImage();
        pacman4right = new ImageIcon("C:/Users/vladb/IdeaProjects/Samsung_HW/src/com/vladcelona/miniproject/images/right3.png").getImage();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        doDrawing(graphics);
    }

    private void doDrawing(Graphics graphics) {

        Graphics2D graphics2D = (Graphics2D) graphics;

        graphics2D.setColor(Color.black);
        graphics2D.fillRect(0, 0, d.width, d.height);

        drawMaze(graphics2D);
        drawScore(graphics2D);
        doAnim();

        if (inGame) {
            playGame(graphics2D);
        } else {
            showIntroScreen(graphics2D);
        }

        graphics2D.drawImage(ii, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        graphics2D.dispose();
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent event) {

            int key = event.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } else if (key == KeyEvent.VK_PAUSE) {
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
            } else {
                if (key == 's' || key == 'S') {
                    inGame = true;
                    initGame();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
            int key = event.getKeyCode();
            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) {
                req_dx = 0;
                req_dy = 0;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        repaint();
    }
}