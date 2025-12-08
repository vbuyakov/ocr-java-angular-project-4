import { HttpRequest, HttpEvent } from '@angular/common/http';
import { runInInjectionContext } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionInformation } from '../core/models/sessionInformation.interface';
import { SessionService } from '../core/service/session.service';

import { customJwtInterceptorFn } from './customJwtInterceptorFn';

describe('customJwtInterceptorFn', () => {
  let sessionService: SessionService;
  let injector: any;

  const mockSessionInformation: SessionInformation = {
    token: 'test-token',
    type: 'Bearer',
    id: 1,
    username: 'admin',
    firstName: 'Admin',
    lastName: 'User',
    admin: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SessionService]
    });
    sessionService = TestBed.inject(SessionService);
    injector = TestBed;
  });

  it('should add Authorization header when user is logged in', (done) => {
    sessionService.logIn(mockSessionInformation);
    
    const request = new HttpRequest('GET', '/api/test');
    const next = (req: HttpRequest<unknown>) => {
      expect(req.headers.get('Authorization')).toBe('Bearer test-token');
      done();
      return of({} as HttpEvent<unknown>);
    };

    runInInjectionContext(injector, () => {
      customJwtInterceptorFn(request, next).subscribe();
    });
  });

  it('should not add Authorization header when user is not logged in', (done) => {
    sessionService.logOut();
    
    const request = new HttpRequest('GET', '/api/test');
    const next = (req: HttpRequest<unknown>) => {
      expect(req.headers.get('Authorization')).toBeNull();
      done();
      return of({} as HttpEvent<unknown>);
    };

    runInInjectionContext(injector, () => {
      customJwtInterceptorFn(request, next).subscribe();
    });
  });

  it('should pass through request when user is not logged in', (done) => {
    sessionService.logOut();
    
    const request = new HttpRequest('GET', '/api/test');
    const next = (req: HttpRequest<unknown>) => {
      expect(req.url).toBe('/api/test');
      expect(req.method).toBe('GET');
      done();
      return of({} as HttpEvent<unknown>);
    };

    runInInjectionContext(injector, () => {
      customJwtInterceptorFn(request, next).subscribe();
    });
  });
});

