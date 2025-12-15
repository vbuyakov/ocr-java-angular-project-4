package com.openclassrooms.starterjwt.security.jwt;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    // HS512 requires at least 512 bits (64 bytes) - this is 64 characters
    private static final String TEST_SECRET = "testSecretKeyForJwtTokenGenerationAndValidationTest12345678901234567890123456789012345678901234567890123456789012345678901234567890";
    private static final int TEST_EXPIRATION_MS = 86400000; // 24 hours

    private UserDetailsImpl userDetails;
    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", TEST_EXPIRATION_MS);

        // Capture log events and suppress console output
        Logger logger = (Logger) LoggerFactory.getLogger(JwtUtils.class);
        logger.detachAndStopAllAppenders(); // Remove console appender
        logger.setAdditive(false); // Prevent propagation to root logger
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
        logger.setLevel(ch.qos.logback.classic.Level.ALL); // Capture all levels

        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .build();
    }

    @Test
    void testGenerateJwtToken_Success() {
        // Given
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null);

        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    void testGetUserNameFromJwtToken_Success() {
        // Given
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Then
        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    void testValidateJwtToken_ValidToken() {
        // Given
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void testValidateJwtToken_InvalidSignature() {
        // Given - Create a token with a different secret key
        String differentSecret = "differentSecretKeyForJwtTokenGenerationAndValidationTest12345678901234567890123456789012345678901234567890123456789012345678901234567890";
        JwtUtils jwtUtilsWithDifferentSecret = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtilsWithDifferentSecret, "jwtSecret", differentSecret);
        ReflectionTestUtils.setField(jwtUtilsWithDifferentSecret, "jwtExpirationMs", TEST_EXPIRATION_MS);
        
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null);
        // Generate token with different secret
        String tokenWithDifferentSecret = jwtUtilsWithDifferentSecret.generateJwtToken(authentication);

        // When - Validate with original secret
        boolean isValid = jwtUtils.validateJwtToken(tokenWithDifferentSecret);

        // Then
        assertThat(isValid).isFalse();
        List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);
        assertThat(logEvents.get(0).getLevel().toString()).isEqualTo("ERROR");
        assertThat(logEvents.get(0).getMessage()).contains("Invalid JWT signature");
    }

    @Test
    void testValidateJwtToken_MalformedToken() {
        // Given
        String malformedToken = "not.a.valid.jwt.token";

        // When
        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        // Then
        assertThat(isValid).isFalse();
        List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);
        assertThat(logEvents.get(0).getLevel().toString()).isEqualTo("ERROR");
        assertThat(logEvents.get(0).getMessage()).contains("Invalid JWT token");
    }

    @Test
    void testValidateJwtToken_EmptyToken() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtUtils.validateJwtToken(emptyToken);

        // Then
        assertThat(isValid).isFalse();
        List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);
        assertThat(logEvents.get(0).getLevel().toString()).isEqualTo("ERROR");
        assertThat(logEvents.get(0).getMessage()).contains("JWT claims string is empty");
    }

    @Test
    void testValidateJwtToken_NullToken() {
        // Given
        String nullToken = null;

        // When
        boolean isValid = jwtUtils.validateJwtToken(nullToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_ExpiredToken() {
        // Given - Create a token with very short expiration
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000); // Negative = already expired
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null);
        String expiredToken = jwtUtils.generateJwtToken(authentication);
        
        // Reset expiration for validation
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", TEST_EXPIRATION_MS);

        // When
        boolean isValid = jwtUtils.validateJwtToken(expiredToken);

        // Then
        assertThat(isValid).isFalse();
        List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);
        assertThat(logEvents.get(0).getLevel().toString()).isEqualTo("ERROR");
        assertThat(logEvents.get(0).getMessage()).contains("JWT token is expired");
    }

    @Test
    void testGenerateJwtToken_DifferentUsers() {
        // Given
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(1L)
                .username("user1@example.com")
                .firstName("User")
                .lastName("One")
                .admin(false)
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(2L)
                .username("user2@example.com")
                .firstName("User")
                .lastName("Two")
                .admin(false)
                .build();

        Authentication auth1 = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                user1, null);
        Authentication auth2 = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                user2, null);

        // When
        String token1 = jwtUtils.generateJwtToken(auth1);
        String token2 = jwtUtils.generateJwtToken(auth2);

        // Then
        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtUtils.getUserNameFromJwtToken(token1)).isEqualTo("user1@example.com");
        assertThat(jwtUtils.getUserNameFromJwtToken(token2)).isEqualTo("user2@example.com");
    }
}

