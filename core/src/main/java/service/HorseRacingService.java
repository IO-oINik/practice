package service;

import entity.HorseRacing;
import entity.HorseStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HorseRacingService {
    private static final Logger logger = LogManager.getLogger(HorseRacingService.class);

    private static final int INDEX_DATE = 0;
    private static final int INDEX_FIRST_PLACE = 1;
    private static final int INDEX_SECOND_PLACE = 2;
    private static final int INDEX_THIRD_PLACE = 3;

    public List<HorseRacing> loadFromFile(String path) {
        logger.info("Loading races from file: {}", path);
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            List<HorseRacing> races = reader.lines()
                    .map(line -> {
                        String[] parts = line.split(";");
                        LocalDate raceDate = LocalDate.parse(parts[INDEX_DATE]);
                        String firstPlaceNameHorse = parts[INDEX_FIRST_PLACE];
                        String secondPlaceNameHorse = parts[INDEX_SECOND_PLACE];
                        String thirdPlaceNameHorse = parts[INDEX_THIRD_PLACE];
                        logger.debug("Parsed race: {} -> 1st: {}, 2nd: {}, 3rd: {}", raceDate, firstPlaceNameHorse, secondPlaceNameHorse, thirdPlaceNameHorse);
                        return new HorseRacing(raceDate, firstPlaceNameHorse, secondPlaceNameHorse, thirdPlaceNameHorse);
                    })
                    .toList();
            logger.info("Loaded {} races from file", races.size());
            return races;
        } catch (IOException e) {
            logger.error("Failed to load races from file: {}", path, e);
            return new ArrayList<>();
        }
    }

    public String findMostFrequentHorse(List<HorseRacing> races) {
        logger.info("Finding most frequently participating horse out of {} races", races.size());
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
        logger.info("Most frequent horse: {}", result);
        return result;
    }

    public String findMostSuccessfulHorse(List<HorseRacing> races) {
        logger.info("Finding most successful horse from {} races", races.size());
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
        logger.info("Most successful horse: {}", result);
        return result;
    }

    public Map<String, HorseStatistics> calculateHorseStatistics(List<HorseRacing> horseRacings) {
        logger.info("Calculating statistics for {} races", horseRacings.size());
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
                                case 1 -> stats.addFirstPlace();
                                case 2 -> stats.addSecondPlace();
                                case 3 -> stats.addThirdPlace();
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
        logger.info("Statistics calculated for {} horses", horseStats.size());
        return horseStats;
    }

    private String findHorseNameMaxCount(Map<String, Integer> horseMap) {
        return horseMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
