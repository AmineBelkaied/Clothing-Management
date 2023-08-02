import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SelectItemGroup } from 'primeng/api';
import { City } from 'src/shared/models/City';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class CityService {

  private baseUrl: string = baseUrl+"/city";
  public citys: City[] = [];
  private groupedCities: SelectItemGroup[] = [];
  public editMode = false;
  constructor(private http: HttpClient) {
  }

  findAllCitys() {
    return this.http.get(this.baseUrl + "/findAll");
  }

  findAllGroupedCities() {
    return this.http.get(this.baseUrl + "/findGroupedCities");
  }

  findCityById(id: number) {
    return this.http.get(this.baseUrl + "/findById/" + id);
  }

  addCity(city: City) {
    return this.http.post(this.baseUrl + "/add" , city , {observe: 'body'})
  }

  updateCity(city: City) {
    return this.http.put(this.baseUrl + "/update" , city , {headers : { 'content-type': 'application/json'}})
  }

  deleteCityById(idCity: any) {
    return this.http.delete(this.baseUrl + "/deleteById/" + idCity)
  }

  deleteSelectedCities(citiesId: any[]) {
    return this.http.delete(this.baseUrl + "/deleteSelectedCities/" + citiesId);
  }

  adaptListToDropDown(groupedCities: any[]) {
    for (var i = 0; i < groupedCities.length; i++) {
      let groupedCity: SelectItemGroup = {
        label: groupedCities[i]?.governorate.name,
        value: groupedCities[i]?.governorate.id,
        items: []
      }
      for (var j = 0; j < groupedCities[i].cities.length; j++) {
          let city: any = {
            label: groupedCities[i].cities[j].name,
            value: groupedCities[i].cities[j],
            governorate : groupedCities[i].governorate.name
          }
          groupedCity.items.push(city);
      }
      this.groupedCities.push(groupedCity);
    };
    return this.groupedCities;
  }

}
