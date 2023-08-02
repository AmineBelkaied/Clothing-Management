import { Component, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Size } from 'src/shared/models/Size';
import { SizeService } from 'src/shared/services/size.service';

@Component({
  selector: 'app-list-sizes',
  templateUrl: './list-sizes.component.html',
  styleUrls: ['./list-sizes.component.css']
})
export class ListSizesComponent implements OnInit {

  sizes: Size[] = [];
  constructor(private sizeSerivce: SizeService, private messageService: MessageService, private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    this.sizeSerivce.sizesSubscriber
      .subscribe((sizeList: any) => {
        this.sizes = sizeList;
      })
  }

  editSize(size: any) {
    this.sizeSerivce.editSize({...size});
    this.sizeSerivce.editMode = true;
  }

  deleteSize(size: any) {
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer la taille séléctionnée ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.sizeSerivce.deleteSizeById(size.id)
          .subscribe(result => {
            this.sizes = this.sizes.filter(val => val.id !== size.id);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été supprimée avec succés", life: 1000 });
          })
      }
    });
  }
}
