package com.example.app_tracker.app_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// import org.junit.Test;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.example.app_tracker.app_backend.entity.App;
import com.example.app_tracker.app_backend.repository.AppRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource("classpath:test_h2.properties")
public class AppRepositoryUnitTest {
    
    @Autowired
	private AppRepository testRepo;

    @BeforeEach
    public void setUp() {
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

    @Test
    public void testSave() {
        // given
        App newApp = new App("Chumly's DevOps Clearing House", "Data Modeller", "DA500", "Syracuse, NY");
        App checkApp = new App();
        
        // when
        testRepo.save(newApp);
        Optional<App> optionalApp = testRepo.findById(10);
        if (optionalApp.isPresent()) {
            checkApp = optionalApp.get();
        }

        // then
        assertEquals(newApp.getCompanyName(), checkApp.getCompanyName());
    }

    @Test
    public void findAllTest() {
        //given
        
        // when
        List<App> aList = testRepo.findAll();

        // then
        Assertions.assertThat(aList).isNotNull();
        Assertions.assertThat(aList).hasSize(9);
    }


    @Test
    public void findAllByCompanyNameIgnoreCaseAssertNotNull() {
        // given
        String testCompany = "United Beverage Security Services";
        
        // when
        List<App> testList = testRepo.findAllByCompanyNameIgnoreCase(testCompany);
        
        // then
        assertEquals(2, testList.size());
        
    }

    @Test
    public void findAllByCompanyNameIgnoreCaseAssertNull() {
        // given
        String testCompany = "Wally's Wacky Webapps";
       
        // when
        List<App> testList = testRepo.findAllByCompanyNameIgnoreCase(testCompany);
        
        // then
        assertEquals(0, testList.size());
    }

    @Test
    public void existsByAppIdTestTrue() {
        // given
        int testId = 1;

        // when
        boolean idExists = testRepo.existsByAppId(testId);

        // then
        Assertions.assertThat(idExists).isTrue();
    }

    @Test
    public void existsByAppIdTestFalse() {
        // given
        int testId = 25;

        // when
        boolean idExists = testRepo.existsByAppId(testId);

        // then
        Assertions.assertThat(idExists).isFalse();
    }

}
