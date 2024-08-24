package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@Table(name = "event_day_details")
public class EventDayDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_start_time", nullable = false)
    private LocalTime eventStartTime;

    @Column(name = "event_end_time", nullable = false)
    private LocalTime eventEndTime;
    
    @Column(name = "all_day", nullable = false)
    private boolean isAllDateDuration;

    @Column(name = "online")
    private boolean isOnline;

    @Column(name = "offline")
    private boolean isOffline;

    @Column(name = "location")
    private String offlinePlace;

    @Column(name = "link")
    private String onlinePlace;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

}
