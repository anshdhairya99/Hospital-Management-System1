package com.hospital.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Patient extends BaseEntity {

    @Column(nullable = false)
    private String fullName;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phone;
    private String address;

    @Column(length = 5)
    private String bloodGroup;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private AppUser user;
}
