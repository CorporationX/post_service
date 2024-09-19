package faang.school.postservice.redisdemo.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "article")
@Convert(attributeName = "jsonb", converter = JsonBinaryType.class)  // Добавляем определение типа jsonb
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 64)
    private String title;

    @Column(name = "text", nullable = false, length = 128)
    private String text;

    @Column(name = "rating")
    private double rating;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "hash_tags", columnDefinition = "jsonb")  // Указываем тип колонки как jsonb
    private List<String> hashTags;
//    @Column(name = "hash_tag")  // Указываем тип колонки как jsonb
//    private String hashTags;
}