package com.relief.service;

import com.relief.dto.AdminStatsResponse;
import com.relief.dto.UserManagementRequest;
import com.relief.entity.User;
import com.relief.repository.UserRepository;
import com.relief.repository.NeedsRequestRepository;
import com.relief.repository.TaskRepository;
import com.relief.repository.InventoryStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NeedsRequestRepository needsRequestRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private InventoryStockRepository inventoryStockRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private boolean maintenanceMode = false;
    
    public AdminStatsResponse getSystemStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActiveTrue();
        long totalNeeds = needsRequestRepository.count();
        long activeNeeds = needsRequestRepository.countByStatus("active");
        long completedTasks = taskRepository.countByStatus("delivered");
        long pendingTasks = taskRepository.countByStatusIn("new", "assigned", "picked_up");
        long totalInventoryItems = inventoryStockRepository.count();
        long lowStockItems = inventoryStockRepository.countByQtyAvailableLessThan(10);
        
        Map<String, Long> usersByRole = userRepository.countByRole();
        Map<String, Long> needsByCategory = needsRequestRepository.countByCategory();
        Map<String, Long> tasksByStatus = taskRepository.countByStatus();
        
        return new AdminStatsResponse(
            totalUsers, activeUsers, totalNeeds, activeNeeds,
            completedTasks, pendingTasks, totalInventoryItems,
            lowStockItems, usersByRole, needsByCategory, tasksByStatus
        );
    }
    
    public Page<User> getAllUsers(String search, String role, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            if (role != null && !role.trim().isEmpty()) {
                return userRepository.findBySearchTermAndRole(search, role, pageable);
            } else {
                return userRepository.findBySearchTerm(search, pageable);
            }
        } else if (role != null && !role.trim().isEmpty()) {
            return userRepository.findByRole(role, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }
    
    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User createUser(UserManagementRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setActive(request.isActive());
        user.setAddress(request.getAddress());
        user.setNotes(request.getNotes());
        
        // Set password if provided, otherwise generate a temporary one
        String password = request.getPassword();
        if (password == null || password.trim().isEmpty()) {
            password = generateTemporaryPassword();
        }
        user.setPassword(passwordEncoder.encode(password));
        
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User updateUser(String id, UserManagementRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setActive(request.isActive());
        user.setAddress(request.getAddress());
        user.setNotes(request.getNotes());
        user.setUpdatedAt(LocalDateTime.now());
        
        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        return userRepository.save(user);
    }
    
    public boolean deleteUser(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        userRepository.delete(userOpt.get());
        return true;
    }
    
    public User updateUserRole(String id, String newRole) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User activateUser(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User deactivateUser(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Database health
        try {
            userRepository.count();
            health.put("database", "UP");
        } catch (Exception e) {
            health.put("database", "DOWN");
        }
        
        // System metrics
        health.put("maintenance_mode", maintenanceMode);
        health.put("timestamp", LocalDateTime.now());
        health.put("uptime", getUptime());
        
        return health;
    }
    
    public void setMaintenanceMode(boolean enabled) {
        this.maintenanceMode = enabled;
    }
    
    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }
    
    private String generateTemporaryPassword() {
        // Generate a random 8-character password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return password.toString();
    }
    
    private String getUptime() {
        // This would typically come from system metrics
        return "24h 15m 30s"; // Mock uptime
    }
}



