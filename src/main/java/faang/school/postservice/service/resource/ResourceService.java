package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**..
 * Сервис для управления ресурсами (изображениями) постов.
 * Обеспечивает загрузку, получение, удаление и скачивание изображений,
 * прикрепленных к постам, с интеграцией с файловым хранилищем MinIO/S3.
 */
public interface ResourceService {

    /**
     * Загружает изображения к указанному посту.
     * Выполняет валидацию, обработку изображений и сохранение в файловое хранилище.
     *
     * @param postId идентификатор поста, к которому прикрепляются изображения
     * @param files список файлов изображений для загрузки
     * @return список созданных сущностей Resource с метаданными загруженных файлов
     * @throws DataValidationException если количество файлов превышает 10 или размер файла превышает 5 МБ
     * @throws faang.school.postservice.exception.PostNotFoundException если пост с указанным ID не найден
     */
    List<ResourceDto> uploadResources(Long postId, List<MultipartFile> files);

    /**
     * Получает все ресурсы (изображения), прикрепленные к указанному посту.
     *
     * @param postId идентификатор поста
     * @return список сущностей Resource, связанных с постом
     * @throws faang.school.postservice.exception.PostNotFoundException если пост с указанным ID не найден
     */
    List<ResourceDto> getResourcesByPostId(Long postId);

    /**
     * Удаляет конкретный ресурс (изображение) из поста.
     * Удаляет файл из файлового хранилища и сущность Resource из базы данных.
     *
     * @param resourceId идентификатор удаляемого ресурса
     * @throws faang.school.postservice.exception.ResourceNotFoundException если ресурс с указанным ID не найден
     */
    void deleteResource(Long resourceId);

    /**
     * Скачивает конкретный ресурс (изображение) из поста.
     * Возвращает файл в виде массива байтов с соответствующими HTTP-заголовками.
     *
     * @param resourceId идентификатор скачиваемого ресурса
     * @return ResponseEntity с массивом байтов файла и настроенными HTTP-заголовками
     * @throws faang.school.postservice.exception.ResourceNotFoundException если ресурс с указанным ID не найден
     */
    ResponseEntity<byte[]> downloadResource(Long resourceId);
}
