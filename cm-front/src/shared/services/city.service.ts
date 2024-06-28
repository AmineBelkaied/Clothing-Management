import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SelectItemGroup } from 'primeng/api';
import { City } from 'src/shared/models/City';
import { environment } from '../../environments/environment';
import { CITY_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class CityService {

  private baseUrl: string = environment.baseUrl + `${CITY_ENDPOINTS.BASE}`;;
  public citys: City[] = [];
  private groupedCities: SelectItemGroup[] = [];
  public editMode = false;

  constructor(private http: HttpClient) {
  }

  findAllCitys() {
    return this.http.get(`${this.baseUrl}`);
  }

  findAllGroupedCities() {
    return this.http.get(`${this.baseUrl}${CITY_ENDPOINTS.GROUPED_BY_GOVERNORATE}`);
  }

  findCityById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  addCity(city: City) {
    return this.http.post(`${this.baseUrl}`, city , {observe: 'body'});
  }

  updateCity(city: City) {
    return this.http.put(`${this.baseUrl}`, city , {headers : { 'content-type': 'application/json'}});
  }

  deleteCityById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteSelectedCities(citiesId: any[]) {
    return this.http.delete(`${this.baseUrl}/${CITY_ENDPOINTS.BATCH_DELETE}/${citiesId}`);
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
