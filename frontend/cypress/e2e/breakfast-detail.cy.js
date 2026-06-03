const apiUrl = 'https://gracious-encouragement-production.up.railway.app';

function todayDateValue() {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
}

describe('Detalhe do café da manhã', () => {
  it('cadastra participação, cadastra item, atualiza status e remove dados', () => {
    const breakfastId = 1;
    const participationId = 2;
    const itemId = 3;
    let breakfast = {
      id: breakfastId,
      breakfastDate: todayDateValue(),
      breakfastTime: '08:30:00',
      location: 'Sala Recife',
      createdDateTime: '2026-06-03T08:00:00',
      participations: [],
    };

    cy.intercept('GET', `${apiUrl}/breakfasts/${breakfastId}`, (request) => {
      request.reply(JSON.parse(JSON.stringify(breakfast)));
    }).as('getBreakfast');

    cy.intercept('POST', `${apiUrl}/participations`, (request) => {
      expect(request.body).to.deep.equal({
        breakfastId,
        name: 'Ana Recife',
        cpf: '73244216013',
      });

      breakfast = {
        ...breakfast,
        participations: [
          {
            id: participationId,
            breakfastId,
            collaboratorId: 10,
            collaborator: {
              id: 10,
              name: 'Ana Recife',
              cpf: '73244216013',
            },
            items: [],
          },
        ],
      };

      request.reply({ statusCode: 201, body: participationId });
    }).as('createParticipation');

    cy.intercept('POST', `${apiUrl}/participations/${participationId}/items`, (request) => {
      expect(request.body).to.deep.equal({
        name: 'Suco de acerola',
      });

      breakfast = {
        ...breakfast,
        participations: [
          {
            ...breakfast.participations[0],
            items: [
              {
                id: itemId,
                breakfastId,
                participationId,
                name: 'Suco de acerola',
                status: 'PENDENTE',
              },
            ],
          },
        ],
      };

      request.reply({ statusCode: 201, body: itemId });
    }).as('createItem');

    cy.intercept('PATCH', `${apiUrl}/items/${itemId}/status`, (request) => {
      expect(request.body).to.deep.equal({
        status: 'TROUXE',
      });

      breakfast = {
        ...breakfast,
        participations: [
          {
            ...breakfast.participations[0],
            items: [
              {
                ...breakfast.participations[0].items[0],
                status: 'TROUXE',
              },
            ],
          },
        ],
      };

      request.reply({ statusCode: 204 });
    }).as('updateItemStatus');

    cy.intercept('PUT', `${apiUrl}/items/${itemId}`, (request) => {
      expect(request.body).to.deep.equal({
        name: 'Suco de caju',
      });

      breakfast = {
        ...breakfast,
        participations: [
          {
            ...breakfast.participations[0],
            items: [
              {
                ...breakfast.participations[0].items[0],
                name: 'Suco de caju',
              },
            ],
          },
        ],
      };

      request.reply({ statusCode: 204 });
    }).as('updateItem');

    cy.intercept('DELETE', `${apiUrl}/items/${itemId}`, (request) => {
      breakfast = {
        ...breakfast,
        participations: [
          {
            ...breakfast.participations[0],
            items: [],
          },
        ],
      };

      request.reply({ statusCode: 204 });
    }).as('deleteItem');

    cy.intercept('DELETE', `${apiUrl}/participations/${participationId}`, (request) => {
      breakfast = {
        ...breakfast,
        participations: [],
      };

      request.reply({ statusCode: 204 });
    }).as('deleteParticipation');

    cy.visit(`/breakfasts/${breakfastId}`);
    cy.wait('@getBreakfast');

    cy.contains('Sala Recife').should('be.visible');
    cy.contains('Nenhum participante cadastrado.').should('be.visible');

    cy.get('#participantName').type('Ana Recife');
    cy.get('#participantCpf').type('73244216013');
    cy.contains('button', 'Cadastrar').click();

    cy.wait('@createParticipation');
    cy.wait('@getBreakfast');
    cy.contains('Participação cadastrada.').should('be.visible');
    cy.contains('Ana Recife').should('be.visible');

    cy.get('#itemName').type('Suco de acerola');
    cy.contains('button', 'Adicionar item').click();

    cy.wait('@createItem');
    cy.wait('@getBreakfast');
    cy.contains('Item cadastrado.').should('be.visible');
    cy.contains('Suco de acerola').should('be.visible');
    cy.contains('Marcação liberada para o café de hoje.').should('be.visible');

    cy.contains('li', 'Suco de acerola').within(() => {
      cy.contains('button', 'Trouxe').click();
    });

    cy.wait('@updateItemStatus');
    cy.wait('@getBreakfast');
    cy.contains('Status atualizado.').should('be.visible');
    cy.get('.status-badge').contains('Trouxe').should('be.visible');

    cy.contains('li', 'Suco de acerola').within(() => {
      cy.contains('button', 'Editar').click();
    });
    cy.get('input[id^="edit-item-"]').clear().type('Suco de caju');
    cy.contains('button', 'Salvar').click();

    cy.wait('@updateItem');
    cy.wait('@getBreakfast');
    cy.contains('Item atualizado.').should('be.visible');
    cy.contains('Suco de caju').should('be.visible');

    cy.contains('li', 'Suco de caju').within(() => {
      cy.contains('button', 'Excluir').click();
    });

    cy.wait('@deleteItem');
    cy.wait('@getBreakfast');
    cy.contains('Item excluído.').should('be.visible');
    cy.contains('Nenhum item cadastrado para esta participação.').should('be.visible');

    cy.contains('.participant-row', 'Ana Recife').within(() => {
      cy.contains('button', 'Excluir').click();
    });

    cy.wait('@deleteParticipation');
    cy.wait('@getBreakfast');
    cy.contains('Participação excluída.').should('be.visible');
    cy.contains('Nenhum participante cadastrado.').should('be.visible');
  });
});
