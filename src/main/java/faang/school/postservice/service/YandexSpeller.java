package faang.school.postservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class YandexSpeller {
    private final String url;
    private RestTemplate restTemplate = new RestTemplate();


    public void check(String text) {
        String newText = restTemplate.getForObject(url, String.class, text);
        System.out.println(newText);
    }


}
