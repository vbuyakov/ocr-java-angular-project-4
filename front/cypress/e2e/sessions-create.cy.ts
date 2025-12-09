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

  it('Update session successfully', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: mockLoginResponses.admin
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      [mockSessions.single]
    ).as('session')

    cy.intercept('GET', '/api/session/1', {
      body: mockSessions.single
    }).as('sessionDetail')

    cy.intercept('GET', '/api/teacher', {
      body: [mockTeachers.single]
    }).as('getTeachers')

    cy.intercept('PUT', '/api/session/1', {
      statusCode: 200,
      body: { ...mockSessions.single, name: 'Updated Session' }
    }).as('updateSession')

    cy.get('input[formControlName=email]').type('admin@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    // Click on Edit button
    cy.contains('Yoga Session 1').closest('mat-card.item').within(() => {
      cy.get('button').contains('Edit').click()
    })

    cy.url().should('include', '/sessions/update/1')
    cy.wait('@getTeachers')

    cy.contains('Update session').should('be.visible')

    // Update the form
    cy.get('input[formControlName=name]').clear().type('Updated Session Name')
    cy.get('textarea[formControlName=description]').clear().type('Updated description')

    // Submit the form
    cy.get('button[type=submit]').click()
    cy.wait('@updateSession')

    // Should navigate back to sessions list
    cy.url().should('include', '/sessions')
  })

  it('Display error when required field is missing', () => {
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

    cy.get('input[formControlName=email]').type('admin@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    cy.contains('Create').click()
    cy.wait('@getTeachers')

    cy.contains('Create session').should('be.visible')

    // Verify submit button is disabled when form is invalid
    cy.get('button[type=submit]').should('be.disabled')

    // Try to submit without filling required fields
    // The button should remain disabled, but let's verify form validation
    cy.get('input[formControlName=name]').should('have.value', '')
    cy.get('input[formControlName=date]').should('have.value', '')
    cy.get('mat-select[formControlName=teacher_id]').should('exist')
    cy.get('textarea[formControlName=description]').should('have.value', '')

    // Fill only name, button should still be disabled
    cy.get('input[formControlName=name]').type('Yoga Session')
    cy.get('button[type=submit]').should('be.disabled')

    // Fill date, button should still be disabled (teacher and description missing)
    const futureDate = new Date()
    futureDate.setDate(futureDate.getDate() + 7)
    const dateString = futureDate.toISOString().split('T')[0]
    cy.get('input[formControlName=date]').type(dateString)
    cy.get('button[type=submit]').should('be.disabled')

    // Fill teacher, button should still be disabled (description missing)
    cy.get('mat-select[formControlName=teacher_id]').click()
    cy.get('mat-option').contains('Margot DELAHAYE').click()
    cy.get('button[type=submit]').should('be.disabled')

    // Now fill description, button should be enabled
    cy.get('textarea[formControlName=description]').type('A relaxing yoga session')
    cy.get('button[type=submit]').should('not.be.disabled')
  })
});

