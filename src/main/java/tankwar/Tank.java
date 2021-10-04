package tankwar;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Random;

public class Tank {
    private int x;
    private int y;
    private Direction direction;
    private boolean enemy;
    private static final int MOVE_SPEED = 5;
    private int HP;
    private boolean alive;

    private static final int MY_HP = 100;
    private static final int ENEMY_HP = 50;

    public Tank(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.enemy = false;
        this.alive = true;
        this.HP = MY_HP;
    }

    public Tank(int x, int y, Direction direction, boolean enemy) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.enemy = enemy;
        this.alive = true;
        this.HP = enemy? ENEMY_HP : MY_HP;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isEnemy() {
        return enemy;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public void getHitByMissile() {
        if (this.alive) {
            this.HP -= Missile.ATTACK;
        }
        if (this.HP <= 0) this.setAlive(false);
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
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
        return direction.getImage(prefix + "tank");
    }

    public void move() {
        if (this.stopped) return;
        this.x += direction.x * MOVE_SPEED;
        this.y += direction.y * MOVE_SPEED;
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
            if (up && left && !down && !right) this.direction = Direction.LEFT_UP;
            else if (up && !left && !down && right) this.direction = Direction.RIGHT_UP;
            else if (!up && left && down && !right) this.direction = Direction.LEFT_DOWN;
            else if (!up && !left && down && right) this.direction = Direction.RIGHT_DOWN;

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

    // make a sound based on the given string source of the sound
    private void fireSound(String audioFile) {
        Media sound = new Media(new File(audioFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    private void superFire() {
        for (Direction direction: Direction.values()) {
            Missile missile = new Missile(this.x + this.getImage().getWidth(null) / 2 - 6,
                    this.y + this.getImage().getHeight(null) / 2 - 6, direction, enemy);
            GameClient.getInstance().addMissile(missile);
        }

        // make a random super fire sound
        String audioFile = new Random().nextBoolean()?
                "assets/audios/supershoot.aiff" :
                "assets/audios/supershoot.wav";
        fireSound(audioFile);
    }

    private void fire() {
        Missile missile = new Missile(this.x + this.getImage().getWidth(null) / 2 - 6,
                this.y + this.getImage().getHeight(null) / 2 - 6, this.direction, enemy);
        GameClient.getInstance().getMissiles().add(missile);

        // make a sound
        fireSound("assets/audios/shoot.wav");
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

        this.stayInFrame();

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
