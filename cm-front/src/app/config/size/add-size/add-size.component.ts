import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Subject, catchError, takeUntil } from 'rxjs';
import { Size } from 'src/shared/models/Size';
import { SizeService } from 'src/shared/services/size.service';

@Component({
  selector: 'app-add-size',
  templateUrl: './add-size.component.html',
  styleUrls: ['./add-size.component.css']
})
export class AddSizeComponent implements OnInit,OnDestroy {

  size: Size;
  editMode!: boolean;
  $unsubscribe: Subject<void> = new Subject();
  constructor(public sizeService: SizeService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.sizeService.size.pipe(takeUntil(this.$unsubscribe)).subscribe(size => {
      if(size!= null)
      this.size = size;
    });
  }

  addSize(form: NgForm) {
    if(this.sizeService.editMode){
      this.size.reference = form.value.reference;
      this.size.description = form.value.description;
      this.sizeService.updateSize(this.size)
      .pipe(
        catchError((err: any): any => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: "Erreur lors de l'ajout' " + err.error.message,
          });
        })
      )
      .subscribe((updateSize: any) => {
        console.log(updateSize)
        this.sizeService.spliceSize(updateSize);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été modifiée avec succés", life: 1000 });
        form.reset();
        this.sizeService.editMode = false;
      });
    } else {
      this.sizeService.addSize(form.value)
      .pipe(
        catchError((err: any): any => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: "Erreur lors de l'ajout' " + err.error.message,
          });
        })
      )
      .subscribe((addedColor: any) => {
        this.sizeService.sizes.push(addedColor);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été crée avec succés", life: 1000 });
        form.reset();
      });
    }
  }

  reset(sizeForm: NgForm){
    sizeForm.reset();
    this.sizeService.editMode = false;
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
this.$unsubscribe.complete();
  }
}
