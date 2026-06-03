export interface Breakfast {
  id: number;
  breakfastDate: string;
  breakfastTime: string | null;
  location: string;
  createdDateTime: string;
}

export interface CreateBreakfastRequest {
  breakfastDate: string;
  breakfastTime: string;
  location: string;
}
