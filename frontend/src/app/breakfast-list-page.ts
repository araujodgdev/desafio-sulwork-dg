import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { resolveHttpErrorMessage } from './api-error';
import { BreakfastApiService } from './breakfast-api.service';
import { formatDate, formatTime, futureDateValidator, getTomorrowDate } from './breakfast-date';
import { Breakfast, CreateBreakfastRequest } from './breakfast.types';

@Component({
  selector: 'app-breakfast-list-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './breakfast-list-page.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BreakfastListPage implements OnInit {
  private readonly breakfastApi = inject(BreakfastApiService);
  private readonly formBuilder = inject(FormBuilder);

  readonly breakfasts = signal<Breakfast[]>([]);
  readonly isLoadingList = signal(true);
  readonly isSaving = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);
  readonly minBreakfastDate = getTomorrowDate();

  readonly form = this.formBuilder.nonNullable.group({
    breakfastDate: ['', [Validators.required, futureDateValidator]],
    breakfastTime: ['08:30', [Validators.required]],
    location: ['', [Validators.required, Validators.maxLength(120)]],
  });

  readonly sortedBreakfasts = computed(() =>
    [...this.breakfasts()].sort((current, next) =>
      current.breakfastDate.localeCompare(next.breakfastDate),
    ),
  );

  ngOnInit(): void {
    this.loadBreakfasts();
  }

  loadBreakfasts(): void {
    this.isLoadingList.set(true);

    this.breakfastApi
      .listBreakfasts()
      .pipe(finalize(() => this.isLoadingList.set(false)))
      .subscribe({
        next: (breakfasts) => {
          this.breakfasts.set(breakfasts);
        },
        error: (error: unknown) => {
          this.errorMessage.set(resolveHttpErrorMessage(error));
        },
      });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload: CreateBreakfastRequest = this.form.getRawValue();

    this.isSaving.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.breakfastApi
      .createBreakfast(payload)
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: (breakfastId) => {
          this.successMessage.set(`Café da manhã #${breakfastId} criado.`);
          this.form.reset({
            breakfastDate: '',
            breakfastTime: '08:30',
            location: '',
          });
          this.loadBreakfasts();
        },
        error: (error: unknown) => {
          this.errorMessage.set(resolveHttpErrorMessage(error));
        },
      });
  }

  hasFieldError(controlName: keyof CreateBreakfastRequest): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.dirty || control.touched);
  }

  dateErrorMessage(): string {
    const control = this.form.controls.breakfastDate;

    if (control.hasError('required')) {
      return 'Informe a data do café.';
    }

    if (control.hasError('futureDate')) {
      return 'A data precisa ser maior que hoje.';
    }

    return 'Data inválida.';
  }

  locationErrorMessage(): string {
    const control = this.form.controls.location;

    if (control.hasError('required')) {
      return 'Informe o local do café.';
    }

    if (control.hasError('maxlength')) {
      return 'Use no máximo 120 caracteres.';
    }

    return 'Local inválido.';
  }

  formatDate(value: string): string {
    return formatDate(value);
  }

  formatTime(value: string | null): string {
    return formatTime(value);
  }
}
