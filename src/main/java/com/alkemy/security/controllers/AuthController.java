package com.alkemy.security.controllers;

import com.alkemy.security.dto.JwtDTO;
import com.alkemy.security.dto.LoginUserDTO;
import com.alkemy.security.dto.NewUserDTO;
import com.alkemy.security.entities.Role;
import com.alkemy.security.entities.User;
import com.alkemy.security.enums.RoleName;
import com.alkemy.security.jwt.JwtProvider;
import com.alkemy.security.jwt.JwtReturnReq;
import com.alkemy.security.services.IRoleService;
import com.alkemy.security.services.IUserService;
import com.alkemy.security.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value ="/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IRoleService iRoleService;

    @Autowired
    private JwtReturnReq jwtReturnReq;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private Optional<User> user;

    private Authentication authentication;

    @PostMapping(value = "/register")
    public ResponseEntity<JwtDTO> register(@Valid @RequestBody NewUserDTO newUserDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Message("Invalid fields or email"), HttpStatus.BAD_REQUEST);
        if(iUserService.existsByUsername(newUserDTO.getUsername()))
            return new ResponseEntity(new Message("Existing username"), HttpStatus.BAD_REQUEST);
        if(iUserService.existsByEmail(newUserDTO.getEmail()))
            return new ResponseEntity(new Message("Existing email"), HttpStatus.BAD_REQUEST);

        User user = new User(newUserDTO.getFullname(), newUserDTO.getEmail(), newUserDTO.getUsername(),
                passwordEncoder.encode(newUserDTO.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(iRoleService.getByRoleName(RoleName.ROLE_USER).get());
        if(newUserDTO.getRoles().contains("admin"))
            roles.add(iRoleService.getByRoleName(RoleName.ROLE_ADMIN).get());
        user.setRoles(roles);
        iUserService.save(user);

        authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(newUserDTO.getUsername(), newUserDTO.getPassword()));
        JwtDTO jwtDTO = jwtReturnReq.getTokenRequest(authentication);

        return new ResponseEntity(jwtDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<JwtDTO> login(@Valid @RequestBody LoginUserDTO loginUserDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Message("Invalid fields or email"), HttpStatus.BAD_REQUEST);

        if(loginUserDTO.getEmail() != null && loginUserDTO.getUsername() == null) {
            user = iUserService.getByEmail(loginUserDTO.getEmail());

            if(!iUserService.existsByEmail(loginUserDTO.getEmail()) || !passwordEncoder.matches(loginUserDTO.getPassword(), user.get().getPassword()))
                return new ResponseEntity(new Message("Email or Password incorrect"), HttpStatus.BAD_REQUEST);
            else
                authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUserDTO.getEmail(), loginUserDTO.getPassword()));
        } else if(loginUserDTO.getEmail() == null && loginUserDTO.getUsername() == null) {
            return new ResponseEntity(new Message("Email required"), HttpStatus.BAD_REQUEST);
        }

        if(loginUserDTO.getUsername() != null && loginUserDTO.getEmail() == null) {
            user = iUserService.getByUsername(loginUserDTO.getUsername());

            if(!iUserService.existsByUsername(loginUserDTO.getUsername()) || !passwordEncoder.matches(loginUserDTO.getPassword(), user.get().getPassword()))
                return new ResponseEntity(new Message("Username or Password incorrect"), HttpStatus.BAD_REQUEST);
            else
                authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword()));
        } else if(loginUserDTO.getEmail() == null && loginUserDTO.getUsername() == null){
            return new ResponseEntity(new Message("Username required"), HttpStatus.BAD_REQUEST);
        }

        JwtDTO jwtDTO = jwtReturnReq.getTokenRequest(authentication);

        return new ResponseEntity(jwtDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<JwtDTO> refresh(@Valid @RequestBody JwtDTO jwtDTO) throws ParseException {
        String token = jwtProvider.refreshToken(jwtDTO);
        JwtDTO jwt = new JwtDTO(token);
        return new ResponseEntity(jwt, HttpStatus.OK);
    }
}