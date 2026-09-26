package com.careconnect.dto;

import com.careconnect.entity.AppointmentStatus;

public class StatusUpdateRequest {

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
