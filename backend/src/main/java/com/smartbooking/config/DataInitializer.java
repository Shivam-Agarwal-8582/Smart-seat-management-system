package com.smartbooking.config;

import com.smartbooking.model.*;
import com.smartbooking.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final BatchRepository batchRepository;
    private final SquadRepository squadRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final WaitlistRepository waitlistRepository;

    public DataInitializer(BatchRepository batchRepository, SquadRepository squadRepository, 
                           SeatRepository seatRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, WaitlistRepository waitlistRepository) {
        this.batchRepository = batchRepository;
        this.squadRepository = squadRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.waitlistRepository = waitlistRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize Core Data (Batches, Squads, Seats) if missing
        if (batchRepository.count() == 0) {
            Batch batch1 = Batch.builder()
                    .name("Batch A")
                    .allowedDays(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)))
                    .build();
            Batch batch2 = Batch.builder()
                    .name("Batch B")
                    .allowedDays(new HashSet<>(Arrays.asList(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)))
                    .build();
            batchRepository.saveAll(Arrays.asList(batch1, batch2));

            IntStream.rangeClosed(1, 5).forEach(i -> {
                squadRepository.save(Squad.builder().name("Squad A" + i).batch(batch1).build());
                squadRepository.save(Squad.builder().name("Squad B" + i).batch(batch2).build());
            });

            IntStream.rangeClosed(1, 10).forEach(i -> {
                seatRepository.save(Seat.builder()
                        .seatNumber("FL-" + i)
                        .type(Seat.SeatType.FLOATING)
                        .build());
            });

            IntStream.rangeClosed(1, 40).forEach(i -> {
                seatRepository.save(Seat.builder()
                        .seatNumber("FX-" + i)
                        .type(Seat.SeatType.FIXED)
                        .build());
            });
        }

        // Initialize Users if we don't have exactly 11 (Admin + 10 Employees)
        if (userRepository.count() != 11) {
            System.out.println("Resetting users to ensure 10 test employees exist...");
            waitlistRepository.deleteAll();
            bookingRepository.deleteAll();
            userRepository.deleteAll();

            User admin = User.builder()
                    .name("Admin")
                    .email("admin@company.com")
                    .role(User.Role.ADMIN)
                    .build();
            userRepository.save(admin);

            var allSquads = squadRepository.findAll();
            IntStream.rangeClosed(1, 10).forEach(i -> {
                userRepository.save(User.builder()
                        .name("User " + i)
                        .email("user" + i + "@company.com")
                        .role(User.Role.USER)
                        .squad(allSquads.get(i - 1))
                        .build());
            });
        }
        
        System.out.println("Data Initialization complete: " + userRepository.count() + " users available.");
    }
}
