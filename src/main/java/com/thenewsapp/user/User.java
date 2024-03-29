package com.thenewsapp.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thenewsapp.news.News;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(
            name = "uuid",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @ManyToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
    @JsonIgnore
    Set<News> news;

    public User(User anotherUser){
        this.id = anotherUser.getId();
        this.email = anotherUser.getEmail();
        this.password = anotherUser.getPassword();
        this.firstName = anotherUser.getFirstName();
        this.lastName = anotherUser.getLastName();
        this.username = anotherUser.getUsername();
    }
}