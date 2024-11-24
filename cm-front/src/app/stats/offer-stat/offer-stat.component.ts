import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { DaySales} from 'src/shared/models/stat';
import { PacketService } from 'src/shared/services/packet.service';
import { Packet } from 'src/shared/models/Packet';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';
import { DateUtils } from 'src/shared/utils/date-utils';
import { Subject, takeUntil } from 'rxjs';
import { ModelService } from 'src/shared/services/model.service';
import { ActivatedRoute } from '@angular/router';
import { ProductCountDTO } from 'src/shared/models/ProductCountDTO';
import { FormControl } from '@angular/forms';
import { DeliveryCompanyService } from 'src/shared/services/delivery-company.service';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';

@Component({
  selector: 'app-offer-stat',
  templateUrl: './offer-stat.component.html',
  styleUrls: ['./offer-stat.component.scss'],
  providers: [DatePipe],
})

export class OfferStatComponent implements OnInit,OnChanges {

  @Input() beginDateString: string;
  @Input() endDateString: string | null;
  offersCount: ProductCountDTO[] = [];
  offersDataSetArray: any[];
  offersData: any;
  offersOptions: any;
  offerChartOptions: string[] = ['Chart', 'Table'];
  offerChartBoolean: boolean = true;
  offerTableData: any;
  totalOffer = {
    name: 'Offer Name', // Example offer or product name
    min: 0,                     // Minimum value, e.g., min price or quantity
    max: 0,                     // Maximum value, e.g., max price or quantity
    avg: 0,                     // Average value, e.g., average sales per day
    paid: 0,                   // Total items sold or paid amount
    progress: 0,                // Current progress metric
    retour: 0,                  // Returns or refund count/rate
    purchasePrice: 0,           // Total purchase price value
    sellingPrice: 0,            // Total selling price value
    profits: 0                  // Projected or actual profit
  };

  dates: any[];
  $unsubscribe: Subject<void> = new Subject();

  constructor(
    private statsService: StatsService,
    public datePipe: DatePipe,
    private deliveryCompanyService: DeliveryCompanyService,
  ) {}

  ngOnInit() {
    this.intitiateLists();

  }
  ngOnChanges(simpleChanges: SimpleChanges): void {
    if(simpleChanges['endDateString'] && this.endDateString){
          this.getStatAllOffersChart();
      }
  }
  getStatAllOffersChart() {
    if(this.endDateString)
    this.statsService
      .statAllOffers(this.beginDateString, this.endDateString)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: any) => {
          this.createOffersChart(response);
        },
        error: (error: any) => {
          console.log('ErrorOffersCount:', error);
        },
        complete: () => {
          console.log('Observable completed-- getStatAllOffersChart --');
        },
      });
  }
  intitiateLists() {

    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue(
      '--text-color-secondary'
    );
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.offersOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.6,
      plugins: {
        legend: {
          labels: {
            color: textColor,
          },
        },
      },
      scales: {
        x: {
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false,
          },
        },
        y: {
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false,
          },
        },
      },
    };
    this.offersData = {
      labels: [],
      datasets: [],
    };


  }

  createOffersChart(data: any) {
    console.log("createOffersChart",data);

    this.offerTableData = [];
    let offersCounts: any[];
    this.offerTableData = data.offersRecapCount;
    offersCounts = data.countOffersLists;

    this.offersDataSetArray = [];
    this.dates = data.dates;

    let k = 0;
    this.offerTableData.forEach((item: any) => {
      this.offersDataSetArray.push({
        label: item.name + '/av:' + this.offerTableData[k].avg,
        data: offersCounts[k],
        fill: false,
        borderColor: this.getRandomColor(item.name),
        tension: 0.4,
        hidden: this.offerTableData[k].avg < 3
      });
      k++;
    });
    this.totalOffer = this.offerTableData[this.offerTableData.length - 1];
    this.offerTableData.pop();

    this.offersData = {
      labels: this.dates,
      datasets: this.offersDataSetArray,
    };
  }
  calculateAverage(numbers: number[]): number {
    if (numbers.length === 0) {
      return 0; // Handle division by zero
    }
    const sum = numbers.reduce((acc, current) => acc + current, 0);
    const average = sum / numbers.length;
    return Number(average.toFixed(1));
  }
  getRandomColor(x: string) {
    if (x == 'Noir' || x == 'noir') return 'black';
    else if (x == 'Vert'|| x == 'vert') return 'green';
    else if (x == 'Beige'|| x == 'beige') return '#D1AF76';
    else if (x == 'Bleu'|| x == 'bleu') return '#0080FF';
    else if (x == 'Gris'|| x == 'gris') return 'grey';
    else if (x == 'Blanc'|| x == 'blanc') return 'pink';

    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
      color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
  }

  formatNumber(item: any) {
    let value = (item.paid*100) / (item.retour+item.paid)
      if (!isNaN(value)) {
        return value.toFixed(2);
      }
      return '0.00';
  }
}
