package tankwar;

import javax.swing.*;
import java.awt.*;

public class Missile {
    private int x, y;
    private final Direction direction;
    private final boolean enemy;
    private static final int SPEED = 10;
    private boolean stopped;

    public Missile(int x, int y, Direction direction, boolean enemy) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.enemy = enemy;
    }

    private Image getImage() {
        return direction.getImage("missile");
    }

    private void move() {
        if (this.stopped) return;
        this.x += direction.x * SPEED;
        this.y += direction.y * SPEED;
    }

    private boolean outOfFrame() {
        return (this.x < 0 || this.x > 800 || this.y < 0 || this.y > 600) ? true : false;
    }

    private boolean hitWall() {
        Rectangle recMissile = this.getRectangle();
        for (Wall wall: GameClient.getInstance().getWalls()) {
            if (recMissile.intersects(wall.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    private boolean hitEnemy() {
        Rectangle recMissile = this.getRectangle();
        for (Tank enemyTank: GameClient.getInstance().getEnemyTanks()) {
            if (recMissile.intersects(enemyTank.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    private Rectangle getRectangle() {
        return new Rectangle(this.x, this.y, this.getImage().getWidth(null),
                this.getImage().getHeight(null));
    }

    public void draw(Graphics g) {
        this.move();
        if (outOfFrame()) return;
        if (hitWall()) {
            this.stopped = true;
            return;
        }
        if (hitEnemy()) {
            this.stopped = true;
            return;
        }
        g.drawImage(this.getImage(), this.x, this.y, null);
    }
}
