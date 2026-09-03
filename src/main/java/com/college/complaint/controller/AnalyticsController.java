package com.college.complaint.controller;

import com.college.complaint.dto.AnalyticsSummaryDto;
import com.college.complaint.dto.UserDto;
import com.college.complaint.entity.Role;
import com.college.complaint.repository.UserRepository;
import com.college.complaint.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/analytics/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<AnalyticsSummaryDto> getAnalyticsSummary() {
        return ResponseEntity.ok(complaintService.getAnalyticsSummary());
    }

    @GetMapping("/users/staff")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<List<UserDto>> getStaffUsers() {
        // Return ONLY users with role = TECHNICIAN
        List<UserDto> technicianList = userRepository.findByRole(Role.TECHNICIAN).stream()
                .map(UserDto::new)
                .toList();

        return ResponseEntity.ok(technicianList);
    }
}
