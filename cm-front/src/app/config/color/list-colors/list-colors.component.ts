import { Component, OnDestroy, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Color } from 'src/shared/models/Color';
import { ColorService } from '../../../../shared/services/color.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-list-colors',
  templateUrl: './list-colors.component.html',
  styleUrls: ['./list-colors.component.css']
})
export class ListColorsComponent implements OnInit,OnDestroy {

  colors: Color[] = [];
  $unsubscribe: Subject<void> = new Subject();
  constructor(
    private colorService: ColorService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService) {

     }

  ngOnInit(): void {
    this.colorService.getColorsSubscriber().pipe(takeUntil(this.$unsubscribe))
    .subscribe((colorList: any) => {
      this.colors = colorList;
    });
  }

  editColor(color: any){
    this.colorService.editColor({...color});
    this.colorService.editMode = true;
  }

  deleteColor(color: any)  {
    this.colorService.checkColorUsage(color.id)
    .subscribe((colorUsage: any) => {
      console.log(colorUsage);
      if(colorUsage > 0) {
        console.log(colorUsage);
        
        this.messageService.add({
          severity: 'warn',
          summary: 'Attention !',
          detail:`La couleur ne peut pas être supprimée car elle est utilisée au niveau des commandes ${colorUsage} fois` ,
          life: 5000,
        });
      } else {
        this.confirmationService.confirm({
          message: 'Êtes-vous sûr de vouloir supprimer la couleur séléctionnée ?',
          header: 'Confirmation',
          icon: 'pi pi-exclamation-triangle',
          accept: () => {
            this.colorService.deleteColorById(color.id).pipe(takeUntil(this.$unsubscribe))
              .subscribe(() => {
                this.colors = this.colors.filter(val => val.id !== color.id);
                this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La couleur a été supprimée avec succés", life: 1000 });
              })
          }
        });
      }
    });
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
