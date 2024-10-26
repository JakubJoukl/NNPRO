package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TESTICEK")
public class Testicek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer testicekId;

    @Column
    private String pokus;
}
