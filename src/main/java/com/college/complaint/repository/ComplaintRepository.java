package com.college.complaint.repository;

import com.college.complaint.entity.Complaint;
import com.college.complaint.entity.Status;
import com.college.complaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long>, JpaSpecificationExecutor<Complaint> {
    
    List<Complaint> findByCreatedByOrderByCreatedAtDesc(User createdBy);

    List<Complaint> findByAssignedToOrderByCreatedAtDesc(User assignedTo);

    List<Complaint> findByAssignedTechnicianOrderByCreatedAtDesc(User assignedTechnician);

    List<Complaint> findByStatus(Status status);

    long countByStatus(Status status);

    @Query("SELECT c.category.name, COUNT(c) FROM Complaint c GROUP BY c.category.name")
    List<Object[]> countComplaintsByCategory();
}
