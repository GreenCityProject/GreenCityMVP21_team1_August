package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.language.LanguageVO;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class EcoNewsVOMapperTest {

    @InjectMocks
    private EcoNewsVOMapper mapper;


    @Test
    void convertTest() {
        // Arrange
        EcoNews news = ModelUtils.getEcoNews()
                .setUsersLikedNews(new HashSet<>(List.of(ModelUtils.getUser())))
                .setUsersDislikedNews(new HashSet<>(List.of(ModelUtils.getUser())))
                .setEcoNewsComments(List.of(ModelUtils.getEcoNewsComment()));

        EcoNewsVO expected = EcoNewsVO.builder()
                .id(news.getId())
                .author(UserVO.builder()
                        .id(news.getAuthor().getId())
                        .name(news.getAuthor().getName())
                        .userStatus(news.getAuthor().getUserStatus())
                        .role(news.getAuthor().getRole())
                        .build())
                .creationDate(news.getCreationDate())
                .imagePath(news.getImagePath())
                .source(news.getSource())
                .text(news.getText())
                .title(news.getTitle())
                .tags(news.getTags().stream()
                        .map(tag -> TagVO.builder()
                                .id(tag.getId())
                                .tagTranslations(tag.getTagTranslations().stream()
                                        .map(tagTranslation -> TagTranslationVO.builder()
                                                .name(tagTranslation.getName())
                                                .id(tagTranslation.getId())
                                                .languageVO(LanguageVO.builder()
                                                        .code(tagTranslation.getLanguage().getCode())
                                                        .id(tagTranslation.getId())
                                                        .build())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .usersLikedNews(news.getUsersLikedNews().stream()
                        .map(user -> UserVO.builder()
                                .id(user.getId())
                                .build())
                        .collect(Collectors.toSet()))
                .usersDislikedNews(news.getUsersDislikedNews().stream()
                        .map(user -> UserVO.builder()
                                .id(user.getId())
                                .build())
                        .collect(Collectors.toSet()))
                .ecoNewsComments(news.getEcoNewsComments().stream()
                        .map(ecoNewsComment -> EcoNewsCommentVO.builder()
                                .id(ecoNewsComment.getId())
                                .createdDate(ecoNewsComment.getCreatedDate())
                                .currentUserLiked(ecoNewsComment.isCurrentUserLiked())
                                .deleted(ecoNewsComment.isDeleted())
                                .text(ecoNewsComment.getText())
                                .modifiedDate(ecoNewsComment.getModifiedDate())
                                .user(UserVO.builder()
                                        .id(ecoNewsComment.getUser().getId())
                                        .name(ecoNewsComment.getUser().getName())
                                        .userStatus(ecoNewsComment.getUser().getUserStatus())
                                        .role(ecoNewsComment.getUser().getRole())
                                        .build())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        // Act
        EcoNewsVO actual = mapper.convert(news);

        // Assert
        assertEquals(expected, actual);
    }
}
