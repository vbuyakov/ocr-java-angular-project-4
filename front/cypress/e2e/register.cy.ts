describe('Register spec', () => {
  it('Register successfully', () => {
    cy.visit('/register')

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {}
    }).as('register')

    cy.get('input[formControlName=firstName]').type('John')
    cy.get('input[formControlName=lastName]').type('Doe')
    cy.get('input[formControlName=email]').type('john.doe@example.com')
    cy.get('input[formControlName=password]').type('password123')
    cy.get('button[type=submit]').click()

    cy.wait('@register')
    cy.url().should('include', '/login')
  })

  it('Register with error', () => {
    cy.visit('/register')

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 500,
      body: {}
    }).as('registerError')

    cy.get('input[formControlName=firstName]').type('John')
    cy.get('input[formControlName=lastName]').type('Doe')
    cy.get('input[formControlName=email]').type('john.doe@example.com')
    cy.get('input[formControlName=password]').type('password123')
    cy.get('button[type=submit]').click()

    cy.wait('@registerError')
    cy.get('.error').should('be.visible')
    cy.get('.error').should('contain', 'An error occurred')
  })
});

