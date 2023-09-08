package com.LetsPlay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegRequest {
    private String name;
    private String email;
    private String password;
}
