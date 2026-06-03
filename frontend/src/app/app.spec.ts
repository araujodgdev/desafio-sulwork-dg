import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { App } from './app';
import { Breakfast } from './breakfast.types';

const apiUrl = 'http://localhost:8080/breakfasts';

describe('App', () => {
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should render the breakfast workspace', () => {
    const fixture = createComponentWithBreakfasts([]);
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.querySelector('h1')?.textContent).toContain('Cafés da manhã');
    expect(compiled.textContent).toContain('Novo café da manhã');
    expect(compiled.textContent).toContain('Cafés cadastrados');
  });

  it('should block submit when the form is invalid', () => {
    const fixture = createComponentWithBreakfasts([]);
    const app = fixture.componentInstance;

    app.submit();

    expect(app.form.invalid).toBe(true);
    expect(httpMock.match(apiUrl).length).toBe(0);
  });

  it('should create a breakfast and reload the list', () => {
    const fixture = createComponentWithBreakfasts([]);
    const app = fixture.componentInstance;

    app.form.setValue({
      breakfastDate: '2099-06-10',
      breakfastTime: '08:30',
      location: 'Sala de reunião',
    });

    app.submit();

    const createRequest = httpMock.expectOne(apiUrl);
    expect(createRequest.request.method).toBe('POST');
    expect(createRequest.request.body).toEqual({
      breakfastDate: '2099-06-10',
      breakfastTime: '08:30',
      location: 'Sala de reunião',
    });
    createRequest.flush(42);

    const reloadRequest = httpMock.expectOne(apiUrl);
    expect(reloadRequest.request.method).toBe('GET');
    reloadRequest.flush([
      {
        id: 42,
        breakfastDate: '2099-06-10',
        breakfastTime: '08:30',
        location: 'Sala de reunião',
        createdDateTime: '2099-01-01T08:00:00',
      },
    ]);

    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Café da manhã #42 criado.');
    expect(compiled.textContent).toContain('10/06/2099');
    expect(compiled.textContent).toContain('Sala de reunião');
  });

  function createComponentWithBreakfasts(breakfasts: Breakfast[]): ComponentFixture<App> {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const listRequest = httpMock.expectOne(apiUrl);
    expect(listRequest.request.method).toBe('GET');
    listRequest.flush(breakfasts);

    fixture.detectChanges();
    return fixture;
  }
});
