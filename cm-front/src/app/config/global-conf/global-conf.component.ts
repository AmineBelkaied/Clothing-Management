import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Observable, Subject, catchError, takeUntil } from 'rxjs';
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


  globalConf: GlobalConf = {
    applicationName: ""
  };
  editMode!: boolean;
  deliveryCompanies: DeliveryCompany[];
  $unsubscribe: Subject<void> = new Subject();

  constructor(public globalConfService: GlobalConfService, private deliveryCompanyService: SteLivraisonService, private messageService: MessageService) {}

  ngOnInit(): void {

    this.globalConfService.globalConf$
    .pipe(takeUntil(this.$unsubscribe))
    .subscribe((globalConf: GlobalConf) => {

      this.globalConf = globalConf
      console.log(this.globalConf);})

    this.deliveryCompanyService.findAllStes()
    .subscribe((deliveryCompanies: any) => this.deliveryCompanies = deliveryCompanies)
  }

  addGlobalConf(form: NgForm) {
      this.globalConfService.updateGlobalConf(this.globalConf)
      .pipe(
        catchError((err: any, caught: Observable<any>): any => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: "Erreur lors de le modification' " + err.error.message,
          });
        })
      )
      .subscribe((result: any) => {
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été modifiée avec succés", life: 1000 });
      });
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();

  }

}
