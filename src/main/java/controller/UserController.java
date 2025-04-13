package controller;

import domain.User;
import io.swagger.v3.oas.annotations.Operation; // Swagger anotace
import io.swagger.v3.oas.annotations.tags.Tag;   // Swagger anotace
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users") // Base path pro user endpointy
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users") // Swagger Tag
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // V reálu zde použít DTO a validaci (@Valid)
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing user")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        // V reálu zde použít DTO a validaci
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // Status 204 No Content
    }
}
