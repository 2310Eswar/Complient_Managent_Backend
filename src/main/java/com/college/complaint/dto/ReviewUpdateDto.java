package com.college.complaint.dto;

public class ReviewUpdateDto {
    private String reviewComment;

    public ReviewUpdateDto() {
    }

    public ReviewUpdateDto(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
}
