package faang.school.postservice.controller;

import faang.school.postservice.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/currency")
    public Map<String, Object> getAllCurrency() {
        currencyService.updateCurrencyRates();
        return currencyService.getCurrencyRates();
    }
}
