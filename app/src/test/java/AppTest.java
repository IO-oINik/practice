import org.junit.jupiter.api.Test;
import service.HorseRacingService;
import util.HorseRacingFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {
    private static final String TEST_FILE_PATH = "test_horseRacing.txt";

    @Test
    void testGenerateToFile() throws Exception {
        HorseRacingFactory.generateToFile(TEST_FILE_PATH, 5);

        File file = new File(TEST_FILE_PATH);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        Files.delete(Path.of(TEST_FILE_PATH));
    }
}
