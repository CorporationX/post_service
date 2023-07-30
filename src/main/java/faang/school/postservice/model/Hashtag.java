package faang.school.postservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "hashtag", nullable = false, length = 140)
    private String hashtag;

    @ManyToMany
    @JoinTable(name = "post_hashtag", joinColumns = @JoinColumn(name = "hashtag"), inverseJoinColumns = @JoinColumn(name = "post_id"))
    private List<Post> posts;
}
