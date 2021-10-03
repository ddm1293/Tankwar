package tankwar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameClient extends JComponent {

    private static final GameClient INSTANCE = new GameClient();

    public static GameClient getInstance() {
        return INSTANCE;
    }

    private Tank playerTank;
    private List<Tank> enemyTanks;
    private List<Wall> walls;
    private List<Missile> missiles;

    public List<Tank> getEnemyTanks() {
        return enemyTanks;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Missile> getMissiles() {
        return missiles;
    }

    private GameClient() {
        this.playerTank = new Tank(400, 100, Direction.DOWN);

        this.enemyTanks = new ArrayList<>(12);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                this.enemyTanks.add(new Tank(200 + j * 120, 400 + i * 40, Direction.UP, true));
            }
        }

        this.walls = Arrays.asList(
                new Wall(200, 140, true, 15),
                new Wall(200, 540, true,15),
                new Wall(100, 80, false, 15),
                new Wall(700, 80, false, 15)
        );

        this.missiles = new ArrayList<>();

        this.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 600);
        super.paintComponent(g);
        this.playerTank.draw(g);
        for (Tank tank : enemyTanks) {
            tank.draw(g);
        }
        for (Wall wall: walls) {
            wall.draw(g);
        }
        for (Missile missile: missiles) {
            missile.draw(g);
        }
    }

    public static void main(String[] args) {
        com.sun.javafx.application.PlatformImpl.startup(()->{});
        JFrame frame = new JFrame();
        frame.setTitle("Tank War");
        frame.setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        // frame.setLocationRelativeTo(null);
        final GameClient client = GameClient.getInstance();
        client.repaint();
        frame.add(client);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.addKeyListener(new KeyAdapter() {
            // pressed = move in the desired direction
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                client.playerTank.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                client.playerTank.keyReleased(e);
            }
        });

        // 不是很懂
        while (true) {
            client.repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
