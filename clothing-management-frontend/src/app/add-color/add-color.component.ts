import { Component, Input, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Color } from '../models/color';
import { ColorService } from '../services/color.service';

@Component({
  selector: 'app-add-color',
  templateUrl: './add-color.component.html',
  styleUrls: ['./add-color.component.css']
})
export class AddColorComponent implements OnInit {

  color!: Color;
  editMode!: boolean;
  constructor(public colorService: ColorService) { }

  ngOnInit(): void {
    this.colorService.color.subscribe(color => {
      this.color = color
      console.log(this.color)
    });
  }

  addColor(form: NgForm) {
    if(this.colorService.editMode){
      this.color.name = form.value.name;
      this.color.reference = form.value.reference;
      this.colorService.updateColor(this.color)
      .subscribe((updateColor: any) => {
        console.log(updateColor)
        this.colorService.spliceColor(updateColor);
        form.reset();
      });
    } else {
      this.colorService.addColor(form.value)
      .subscribe((addedColor: any) => {
        this.colorService.colors.push(addedColor);
        form.reset();
      });
    }
  }
}
