package tankwar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameClient extends JComponent {

    private Tank playerTank;

    private GameClient() {
        this.playerTank = new Tank(400, 100, Direction.DOWN);

        this.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.playerTank.draw(g);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Tank War");
        frame.setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        // frame.setLocationRelativeTo(null);
        GameClient client = new GameClient();
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
