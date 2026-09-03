package com.college.complaint.controller;

import com.college.complaint.dto.*;
import com.college.complaint.entity.Priority;
import com.college.complaint.entity.Status;
import com.college.complaint.service.ComplaintService;
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
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ComplaintResponseDto> createComplaint(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "priority", defaultValue = "MEDIUM") Priority priority,
            @RequestParam(value = "isAnonymous", defaultValue = "false") boolean isAnonymous,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication) {

        CreateComplaintDto dto = new CreateComplaintDto();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setCategoryId(categoryId);
        dto.setPriority(priority);
        dto.setAnonymous(isAnonymous);

        return ResponseEntity.ok(complaintService.createComplaint(dto, files, authentication.getName()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ComplaintResponseDto>> getMyComplaints(Authentication authentication) {
        return ResponseEntity.ok(complaintService.getMyComplaints(authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<List<ComplaintResponseDto>> getAllComplaints(
            @RequestParam(value = "status", required = false) Status status,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "priority", required = false) Priority priority) {
        return ResponseEntity.ok(complaintService.getAllComplaints(status, categoryId, priority));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponseDto> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<ComplaintResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusDto dto,
            Authentication authentication) {
        return ResponseEntity.ok(complaintService.updateStatus(id, dto, authentication.getName()));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplaintResponseDto> assignStaff(
            @PathVariable Long id,
            @Valid @RequestBody AssignStaffDto dto,
            Authentication authentication) {
        return ResponseEntity.ok(complaintService.assignStaff(id, dto.getStaffId(), authentication.getName()));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentDto dto,
            Authentication authentication) {
        return ResponseEntity.ok(complaintService.addComment(id, dto.getMessage(), authentication.getName()));
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentDto>> addAttachments(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(complaintService.addAttachments(id, files));
    }
}
