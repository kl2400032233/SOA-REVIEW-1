package com.klu.springmvc.dto;

import com.klu.springmvc.model.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;

public class AvailabilityUpdateRequest {

    @NotNull(message = "Availability status is required (AVAILABLE or UNAVAILABLE)")
    private AvailabilityStatus availabilityStatus;

    public AvailabilityUpdateRequest() {
    }

    public AvailabilityUpdateRequest(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}
