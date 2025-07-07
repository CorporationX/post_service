package faang.school.postservice.dto.kafkaevents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class FeedHeatEvent {
    private Long id;

}
