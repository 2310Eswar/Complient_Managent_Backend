package com.college.complaint.repository;

import com.college.complaint.entity.ApprovalStatus;
import com.college.complaint.entity.ComplaintUpdate;
import com.college.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintUpdateRepository extends JpaRepository<ComplaintUpdate, Long> {
    List<ComplaintUpdate> findByComplaintIdOrderByCreatedAtDesc(Long complaintId);
    List<ComplaintUpdate> findByComplaintIdAndApprovalStatusOrderByCreatedAtDesc(Long complaintId, ApprovalStatus approvalStatus);
    List<ComplaintUpdate> findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus approvalStatus);
    List<ComplaintUpdate> findBySubmittedByOrderByCreatedAtDesc(User submittedBy);
}
