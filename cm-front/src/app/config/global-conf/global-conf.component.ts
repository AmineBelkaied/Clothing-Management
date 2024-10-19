import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { GlobalConf } from 'src/shared/models/GlobalConf';
import { GlobalConfService } from 'src/shared/services/global-conf.service';
import { DeliveryCompanyService } from 'src/shared/services/delivery-company.service';
import { FbPageService } from 'src/shared/services/fb-page.service';
import { FbPage } from 'src/shared/models/FbPage';

@Component({
  selector: 'app-global-conf',
  templateUrl: './global-conf.component.html',
  styleUrls: ['./global-conf.component.scss']
})
export class GlobalConfComponent implements OnInit, OnDestroy {

  multipage: any[] = [{ label: 'Off', value: true }, { label: 'Multipage', value: false }];
  defaultPage: any[] = [{ label: 'Off', value: false }, { label: 'DefaultPage', value: true }];
  globalConf: GlobalConf;// = {applicationName: ""};
  editMode!: boolean;
  deliveryCompanies: DeliveryCompany[];
  $unsubscribe: Subject<void> = new Subject();
  fbPages: FbPage[] = [];
  multipageDisabled:boolean = true;

  constructor(
    public globalConfService: GlobalConfService,
    private fbPageService: FbPageService,
    private deliveryCompanyService: DeliveryCompanyService,) {

    }

  ngOnInit(): void {
    this.deliveryCompanyService.getDeliveryCompaniesSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (deliveryCompany: DeliveryCompany[]) => {
        this.deliveryCompanies = deliveryCompany;
      }
    );
    this.fbPageService.getFbPagesSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (fbPages: FbPage[]) => {
        this.fbPages = fbPages;
        if(this.fbPages.length >1) {
          this.multipageDisabled = false;
        }
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
