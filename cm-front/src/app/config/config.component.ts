import { Component, Input, OnInit } from '@angular/core';
import { Color } from 'src/shared/models/Color';
import { Governorate } from 'src/shared/models/Governorate';
import { GovernorateService } from '../../shared/services/governorate.service';

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
