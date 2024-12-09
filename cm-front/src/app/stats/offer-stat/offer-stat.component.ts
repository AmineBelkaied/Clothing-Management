import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';
import { Subject, takeUntil } from 'rxjs';
import { ProductCountDTO } from 'src/shared/models/ProductCountDTO';
import { ChartDTO } from 'src/shared/models/ChartDTO';

@Component({
  selector: 'app-offer-stat',
  templateUrl: './offer-stat.component.html',
  styleUrls: ['./offer-stat.component.scss'],
  providers: [DatePipe],
})

export class OfferStatComponent implements OnInit,OnChanges,OnDestroy {
  @Input() countProgressEnabler : boolean = false;
  @Input() beginDateString: string;
  @Input() endDateString: string | null;
  @Input() calculateAverage:(item: any) => number;
  @Input() getMinMax:(item: any,tableData: any)=> string;
  @Input() calculateSomme:(numbers: number[]) => number;
  @Input() getRandomColor:(x: string)=> string;
  @Input() formatNumber:(item: any)=> string;
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

  $unsubscribe: Subject<void> = new Subject();


  offersCounts: any[] = [];
  totals = {
    paid: 0,
    progress: 0,
    retour: 0,
    purchasePrice: 0,
    sellingPrice: 0,
    profits: 0,
  };

  constructor(
    private statsService: StatsService,
    public datePipe: DatePipe
  ) {}

  ngOnInit() {
    this.intitiateLists();

  }

  ngOnChanges(simpleChanges: SimpleChanges): void {
    if((simpleChanges['endDateString'] || simpleChanges['countProgressEnabler']) && this.endDateString){
          this.getStatAllOffersChart();
      }
  }
  getStatAllOffersChart() {
    if(this.endDateString){
      this.offerTableData = [];
      this.statsService
        .statAllOffers(this.beginDateString, this.endDateString,this.countProgressEnabler)
        .pipe(takeUntil(this.$unsubscribe))
        .subscribe({
          next: (data: any) => {
            this.offerTableData = data.offersStat;
            this.totals = this.calculateTotals();
            this.createOffersChart(data.chart);
          },
          error: (error: any) => {
            console.log('ErrorOffersCount:', error);
          },
          complete: () => {
            console.log('Observable completed-- getStatAllOffersChart --');
          },
        });
    }
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

  createOffersChart(chart : ChartDTO) {
    let dates = chart.uniqueDates;
    this.offersCounts = chart.itemsCount;
    let uniqueOffers = chart.uniqueItems;

    this.offersDataSetArray = [];
    let i = 0;
    uniqueOffers.forEach((item: any) => {
      let x = this.calculateAverage(this.offersCounts[i]);
      this.offersDataSetArray.push({
        label: item.name + '/av:' + x,
        data: this.offersCounts[i],
        fill: false,
        borderColor: this.getRandomColor(item.name),
        tension: 0.4,
        hidden: x < 3
      });
      i++;
    });

    this.offersData = {
      labels: dates,
      datasets: this.offersDataSetArray,
    };
  }

  calculateTotals() {
    const totals = {
      paid: 0,
      progress: 0,
      retour: 0,
      purchasePrice: 0,
      sellingPrice: 0,
      profits: 0,
    };
    let i = 1
    let per=0.00;
    this.offerTableData.forEach((item : any) => {
      totals.paid += item.countPaid || 0;
      totals.progress += item.countProgress || 0;
      totals.retour += item.countReturn || 0;
      totals.purchasePrice += 0;// (item.model.purchasePrice * item.countPaid) || 0;
      totals.sellingPrice += 0;//((item.model.purchasePrice * item.countPaid) + item.profits) || 0;
      totals.profits += item.profits || 0;
      i++;
    });
    return totals;
  }

  getItemIndex(item: any): number[] {
    let x = this.offerTableData.indexOf(item);
    let y = this.offersCounts[x];

    return y;
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
