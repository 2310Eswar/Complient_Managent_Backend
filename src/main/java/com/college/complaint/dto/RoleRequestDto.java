package com.college.complaint.dto;

import com.college.complaint.entity.Role;

public class RoleRequestDto {
    private Role requestedRole;
    private String reason;

    public RoleRequestDto() {}

    public Role getRequestedRole() {
        return requestedRole;
    }

    public void setRequestedRole(Role requestedRole) {
        this.requestedRole = requestedRole;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
