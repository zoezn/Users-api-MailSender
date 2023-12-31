package com.ecran.users.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
//@ToString(exclude = {"watchlist"})
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = -2731425678149216053L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable=false, unique=true)
    private String userId;

    @Column(nullable=false, length=50)
    private String firstName;

    @Column(nullable=false, length=50)
    private String lastName;

    @Column(nullable=false, length=120, unique=true)
    private String email;

    @Column(nullable=false, unique=true)
    private String encryptedPassword;

    @Column
    private Boolean enabled;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    @JoinColumn(name="user_id")
//    private List<UsersMovieWatchlist> watchlist = new ArrayList<>();
}
