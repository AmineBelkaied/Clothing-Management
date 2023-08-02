import { Component, Input} from '@angular/core';
import { Color } from 'src/shared/models/Color';
import { Model } from 'src/shared/models/Model';
import { Size } from 'src/shared/models/Size';

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
export class AddModelComponent{


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
  @Input() editMode!: boolean;
  selectedColors: any[] = [];

  selectedSize: any;
  constructor() {

    console.log(this.model);
    
    console.log(this.editMode);

  }
}
