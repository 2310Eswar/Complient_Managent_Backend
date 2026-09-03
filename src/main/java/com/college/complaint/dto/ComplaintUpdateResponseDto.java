package com.college.complaint.dto;

import com.college.complaint.entity.ApprovalStatus;
import com.college.complaint.entity.ComplaintUpdate;
import com.college.complaint.entity.Status;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComplaintUpdateResponseDto {
    private Long id;
    private Long complaintId;
    private String complaintTitle;
    private UserDto submittedBy;
    private String updateText;
    private Status proposedStatus;
    private ApprovalStatus approvalStatus;
    private UserDto reviewedBy;
    private String reviewComment;
    private List<AttachmentDto> attachments = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public ComplaintUpdateResponseDto() {
    }

    public ComplaintUpdateResponseDto(ComplaintUpdate update) {
        if (update != null) {
            this.id = update.getId();
            if (update.getComplaint() != null) {
                this.complaintId = update.getComplaint().getId();
                this.complaintTitle = update.getComplaint().getTitle();
            }
            if (update.getSubmittedBy() != null) {
                this.submittedBy = new UserDto(update.getSubmittedBy());
            }
            this.updateText = update.getUpdateText();
            this.proposedStatus = update.getProposedStatus();
            this.approvalStatus = update.getApprovalStatus();
            if (update.getReviewedBy() != null) {
                this.reviewedBy = new UserDto(update.getReviewedBy());
            }
            this.reviewComment = update.getReviewComment();
            if (update.getAttachments() != null) {
                this.attachments = update.getAttachments().stream()
                        .map(AttachmentDto::new)
                        .toList();
            }
            this.createdAt = update.getCreatedAt();
            this.reviewedAt = update.getReviewedAt();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getComplaintTitle() {
        return complaintTitle;
    }

    public void setComplaintTitle(String complaintTitle) {
        this.complaintTitle = complaintTitle;
    }

    public UserDto getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(UserDto submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getUpdateText() {
        return updateText;
    }

    public void setUpdateText(String updateText) {
        this.updateText = updateText;
    }

    public Status getProposedStatus() {
        return proposedStatus;
    }

    public void setProposedStatus(Status proposedStatus) {
        this.proposedStatus = proposedStatus;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public UserDto getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(UserDto reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public List<AttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDto> attachments) {
        this.attachments = attachments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
