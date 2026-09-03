package com.college.complaint.dto;

import com.college.complaint.entity.Attachment;
import java.time.LocalDateTime;

public class AttachmentDto {
    private Long id;
    private String fileName;
    private String originalName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
    private LocalDateTime uploadedAt;

    public AttachmentDto() {
    }

    public AttachmentDto(Attachment attachment) {
        if (attachment != null) {
            this.id = attachment.getId();
            this.fileName = attachment.getFileName();
            this.originalName = attachment.getOriginalName();
            this.fileType = attachment.getFileType();
            this.fileSize = attachment.getFileSize();
            this.fileUrl = attachment.getFileUrl();
            this.uploadedAt = attachment.getUploadedAt();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
