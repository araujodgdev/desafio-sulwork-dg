import { Routes } from '@angular/router';

import { BreakfastDetailPage } from './breakfast-detail-page';
import { BreakfastListPage } from './breakfast-list-page';

export const routes: Routes = [
  {
    path: '',
    component: BreakfastListPage,
    title: 'Agenda de cafés | Sulwork Cafe',
  },
  {
    path: 'breakfasts/:id',
    component: BreakfastDetailPage,
    title: 'Detalhes do café | Sulwork Cafe',
  },
  {
    path: '**',
    redirectTo: '',
  },
];
