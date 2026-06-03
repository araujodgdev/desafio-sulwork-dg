import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import {
  Breakfast,
  CreateBreakfastRequest,
  CreateItemRequest,
  CreateParticipationRequest,
  ItemStatus,
  UpdateBreakfastRequest,
  UpdateItemRequest,
} from './breakfast.types';

@Injectable({
  providedIn: 'root',
})
export class BreakfastApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'https://gracious-encouragement-production.up.railway.app';

  listBreakfasts(): Observable<Breakfast[]> {
    return this.http.get<Breakfast[]>(`${this.baseUrl}/breakfasts`);
  }

  getBreakfast(id: number): Observable<Breakfast> {
    return this.http.get<Breakfast>(`${this.baseUrl}/breakfasts/${id}`);
  }

  createBreakfast(payload: CreateBreakfastRequest): Observable<number> {
    return this.http.post<number>(`${this.baseUrl}/breakfasts`, payload);
  }

  updateBreakfast(id: number, payload: UpdateBreakfastRequest): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/breakfasts/${id}`, payload);
  }

  deleteBreakfast(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/breakfasts/${id}`);
  }

  createParticipation(payload: CreateParticipationRequest): Observable<number> {
    return this.http.post<number>(`${this.baseUrl}/participations`, payload);
  }

  deleteParticipation(participationId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/participations/${participationId}`);
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

  updateItem(itemId: number, payload: UpdateItemRequest): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/items/${itemId}`, payload);
  }

  deleteItem(itemId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/items/${itemId}`);
  }
}
