package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.entity.HabitStatistic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitStatisticDtoMapperTest {

    @InjectMocks
    private HabitStatisticDtoMapper mapper;

    @Test
    void convertTest() {
        // Arrange
        HabitStatistic habitStatistic = ModelUtils.getHabitStatistic();
        habitStatistic.setHabitAssign(ModelUtils.getHabitAssign());

        HabitStatisticDto expected = HabitStatisticDto.builder()
                .id(habitStatistic.getId())
                .amountOfItems(habitStatistic.getAmountOfItems())
                .createDate(habitStatistic.getCreateDate())
                .habitRate(habitStatistic.getHabitRate())
                .habitAssignId(habitStatistic.getHabitAssign().getId())
                .build();

        // Act
        HabitStatisticDto actual = mapper.convert(habitStatistic);

        // Assert
        assertEquals(expected, actual);
    }
}
