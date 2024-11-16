package com.sameerasw.ticketin.cli;

import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sameerasw.ticketin.server.model.Customer;
import com.sameerasw.ticketin.server.model.EventItem;
import com.sameerasw.ticketin.server.model.Ticket;
import com.sameerasw.ticketin.server.model.Vendor;
import com.sameerasw.ticketin.server.service.CustomerService;
import com.sameerasw.ticketin.server.service.EventService;
import com.sameerasw.ticketin.server.service.TicketService;
import com.sameerasw.ticketin.server.service.VendorService;


@Component
public class Cli implements CommandLineRunner {

    @Autowired
    private VendorService vendorService;
    @Autowired
    private EventService eventService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private RestTemplate restTemplate;

    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run(String... args) throws Exception {
        start();
    }

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
                System.out.println("Exiting...");
                scanner.close();
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }


    private void displayMenu() {
        System.out.println("\n--- TicketIn CLI Menu ---");
        System.out.println("1. Create Vendor");
        System.out.println("2. List Vendors");
        System.out.println("3. Create Customer");
        System.out.println("4. List Customers");
        System.out.println("5. Create Event");
        System.out.println("6. List Events");
        System.out.println("7. List Tickets for Event");
        System.out.println("8. Buy Ticket");
        System.out.println("9. Release Tickets");
        System.out.println("10. Exit");
    }

    //Helper function to get integer input from the console
    private int getIntegerInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    //Helper function to get string input from the console
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    //Helper function to get long input from the console
    private long getLongInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    //Helper function to get double input from the console
    private double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }


    private void createVendor() {
        String name = getStringInput("Enter vendor name: ");
        String email = getStringInput("Enter vendor email: ");
        int ticketReleaseRate = getIntegerInput("Enter ticket release rate: ");
        Vendor vendor = new Vendor(name, email, ticketReleaseRate);
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
        String email = getStringInput("Enter customer email: ");
        int ticketRetrievalRate = getIntegerInput("Enter ticket retrieval rate: ");
        Customer customer = new Customer(name, email, ticketRetrievalRate);
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
        String eventLocation = getStringInput("Enter event location: ");
        String eventDate = getStringInput("Enter event date (yyyy-MM-dd): ");
        String eventTime = getStringInput("Enter event time (HH:mm): ");
        double ticketPrice = getDoubleInput("Enter ticket price: ");
        int maxPoolSize = getIntegerInput("Enter max pool size: ");

        EventItem eventItem = new EventItem(eventName, eventLocation, eventDate, eventTime, ticketPrice, vendorService.createVendor(new Vendor("test", "test@test.com", 1)), maxPoolSize);
        eventService.createEvent(eventItem);
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
        HttpEntity<Void> entity = new HttpEntity<>(null, new HttpHeaders());
        String url = "http://localhost:8081/tickets/events/" + eventId;
        ResponseEntity<List<Ticket>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Ticket>>() {});

        if (response.getStatusCode().is2xxSuccessful()) {
            List<Ticket> tickets = response.getBody();
            System.out.println("Tickets for Event " + eventId + ":");
            for (Ticket ticket : tickets) {
                System.out.println("  ID: " + ticket.getId() + ", Price: " + ticket.getTicketPrice());
            }
        } else {
            System.err.println("Error fetching tickets: " + response.getStatusCode());
        }

    }

    private void buyTicket() {
        long customerId = getLongInput("Enter customer ID: ");
        long eventId = getLongInput("Enter event ID: ");
        customerService.purchaseTicket(customerService.createCustomer(new Customer("test", "test@test.com", 1)), eventId);
        System.out.println("Ticket purchased successfully.");
    }

    private void releaseTickets() {
        long vendorId = getLongInput("Enter vendor ID: ");
        long eventId = getLongInput("Enter event ID: ");
        vendorService.releaseTickets(vendorService.createVendor(new Vendor("test", "test@test.com", 1)), eventId);
        System.out.println("Tickets released successfully.");
    }

}