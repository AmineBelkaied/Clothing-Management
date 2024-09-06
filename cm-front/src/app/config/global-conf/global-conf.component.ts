import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { GlobalConf } from 'src/shared/models/GlobalConf';
import { GlobalConfService } from 'src/shared/services/global-conf.service';
import { DeliveryCompanyService } from 'src/shared/services/delivery-company.service';

@Component({
  selector: 'app-global-conf',
  templateUrl: './global-conf.component.html',
  styleUrls: ['./global-conf.component.scss']
})
export class GlobalConfComponent implements OnInit, OnDestroy {


  globalConf: GlobalConf;// = {applicationName: ""};
  editMode!: boolean;
  deliveryCompanies: DeliveryCompany[];
  $unsubscribe: Subject<void> = new Subject();

  constructor(
    public globalConfService: GlobalConfService,
    private deliveryCompanyService: DeliveryCompanyService) {

    }

  ngOnInit(): void {
    this.deliveryCompanyService.getDeliveryCompaniesSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (deliveryCompany: DeliveryCompany[]) => {
        this.deliveryCompanies = deliveryCompany;
      }
    );

    this.globalConfService.getGlobalConfSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (globalConf: GlobalConf) => {
        this.globalConf = globalConf;
      }
    );
  }

  addGlobalConf() {
      this.globalConfService.setGlobalConfSubscriber(this.globalConf);
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }

}
