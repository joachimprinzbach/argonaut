package com.baloise.incubator.argonaut.infrastructure.github;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeFileDto {

    private String message;
    private String content;
    private String sha;
}
