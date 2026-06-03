import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Breakfast, CreateBreakfastRequest } from './breakfast.types';

@Injectable({
  providedIn: 'root',
})
export class BreakfastApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080';

  listBreakfasts(): Observable<Breakfast[]> {
    return this.http.get<Breakfast[]>(`${this.baseUrl}/breakfasts`);
  }

  createBreakfast(payload: CreateBreakfastRequest): Observable<number> {
    return this.http.post<number>(`${this.baseUrl}/breakfasts`, payload);
  }
}
