package com.example.nnprorocnikovyprojekt.services;


import com.example.nnprorocnikovyprojekt.Utility.Utils;
import com.example.nnprorocnikovyprojekt.advice.exceptions.NotFoundException;
import com.example.nnprorocnikovyprojekt.advice.exceptions.UnauthorizedException;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoRequest;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoResponse;
import com.example.nnprorocnikovyprojekt.dtos.user.*;
import com.example.nnprorocnikovyprojekt.entity.*;
import com.example.nnprorocnikovyprojekt.external.CaptchaService;
import com.example.nnprorocnikovyprojekt.repositories.*;
import com.example.nnprorocnikovyprojekt.security.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private CaptchaService captchaService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private ObjectMapper objectMapper;
    private SecureRandom secureRandom = new SecureRandom();
    private Integer RANDOM_BOUND = 999999;

    public void setCaptchaService(CaptchaService captchaService){
        this.captchaService = captchaService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username).orElse(null);
    }

    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username).orElse(null);
    }

    public User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUserByUsername(authentication.getName());
    }

    public ResetToken getResetTokenByUsernameAndValue(User user, String resetTokenValue){
        return resetTokenRepository.getResetTokenByUserAndToken(user, resetTokenValue).orElseThrow(() -> new RuntimeException("Reset token was not found"));
    }

    public void newPassword(NewPasswordDto resetPasswordRequest){
        validateCaptcha(resetPasswordRequest.getCaptchaToken());
        User user = getUserByUsername(resetPasswordRequest.getUsername());
        ResetToken resetToken = getResetTokenByUsernameAndValue(user, resetPasswordRequest.getToken());

        if(resetToken == null) throw new UnauthorizedException("Reset token was not found");

        boolean resetTokenIsValid = resetToken.isValid() && Instant.now().isBefore(resetToken.getExpirationDate());
        if(resetTokenIsValid){
            changePassword(resetPasswordRequest.getPassword(), user);
            saveUser(user);
        } else {
            throw new UnauthorizedException("Reset token is no longer valid");
        }
        resetToken.setValid(false);
        saveResetToken(resetToken);
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
    public void deactivateUserAuthenticationTokens(User user){
        user.getAuthTokens().forEach(authToken -> {
            authToken.setValid(false);
            authTokenRepository.save(authToken);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean registerUser(RegistrationDto registrationRequest) {
        validateCaptcha(registrationRequest.getCaptchaToken());

        boolean alreadyExists = userRepository.getUserByUsername(registrationRequest.getUsername()).isPresent();
        if(alreadyExists) throw new UnauthorizedException("User already exists");
        User user = new User(registrationRequest.getUsername(), encryptPassword(registrationRequest.getPassword()), registrationRequest.getEmail());
        Authority authority = authorityRepository.getAuthorityByAuthorityName("USER");
        user.addAuthority(authority);
        User savedUser = userRepository.save(user);
        return savedUser.getUserId() != null;
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

    @PreAuthorize("hasRole('ADMIN')")
    public void addAdminToUser(UsernameDto usernameDto) {
        User userAddingAdminRole = getUserFromContext();
        User userToAddAdminTo = getUserByUsername(usernameDto.getUsername());
        if(userToAddAdminTo == null || userAddingAdminRole == null) throw new RuntimeException("Target user is null");
        if(userToAddAdminTo.containsAuthority("ADMIN")) throw new RuntimeException("User is already admin");

        userToAddAdminTo.addAuthority(authorityRepository.getAuthorityByAuthorityName("ADMIN"));
        saveUser(userToAddAdminTo);
        logger.debug("User " + userAddingAdminRole.getUsername() + " added admin role to " + usernameDto.getUsername());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void removeAdminFromUser(UsernameDto usernameDto) {
        User userAddingAdminRole = getUserFromContext();
        User userToRemoveAdminFrom = getUserByUsername(usernameDto.getUsername());
        if(userToRemoveAdminFrom == null || userAddingAdminRole == null) throw new RuntimeException("Target user is null");
        if(!userToRemoveAdminFrom.containsAuthority("ADMIN")) throw new RuntimeException("User is NOT admin");

        boolean removed = userToRemoveAdminFrom.removeAuthority("ADMIN");
        if(!removed) throw new RuntimeException("Failed to remove role ADMIN");
        saveUser(userToRemoveAdminFrom);
        logger.debug("User " + userAddingAdminRole.getUsername() + " removed admin role from " + usernameDto.getUsername());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void banUser(UsernameDto usernameDto) {
        User userAddingAdminRole = getUserFromContext();
        User userToBan = getUserByUsername(usernameDto.getUsername());
        if(userToBan == null || userAddingAdminRole == null) throw new RuntimeException("Target user is null");
        if(!userToBan.isEnabled()) throw new RuntimeException("User is already banned");

        userToBan.setBanned(true);
        saveUser(userToBan);
        logger.debug("User " + userAddingAdminRole.getUsername() + " banned " + usernameDto.getUsername());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void unbanUser(UsernameDto usernameDto) {
        User userAddingAdminRole = getUserFromContext();
        User userToBan = getUserByUsername(usernameDto.getUsername());
        if(userToBan == null || userAddingAdminRole == null) throw new RuntimeException("Target user is null");
        if(userToBan.isEnabled()) throw new RuntimeException("User is NOT banned");

        userToBan.setBanned(false);
        saveUser(userToBan);
        logger.debug("User " + userAddingAdminRole.getUsername() + " unbanned " + usernameDto.getUsername());
    }

    @Transactional(rollbackFor = Exception.class)
    public VerificationCode generateVerificationCodeForUser(User user){
        deactivateUserVerificationTokens(user);
        String verificationCodeValue = Integer.toString(secureRandom.nextInt(RANDOM_BOUND));
        verificationCodeValue = StringUtils.leftPad(verificationCodeValue, 6, "0");
        Instant expirationDate = Instant.now().plusSeconds(5 * 60);
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

    public User saveUser(User user){
        return userRepository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean verifyVerificationCode(String username, String verificationCodeValue) {
        if(verificationCodeValue.length() < Integer.toString(RANDOM_BOUND).length()) return false;
        User user = getUserByUsername(username);
        if(user == null) return false;

        List<VerificationCode> verificationCode = verificationCodeRepository.findValidVerificationCodes(user, verificationCodeValue);
        Instant now = Instant.now();

        boolean nonExpiredValidCodeExists = verificationCode.stream().anyMatch(verificationCode1 -> verificationCode1.getExpirationDate().isAfter(now));
        if(nonExpiredValidCodeExists) {
            deactivateUserVerificationTokens(user);
            return true;
        }
        return false;
    }

    public UserDto updateUser(UpdateUserDto updateUserDto) throws JsonProcessingException {
        User user = getUserFromContext();
        if(!userPasswordMatches(updateUserDto.getConfirmationPassword(), user)) {
            throw new UnauthorizedException("Password does not match");
        }
        if(updateUserDto.getEmail() != null || updateUserDto.getPassword() != null || updateUserDto.getPublicKey() != null){
            if(updateUserDto.getEmail() != null) user.setEmail(updateUserDto.getEmail());
            if(updateUserDto.getPassword() != null) user.setPassword(encryptPassword(updateUserDto.getPassword()));
            if(updateUserDto.getPublicKey() != null) {
                user.getActivePublicKey().ifPresent(publicKey -> publicKey.setValid(false));
                user.getPublicKeys().add(new PublicKey(objectMapper.writeValueAsString(updateUserDto.getPublicKey()), Instant.now(), true, user));
            }
            user = saveUser(user);
        }
        return userToUserDto(user);
    }

    public void addContact(AddRemoveContactDto addRemoveContactDto) {
        User user = getUserFromContext();
        User contact = getUserByUsername(addRemoveContactDto.getUsername());

        if(contact == null) throw new NotFoundException("Contact is null");
        if(user.getContacts().contains(contact)) throw new RuntimeException("User has already added this contact.");

        user.getContacts().add(contact);
        saveUser(user);
    }

    public UserPageResponseDto listContacts(SearchUserDtoRequest searchUserDtoRequest) {
        User user = getUserFromContext();
        PageInfoDtoRequest pageInfo = searchUserDtoRequest.getPageInfo();
        List<User> contactsFiltered = user.getContacts().stream()
                .filter(contact -> contact.getUsername().startsWith(searchUserDtoRequest.getUsername())).collect(Collectors.toList());
        List<User> contactsFilteredPaged = Utils.getPage(contactsFiltered, pageInfo.getPageIndex(), pageInfo.getPageSize());
        return contactsToContactsPageResponseDtos(contactsFilteredPaged, pageInfo, user.getContacts().size());
    }

    private UserPageResponseDto contactsToContactsPageResponseDtos(List<User> contacts, PageInfoDtoRequest pageInfo, Integer total){
        List<UserDto> userDtos = contacts.stream()
                .map(this::userToUserDto)
                .collect(Collectors.toList());

        UserPageResponseDto userPageResponseDto = new UserPageResponseDto();
        userPageResponseDto.setItemList(userDtos);
        userPageResponseDto.setPageInfoDto(new PageInfoDtoResponse(pageInfo.getPageSize(), pageInfo.getPageIndex(), (long)total));
        return userPageResponseDto;
    }

    public List<UserDto> usersToUserDtos(List<User> users) {
        return users.stream()
                .map(this::userToUserDto)
                .collect(Collectors.toList());
    }

    private UserDto userToUserDto(User user) {
        String publicKeyString = user.getActivePublicKey().isPresent()? user.getActivePublicKey().get().getKeyValue() : null;
        try {
            PublicKeyDto publicKeyDto;
            if(publicKeyString == null) {
                publicKeyDto = null;
            } else {
                publicKeyDto = objectMapper.readValue(publicKeyString, PublicKeyDto.class);
            }
            List<String> authorities = authoritiesToStringListOfAuthorities(user.getAuthoritiesAsAuthority());
            return new UserDto(user.getUsername(), user.getEmail(), publicKeyDto, authorities);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not parse key");
        }
    }

    private List<String> authoritiesToStringListOfAuthorities(List<Authority> authorities) {
       List<String> authorityDtos = authorities.stream()
               .map(authority -> authority.getAuthorityName())
               .collect(Collectors.toList());
       return authorityDtos;
    }

    private ContactDto userToContactDto(User user, User contact) {
        String publicKeyString = contact.getActivePublicKey().isPresent()? contact.getActivePublicKey().get().getKeyValue() : null;
        try {
            PublicKeyDto publicKeyDto;
            if(publicKeyString == null) {
                publicKeyDto = null;
            } else {
                publicKeyDto = objectMapper.readValue(publicKeyString, PublicKeyDto.class);
            }
            List<String> authorityDtos = authoritiesToStringListOfAuthorities(contact.getAuthoritiesAsAuthority());
            return new ContactDto(contact.getUsername(), contact.getEmail(), publicKeyDto, user.getContacts().contains(contact), authorityDtos);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not parse key");
        }
    }

    public UserDto getUserData() {
        User user = getUserFromContext();
        return userToUserDto(user);
    }

    public ContactPageResponseDto searchUsers(SearchUserDtoRequest searchUserDtoRequest) {
        User user = getUserFromContext();
        Pageable pageInfo = PageRequest.of(searchUserDtoRequest.getPageInfo().getPageIndex(), searchUserDtoRequest.getPageInfo().getPageSize()).withSort(Sort.Direction.ASC, "userId");
        Page<User> usersPage = null;
        if(searchUserDtoRequest.getAuthorities() == null || searchUserDtoRequest.getAuthorities().isEmpty()) {
            usersPage = userRepository.findUsersByUsernameStartingWithAndUsernameNot(searchUserDtoRequest.getUsername(), user.getUsername(), pageInfo);
        } else {
            List<Authority> authorities = authorityDtosToAuthorities(searchUserDtoRequest.getAuthorities());
            usersPage = userRepository.findUsersByUsernameStartingWithAndUsernameNotAndAuthoritiesIn(searchUserDtoRequest.getUsername(), user.getUsername(), authorities, pageInfo);
        }
        return usersToUserPageResponseDto(usersPage, user);
    }

    private List<Authority> authorityDtosToAuthorities(List<String> authorityDtos) {
        List<Authority> authorities = authorityDtos.stream()
                .map(authorityDto -> authorityRepository.getAuthorityByAuthorityName(authorityDto))
                .collect(Collectors.toList());
        return authorities;
    }

    private ContactPageResponseDto usersToUserPageResponseDto(Page<User> page, User user){
        if(page == null) return null;
        List<ContactDto> contactDtos = page.getContent().stream()
                .map(contact -> userToContactDto(user, contact)).collect(Collectors.toList());

        ContactPageResponseDto contactPageResponseDto = new ContactPageResponseDto();
        contactPageResponseDto.setItemList(contactDtos);
        contactPageResponseDto.setPageInfoDto(new PageInfoDtoResponse(page.getSize(), page.getSize(), page.getTotalElements()));
        return contactPageResponseDto;
    }

    private ListBannedUnbannedUsersDto usersToListBannedUnbannedUsersDto(Page<User> page){
        if(page == null) return null;
        List<UsernameDto> usernameDtos = page.getContent().stream().map(this::userToUsernameDto).collect(Collectors.toList());

        ListBannedUnbannedUsersDto listBannedUnbannedUsersDto = new ListBannedUnbannedUsersDto();
        listBannedUnbannedUsersDto.setItemList(usernameDtos);
        listBannedUnbannedUsersDto.setPageInfoDto(new PageInfoDtoResponse(page.getSize(), page.getSize(), page.getTotalElements()));
        return listBannedUnbannedUsersDto;
    }

    private UsernameDto userToUsernameDto(User user) {
        return new UsernameDto(user.getUsername());
    }

    @Transactional(rollbackFor = Exception.class)
    public JwtTokenDto verify2FaAndGetJwtToken(VerificationDto verificationDto) {
        validateCaptcha(verificationDto.getCaptchaToken());

        boolean verificationCodeMatches = verifyVerificationCode(verificationDto.getUsername(), verificationDto.getVerificationCode());

        if(verificationCodeMatches) {
            User user = getUserByUsername(verificationDto.getUsername());
            AuthToken jwtToken = jwtService.generateToken(user);
            JwtTokenDto jwtTokenDto = new JwtTokenDto();
            jwtTokenDto.setJwtToken(jwtToken.getToken());
            return jwtTokenDto;
        } else {
            throw new UnauthorizedException("Verification code does not match");
        }
    }

    public ExpirationDateDto loginUser(LoginDto authRequest) {
        validateCaptcha(authRequest.getCaptchaToken());

        Authentication authentication = applicationContext.getBean(AuthenticationManager.class).authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            User user = getUserByUsername(authRequest.getUsername());
            VerificationCode verificationCode = emailService.sendVerificationCodeEmail(user);
            if(verificationCode != null) {
                ExpirationDateDto expirationDateDto = new ExpirationDateDto();
                expirationDateDto.setExpirationDate(verificationCode.getExpirationDate());
                return expirationDateDto;
            } else {
                throw new RuntimeException("Failed to send verification code");
            }
        } else {
            throw new UnauthorizedException("Failed to authenticate user");
        }
    }

    public void removeContact(AddRemoveContactDto addRemoveContactDto) {
        User user = getUserFromContext();
        User contact = userRepository.getUserByUsername(addRemoveContactDto.getUsername()).orElseThrow(() -> new RuntimeException("Contact does not exist"));
        boolean removeSucceeded = user.getContacts().remove(contact);
        if(!removeSucceeded) {
            throw new RuntimeException("Failed to remove contact -> contact was not in list of user contacts");
        }
        saveUser(user);
    }

    public void sendResetPassword(ResetPasswordDto authRequest) {
        validateCaptcha(authRequest.getCaptchaToken());

        User user = getUserByUsername(authRequest.getUsername());
        if(user == null) throw new NotFoundException("User not found");
        emailService.sendResetTokenEmail(user);
    }

    private void validateCaptcha(String captchaToken) {
        boolean captchaIsValid = captchaService.validateCaptcha(captchaToken);
        if(!captchaIsValid) {
            throw new UnauthorizedException("Captcha is not valid");
        }
    }

    public ListBannedUnbannedUsersDto listBannedUsers(SearchUserDtoRequest searchUserDtoRequest) {
        Pageable pageInfo = PageRequest.of(searchUserDtoRequest.getPageInfo().getPageIndex(), searchUserDtoRequest.getPageInfo().getPageSize()).withSort(Sort.Direction.ASC, "userId");
        Page<User> usersPage = userRepository.getBannedUsers(searchUserDtoRequest.getUsername(), pageInfo);
        return usersToListBannedUnbannedUsersDto(usersPage);
    }

    public ListBannedUnbannedUsersDto listNotBannedUsers(SearchUserDtoRequest searchUserDtoRequest) {
        Pageable pageInfo = PageRequest.of(searchUserDtoRequest.getPageInfo().getPageIndex(), searchUserDtoRequest.getPageInfo().getPageSize()).withSort(Sort.Direction.ASC, "userId");
        Page<User> usersPage = userRepository.getNotBannedUsers(searchUserDtoRequest.getUsername(), pageInfo);
        return usersToListBannedUnbannedUsersDto(usersPage);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addAuthTokenAndInvalidateOthers(User user, AuthToken authToken) {
        deactivateUserAuthenticationTokens(user);
        user.getAuthTokens().add(authToken);
        saveUser(user);
    }

    public void logout() {
        User user = getUserFromContext();
        user.getAuthTokens().stream()
                .filter(AuthToken::isValid)
                .forEach(authToken -> authToken.setValid(false));
        saveUser(user);
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
