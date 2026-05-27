package com.itesm.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ChangeUserPasswordDto {

    @NotBlank(message = "newPassword is required")
    @Size(min = 8, message = "newPassword must have at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "newPassword must contain uppercase, lowercase and number"
    )
    private String newPassword;

    private Boolean revokeSessions = true;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Boolean getRevokeSessions() {
        return revokeSessions;
    }

    public void setRevokeSessions(Boolean revokeSessions) {
        this.revokeSessions = revokeSessions;
    }
}