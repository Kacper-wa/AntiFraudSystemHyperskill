package antifraud.controller;

import antifraud.entity.request.Access;
import antifraud.entity.request.Role;
import antifraud.entity.User;
import antifraud.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listUsers() {
        return userService.getListOfAllUsers();
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @PutMapping("/access")
    public ResponseEntity<?> updateAccess(@Valid @RequestBody Access access) {
        return userService.updateAccess(access);
    }

    @PutMapping("/role")
    public ResponseEntity<?> updateRole(@Valid @RequestBody Role role) {
        return userService.updateRole(role);
    }
}
