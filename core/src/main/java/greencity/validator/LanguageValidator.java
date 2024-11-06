package greencity.validator;

import greencity.annotations.ValidLanguage;
import greencity.service.LanguageService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

public class LanguageValidator implements ConstraintValidator<ValidLanguage, Locale> {
    private List<String> codes;

    @Autowired
    private LanguageService languageService;

    @Override
    public void initialize(ValidLanguage constraintAnnotation) {
        this.codes = this.languageService.findAllLanguageCodes();
    }

    @Override
    public boolean isValid(Locale value, ConstraintValidatorContext context) {
        return this.codes.contains(value.getLanguage());
    }
}