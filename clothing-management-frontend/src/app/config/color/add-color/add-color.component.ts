import { Component, Input, OnInit } from '@angular/core';
import { Form, NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Color } from 'src/shared/models/color';
import { ColorService } from 'src/shared/services/color.service';


@Component({
  selector: 'app-add-color',
  templateUrl: './add-color.component.html',
  styleUrls: ['./add-color.component.css']
})
export class AddColorComponent implements OnInit {

  color!: Color;
  editMode!: boolean;
  constructor(public colorService: ColorService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.colorService.color.subscribe(color => {
      this.color = color
    });
  }

  addColor(form: NgForm) {
    if(this.colorService.editMode){
      this.color.name = form.value.name;
      this.color.reference = form.value.reference;
      this.colorService.updateColor(this.color)
      .subscribe((updatedColor: any) => {
        console.log(updatedColor) 
        this.colorService.spliceColor(updatedColor);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La couleur a été modifiée avec succés", life: 1000 });
        form.reset();
        this.colorService.editMode = false;
      });
    } else {
      this.colorService.addColor(form.value)
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
}
