package greencity.filters;

import greencity.entity.HabitFactTranslation_;
import greencity.entity.ShoppingListItem_;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.ShoppingListItemTranslation_;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import greencity.entity.ShoppingListItem;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ShoppingListItemSpecificationTest {

    @Mock
    private Root<ShoppingListItem> root;

    @Mock
    private Root<ShoppingListItemTranslation> itemTranslationRoot;

    @Mock
    private CriteriaQuery<?> criteriaQuery;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate predicate;

    @Mock
    private Path<Object> idPath;

    @Mock
    private Path<String> contentPath;

    private List<SearchCriteria> searchCriteriaList;

    @Mock
    private SearchCriteria searchCriteria;


    @InjectMocks
    private ShoppingListItemSpecification shoppingListItemSpecification;

    @BeforeEach
    void setUp() {
        searchCriteriaList = new ArrayList<>();
        shoppingListItemSpecification = new ShoppingListItemSpecification(searchCriteriaList);
    }

    @Test
    void toPredicate_withIdCriteria_shouldReturnPredicate() {
        // Arrange
        SearchCriteria idCriteria = new SearchCriteria(1L, "id", "id");
        this.searchCriteriaList.add(idCriteria);

        when(this.criteriaBuilder.conjunction()).thenReturn(this.predicate);
        when(this.root.get("id")).thenReturn(this.idPath);
        when(this.criteriaBuilder.equal(this.idPath, 1L)).thenReturn(this.predicate);
        when(this.criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(this.predicate);

        // Act
        Predicate result = this.shoppingListItemSpecification.toPredicate(this.root, this.criteriaQuery, this.criteriaBuilder);

        // Assert
        assertNotNull(result);
        verify(this.criteriaBuilder).equal(this.idPath, 1L);
        verify(this.criteriaBuilder).and(any(Predicate.class), eq(this.predicate));
    }

    @Test
    void testGetTranslationPredicate_withEmptyValue_shouldReturnConjunction() {
        // Arrange
        SearchCriteria searchCriteria = new SearchCriteria("   ", "content", "content"); // Ensure empty value
        this.shoppingListItemSpecification = new ShoppingListItemSpecification(List.of(searchCriteria));

        // Mock setup
        when(this.criteriaBuilder.conjunction()).thenReturn(this.predicate);

        // Act
        Predicate result = this.shoppingListItemSpecification.toPredicate(this.root, this.criteriaQuery, this.criteriaBuilder);

        // Assert
        assertNotNull(result, "The result predicate should not be null");

        verify(this.criteriaBuilder).conjunction();
        verify(this.criteriaBuilder, never()).and(any(Predicate.class), any(Predicate.class));
    }

    @Test
    void testGetTranslationPredicate_WhenSearchCriteriaIsEmpty_ShouldReturnConjunction() throws Exception {
        // Arrange
        when(this.searchCriteria.getValue()).thenReturn("");
        when(this.criteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(this.itemTranslationRoot);
        when(this.criteriaBuilder.conjunction()).thenReturn(this.predicate);

        // Act
        Predicate predicate = invokeGetTranslationPredicate(this.searchCriteria);

        // Assert
        assertNotNull(predicate);
        verify(this.criteriaBuilder).conjunction();
        verifyNoMoreInteractions(this.criteriaBuilder);
    }

    @Test
    void getTranslationPredicate_whenValueIsNonEmpty_shouldReturnCorrectPredicate() throws Exception {
        // Arrange
        SearchCriteria searchCriteria = SearchCriteria.builder()
                .key(HabitFactTranslation_.CONTENT)
                .type(HabitFactTranslation_.CONTENT)
                .value("searchTerm")
                .build();

        when(this.criteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(this.itemTranslationRoot);
        when(this.criteriaBuilder.like(any(), anyString())).thenReturn(this.predicate);
        when(this.criteriaBuilder.equal(any(), any())).thenReturn(this.predicate);

        Predicate likePredicate = mock(Predicate.class);
        Predicate equalPredicate = mock(Predicate.class);
        when(this.criteriaBuilder.like(any(), anyString())).thenReturn(likePredicate);
        when(this.criteriaBuilder.equal(any(), any())).thenReturn(equalPredicate);

        // Act
        Predicate result = invokeGetTranslationPredicate(searchCriteria);

        // Assert
        assertNotNull(result);
        verify(this.criteriaBuilder).and(likePredicate, equalPredicate);
        verify(this.criteriaBuilder).like(this.itemTranslationRoot.get("content"), "%searchTerm%");
        verify(this.criteriaBuilder).equal(this.itemTranslationRoot.get(ShoppingListItemTranslation_.shoppingListItem).get(ShoppingListItem_.id), this.root.get(ShoppingListItem_.id));
    }

    private Predicate invokeGetTranslationPredicate(SearchCriteria searchCriteria) throws Exception {
        Method method = ShoppingListItemSpecification.class.getDeclaredMethod(
                "getTranslationPredicate",
                Root.class,
                CriteriaQuery.class,
                CriteriaBuilder.class,
                SearchCriteria.class);
        method.setAccessible(true);
        return (Predicate) method.invoke(shoppingListItemSpecification, root, criteriaQuery, criteriaBuilder, searchCriteria);
    }
}
