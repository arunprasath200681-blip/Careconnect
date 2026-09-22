package com.careconnect.dto;

import com.careconnect.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "Status cannot be null")
    private AppointmentStatus status;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(AppointmentStatus status) {
        this.status = status;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}
