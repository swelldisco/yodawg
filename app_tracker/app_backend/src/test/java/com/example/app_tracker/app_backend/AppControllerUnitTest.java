package com.example.app_tracker.app_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.app_tracker.app_backend.dto.AppDto;
import com.example.app_tracker.app_backend.service.AppService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import jakarta.servlet.ServletContext;

@WebMvcTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AppControllerUnitTest {
    
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private AppService testService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void mockContext() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .build();
    }

    List<AppDto> testAppList = Arrays.asList(
        new AppDto("United Beverage Security Services", "Java Engineer", "", "New York, NY"),
        new AppDto("Defense Industrial Coffee", "Entry Level Software Developer", "DEV001", "Boston, MA"),
        new AppDto("Cletus and Bubba Manufacturing", "Web Service Backend Developer", "JAVA03", "Denver, CO"),
        new AppDto("Billy Bob's Cybersecurity Emporium", "Tier 1 SOC", "SOC1", "Baltimore, MD"),
        new AppDto("United Beverage Security Services", "Software Engineer Level 1", "SE001", "Alexandria, VA"),
        new AppDto("Grounds Caffeine Research Institute", "Forensic Technician", "TECH00", "Chicago, IL"),
        new AppDto("Medium Roast Threat Intelligence", "Java Developer", "", "Portland, OR"),
        new AppDto("Red Eye", "Java Developer", "DEV_L1", "New York, NY"),
        new AppDto("Java the Hut", "TS/SCI w/Poly Gofer", "TOP_SECRET_GOFER", "Fort Mead, MD")
    );
    
    @Test
	public void contextLoads() {
		assertNotNull(mockMvc);
		ServletContext servletContext = context.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(context.getBean("appController"));
	}

    @Test
    public void testCreateApplication() throws Exception {
		// given
        LocalDateTime testTime = LocalDateTime.now();
        AppDto testApp = new AppDto(1, "A Test Company", "A Made Up Position", "TES123", "Testville, TN", testTime, false, false, false);
        
        // when
        // so, I think this needs to be stubbed to work?
        when(testService.createApplication(any(AppDto.class))).thenReturn(testApp);
        
        RequestBuilder rq = MockMvcRequestBuilders.post("/api/v1/create")
            .content(objectMapper.writeValueAsString(testApp))
            .contentType(MediaType.APPLICATION_JSON_VALUE);
        
        // then
        mockMvc.perform(rq)
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.companyName").value(testApp.getCompanyName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.positionName").value(testApp.getPositionName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.positionId").value(testApp.getPositionId()))
            .andReturn();
    }

    @Test
    public void testGetApplication() throws Exception {
        // given (testApps)
        int testAppId = 1;
        String testURL = "/api/v1/id?appId=" + testAppId;
        String testCompanyName = testAppList.get(testAppId).getCompanyName();
        String testPositionName = testAppList.get(testAppId).getPositionName();

        // when
        when(testService.getApplication(testAppId)).thenReturn(testAppList.get(testAppId));

        // then
        this.mockMvc.perform(MockMvcRequestBuilders.get(testURL))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.companyName").value(testCompanyName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.positionName").value(testPositionName))
            .andReturn();
    }

    @Test
    public void testGetAllApplications() throws Exception {
        // given (testApps) 

        //when
        when(testService.getAllApplications()).thenReturn(testAppList);

        // then
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/all"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(testAppList.size()));
    }

    @Test
    public void testGetAllApplicationsByKeyword() throws Exception {
        // given
        String testWord = "Java";
        String testURL = "/api/v1/keyword?target=" + testWord;
        int testWordCount = testAppList.stream()
            .filter(a -> a.getPositionName().toLowerCase().contains(testWord.toLowerCase()))
            .mapToInt(i -> 1)
            .reduce((a, b) -> a + b)
            .orElse(0);
        List<AppDto> newList = testAppList.stream()
            .filter(a -> a.getPositionName().toLowerCase().contains(testWord.toLowerCase()))
            .toList();

        // when
        when(testService.getAllByPositionKeyWord(testWord)).thenReturn(newList);

        // then
        this.mockMvc.perform(MockMvcRequestBuilders.get(testURL))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(newList.size()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(testWordCount));
    }

    @Test
    public void testGetAllApplicationsByCompanyName() throws Exception {
        // given
        int testId = 1;
        String testName = testAppList.get(testId).getCompanyName();
        String testURL = "/api/v1/company?name=" + testName;
        int testNameCount = testAppList.stream()
            .filter(a -> a.getCompanyName().equalsIgnoreCase(testName))
            .mapToInt(i -> 1)
            .reduce((a, b) -> a + b)
            .orElse(0);
        List<AppDto> newList = testAppList.stream()
            .filter(a -> a.getCompanyName().equalsIgnoreCase(testName))
            .toList();

        // when
        when(testService.getAllByCompany(testName)).thenReturn(newList);

        // then
        this.mockMvc.perform(MockMvcRequestBuilders.get(testURL))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(newList.size()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(testNameCount));
    }

    @Test
    public void testUpdateApplication() throws Exception {
        // given
        AppDto updatedDto = new AppDto(0, "United Beverage Security Services", "Java Engineer", "", "Salt Lake City, UT", LocalDateTime.now(), true, false, false);
        int testId = updatedDto.getAppId();
        String testURL = "/api/v1/" + testId;
        String oldLocation = testAppList.get(testId).getLocation();
        String oldCompanyName = testAppList.get(testId).getCompanyName();

        // when
        when(testService.updateApplication(testId, updatedDto)).thenReturn(updatedDto);
        RequestBuilder rq = MockMvcRequestBuilders.put(testURL)
            .content(objectMapper.writeValueAsString(updatedDto))
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        // then
        MvcResult mvcResult = mockMvc.perform(rq)
            .andExpect(MockMvcResultMatchers.status().isAccepted())
            .andExpect(MockMvcResultMatchers.jsonPath("$.location").value(updatedDto.getLocation()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.companyName").value(oldCompanyName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.companyName").value(updatedDto.getCompanyName()))
            .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String newLocation = JsonPath.parse(response).read("$.location");
        assertNotEquals(oldLocation, newLocation);
        assertEquals(updatedDto.getLocation(), newLocation);
    }

    @Test
    public void testDeleteApplication() throws Exception {
        // given
        int testId = 1;
        String testURL = "/api/v1/" + testId;

        // when
        doNothing().when(testService).deleteApplication(testId);

        // then
        this.mockMvc.perform(MockMvcRequestBuilders.delete(testURL))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
