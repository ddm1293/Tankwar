package tankwar;

import java.awt.*;

public class Missile {
    private int x, y;
    private final Direction direction;
    private final boolean enemy;
    private static final int SPEED = 10;
    private boolean dead;
    public static final int ATTACK = 25;

    public Missile(int x, int y, Direction direction, boolean enemy) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.enemy = enemy;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    private Image getImage() {
        return direction.getImage("missile");
    }

    private void move() {
        if (this.dead) return;
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

    private boolean hitTank() {
        Rectangle recMissile = this.getRectangle();
        
        if (this.enemy) {
            Tank playerTank = GameClient.getInstance().getPlayerTank();
            if (recMissile.intersects(playerTank.getRectangle())) {
                playerTank.getHitByMissile();
                /*
                playerTank.setHP(playerTank.getHP() - 10); //初步版本
                if (playerTank.getHP() <= 0) {
                    playerTank.setAlive(false);
                }*/
                return true;
            }
        } else {
            for (Tank enemyTank: GameClient.getInstance().getEnemyTanks()) {
                if (recMissile.intersects(enemyTank.getRectangle())) {
                    enemyTank.getHitByMissile();
                    return true;
                }
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
        if (outOfFrame()) {
            this.setDead(true);
            return;
        }
        if (hitWall()) {
            this.setDead(true);
            return;
        }
        if (hitTank()) {
            this.setDead(true);
            return;
        }
        g.drawImage(this.getImage(), this.x, this.y, null);
    }
}
