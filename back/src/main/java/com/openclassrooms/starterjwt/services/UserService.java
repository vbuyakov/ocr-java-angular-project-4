package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public void delete(Long id, String currentUserEmail) {
        User user = findById(id);
        
        if (!Objects.equals(currentUserEmail, user.getEmail())) {
            throw new BadRequestException("Unauthorized: You can only delete your own account");
        }
        
        userRepository.deleteById(id);
    }
}
