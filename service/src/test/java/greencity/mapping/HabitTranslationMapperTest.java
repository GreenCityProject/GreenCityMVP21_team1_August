package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitTranslationMapperTest {

    @InjectMocks
    private HabitTranslationMapper mapper;

    @Test
    void convertTest() {
        // Arrange
        HabitTranslationDto habitTranslationDto = HabitTranslationDto.builder()
                .name("name")
                .description("description")
                .habitItem("item")
                .languageCode(ModelUtils.getLanguage().getCode())
                .build();


        HabitTranslation expected = HabitTranslation.builder()
                .name(habitTranslationDto.getName())
                .description(habitTranslationDto.getDescription())
                .habitItem(habitTranslationDto.getHabitItem())
                .build();

        // Act
        HabitTranslation actual = mapper.convert(habitTranslationDto);

        //Assert
        assertEquals(expected, actual);
    }

    @Test
    void mapAllToListTest() {
        // Arrange
        HabitTranslationDto habitTranslationDto = HabitTranslationDto.builder()
                .name("name")
                .description("description")
                .habitItem("item")
                .languageCode(ModelUtils.getLanguage().getCode())
                .build();

        HabitTranslation expected = HabitTranslation.builder()
                .name(habitTranslationDto.getName())
                .description(habitTranslationDto.getDescription())
                .habitItem(habitTranslationDto.getHabitItem())
                .build();

        //Act
        List<HabitTranslation> expectedList = List.of(expected);
        List<HabitTranslation> actualList = mapper.mapAllToList(List.of(habitTranslationDto));

        // Assert
        assertEquals(expectedList, actualList);
    }
}
