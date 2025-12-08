import { mockLoginResponses, mockSessions, mockTeachers } from '../fixtures/mockData';

describe('Sessions Create spec', () => {
  it('Create session successfully', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: mockLoginResponses.admin
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      mockSessions.empty).as('session')

    cy.intercept('GET', '/api/teacher', {
      body: [mockTeachers.single]
    }).as('getTeachers')

    cy.intercept('POST', '/api/session', {
      statusCode: 200,
      body: mockSessions.created
    }).as('createSession')

    cy.get('input[formControlName=email]').type('admin@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    cy.contains('Create').click()
    cy.wait('@getTeachers')

    cy.contains('Create session').should('be.visible')

    cy.get('input[formControlName=name]').type('Yoga Session')
    
    // Set date to a future date
    const futureDate = new Date()
    futureDate.setDate(futureDate.getDate() + 7)
    const dateString = futureDate.toISOString().split('T')[0]
    cy.get('input[formControlName=date]').type(dateString)

    cy.get('mat-select[formControlName=teacher_id]').click()
    cy.get('mat-option').contains('Margot DELAHAYE').click()

    cy.get('textarea[formControlName=description]').type('A relaxing yoga session for beginners')

    cy.get('button[type=submit]').click()

    cy.wait('@createSession')
    cy.url().should('include', '/sessions')
  })

  it('Regular user cannot access create page', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: mockLoginResponses.regular
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      mockSessions.empty).as('session')

    cy.get('input[formControlName=email]').type('user@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    // Regular user should not see Create button
    cy.contains('Create').should('not.exist')
    cy.get('button[routerLink="create"]').should('not.exist')
  })
});

