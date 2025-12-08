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
import { AuthService } from 'src/app/core/service/auth.service';

import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: jest.Mocked<AuthService>;
  let router: Router;

  beforeEach(async () => {
    const authServiceMock = {
      register: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        RegisterComponent,
        BrowserAnimationsModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values', () => {
    expect(component.form.get('firstName')?.value).toBe('');
    expect(component.form.get('lastName')?.value).toBe('');
    expect(component.form.get('email')?.value).toBe('');
    expect(component.form.get('password')?.value).toBe('');
  });

  it('should have form invalid by default', () => {
    expect(component.form.invalid).toBe(true);
  });

  it('should validate firstName as required', () => {
    const firstNameControl = component.form.get('firstName');
    firstNameControl?.setValue('');
    expect(firstNameControl?.hasError('required')).toBe(true);
  });


  it('should validate lastName as required', () => {
    const lastNameControl = component.form.get('lastName');
    lastNameControl?.setValue('');
    expect(lastNameControl?.hasError('required')).toBe(true);
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

  it('should have form valid with correct values', () => {
    component.form.get('firstName')?.setValue('John');
    component.form.get('lastName')?.setValue('Doe');
    component.form.get('email')?.setValue('john.doe@example.com');
    component.form.get('password')?.setValue('password123');
    expect(component.form.valid).toBe(true);
  });

  it('should call authService.register on submit with valid form', () => {
    component.form.get('firstName')?.setValue('John');
    component.form.get('lastName')?.setValue('Doe');
    component.form.get('email')?.setValue('john.doe@example.com');
    component.form.get('password')?.setValue('password123');
    authService.register.mockReturnValue(of(undefined));
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.submit();

    expect(authService.register).toHaveBeenCalledWith({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      password: 'password123'
    });
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBe(false);
  });

  it('should set onError to true on registration error', () => {
    component.form.get('firstName')?.setValue('John');
    component.form.get('lastName')?.setValue('Doe');
    component.form.get('email')?.setValue('john.doe@example.com');
    component.form.get('password')?.setValue('password123');
    authService.register.mockReturnValue(throwError(() => new Error('Registration failed')));

    component.submit();

    expect(component.onError).toBe(true);
  });

  it('should display error message when registration fails', () => {
    component.form.get('firstName')?.setValue('John');
    component.form.get('lastName')?.setValue('Doe');
    component.form.get('email')?.setValue('john.doe@example.com');
    component.form.get('password')?.setValue('password123');
    authService.register.mockReturnValue(throwError(() => new Error('Registration failed')));

    component.submit();
    fixture.detectChanges();

    expect(component.onError).toBe(true);
  });

  it('should not submit if form is invalid', () => {
    component.form.get('firstName')?.setValue('');
    component.form.get('lastName')?.setValue('');
    component.form.get('email')?.setValue('');
    component.form.get('password')?.setValue('');
    
    expect(component.form.invalid).toBe(true);
  });
});
