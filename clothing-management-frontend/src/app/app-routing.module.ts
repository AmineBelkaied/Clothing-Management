import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddPacketComponent } from './add-packet/add-packet.component';
import { ConfigComponent } from './config/config.component';
import { ListModelsComponent } from './list-models/list-models.component';
import { ListOffersComponent } from './list-offers/list-offers.component';
import { ListPacketsComponent } from './list-packets/list-packets.component';
import { ListSizesComponent } from './list-sizes/list-sizes.component';

const routes: Routes = [
  {path : "" , redirectTo : "/packets" , pathMatch : "full"},
  {path : "models" , "component" : ListModelsComponent},
  {path : "offers" , "component" : ListOffersComponent},
{path : "config" , "component" : ConfigComponent},
{path : "sizes" , "component" : ListSizesComponent},
  {path : "packets" , "component" : ListPacketsComponent , children :[
    {path : "" , redirectTo :"/packets" , pathMatch : "full"},
    {path : "add" , "component" : AddPacketComponent}
  ]}
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
