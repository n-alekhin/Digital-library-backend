package com.springproject.core.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    @Id
    private Long userId;
    @OneToOne
    @MapsId
    @JoinColumn
    private User user;
    private String token;
}
