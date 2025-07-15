package service;

import entity.HorseRacing;
import entity.HorseStatistics;
import util.HorseRacingFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HorseRacingService {
    private static final int INDEX_DATE = 0;
    private static final int INDEX_FIRST_PLACE = 1;
    private static final int  INDEX_SECOND_PLACE = 2;
    private static final int INDEX_THIRD_PLACE = 3;

    public static List<HorseRacing> loadFromFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            List<HorseRacing> races = reader.lines()
                    .map(line -> {
                        String[] parts = line.split(";");
                        LocalDate raceDate = LocalDate.parse(parts[INDEX_DATE]);
                        String firstPlaceNameHorse = parts[INDEX_FIRST_PLACE];
                        String secondPlaceNameHorse = parts[INDEX_SECOND_PLACE];
                        String thirdPlaceNameHorse = parts[INDEX_THIRD_PLACE];
                        return new HorseRacing(
                                raceDate,
                                firstPlaceNameHorse,
                                secondPlaceNameHorse,
                                thirdPlaceNameHorse);
                    }).toList();
            return races;
        } catch (IOException e) {
            return new ArrayList<>();
        }
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

    public static String findMostFrequentHorse(List<HorseRacing> races) {
        Map<String, Integer> horseParticipationCount = races.stream()
                .flatMap(race -> Stream.of(
                        race.getFirstPlaceNameHorse(),
                        race.getSecondPlaceNameHorse(),
                        race.getThirdPlaceNameHorse()
                ))
                .collect(Collectors.toMap(
                        horseName -> horseName,
                        horseName -> 1,
                        Integer::sum
                ));
        String result = findHorseNameMaxCount(horseParticipationCount);
        return result;
    }

    public static String findMostSuccessfulHorse(List<HorseRacing> races) {
        Map<String, Integer> horseSuccessCount = races.stream()
                .flatMap(race -> Stream.of(
                        Map.entry(race.getFirstPlaceNameHorse(), 3),
                        Map.entry(race.getSecondPlaceNameHorse(), 2),
                        Map.entry(race.getThirdPlaceNameHorse(), 1)
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum
                ));

        String result = findHorseNameMaxCount(horseSuccessCount);
        return result;
    }

    public static Map<String, HorseStatistics> calculateHorseStatistics(List<HorseRacing> horseRacings) {
        Map<String, HorseStatistics> horseStats = horseRacings.stream()
                .flatMap(race -> Stream.of(
                        Map.entry(race.getFirstPlaceNameHorse(), 1),
                        Map.entry(race.getSecondPlaceNameHorse(), 2),
                        Map.entry(race.getThirdPlaceNameHorse(), 3)
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            HorseStatistics stats = new HorseStatistics();
                            switch (entry.getValue()) {
                                case 1:
                                    stats.addFirstPlace();
                                    break;
                                case 2:
                                    stats.addSecondPlace();
                                    break;
                                case 3:
                                    stats.addThirdPlace();
                                    break;
                            }
                            return stats;
                        },
                        (existing, replacement) -> {
                            if (replacement.getFirstPlaces() > 0) {
                                existing.addFirstPlace();
                            }
                            if (replacement.getSecondPlaces() > 0) {
                                existing.addSecondPlace();
                            }
                            if (replacement.getThirdPlaces() > 0) {
                                existing.addThirdPlace();
                            }
                            return existing;
                        }
                ));

        return horseStats;
    }

    private static String findHorseNameMaxCount(Map<String, Integer> horseMap) {
        return horseMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

}