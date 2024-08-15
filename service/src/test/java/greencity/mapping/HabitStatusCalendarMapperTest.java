package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitStatusCalendarMapperTest {

    @InjectMocks
    private HabitStatusCalendarMapper mapper;

    @Test
    void convertTest() {
        // Arrange
        HabitStatusCalendar habitStatusCalendar = ModelUtils.getHabitStatusCalendar();
        habitStatusCalendar.setHabitAssign(ModelUtils.getHabitAssign());

        HabitStatusCalendarVO habitStatusCalendarVO = HabitStatusCalendarVO.builder()
                .id(habitStatusCalendar.getId())
                .enrollDate(habitStatusCalendar.getEnrollDate())
                .habitAssignVO(HabitAssignVO.builder()
                        .id(habitStatusCalendar.getHabitAssign().getId())
                        .build())
                .build();

        HabitStatusCalendar expected = HabitStatusCalendar.builder()
                .id(habitStatusCalendarVO.getId())
                .enrollDate(habitStatusCalendarVO.getEnrollDate())
                .habitAssign(HabitAssign.builder()
                        .id(habitStatusCalendarVO.getHabitAssignVO().getId())
                        .build())
                .build();

        // Act
        HabitStatusCalendar actual = mapper.convert(habitStatusCalendarVO);

        // Assert
        assertEquals(expected, actual);
    }
}
