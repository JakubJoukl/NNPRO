package Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resetTokenId;

    private String token;

    private LocalDateTime expirationDate;

    @ManyToOne
    private User user;
}
