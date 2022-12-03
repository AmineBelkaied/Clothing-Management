import { Component, Input, OnInit } from '@angular/core';
import { City } from 'src/shared/models/City';
import { Governorate } from 'src/shared/models/Governorate';


@Component({
  selector: 'app-add-city',
  templateUrl: './add-city.component.html',
  styleUrls: ['./add-city.component.scss']
})
export class AddCityComponent implements OnInit {

  @Input() city: City = {
      "id" : "",
      "name": "",
      "postalCode": ""
  }
  editMode!: boolean;
  @Input() governorates: Governorate[] = [];
  constructor() { }

  ngOnInit(): void {
  }
}
