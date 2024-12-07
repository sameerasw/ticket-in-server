package com.sameerasw.ticketin.cli;

import com.sameerasw.ticketin.server.model.Customer;
import com.sameerasw.ticketin.server.model.EventItem;
import com.sameerasw.ticketin.server.model.Ticket;
import com.sameerasw.ticketin.server.model.Vendor;
import com.sameerasw.ticketin.server.service.CustomerService;
import com.sameerasw.ticketin.server.service.EventService;
import com.sameerasw.ticketin.server.service.TicketService;
import com.sameerasw.ticketin.server.service.VendorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class Cli {
    public static final Logger logger = LoggerFactory.getLogger(Cli.class);

    @Autowired
    private VendorService vendorService;
    @Autowired
    private EventService eventService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private CustomerService customerService;

    private Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("TicketIn CLI - Started");
        while (true) {
            displayMenu();
            int choice = getIntegerInput("Enter your choice: ");
            processChoice(choice);
        }
    }

    private void processChoice(int choice) {
        switch (choice) {
            case 1:
                createVendor();
                break;
            case 2:
                listVendors();
                break;
            case 3:
                createCustomer();
                break;
            case 4:
                listCustomers();
                break;
            case 5:
                createEvent();
                break;
            case 6:
                listEvents();
                break;
            case 7:
                listTicketsForEvent();
                break;
            case 8:
                buyTicket();
                break;
            case 9:
                releaseTickets();
                break;
            case 10:
                viewTicketPool();
                break;
            case 11:
                System.out.println("Exiting...");
                scanner.close();
                System.exit(0);
                break;
            case 12:
                startSimulation();
                break;
            case 13:
                configureSimulation();
                break;
            case 14:
                howManyThreads();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void howManyThreads() {
        System.out.println("Running threads: " + Thread.activeCount());
    }

    private void startSimulation() {
        System.out.println("Starting simulation... Press Enter to stop.");
        List<Customer> customers = customerService.getAllCustomers(true);
        List<Vendor> vendors = vendorService.getAllVendors(true);
        List<EventItem> events = eventService.getAllEvents(true);
        final boolean[] isSimulating = {true};

        for (Vendor vendor : vendors) {
            new Thread(new VendorSimulation(vendor, events, vendorService, isSimulating)).start();
        }

        for (Customer customer : customers) {
            new Thread(new CustomerSimulation(customer, events, customerService, isSimulating)).start();
        }

        System.out.println("Running threads: " + Thread.activeCount());
        scanner.nextLine();
        System.out.println("Stopping simulation...");
        isSimulating[0] = false;

        Thread.currentThread().interrupt();
    }

    private void displayMenu() {
        System.out.println("\n--- TicketIn CLI Menu ---\n" +
                "1. Create Vendor\n" +
                "2. List Vendors\n" +
                "3. Create Customer\n" +
                "4. List Customers\n" +
                "5. Create Event\n" +
                "6. List Events\n" +
                "7. List Tickets for Event\n" +
                "8. Buy Ticket\n" +
                "9. Release Tickets\n" +
                "10. View TicketPool\n" +
                "11. Exit\n" +
                "12. Start Simulation\n" +
                "13. Configure the simulation\n" +
                "14. How many threads are running?"
        );
    }

    private void configureSimulation() {
        System.out.println("Configure the simulation");
        int numVendors = getIntegerInput("Enter the number of vendors: ");
        int numCustomers = getIntegerInput("Enter the number of customers: ");
//        int numEvents = getIntegerInput("Enter the number of events: ");
//        int numTicketsPerEvent = getIntegerInput("Enter the number of tickets per event: ");
//        int ticketReleaseRate = getIntegerInput("Enter the ticket release rate: ");
//        int ticketRetrievalRate = getIntegerInput("Enter the ticket retrieval rate: ");

        System.out.println("Creating simulation data... Please wait. This may take a while.");

        for (int i = 0; i < numVendors; i++) {
            int ticketReleaseRate = (int) (Math.random() * 5) + 1;
            Vendor vendor = new Vendor("Simulated_Vendor " + i, getRandomeEmail("Vendor " + i), ticketReleaseRate);
            vendorService.createVendor(vendor);
//            logger.info("Vendor created: " + vendor.getId() + vendor.getName());
        }
        logger.info("Vendors created");

        for (int i = 0; i < numCustomers; i++) {
            int ticketRetrievalRate = (int) (Math.random() * 5) + 1;
            Customer customer = new Customer("Simulated_Customer " + i, getRandomeEmail("Customer " + i), ticketRetrievalRate);
            customerService.createCustomer(customer);
//            logger.info("Customer created: " + customer.getId() + customer.getName());
        }
        logger.info("Customers created");

        for (int i = 0; i < numVendors; i++) {
            int numTicketsPerEvent = (int) (Math.random() * 10) + 1;
            Vendor vendor = vendorService.getAllVendors(true).get((int) (Math.random() * numVendors));
            EventItem eventItem = new EventItem("Simulated_Event " + i, vendor, true);
            eventService.createEvent(eventItem, numTicketsPerEvent);
            eventItem.createTicketPool(numTicketsPerEvent);
//            logger.info("Event created: " + eventItem.getId() + eventItem.getEventName());
        }

        System.out.println("Created " + numVendors + " vendors, " + numCustomers + " customers, and " + numVendors + " events.\n Simulation is ready.");
    }

    private int getIntegerInput(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private String getStringInput(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }

    private long getLongInput(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private String getRandomeEmail(String name) {
        // Generate a random email address
        return name.toLowerCase().replace(" ", "") + (int) (Math.random() * 1000) + "@example.com";
    }

    private void createVendor() {
        String name = getStringInput("Enter vendor name: ");
        int ticketReleaseRate = getIntegerInput("Enter ticket release rate: ");
        Vendor vendor = new Vendor(name, getRandomeEmail(name), ticketReleaseRate);
        vendorService.createVendor(vendor);
        System.out.println("Vendor created successfully.");
    }

    private void listVendors() {
        List<Vendor> vendors = vendorService.getAllVendors(true);
        System.out.println("Vendors:");
        for (Vendor vendor : vendors) {
            System.out.println("  ID: " + vendor.getId() + ", Name: " + vendor.getName());
        }
    }

    private void createCustomer() {
        String name = getStringInput("Enter customer name: ");
        int ticketRetrievalRate = getIntegerInput("Enter ticket retrieval rate: ");
        Customer customer = new Customer(name, getRandomeEmail(name), ticketRetrievalRate);
        customerService.createCustomer(customer);
        System.out.println("Customer created successfully.");
    }

    private void listCustomers() {
        List<Customer> customers = customerService.getAllCustomers(true);
        System.out.println("Customers:");
        for (Customer customer : customers) {
            System.out.println("  ID: " + customer.getId() + ", Name: " + customer.getName());
        }
    }

    private void createEvent() {
        long vendorId = getLongInput("Enter vendor ID: ");
        String eventName = getStringInput("Enter event name: ");
        int maxPoolSize = getIntegerInput("Enter max pool size: ");

        EventItem eventItem = new EventItem(eventName, vendorService.getVendorById(vendorId), true);
        eventService.createEvent(eventItem, maxPoolSize);
        eventItem.createTicketPool(maxPoolSize);
        System.out.println("Event created successfully.");
    }

    private void listEvents() {
        List<EventItem> eventItems = eventService.getAllEvents(true);
        System.out.println("Events:");
        for (EventItem eventItem : eventItems) {
            System.out.println("  ID: " + eventItem.getId() + ", Name: " + eventItem.getEventName());
        }
    }

    private void listTicketsForEvent() {
        long eventId = getLongInput("Enter event ID: ");
        EventItem eventItem = eventService.getEventById(eventId);
        if (eventItem != null) {
            List<Ticket> tickets = eventItem.getTicketPool().getTickets();
            System.out.println("Tickets for event " + eventItem.getEventName() + ":");
            for (Ticket ticket : tickets) {
                System.out.println("  ID: " + ticket.getId() + ", Available?: " + ticket.isAvailable());
            }
        } else {
            System.out.println("Event not found.");
        }
    }

    private void buyTicket() {
        long customerId = getLongInput("Enter customer ID: ");
        long eventId = getLongInput("Enter event ID: ");
        Customer customer = customerService.getCustomerById(customerId);
        EventItem eventItem = eventService.getEventById(eventId);

        if (customer != null && eventItem != null) {
            System.out.println("Ticket purchase requested.");
            customerService.purchaseTicket(customer, eventId);
        } else {
            System.out.println("Customer or Event not found.");
        }
    }

    private void releaseTickets() {
        long vendorId = getLongInput("Enter vendor ID: ");
        long eventId = getLongInput("Enter event ID: ");
        Vendor vendor = vendorService.getVendorById(vendorId);
        EventItem eventItem = eventService.getEventById(eventId);

        if (vendor != null && eventItem != null && eventItem.getVendor().getId().equals(vendorId)) {
            System.out.println("Tickets release requested.");
            vendorService.releaseTickets(vendor, eventId);
        } else {
            System.out.println("Vendor or Event not found, or they are not related.");
        }
    }

    private void viewTicketPool() {
        long eventId = getLongInput("Enter event ID: ");
        EventItem eventItem = eventService.getEventById(eventId);
        if (eventItem != null) {
            System.out.println(eventItem.toString());
        } else {
            System.out.println("Event not found.");
        }
    }
}