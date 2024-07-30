import { Component, Input, OnInit } from '@angular/core';
import { Color } from 'src/shared/models/Color';
import { Governorate } from 'src/shared/models/Governorate';
import { GovernorateService } from '../../shared/services/governorate.service';
import { SizeService } from 'src/shared/services/size.service';
import { ColorService } from 'src/shared/services/color.service';
import { FbPageService } from 'src/shared/services/fb-page.service';
import { SteLivraisonService } from 'src/shared/services/ste-livraison.service';

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
    private steLivraisonService :SteLivraisonService
  ) {
    //this.steLivraisonService.loadDeliveryCompanies();
    this.governorateService.findAllGovernorates()
    .subscribe((governorates: any) => {
      this.governorates = governorates;
      console.log(this.governorates)
    })

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
