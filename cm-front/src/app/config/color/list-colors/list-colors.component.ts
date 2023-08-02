import { Component, Input, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Color } from 'src/shared/models/Color';
import { ColorService } from '../../../../shared/services/color.service';

@Component({
  selector: 'app-list-colors',
  templateUrl: './list-colors.component.html',
  styleUrls: ['./list-colors.component.css']
})
export class ListColorsComponent implements OnInit {

  colors: Color[] = [];

  constructor(private colorService: ColorService,private messageService: MessageService, private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    this.colorService.colorsSubscriber
    .subscribe((colorList: any) => {
      this.colors = colorList;
    });
  }

  editColor(color: any){
    this.colorService.editColor({...color});
    this.colorService.editMode = true;
  }

  deleteColor(color: any)  {
    console.log("okkk");

    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer la couleur séléctionnée ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.colorService.deleteColorById(color.id)
          .subscribe(result => {
            this.colors = this.colors.filter(val => val.id !== color.id);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La couleur a été supprimée avec succés", life: 1000 });
          })
      }
    });
  }
}
