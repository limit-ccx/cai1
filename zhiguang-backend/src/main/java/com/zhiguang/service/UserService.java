package com.zhiguang.service;

import com.zhiguang.dto.AuthResponseDto;
import com.zhiguang.dto.LoginRequestDto;
import com.zhiguang.dto.RegisterRequestDto;
import com.zhiguang.dto.UserUpdateDto;
import com.zhiguang.entity.User;
import com.zhiguang.exception.BadRequestException;
import com.zhiguang.exception.ResourceNotFoundException;
import com.zhiguang.repository.ArticleRepository;
import com.zhiguang.repository.UserFollowRepository;
import com.zhiguang.repository.UserRepository;
import com.zhiguang.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final UserFollowRepository userFollowRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("用户名已存在");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");

        user = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return buildAuthResponse(token, user);
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("邮箱或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("邮箱或密码错误");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return buildAuthResponse(token, user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "username", username));
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateDto updateDto) {
        User user = getUserById(userId);

        if (updateDto.getUsername() != null && !updateDto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updateDto.getUsername())) {
                throw new BadRequestException("用户名已存在");
            }
            user.setUsername(updateDto.getUsername());
        }

        if (updateDto.getAvatar() != null) {
            user.setAvatar(updateDto.getAvatar());
        }

        if (updateDto.getBio() != null) {
            user.setBio(updateDto.getBio());
        }

        return userRepository.save(user);
    }

    public AuthResponseDto.UserDto getUserDto(Long userId) {
        User user = getUserById(userId);
        return buildUserDto(user);
    }

    public Map<String, Object> getUserStats(Long userId) {
        long articleCount = articleRepository.countByAuthorIdAndStatus(userId, 2);
        long followCount = userFollowRepository.countByFollowerId(userId);
        long fansCount = userFollowRepository.countByFollowingId(userId);

        return Map.of(
                "articleCount", articleCount,
                "followCount", followCount,
                "fansCount", fansCount
        );
    }

    private AuthResponseDto buildAuthResponse(String token, User user) {
        return new AuthResponseDto(token, buildUserDto(user));
    }

    public AuthResponseDto.UserDto buildUserDto(User user) {
        return new AuthResponseDto.UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatar(),
                user.getBio()
        );
    }
}