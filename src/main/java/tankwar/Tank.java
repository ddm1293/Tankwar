package tankwar;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.Random;

public class Tank {
    private int x;
    private int y;
    private Direction direction;
    private boolean enemy;

    public Tank(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.enemy = false;
    }

    public Tank(int x, int y, Direction direction, boolean enemy) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.enemy = enemy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    Image getImage() {
        String prefix = enemy ? "e" : "";
        switch (this.direction) {
            case UP: return new ImageIcon("assets/images/" + prefix + "tankU.gif").getImage();
            case DOWN: return new ImageIcon("assets/images/" + prefix + "tankD.gif").getImage();
            case LEFT: return new ImageIcon("assets/images/" + prefix + "tankL.gif").getImage();
            case RIGHT: return new ImageIcon("assets/images/" + prefix + "tankR.gif").getImage();

            case UPLEFT: return new ImageIcon("assets/images/" + prefix + "tankLU.gif").getImage();
            case UPRIGHT: return new ImageIcon("assets/images/" + prefix + "tankRU.gif").getImage();
            case DOWNLEFT: return new ImageIcon("assets/images/" + prefix + "tankLD.gif").getImage();
            case DOWNRIGHT: return new ImageIcon("assets/images/" + prefix + "tankRD.gif").getImage();
        }
        return null;
    }

    public void move() {
        if (this.stopped) return;
        switch (this.direction) {
            case UP:
                y -= 5;
                break;
            case DOWN:
                y += 5;
                break;
            case LEFT:
                x -= 5;
                break;
            case RIGHT:
                x += 5;
                break;

            case UPLEFT:
                y -= 5;
                x -= 5;
                break;
            case UPRIGHT:
                y -= 5;
                x += 5;
                break;
            case DOWNLEFT:
                y += 5;
                x -= 5;
                break;
            case DOWNRIGHT:
                y += 5;
                x += 5;
                break;
        }
        this.stayInFrame();
    }

    private void stayInFrame() {
        if (x < 0) x = 0;
        else if (x + this.getImage().getWidth(null) > 800) x = 800 - this.getImage().getWidth(null);
        if (y < 0) y = 0;
        else if (y + this.getImage().getHeight(null)> 600) y = 600 - this.getImage().getHeight(null);
    }

    private boolean hitWall() {
        Rectangle rectangleTank = this.getRectangle();
        for (Wall wall: GameClient.getInstance().getWalls()) {
            if (rectangleTank.intersects(wall.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    private boolean hitEnemy() {
        Rectangle rectangleTank = this.getRectangle();
        for (Tank enemyTank: GameClient.getInstance().getEnemyTanks()) {
            if (rectangleTank.intersects(enemyTank.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, y, this.getImage().getWidth(null),
                this.getImage().getHeight(null));
    }

    private boolean up, down, left, right, stopped;

    private void determineDirection() {
        if (!up && !left && !down && !right) this.stopped = true;
        else {
            if (up && left && !down && !right) this.direction = Direction.UPLEFT;
            else if (up && !left && !down && right) this.direction = Direction.UPRIGHT;
            else if (!up && left && down && !right) this.direction = Direction.DOWNLEFT;
            else if (!up && !left && down && right) this.direction = Direction.DOWNRIGHT;

            else if (up && !left && !down && !right) this.direction = Direction.UP;
            else if (!up && !left && down && !right) this.direction = Direction.DOWN;
            else if (!up && left && !down && !right) this.direction = Direction.LEFT;
            else if (!up && !left && !down && right) this.direction = Direction.RIGHT;

            this.stopped = false;
        }
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                up = true;
                break;
            case KeyEvent.VK_DOWN:
                down = true;
                break;
            case KeyEvent.VK_LEFT:
                left = true;
                break;
            case KeyEvent.VK_RIGHT:
                right = true;
                break;
            case KeyEvent.VK_CONTROL:
                this.fire();
                break;
            case KeyEvent.VK_Z:
                this.superFire();
        }
        //this.determineDirection();
        //this.move();
    }

    private void superFire() {
        for (Direction direction: Direction.values()) {
            Missile missile = new Missile(this.x + this.getImage().getWidth(null) / 2 - 6,
                    this.y + this.getImage().getHeight(null) / 2 - 6, direction, enemy);
            GameClient.getInstance().getMissiles().add(missile);
        }

        // make a random super fire sound
        String audioFile = new Random().nextBoolean()?
                "assets/audios/supershoot.aiff" :
                "assets/audios/supershoot.wav";
        Media sound = new Media(new File(audioFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    private void fire() {
        Missile missile = new Missile(this.x + this.getImage().getWidth(null) / 2 - 6,
                this.y + this.getImage().getHeight(null) / 2 - 6, this.direction, enemy);
        GameClient.getInstance().getMissiles().add(missile);

        // make a sound
        Media sound = new Media(new File("assets/audios/shoot.wav").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                up = false;
                break;
            case KeyEvent.VK_DOWN:
                down = false;
                break;
            case KeyEvent.VK_LEFT:
                left = false;
                break;
            case KeyEvent.VK_RIGHT:
                right = false;
                break;
        }
        //this.determineDirection();
    }

    void draw(Graphics g) {
        int oldX = x, oldY = y;
        this.determineDirection();
        this.move();

        if (this.hitWall()) {
            setX(oldX);
            setY(oldY);
        }

        if (this.hitEnemy()) {
            setX(oldX);
            setY(oldY);
        }

        g.drawImage(this.getImage(), this.x, this.y, null);
    }
}
