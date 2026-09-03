package com.college.complaint.dto;

import jakarta.validation.constraints.NotNull;

public class AssignStaffDto {

    @NotNull(message = "Staff ID is required")
    private Long staffId;

    public AssignStaffDto() {
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
}
