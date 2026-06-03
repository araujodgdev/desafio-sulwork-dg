import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { resolveHttpErrorMessage } from './api-error';
import { BreakfastApiService } from './breakfast-api.service';
import { formatDate, formatTime } from './breakfast-date';
import { Breakfast, ItemStatus } from './breakfast.types';
import { cpfValidator, onlyCpfDigits } from './cpf.validator';

@Component({
  selector: 'app-breakfast-detail-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './breakfast-detail-page.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BreakfastDetailPage implements OnInit {
  private readonly breakfastApi = inject(BreakfastApiService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly breakfast = signal<Breakfast | null>(null);
  readonly selectedParticipationId = signal<number | null>(null);
  readonly isLoading = signal(true);
  readonly isSavingParticipation = signal(false);
  readonly isSavingItem = signal(false);
  readonly isDeletingBreakfast = signal(false);
  readonly deletingParticipationId = signal<number | null>(null);
  readonly deletingItemId = signal<number | null>(null);
  readonly editingItemId = signal<number | null>(null);
  readonly isSavingItemEdit = signal(false);
  readonly updatingItemId = signal<number | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  readonly participationForm = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
    cpf: ['', [Validators.required, cpfValidator]],
  });

  readonly itemForm = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
  });

  readonly itemEditForm = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
  });

  readonly participations = computed(() =>
    [...(this.breakfast()?.participations ?? [])].sort((current, next) =>
      current.collaborator.name.localeCompare(next.collaborator.name),
    ),
  );

  readonly selectedParticipation = computed(() => {
    const selectedId = this.selectedParticipationId();
    return this.participations().find((participation) => participation.id === selectedId) ?? null;
  });

  readonly breakfastStatusMode = computed<'today' | 'future' | 'past' | 'unknown'>(() => {
    const breakfast = this.breakfast();

    if (!breakfast) {
      return 'unknown';
    }

    const today = this.todayDateValue();

    if (breakfast.breakfastDate === today) {
      return 'today';
    }

    return breakfast.breakfastDate > today ? 'future' : 'past';
  });

  private get breakfastId(): number {
    return Number(this.route.snapshot.paramMap.get('id'));
  }

  ngOnInit(): void {
    this.loadBreakfast();
  }

  loadBreakfast(): void {
    if (!Number.isFinite(this.breakfastId)) {
      this.errorMessage.set('Café da manhã inválido.');
      this.isLoading.set(false);
      return;
    }

    this.isLoading.set(true);

    this.breakfastApi
      .getBreakfast(this.breakfastId)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (breakfast) => this.setBreakfast(breakfast),
        error: (error: unknown) => {
          this.errorMessage.set(resolveHttpErrorMessage(error));
        },
      });
  }

  createParticipation(): void {
    if (this.participationForm.invalid) {
      this.participationForm.markAllAsTouched();
      return;
    }

    const formValue = this.participationForm.getRawValue();

    this.isSavingParticipation.set(true);
    this.clearMessages();

    this.breakfastApi
      .createParticipation({
        breakfastId: this.breakfastId,
        name: formValue.name.trim(),
        cpf: onlyCpfDigits(formValue.cpf),
      })
      .pipe(finalize(() => this.isSavingParticipation.set(false)))
      .subscribe({
        next: (participationId) => {
          this.successMessage.set('Participação cadastrada.');
          this.selectedParticipationId.set(participationId);
          this.participationForm.reset({ name: '', cpf: '' });
          this.loadBreakfast();
        },
        error: (error: unknown) => {
          this.errorMessage.set(resolveHttpErrorMessage(error));
        },
      });
  }

  createItem(): void {
    if (this.itemForm.invalid) {
      this.itemForm.markAllAsTouched();
      return;
    }

    const participationId = this.selectedParticipationId();

    if (!participationId) {
      this.errorMessage.set('Selecione uma participação.');
      return;
    }

    this.isSavingItem.set(true);
    this.clearMessages();

    this.breakfastApi
      .createItem(participationId, {
        name: this.itemForm.controls.name.value.trim(),
      })
      .pipe(finalize(() => this.isSavingItem.set(false)))
      .subscribe({
        next: () => {
          this.successMessage.set('Item cadastrado.');
          this.itemForm.reset({ name: '' });
          this.loadBreakfast();
        },
        error: (error: unknown) => {
          this.errorMessage.set(resolveHttpErrorMessage(error));
        },
      });
  }

  updateItemStatus(itemId: number, status: ItemStatus): void {
    if (this.breakfastStatusMode() !== 'today') {
      this.errorMessage.set('Status só pode ser atualizado no dia do café.');
      return;
    }

    this.updatingItemId.set(itemId);
    this.clearMessages();

    this.breakfastApi
      .updateItemStatus(itemId, status)
      .pipe(finalize(() => this.updatingItemId.set(null)))
      .subscribe({
        next: () => {
          this.successMessage.set('Status atualizado.');
          this.loadBreakfast();
        },
        error: (error: unknown) => {
          this.errorMessage.set(resolveHttpErrorMessage(error));
        },
      });
  }

  selectParticipation(participationId: number): void {
    this.selectedParticipationId.set(participationId);
    this.errorMessage.set(null);
  }

  deleteBreakfast(): void {
    const breakfast = this.breakfast();

    if (!breakfast) {
      return;
    }

    this.isDeletingBreakfast.set(true);
    this.clearMessages();

    this.breakfastApi
      .deleteBreakfast(breakfast.id)
      .pipe(finalize(() => this.isDeletingBreakfast.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigate(['/']);
        },
        error: (error: unknown) => {
          this.errorMessage.set(resolveHttpErrorMessage(error));
        },
      });
  }

  deleteParticipation(participationId: number): void {
    this.deletingParticipationId.set(participationId);
    this.clearMessages();

    this.breakfastApi
      .deleteParticipation(participationId)
      .pipe(finalize(() => this.deletingParticipationId.set(null)))
      .subscribe({
        next: () => {
          this.successMessage.set('Participação excluída.');
          if (this.selectedParticipationId() === participationId) {
            this.selectedParticipationId.set(null);
          }
          this.loadBreakfast();
        },
        error: (error: unknown) => {
          this.errorMessage.set(resolveHttpErrorMessage(error));
        },
      });
  }

  startEditItem(itemId: number, name: string): void {
    this.editingItemId.set(itemId);
    this.itemEditForm.setValue({ name });
    this.errorMessage.set(null);
  }

  cancelEditItem(): void {
    this.editingItemId.set(null);
    this.itemEditForm.reset({ name: '' });
  }

  updateItem(itemId: number): void {
    if (this.itemEditForm.invalid) {
      this.itemEditForm.markAllAsTouched();
      return;
    }

    this.isSavingItemEdit.set(true);
    this.clearMessages();

    this.breakfastApi
      .updateItem(itemId, {
        name: this.itemEditForm.controls.name.value.trim(),
      })
      .pipe(finalize(() => this.isSavingItemEdit.set(false)))
      .subscribe({
        next: () => {
          this.successMessage.set('Item atualizado.');
          this.cancelEditItem();
          this.loadBreakfast();
        },
        error: (error: unknown) => {
          this.errorMessage.set(resolveHttpErrorMessage(error));
        },
      });
  }

  deleteItem(itemId: number): void {
    this.deletingItemId.set(itemId);
    this.clearMessages();

    this.breakfastApi
      .deleteItem(itemId)
      .pipe(finalize(() => this.deletingItemId.set(null)))
      .subscribe({
        next: () => {
          this.successMessage.set('Item excluído.');
          if (this.editingItemId() === itemId) {
            this.cancelEditItem();
          }
          this.loadBreakfast();
        },
        error: (error: unknown) => {
          this.errorMessage.set(resolveHttpErrorMessage(error));
        },
      });
  }

  hasParticipationFieldError(controlName: 'name' | 'cpf'): boolean {
    const control = this.participationForm.controls[controlName];
    return control.invalid && (control.dirty || control.touched);
  }

  hasItemFieldError(): boolean {
    const control = this.itemForm.controls.name;
    return control.invalid && (control.dirty || control.touched);
  }

  hasItemEditFieldError(): boolean {
    const control = this.itemEditForm.controls.name;
    return control.invalid && (control.dirty || control.touched);
  }

  cpfErrorMessage(): string {
    const control = this.participationForm.controls.cpf;

    if (control.hasError('required')) {
      return 'Informe o CPF.';
    }

    if (control.hasError('cpf')) {
      return 'Informe um CPF válido com 11 dígitos.';
    }

    return 'CPF inválido.';
  }

  formatDate(value: string): string {
    return formatDate(value);
  }

  formatTime(value: string | null): string {
    return formatTime(value);
  }

  formatStatus(status: ItemStatus): string {
    const labels: Record<ItemStatus, string> = {
      PENDENTE: 'Pendente',
      TROUXE: 'Trouxe',
      NAO_TROUXE: 'Não trouxe',
    };

    return labels[status];
  }

  statusNote(): string {
    const mode = this.breakfastStatusMode();

    if (mode === 'today') {
      return 'Marcação liberada para o café de hoje.';
    }

    if (mode === 'future') {
      return 'A marcação ficará disponível no dia do café.';
    }

    if (mode === 'past') {
      return 'Café encerrado. Itens pendentes são marcados como não trouxe.';
    }

    return '';
  }

  private setBreakfast(breakfast: Breakfast): void {
    this.breakfast.set({
      ...breakfast,
      participations: breakfast.participations ?? [],
    });

    const participations = breakfast.participations ?? [];
    const selectedId = this.selectedParticipationId();
    const selectedStillExists = participations.some(
      (participation) => participation.id === selectedId,
    );

    if (!selectedStillExists) {
      this.selectedParticipationId.set(participations[0]?.id ?? null);
    }
  }

  private clearMessages(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);
  }

  private todayDateValue(): string {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }
}
