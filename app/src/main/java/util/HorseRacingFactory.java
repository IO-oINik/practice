package util;

import entity.HorseRacing;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HorseRacingFactory {
    private static final int DAYS = 500;

    private static final String[] HORSE_NAME = {
            "Blizzard",
            "Dawn",
            "Breeze",
            "Blackie",
            "Thunder",
            "Spark",
            "Lightning",
            "Grey",
            "Rainbow"
    };

    public static HorseRacing generate() {
        Random random = new Random();
        LocalDate raceDate = LocalDate.now().minusDays(random.nextInt(DAYS));
        List<String> horseNames = Arrays.asList(HORSE_NAME);
        Collections.shuffle(horseNames);
        String firstPlaceHorseName = horseNames.get(0);
        String secondPlaceHorseName = horseNames.get(1);
        String thirdPlaceHorseName = horseNames.get(2);

        return new HorseRacing(raceDate, firstPlaceHorseName, secondPlaceHorseName, thirdPlaceHorseName);
    }

    public static void generateToFile(String path, int n) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
            for (int i = 0; i < n; i++) {
                HorseRacing horseRacing = HorseRacingFactory.generate();
                writer.write(String.format("%s;%s;%s;%s\n",
                        horseRacing.getDate(),
                        horseRacing.getFirstPlaceNameHorse(),
                        horseRacing.getSecondPlaceNameHorse(),
                        horseRacing.getThirdPlaceNameHorse()
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}