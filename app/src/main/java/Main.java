import service.HorseRacingService;
import util.HorseRacingFactory;

public class Main {
    public static void main(String[] args) {
        HorseRacingApplication horseRacingApplication = new HorseRacingApplication(
                new HorseRacingFactory(),
                new HorseRacingService()
        );

        horseRacingApplication.start();
    }
}
