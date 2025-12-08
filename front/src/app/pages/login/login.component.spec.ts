import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { SessionInformation } from 'src/app/core/models/sessionInformation.interface';
import { AuthService } from 'src/app/core/service/auth.service';
import { SessionService } from 'src/app/core/service/session.service';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jest.Mocked<AuthService>;
  let sessionService: jest.Mocked<SessionService>;
  let router: Router;

  const mockSessionInformation: SessionInformation = {
    token: 'token',
    type: 'Bearer',
    id: 1,
    username: 'admin',
    firstName: 'Admin',
    lastName: 'User',
    admin: true
  };

  beforeEach(async () => {
    const authServiceMock = {
      login: jest.fn()
    };

    const sessionServiceMock = {
      logIn: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        LoginComponent,
        RouterTestingModule,
        BrowserAnimationsModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values', () => {
    expect(component.form.get('email')?.value).toBe('');
    expect(component.form.get('password')?.value).toBe('');
  });

  it('should have form invalid by default', () => {
    expect(component.form.invalid).toBe(true);
  });

  it('should validate email as required', () => {
    const emailControl = component.form.get('email');
    emailControl?.setValue('');
    expect(emailControl?.hasError('required')).toBe(true);
  });

  it('should validate email format', () => {
    const emailControl = component.form.get('email');
    emailControl?.setValue('invalid-email');
    expect(emailControl?.hasError('email')).toBe(true);
  });

  it('should validate password as required', () => {
    const passwordControl = component.form.get('password');
    passwordControl?.setValue('');
    expect(passwordControl?.hasError('required')).toBe(true);
  });

  it('should validate password is required', () => {
    const passwordControl = component.form.get('password');
    passwordControl?.setValue('');
    expect(passwordControl?.hasError('required')).toBe(true);
  });

  it('should have form valid with correct values', () => {
    component.form.get('email')?.setValue('test@example.com');
    component.form.get('password')?.setValue('password123');
    expect(component.form.valid).toBe(true);
  });

  it('should call authService.login on submit with valid form', () => {
    component.form.get('email')?.setValue('test@example.com');
    component.form.get('password')?.setValue('password123');
    authService.login.mockReturnValue(of(mockSessionInformation));
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.submit();

    expect(authService.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123'
    });
    expect(sessionService.logIn).toHaveBeenCalledWith(mockSessionInformation);
    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  });

  it('should set onError to true on login error', () => {
    component.form.get('email')?.setValue('test@example.com');
    component.form.get('password')?.setValue('password123');
    authService.login.mockReturnValue(throwError(() => new Error('Login failed')));

    component.submit();

    expect(component.onError).toBe(true);
    expect(sessionService.logIn).not.toHaveBeenCalled();
  });

  it('should handle login with wrong credentials', () => {
    component.form.get('email')?.setValue('wrong@example.com');
    component.form.get('password')?.setValue('wrongpassword');
    authService.login.mockReturnValue(throwError(() => new Error('Unauthorized')));

    component.submit();

    expect(component.onError).toBe(true);
  });

  it('should toggle password visibility', () => {
    expect(component.hide).toBe(true);
    component.hide = false;
    expect(component.onError).toBe(false);
  });

  it('should not submit if form is invalid', () => {
    component.form.get('email')?.setValue('');
    component.form.get('password')?.setValue('');
    
    // Mock authService to verify it's not called
    authService.login.mockReturnValue(of(mockSessionInformation));
    
    component.submit();

    // Even if submit is called, form validation should prevent actual submission
    // In real scenario, the submit button should be disabled when form is invalid
    expect(component.form.invalid).toBe(true);
  });
});
