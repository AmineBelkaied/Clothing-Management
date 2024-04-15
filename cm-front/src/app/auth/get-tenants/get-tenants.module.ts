import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { FileUploadModule } from 'primeng/fileupload';
import { ToastModule } from 'primeng/toast';
import { GetTenantsComponent } from './get-tenants.component';
import { GetTenantsRoutingModule } from './get-tenants-routing.module';
import { DropdownModule } from 'primeng/dropdown';

@NgModule({
    imports: [
        CommonModule,
        GetTenantsRoutingModule,
        ButtonModule,
        InputTextModule,
        FormsModule,
        FileUploadModule,
        ToastModule,
        DropdownModule
    ],
    declarations: [GetTenantsComponent],
    schemas: [ CUSTOM_ELEMENTS_SCHEMA,  NO_ERRORS_SCHEMA],
})
export class GetTenantsModule { }
