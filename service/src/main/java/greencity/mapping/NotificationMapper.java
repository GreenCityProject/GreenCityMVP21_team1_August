package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationSource;
import greencity.enums.NotificationSourceType;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper implements GenericMapper<Notification, NotificationDto>{
    @Override
    public Notification toEntity(NotificationDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException(ErrorMessage.CANNOT_MAP_NULL_TO_ENTITY);
        }

        return Notification.builder()
                .id(dto.getId())
                .section(NotificationSource.valueOf(dto.getSection()))
                .sectionType(NotificationSourceType.valueOf(dto.getSectionType()))
                .text(dto.getText())
                .isRead(dto.isRead())
                .user(User.builder().id(dto.getUserId()).build())
                .build();
    }

    @Override
    public NotificationDto toDto(Notification entity) {
        if (entity == null) {
            throw new IllegalArgumentException(ErrorMessage.CANNOT_MAP_NULL_TO_DTO);
        }
        return NotificationDto.builder()
                .id(entity.getId())
                .section(entity.getSection().toString())
                .sectionType(entity.getSectionType().toString())
                .text(entity.getText())
                .isRead(entity.isRead())
                .userId(entity.getUser().getId())
                .build();
    }
}
