package com.baloise.incubator.argonaut.infrastructure.github;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class CreateCommentDto {
    private String body;
}