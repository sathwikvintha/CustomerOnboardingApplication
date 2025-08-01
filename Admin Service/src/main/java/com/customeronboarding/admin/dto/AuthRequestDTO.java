package com.customeronboarding.admin.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthRequestDTO {
    private String username;
    private String password;
}
