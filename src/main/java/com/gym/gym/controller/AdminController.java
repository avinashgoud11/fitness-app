package com.gym.gym.controller;

import com.gym.gym.model.Admin;
import com.gym.gym.service.AdminService;
import com.gym.gym.exception.ResourceNotFoundException;
import com.gym.gym.exception.DuplicateResourceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Create a new admin
     */
    @PostMapping
    public ResponseEntity<?> createAdmin(@RequestBody Admin admin) {
        try {
            Admin createdAdmin = adminService.createAdmin(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
        } catch (DuplicateResourceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Create admin from existing user
     */
    @PostMapping("/from-user/{userId}")
    public ResponseEntity<?> createAdminFromUser(
            @PathVariable Long userId,
            @RequestParam String department,
            @RequestParam String accessLevel) {
        try {
            Admin createdAdmin = adminService.createAdminFromUser(userId, department, accessLevel);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (DuplicateResourceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get admin by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        try {
            Admin admin = adminService.getAdminById(id);
            return ResponseEntity.ok(admin);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get admin by admin code
     */
    @GetMapping("/code/{adminCode}")
    public ResponseEntity<?> getAdminByCode(@PathVariable String adminCode) {
        try {
            Admin admin = adminService.getAdminByCode(adminCode);
            return ResponseEntity.ok(admin);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get admin by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAdminByUserId(@PathVariable Long userId) {
        try {
            Admin admin = adminService.getAdminByUserId(userId);
            return ResponseEntity.ok(admin);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get admin by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getAdminByEmail(@PathVariable String email) {
        try {
            Admin admin = adminService.getAdminByEmail(email);
            return ResponseEntity.ok(admin);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all admins
     */
    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    /**
     * Get all active admins
     */
    @GetMapping("/active")
    public ResponseEntity<List<Admin>> getActiveAdmins() {
        List<Admin> admins = adminService.getActiveAdmins();
        return ResponseEntity.ok(admins);
    }

    /**
     * Get admins by department
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Admin>> getAdminsByDepartment(@PathVariable String department) {
        List<Admin> admins = adminService.getAdminsByDepartment(department);
        return ResponseEntity.ok(admins);
    }

    /**
     * Get admins by access level
     */
    @GetMapping("/access-level/{accessLevel}")
    public ResponseEntity<List<Admin>> getAdminsByAccessLevel(@PathVariable String accessLevel) {
        List<Admin> admins = adminService.getAdminsByAccessLevel(accessLevel);
        return ResponseEntity.ok(admins);
    }

    /**
     * Update admin
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Long id, @RequestBody Admin adminDetails) {
        try {
            Admin updatedAdmin = adminService.updateAdmin(id, adminDetails);
            return ResponseEntity.ok(updatedAdmin);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (DuplicateResourceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update admin access level
     */
    @PatchMapping("/{id}/access-level")
    public ResponseEntity<?> updateAdminAccessLevel(
            @PathVariable Long id,
            @RequestParam String accessLevel) {
        try {
            Admin updatedAdmin = adminService.updateAdminAccessLevel(id, accessLevel);
            return ResponseEntity.ok(updatedAdmin);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update admin department
     */
    @PatchMapping("/{id}/department")
    public ResponseEntity<?> updateAdminDepartment(
            @PathVariable Long id,
            @RequestParam String department) {
        try {
            Admin updatedAdmin = adminService.updateAdminDepartment(id, department);
            return ResponseEntity.ok(updatedAdmin);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Activate admin
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateAdmin(@PathVariable Long id) {
        try {
            Admin activatedAdmin = adminService.activateAdmin(id);
            return ResponseEntity.ok(activatedAdmin);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deactivate admin
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateAdmin(@PathVariable Long id) {
        try {
            Admin deactivatedAdmin = adminService.deactivateAdmin(id);
            return ResponseEntity.ok(deactivatedAdmin);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete admin
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Admin deleted successfully");
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Check if user is admin
     */
    @GetMapping("/check/{userId}")
    public ResponseEntity<Map<String, Boolean>> isUserAdmin(@PathVariable Long userId) {
        boolean isAdmin = adminService.isUserAdmin(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAdmin", isAdmin);
        return ResponseEntity.ok(response);
    }

    /**
     * Check if admin code exists
     */
    @GetMapping("/exists/{adminCode}")
    public ResponseEntity<Map<String, Boolean>> adminCodeExists(@PathVariable String adminCode) {
        boolean exists = adminService.adminCodeExists(adminCode);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * Generate admin code
     */
    @GetMapping("/generate-code")
    public ResponseEntity<Map<String, String>> generateAdminCode() {
        String adminCode = adminService.generateAdminCode();
        Map<String, String> response = new HashMap<>();
        response.put("adminCode", adminCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Get admin statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<AdminService.AdminStatistics> getAdminStatistics() {
        AdminService.AdminStatistics stats = adminService.getAdminStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Bulk operations
     */
    @PostMapping("/bulk/activate")
    public ResponseEntity<Map<String, String>> bulkActivateAdmins(@RequestBody List<Long> adminIds) {
        int activatedCount = 0;
        for (Long id : adminIds) {
            try {
                adminService.activateAdmin(id);
                activatedCount++;
            } catch (ResourceNotFoundException e) {
                // Continue with other admins
            }
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Activated " + activatedCount + " out of " + adminIds.size() + " admins");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bulk/deactivate")
    public ResponseEntity<Map<String, String>> bulkDeactivateAdmins(@RequestBody List<Long> adminIds) {
        int deactivatedCount = 0;
        for (Long id : adminIds) {
            try {
                adminService.deactivateAdmin(id);
                deactivatedCount++;
            } catch (ResourceNotFoundException e) {
                // Continue with other admins
            }
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Deactivated " + deactivatedCount + " out of " + adminIds.size() + " admins");
        return ResponseEntity.ok(response);
    }
} 