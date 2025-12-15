package com.openclassrooms.starterjwt.security.jwt;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String INVALID_TOKEN = "invalid.token";
    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() throws Exception {
        // Capture log events and suppress console output
        Logger logger = (Logger) LoggerFactory.getLogger(AuthTokenFilter.class);
        logger.detachAndStopAllAppenders(); // Remove console appender
        logger.setAdditive(false); // Prevent propagation to root logger
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
        logger.setLevel(ch.qos.logback.classic.Level.ALL); // Capture all levels

        // Use reflection to inject mocks since AuthTokenFilter uses @Autowired
        Field jwtUtilsField = AuthTokenFilter.class.getDeclaredField("jwtUtils");
        jwtUtilsField.setAccessible(true);
        jwtUtilsField.set(authTokenFilter, jwtUtils);

        Field userDetailsServiceField = AuthTokenFilter.class.getDeclaredField("userDetailsService");
        userDetailsServiceField.setAccessible(true);
        userDetailsServiceField.set(authTokenFilter, userDetailsService);
    }

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        // Given
        UserDetails userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(VALID_TOKEN)).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtils).validateJwtToken(VALID_TOKEN);
        verify(jwtUtils).getUserNameFromJwtToken(VALID_TOKEN);
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer " + INVALID_TOKEN);
        when(jwtUtils.validateJwtToken(INVALID_TOKEN)).thenReturn(false);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtils).validateJwtToken(INVALID_TOKEN);
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidAuthorizationFormat() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat token");

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_EmptyBearerToken() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer ");
        when(jwtUtils.validateJwtToken("")).thenReturn(false);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtils).validateJwtToken("");
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ExceptionHandling() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(VALID_TOKEN)).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response); // Should continue even on exception
        List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);
        assertThat(logEvents.get(0).getLevel().toString()).isEqualTo("ERROR");
        assertThat(logEvents.get(0).getMessage()).contains("Cannot set user authentication");
    }

    @Test
    void testDoFilterInternal_ExceptionDuringTokenValidation() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenThrow(new RuntimeException("Validation error"));

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response); // Should continue even on exception
        List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);
        assertThat(logEvents.get(0).getLevel().toString()).isEqualTo("ERROR");
        assertThat(logEvents.get(0).getMessage()).contains("Cannot set user authentication");
    }

    @Test
    void testDoFilterInternal_ExceptionDuringUsernameExtraction() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(VALID_TOKEN)).thenThrow(new RuntimeException("Extraction error"));

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response); // Should continue even on exception
        List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);
        assertThat(logEvents.get(0).getLevel().toString()).isEqualTo("ERROR");
        assertThat(logEvents.get(0).getMessage()).contains("Cannot set user authentication");
    }
}

