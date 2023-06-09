package com.example.app_tracker.app_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import com.example.app_tracker.app_backend.dto.AppDto;
import com.example.app_tracker.app_backend.entity.App;
import com.example.app_tracker.app_backend.repository.AppRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AppBackendApplication.class)
@TestPropertySource("classpath:test_mysql.properties")
public class AppIntegrationTest {

    @LocalServerPort
    private int port;

    private int getPort() {
        return port;
    }

    private String baseURL = "http://127.0.0.1";

    private static RestTemplate restTemplate;

    @Autowired
    private AppRepository testRepo;

    @BeforeAll
    public static void setUpRestTemplate() {
        restTemplate = new RestTemplate();
    }

    public void setUpAppsInDataBase() {
        testRepo.saveAllAndFlush(List.of(
            new App("United Beverage Security Services", "Java Engineer", "", "New York, NY"),
            new App("Defense Industrial Coffee", "Entry Level Software Developer", "DEV001", "Boston, MA"),
            new App("Cletus and Bubba Manufacturing", "Web Service Backend Developer", "JAVA03", "Denver, CO"),
            new App("Billy Bob's Cybersecurity Emporium", "Tier 1 SOC", "SOC1", "Baltimore, MD"),
            new App("United Beverage Security Services", "Software Engineer Level 1", "SE001", "Alexandria, VA"),
            new App("Grounds Caffeine Research Institute", "Forensic Technician", "TECH00", "Chicago, IL"),
            new App("Medium Roast Threat Intelligence", "Java Developer", "", "Portland, OR"),
            new App("Red Eye", "Java Developer", "DEV_L1", "New York, NY"),
            new App("Java the Hut", "TS/SCI w/Poly Gofer", "TOP_SECRET_GOFER", "Fort Mead, MD")
        ));
    }
    
    @AfterEach
    public void afterTestDataBaseCleanUp() {
        testRepo.truncateTable();
    }
    
    @Test
    public void createApplicationTest() {
        // given
        String testUrl = baseURL + ":" + getPort() + "/api/v1/create";
        AppDto testApp = new AppDto("A Test Company", "A Made Up Position", "TES123", "Testville, TN");
        
        // when
        AppDto returnedDto = restTemplate.postForObject(testUrl, testApp, AppDto.class);

        // then
        assertNotNull(returnedDto);
        Assertions.assertThat(returnedDto.getAppId()).isNotNull();
    }

    @Test
    public void getApplicationByIdTest() {
        // given
        int testId = 1;
        String testUrl = baseURL + ":" + getPort() + "/api/v1/id?appId=" + testId;
        setUpAppsInDataBase();
       
        // when
        AppDto returnedDto = restTemplate.getForObject(testUrl, AppDto.class);
        
        // then
        assertNotNull(returnedDto);
        assertEquals(returnedDto.getAppId(), testId);
        assertEquals(returnedDto.getCompanyName(), testRepo.findById(testId).get().getCompanyName());
    }

    @Test
    public void getAllApplicationsTest() {
        // given
        String testUrl = baseURL + ":" + getPort() + "/api/v1/all";
        setUpAppsInDataBase();

        // when
        List<?> returnedList = (List<?>)restTemplate.getForObject(testUrl, List.class);

        // then
        assertNotNull(returnedList);
        Assertions.assertThat(returnedList).isNotEmpty();
        Assertions.assertThat(returnedList).hasSize(testRepo.findAll().size());
    }

    @Test
    public void getAllApplicationsByKeywordTest() {
        // given
        String testWord = "Java";
        String testUrl = baseURL + ":" + getPort() + "/api/v1/keyword?target=" + testWord;
        setUpAppsInDataBase();
        int testWordCount = testRepo.findAll().stream()
            .filter(a -> a.getPositionName().toLowerCase().contains(testWord.toLowerCase()))
            .mapToInt(i -> 1)
            .reduce((a, b) -> a + b)
            .orElse(0);

        // when
        List<?> returnedList = (List<?>)restTemplate.getForObject(testUrl, List.class);

        //then
        assertNotNull(returnedList);
        Assertions.assertThat(returnedList).isNotEmpty();
        Assertions.assertThat(returnedList).hasSize(testWordCount);
    }

    @Test
    public void getAllApplicationsByCompanyName() {
        // given
        String testName = "United Beverage Security Services";
        String testUrl = baseURL + ":" + getPort() + "/api/v1/company?name=" + testName;
        setUpAppsInDataBase();
        int testNameCount = testRepo.findAll().stream()
            .filter(a -> a.getCompanyName().equalsIgnoreCase(testName))
            .mapToInt(i -> 1)
            .reduce((a, b) -> a + b)
            .orElse(0);

        // when
        List<?> returnedList = (List<?>)restTemplate.getForObject(testUrl, List.class);

        // then
        assertNotNull(returnedList);
        Assertions.assertThat(returnedList).isNotEmpty();
        Assertions.assertThat(returnedList).hasSize(testNameCount);
    }

    @Test
    public void updateApplicationTest() {
        // given
        setUpAppsInDataBase();
        int testId = 1;
        String testUrl = baseURL + ":" + getPort() + "/api/v1/" + testId;
        String getUrl = baseURL + ":" + getPort() + "/api/v1/id?appId=" + testId;
        AppDto updatedDto = restTemplate.getForObject(getUrl, AppDto.class);
        assertNotNull(updatedDto);
        String oldLocation = updatedDto.getLocation();
        updatedDto.setLocation("Toronto, ON");
        String newLocation = updatedDto.getLocation();
        
        // when
        restTemplate.put(testUrl, updatedDto, testId);
        AppDto returneDto = restTemplate.getForObject(getUrl, AppDto.class);

        // then
        assertNotNull(returneDto);
        Assertions.assertThat(oldLocation).isNotEqualTo(returneDto.getLocation());
        assertEquals(newLocation, returneDto.getLocation());
    }

    @Test
    public void deleteApplicationTest() {
        // given
        setUpAppsInDataBase();
        int testId = 1;
        int countStart = testRepo.findAll().size();
        String testUrl = baseURL + ":" + getPort() + "/api/v1/" + testId;

        // then
        restTemplate.delete(testUrl);
        int countEnd = testRepo.findAll().size();

        // when
        assertEquals(countStart - 1, countEnd);
        Assertions.assertThat(testRepo.findById(testId).isEmpty());
    }

}
