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

  it('Session information is correctly displayed', () => {
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

    // Click on Detail button
    cy.contains('Yoga Session 1').closest('mat-card.item').within(() => {
      cy.get('button').contains('Detail').click()
    })

    cy.url().should('include', '/sessions/detail/1')

    // Verify all session information is displayed
    cy.contains('Yoga Session 1').should('be.visible')
    cy.contains('A relaxing yoga session for beginners').should('be.visible')
    cy.contains('Margot DELAHAYE').should('be.visible')
    cy.contains('Description:').should('be.visible')
    cy.contains('0 attendees').should('be.visible')
    
    // Verify date is displayed (format may vary)
    cy.contains('December').should('be.visible')
    
    // Verify created/updated dates are displayed
    cy.contains('Create at:').should('be.visible')
    cy.contains('Last update:').should('be.visible')
  })

  it('Delete button appears for admin user on detail page', () => {
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

    // Click on Detail button
    cy.contains('Yoga Session 1').closest('mat-card.item').within(() => {
      cy.get('button').contains('Detail').click()
    })

    cy.url().should('include', '/sessions/detail/1')

    // Admin should see Delete button
    cy.contains('Delete').should('be.visible')
    cy.get('button').contains('Delete').should('be.visible')
  })

  it('Delete button does not appear for regular user on detail page', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: mockLoginResponses.regular
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

    cy.get('input[formControlName=email]').type('user@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    // Click on Detail button
    cy.contains('Yoga Session 1').closest('mat-card.item').within(() => {
      cy.get('button').contains('Detail').click()
    })

    cy.url().should('include', '/sessions/detail/1')

    // Regular user should NOT see Delete button
    cy.contains('Delete').should('not.exist')
    cy.get('button').contains('Delete').should('not.exist')
    
    // Regular user should see Participate button instead
    cy.contains('Participate').should('be.visible')
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

  it('Delete session functionality', () => {
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

    cy.intercept('DELETE', '/api/session/1', {
      statusCode: 200
    }).as('deleteSession')

    cy.intercept('GET', '/api/teacher/1', {
      body: mockTeachers.single
    }).as('teacher')

    cy.get('input[formControlName=email]').type('admin@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    // Click on Detail button
    cy.contains('Yoga Session 1').closest('mat-card.item').within(() => {
      cy.get('button').contains('Detail').click()
    })

    cy.url().should('include', '/sessions/detail/1')

    // Click Delete button
    cy.contains('Delete').click()
    cy.wait('@deleteSession')

    // Should navigate back to sessions list
    cy.url().should('include', '/sessions')
  })

  it('Regular user can participate in session', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: mockLoginResponses.regular
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      [{ ...mockSessions.single, users: [] }]
    ).as('session')

    // Initial state: user is not participating
    const sessionWithoutUser = { ...mockSessions.single, users: [] }
    const sessionWithUser = { ...mockSessions.single, users: [2] }
    
    // Use a counter to return different responses for initial load and refresh
    let callCount = 0
    cy.intercept('GET', '/api/session/1', (req) => {
      callCount++
      if (callCount === 1) {
        req.reply({ body: sessionWithoutUser })
      } else {
        req.reply({ body: sessionWithUser })
      }
    }).as('sessionDetail')

    cy.intercept('POST', '/api/session/1/participate/2', {
      statusCode: 200
    }).as('participate')

    cy.intercept('GET', '/api/teacher/1', {
      body: mockTeachers.single
    }).as('teacher')

    cy.get('input[formControlName=email]').type('user@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    // Click on Detail button
    cy.contains('Yoga Session 1').closest('mat-card.item').within(() => {
      cy.get('button').contains('Detail').click()
    })

    cy.url().should('include', '/sessions/detail/1')

    // Verify initial state: Participate button is visible
    cy.contains('Participate').should('be.visible')
    cy.contains('0 attendees').should('be.visible')

    // Click Participate button
    cy.contains('Participate').click()
    cy.wait('@participate')

    // Wait for session refresh after participation
    cy.wait('@sessionDetail')

    // Verify UI updated: "Do not participate" button is now visible
    cy.contains('Do not participate').should('be.visible')
    cy.contains('Participate').should('not.exist')
    
    // Verify attendee count updated
    cy.contains('1 attendees').should('be.visible')
  })

  it('Regular user can unparticipate from session', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: mockLoginResponses.regular
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      [{ ...mockSessions.single, users: [2] }]
    ).as('session')

    // Initial state: user is already participating
    const sessionWithUser = { ...mockSessions.single, users: [2] }
    const sessionWithoutUser = { ...mockSessions.single, users: [] }
    
    // Use a counter to return different responses for initial load and refresh
    let callCount = 0
    cy.intercept('GET', '/api/session/1', (req) => {
      callCount++
      if (callCount === 1) {
        req.reply({ body: sessionWithUser })
      } else {
        req.reply({ body: sessionWithoutUser })
      }
    }).as('sessionDetail')

    cy.intercept('DELETE', '/api/session/1/participate/2', {
      statusCode: 200
    }).as('unParticipate')

    cy.intercept('GET', '/api/teacher/1', {
      body: mockTeachers.single
    }).as('teacher')

    cy.get('input[formControlName=email]').type('user@example.com')
    cy.get('input[formControlName=password]').type(`password123{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')

    // Click on Detail button
    cy.contains('Yoga Session 1').closest('mat-card.item').within(() => {
      cy.get('button').contains('Detail').click()
    })

    cy.url().should('include', '/sessions/detail/1')

    // Verify initial state: "Do not participate" button is visible
    cy.contains('Do not participate').should('be.visible')
    cy.contains('Participate').should('not.exist')
    cy.contains('1 attendees').should('be.visible')

    // Click "Do not participate" button
    cy.contains('Do not participate').click()
    cy.wait('@unParticipate')

    // Wait for session refresh after unparticipating
    cy.wait('@sessionDetail')

    // Verify UI updated: "Participate" button is now visible
    cy.contains('Participate').should('be.visible')
    cy.contains('Do not participate').should('not.exist')
    
    // Verify attendee count updated
    cy.contains('0 attendees').should('be.visible')
  })

  it('Back button functionality on detail page', () => {
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

    // Click on Detail button
    cy.contains('Yoga Session 1').closest('mat-card.item').within(() => {
      cy.get('button').contains('Detail').click()
    })

    cy.url().should('include', '/sessions/detail/1')

    // Click back button
    cy.get('button[mat-icon-button]').contains('arrow_back').click()

    // Should navigate back (browser history)
    cy.url().should('include', '/sessions')
  })
});

