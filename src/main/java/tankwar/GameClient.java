package tankwar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GameClient extends JComponent {

    private static final GameClient INSTANCE = new GameClient();

    public static GameClient getInstance() {
        return INSTANCE;
    }

    private Tank playerTank;
    private List<Tank> enemyTanks;
    private final AtomicInteger enemyKilled = new AtomicInteger(0);
    private List<Wall> walls;
    private List<Missile> missiles;
    private List<Explosion> explosions;

    public Tank getPlayerTank() {
        return playerTank;
    }

    public List<Tank> getEnemyTanks() {
        return enemyTanks;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Missile> getMissiles() {
        return missiles;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    synchronized void addMissile(Missile missile) {
        missiles.add(missile);
    }

    public void addExplosion(Explosion explosion) {
        explosions.add(explosion);
    }

    private GameClient() {
        this.playerTank = new Tank(400, 100, Direction.DOWN);

        this.intiEnemyTanks();

        this.walls = Arrays.asList(
                new Wall(200, 140, true, 15),
                new Wall(200, 540, true,15),
                new Wall(100, 160, false, 12),
                new Wall(700, 160, false, 12)
        );

        this.missiles = new CopyOnWriteArrayList<>();

        this.explosions = new ArrayList<>();

        this.setPreferredSize(new Dimension(800, 600));
    }

    private void intiEnemyTanks() {
        this.enemyTanks = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                this.enemyTanks.add(new Tank(200 + j * 120, 400 + i * 40, Direction.UP, true));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 600);
        super.paintComponent(g);

        if (!playerTank.isAlive()) {
            g.setColor(Color.RED);
            g.setFont(new Font(null, Font.BOLD, 100));
            g.drawString("Game Over", 100, 200);

            g.setFont(new Font(null, Font.BOLD, 50));
            g.drawString("Press A To Restart", 130, 360);
        } else {
            g.setColor(Color.WHITE);
            g.setFont(new Font(null, Font.BOLD, 16));
            g.drawString("Missiles: " + missiles.size(), 10, 50);
            g.drawString("Player Tank HP: " + playerTank.getHP(), 10, 70);
            g.drawString("Enemies Left: " + enemyTanks.size(), 10, 90);
            g.drawString("Enemies Killed: " + enemyKilled.get(), 10 ,110);

            this.playerTank.draw(g);

            int enemySize = enemyTanks.size();
            enemyTanks.removeIf(tank -> !tank.isAlive());
            enemyKilled.addAndGet(enemySize - enemyTanks.size());
            if (enemyTanks.isEmpty()) {
                this.intiEnemyTanks();
            }
            for (Tank tank : enemyTanks) {
                tank.draw(g);
            }

            for (Wall wall: walls) {
                wall.draw(g);
            }

            missiles.removeIf(missile -> !missile.isAlive());
            for (Missile missile: missiles) {
                missile.draw(g);
            }

            explosions.removeIf(explosion -> !explosion.isAlive());
            for (Explosion explosion: explosions) {
                explosion.draw(g);
            }
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

        // Keep repainting
        while (true) {
            try {
                client.repaint();
                if (client.playerTank.isAlive()) {
                    for (Tank enemyTank : client.enemyTanks) {
                        enemyTank.actRandomly();
                    }
                }
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void restart() {
        if (!playerTank.isAlive()) {
            playerTank = new Tank(400, 100 ,Direction.DOWN);
        }
        this.intiEnemyTanks();
    }
}
