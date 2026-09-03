package com.college.complaint.dto;

import com.college.complaint.entity.Status;
import jakarta.validation.constraints.NotBlank;

public class SubmitUpdateDto {

    @NotBlank(message = "Update text/description is required")
    private String updateText;

    private Status proposedStatus = Status.RESOLVED;

    public SubmitUpdateDto() {
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
}
