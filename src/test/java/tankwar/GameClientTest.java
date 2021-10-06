package tankwar;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GameClientTest {

    @Test
    void save() throws IOException {
        String destination = "tmp/game.sav";
        GameClient.getInstance().save(destination);

        byte[] bytes = Files.readAllBytes(Paths.get(destination));
        Save save = JSON.parseObject(bytes, Save.class);

        assertTrue(save.isGameContinued());
        assertEquals(12, save.getEnemyTankPosition().size());
    }
}