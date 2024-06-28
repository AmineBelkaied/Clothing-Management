import { Component, OnInit } from '@angular/core';
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

  constructor(
    private governorateService: GovernorateService,
  ) {
    this.governorateService.findAllGovernorates()
      .subscribe((governorates: any) => {
        this.governorates = governorates;
      })
  }

  ngOnInit(): void {
  }

  setSelection(items: any) {
    this.selectedCities = items;
  }
}
