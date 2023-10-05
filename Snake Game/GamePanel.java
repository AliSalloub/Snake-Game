import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private static final int SCREEN_WIDTH = 1300;
    private static final int SCREEN_HEIGHT = 750;
    private static final int UNIT_SIZE = 50;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 175;

    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;

    private Timer timer;
    private final Random random;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        int result = JOptionPane.showOptionDialog(this, "Do you want to start the game?", "Snake Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (result == JOptionPane.YES_OPTION) {
        startGame();
        }else {
        System.exit(0);  // Exit the game if "No" is selected
        }
    }

    private void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            displayScore(g);
        } else {
            gameOver(g);
        }
    }

    private void displayScore(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        String scoreString = "Score: " + applesEaten;
        g.drawString(scoreString, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(scoreString)) / 2, g.getFont().getSize());
    }

    private void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    private void move() {
        System.arraycopy(x, 0, x, 1, bodyParts);
        System.arraycopy(y, 0, y, 1, bodyParts);

        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    private void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        if (x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void gameOver(Graphics g) {
        displayScore(g);
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        String gameOverString = "Game Over";
        g.drawString(gameOverString, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameOverString)) / 2, SCREEN_HEIGHT / 2);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                displayRestartOption();
            }
        });
    }

    private void displayRestartOption() {
        
        int result = JOptionPane.showOptionDialog(this, "Do you want to restart?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (result == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    public void restartGame() {
        timer.stop();
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = false;
        for (int i = 0; i < GAME_UNITS; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        startGame();
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:  // Added for 'A' key
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:  // Added for 'D' key
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:  // Added for 'W' key
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:  // Added for 'S' key
                    if (direction != 'U') direction = 'D';
                    break;
            }
        }
    }
    
}
