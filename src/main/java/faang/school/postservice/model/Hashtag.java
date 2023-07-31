package faang.school.postservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hashtags")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hashtag", nullable = false, length = 140, unique = true)
    private String hashtag;

    @ManyToMany
    @JoinTable(name = "post_hashtag", joinColumns = @JoinColumn(name = "hashtag"), inverseJoinColumns = @JoinColumn(name = "post_id"))
    private List<Post> posts;

    //TODO add migration
    @ManyToMany
    @JoinTable(name = "comment_hashtag", joinColumns = @JoinColumn(name = "comment"), inverseJoinColumns = @JoinColumn(name = "comment_id"))
    private List<Comment> comments;
}
