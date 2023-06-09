package com.example.app_tracker.app_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.app_tracker.app_backend.entity.App;

import jakarta.transaction.Transactional;

public interface AppRepository extends JpaRepository<App, Integer> {
    List<App> findAllByCompanyNameIgnoreCase(String companyName);

    // figure out why this isn't working later for my own entertainment
    // @Query("SELECT a FROM applications a WHERE UPPER(a.positionName) LIKE CONCAT('%',:target,'%')")
    // List<App> findByPositionKeyword(@Param("target") String target);

    List<App> findByPositionNameContainingIgnoringCase(String target);

    boolean existsByAppId(int appId);

    // This needs to be commented out or deleted when not testing, as it's dangerous to have lying around
    // the reason it's needed for testing is due to the fact that you cannot do integration testing on find by ID when by just deleting the entries after every test.  The database will keep incrementing IDs unless it's truncated.
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE applications", nativeQuery = true)
    void truncateTable();

}
