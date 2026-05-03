package com.gabriel.task_manager.Infra.Security;

import lombok.Builder;

@Builder
public record JWTUserData(
        Long id, String name
) {
}
