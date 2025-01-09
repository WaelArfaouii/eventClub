package org.example.springbootsecurityjwt.Controller;

import org.example.springbootsecurityjwt.models.Event;
import org.example.springbootsecurityjwt.models.User;
import org.example.springbootsecurityjwt.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private EventService eventService;


    @GetMapping({"" , "/home"})
    public String getHomePage() {
        return "index";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "register";
    }

    @GetMapping("/event")
    public String getEventPage(){
        return "event";
    }

    @GetMapping("/club-dashboard")
    public String getClubDashboard(){
        return "club-dashboard";
    }

    @GetMapping("events")
    public String getAllEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "eventList";
    }

    @PostMapping("/delete-event/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEventById(id); // Replace with your actual service logic
            redirectAttributes.addFlashAttribute("successMessage", "Événement supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression de l'événement.");
        }
        return "redirect:/events";
    }

    @GetMapping("/{id}/edit")
    public String editEvent(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "editEvent";
    }

    @PostMapping("/edit-event")
    public String editEvent(@ModelAttribute("event") Event event, RedirectAttributes redirectAttributes) {
        try {
            eventService.updateEvent(event);
            redirectAttributes.addFlashAttribute("successMessage", "Événement modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la modification de l'événement.");
        }
        return "redirect:/events";
    }

    @GetMapping("participation")
    public String getParticipation(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "participation";
    }

    @GetMapping("participations")
    public String getParticipations(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "participation";
    }

    @PostMapping("/participate/{eventId}")
    public String participateInEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        try {
            eventService.addParticipant(eventId);
            redirectAttributes.addFlashAttribute("successMessage", "You have successfully participated in the event!");
            return "redirect:/events";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return null ;
        }
          // Redirect to events page after participation
    }
}
