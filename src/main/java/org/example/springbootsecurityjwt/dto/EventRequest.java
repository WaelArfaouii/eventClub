package org.example.springbootsecurityjwt.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class EventRequest {
    private String eventName;
    private String eventDescription;
    private String eventDate;
    private String eventLocation;
    private String organizerName;
}