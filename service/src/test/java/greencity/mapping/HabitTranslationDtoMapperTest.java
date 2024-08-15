package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitTranslationDtoMapperTest {

    @InjectMocks
    private HabitTranslationDtoMapper mapper;

    @Test
    void convertTest() {
        HabitTranslation habitTranslation = HabitTranslation.builder()
                .id(1L)
                .name("name")
                .description("description")
                .habitItem("item")
                .language(ModelUtils.getLanguage())
                .build();

        HabitTranslationDto expected = HabitTranslationDto.builder()
                .name(habitTranslation.getName())
                .description(habitTranslation.getDescription())
                .habitItem(habitTranslation.getHabitItem())
                .languageCode(habitTranslation.getLanguage().getCode())
                .build();

        HabitTranslationDto actual = mapper.convert(habitTranslation);

        assertEquals(expected, actual);
    }

    @Test
    void mapToListTest() {
        HabitTranslation habitTranslation = HabitTranslation.builder()
                .id(1L)
                .name("name")
                .description("description")
                .habitItem("item")
                .language(ModelUtils.getLanguage())
                .build();

        HabitTranslationDto expected = HabitTranslationDto.builder()
                .name(habitTranslation.getName())
                .description(habitTranslation.getDescription())
                .habitItem(habitTranslation.getHabitItem())
                .languageCode(habitTranslation.getLanguage().getCode())
                .build();

        List<HabitTranslationDto> expectedList = List.of(expected);
        List<HabitTranslationDto> actualList = mapper.mapAllToList(List.of(habitTranslation));

        assertArrayEquals(expectedList.toArray(), actualList.toArray());
    }
}
