package com.mouken.event;


import com.mouken.account.CurrentAccount;
import com.mouken.domain.Account;
import com.mouken.domain.Event;
import com.mouken.domain.Party;
import com.mouken.event.form.EventForm;
import com.mouken.event.validator.EventValidator;
import com.mouken.party.PartyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/party/{path}")
@RequiredArgsConstructor
public class EventController {

    private final PartyService partyService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    
    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Party party = partyService.getPartyToUpdateStatus(account, path);
        model.addAttribute(party);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model) {
        Party party = partyService.getPartyToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(party);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), party, account);
        return "redirect:/party/" + party.getPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}")
    public String getEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id,
                           Model model) {
        model.addAttribute(account);
        model.addAttribute(eventRepository.findById(id).orElseThrow());
        model.addAttribute(partyService.getParty(path));
        return "event/view";
    }

    @GetMapping("/events")
    public String viewPartyEvents(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Party party = partyService.getParty(path);
        model.addAttribute(account);
        model.addAttribute(party);

        List<Event> events = eventRepository.findByPartyOrderByStartDateTime(party);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        events.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(e);
            } else {
                newEvents.add(e);
            }
        });

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "party/events";
    }
}