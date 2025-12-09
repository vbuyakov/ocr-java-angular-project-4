import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { Session } from '../models/session.interface';

import { SessionApiService } from './session-api.service';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all sessions', () => {
    const mockSessions: Session[] = [
      {
        id: 1,
        name: 'Yoga Session 1',
        description: 'A relaxing yoga session',
        date: new Date('2024-12-31'),
        teacher_id: 1,
        users: []
      }
    ];

    service.all().subscribe(sessions => {
      expect(sessions).toEqual(mockSessions);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(mockSessions);
  });

  it('should get session by id', () => {
    const mockSession: Session = {
      id: 1,
      name: 'Yoga Session 1',
      description: 'A relaxing yoga session',
      date: new Date('2024-12-31'),
      teacher_id: 1,
      users: []
    };

    service.detail('1').subscribe(session => {
      expect(session).toEqual(mockSession);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockSession);
  });

  it('should delete session', () => {
    service.delete('1').subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should create session', () => {
    const newSession: Session = {
      name: 'New Session',
      description: 'A new yoga session',
      date: new Date('2024-12-31'),
      teacher_id: 1,
      users: []
    };

    const createdSession: Session = {
      id: 1,
      ...newSession
    };

    service.create(newSession).subscribe(session => {
      expect(session).toEqual(createdSession);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newSession);
    req.flush(createdSession);
  });

  it('should update session', () => {
    const updatedSession: Session = {
      id: 1,
      name: 'Updated Session',
      description: 'An updated yoga session',
      date: new Date('2024-12-31'),
      teacher_id: 1,
      users: []
    };

    service.update('1', updatedSession).subscribe(session => {
      expect(session).toEqual(updatedSession);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedSession);
    req.flush(updatedSession);
  });

  it('should participate in session', () => {
    service.participate('1', '2').subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('api/session/1/participate/2');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();
    req.flush(null);
  });

  it('should unparticipate from session', () => {
    service.unParticipate('1', '2').subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('api/session/1/participate/2');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
