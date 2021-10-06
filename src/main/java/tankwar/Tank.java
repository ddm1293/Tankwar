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

    public Tank(Save.Position position, boolean enemy) {
        this(position.getX(), position.getY(), position.getDirection(), enemy);
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
        if (x < 0) {
            x = 0;
        } else if (x + this.getImage().getWidth(null) > GameClient.WIDTH) {
            x = GameClient.WIDTH - this.getImage().getWidth(null);
        }

        if (y < 0) {
            y = 0;
        } else if (y + this.getImage().getHeight(null)> GameClient.HEIGHT) {
            y = GameClient.HEIGHT - this.getImage().getHeight(null);
        }
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
            if (enemyTank != this && rectangleTank.intersects(enemyTank.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    private boolean hitFirstAid() {
        Rectangle rectangleTank = this.getRectangle();
        FirstAid firstAid = GameClient.getInstance().getFirstAid();
        if (rectangleTank.intersects(firstAid.getRectangle())
                && firstAid.isAlive()) {
            return true;
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
                break;
            case KeyEvent.VK_A:
                GameClient.getInstance().restart();
                break;
        }
        //this.determineDirection();
        //this.move();
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
        Tools.playSound(audioFile);
    }

    private void fire() {
        Missile missile = new Missile(this.x + this.getImage().getWidth(null) / 2 - 6,
                this.y + this.getImage().getHeight(null) / 2 - 6, this.direction, enemy);
        GameClient.getInstance().getMissiles().add(missile);

        // make a sound
        Tools.playSound("assets/audios/shoot.wav");
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

    public void draw(Graphics g) {

        int oldX = x, oldY = y;
        if (!this.enemy) this.determineDirection();
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

        // Enemies hit into playerTank
        if (this.enemy &&
                this.getRectangle().intersects(GameClient.getInstance().getPlayerTank().getRectangle())) {
            setX(oldX);
            setY(oldY);
        }

        this.drawHPBar(g);

        //this.drawCamel(g);

        if (!enemy) {
            if (this.hitFirstAid()) {
                int recovery = (this.getHP() + FirstAid.RECOVERY >= MY_HP)?
                        MY_HP : this.getHP() + FirstAid.RECOVERY;
                this.setHP(recovery);
                Tools.playSound("assets/audios/revive.wav");
                GameClient.getInstance().getFirstAid().setAlive(false);
            }
        }


        g.drawImage(this.getImage(), this.x, this.y, null);
    }

    private void drawCamel(Graphics g) {
        if (!enemy) {
            Image camel = Tools.getImage("pet-camel.gif");
            g.drawImage(camel, this.x - camel.getWidth(null) - 4,
                    this.y, null);
        }
    }

    private final Random random = new Random();
    private int step = random.nextInt(12) + 3;

    public void actRandomly() {
        Direction[] directions = Direction.values();
        if (step == 0) {
            step = random.nextInt(12) + 3;
            this.direction = directions[random.nextInt(directions.length)];
            if (random.nextBoolean()) {
                this.fire();
            }
        }
        step--;
    }

    private void drawHPBar(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(this.x, this.y - 10, this.getImage().getWidth(null), 10);
        g.setColor(Color.RED);
        int oldHP = enemy? ENEMY_HP : MY_HP;
        int HPLeft = HP * this.getImage().getWidth(null) / oldHP;
        g.fillRect(this.x, this.y - 10, HPLeft, 10);
    }

    public boolean isDying() {
        return this.getHP() <= MY_HP * 0.2;
    }

    public Save.Position getPosition() {
        return new Save.Position(this.x, this.y, this.direction);
    }
}
