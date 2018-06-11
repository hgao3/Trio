package edu.bu.controller;

import com.example.jpa.exception.ResourceNotFoundException;
import com.example.jpa.model.Comment;
import com.example.jpa.repository.CommentRepository;
import com.example.jpa.repository.userRepository;
import edu.bu.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @PostMapping("/user")
    public User createPost(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String first_name,
                           @RequestParam String last_name) {
        User user = new User(first_name, last_name, email, password, false);
        return userRepository.save(user);
    }

    @PutMapping("/user/{user_id}")
    public User getUserById(@PathVariable(value = "user_id") Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

}