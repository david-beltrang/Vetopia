package com.vetopia.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Administrador;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Role;
import com.vetopia.entities.UserEntity;
import com.vetopia.entities.Veterinario;
import com.vetopia.repository.RoleRepository;
import com.vetopia.repository.UserRepository;

@Service 
public class CustomUserDetailService implements UserDetailsService {
    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override 
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        UserEntity userDB = userRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException("User not found")
        );

        UserDetails userDetails = new User(userDB.getUsername(),
            userDB.getPassword(), mapRolesToAuthorities(userDB.getRoles()));
        return userDetails;
    }

    // map roles to authorities
    private Collection<GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    public UserEntity DuenoToUser(Dueno dueno) {
        UserEntity user = new UserEntity();
        user.setUsername(dueno.getCorreo());
        user.setPassword(passwordEncoder.encode(dueno.getContrasena()));

        Role role = roleRepository.findByName("DUENO")
                .orElseThrow(() -> new IllegalStateException("No existe el rol DUENO"));
        user.setRoles(List.of(role));
        return userRepository.save(user);
    }

    public UserEntity VeterinarioToUser(Veterinario veterinario) {
        UserEntity user = new UserEntity();
        user.setUsername(veterinario.getCorreo());
        user.setPassword(passwordEncoder.encode(veterinario.getContrasena()));

        Role role = roleRepository.findByName("VETERINARIO")
                .orElseThrow(() -> new IllegalStateException("No existe el rol VETERINARIO"));
        user.setRoles(List.of(role));
        return userRepository.save(user);
    }

    public UserEntity AdministradorToUser(Administrador admin) {
        UserEntity user = new UserEntity();
        user.setUsername(admin.getCorreo());
        user.setPassword(passwordEncoder.encode(admin.getContrasena()));

        Role role = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("No existe el rol ADMIN"));
        user.setRoles(List.of(role));
        return userRepository.save(user);
    }
}
