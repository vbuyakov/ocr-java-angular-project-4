package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    @Test
    void testBuilder() {
        // When
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getFirstName()).isEqualTo("John");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.getAdmin()).isFalse();
    }

    @Test
    void testGetAuthorities() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        // When
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Then
        assertThat(authorities).isNotNull();
        assertThat(authorities).isEmpty();
    }

    @Test
    void testIsAccountNonExpired() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        // When/Then
        assertThat(userDetails.isAccountNonExpired()).isTrue();
    }

    @Test
    void testIsAccountNonLocked() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        // When/Then
        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    void testIsCredentialsNonExpired() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        // When/Then
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void testIsEnabled() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        // When/Then
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void testEquals_SameInstance() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        // When/Then
        assertThat(userDetails.equals(userDetails)).isTrue();
    }

    @Test
    void testEquals_SameId() {
        // Given
        UserDetailsImpl userDetails1 = UserDetailsImpl.builder()
                .id(1L)
                .username("test1@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password1")
                .admin(false)
                .build();

        UserDetailsImpl userDetails2 = UserDetailsImpl.builder()
                .id(1L)
                .username("test2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("password2")
                .admin(true)
                .build();

        // When/Then
        assertThat(userDetails1.equals(userDetails2)).isTrue();
    }

    @Test
    void testEquals_DifferentId() {
        // Given
        UserDetailsImpl userDetails1 = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        UserDetailsImpl userDetails2 = UserDetailsImpl.builder()
                .id(2L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        // When/Then
        assertThat(userDetails1.equals(userDetails2)).isFalse();
    }

    @Test
    void testEquals_Null() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        // When/Then
        assertThat(userDetails.equals(null)).isFalse();
    }

    @Test
    void testEquals_DifferentClass() {
        // Given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        String differentObject = "not a UserDetailsImpl";

        // When/Then
        assertThat(userDetails.equals(differentObject)).isFalse();
    }

    @Test
    void testEquals_WithNullId() {
        // Given
        UserDetailsImpl userDetails1 = UserDetailsImpl.builder()
                .id(null)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        UserDetailsImpl userDetails2 = UserDetailsImpl.builder()
                .id(null)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        // When/Then
        assertThat(userDetails1.equals(userDetails2)).isTrue();
    }
}

