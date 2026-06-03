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
}

export interface CreateParticipationRequest {
  breakfastId: number;
  name: string;
  cpf: string;
}

export interface CreateItemRequest {
  name: string;
}
