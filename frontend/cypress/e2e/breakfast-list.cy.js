const apiUrl = 'http://localhost:8080';

describe('Agenda de cafés da manhã', () => {
  it('cria, edita e exclui um café da manhã', () => {
    let breakfasts = [];

    cy.intercept('GET', `${apiUrl}/breakfasts`, (request) => {
      request.reply(breakfasts);
    }).as('listBreakfasts');

    cy.intercept('POST', `${apiUrl}/breakfasts`, (request) => {
      expect(request.body).to.deep.equal({
        breakfastDate: '2027-06-10',
        breakfastTime: '08:30',
        location: 'Sala de reunião',
      });

      breakfasts = [
        {
          id: 1,
          breakfastDate: '2027-06-10',
          breakfastTime: '08:30:00',
          location: 'Sala de reunião',
          createdDateTime: '2026-06-03T08:00:00',
          participations: [],
        },
      ];

      request.reply({ statusCode: 201, body: 1 });
    }).as('createBreakfast');

    cy.intercept('PUT', `${apiUrl}/breakfasts/1`, (request) => {
      expect(request.body).to.deep.equal({
        breakfastDate: '2027-06-11',
        breakfastTime: '09:00',
        location: 'Auditório',
      });

      breakfasts = [
        {
          ...breakfasts[0],
          breakfastDate: '2027-06-11',
          breakfastTime: '09:00:00',
          location: 'Auditório',
        },
      ];

      request.reply({ statusCode: 204 });
    }).as('updateBreakfast');

    cy.intercept('DELETE', `${apiUrl}/breakfasts/1`, (request) => {
      breakfasts = [];
      request.reply({ statusCode: 204 });
    }).as('deleteBreakfast');

    cy.visit('/');
    cy.wait('@listBreakfasts');

    cy.contains('Nenhum café cadastrado.').should('be.visible');

    cy.get('#breakfastDate').type('2027-06-10');
    cy.get('#breakfastTime').clear().type('08:30');
    cy.get('#location').type('Sala de reunião');
    cy.contains('button', 'Criar café').click();

    cy.wait('@createBreakfast');
    cy.wait('@listBreakfasts');
    cy.contains('Café da manhã #1 criado.').should('be.visible');
    cy.contains('tr', 'Sala de reunião').should('be.visible');

    cy.contains('tr', 'Sala de reunião').within(() => {
      cy.contains('button', 'Editar').click();
    });

    cy.get('#breakfastDate').clear().type('2027-06-11');
    cy.get('#breakfastTime').clear().type('09:00');
    cy.get('#location').clear().type('Auditório');
    cy.contains('button', 'Salvar alterações').click();

    cy.wait('@updateBreakfast');
    cy.wait('@listBreakfasts');
    cy.contains('Café da manhã atualizado.').should('be.visible');
    cy.contains('tr', 'Auditório').should('be.visible');

    cy.contains('tr', 'Auditório').within(() => {
      cy.contains('button', 'Excluir').click();
    });

    cy.wait('@deleteBreakfast');
    cy.wait('@listBreakfasts');
    cy.contains('Café da manhã excluído.').should('be.visible');
    cy.contains('Nenhum café cadastrado.').should('be.visible');
  });
});
