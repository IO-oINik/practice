import entity.HorseRacing;
import service.HorseRacingService;

import java.util.List;

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

    public static void main(String[] args) {
        try {
            HorseRacingService.generateToFile(PATH, 30);
        } catch (RuntimeException e) {
            System.out.println("Failed to generate to file");
            return;
        }

        List<HorseRacing> horseRacings = HorseRacingService.loadFromFile(PATH);

        System.out.printf("Most successful horse: %s\n\n", HorseRacingService.findMostSuccessfulHorse(horseRacings));
        System.out.printf("Most frequent horse: %s\n\n", HorseRacingService.findMostFrequentHorse(horseRacings));
    }
}