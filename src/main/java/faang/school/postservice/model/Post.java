package faang.school.postservice.model;

import faang.school.postservice.model.ad.Ad;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    @Column(name = "views")
    private long views;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Like> likes;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments;

    @ManyToMany(mappedBy = "posts")
    private List<Album> albums;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    private Ad ad;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "corrected", nullable = false)
    private boolean corrected;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "verified_date")
    private LocalDateTime verifiedDate;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        if (id != post.id) return false;
        if (published != post.published) return false;
        if (corrected != post.corrected) return false;
        if (deleted != post.deleted) return false;
        if (verified != post.verified) return false;
        if (!Objects.equals(content, post.content)) return false;
        if (!Objects.equals(authorId, post.authorId)) return false;
        if (!Objects.equals(projectId, post.projectId)) return false;
        if (!Objects.equals(views, post.views)) return false;
        if (!Objects.equals(likes, post.likes)) return false;
        if (!Objects.equals(comments, post.comments)) return false;
        if (!Objects.equals(albums, post.albums)) return false;
        if (!Objects.equals(ad, post.ad)) return false;
        if (!Objects.equals(publishedAt, post.publishedAt)) return false;
        if (!Objects.equals(scheduledAt, post.scheduledAt)) return false;
        if (!Objects.equals(createdAt, post.createdAt)) return false;
        if (!Objects.equals(updatedAt, post.updatedAt)) return false;
        return Objects.equals(verifiedDate, post.verifiedDate);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (authorId != null ? authorId.hashCode() : 0);
        result = 31 * result + (projectId != null ? projectId.hashCode() : 0);
        result = 31 * result + (likes != null ? likes.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + (albums != null ? albums.hashCode() : 0);
        result = 31 * result + (ad != null ? ad.hashCode() : 0);
        result = 31 * result + (published ? 1 : 0);
        result = 31 * result + (corrected ? 1 : 0);
        result = 31 * result + (publishedAt != null ? publishedAt.hashCode() : 0);
        result = 31 * result + (scheduledAt != null ? scheduledAt.hashCode() : 0);
        result = 31 * result + (deleted ? 1 : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (verifiedDate != null ? verifiedDate.hashCode() : 0);
        result = 31 * result + (verified ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", authorId=" + authorId +
                ", projectId=" + projectId +
                ", views=" + views +
                ", likes=" + likes +
                ", comments=" + comments +
                ", albums=" + albums +
                ", ad=" + ad +
                ", published=" + published +
                ", corrected=" + corrected +
                ", publishedAt=" + publishedAt +
                ", scheduledAt=" + scheduledAt +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", verifiedDate=" + verifiedDate +
                ", verified=" + verified +
                '}';
    }
}
