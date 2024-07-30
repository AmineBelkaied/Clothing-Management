import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Observable, Subject, Subscription, catchError, takeUntil } from 'rxjs';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { GlobalConf } from 'src/shared/models/GlobalConf';
import { GlobalConfService } from 'src/shared/services/global-conf.service';
import { SteLivraisonService } from 'src/shared/services/ste-livraison.service';

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
    private steLivraisonService: SteLivraisonService,
    private messageService: MessageService) {

    }

  ngOnInit(): void {
    this.steLivraisonService.getDCSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (dc: DeliveryCompany[]) => {
        this.deliveryCompanies = dc;
      }
    );

    this.globalConfService.getGlobalConfSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (globalConf: GlobalConf) => {
        this.globalConf = globalConf;
      }
    );
  }

  addGlobalConf(form: NgForm) {
      this.globalConfService.setGlobalConfSubscriber(this.globalConf);
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
this.$unsubscribe.complete();
  }

}
