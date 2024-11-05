package com.example.nnprorocnikovyprojekt.services;


import com.example.nnprorocnikovyprojekt.dtos.user.RegistrationDto;
import com.example.nnprorocnikovyprojekt.entity.ResetToken;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.entity.VerificationCode;
import com.example.nnprorocnikovyprojekt.repositories.ResetTokenRepository;
import com.example.nnprorocnikovyprojekt.repositories.UserRepository;
import com.example.nnprorocnikovyprojekt.repositories.VerificationCodeRepository;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private ModelMapper modelMapper;

    private SecureRandom secureRandom = new SecureRandom();
    private Integer RANDOM_BOUND = 999999;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username).orElse(null);
    }

    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username).orElse(null);
    }

    public User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getUserByUsername(authentication.getName());
        return user;
    }

    public ResetToken getResetTokenByValue(String resetTokenValue){
        return resetTokenRepository.getResetTokenByToken(resetTokenValue).orElseThrow(() -> new RuntimeException("Nebyl nalezen token"));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deactivateUserResetTokens(User user){
        user.getResetTokens().forEach(resetToken -> {
            resetToken.setValid(false);
            resetTokenRepository.save(resetToken);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void deactivateUserVerificationTokens(User user){
        user.getVerificationCodes().forEach(verificationCode -> {
            verificationCode.setValid(false);
            verificationCodeRepository.save(verificationCode);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean registerUser(RegistrationDto registrationRequest) {
        boolean alreadyExists = userRepository.getUserByUsername(registrationRequest.getUsername()).isPresent();
        if(alreadyExists) return false;
        //TODO zde dat i salt
        else return userRepository.save(new User(registrationRequest.getUsername(), encryptPassword(registrationRequest.getPassword()), registrationRequest.getEmail()))
                .getUserId() != null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String newPassword, User user) {
        user.setPassword(encryptPassword(newPassword));
        saveUser(user);
    }

    public String encryptPassword(String password){
        return passwordEncoder.encode(password);
    }

    public boolean userPasswordMatches(String password, User user){
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Transactional(rollbackFor = Exception.class)
    public VerificationCode generateVerificationCodeForUser(User user){
        deactivateUserVerificationTokens(user);
        String verificationCodeValue = Integer.toString(secureRandom.nextInt(RANDOM_BOUND));
        verificationCodeValue = StringUtils.leftPad(verificationCodeValue, 6, "0");
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(5);
        VerificationCode verificationCode = new VerificationCode(verificationCodeValue, expirationDate, user);
        user.getVerificationCodes().add(verificationCode);
        userRepository.save(user);
        return verificationCode;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResetToken generateResetTokenForUser(User user) {
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        deactivateUserResetTokens(user);
        ResetToken resetToken = new ResetToken(user, token);
        user.getResetTokens().add(resetToken);
        saveResetToken(resetToken);
        return resetToken;
    }

    public void saveResetToken(ResetToken resetToken){
        resetTokenRepository.save(resetToken);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean verifyVerificationCode(String username, String verificationCodeValue) {
        if(verificationCodeValue.length() < Integer.toString(RANDOM_BOUND).length()) return false;
        User user = getUserByUsername(username);
        if(user == null) return false;

        List<VerificationCode> verificationCode = verificationCodeRepository.findValidVerificationCodes(user, verificationCodeValue);
        LocalDateTime now = LocalDateTime.now();

        boolean nonExpiredValidCodeExists = verificationCode.stream().anyMatch(verificationCode1 -> verificationCode1.getExpirationDate().isAfter(now));
        if(nonExpiredValidCodeExists) {
            deactivateUserVerificationTokens(user);
            return true;
        }
        return false;
    }


    //TODO zkopirovane -> bude vubec potreba?
    /*
    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
    */
}
