// Mock data for E2E tests

export const mockUsers = {
  admin: {
    id: 1,
    email: 'admin@example.com',
    firstName: 'Admin',
    lastName: 'User',
    admin: true,
    createdAt: '2024-01-01T00:00:00.000Z',
    updatedAt: '2024-01-01T00:00:00.000Z'
  },
  regular: {
    id: 2,
    email: 'user@example.com',
    firstName: 'Regular',
    lastName: 'User',
    admin: false,
    createdAt: '2024-01-01T00:00:00.000Z',
    updatedAt: '2024-01-01T00:00:00.000Z'
  }
};

export const mockLoginResponses = {
  admin: {
    token: 'token',
    id: 1,
    username: 'admin',
    firstName: 'Admin',
    lastName: 'User',
    admin: true
  },
  regular: {
    token: 'token',
    id: 2,
    username: 'user',
    firstName: 'Regular',
    lastName: 'User',
    admin: false
  }
};

export const mockSessions = {
  empty: [],
  list: [
    {
      id: 1,
      name: 'Yoga Session 1',
      description: 'A relaxing yoga session for beginners',
      date: '2024-12-31T00:00:00.000Z',
      teacher_id: 1,
      users: []
    },
    {
      id: 2,
      name: 'Yoga Session 2',
      description: 'An advanced yoga session',
      date: '2025-01-15T00:00:00.000Z',
      teacher_id: 2,
      users: []
    }
  ],
  single: {
    id: 1,
    name: 'Yoga Session 1',
    description: 'A relaxing yoga session for beginners',
    date: '2024-12-31T00:00:00.000Z',
    teacher_id: 1,
    users: [],
    createdAt: '2024-01-01T00:00:00.000Z',
    updatedAt: '2024-01-01T00:00:00.000Z'
  },
  created: {
    id: 1,
    name: 'Yoga Session',
    description: 'A relaxing yoga session',
    date: '2024-12-31T00:00:00.000Z',
    teacher_id: 1,
    users: []
  }
};

export const mockTeachers = {
  list: [
    {
      id: 1,
      firstName: 'Margot',
      lastName: 'DELAHAYE',
      createdAt: '2024-01-01T00:00:00.000Z',
      updatedAt: '2024-01-01T00:00:00.000Z'
    },
    {
      id: 2,
      firstName: 'John',
      lastName: 'DOE',
      createdAt: '2024-01-01T00:00:00.000Z',
      updatedAt: '2024-01-01T00:00:00.000Z'
    }
  ],
  single: {
    id: 1,
    firstName: 'Margot',
    lastName: 'DELAHAYE',
    createdAt: '2024-01-01T00:00:00.000Z',
    updatedAt: '2024-01-01T00:00:00.000Z'
  }
};

