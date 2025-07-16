import entity.HorseRacing;
import entity.HorseStatistics;
import service.HorseRacingService;
import util.HorseRacingFactory;

import java.util.List;
import java.util.Map;

/*
Есть файл с записями о результатах лошадиных скачек
Каждая строка файла имеет следующую структуру:
дата скачек;имя лошади занявшей 1 место;имя лошади занявшей 2 место;имя лошади занявшей 3 место;

Необходимо собрать статистику забегов для каждой лошади
Пользователь должен иметь возможность узнать статистику для любой лошади, самую успешную лошадь,
самую активно участвующую в соревнованиях лошадь
*/

public class HorseRacingApplication {
    private static final String PATH = "horseRacing.txt";
    private final HorseRacingFactory horseRacingFactory;
    private final HorseRacingService horseRacingService;


    public HorseRacingApplication(HorseRacingFactory horseRacingFactory, HorseRacingService horseRacingService) {
        this.horseRacingFactory = horseRacingFactory;
        this.horseRacingService = horseRacingService;
    }

    public void start() {
        try {
            horseRacingFactory.generateToFile(PATH, 30);
        } catch (RuntimeException e) {
            System.out.println("Failed to generate to file");
            return;
        }

        List<HorseRacing> horseRacings = horseRacingService.loadFromFile(PATH);

        System.out.printf("Most successful horse: %s\n", horseRacingService.findMostSuccessfulHorse(horseRacings));
        System.out.printf("Most frequent horse: %s\n\n", horseRacingService.findMostFrequentHorse(horseRacings));
        System.out.println("Horses statistics:");
        Map<String, HorseStatistics> horseStatistics = horseRacingService.calculateHorseStatistics(horseRacings);
        for (Map.Entry<String, HorseStatistics> entry : horseStatistics.entrySet()) {
            System.out.printf("%s: %s\n", entry.getKey(), entry.getValue());
        }
    }
}