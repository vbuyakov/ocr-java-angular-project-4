import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionInformation } from '../core/models/sessionInformation.interface';
import { SessionService } from '../core/service/session.service';

import { AuthGuard } from './auth.guard';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let sessionService: SessionService;
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

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [AuthGuard, SessionService]
    });
    guard = TestBed.inject(AuthGuard);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow activation when user is logged in', () => {
    sessionService.logIn(mockSessionInformation);
    expect(guard.canActivate()).toBe(true);
  });

  it('should deny activation and navigate to login when user is not logged in', () => {
    sessionService.logOut();
    const navigateSpy = jest.spyOn(router, 'navigate');
    
    expect(guard.canActivate()).toBe(false);
    expect(navigateSpy).toHaveBeenCalledWith(['login']);
  });
});

