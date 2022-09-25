import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Model } from 'src/shared/models/Model';
import { Size } from 'src/shared/models/Size';
import { Color } from '../models/color';
import { ColorService } from '../services/color.service';
import { ModelService } from '../services/model.service';

@Component({
  selector: 'app-add-model',
  templateUrl: './add-model.component.html',
  styles: [`
  :host ::ng-deep .p-dialog .product-image {
      width: 150px;
      margin: 0 auto 2rem auto;
      display: block;
  }
`]
})
export class AddModelComponent implements OnChanges{


  @Input() model: Model = {
    "id" : "",
    "name" : "",
    "reference" : "",
    "description" : "",
    "colors" : [],
    "sizes": []
  }
  @Input() colors: Color[] = [];
  @Input() sizes: Size[] = [];
  selectedColors: any[] = [];

  selectedSize: any;
  constructor(private modelService: ModelService) {
      //this.sizes = ["S" , "M" , "L" , "1" , "2" , "3" , "4"];
  }

  ngOnChanges(changes: SimpleChanges): void {
    //Called before any other lifecycle hook. Use it to inject dependencies, but avoid any serious work here.
    //Add '${implements OnChanges}' to the class.
    console.log(changes['model'].currentValue.colors);
    console.log(this.colors)
    //this.model.colors = changes['model'].currentValue.colors;
  }

  addModel(form: NgForm) {
   /* form.value.colors = form.value.colors.map((id: any) => {return  {"id" : id}});
    console.log(form.value)
    this.modelService.addModel(form.value)
    .subscribe({next: response => {
          console.log("success !!");
          console.log(response);
      },
      error: error => {
        
          console.error('There was an error!', error);
      }
    })*/
  }
}
