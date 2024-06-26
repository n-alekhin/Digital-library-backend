package com.springproject.core.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangeNotificationPolicyDTO {
    @NotNull
    private Boolean isSendNotification;
}
