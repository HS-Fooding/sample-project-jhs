package hansung.com.sample_project.service;

import hansung.com.sample_project.domain.User;
import hansung.com.sample_project.dto.SignUpRequest;
import hansung.com.sample_project.exception.UserEmailAlreadyExistsException;
import hansung.com.sample_project.exception.UserIdExistsException;
import hansung.com.sample_project.exception.UserNickNameExistsException;
import hansung.com.sample_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 단일 회원 조회
    public User findUser(Long userId) {
        return userRepository.findById(userId);
    }

    // 전체 회원 조회
    public List<User> findUsers() {
        return userRepository.findAll();
    }

    // 회원 가입
    @Transactional
    public void join(SignUpRequest req)
            throws UserIdExistsException, UserEmailAlreadyExistsException, UserNickNameExistsException {
        validateSignUpInfo(req);
        User user = new User(req);
        String encryptedPassword = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encryptedPassword);

        userRepository.save(user);
    }

    public void validateSignUpInfo(SignUpRequest req)
            throws UserEmailAlreadyExistsException, UserNickNameExistsException, UserIdExistsException {
        if(userRepository.existByEmail(req.getEmail()))
            throw new UserEmailAlreadyExistsException(req.getEmail());
        if(userRepository.existByNickName(req.getNickName()))
            throw new UserNickNameExistsException(req.getNickName());
        if(userRepository.existByUserId(req.getUserId()))
            throw new UserIdExistsException(req.getUserId());
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        List<User> userTmp = userRepository.findByUserId(userId);
        User user = userTmp.get(0);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(String.valueOf(user.getRole())));

        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getUserPassword(), authorities);
    }

    // 로그인

}
