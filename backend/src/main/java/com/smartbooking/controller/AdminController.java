package com.smartbooking.controller;
import com.smartbooking.model.BlockedDay;
import com.smartbooking.model.Seat;
import com.smartbooking.model.User;
import com.smartbooking.service.AdminService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    @PostMapping("/blocked-days")
    public BlockedDay addBlockedDay(@RequestBody BlockedDay day) {
        return adminService.addBlockedDay(day);
    }
    @GetMapping("/blocked-days")
    public List<BlockedDay> getBlockedDays() {
        return adminService.getAllBlockedDays();
    }
    @GetMapping("/seats")
    public List<Seat> getSeats() {
        return adminService.getAllSeats();
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/stats")
    public java.util.Map<String, Long> getStats() {
        return adminService.getDashboardStats();
    }
}
