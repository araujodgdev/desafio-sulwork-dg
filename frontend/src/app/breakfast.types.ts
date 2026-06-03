export interface Breakfast {
  id: number;
  breakfastDate: string;
  breakfastTime: string | null;
  location: string;
  createdDateTime: string;
  participations?: Participation[];
}

export interface CreateBreakfastRequest {
  breakfastDate: string;
  breakfastTime: string;
  location: string;
}

export type UpdateBreakfastRequest = CreateBreakfastRequest;

export interface Collaborator {
  id: number;
  name: string;
  cpf: string;
}

export interface Participation {
  id: number;
  breakfastId: number;
  collaboratorId: number;
  collaborator: Collaborator;
  items: BreakfastItem[];
}

export interface BreakfastItem {
  id: number;
  breakfastId: number;
  participationId: number;
  name: string;
  status: ItemStatus;
}

export interface CreateParticipationRequest {
  breakfastId: number;
  name: string;
  cpf: string;
}

export interface CreateItemRequest {
  name: string;
}

export type UpdateItemRequest = CreateItemRequest;

export type ItemStatus = 'PENDENTE' | 'TROUXE' | 'NAO_TROUXE';

export interface UpdateItemStatusRequest {
  status: ItemStatus;
}
