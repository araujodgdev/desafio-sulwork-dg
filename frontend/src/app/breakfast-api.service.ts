import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import {
  Breakfast,
  CreateBreakfastRequest,
  CreateItemRequest,
  CreateParticipationRequest,
  ItemStatus,
} from './breakfast.types';

@Injectable({
  providedIn: 'root',
})
export class BreakfastApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080';

  listBreakfasts(): Observable<Breakfast[]> {
    return this.http.get<Breakfast[]>(`${this.baseUrl}/breakfasts`);
  }

  getBreakfast(id: number): Observable<Breakfast> {
    return this.http.get<Breakfast>(`${this.baseUrl}/breakfasts/${id}`);
  }

  createBreakfast(payload: CreateBreakfastRequest): Observable<number> {
    return this.http.post<number>(`${this.baseUrl}/breakfasts`, payload);
  }

  createParticipation(payload: CreateParticipationRequest): Observable<number> {
    return this.http.post<number>(`${this.baseUrl}/participations`, payload);
  }

  createItem(participationId: number, payload: CreateItemRequest): Observable<number> {
    return this.http.post<number>(
      `${this.baseUrl}/participations/${participationId}/items`,
      payload,
    );
  }

  updateItemStatus(itemId: number, status: ItemStatus): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/items/${itemId}/status`, {
      status,
    });
  }
}
