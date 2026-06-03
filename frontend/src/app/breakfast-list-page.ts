import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable, finalize } from 'rxjs';

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
export class BreakfastListPage implements OnInit, OnDestroy {
  private readonly breakfastApi = inject(BreakfastApiService);
  private readonly formBuilder = inject(FormBuilder);

  readonly breakfasts = signal<Breakfast[]>([]);
  readonly isLoadingList = signal(true);
  readonly isSaving = signal(false);
  readonly deletingBreakfastId = signal<number | null>(null);
  readonly editingBreakfastId = signal<number | null>(null);
  readonly pendingDeleteBreakfastId = signal<number | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly listError = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);
  readonly minBreakfastDate = getTomorrowDate();

  private successTimer: ReturnType<typeof setTimeout> | null = null;

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

  ngOnDestroy(): void {
    this.clearSuccessTimer();
  }

  loadBreakfasts(): void {
    this.isLoadingList.set(true);
    this.listError.set(null);
    this.pendingDeleteBreakfastId.set(null);

    this.breakfastApi
      .listBreakfasts()
      .pipe(finalize(() => this.isLoadingList.set(false)))
      .subscribe({
        next: (breakfasts) => {
          this.breakfasts.set(breakfasts);
        },
        error: (error: unknown) => {
          this.listError.set(resolveHttpErrorMessage(error));
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

    const editingId = this.editingBreakfastId();
    const request$: Observable<number | void> = editingId
      ? this.breakfastApi.updateBreakfast(editingId, payload)
      : this.breakfastApi.createBreakfast(payload);

    request$.pipe(finalize(() => this.isSaving.set(false))).subscribe({
      next: (breakfastId) => {
        this.showSuccess(
          editingId ? 'Café da manhã atualizado.' : `Café da manhã #${breakfastId} criado.`,
        );
        this.resetForm();
        this.loadBreakfasts();
      },
      error: (error: unknown) => {
        this.errorMessage.set(resolveHttpErrorMessage(error));
      },
    });
  }

  startEditBreakfast(breakfast: Breakfast): void {
    this.editingBreakfastId.set(breakfast.id);
    this.pendingDeleteBreakfastId.set(null);
    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.form.setValue({
      breakfastDate: breakfast.breakfastDate,
      breakfastTime: this.formatTimeForInput(breakfast.breakfastTime),
      location: breakfast.location,
    });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  requestDeleteBreakfast(id: number): void {
    this.pendingDeleteBreakfastId.set(id);
    this.errorMessage.set(null);
  }

  cancelDeleteBreakfast(): void {
    this.pendingDeleteBreakfastId.set(null);
  }

  deleteBreakfast(id: number): void {
    this.deletingBreakfastId.set(id);
    this.errorMessage.set(null);
    this.clearSuccess();

    this.breakfastApi
      .deleteBreakfast(id)
      .pipe(finalize(() => this.deletingBreakfastId.set(null)))
      .subscribe({
        next: () => {
          this.showSuccess('Café da manhã excluído.');
          if (this.editingBreakfastId() === id) {
            this.resetForm();
          }
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

  submitButtonLabel(): string {
    if (this.isSaving()) {
      return 'Salvando...';
    }

    return this.editingBreakfastId() ? 'Salvar alterações' : 'Criar café';
  }

  private resetForm(): void {
    this.editingBreakfastId.set(null);
    this.form.reset({
      breakfastDate: '',
      breakfastTime: '08:30',
      location: '',
    });
  }

  private formatTimeForInput(value: string | null): string {
    return value ? value.slice(0, 5) : '08:30';
  }

  private showSuccess(message: string): void {
    this.successMessage.set(message);
    this.clearSuccessTimer();
    this.successTimer = setTimeout(() => {
      this.successMessage.set(null);
      this.successTimer = null;
    }, 4000);
  }

  private clearSuccess(): void {
    this.successMessage.set(null);
    this.clearSuccessTimer();
  }

  private clearSuccessTimer(): void {
    if (this.successTimer !== null) {
      clearTimeout(this.successTimer);
      this.successTimer = null;
    }
  }
}
