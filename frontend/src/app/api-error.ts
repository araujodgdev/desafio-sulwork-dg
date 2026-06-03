import { HttpErrorResponse } from '@angular/common/http';

export function resolveHttpErrorMessage(error: unknown): string {
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

  if (error.status === 404) {
    return 'Registro não encontrado.';
  }

  return 'O backend retornou um erro inesperado.';
}
