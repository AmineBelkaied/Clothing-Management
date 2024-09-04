import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Subject, catchError, takeUntil } from 'rxjs';
import { Color } from 'src/shared/models/Color';
import { ColorService } from 'src/shared/services/color.service';


@Component({
  selector: 'app-add-color',
  templateUrl: './add-color.component.html',
  styleUrls: ['./add-color.component.css']
})
export class AddColorComponent implements OnInit,OnDestroy {

  color!: Color;
  editMode!: boolean;
  $unsubscribe: Subject<void> = new Subject();
  constructor(public colorService: ColorService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.colorService.color.pipe(takeUntil(this.$unsubscribe)).subscribe(color => {
      this.color = color
    });
  }

  addColor(form: NgForm) {
    if(this.colorService.editMode){
      this.color.name = form.value.name;
      this.colorService.updateColor(this.color)
      .pipe(
        catchError((err: any): any => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: "Erreur lors de l'ajout' " + err.error.message,
          });
        })
      )
      .subscribe((updatedColor: any) => {
        console.log(updatedColor)
        this.colorService.spliceColor(updatedColor);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La couleur a été modifiée avec succés", life: 1000 });
        form.reset();
        this.colorService.editMode = false;
      });
    } else {
      this.colorService.addColor(form.value)
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
        this.colorService.colors.push(addedColor);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La couleur a été crée avec succés", life: 1000 });
        form.reset();
      });
    }
  }

  reset(modelForm: NgForm){
    modelForm.reset();
    this.colorService.editMode = false;
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
