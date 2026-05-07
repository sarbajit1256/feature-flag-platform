package com.featureflag.system.model;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
/*public class User implements Serializable {
    private static final long serialVersionUID = 1L; */
public class User {

    @Id
    private String id;

    private String country;
    private String device;
}