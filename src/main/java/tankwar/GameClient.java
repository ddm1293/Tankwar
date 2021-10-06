package tankwar;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameClient extends JComponent {

    private static final GameClient INSTANCE = new GameClient();
    public static final String GAME_SAVE = "game.save";
    public static final int WIDTH = 800, HEIGHT = 600;

    public static GameClient getInstance() {
        return INSTANCE;
    }

    private Tank playerTank;
    private List<Tank> enemyTanks;
    private final AtomicInteger enemyKilled = new AtomicInteger(0);
    private List<Wall> walls;
    private List<Missile> missiles;
    private List<Explosion> explosions;
    private FirstAid firstAid;

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

    public FirstAid getFirstAid() {
        return firstAid;
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

        this.firstAid = new FirstAid(400, 250); // 待定

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
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
        g.fillRect(0, 0, WIDTH, HEIGHT);
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

            g.drawImage(Tools.getImage("tree.png"), 720, 10, null);
            g.drawImage(Tools.getImage("tree.png"), 10, 520, null);

            this.playerTank.draw(g);

            int enemySize = enemyTanks.size();
            enemyTanks.removeIf(tank -> !tank.isAlive());
            enemyKilled.addAndGet(enemySize - enemyTanks.size());
            if (enemyTanks.isEmpty()) {
                this.intiEnemyTanks();
            }
            for (Tank enemyTank : enemyTanks) {
                enemyTank.draw(g);
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

            if (playerTank.isDying() && new Random().nextInt(3) == 2) {
                firstAid.setAlive(true);
            }
            if (firstAid.isAlive()) {
                firstAid.draw(g);
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
        frame.add(client);
        frame.pack();
        frame.setVisible(true);
        // frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    client.save();
                    System.exit(0);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Failed to save current game!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                System.exit(4);
            }
        });

        try {
            client.load();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to load previous game!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

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

    private void load() throws IOException {
        File file = new File(GAME_SAVE);

        if (file.exists() && file.isFile()) {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            Save save = JSON.parseObject(json, Save.class);
            if (save.isGameContinued()) {
                this.playerTank = new Tank(save.getPlayerTankPosition(), false);

                this.enemyTanks.clear();
                List<Save.Position> enemyPositions = save.getEnemyTankPosition();
                if (enemyPositions != null && !enemyPositions.isEmpty()) {
                    for (Save.Position position: enemyPositions) {
                        this.enemyTanks.add(new Tank(position, true));
                    }
                }
            }
        }

    }

    public void save(String destination) throws IOException {
        Save save = new Save(playerTank.isAlive(),
                playerTank.getPosition(),
                enemyTanks.stream()
                        .filter(Tank::isAlive)
                        .map(Tank::getPosition)
                        .collect(Collectors.toList()));

        /*try (PrintWriter out = new PrintWriter(
                new BufferedWriter(
                        new FileWriter(destination)))) {
            out.println(JSON.toJSONString(save, true));*/
        FileUtils.write(new File(destination), JSON.toJSONString(save, true), StandardCharsets.UTF_8);
    }

    public void save() throws IOException {
        save(GAME_SAVE);
    }

    public void restart() {
        if (!playerTank.isAlive()) {
            playerTank = new Tank(400, 100 ,Direction.DOWN);
        }
        this.intiEnemyTanks();
    }
}
