package com.college.complaint.controller;

import com.college.complaint.dto.*;
import com.college.complaint.entity.Status;
import com.college.complaint.service.ComplaintUpdateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ComplaintUpdateController {

    @Autowired
    private ComplaintUpdateService updateService;

    @PostMapping("/complaints/{id}/assign-technician")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ComplaintResponseDto> assignTechnician(
            @PathVariable Long id,
            @Valid @RequestBody AssignTechnicianDto dto,
            Authentication authentication) {
        return ResponseEntity.ok(updateService.assignTechnician(id, dto.getTechnicianId(), authentication.getName()));
    }

    @GetMapping("/technician/complaints")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'STAFF')")
    public ResponseEntity<List<ComplaintResponseDto>> getTechnicianComplaints(Authentication authentication) {
        return ResponseEntity.ok(updateService.getTechnicianComplaints(authentication.getName()));
    }

    @PostMapping(value = "/complaints/{id}/updates", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'STAFF', 'ADMIN')")
    public ResponseEntity<ComplaintUpdateResponseDto> submitUpdate(
            @PathVariable Long id,
            @RequestParam("updateText") String updateText,
            @RequestParam(value = "proposedStatus", defaultValue = "RESOLVED") Status proposedStatus,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication) {

        SubmitUpdateDto dto = new SubmitUpdateDto();
        dto.setUpdateText(updateText);
        dto.setProposedStatus(proposedStatus);

        return ResponseEntity.ok(updateService.submitUpdate(id, dto, files, authentication.getName()));
    }

    @GetMapping("/complaints/{id}/updates")
    public ResponseEntity<List<ComplaintUpdateResponseDto>> getUpdatesByComplaint(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(updateService.getUpdatesByComplaint(id, authentication.getName()));
    }

    @GetMapping("/admin/pending-approvals")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ComplaintUpdateResponseDto>> getPendingApprovals() {
        return ResponseEntity.ok(updateService.getPendingApprovals());
    }

    @PutMapping("/updates/{updateId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ComplaintUpdateResponseDto> approveUpdate(
            @PathVariable Long updateId,
            @RequestBody(required = false) ReviewUpdateDto dto,
            Authentication authentication) {
        return ResponseEntity.ok(updateService.approveUpdate(updateId, dto, authentication.getName()));
    }

    @PutMapping("/updates/{updateId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ComplaintUpdateResponseDto> rejectUpdate(
            @PathVariable Long updateId,
            @RequestBody(required = false) ReviewUpdateDto dto,
            Authentication authentication) {
        return ResponseEntity.ok(updateService.rejectUpdate(updateId, dto, authentication.getName()));
    }
}
