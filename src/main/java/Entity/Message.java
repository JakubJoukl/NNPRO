package Entity;

import jakarta.persistence.*;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name="sender_id", nullable=false)
    private User sender;

    @ManyToOne
    @JoinColumn(name="receiver_id", nullable=false)
    private User receiver;
}
