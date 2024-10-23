package Entity;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Entity
public class PublicKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer publicKeyId;

    @Column
    private String key;

    @Column
    private LocalDateTime creationDate;

    @Column
    private boolean valid;

    @Column
    private String fingerprint;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
