import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Observable } from 'rxjs';
import { TenantService } from 'src/shared/services/tenant.service';


@Component({
  selector: 'app-new-tenant',
  templateUrl: './new-tenant.component.html',
  styleUrls: ['./new-tenant.component.scss']
})
export class NewTenantComponent {

  tenantName!: string;
  selectedFile: any;
  progress = 0;
  message = '';

  fileInfos!: Observable<any>;

  constructor(private messageService: MessageService, private tenantService: TenantService) {}

  onBasicUpload() {
      this.messageService.add({ severity: 'info', summary: 'Success', detail: 'File Uploaded with Basic Mode' });
  }

  selectFile($event: any) {
    this.selectedFile = $event.target.files[0];
    console.log($event.target.files[0]);
    this.tenantService.upload(this.selectedFile).subscribe(
      (event: any) => {
        if (event.type === HttpEventType.UploadProgress) {
          this.progress = Math.round(100 * event.loaded / event.total);
        } else if (event instanceof HttpResponse) {
          this.message = event.body.message;
          //this.fileInfos = this.tenantService.getFiles();
        }
        console.log("uploaded !!!");
        
        this.messageService.add({ severity: 'info', summary: 'Success', detail: "Le logo de l'entreprise est ajouté avec succés" });
      },
      err => {
        this.progress = 0;
        this.message = 'Could not upload the file!';
        this.messageService.add({ severity: 'error  ', summary: 'Success', detail: "Un problème est survenue lors de l'ajout du logo de l'entreprise" });
      });
  }

  add() {
    this.progress = 0;
    this.tenantService.addTenant(this.tenantName)
    .subscribe(() => { 
              
      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La nouvelle entreprise est crée avec succés' });
    })
  
  }

}
