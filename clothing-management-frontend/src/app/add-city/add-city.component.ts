import { Component, Input, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { City } from 'src/shared/models/City';
import { Governorate } from 'src/shared/models/Governorate';
import { CityService } from '../services/city.service';
import { GovernorateService } from '../services/governorate.service';

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
  constructor(public cityService: CityService, private governorateService: GovernorateService, private messageService: MessageService) { }

  ngOnInit(): void {
    console.log(this.city)
  }

/*   addCity(form: NgForm) {
    if(this.cityService.editMode){
      this.city.name = form.value.name;
      this.city.postalCode = form.value.postalCode;
      this.city.governorate = form.value.gouvernorate;
      this.cityService.updateCity(this.city)
      .subscribe((updateCity: any) => {
        console.log(updateCity)
        this.cityService.spliceCity(updateCity);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été modifiée avec succés", life: 1000 });
        form.reset();
      });
    } else {
      this.cityService.addCity(form.value)
      .subscribe((addedColor: any) => {
        this.cityService.citys.push(addedColor);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été crée avec succés", life: 1000 });
        form.reset();
      });
    }
  } */

  addCity(form: NgForm) {
    
  }
}
