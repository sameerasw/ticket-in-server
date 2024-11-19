package com.sameerasw.ticketin.server.service;

import com.sameerasw.ticketin.server.model.EventItem;
import com.sameerasw.ticketin.server.model.Ticket;
import com.sameerasw.ticketin.server.model.TicketPool;
import com.sameerasw.ticketin.server.model.Vendor;
import com.sameerasw.ticketin.server.repository.EventRepository;
import com.sameerasw.ticketin.server.repository.TicketPoolRepository;
import com.sameerasw.ticketin.server.repository.TicketRepository;
import com.sameerasw.ticketin.server.repository.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sameerasw.ticketin.server.Application.*;

@Service
public class VendorService {
    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);

    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketPoolService ticketPoolService;
    @Autowired
    private TicketPoolRepository ticketPoolRepository;

    public Vendor createVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    public void releaseTickets(Vendor vendor, Long eventId) {
        EventItem eventItem = eventRepository.findById(eventId).orElse(null);
        if (eventItem != null) {
            TicketPool ticketPool = eventItem.getTicketPool();
            if (ticketPool != null) {
                if (ticketPool.getAvailableTickets() < ticketPool.getMaxPoolSize()) {
                    Ticket ticket = new Ticket(eventItem, true);
                    ticketRepository.save(ticket);
                    ticketPoolService.addTicket(ticketPool, ticket);
                    ticketPoolRepository.save(ticketPool);
                    logger.info(ANSI_CYAN + vendor.getName() + " - Released ticket: " + ticket.getId() + " for: " + eventItem.getName() + ANSI_RESET);
                } else {
                    logger.info(ANSI_YELLOW + vendor.getName() + " - Ticket pool is full for: " + eventItem.getName() + ANSI_RESET);
                }
            } else {
                logger.info("Ticket pool not found");
            }
        } else {
            logger.info("Event not found");
        }
    }

    public List<Vendor> getAllVendors(boolean isSimulated) {
        return vendorRepository.findByisSimulated(isSimulated);
    }

    public Vendor getVendorById(long vendorId) {
        return vendorRepository.findById(vendorId).orElse(null);
    }
}