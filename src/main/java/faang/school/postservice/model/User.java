package faang.school.postservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username")
    @Length(max = 64)
    private String username;

    @Column(name = "password")
    @Length(max = 128)
    private String password;

    @Column(name = "email")
    @Length(max = 64)
    private String email;

    @Column(name = "phone")
    @Length(max = 32)
    private String phone;

    @Column(name = "about_Me")
    @Length(max = 4096)
    private String aboutMe;

    @Column(name = "active")
    private boolean active;

    @Column(name = "city")
    @Length(max = 64)
    private String city;

    @Column(name = "country_id")
    private Long countryId;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "profile_pic_file_id")
    private String profilePicFileId;

    @Column(name = "profile_pic_small_file_id")
    private String profilePicSmallFileId;

    @Column(name = "banned")
    private boolean banned;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(
            name = "subscription",
            joinColumns = {@JoinColumn(name = "followee_id")},
            inverseJoinColumns = {@JoinColumn(name = "follower_id")}
    )
    private List<User> subscribers;
}
