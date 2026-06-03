import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';

import { BreakfastDetailPage } from './breakfast-detail-page';
import { BreakfastListPage } from './breakfast-list-page';
import { Breakfast } from './breakfast.types';

const apiBaseUrl = 'http://localhost:8080';

const breakfastFixture: Breakfast = {
  id: 42,
  breakfastDate: '2099-06-10',
  breakfastTime: '08:30',
  location: 'Sala de reunião',
  createdDateTime: '2099-01-01T08:00:00',
  participations: [],
};

describe('BreakfastListPage', () => {
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BreakfastListPage],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should render the breakfast workspace', () => {
    const fixture = createListComponentWithBreakfasts([]);
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Novo café da manhã');
    expect(compiled.textContent).toContain('Cafés cadastrados');
  });

  it('should block submit when the form is invalid', () => {
    const fixture = createListComponentWithBreakfasts([]);
    const page = fixture.componentInstance;

    page.submit();

    expect(page.form.invalid).toBe(true);
    expect(httpMock.match(`${apiBaseUrl}/breakfasts`).length).toBe(0);
  });

  it('should create a breakfast and reload the list', () => {
    const fixture = createListComponentWithBreakfasts([]);
    const page = fixture.componentInstance;

    page.form.setValue({
      breakfastDate: '2099-06-10',
      breakfastTime: '08:30',
      location: 'Sala de reunião',
    });

    page.submit();

    const createRequest = httpMock.expectOne(`${apiBaseUrl}/breakfasts`);
    expect(createRequest.request.method).toBe('POST');
    expect(createRequest.request.body).toEqual({
      breakfastDate: '2099-06-10',
      breakfastTime: '08:30',
      location: 'Sala de reunião',
    });
    createRequest.flush(42);

    const reloadRequest = httpMock.expectOne(`${apiBaseUrl}/breakfasts`);
    expect(reloadRequest.request.method).toBe('GET');
    reloadRequest.flush([breakfastFixture]);

    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Café da manhã #42 criado.');
    expect(compiled.textContent).toContain('10/06/2099');
    expect(compiled.textContent).toContain('Sala de reunião');
  });

  function createListComponentWithBreakfasts(
    breakfasts: Breakfast[],
  ): ComponentFixture<BreakfastListPage> {
    const fixture = TestBed.createComponent(BreakfastListPage);
    fixture.detectChanges();

    const listRequest = httpMock.expectOne(`${apiBaseUrl}/breakfasts`);
    expect(listRequest.request.method).toBe('GET');
    listRequest.flush(breakfasts);

    fixture.detectChanges();
    return fixture;
  }
});

