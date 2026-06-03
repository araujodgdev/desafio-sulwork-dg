import { Routes } from '@angular/router';

import { BreakfastDetailPage } from './breakfast-detail-page';
import { BreakfastListPage } from './breakfast-list-page';

export const routes: Routes = [
  {
    path: '',
    component: BreakfastListPage,
  },
  {
    path: 'breakfasts/:id',
    component: BreakfastDetailPage,
  },
  {
    path: '**',
    redirectTo: '',
  },
];
