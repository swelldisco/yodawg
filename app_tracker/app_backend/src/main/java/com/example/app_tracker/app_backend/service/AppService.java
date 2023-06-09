package com.example.app_tracker.app_backend.service;

import java.util.List;

import com.example.app_tracker.app_backend.dto.AppDto;

public interface AppService {
    
    AppDto createApplication(AppDto dto);
    AppDto getApplication(int appId);
    List<AppDto> getAllApplications();
    List<AppDto> getAllByCompany(String companyName);
    List<AppDto> getAllByPositionKeyWord(String target);
    AppDto updateApplication(int appId, AppDto dto);
    void deleteApplication(int appId);
    
}
