import entity.HorseRacing;
import entity.HorseStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import service.HorseRacingService;
import util.HorseRacingFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class AppIntegrationsTest {

    @Mock
    private HorseRacingFactory factory;

    @Mock
    private HorseRacingService service;

    @InjectMocks
    private HorseRacingApplication application;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStart_successfulExecution() {
        String path = "horseRacing.txt";

        List<HorseRacing> mockRaces = List.of(
                new HorseRacing(LocalDate.of(2024, 5, 1), "Flash", "Thunder", "Storm"),
                new HorseRacing(LocalDate.of(2024, 5, 2), "Thunder", "Flash", "Storm"),
                new HorseRacing(LocalDate.of(2024, 5, 3), "Flash", "Storm", "Thunder")
        );

        HorseStatistics flashStats = new HorseStatistics();
        flashStats.addFirstPlace(); // 1
        flashStats.addSecondPlace(); // 1
        flashStats.addFirstPlace(); // 2

        HorseStatistics thunderStats = new HorseStatistics();
        thunderStats.addSecondPlace(); // 1
        thunderStats.addFirstPlace(); // 1
        thunderStats.addThirdPlace(); // 1

        HorseStatistics stormStats = new HorseStatistics();
        stormStats.addThirdPlace(); // 1
        stormStats.addThirdPlace(); // 2
        stormStats.addSecondPlace(); // 1

        Map<String, HorseStatistics> mockStats = new LinkedHashMap<>();
        mockStats.put("Flash", flashStats);
        mockStats.put("Thunder", thunderStats);
        mockStats.put("Storm", stormStats);

        when(service.loadFromFile(path)).thenReturn(mockRaces);
        when(service.findMostSuccessfulHorse(mockRaces)).thenReturn("Flash");
        when(service.findMostFrequentHorse(mockRaces)).thenReturn("Flash");
        when(service.calculateHorseStatistics(mockRaces)).thenReturn(mockStats);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        application.start();

        verify(factory).generateToFile(path, 30);
        verify(service).loadFromFile(path);
        verify(service).findMostSuccessfulHorse(mockRaces);
        verify(service).findMostFrequentHorse(mockRaces);
        verify(service).calculateHorseStatistics(mockRaces);

        String output = outContent.toString();
        assert output.contains("Most successful horse: Flash");
        assert output.contains("Most frequent horse: Flash");
        assert output.contains("Flash: Participation: 3, 1 places: 2, 2 places: 1");
        assert output.contains("Thunder:");
        assert output.contains("Storm:");

        System.setOut(originalOut);
    }

    @Test
    void testStart_whenGenerationFails_thenPrintsErrorAndSkipsProcessing() {
        doThrow(new RuntimeException("generation error")).when(factory).generateToFile(anyString(), anyInt());

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        application.start();

        verify(factory).generateToFile("horseRacing.txt", 30);
        verifyNoInteractions(service);

        String output = outContent.toString();
        assert output.contains("Failed to generate to file");

        System.setOut(originalOut);
    }
}
