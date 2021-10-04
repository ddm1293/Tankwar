package tankwar;

import java.awt.*;

public class Explosion {
    private int x, y;
    private int step = 0;
    private boolean alive;

    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.alive = true;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void draw(Graphics g) {
        if (step > 10) {
            this.setAlive(false);
            return;
        }
        g.drawImage(Tools.getImage(this.step++ + ".gif"), x, y, null);
    }
}
