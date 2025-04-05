package faang.school.postservice.model.ad;

import faang.school.postservice.model.Identifiable;
import faang.school.postservice.model.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_ad")
public class Ad extends Identifiable {

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @JoinColumn(name = "buyer_id", nullable = false)
    private long buyerId;

    @Column(name = "appearances_left", nullable = false)
    private long appearancesLeft;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime endDate;
}
