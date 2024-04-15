import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { GetTenantsComponent } from './get-tenants.component';

@NgModule({
    imports: [RouterModule.forChild([
        { path: '', component: GetTenantsComponent }
    ])],
    exports: [RouterModule]
})
export class GetTenantsRoutingModule { }
