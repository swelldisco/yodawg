package com.example.app_tracker.app_backend.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppDto {

    @Schema(description = "Application ID number.")
    private int appId;

    @NonNull
    @Schema(description = "Company name.")
    @NotBlank(message = "Company name cannot be left blank.")
    @NotEmpty(message = "Company name cannot be left empty.")
    @Size(max = 50, message = "Company name must be 50 characters or less.")
    private String companyName;

    @NonNull
    @Schema(description = "Postion name or description.")
    @NotBlank(message = "Position cannot be left blank.")
    @NotEmpty(message = "Position name cannot be left empty.")
    @Size(max = 250, message = "Position name must be 250 characters or less.")
    private String positionName;

    @Schema(description = "Position ID if present, to help differentiate between two openings at the same company with the same name.")
    @Size(max = 20, message = "Position ID must be 20 characters or less.")
    private String positionId;

    @Schema(description = "Physical location for in person and hybrid positions.")
    @Size(max = 35, message = "Location must be 35 characters or less.")
    private String location;

    @Schema(description = "Date and time the application was submitted.")
    @PastOrPresent
    @JsonFormat(pattern="MM-dd-yyyy HH:mm:ss")
    private LocalDateTime applicationDate;

    @Schema(description = "Whether or not a rejection for this application has been received.")
    private boolean rejected;

    @Schema(description = "Was a first interview offered.")
    private boolean firstInterview;

    @Schema(description = "Was a second interview offered.")
    private boolean secondInterview;

    // constructor to set the application date, and set rejection and interview status to false
    public AppDto(String companyName, String positionName, String positionId, String location) {
        this.companyName = companyName;
        this.positionName = positionName;
        this.positionId = positionId;
        this.location = location;
        this.applicationDate = LocalDateTime.now();
        this.rejected = false;
        this.firstInterview = false;
        this.secondInterview = false;
    }

    public boolean getRejected() {
        return rejected;
    }

    public boolean getFirstInterview() {
        return firstInterview;
    }

    public boolean getSecondInterview() {
        return secondInterview;
    }

    public void setRejected(boolean rejection) {
        this.rejected = rejection;
    }

    public void setFirstInterview(boolean bool) {
        this.firstInterview = bool;
    }

    public void setSecondInterview(boolean bool) {
        this.secondInterview = bool;
    }

    private String readOut(boolean rejected) {
        if (rejected) {
            return "has been rejected.";
        } else {
            return "has not yet been rejected.";
        }
    }

    @Override
    public String toString() {
        return "Application with " + getCompanyName() + " for the position " + getPositionName() + " " + readOut(rejected);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
        result = prime * result + ((positionName == null) ? 0 : positionName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AppDto other = (AppDto) obj;
        if (companyName == null) {
            if (other.companyName != null)
                return false;
        } else if (!companyName.equals(other.companyName))
            return false;
        if (positionName == null) {
            if (other.positionName != null)
                return false;
        } else if (!positionName.equals(other.positionName))
            return false;
        return true;
    }

}
