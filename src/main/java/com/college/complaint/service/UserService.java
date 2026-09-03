package com.college.complaint.service;

import com.college.complaint.dto.RoleRequestDto;
import com.college.complaint.dto.UpdateUserRoleDto;
import com.college.complaint.dto.UserDto;
import com.college.complaint.entity.User;
import com.college.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserDto requestRoleChange(String userEmail, RoleRequestDto requestDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        user.setRequestedRole(requestDto.getRequestedRole());
        user.setRoleRequestReason(requestDto.getReason());
        user.setRoleRequestStatus("PENDING");

        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAllByOrderByIdDesc()
                .stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUserRole(Long userId, UpdateUserRoleDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if ("APPROVE".equalsIgnoreCase(dto.getRequestAction())) {
            if (user.getRequestedRole() != null) {
                user.setRole(user.getRequestedRole());
            } else if (dto.getNewRole() != null) {
                user.setRole(dto.getNewRole());
            }
            user.setRoleRequestStatus("APPROVED");
        } else if ("REJECT".equalsIgnoreCase(dto.getRequestAction())) {
            user.setRoleRequestStatus("REJECTED");
        } else {
            // DIRECT CHANGE BY ADMIN
            if (dto.getNewRole() != null) {
                user.setRole(dto.getNewRole());
                user.setRoleRequestStatus("APPROVED");
            }
        }

        User updatedUser = userRepository.save(user);
        return new UserDto(updatedUser);
    }

    @Transactional
    public UserDto toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if ("eswarrawsr2006@gmail.com".equalsIgnoreCase(user.getEmail())) {
            throw new RuntimeException("Primary Administrator account cannot be deactivated.");
        }

        user.setActive(!user.isActive());
        User updatedUser = userRepository.save(user);
        return new UserDto(updatedUser);
    }
}
