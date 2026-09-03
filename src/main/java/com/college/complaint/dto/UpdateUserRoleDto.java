package com.college.complaint.dto;

import com.college.complaint.entity.Role;

public class UpdateUserRoleDto {
    private Role newRole;
    private String requestAction; // APPROVE, REJECT, DIRECT_CHANGE

    public UpdateUserRoleDto() {}

    public Role getNewRole() {
        return newRole;
    }

    public void setNewRole(Role newRole) {
        this.newRole = newRole;
    }

    public String getRequestAction() {
        return requestAction;
    }

    public void setRequestAction(String requestAction) {
        this.requestAction = requestAction;
    }
}
