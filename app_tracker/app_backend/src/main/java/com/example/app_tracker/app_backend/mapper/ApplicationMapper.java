package com.example.app_tracker.app_backend.mapper;

import com.example.app_tracker.app_backend.dto.AppDto;
import com.example.app_tracker.app_backend.entity.App;

public class ApplicationMapper {
    
    public static App mapToApp(AppDto source) {
        return new App(
            source.getAppId(), 
            source.getCompanyName(),
            source.getPositionName(),
            source.getPositionId(),
            source.getLocation(),
            source.getApplicationDate(),
            source.getRejected(),
            source.getFirstInterview(),
            source.getSecondInterview()
            );
    }

    public static AppDto mapToDto(App source) {
        return new AppDto(
            source.getAppId(), 
            source.getCompanyName(),
            source.getPositionName(),
            source.getPositionId(),
            source.getLocation(),
            source.getApplicationDate(),
            source.getRejected(),
            source.getFirstInterview(),
            source.getSecondInterview()
        );
    }
    
}
