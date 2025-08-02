package faang.school.postservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "faang.school.postservice.client")
public class PostServiceApp {

    /* todo:
     *  Публикация эвента поста в Kafka (https://faang-school.atlassian.net/browse/BJS2-77343)
     *  Задание:
     *      - Когда пользователь создает пост, то после его сохранения в БД нужно отправить соответствующий
     *      эвент в новый Kafka-топик: posts
     *      - Для этого создать конфигурацию KafkaTemplate и данного топика, если их еще нет. Подключить
     *      зависимость Spring + Kafka, а также добавить соответствующие Docker контейнеры в docker compose
     *      в репозитории infra. ВАЖНО: сделать ветку для вашей команды в этом репозитории, если ее еще нет
     *      - Далее создать KafkaPostProducer, который будет публиковать JSON эвента о создании поста в данный
     *      топик после того, как пост был успешно сохранен в БД
     *      - В эвенте должен содержаться список id всех подписчиков автора данного поста.
     *      Его, разумеется, получить из БД, написав соответствующий запрос
     */

    /* todo
     *  Слушатель Kafka-эвентов для постов (https://faang-school.atlassian.net/browse/BJS2-77340)
     *  Задание
     *      - Создать слушателя событий KafkaPostConsumer, который слушает Kafka-топик posts, получая соответствующие
     *      эвенты. Использовать аннотацию @KafkaListener
     *      - Данный потребитель должен принимать событие и для всех id подписчиков, которые в нем содержатся,
     *      добавлять/изменять соответствующие записи в коллекции Feed в Redis
     *      - Для работы с Redis можно использовать этот туториал:
     *      [Introduction to Spring Data Redis | Baeldung|https://www.baeldung.com/spring-data-redis-tutorial]
     *      - Feed представляет собой ассоциацию: ключ - id подписчика, значение - набор из 500 (max) постов в его фиде.
     *      Подумать, какую коллекцию следует использовать для хранения набора этих постов.
     *      Требования: посты не должны дублироваться, посты должны располагаться в хронологическом
     *      порядке (новые - сверху)
     *      - Если фид пользователя уже заполнен (500), то удалять пост в конце фида, а новый добавлять сверху
     *      - Убедиться в корректной обработке конкурентных эвентов. Два поста в один и тот же фид могут
     *      прилететь одновременно. В Redis нет транзакций.
     *      - Убедиться, что максимальный размер кэша фида можно менять через конфиг
     *      - Делать ack в Kafka на обработку эвента поста только после того, как все подписчики успешно
     *      обработаны в рамках данного эвента
     *      - Подумать, почему при падении сервера и повторной обработке того же самого эвента посты не будут
     *      задублированы в фиде пользователей, для которых они были добавлены в фид при первой неудачной
     *      обработке события. Как структура данных в Redis решает эту проблему?
     */

    public static void main(String[] args) {
        new SpringApplicationBuilder(PostServiceApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
