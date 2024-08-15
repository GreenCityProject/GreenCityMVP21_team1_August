package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserFilterDtoResponse;
import greencity.entity.Filter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
public class FilterDtoResponseMapperTest {

    @InjectMocks
    private FilterDtoResponseMapper mapper;

    @Test
    void convertTest() {
        // Arrange
        Filter filter = ModelUtils.getFilter();
        String values = filter.getValues();
        String[] criterias = values.split(";");

        UserFilterDtoResponse expected = UserFilterDtoResponse.builder()
                .id(filter.getId())
                .name(filter.getName())
                .searchCriteria(criterias[0])
                .userRole(criterias[1])
                .userStatus(criterias[2])
                .build();

        // Act
        UserFilterDtoResponse actual = mapper.convert(filter);

        // Assert
        assertEquals(expected, actual);
    }
}
