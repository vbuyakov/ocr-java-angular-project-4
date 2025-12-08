import { mockLoginResponses, mockSessions, mockTeachers } from '../fixtures/mockData';

describe('Sessions spec', () => {
  it('Display Create button for admin user', () => {
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
    cy.contains('Rentals available').should('be.visible')
    cy.contains('Create').should('be.visible')
    cy.get('button[routerLink="create"]').should('be.visible')
  })

  it('Do not display Create button for regular user', () => {
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
    cy.contains('Create').should('not.exist')
    cy.get('button[routerLink="create"]').should('not.exist')
  })

  it('Display sessions list for regular user without Edit buttons', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: mockLoginResponses.regular
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      mockSessions.list
    ).as('session')

    cy.get('input[formControlName=email]').type('user@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    // Verify sessions are displayed
    cy.contains('Yoga Session 1').should('be.visible')
    cy.contains('Yoga Session 2').should('be.visible')
    cy.contains('A relaxing yoga session for beginners').should('be.visible')
    cy.contains('An advanced yoga session').should('be.visible')

    // Scroll to ensure all elements are visible
    cy.scrollTo('bottom')

    // Verify session cards exist
    cy.get('mat-card.item').should('have.length', 2)
    
    // Verify Detail buttons are present for all users
    cy.get('mat-card.item').each(($card) => {
      cy.wrap($card).find('button').contains('Detail').should('exist')
    })
    
    // Verify Edit buttons are NOT present for regular users
    cy.get('button').contains('Edit').should('not.exist')
  })

  it('Display sessions list', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: mockLoginResponses.admin
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      mockSessions.list
    ).as('session')

    cy.get('input[formControlName=email]').type('admin@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    // Verify sessions are displayed
    cy.contains('Yoga Session 1').should('be.visible')
    cy.contains('Yoga Session 2').should('be.visible')
    cy.contains('A relaxing yoga session for beginners').should('be.visible')
    cy.contains('An advanced yoga session').should('be.visible')

    // Scroll to ensure all elements are visible
    cy.scrollTo('bottom')

    // Verify Detail buttons are present - check within session cards
    cy.get('mat-card.item').should('have.length', 2)
    cy.get('mat-card.item').each(($card) => {
      cy.wrap($card).find('button').contains('Detail').should('exist')
      cy.wrap($card).find('button').contains('Edit').should('exist')
    })
  })

  it('Navigate to session detail page', () => {
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

    cy.intercept('GET', '/api/teacher/1', {
      body: mockTeachers.single
    }).as('teacher')

    cy.get('input[formControlName=email]').type('admin@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    // Click on Detail button - find the card containing the session name, then find the Detail button
    cy.contains('Yoga Session 1').closest('mat-card.item').within(() => {
      cy.get('button').contains('Detail').click()
    })

    // Verify navigation to detail page
    cy.url().should('include', '/sessions/detail/1')
    
    // Verify session details are displayed
    cy.contains('Yoga Session 1').should('be.visible')
    cy.contains('A relaxing yoga session for beginners').should('be.visible')
    cy.contains('Margot DELAHAYE').should('be.visible')
    cy.contains('Description:').should('be.visible')
    cy.contains('0 attendees').should('be.visible')
    
    // Admin should see Delete button
    cy.contains('Delete').should('be.visible')
  })

  it('Navigate to session edit page', () => {
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
      body: mockTeachers.list
    }).as('teachers')

    cy.get('input[formControlName=email]').type('admin@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    // Click on Edit button - find the card containing the session name, then find the Edit button
    cy.contains('Yoga Session 1').closest('mat-card.item').within(() => {
      cy.get('button').contains('Edit').click()
    })

    // Verify navigation to edit page
    cy.url().should('include', '/sessions/update/1')
    
    // Verify form is displayed with pre-filled data
    cy.contains('Update session').should('be.visible')
    cy.get('input[formControlName=name]').should('have.value', 'Yoga Session 1')
    cy.get('textarea[formControlName=description]').should('contain.value', 'A relaxing yoga session for beginners')
  })
});

