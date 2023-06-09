package com.example.app_tracker.app_backend.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app_tracker.app_backend.dto.AppDto;
import com.example.app_tracker.app_backend.service.AppService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Tag(name = "REST API controller for Application Tracking Application.",
    description = "Basic web API for creating, retrieving, editing, and deleting job applications.")
@RestController
@RequestMapping(value = "/api/v1")
@AllArgsConstructor
public class AppController {
    
    private AppService service;

    // http://127.0.0.1:8081/api/v1/create
    @Operation(
        summary = "REST API to create a new job application entry.",
        description = "Creates a new application object based on a validated AppDto object.  Required fields are companyName and positionName.  The constructor allows you to pass a null positionId and location.")
    @ApiResponse(
        responseCode = "201",
        description = "HTTP STATUS 201 CREATED")
    @PostMapping("/create")
    public ResponseEntity<AppDto> createApplication(@RequestBody @Valid AppDto dto) {
        return new ResponseEntity<>(service.createApplication(dto), HttpStatus.CREATED);
    }

    // http://127.0.0.1:8081/api/v1/id?appId=1
    @Operation(
        summary = "REST API to get an application's record by its appId.",
        description = "Retrieves a single application entry.")
    @ApiResponse(
        responseCode = "200",
        description = "HTTP STATUS 200 OK")
    @GetMapping("/id")
    public ResponseEntity<AppDto> getApplication(@RequestParam int appId) {
        return new ResponseEntity<>(service.getApplication(appId), HttpStatus.OK);
    }

    // http://127.0.0.1:8081/api/v1/all
    @Operation(
        summary = "REST API to get all applications.",
        description = "Retrieves a list of all applications currently in the repository.")
    @ApiResponse(
        responseCode = "200",
        description = "HTTP STATUS 200 OK")
    @GetMapping("/all")
    public ResponseEntity<List<AppDto>> getAllApplications() {
        return new ResponseEntity<>(service.getAllApplications(), HttpStatus.OK);
    }

    // http://127.0.0.1:8081/api/v1/keyword?target=java
    @Operation(
        summary = "REST API to get applications based on a keyword.",
        description = "Retrieves a list of all applications that include a given keyword in the application's positionName field.")
    @ApiResponse(
        responseCode = "200",
        description = "HTTP STATUS 200 OK")
    @GetMapping("/keyword")
    public ResponseEntity<List<AppDto>> getApplicationByKeyword(@RequestParam String target) {
        return new ResponseEntity<>(service.getAllByPositionKeyWord(target), HttpStatus.OK);
    }

    // http://127.0.0.1:8081/api/v1/company?name=Billy%20Bob%27s%20Data%20Science%20Emporium
    @Operation(
        summary = "REST API to get applications based on company name.",
        description = "Retrieves a list of all applications submitted to a given company.")
    @ApiResponse(
        responseCode = "200",
        description = "HTTP STATUS 200 OK")
    @GetMapping("/company")
    public ResponseEntity<List<AppDto>> getApplicationByCompany(@RequestParam String name) {
        return new ResponseEntity<>(service.getAllByCompany(name), HttpStatus.OK);
    }

    // http://127.0.0.1:8081/api/v1/1
    @Operation(
        summary = "REST API to update an application entry.",
        description = "Update an application based on its appId and passing a validated AppDto object.")
    @ApiResponse(
        responseCode = "202",
        description = "HTTP STATUS 202 ACCEPTED")
    @PutMapping("/{appId}")
    public ResponseEntity<AppDto> updateApplication(@PathVariable int appId, @RequestBody @Valid AppDto dto) {
        return new ResponseEntity<>(service.updateApplication(appId, dto), HttpStatus.ACCEPTED);
    }

    // http://127.0.0.1:8081/api/v1/1
    @Operation(
        summary = "REST API to delete application's record by its appId.",
        description = "Checks to see if the appId is valid, then deletes the record if it is.  Here for the sake of completeness rather than thinking I'll actually use it.")
    @ApiResponse(
        responseCode = "200",
        description = "HTTP STATUS 200 OK")
    @DeleteMapping("/{appId}")
    public ResponseEntity<HttpStatus> deleteApplication(@PathVariable int appId) {
        service.deleteApplication(appId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // API call to automatically load test data to the database
    // http://127.0.0.1:8081/api/v1/addtestdata
    @GetMapping("/addtestdata")
    public ResponseEntity<List<AppDto>> addTestData() {
        List<AppDto> testApps = Arrays.asList(
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
        List<AppDto> returnList = new ArrayList<>();
        for (AppDto newApp : testApps) {
            returnList.add(service.createApplication(newApp));
        }
        return new ResponseEntity<>(returnList, HttpStatus.CREATED);
    }
}
