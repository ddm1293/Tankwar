package tankwar;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.*;


class TankTest {

    @Test
    void getValidImage() {
        for (Direction direction: Direction.values()) {
            Tank tank = new Tank(0, 0, direction, false);
            assertTrue(tank.getImage().getWidth(null) > 0,
                    direction + "cannot get a valid image");



            Tank enemyTank = new Tank(0, 0, direction, true);
            assertTrue(tank.getImage().getWidth(null) > 0,
                    direction + "cannot get a valid image");
        }
    }
}