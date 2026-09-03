package com.college.complaint.dto;

import com.college.complaint.entity.Complaint;
import com.college.complaint.entity.Priority;
import com.college.complaint.entity.Status;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComplaintResponseDto {
    private Long id;
    private String title;
    private String description;
    private String categoryName;
    private Long categoryId;
    private Status status;
    private Priority priority;
    private boolean isAnonymous;
    private UserDto createdBy;
    private UserDto assignedTo;
    private UserDto assignedTechnician;
    private UserDto assignedBy;
    private List<AttachmentDto> attachments = new ArrayList<>();
    private List<CommentResponseDto> comments = new ArrayList<>();
    private List<ComplaintUpdateResponseDto> updates = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ComplaintResponseDto() {
    }

    public ComplaintResponseDto(Complaint complaint) {
        if (complaint != null) {
            this.id = complaint.getId();
            this.title = complaint.getTitle();
            this.description = complaint.getDescription();
            if (complaint.getCategory() != null) {
                this.categoryName = complaint.getCategory().getName();
                this.categoryId = complaint.getCategory().getId();
            }
            this.status = complaint.getStatus();
            this.priority = complaint.getPriority();
            this.isAnonymous = complaint.isAnonymous();
            
            if (complaint.isAnonymous()) {
                UserDto anonUser = new UserDto();
                anonUser.setId(0L);
                anonUser.setName("Anonymous Student");
                anonUser.setEmail("anonymous@hidden");
                this.createdBy = anonUser;
            } else if (complaint.getCreatedBy() != null) {
                this.createdBy = new UserDto(complaint.getCreatedBy());
            }

            if (complaint.getAssignedTo() != null) {
                this.assignedTo = new UserDto(complaint.getAssignedTo());
            }

            if (complaint.getAssignedTechnician() != null) {
                this.assignedTechnician = new UserDto(complaint.getAssignedTechnician());
            }

            if (complaint.getAssignedBy() != null) {
                this.assignedBy = new UserDto(complaint.getAssignedBy());
            }

            if (complaint.getAttachments() != null) {
                this.attachments = complaint.getAttachments().stream()
                        .map(AttachmentDto::new)
                        .toList();
            }

            if (complaint.getComments() != null) {
                this.comments = complaint.getComments().stream()
                        .map(CommentResponseDto::new)
                        .toList();
            }

            this.createdAt = complaint.getCreatedAt();
            this.updatedAt = complaint.getUpdatedAt();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public UserDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDto createdBy) {
        this.createdBy = createdBy;
    }

    public UserDto getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserDto assignedTo) {
        this.assignedTo = assignedTo;
    }

    public UserDto getAssignedTechnician() {
        return assignedTechnician;
    }

    public void setAssignedTechnician(UserDto assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }

    public UserDto getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(UserDto assignedBy) {
        this.assignedBy = assignedBy;
    }

    public List<AttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDto> attachments) {
        this.attachments = attachments;
    }

    public List<CommentResponseDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentResponseDto> comments) {
        this.comments = comments;
    }

    public List<ComplaintUpdateResponseDto> getUpdates() {
        return updates;
    }

    public void setUpdates(List<ComplaintUpdateResponseDto> updates) {
        this.updates = updates;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
