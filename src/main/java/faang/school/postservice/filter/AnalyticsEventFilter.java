package faang.school.postservice.filter;

import faang.school.postservice.dto.analytic.AnalyticsEventFilterDto;
import faang.school.postservice.model.analytic.AnalyticsEvent;

import java.util.stream.Stream;

/**
 * Интерфейс для фильтрации событий
 */
public interface AnalyticsEventFilter {

    /**
     * Должно ли событие быть отфильтровано
     * @param filter DTO фильтра
     * @return true, если фильтр применим, иначе false
     */
    boolean isApplicable(AnalyticsEventFilterDto filter);

    /**
     * Применял фильтр к Stream
     * @param stream Поток данных
     * @param filter DTO фильтра
     * @return {@link Stream<AnalyticsEvent>} отфильтрованные события
     */
    Stream<AnalyticsEvent> apply(Stream<AnalyticsEvent> stream, AnalyticsEventFilterDto filter);
}
