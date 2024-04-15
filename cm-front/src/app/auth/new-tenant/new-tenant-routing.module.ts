import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NewTenantComponent } from './new-tenant.component';
import { AuthGuard } from 'src/shared/services/auth-gard.service';
import { Roles } from 'src/shared/enums/roles';

@NgModule({
    imports: [RouterModule.forChild([
        { path: '', component: NewTenantComponent }
    ])],
    exports: [RouterModule]
})
export class NewTenantRoutingModule { }
