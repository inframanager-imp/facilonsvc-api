package com.facilon.app.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "password_reset_token") // Use snake_case for table name
@Getter @Setter @NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "password_reset_token_id")
    private Long id;

    @Column(name = "token", nullable = false) // Explicitly naming the column
    private String token;

    @OneToOne(targetEntity = AuthorizedUser.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "authorized_user_id", nullable = false) // Use snake_case for column names
    private AuthorizedUser authorizedUser;

    @Column(name = "expiry_date", nullable = false) // Explicitly naming the column
    private Date expiryDate;

    public PasswordResetToken(String token, AuthorizedUser authorizedUser, Date expiryDate) {
        this.token = token;
        this.authorizedUser = authorizedUser;
        this.expiryDate = expiryDate;
    }
    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
// No need for explicit constructors, getters, and setters due to Lombok
}
