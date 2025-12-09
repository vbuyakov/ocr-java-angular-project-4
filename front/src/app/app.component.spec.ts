import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { AuthService } from './core/service/auth.service';
import { SessionService } from './core/service/session.service';

import { AppComponent } from './app.component';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let sessionService: jest.Mocked<SessionService>;
  let router: Router;

  beforeEach(async () => {
    const sessionServiceMock = {
      $isLogged: jest.fn(),
      logOut: jest.fn()
    };

    const authServiceMock = {
      register: jest.fn(),
      login: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        AppComponent,
        RouterTestingModule,
        HttpClientModule
      ],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: AuthService, useValue: authServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    router = TestBed.inject(Router);
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should return logged in status observable', () => {
    sessionService.$isLogged.mockReturnValue(of(true));

    const isLogged$ = component.$isLogged();

    expect(sessionService.$isLogged).toHaveBeenCalled();
    isLogged$.subscribe(isLogged => {
      expect(isLogged).toBe(true);
    });
  });

  it('should logout user and navigate to home', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.logout();

    expect(sessionService.logOut).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  });
});
