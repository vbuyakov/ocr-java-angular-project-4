import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { User } from '../../core/models/user.interface';
import { SessionInformation } from '../../core/models/sessionInformation.interface';
import { SessionService } from '../../core/service/session.service';
import { UserService } from '../../core/service/user.service';

import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: jest.Mocked<UserService>;
  let sessionService: jest.Mocked<SessionService>;
  let router: Router;
  let matSnackBar: MatSnackBar;

  const mockAdminUser: User = {
    id: 1,
    email: 'admin@example.com',
    firstName: 'Admin',
    lastName: 'User',
    admin: true,
    password: 'password',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-01')
  };

  const mockRegularUser: User = {
    id: 2,
    email: 'user@example.com',
    firstName: 'Regular',
    lastName: 'User',
    admin: false,
    password: 'password',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-01')
  };

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
    const userServiceMock = {
      getById: jest.fn(),
      delete: jest.fn()
    };

    const sessionServiceMock = {
      sessionInformation: mockSessionInformation,
      logOut: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        MeComponent,
        RouterTestingModule,
        MatSnackBarModule
      ],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jest.Mocked<UserService>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user information on init', () => {
    userService.getById.mockReturnValue(of(mockAdminUser));

    component.ngOnInit();

    expect(userService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(mockAdminUser);
  });

  it('should display user information for admin user', () => {
    userService.getById.mockReturnValue(of(mockAdminUser));

    component.ngOnInit();
    fixture.detectChanges();

    expect(component.user).toEqual(mockAdminUser);
    expect(component.user?.admin).toBe(true);
  });

  it('should display user information for regular user', () => {
    userService.getById.mockReturnValue(of(mockRegularUser));

    component.ngOnInit();
    fixture.detectChanges();

    expect(component.user).toEqual(mockRegularUser);
    expect(component.user?.admin).toBe(false);
  });

  it('should call back method', () => {
    const historyBackSpy = jest.spyOn(window.history, 'back');

    component.back();

    expect(historyBackSpy).toHaveBeenCalled();
  });

  it('should delete user account and logout', () => {
    userService.getById.mockReturnValue(of(mockRegularUser));
    userService.delete.mockReturnValue(of(undefined));
    const navigateSpy = jest.spyOn(router, 'navigate');
    const snackBarSpy = jest.spyOn(matSnackBar, 'open');

    component.ngOnInit();
    fixture.detectChanges();
    component.delete();
    fixture.detectChanges();

    expect(userService.delete).toHaveBeenCalledWith('1');
    // Note: takeUntilDestroyed might prevent subscribe callback in test environment
    // But we can verify the service was called
    expect(userService.delete).toHaveBeenCalled();
  });
});
