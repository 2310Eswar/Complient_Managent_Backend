package com.college.complaint.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentDto {

    @NotBlank(message = "Comment message cannot be blank")
    private String message;

    public CommentDto() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
