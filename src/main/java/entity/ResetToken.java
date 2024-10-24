package entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESET_TOKEN")
public class ResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resetTokenId;

    private String token;

    private LocalDateTime expirationDate;

    @ManyToOne
    private User user;

    public Integer getResetTokenId() {
        return resetTokenId;
    }

    public void setResetTokenId(Integer resetTokenId) {
        this.resetTokenId = resetTokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
