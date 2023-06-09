package com.example.app_tracker.app_backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app_tracker.app_backend.dto.AppDto;
import com.example.app_tracker.app_backend.entity.App;
import com.example.app_tracker.app_backend.exceptions.AppNotFoundException;
import com.example.app_tracker.app_backend.exceptions.RepositoryEmptyException;
import com.example.app_tracker.app_backend.service.AppService;

import lombok.AllArgsConstructor;

import com.example.app_tracker.app_backend.mapper.ApplicationMapper;
import com.example.app_tracker.app_backend.repository.AppRepository;

@Service
@AllArgsConstructor
public class AppServiceImplementation implements AppService {

    private AppRepository repository;

    @Override
    public AppDto createApplication(AppDto dto) {
        return ApplicationMapper.mapToDto(repository.save(ApplicationMapper.mapToApp(dto)));
    }

    @Override
    public AppDto getApplication(int appId) {
        return ApplicationMapper.mapToDto(pokeOptionalAppById(appId));
    }

    @Override
    public List<AppDto> getAllApplications() {
        if (repository.findAll() != null && !repository.findAll().isEmpty()) {
        return repository.findAll().stream()
            .map(a -> ApplicationMapper.mapToDto(a))
            .toList();
        } else {
            throw new RepositoryEmptyException();
        }
    }

    @Override
    public List<AppDto> getAllByCompany(String companyName) {
        if (repository.findAllByCompanyNameIgnoreCase(companyName) != null && !repository.findAllByCompanyNameIgnoreCase(companyName).isEmpty()) {
            return repository.findAllByCompanyNameIgnoreCase(companyName).stream()
                .map(a -> ApplicationMapper.mapToDto(a))
                .toList();
        } else {
            throw new AppNotFoundException("Application", "Company Name", companyName);
        }
    }

    @Override
    public List<AppDto> getAllByPositionKeyWord(String target) {
        if (repository.findByPositionNameContainingIgnoringCase(target) != null && !repository.findByPositionNameContainingIgnoringCase(target).isEmpty()) {
            return repository.findByPositionNameContainingIgnoringCase(target).stream()
                .map(a -> ApplicationMapper.mapToDto(a))
                .toList();
        } else {
            throw new AppNotFoundException("Application", "Position Keyword", target);
        }
    }

    @Override
    public AppDto updateApplication(int appId, AppDto dto) {
        App updatedApp = pokeOptionalAppById(appId);
        updatedApp.setCompanyName(dto.getCompanyName());
        updatedApp.setPositionName(dto.getPositionName());
        updatedApp.setPositionId(dto.getPositionId());
        updatedApp.setLocation(dto.getLocation());
        updatedApp.setRejected(dto.getRejected());
        updatedApp.setFirstInterview(dto.getFirstInterview());
        updatedApp.setSecondInterview(dto.getSecondInterview());
        repository.save(updatedApp);
        return ApplicationMapper.mapToDto(updatedApp);
    }

    @Override
    public void deleteApplication(int appId) {
        if (repository.existsByAppId(appId)) {
            repository.deleteById(appId);
        } else {
            throw new AppNotFoundException("Application", "ID", String.valueOf(appId));
        }
    }

    private App pokeOptionalAppById(int appId) {
        return repository.findById(appId)
            .orElseThrow(() -> new AppNotFoundException("Application", "ID", String.valueOf(appId)));
    }
    
}
