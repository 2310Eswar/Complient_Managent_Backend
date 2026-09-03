package com.college.complaint.dto;

import com.college.complaint.entity.ComplaintComment;
import com.college.complaint.entity.Status;
import java.time.LocalDateTime;

public class CommentResponseDto {
    private Long id;
    private UserDto commentedBy;
    private String message;
    private Status statusChange;
    private LocalDateTime timestamp;

    public CommentResponseDto() {
    }

    public CommentResponseDto(ComplaintComment comment) {
        if (comment != null) {
            this.id = comment.getId();
            this.commentedBy = new UserDto(comment.getCommentedBy());
            this.message = comment.getMessage();
            this.statusChange = comment.getStatusChange();
            this.timestamp = comment.getTimestamp();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(UserDto commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatusChange() {
        return statusChange;
    }

    public void setStatusChange(Status statusChange) {
        this.statusChange = statusChange;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
