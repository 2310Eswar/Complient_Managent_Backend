package com.college.complaint.dto;

import com.college.complaint.entity.Role;
import com.college.complaint.entity.User;

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String department;
    private String phone;
    private Role requestedRole;
    private String roleRequestReason;
    private String roleRequestStatus;
    private boolean active = true;

    public UserDto() {
    }

    public UserDto(User user) {
        if (user != null) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.role = user.getRole();
            this.department = user.getDepartment();
            this.phone = user.getPhone();
            this.requestedRole = user.getRequestedRole();
            this.roleRequestReason = user.getRoleRequestReason();
            this.roleRequestStatus = user.getRoleRequestStatus();
            this.active = user.isActive();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRequestedRole() {
        return requestedRole;
    }

    public void setRequestedRole(Role requestedRole) {
        this.requestedRole = requestedRole;
    }

    public String getRoleRequestReason() {
        return roleRequestReason;
    }

    public void setRoleRequestReason(String roleRequestReason) {
        this.roleRequestReason = roleRequestReason;
    }

    public String getRoleRequestStatus() {
        return roleRequestStatus;
    }

    public void setRoleRequestStatus(String roleRequestStatus) {
        this.roleRequestStatus = roleRequestStatus;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
