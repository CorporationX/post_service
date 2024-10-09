package faang.school.postservice.exception.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {

    /**
     * Код статуса HTTP-ответа
     */
    private String status;

    /**
     *Общее описание причины ошибки
     */
    private String reason;

    /**
     *Сообщение об ошибке
     */
    private String message;

    /**
     *Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
     */
    private String timestamp;
}
