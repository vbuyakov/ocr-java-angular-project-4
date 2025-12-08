import { mockLoginResponses, mockSessions } from '../fixtures/mockData';

describe('Logout spec', () => {
  it('Logout functionality', () => {
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

    cy.get('input[formControlName=email]').type('admin@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')

    // Click logout button
    cy.contains('Logout').click()

    // Should navigate to home page
    cy.url().should('not.include', '/sessions')
    cy.url().should('not.include', '/me')
  })
});

