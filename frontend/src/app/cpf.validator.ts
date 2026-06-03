import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const cpfValidator: ValidatorFn = (
  control: AbstractControl<string>,
): ValidationErrors | null => {
  const digits = onlyCpfDigits(control.value ?? '');

  if (!digits) {
    return null;
  }

  if (digits.length !== 11 || /^(\d)\1{10}$/.test(digits)) {
    return { cpf: true };
  }

  const firstDigit = calculateCpfDigit(digits.slice(0, 9), 10);
  const secondDigit = calculateCpfDigit(`${digits.slice(0, 9)}${firstDigit}`, 11);

  return digits.endsWith(`${firstDigit}${secondDigit}`) ? null : { cpf: true };
};

export function onlyCpfDigits(value: string): string {
  return value.replace(/\D/g, '');
}

function calculateCpfDigit(base: string, initialWeight: number): number {
  const total = base
    .split('')
    .reduce((sum, digit, index) => sum + Number(digit) * (initialWeight - index), 0);

  const rest = total % 11;
  return rest < 2 ? 0 : 11 - rest;
}
