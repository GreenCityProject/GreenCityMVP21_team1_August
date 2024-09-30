package greencity.mapping;

import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.entity.EventComment;
import org.springframework.stereotype.Component;

@Component
public class EventCommentDtoRequestMapper implements GenericMapper<EventComment, EventCommentDtoRequest>{
    @Override
    public EventComment toEntity(EventCommentDtoRequest dto) {
        if (dto == null) {
            return null;
        }
        EventComment eventComment = new EventComment();
        eventComment.setContent(dto.getText());
        return eventComment;
    }

    @Override
    public EventCommentDtoRequest toDto(EventComment entity) {
        if (entity == null) {
            return null;
        }
        EventCommentDtoRequest eventCommentDtoRequest = new EventCommentDtoRequest();
        eventCommentDtoRequest.setText(entity.getContent());
        return eventCommentDtoRequest;
    }
}
