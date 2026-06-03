import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const futureDateValidator: ValidatorFn = (
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

export function getTomorrowDate(): string {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);

  const year = tomorrow.getFullYear();
  const month = String(tomorrow.getMonth() + 1).padStart(2, '0');
  const day = String(tomorrow.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
}

export function formatDate(value: string): string {
  const [year, month, day] = value.split('-');
  return `${day}/${month}/${year}`;
}

export function formatTime(value: string | null): string {
  if (!value) {
    return 'Sem horário';
  }

  return value.slice(0, 5);
}
