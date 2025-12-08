import { mockLoginResponses, mockUsers, mockSessions } from '../fixtures/mockData';

describe('Me spec', () => {
  it('Display user information for admin user', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: mockLoginResponses.admin
    })

    cy.intercept('GET', '/api/user/1', {
      body: mockUsers.admin
    }).as('getUser')

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      mockSessions.empty).as('session')

    cy.get('input[formControlName=email]').type('admin@example.com')
    cy.get('input[formControlName=password]').type(`${"password123"}{enter}{enter}`)

    cy.url().should('include', '/sessions')

    cy.contains('Account').click()
    cy.url().should('include', '/me')
    cy.contains('User information').should('be.visible')
    cy.contains('Name: Admin USER').should('be.visible')
    cy.contains('Email: admin@example.com').should('be.visible')
    cy.contains('You are admin').should('be.visible')
  })

  it('Delete user account', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: mockLoginResponses.regular
    })

    cy.intercept('GET', '/api/user/2', {
      body: mockUsers.regular
    }).as('getUser')

    cy.intercept('DELETE', '/api/user/2', {
      statusCode: 200
    }).as('deleteUser')

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      mockSessions.empty).as('session')

    cy.get('input[formControlName=email]').type('user@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')

    cy.contains('Account').click()
    cy.url().should('include', '/me')

    // Click delete button (button contains delete icon, text says "Detail" but it's the delete button)
    cy.get('button[mat-raised-button]').contains('Detail').click()
    cy.wait('@deleteUser')

    // Should navigate to home page after deletion
    cy.url().should('include', '/')
  })

  it('Back button functionality on me page', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: mockLoginResponses.admin
    })

    cy.intercept('GET', '/api/user/1', {
      body: mockUsers.admin
    }).as('getUser')

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      mockSessions.empty).as('session')

    cy.get('input[formControlName=email]').type('admin@example.com')
    cy.get('input[formControlName=password]').type(`${"password123"}{enter}{enter}`)

    cy.url().should('include', '/sessions')

    cy.contains('Account').click()
    cy.url().should('include', '/me')

    // Click back button
    cy.get('button[mat-icon-button]').contains('arrow_back').click()

    // Should navigate back (browser history)
    cy.url().should('include', '/sessions')
  })
});

