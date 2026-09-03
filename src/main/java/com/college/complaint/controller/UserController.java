package com.college.complaint.controller;

import com.college.complaint.dto.RoleRequestDto;
import com.college.complaint.dto.UpdateUserRoleDto;
import com.college.complaint.dto.UserDto;
import com.college.complaint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/request-role")
    public ResponseEntity<UserDto> requestRoleChange(Authentication authentication, @RequestBody RoleRequestDto dto) {
        String email = authentication.getName();
        UserDto updated = userService.requestRoleChange(email, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable Long userId, @RequestBody UpdateUserRoleDto dto) {
        UserDto updated = userService.updateUserRole(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> toggleUserStatus(@PathVariable Long userId) {
        UserDto updated = userService.toggleUserStatus(userId);
        return ResponseEntity.ok(updated);
    }
}
