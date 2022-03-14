package com.outsource.bookingticket.entities.airplane;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "airplane")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Airplane implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airplane_id")
    private Integer airplaneId;

    @Column(name = "airplane_name")
    private String airplaneName;

    private Float capacity;
}
