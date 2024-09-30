package greencity.mapping;


import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationResponseDtoMapper implements GenericMapper<Notification, NotificationResponseDto>{
    @Override
    public NotificationResponseDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        return new NotificationResponseDto(
                notification.getId(),
                notification.getSection().name(),
                notification.getSectionType().name(),
                notification.getText(),
                notification.isRead(),
                notification.getReceivedTime(),
                notification.getUser().getId()
        );
    }

    @Override
    public Notification toEntity(NotificationResponseDto dto) {
        throw new UnsupportedOperationException("Mapping from NotificationResponseDto to Notification is not supported.");
    }
}
