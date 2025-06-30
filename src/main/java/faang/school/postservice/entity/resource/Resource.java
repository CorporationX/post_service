package faang.school.postservice.entity.resource;

<<<<<<<< HEAD:src/main/java/faang/school/postservice/entity/ImageResource.java
import faang.school.postservice.model.comment.CommentImage;
========
import faang.school.postservice.entity.post.Post;
>>>>>>>> remotes/origin/cerberus-master-stream10:src/main/java/faang/school/postservice/entity/resource/Resource.java
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
<<<<<<<< HEAD:src/main/java/faang/school/postservice/entity/ImageResource.java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
========
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
>>>>>>>> remotes/origin/cerberus-master-stream10:src/main/java/faang/school/postservice/entity/resource/Resource.java
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
<<<<<<<< HEAD:src/main/java/faang/school/postservice/entity/ImageResource.java
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "resource")
public class ImageResource {
========
@ToString(exclude = {"post"})
@Entity
@Table(name = "post_resource")
public class Resource {
>>>>>>>> remotes/origin/cerberus-master-stream10:src/main/java/faang/school/postservice/entity/resource/Resource.java

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key", nullable = false, length = 50)
    private String key;

    @Column(name = "size")
    private long size;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "type", length = 50)
    private String type;

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY)
    private CommentImage commentImageOriginal;

    @OneToOne(mappedBy = "preview", fetch = FetchType.LAZY)
    private CommentImage commentImagePreview;
}
