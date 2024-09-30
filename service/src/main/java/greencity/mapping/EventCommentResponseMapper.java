package greencity.mapping;


import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EventCommentResponseMapper implements GenericMapper<EventComment, EventCommentDtoResponse>{
    @Override
    public EventComment toEntity(EventCommentDtoResponse dto) {
        if (dto == null) {
            return null;
        }
        EventComment eventComment = new EventComment();
        eventComment.setId(dto.getId());
        eventComment.setEvent(Event.builder().id(dto.getEventId()).build());
        eventComment.setAuthor(User.builder().id(dto.getUserId()).name(dto.getUserName()).build());
        eventComment.setContent(dto.getText());
        eventComment.setCreatedDate(dto.getCreatedDate());
        eventComment.setIsEdited(dto.isEdited());
        return eventComment;
    }

    @Override
    public EventCommentDtoResponse toDto(EventComment entity) {
        if (entity == null) {
            return null;
        }
        EventCommentDtoResponse dto = new EventCommentDtoResponse();
        dto.setId(entity.getId());
        dto.setEventId(entity.getEvent().getId());
        dto.setUserId(entity.getAuthor().getId());
        dto.setUserName(entity.getAuthor().getName());
        dto.setText(entity.getContent());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setEdited(entity.getIsEdited());
        return dto;
    }
}
