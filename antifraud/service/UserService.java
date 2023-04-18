package antifraud.service;

import antifraud.entity.User;
import antifraud.entity.request.Access;
import antifraud.entity.request.Role;
import antifraud.entity.response.Status;
import antifraud.entity.response.UserDeleted;
import antifraud.repository.UserRepository;
import antifraud.security.BCryptEncoderConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private BCryptEncoderConfig bCrypt;

    public UserService(UserRepository userRepository, BCryptEncoderConfig bCrypt) {
        this.userRepository = userRepository;
        this.bCrypt = bCrypt;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetailsImpl(userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }

    @Transactional
    public ResponseEntity<?> save(User user) {
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User already exists!");
        }

        if (!userRepository.existsByRole("ADMINISTRATOR")) {
            user.setRole("ADMINISTRATOR");
            user.setAccountNonLocked(true);
        } else {
            user.setRole("MERCHANT");
            user.setAccountNonLocked(false);
        }

        user.setPassword(bCrypt.passwordEncoder().encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    public ResponseEntity<List<User>> getListOfAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @Transactional
    public ResponseEntity<?> deleteUser(String username) {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);
        }
        if (user.get().getRole().equals("ADMINISTRATOR")) {
            return new ResponseEntity<>("You can't delete administrator!", HttpStatus.BAD_REQUEST);
        }
        username = user.get().getUsername();
        userRepository.delete(user.get());
        return new ResponseEntity<>(new UserDeleted(username, "Deleted successfully!"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> updateAccess(Access access) {
        Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(access.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        if (user.getRole().equals("ADMINISTRATOR")) {
            return new ResponseEntity<>("You can't change access for administrator!", HttpStatus.BAD_REQUEST);
        }

        if (access.getOperation().equals("UNLOCK")) {
            user.setAccountNonLocked(true);
            userRepository.save(user);

            return ResponseEntity.ok(new Status("User " + user.getUsername() + " unlocked!"));
        } else if (access.getOperation().equals("LOCK")) {
            user.setAccountNonLocked(false);
            userRepository.save(user);

            return ResponseEntity.ok(new Status("User " + user.getUsername() + " locked!"));
        }

        return ResponseEntity.badRequest().body("Access not found!");
    }


    @Transactional
    public ResponseEntity<?> updateRole(Role role) {
        Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(role.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        if (user.getRole().equals(role.getRole())) {
            return new ResponseEntity<>("User already has this role!", HttpStatus.CONFLICT);
        }

        user.setRole(role.getRole());
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}