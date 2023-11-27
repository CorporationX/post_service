package faang.school.postservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Builder
@Getter
@Setter
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false, length = 4096)
    private String content;

    @Column(name = "author_id", nullable = false)
    private long authorId;

    @OneToMany(mappedBy = "comment", orphanRemoval = true)
    private List<Like> likes;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "verified_date")
    private LocalDateTime verifiedDate;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", authorId=" + authorId +
                ", likes=" + likes +
                ", post=" + post +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", verified=" + verified +
                ", verifiedDate=" + verifiedDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (id != comment.id) return false;
        if (authorId != comment.authorId) return false;
        if (verified != comment.verified) return false;
        if (!Objects.equals(content, comment.content)) return false;
        if (!Objects.equals(likes, comment.likes)) return false;
        if (!Objects.equals(post, comment.post)) return false;
        if (!Objects.equals(createdAt, comment.createdAt)) return false;
        if (!Objects.equals(updatedAt, comment.updatedAt)) return false;
        return Objects.equals(verifiedDate, comment.verifiedDate);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (int) (authorId ^ (authorId >>> 32));
        result = 31 * result + (likes != null ? likes.hashCode() : 0);
        result = 31 * result + (post != null ? post.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (verified ? 1 : 0);
        result = 31 * result + (verifiedDate != null ? verifiedDate.hashCode() : 0);
        return result;
    }
}
