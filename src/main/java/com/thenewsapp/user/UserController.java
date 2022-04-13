package com.thenewsapp.user;

import com.thenewsapp.news.News;
import com.thenewsapp.provider.JwtProvider;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.ResponseEntity.ok;

@Data
@Getter
@Setter
class AuthData{
    private String username;
    private String password;
}

@RestController
@RequestMapping(path="/user")
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtProvider jwtTokenProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping(path = "/signup")
    public ResponseEntity<String> signup(@RequestBody User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ok("Success");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthData data) {
        try {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username);
            Map<String, String> model = new HashMap<>();
            model.put("Username", username);
            model.put("Token", token);
            return ok(model);

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        return ok(userRepository.findByUsername(username).get());
    }

    @GetMapping("/{username}/saved")
    public  ResponseEntity<Set<News>> getNews(@PathVariable String username){
        return ok(userRepository.findByUsername(username).get().getNews());
    }

    @DeleteMapping("/{username}")
    @Transactional
    public void deleteUser(@PathVariable String username){
        userRepository.deleteByUsername(username);
    }
}


