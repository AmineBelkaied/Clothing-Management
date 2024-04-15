import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewTenantRoutingModule } from './new-tenant-routing.module';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { NewTenantComponent } from './new-tenant.component';
import { FileUploadModule } from 'primeng/fileupload';
import { ToastModule } from 'primeng/toast';

@NgModule({
    imports: [
        CommonModule,
        NewTenantRoutingModule,
        ButtonModule,
        InputTextModule,
        FormsModule,
        FileUploadModule,
        ToastModule
    ],
    declarations: [NewTenantComponent],
    schemas: [ CUSTOM_ELEMENTS_SCHEMA,  NO_ERRORS_SCHEMA],
})
export class NewTenantModule { }
