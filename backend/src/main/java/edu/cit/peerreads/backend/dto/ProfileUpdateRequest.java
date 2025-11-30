package edu.cit.peerreads.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @NotBlank
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;
    
    @Size(max = 1024, message = "Bio must not exceed 1024 characters")
    private String bio;
    
    @Size(max = 16777215, message = "Profile picture URL is too large")
    private String profilePictureUrl;
}

