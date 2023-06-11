package com.example.app_tracker.app_backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.app_tracker.app_backend.dto.AppDto;
import com.example.app_tracker.app_backend.entity.App;
import com.example.app_tracker.app_backend.exceptions.AppNotFoundException;
import com.example.app_tracker.app_backend.exceptions.RepositoryEmptyException;
import com.example.app_tracker.app_backend.mapper.ApplicationMapper;
import com.example.app_tracker.app_backend.repository.AppRepository;
import com.example.app_tracker.app_backend.service.implementation.AppServiceImplementation;

// @DataJpaTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AppServiceUnitTest {
    
    @Mock(name = "database")
    private AppRepository testRepo;

    @InjectMocks
    private AppServiceImplementation testService;

    List<App> testAppList = Arrays.asList(
        new App("United Beverage Security Services", "Java Engineer", "", "New York, NY"),
        new App("Defense Industrial Coffee", "Entry Level Software Developer", "DEV001", "Boston, MA"),
        new App("Cletus and Bubba Manufacturing", "Web Service Backend Developer", "JAVA03", "Denver, CO"),
        new App("Billy Bob's Cybersecurity Emporium", "Tier 1 SOC", "SOC1", "Baltimore, MD"),
        new App("United Beverage Security Services", "Software Engineer Level 1", "SE001", "Alexandria, VA"),
        new App("Grounds Caffeine Research Institute", "Forensic Technician", "TECH00", "Chicago, IL"),
        new App("Medium Roast Threat Intelligence", "Java Developer", "", "Portland, OR"),
        new App("Red Eye", "Java Developer", "DEV_L1", "New York, NY"),
        new App("Java the Hut", "TS/SCI w/Poly Gofer", "TOP_SECRET_GOFER", "Fort Mead, MD")
        );

    @Test
    public void testCreateApp() {
        // given
        AppDto newApp = new AppDto(0, "United Beverage Security Services", "Java Engineer", "", "New York, NY", LocalDateTime.now(), false, false, false);
        App app = ApplicationMapper.mapToApp(newApp);
        List<App> emptyList = Arrays.asList(app);
        int testId = newApp.getAppId();

        // when
        when(testRepo.save(app)).thenReturn(app);
        when(testRepo.findAll()).thenReturn(emptyList);
        when(testRepo.findById(testId)).thenReturn(Optional.of(emptyList.get(testId)));
        testService.createApplication(newApp);
        AppDto checkApp = testService.getApplication(testId);

        // then
        Assertions.assertThat(testService.getAllApplications()).isNotEmpty();
        assertEquals(1, testService.getAllApplications().size());
        assertEquals(newApp.getCompanyName(), checkApp.getCompanyName());
        verify(testRepo, times(1)).save(app);
    }

    @Test
    public void testFindAppByAppId() {
        // given
        int testAppId = 1;
        String companyName = testAppList.get(testAppId).getCompanyName();
        String positionName = testAppList.get(testAppId).getPositionName();
        
        // when
        when(testRepo.findById(testAppId)).thenReturn(Optional.of(testAppList.get(testAppId)));
        AppDto myAppDto = testService.getApplication(testAppId);

        // then
        assertEquals(companyName, myAppDto.getCompanyName());
        assertEquals(positionName, myAppDto.getPositionName());
    }

    @Test
    public void testFindAppByAppIdNotEqual() {
        // given
        int testAppId = 4;
        int anotherId = 1;
        String companyName = testAppList.get(anotherId).getCompanyName();
        String positionName = testAppList.get(anotherId).getPositionName();

        // when
        when(testRepo.findById(testAppId)).thenReturn(Optional.of(testAppList.get(testAppId)));
        AppDto myAppDto = testService.getApplication(testAppId);

        // then
        assertNotEquals(companyName, myAppDto.getCompanyName());
        assertNotEquals(positionName, myAppDto.getPositionName());
    }

    @Test
    public void testGetAllApplications() {
        // given
        int listSize = testAppList.size();
        
        // when
        when(testRepo.findAll()).thenReturn(testAppList);
        List<AppDto> testThisList = testService.getAllApplications();
        
        // then
        Assertions.assertThat(testThisList).isNotNull();
        Assertions.assertThat(testThisList).isNotEmpty();
        Assertions.assertThat(testThisList).hasSize(listSize);
    }

    @Test
    public void testGetAllApplicationsNull() {
        // given
        List<App> emptyList = new ArrayList<>();
        
        // when
        when(testRepo.findAll()).thenReturn(emptyList);

        // then
        assertThrowsExactly(RepositoryEmptyException.class, () -> testService.getAllApplications());
    }

    @Test
    public void testGetAllByCompany() {
        // given
        int testId = 1;
        String testName = testAppList.get(testId).getCompanyName();
        int testNameCount = testAppList.stream()
            .filter(a -> a.getCompanyName().equalsIgnoreCase(testName))
            .mapToInt(i -> 1)
            .reduce((a, b) -> a + b)
            .orElse(0);
        List<App> newList = testAppList.stream()
            .filter(a -> a.getCompanyName().equalsIgnoreCase(testName))
            .toList();
        
        // when
        when(testRepo.findAllByCompanyNameIgnoreCase(testName)).thenReturn(newList);

        // then
        assertEquals(testNameCount,testService.getAllByCompany(testName).size());

    }

    @Test
    public void testGetAllByCompanyNull() {
        // given
        String testName = "Wally's Wacky Web Apps";
        int testNameCount = testAppList.stream()
            .filter(a -> a.getCompanyName().equalsIgnoreCase(testName))
            .mapToInt(i -> 1)
            .reduce((a, b) -> a + b)
            .orElse(0);
        List<App> newList = testAppList.stream()
            .filter(a -> a.getCompanyName().equalsIgnoreCase(testName))
            .toList();
        
        // when
        when(testRepo.findAllByCompanyNameIgnoreCase(testName)).thenReturn(newList);

        // then
        assertThrowsExactly(AppNotFoundException.class, () -> testService.getAllByCompany(testName));
        assertEquals(testNameCount, newList.size());

    }

    @Test
    public void testGetByKeyword() {
        // given
        String testWord = "Java";
        int testWordCount = testAppList.stream()
            .filter(a -> a.getPositionName().toLowerCase().contains(testWord.toLowerCase()))
            .mapToInt(i -> 1)
            .reduce((a, b) -> a + b)
            .orElse(0);
        List<App> newList = testAppList.stream()
            .filter(a -> a.getPositionName().toLowerCase().contains(testWord.toLowerCase()))
            .toList();

        // when
        when(testRepo.findByPositionNameContainingIgnoringCase(testWord)).thenReturn(newList);

        //then
        assertEquals(testWordCount, testService.getAllByPositionKeyWord(testWord).size());
    }

    @Test
    public void testGetByKeywordNull() {
        // given
        String testWord = "Python";
        int testWordCount = testAppList.stream()
            .filter(a -> a.getPositionName().toLowerCase().contains(testWord.toLowerCase()))
            .mapToInt(i -> 1)
            .reduce((a, b) -> a + b)
            .orElse(0);
        List<App> newList = testAppList.stream()
            .filter(a -> a.getPositionName().toLowerCase().contains(testWord.toLowerCase()))
            .toList();

        // when
        // this throws an Unnecessary Stubbing Exception, but I want to load to empty list just because
        when(testRepo.findByPositionNameContainingIgnoringCase(testWord)).thenReturn(newList);
        List<App> checkList = testRepo.findByPositionNameContainingIgnoringCase(testWord);

        //then
        assertThrowsExactly(AppNotFoundException.class, () -> testService.getAllByCompany(testWord));
        assertEquals(testWordCount, checkList.size());
    }

    @Test
    public void testUpdateApp() {
        // given
        AppDto updatedDto = new AppDto(0, "United Beverage Security Services", "Java Engineer", "", "Salt Lake City, UT", LocalDateTime.now(), true, false, false);
        int testId = updatedDto.getAppId();
        App updatedApp = ApplicationMapper.mapToApp(updatedDto);
        String oldLocation = testAppList.get(testId).getLocation();

        // when
        when(testRepo.save(updatedApp)).thenReturn(testAppList.set(testId, updatedApp));
        when(testRepo.findById(testId)).thenReturn(Optional.of(testAppList.get(testId)));
        testService.updateApplication(testId, updatedDto);

        // then
        assertEquals(true, testService.getApplication(testId).getRejected());
        assertNotEquals(oldLocation, testService.getApplication(testId).getLocation());
    }

    @Test
    public void testDeleteApp() {
        // given
        int testId = 4;
        boolean exists = testId <= (testAppList.size() - 1) && testId >= 0;

        // when
        when(testRepo.existsByAppId(testId)).thenReturn(exists);
        doNothing().when(testRepo).deleteById(testId);
        testService.deleteApplication(testId);

        // then
        verify(testRepo, times(1)).deleteById(testId);
    }

    @Test
    public void testDeleteAppNull() {
        // given
        int testId = testAppList.size() + 5;
        boolean exists = testId <= (testAppList.size() - 1) && testId >= 0;

        // when
        when(testRepo.existsByAppId(testId)).thenReturn(exists);
        doNothing().when(testRepo).deleteById(testId);

        // then
        assertThrowsExactly(AppNotFoundException.class, () -> testService.deleteApplication(testId));
        verify(testRepo, times(0)).deleteById(testId);
    }

}
