
import { AddPacketComponent } from './packet/add-packet/add-packet.component';
import { ConfigComponent } from './config/config.component';
import { ListModelsComponent } from './model/list-models/list-models.component';
import { ListOffersComponent } from './offer/list-offers/list-offers.component';
import { StockComponent } from './stock/stock.component';
import { ListPacketsComponent } from './packet/list-packets/list-packets.component';
import { ListSizesComponent } from './config/size/list-sizes/list-sizes.component';
import { StatistiqueComponent } from './statistique/statistique.component';
import { PayedReturnComponent } from './payed-return/payed-return.component';
import { AuthGuard } from 'src/shared/services/auth-gard.service';
import { Roles } from 'src/shared/enums/roles';
import { VerificationComponent } from './verification/verification.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import 'tslib';

const routes: Routes = [
  { path: "", redirectTo: "/packets", pathMatch: "full" },
  {
    path: "models", "component": ListModelsComponent, canActivate: [AuthGuard], data: {
      role: [Roles.ADMIN]
    }
  },
  {
    path: "offers", "component": ListOffersComponent, canActivate: [AuthGuard], data: {
      role: [Roles.ADMIN]
    }
  },
  {
    path: "config", "component": ConfigComponent, canActivate: [AuthGuard], data: {
      role: [Roles.ADMIN]
    }
  },
  {
    path: "stock", "component": StockComponent, canActivate: [AuthGuard], data: {
      role: [Roles.ADMIN, Roles.USER]
    }
  },
  { path: "sizes", "component": ListSizesComponent, canActivate: [AuthGuard], data: {
      role: [Roles.ADMIN]
    }
  },
  {
    path: "statistique", "component": StatistiqueComponent, canActivate: [AuthGuard], data: {
      role: [Roles.ADMIN]
    }
  },
  {
    path: "payed-return", "component": PayedReturnComponent, canActivate: [AuthGuard], data: {
      role: [Roles.ADMIN,, Roles.USER]
    }
  },
  { path: "verification", "component": VerificationComponent },
  {
    path: "packets", "component": ListPacketsComponent, canActivate: [AuthGuard], data: {
      role: [Roles.ADMIN, Roles.USER]
    }, children: [
      { path: "add", "component": AddPacketComponent }
    ]
  },
  { path: 'login', loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) },

];

@NgModule({
  imports: [RouterModule.forRoot(routes , { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
