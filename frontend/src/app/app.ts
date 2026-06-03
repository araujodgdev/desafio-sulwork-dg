import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { finalize } from 'rxjs';

import { BreakfastApiService } from './breakfast-api.service';
import { Breakfast, CreateBreakfastRequest } from './breakfast.types';

const futureDateValidator: ValidatorFn = (
  control: AbstractControl<string>,
): ValidationErrors | null => {
  if (!control.value) {
    return null;
  }

  const selectedDate = new Date(`${control.value}T00:00:00`);
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  return selectedDate > today ? null : { futureDate: true };
};

@Component({
  selector: 'app-root',
  imports: [ReactiveFormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App implements OnInit {
  private readonly breakfastApi = inject(BreakfastApiService);
  private readonly formBuilder = inject(FormBuilder);

  readonly breakfasts = signal<Breakfast[]>([]);
  readonly isLoadingList = signal(true);
  readonly isSaving = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);
  readonly minBreakfastDate = this.getTomorrowDate();

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
          this.errorMessage.set(this.resolveErrorMessage(error));
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
          this.errorMessage.set(this.resolveErrorMessage(error));
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
    const [year, month, day] = value.split('-');
    return `${day}/${month}/${year}`;
  }

  formatTime(value: string | null): string {
    if (!value) {
      return 'Sem horário';
    }

    return value.slice(0, 5);
  }

  private resolveErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Não foi possível concluir a operação.';
    }

    if (error.status === 0) {
      return 'Não foi possível conectar ao backend em localhost:8080.';
    }

    if (typeof error.error === 'string' && error.error.trim()) {
      return error.error;
    }

    if (
      error.error &&
      typeof error.error === 'object' &&
      'message' in error.error &&
      typeof error.error.message === 'string'
    ) {
      return error.error.message;
    }

    if (error.status === 400) {
      return 'Revise os dados informados e tente novamente.';
    }

    return 'O backend retornou um erro inesperado.';
  }

  private getTomorrowDate(): string {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);

    const year = tomorrow.getFullYear();
    const month = String(tomorrow.getMonth() + 1).padStart(2, '0');
    const day = String(tomorrow.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }
}
