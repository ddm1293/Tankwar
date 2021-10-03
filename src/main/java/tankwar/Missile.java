package tankwar;

import javax.swing.*;
import java.awt.*;

public class Missile {
    private int x, y;
    private final Direction direction;
    private final boolean enemy;
    private int SPEED = 10;
    private boolean stopped;

    public Missile(int x, int y, Direction direction, boolean enemy) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.enemy = enemy;
    }

    private Image getImage() {
        switch (this.direction) {
            case UP: return new ImageIcon("assets/images/missileU.gif").getImage();
            case DOWN: return new ImageIcon("assets/images/missileD.gif").getImage();
            case LEFT: return new ImageIcon("assets/images/missileL.gif").getImage();
            case RIGHT: return new ImageIcon("assets/images/missileR.gif").getImage();

            case UPLEFT: return new ImageIcon("assets/images/missileLU.gif").getImage();
            case UPRIGHT: return new ImageIcon("assets/images/missileRU.gif").getImage();
            case DOWNLEFT: return new ImageIcon("assets/images/missileLD.gif").getImage();
            case DOWNRIGHT: return new ImageIcon("assets/images/missileRD.gif").getImage();
        }
        return null;
    }

    private void move() {
        if (this.stopped) return;
        switch (this.direction) {
            case UP:
                this.y -= SPEED;
                break;
            case DOWN:
                this.y += SPEED;
                break;
            case LEFT:
                x -= SPEED;
                break;
            case RIGHT:
                x += SPEED;
                break;

            case UPLEFT:
                this.y -= SPEED;
                x -= SPEED;
                break;
            case UPRIGHT:
                this.y -= SPEED;
                x += SPEED;
                break;
            case DOWNLEFT:
                this.y += SPEED;
                x -= SPEED;
                break;
            case DOWNRIGHT:
                this.y += SPEED;
                x += SPEED;
                break;
        }
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
