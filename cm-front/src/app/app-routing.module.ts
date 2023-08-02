import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddPacketComponent } from './packet/add-packet/add-packet.component';
import { ConfigComponent } from './config/config.component';
import { ListModelsComponent } from './model/list-models/list-models.component';
import { ListOffersComponent } from './offer/list-offers/list-offers.component';
import { StockComponent } from './stock/stock.component';
import { ListPacketsComponent } from './packet/list-packets/list-packets.component';
import { ListSizesComponent } from './config/size/list-sizes/list-sizes.component';
import { StatistiqueComponent } from './statistique/statistique.component';

const routes: Routes = [
  { path: "", redirectTo: "/packets", pathMatch: "full" },
  { path: "models", "component": ListModelsComponent },
  { path: "offers", "component": ListOffersComponent },
  { path: "config", "component": ConfigComponent },
  { path: "stock", "component": StockComponent },
  { path: "sizes", "component": ListSizesComponent },
  { path: "statistique", "component": StatistiqueComponent },
  {
    path: "packets", "component": ListPacketsComponent, children: [
      { path: "", redirectTo: "/packets", pathMatch: "full" },
      { path: "add", "component": AddPacketComponent }
    ]
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
