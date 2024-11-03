import { Component, OnInit , OnDestroy} from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { Size } from 'src/shared/models/Size';
import { SizeService } from 'src/shared/services/size.service';

@Component({
  selector: 'app-list-sizes',
  templateUrl: './list-sizes.component.html',
  styleUrls: ['./list-sizes.component.css']
})
export class ListSizesComponent implements OnInit, OnDestroy {

  sizes: Size[] = [];
  $unsubscribe: Subject<void> = new Subject();
  constructor(private sizeService: SizeService, private messageService: MessageService, private confirmationService: ConfirmationService) {

  }

  ngOnInit(): void {
    this.sizeService.getSizesSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (sizes: Size[]) => {
        this.sizes = sizes;
      }
    );
  }

  editSize(size: any) {
    this.sizeService.editSize({...size});
    this.sizeService.editMode = true;
  }

  deleteSize(size: any) {
    this.sizeService.checkSizeUsage(size.id)
    .subscribe((sizeUsage: any) => {
      if(sizeUsage > 0) {
        this.messageService.add({
          severity: 'warn',
          summary: 'Attention !',
          detail:`La taille ne peut pas être supprimée car elle est utilisée au niveau des commandes ${sizeUsage} fois` ,
          life: 5000,
        });
      } else {
        this.confirmationService.confirm({
          message: 'Êtes-vous sûr de vouloir supprimer la taille séléctionnée ?',
          header: 'Confirmation',
          icon: 'pi pi-exclamation-triangle',
          accept: () => {
            this.sizeService.deleteSizeById(size.id).pipe(takeUntil(this.$unsubscribe))
              .subscribe(() => {
                this.sizes = this.sizes.filter(val => val.id !== size.id);
                this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été supprimée avec succés", life: 1000 });
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
