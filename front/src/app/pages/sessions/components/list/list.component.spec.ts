import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { Session } from '../../../../core/models/session.interface';
import { SessionInformation } from '../../../../core/models/sessionInformation.interface';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';

import { ListComponent } from './list.component';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionApiService: jest.Mocked<SessionApiService>;
  let sessionService: jest.Mocked<SessionService>;

  const mockSessions: Session[] = [
    {
      id: 1,
      name: 'Yoga Session 1',
      description: 'A relaxing yoga session',
      date: new Date('2024-12-31'),
      teacher_id: 1,
      users: []
    },
    {
      id: 2,
      name: 'Yoga Session 2',
      description: 'An advanced yoga session',
      date: new Date('2025-01-15'),
      teacher_id: 2,
      users: []
    }
  ];

  const mockAdminUser: SessionInformation = {
    token: 'token',
    type: 'Bearer',
    id: 1,
    username: 'admin',
    firstName: 'Admin',
    lastName: 'User',
    admin: true
  };

  const mockRegularUser: SessionInformation = {
    token: 'token',
    type: 'Bearer',
    id: 2,
    username: 'user',
    firstName: 'Regular',
    lastName: 'User',
    admin: false
  };

  beforeEach(async () => {
    const sessionApiServiceMock = {
      all: jest.fn().mockReturnValue(of(mockSessions))
    };

    const sessionServiceMock = {
      sessionInformation: mockAdminUser
    };

    await TestBed.configureTestingModule({
      imports: [
        ListComponent,
        RouterTestingModule,
        MatCardModule,
        MatIconModule
      ],
      providers: [
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    sessionApiService = TestBed.inject(SessionApiService) as jest.Mocked<SessionApiService>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load sessions list', (done) => {
    fixture.detectChanges();

    component.sessions$.subscribe(sessions => {
      expect(sessions).toEqual(mockSessions);
      expect(sessions.length).toBe(2);
      done();
    });
  });

  it('should return admin user information', () => {
    sessionService.sessionInformation = mockAdminUser;
    fixture.detectChanges();

    expect(component.user).toEqual(mockAdminUser);
    expect(component.user?.admin).toBe(true);
  });

  it('should return regular user information', () => {
    sessionService.sessionInformation = mockRegularUser;
    fixture.detectChanges();

    expect(component.user).toEqual(mockRegularUser);
    expect(component.user?.admin).toBe(false);
  });

  it('should return undefined when user is not logged in', () => {
    sessionService.sessionInformation = undefined;
    fixture.detectChanges();

    expect(component.user).toBeUndefined();
  });

  it('should display Create button for admin user', () => {
    sessionService.sessionInformation = mockAdminUser;
    sessionApiService.all.mockReturnValue(of(mockSessions));
    fixture.detectChanges();

    expect(component.user?.admin).toBe(true);
  });

  it('should not display Create button for regular user', () => {
    sessionService.sessionInformation = mockRegularUser;
    sessionApiService.all.mockReturnValue(of(mockSessions));
    fixture.detectChanges();

    expect(component.user?.admin).toBe(false);
  });

  it('should display Detail button for all users', (done) => {
    fixture.detectChanges();

    component.sessions$.subscribe(sessions => {
      expect(sessions.length).toBeGreaterThan(0);
      done();
    });
  });
});
