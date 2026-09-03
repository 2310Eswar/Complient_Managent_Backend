package com.college.complaint.service;

import com.college.complaint.dto.*;
import com.college.complaint.entity.*;
import com.college.complaint.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComplaintUpdateService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ComplaintUpdateRepository updateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintCommentRepository commentRepository;

    @Autowired
    private StorageService storageService;

    @Transactional
    public ComplaintResponseDto assignTechnician(Long complaintId, Long technicianId, String adminEmail) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found: " + complaintId));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician not found: " + technicianId));

        if (technician.getRole() != Role.TECHNICIAN && technician.getRole() != Role.STAFF && technician.getRole() != Role.ADMIN) {
            throw new RuntimeException("Target user is not a Technician or Staff member");
        }

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        complaint.setAssignedTechnician(technician);
        complaint.setAssignedBy(admin);
        
        if (complaint.getStatus() == Status.PENDING) {
            complaint.setStatus(Status.ASSIGNED);
        }

        Complaint saved = complaintRepository.save(complaint);

        ComplaintComment comment = new ComplaintComment(
                saved,
                admin,
                "Assigned to Field Technician: " + technician.getName() + " (" + technician.getDepartment() + ")",
                saved.getStatus()
        );
        commentRepository.save(comment);

        return new ComplaintResponseDto(saved);
    }

    public List<ComplaintResponseDto> getTechnicianComplaints(String technicianEmail) {
        User technician = userRepository.findByEmail(technicianEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Complaint> complaints = complaintRepository.findByAssignedTechnicianOrderByCreatedAtDesc(technician);
        return complaints.stream().map(ComplaintResponseDto::new).toList();
    }

    @Transactional
    public ComplaintUpdateResponseDto submitUpdate(Long complaintId, SubmitUpdateDto dto, List<MultipartFile> files, String technicianEmail) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found: " + complaintId));

        User technician = userRepository.findByEmail(technicianEmail)
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        ComplaintUpdate update = new ComplaintUpdate(
                complaint,
                technician,
                dto.getUpdateText(),
                dto.getProposedStatus() != null ? dto.getProposedStatus() : Status.RESOLVED
        );

        // Store update photo attachments if provided
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String storedFileName = storageService.storeFile(file);
                    String fileUrl = storageService.getFileUrl(storedFileName);

                    Attachment attachment = new Attachment(
                            complaint,
                            storedFileName,
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getSize(),
                            fileUrl
                    );
                    update.getAttachments().add(attachment);
                }
            }
        }

        ComplaintUpdate savedUpdate = updateRepository.save(update);

        // Transition complaint status to PENDING_APPROVAL
        complaint.setStatus(Status.PENDING_APPROVAL);
        complaintRepository.save(complaint);

        // Trace comment
        ComplaintComment comment = new ComplaintComment(
                complaint,
                technician,
                "Technician submitted update: \"" + dto.getUpdateText() + "\" (Awaiting Admin/Staff Approval)",
                Status.PENDING_APPROVAL
        );
        commentRepository.save(comment);

        return new ComplaintUpdateResponseDto(savedUpdate);
    }

    public List<ComplaintUpdateResponseDto> getUpdatesByComplaint(Long complaintId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ComplaintUpdate> updates;
        if (user.getRole() == Role.STUDENT) {
            // Students ONLY see APPROVED updates
            updates = updateRepository.findByComplaintIdAndApprovalStatusOrderByCreatedAtDesc(complaintId, ApprovalStatus.APPROVED);
        } else {
            // Staff, Admin, Technician see all updates
            updates = updateRepository.findByComplaintIdOrderByCreatedAtDesc(complaintId);
        }

        return updates.stream().map(ComplaintUpdateResponseDto::new).toList();
    }

    public List<ComplaintUpdateResponseDto> getPendingApprovals() {
        List<ComplaintUpdate> pendingUpdates = updateRepository.findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus.PENDING);
        return pendingUpdates.stream().map(ComplaintUpdateResponseDto::new).toList();
    }

    @Transactional
    public ComplaintUpdateResponseDto approveUpdate(Long updateId, ReviewUpdateDto dto, String adminEmail) {
        ComplaintUpdate update = updateRepository.findById(updateId)
                .orElseThrow(() -> new RuntimeException("Complaint update not found: " + updateId));

        User reviewer = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        update.setApprovalStatus(ApprovalStatus.APPROVED);
        update.setReviewedBy(reviewer);
        update.setReviewedAt(LocalDateTime.now());
        if (dto != null && dto.getReviewComment() != null && !dto.getReviewComment().isBlank()) {
            update.setReviewComment(dto.getReviewComment());
        }

        ComplaintUpdate savedUpdate = updateRepository.save(update);

        // Update parent Complaint status to proposed status
        Complaint complaint = update.getComplaint();
        complaint.setStatus(update.getProposedStatus());
        complaintRepository.save(complaint);

        // Trace comment
        String remark = (dto != null && dto.getReviewComment() != null && !dto.getReviewComment().isBlank())
                ? " (" + dto.getReviewComment() + ")"
                : "";
        ComplaintComment comment = new ComplaintComment(
                complaint,
                reviewer,
                "Approved technician update." + remark + " Ticket status updated to: " + update.getProposedStatus(),
                update.getProposedStatus()
        );
        commentRepository.save(comment);

        return new ComplaintUpdateResponseDto(savedUpdate);
    }

    @Transactional
    public ComplaintUpdateResponseDto rejectUpdate(Long updateId, ReviewUpdateDto dto, String adminEmail) {
        ComplaintUpdate update = updateRepository.findById(updateId)
                .orElseThrow(() -> new RuntimeException("Complaint update not found: " + updateId));

        User reviewer = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        update.setApprovalStatus(ApprovalStatus.REJECTED);
        update.setReviewedBy(reviewer);
        update.setReviewedAt(LocalDateTime.now());
        String rejectionNote = (dto != null && dto.getReviewComment() != null && !dto.getReviewComment().isBlank())
                ? dto.getReviewComment()
                : "Update rejected by Admin/Staff. Please inspect and resubmit.";
        update.setReviewComment(rejectionNote);

        ComplaintUpdate savedUpdate = updateRepository.save(update);

        // Reset parent Complaint status back to IN_PROGRESS so technician can rework & resubmit
        Complaint complaint = update.getComplaint();
        complaint.setStatus(Status.IN_PROGRESS);
        complaintRepository.save(complaint);

        // Trace comment
        ComplaintComment comment = new ComplaintComment(
                complaint,
                reviewer,
                "Rejected technician update. Reason: \"" + rejectionNote + "\". Ticket status reset to IN_PROGRESS.",
                Status.IN_PROGRESS
        );
        commentRepository.save(comment);

        return new ComplaintUpdateResponseDto(savedUpdate);
    }
}
