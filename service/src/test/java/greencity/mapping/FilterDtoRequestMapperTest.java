package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserFilterDtoRequest;
import greencity.entity.Filter;
import greencity.enums.FilterType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class FilterDtoRequestMapperTest {

    @InjectMocks
    private FilterDtoRequestMapper mapper;

    @Test
    void convertTest() {
        // Arrange
        UserFilterDtoRequest userFilterDtoRequest = ModelUtils.getUserFilterDtoRequest();

        String values = userFilterDtoRequest.getSearchCriteria() +
                ";" +
                userFilterDtoRequest.getUserRole() +
                ";" +
                userFilterDtoRequest.getUserStatus();

        Filter expected = Filter.builder()
                .name(userFilterDtoRequest.getName())
                .type(FilterType.USERS.toString())
                .values(values)
                .build();

        // Act
        Filter actual = mapper.convert(userFilterDtoRequest);

        // Assert
        assertEquals(expected, actual);
    }
}
