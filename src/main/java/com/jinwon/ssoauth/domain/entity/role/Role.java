package com.jinwon.ssoauth.domain.entity.role;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jinwon.ssoauth.domain.entity.profile.Profile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 권한 Entity
 */
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "profile_role_type_unique_001",
                columnNames = {"profile_id", "roleType"}
        )
})
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, foreignKey = @ForeignKey(name = "role_profile_foreign_key_001"))
    private Profile profile;

    @Column(nullable = false)
    private String roleType;

    public Role(String roleType) {
        this.roleType = roleType;
    }

    public void grant(Profile profile) {
        this.profile = profile;
    }

}


