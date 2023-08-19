package faang.school.postservice.corrector.external_service;

public enum Language {
    EN_US("en-US"),
    EN_GB("en-GB"),
    EN_ZA("en-ZA"),
    EN_AU("en-AU"),
    EN_NZ("en-NZ"),
    FR_FR("fr-FR"),
    DE_DE("de-DE"),
    DE_AT("de-AT"),
    DE_CH("de-CH"),
    PT_PT("pt-PT"),
    PT_BR("pt-BR"),
    IT_IT("it-IT"),
    ES_ES("es-ES"),
    JA_JP("ja-JP"),
    ZH_CN("zh-CN"),
    EL_GR("el-GR");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
