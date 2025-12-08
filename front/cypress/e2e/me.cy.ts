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


});

