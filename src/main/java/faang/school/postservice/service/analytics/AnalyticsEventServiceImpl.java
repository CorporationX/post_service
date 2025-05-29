package faang.school.postservice.service.analytics;

import faang.school.postservice.dto.analytic.AnalyticEventDto;
import faang.school.postservice.dto.analytic.AnalyticsEventCreateDto;
import faang.school.postservice.dto.analytic.AnalyticsEventFilterDto;
import faang.school.postservice.filter.AnalyticsEventFilter;
import faang.school.postservice.mapper.AnalyticsEventMapper;
import faang.school.postservice.model.analytic.AnalyticsEvent;
import faang.school.postservice.repository.AnalyticsEventRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;


/**
 * Сервис для работы с лайками.
 * <p>
 * Основные методы:
 * <ul>
 *   <li>{@link #saveEvent(AnalyticsEventCreateDto)} )} - Сохраняет аналитику</li>
 *   <li>{@link #getAnalytics(AnalyticsEventFilterDto)} - Получает аналитику по заданным параметрам</li>
 * </ul>
 *
 * @author takewqa
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventServiceImpl implements AnalyticsEventService {
    private final AnalyticsEventRepository analyticsEventRepository;
    private final AnalyticsEventMapper analyticsEventMapper;
    private final List<AnalyticsEventFilter> analyticsEventFilters;

    /**
     * Сохраняет аналитику
     *
     * @param analyticsEventCreateDto Сущность для создания аналитики
     */
    @Override
    @Transactional
    public void saveEvent(@NotNull AnalyticsEventCreateDto analyticsEventCreateDto) {
        analyticsEventRepository.save(analyticsEventMapper.toEntity(analyticsEventCreateDto));
        log.debug("Event saved {}", analyticsEventCreateDto);
    }

    /**
     * Получение аналитики по заданным параметрам
     *
     * @param filter Фильтр для поиска
     * @return {@link List<AnalyticEventDto>} Отфильтрованный список с аналитикой
     */
    @Override
    @Transactional(readOnly = true)
    public List<AnalyticEventDto> getAnalytics(@NotNull AnalyticsEventFilterDto filter) {
        Stream<AnalyticsEvent> eventsStream =
                analyticsEventRepository.findByAuthorIdAndReceiverIdOrderByCreatedAtDesc(filter.authorId(), filter.receiverId());

        for (AnalyticsEventFilter eventFilter : analyticsEventFilters) {
            if (eventFilter.isApplicable(filter)) {
                eventsStream = eventFilter.apply(eventsStream, filter);
            }
        }

        return eventsStream.map(analyticsEventMapper::toAnalyticEventDto).toList();
    }
}
