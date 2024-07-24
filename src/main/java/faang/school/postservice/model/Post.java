package faang.school.postservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import faang.school.postservice.model.ad.Ad;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@AllArgsConstructor
@Builder
@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "content", nullable = false, length = 4096)
    private String content;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "project_id")
    private Long projectId;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "post_hashtag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    @JsonIdentityReference(alwaysAsId = true)
    private List<Hashtag> hashtags = new ArrayList<>();

    @ManyToMany(mappedBy = "posts")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Album> albums = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonIdentityReference(alwaysAsId = true)
    private Ad ad;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Resource> resources = new ArrayList<>();

    @Column(name = "published", nullable = false)
    private boolean published;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
