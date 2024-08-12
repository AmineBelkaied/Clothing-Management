import { Component, OnDestroy, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { CityService } from 'src/shared/services/city.service';
import { City } from 'src/shared/models/City';
import { Governorate } from 'src/shared/models/Governorate';
import { Subject, takeUntil } from 'rxjs';


@Component({
  selector: 'app-list-cities',
  templateUrl: './list-cities.component.html',
  styleUrls: ['./list-cities.component.scss']
})
export class ListCitiesComponent implements OnInit,OnDestroy {

  cities: City[] = [];
  cityDialog!: boolean;

  governorate!: Governorate;
  city!: City;
  oldCity!: City;
  editMode = false;
  submitted = false;
  $unsubscribe: Subject<void> = new Subject();

  selectedCities: City[] = [];
  governorates: Governorate[] = [];

  constructor(
    private citySerivce: CityService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService) {
    /*this.governorate = {
      "id" :"",
      "name": ""
    };
    this.city = {
      "id" :"",
      "name": "",
      "postalCode": "",
      "governorate" : this.governorate
    }*/
   }

  ngOnInit(): void {
    this.citySerivce.findAllCitys().pipe(takeUntil(this.$unsubscribe))
      .subscribe((cityList: any) => {
        this.cities = cityList;
        //console.log(this.cities)
      })

  }


  deleteCity(city: any) {
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer la taille séléctionnée ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.citySerivce.deleteCityById(city.id).pipe(takeUntil(this.$unsubscribe))
          .subscribe(() => {
            this.cities = this.cities.filter(val => val.id !== city.id);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été supprimée avec succés", life: 1000 });
          })
      }
    });
  }

  openNew() {
    this.governorate = {
      "id" :"",
      "name": ""
    };
    this.city = {
      "id" : "",
      "name": "",
      "postalCode": "",
      "governorate": this.governorate
    }
    this.submitted = false;
    this.cityDialog = true;
    this.editMode = false;
  }

   deleteSelectedCities() {
    let selectedCitiesId = this.selectedCities.map((selectedCity: City) => selectedCity.id);
    console.log(selectedCitiesId);
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer les villes séléctionnées ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.citySerivce.deleteSelectedCities(selectedCitiesId).pipe(takeUntil(this.$unsubscribe))
          .subscribe(() => {
            this.cities = this.cities.filter((city: City) => selectedCitiesId.indexOf(city.id) == -1);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Les villes séléctionnées ont été supprimé avec succés', life: 1000 });
          })
      }
    });
  }

  editCity(city: City) {
    this.city = { ...city };
    this.cityDialog = true;
    this.editMode = true;
  }

  hideDialog() {
    this.cityDialog = false;
    this.submitted = false;
  }

  saveCity() {
    this.submitted = true;
    console.log(this.city)
    //if (this.city.name.trim()) {
      if (this.city.id) {
        this.citySerivce.updateCity(this.city)
          .subscribe({
            next: response => {
              console.log(response);
              this.cities[this.findIndexById(this.city.id)] = this.city;
              this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'city Updated', life: 3000 });
            }
          });
      }
      else {
        this.citySerivce.addCity(this.city)
          .subscribe({
            next: (response: any) => {
              console.log(response);
              this.cities.push(response);
              this.cities = [...this.cities];
              this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'city Created', life: 3000 });
            },
            error: error => {

              console.error('There was an error!', error);
            }
          })
      }

      console.log(this.cities);

      this.cityDialog = false;
      this.governorate = Object.assign({}, this.governorate);
      this.city = Object.assign({}, this.city);
   // }
  }

  findIndexById(id: string): number {
    let index = -1;
    for (let i = 0; i < this.cities.length; i++) {
      if (this.cities[i].id === id) {
        index = i;
        break;
      }
    }

    return index;
  }


  search(dt: any, event: any) {
    this.cities = this.cities.slice();
    console.log(event.target.value);
    dt.filterGlobal(event.target.value, 'contains')
  }

  onEditInit($event: any) {
    this.oldCity = Object.assign({}, $event.data);
  }

  onEditComplete($event: any) {
    if(JSON.stringify(this.oldCity) != JSON.stringify($event.data)) {
      this.citySerivce.updateCity($event.data).pipe(takeUntil(this.$unsubscribe))
      .subscribe(() => {
        console.log("city successfully updated !");
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'La ville a été mise à jour avec succés', life: 1000 });
      })
    }
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
