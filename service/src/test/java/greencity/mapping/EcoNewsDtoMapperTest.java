package greencity.mapping;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class EcoNewsDtoMapperTest {

    @InjectMocks
    private EcoNewsDtoMapper mapper;

    @Test
    void convertTest() {
        //Arrange
        EcoNews news = ModelUtils.getEcoNews();
        EcoNewsDto expected = EcoNewsDto.builder()
                .author(EcoNewsAuthorDto.builder()
                        .id(news.getId())
                        .name(news.getAuthor().getName())
                        .build())
                .id(news.getId())
                .content(news.getText())
                .creationDate(news.getCreationDate())
                .imagePath(news.getImagePath())
                .likes(news.getUsersLikedNews().size())
                .shortInfo(news.getShortInfo())
                .tags(news.getTags().stream()
                        .flatMap(t -> t.getTagTranslations().stream())
                        .filter(t -> t.getLanguage().getCode().equals(AppConstant.DEFAULT_LANGUAGE_CODE))
                        .map(TagTranslation::getName).toList())
                .tagsUa(news.getTags().stream()
                        .flatMap(t -> t.getTagTranslations().stream())
                        .filter(t -> t.getLanguage().getCode().equals("ua"))
                        .map(TagTranslation::getName).toList())
                .likes(news.getUsersLikedNews().size())
                .dislikes(news.getUsersDislikedNews().size())
                .title(news.getTitle())
                .countComments((int) news.getEcoNewsComments().stream().filter(deleted -> !deleted.isDeleted()).count())
                .build();

        // Act
        EcoNewsDto actual = mapper.convert(news);

        // Assert
        assertEquals(expected, actual);
    }
}