describe('BreakfastDetailPage', () => {
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BreakfastDetailPage],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ id: '42' }),
            },
          },
        },
      ],
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should render breakfast details with participations', () => {
    const fixture = createDetailComponentWithBreakfast({
      ...breakfastFixture,
      participations: [
        {
          id: 7,
          breakfastId: 42,
          collaboratorId: 3,
          collaborator: {
            id: 3,
            name: 'João',
            cpf: '73244216013',
          },
          items: [],
        },
      ],
    });
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('10/06/2099');
    expect(compiled.textContent).toContain('João');
    expect(compiled.textContent).toContain('CPF 73244216013');
  });

  it('should create a participation and reload details', () => {
    const fixture = createDetailComponentWithBreakfast(breakfastFixture);
    const page = fixture.componentInstance;

    page.participationForm.setValue({
      name: 'Maria',
      cpf: '52998224725',
    });

    page.createParticipation();

    const createRequest = httpMock.expectOne(`${apiBaseUrl}/participations`);
    expect(createRequest.request.method).toBe('POST');
    expect(createRequest.request.body).toEqual({
      breakfastId: 42,
      name: 'Maria',
      cpf: '52998224725',
    });
    createRequest.flush(9);

    const reloadRequest = httpMock.expectOne(`${apiBaseUrl}/breakfasts/42`);
    expect(reloadRequest.request.method).toBe('GET');
    reloadRequest.flush({
      ...breakfastFixture,
      participations: [
        {
          id: 9,
          breakfastId: 42,
          collaboratorId: 4,
          collaborator: {
            id: 4,
            name: 'Maria',
            cpf: '52998224725',
          },
          items: [],
        },
      ],
    });

    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Participação cadastrada.');
    expect(compiled.textContent).toContain('Maria');
  });

  it('should create an item for the selected participation and reload details', () => {
    const fixture = createDetailComponentWithBreakfast({
      ...breakfastFixture,
      participations: [
        {
          id: 7,
          breakfastId: 42,
          collaboratorId: 3,
          collaborator: {
            id: 3,
            name: 'João',
            cpf: '73244216013',
          },
          items: [],
        },
      ],
    });
    const page = fixture.componentInstance;

    page.itemForm.setValue({
      name: 'Queijo',
    });

    page.createItem();

    const createRequest = httpMock.expectOne(`${apiBaseUrl}/participations/7/items`);
    expect(createRequest.request.method).toBe('POST');
    expect(createRequest.request.body).toEqual({ name: 'Queijo' });
    createRequest.flush(99);

    const reloadRequest = httpMock.expectOne(`${apiBaseUrl}/breakfasts/42`);
    expect(reloadRequest.request.method).toBe('GET');
    reloadRequest.flush({
      ...breakfastFixture,
      participations: [
        {
          id: 7,
          breakfastId: 42,
          collaboratorId: 3,
          collaborator: {
            id: 3,
            name: 'João',
            cpf: '73244216013',
          },
          items: [
            {
              id: 99,
              breakfastId: 42,
              participationId: 7,
              name: 'Queijo',
              status: 'PENDENTE',
            },
          ],
        },
      ],
    });

    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Item cadastrado.');
    expect(compiled.textContent).toContain('Queijo');
  });

  it('should update item status when breakfast is today', () => {
    const today = todayDateValue();
    const fixture = createDetailComponentWithBreakfast({
      ...breakfastFixture,
      breakfastDate: today,
      participations: [
        {
          id: 7,
          breakfastId: 42,
          collaboratorId: 3,
          collaborator: {
            id: 3,
            name: 'João',
            cpf: '73244216013',
          },
          items: [
            {
              id: 99,
              breakfastId: 42,
              participationId: 7,
              name: 'Queijo',
              status: 'PENDENTE',
            },
          ],
        },
      ],
    });
    const page = fixture.componentInstance;

    page.updateItemStatus(99, 'TROUXE');

    const updateRequest = httpMock.expectOne(`${apiBaseUrl}/items/99/status`);
    expect(updateRequest.request.method).toBe('PATCH');
    expect(updateRequest.request.body).toEqual({ status: 'TROUXE' });
    updateRequest.flush(null);

    const reloadRequest = httpMock.expectOne(`${apiBaseUrl}/breakfasts/42`);
    expect(reloadRequest.request.method).toBe('GET');
    reloadRequest.flush({
      ...breakfastFixture,
      breakfastDate: today,
      participations: [
        {
          id: 7,
          breakfastId: 42,
          collaboratorId: 3,
          collaborator: {
            id: 3,
            name: 'João',
            cpf: '73244216013',
          },
          items: [
            {
              id: 99,
              breakfastId: 42,
              participationId: 7,
              name: 'Queijo',
              status: 'TROUXE',
            },
          ],
        },
      ],
    });

    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Status atualizado.');
    expect(compiled.textContent).toContain('Trouxe');
  });

  function createDetailComponentWithBreakfast(
    breakfast: Breakfast,
  ): ComponentFixture<BreakfastDetailPage> {
    const fixture = TestBed.createComponent(BreakfastDetailPage);
    fixture.detectChanges();

    const detailRequest = httpMock.expectOne(`${apiBaseUrl}/breakfasts/42`);
    expect(detailRequest.request.method).toBe('GET');
    detailRequest.flush(breakfast);

    fixture.detectChanges();
    return fixture;
  }

  function todayDateValue(): string {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }
});
