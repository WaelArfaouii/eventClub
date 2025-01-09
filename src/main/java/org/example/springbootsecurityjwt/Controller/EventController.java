package org.example.springbootsecurityjwt.Controller;

import org.example.springbootsecurityjwt.dto.EventRequest;
import org.example.springbootsecurityjwt.models.Event;
import org.example.springbootsecurityjwt.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestBody EventRequest eventRequest) {
        LocalDate date = LocalDate.parse(eventRequest.getEventDate());
        Event event = eventService.createEvent(
                eventRequest.getEventName(),
                eventRequest.getEventDescription(),
                date,
                eventRequest.getEventLocation(),
                eventRequest.getOrganizerName()
        );
        return ResponseEntity.ok(event);
    }

    @GetMapping("events")
    public String getAllEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "eventList";
    }

    @PostMapping("/participate/{eventId}")
    public String participateInEvent(@PathVariable Long eventId , RedirectAttributes redirectAttributes) {
        try {
            eventService.addParticipant(eventId);  // Add participant to event
            redirectAttributes.addFlashAttribute("successMessage", "You have successfully participated in the event!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return null;
        }
        return "redirect:/events";  // Redirect to the events page
    }
}