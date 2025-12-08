import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionInformation } from '../models/sessionInformation.interface';

import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;

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
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize with logged out state', () => {
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should return logged in status observable', (done) => {
    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(false);
      done();
    });
  });

  it('should log in user', () => {
    service.logIn(mockSessionInformation);

    expect(service.sessionInformation).toEqual(mockSessionInformation);
    expect(service.isLogged).toBe(true);
  });

  it('should emit logged in status when user logs in', (done) => {
    service.$isLogged().subscribe(isLogged => {
      if (isLogged) {
        expect(isLogged).toBe(true);
        done();
      }
    });

    service.logIn(mockSessionInformation);
  });

  it('should log out user', () => {
    service.logIn(mockSessionInformation);
    service.logOut();

    expect(service.sessionInformation).toBeUndefined();
    expect(service.isLogged).toBe(false);
  });

  it('should emit logged out status when user logs out', (done) => {
    service.logIn(mockSessionInformation);
    
    let loggedInCount = 0;
    service.$isLogged().subscribe(isLogged => {
      loggedInCount++;
      if (loggedInCount === 2 && !isLogged) {
        expect(isLogged).toBe(false);
        done();
      }
    });

    service.logOut();
  });
});
