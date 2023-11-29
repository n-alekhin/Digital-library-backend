package com.springproject.core.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String refreshToken;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  User user;
}
