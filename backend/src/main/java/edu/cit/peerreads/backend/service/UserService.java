package edu.cit.peerreads.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.peerreads.backend.dto.ProfileUpdateRequest;
import edu.cit.peerreads.backend.dto.UserDto;
import edu.cit.peerreads.backend.entity.Role;
import edu.cit.peerreads.backend.entity.User;
import edu.cit.peerreads.backend.exception.BadRequestException;
import edu.cit.peerreads.backend.exception.ResourceNotFoundException;
import edu.cit.peerreads.backend.repository.UserRepository;
import edu.cit.peerreads.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BadRequestException("No authenticated user found");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserDto toDto(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toDto(user);
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .location(user.getLocation())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .joinedDate(user.getJoinedDate())
                .build();
    }

    @Transactional
    public UserDto update(ProfileUpdateRequest request) {
        User user = getCurrentUser();

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        // Sanitize and truncate fields to prevent data truncation errors
        String fullName = request.getFullName() != null 
            ? request.getFullName().trim() 
            : "";
        if (fullName.length() > 255) {
            fullName = fullName.substring(0, 255);
        }

        String email = request.getEmail() != null 
            ? request.getEmail().trim().toLowerCase() 
            : "";
        if (email.length() > 255) {
            email = email.substring(0, 255);
        }

        String location = request.getLocation() != null 
            ? request.getLocation().trim() 
            : null;
        if (location != null && location.length() > 255) {
            location = location.substring(0, 255);
        }

        String bio = request.getBio() != null 
            ? request.getBio().trim() 
            : null;
        if (bio != null && bio.length() > 1024) {
            bio = bio.substring(0, 1024);
        }

        String profilePictureUrl = request.getProfilePictureUrl() != null 
            ? request.getProfilePictureUrl().trim() 
            : null;
        // MEDIUMTEXT can hold up to 16MB, but we'll limit to 10MB for safety
        if (profilePictureUrl != null && profilePictureUrl.length() > 10485760) {
            throw new BadRequestException("Profile picture URL is too large. Please use a smaller image.");
        }

        user.setFullName(fullName);
        user.setEmail(email);
        user.setLocation(location);
        user.setBio(bio);
        user.setProfilePictureUrl(profilePictureUrl);

        userRepository.save(user);
        return toDto(user);
    }

    public boolean isCurrentUserAdmin() {
        User currentUser = getCurrentUser();
        return currentUser.getRole() == Role.ADMIN;
    }

    public boolean hasAnyAdmin() {
        return userRepository.existsByRole(Role.ADMIN);
    }

    @Transactional
    public UserDto promoteToAdmin(String emailOrUsername) {
        // Find user by email or username
        User userToPromote = userRepository.findByEmailIgnoreCase(emailOrUsername)
                .or(() -> userRepository.findByUsernameIgnoreCase(emailOrUsername))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email or username: " + emailOrUsername));

        // Check if user is already admin
        if (userToPromote.getRole() == Role.ADMIN) {
            throw new BadRequestException("User is already an administrator");
        }

        // Promote to admin
        userToPromote.setRole(Role.ADMIN);
        userRepository.save(userToPromote);

        return toDto(userToPromote);
    }
}

