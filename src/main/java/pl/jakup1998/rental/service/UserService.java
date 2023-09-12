package pl.jakup1998.rental.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.jakup1998.rental.dto.UserDto;
import pl.jakup1998.rental.mapper.CustomUserMapper;
import pl.jakup1998.rental.model.Role;
import pl.jakup1998.rental.model.User;
import pl.jakup1998.rental.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private CustomUserMapper customUserMapper;

    public UserService(UserRepository userRepository, CustomUserMapper customUserMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.customUserMapper = customUserMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Nie znaleziono użytkownika o podanej nazwie: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    public UserDto save(UserDto userDto) {
        User user = customUserMapper.convertToEntity(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return customUserMapper.convertToDto(savedUser);
    }

    public UserDto findByUsernameDto(String username) {
        User user = userRepository.findByUsername(username);
        return customUserMapper.convertToDto(user);
    }
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return user;
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}