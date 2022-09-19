import { Component, Input, OnInit } from '@angular/core';
import { SelectItemGroup } from 'primeng/api';
import { Governorate } from 'src/shared/models/Governorate';
import { Color } from '../models/color';
import { GovernorateService } from '../services/governorate.service';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.css']
})
export class ConfigComponent implements OnInit {
  selectedCities: string[] = [];
  governorates: Governorate[] = [];
  constructor(private governorateService: GovernorateService) { 
    this.governorateService.findAllGovernorates()
    .subscribe((governorates: any) => {
      this.governorates = governorates;
      console.log(this.governorates)
    })

  }
  @Input() color: Color = {
    'id': '',
    'name' : '',
    'reference': ''
  }
  ngOnInit(): void {

  }
  setSelection(items: any) {
    this.selectedCities = items;
  }

  change($event:any) {
    console.log($event);
    
  }


 }
