package com.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_roles", schema = "auth_data")
@Getter @Setter
public class UserRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "description")
    private String description;

    public UserRoleEntity() {}

    public UserRoleEntity(String displayName, String roleName) {
        this.displayName = displayName;
        this.roleName = roleName;
    }

}
