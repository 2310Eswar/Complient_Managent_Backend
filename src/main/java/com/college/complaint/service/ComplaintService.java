package com.college.complaint.service;

import com.college.complaint.dto.*;
import com.college.complaint.entity.*;
import com.college.complaint.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ComplaintCategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintCommentRepository commentRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private StorageService storageService;

    @Transactional
    public ComplaintResponseDto createComplaint(CreateComplaintDto dto, List<MultipartFile> files, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        ComplaintCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + dto.getCategoryId()));

        Complaint complaint = new Complaint();
        complaint.setTitle(dto.getTitle());
        complaint.setDescription(dto.getDescription());
        complaint.setCategory(category);
        complaint.setPriority(dto.getPriority() != null ? dto.getPriority() : Priority.MEDIUM);
        complaint.setStatus(Status.PENDING);
        complaint.setAnonymous(dto.isAnonymous());
        complaint.setCreatedBy(user);

        Complaint savedComplaint = complaintRepository.save(complaint);

        // Process attachments if present
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String storedFileName = storageService.storeFile(file);
                    String fileUrl = storageService.getFileUrl(storedFileName);

                    Attachment attachment = new Attachment(
                            savedComplaint,
                            storedFileName,
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getSize(),
                            fileUrl
                    );
                    attachmentRepository.save(attachment);
                    savedComplaint.getAttachments().add(attachment);
                }
            }
        }

        // Add initial system trace comment
        ComplaintComment initialComment = new ComplaintComment(
                savedComplaint,
                user,
                "Complaint filed under category: " + category.getName() + " with priority: " + complaint.getPriority(),
                Status.PENDING
        );
        commentRepository.save(initialComment);
        savedComplaint.getComments().add(initialComment);

        return new ComplaintResponseDto(savedComplaint);
    }

    public List<ComplaintResponseDto> getMyComplaints(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Complaint> complaints;
        if (user.getRole() == Role.STAFF || user.getRole() == Role.TECHNICIAN) {
            complaints = complaintRepository.findByAssignedToOrderByCreatedAtDesc(user);
        } else {
            complaints = complaintRepository.findByCreatedByOrderByCreatedAtDesc(user);
        }

        return complaints.stream().map(ComplaintResponseDto::new).toList();
    }

    public List<ComplaintResponseDto> getAllComplaints(Status status, Long categoryId, Priority priority) {
        List<Complaint> complaints = complaintRepository.findAll();

        return complaints.stream()
                .filter(c -> status == null || c.getStatus() == status)
                .filter(c -> categoryId == null || (c.getCategory() != null && c.getCategory().getId().equals(categoryId)))
                .filter(c -> priority == null || c.getPriority() == priority)
                .sorted(Comparator.comparing(Complaint::getCreatedAt).reversed())
                .map(ComplaintResponseDto::new)
                .toList();
    }

    public ComplaintResponseDto getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with ID: " + id));
        return new ComplaintResponseDto(complaint);
    }

    @Transactional
    public ComplaintResponseDto updateStatus(Long id, UpdateStatusDto dto, String currentUserEmail) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        User actor = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Status oldStatus = complaint.getStatus();
        complaint.setStatus(dto.getStatus());
        Complaint updated = complaintRepository.save(complaint);

        String message = (dto.getComment() != null && !dto.getComment().isBlank())
                ? dto.getComment()
                : "Status changed from " + oldStatus + " to " + dto.getStatus();

        ComplaintComment comment = new ComplaintComment(updated, actor, message, dto.getStatus());
        commentRepository.save(comment);

        return new ComplaintResponseDto(updated);
    }

    @Transactional
    public ComplaintResponseDto assignStaff(Long complaintId, Long staffId, String currentUserEmail) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff member not found with ID: " + staffId));

        if (staff.getRole() != Role.STAFF && staff.getRole() != Role.ADMIN && staff.getRole() != Role.TECHNICIAN) {
            throw new RuntimeException("Target user is not a Staff, Admin, or Technician");
        }

        User actor = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        complaint.setAssignedTo(staff);
        if (complaint.getStatus() == Status.PENDING) {
            complaint.setStatus(Status.IN_PROGRESS);
        }

        Complaint saved = complaintRepository.save(complaint);

        ComplaintComment comment = new ComplaintComment(
                saved,
                actor,
                "Complaint assigned to " + staff.getName() + " (" + staff.getDepartment() + ")",
                saved.getStatus()
        );
        commentRepository.save(comment);

        return new ComplaintResponseDto(saved);
    }

    @Transactional
    public CommentResponseDto addComment(Long complaintId, String message, String currentUserEmail) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        User actor = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ComplaintComment comment = new ComplaintComment(complaint, actor, message, null);
        ComplaintComment saved = commentRepository.save(comment);

        return new CommentResponseDto(saved);
    }

    @Transactional
    public List<AttachmentDto> addAttachments(Long complaintId, List<MultipartFile> files) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        List<Attachment> addedList = new ArrayList<>();
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
                addedList.add(attachmentRepository.save(attachment));
            }
        }

        return addedList.stream().map(AttachmentDto::new).toList();
    }

    public AnalyticsSummaryDto getAnalyticsSummary() {
        long total = complaintRepository.count();
        long pending = complaintRepository.countByStatus(Status.PENDING);
        long inProgress = complaintRepository.countByStatus(Status.IN_PROGRESS);
        long resolved = complaintRepository.countByStatus(Status.RESOLVED);
        long closed = complaintRepository.countByStatus(Status.CLOSED);
        long rejected = complaintRepository.countByStatus(Status.REJECTED);

        List<Object[]> categoryCountsRaw = complaintRepository.countComplaintsByCategory();
        Map<String, Long> byCategory = new HashMap<>();
        for (Object[] row : categoryCountsRaw) {
            String catName = (String) row[0];
            Long cnt = (Long) row[1];
            byCategory.put(catName, cnt);
        }

        return new AnalyticsSummaryDto(total, pending, inProgress, resolved, closed, rejected, byCategory);
    }
}
