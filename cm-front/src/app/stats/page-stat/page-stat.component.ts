import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';
import { DateUtils } from 'src/shared/utils/date-utils';
import { Subject, takeUntil } from 'rxjs';
import { ChartDTO } from 'src/shared/models/ChartDTO';

@Component({
  selector: 'app-page-stat',
  templateUrl: './page-stat.component.html',
  styleUrls: ['./page-stat.component.scss'],
  providers: [DatePipe],
})

export class PageStatComponent implements OnInit ,OnChanges ,OnDestroy {

  @Input() countProgressEnabler : boolean = false;
  @Input() beginDateString: string;
  @Input() endDateString: string | null;
  @Input() calculateAverage:(item: any) => number;
  @Input() calculateSomme:(numbers: number[]) => number;
  @Input() getRandomColor:(x: string)=> string;
  @Input() formatNumber:(item: any)=> string;


  chartData: any[];
  pagesCountData: any;
  pagesCountOptions: any;
  pagesCountChartOptions: string[] = ['Chart', 'Table'];
  pagesCountChartBoolean: boolean = true;
  pagesTableData: any;
  PagesData: any;
  PagesOptions: any;

  $unsubscribe: Subject<void> = new Subject();
  dates: any[];
  pagesCounts: number[][];

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
      this.getStatAllPagesCountChart();
      }
  }
  intitiateLists() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue(
      '--text-color-secondary'
    );
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');
    this.PagesOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.6,
      plugins: {
        legend: {
          labels: {
            usePointStyle: true,
            color: textColor,
          },
        },
      },
    };
  }

  getStatAllPagesCountChart() {
    if(this.endDateString)
    this.statsService
      .statAllPagesCount(this.beginDateString, this.endDateString, this.countProgressEnabler)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (data: any) => {
          this.pagesTableData = data.pagesStat;
          this.createPagesOChart(data.pagesStat);
          this.createPagesChart(data.chart);
          this.totals = this.calculateTotals();
        },
        error: (error: any) => {
          console.log('ErrorProductsCount:', error);
        },
        complete: () => {
          console.log('Observable completed-- getStatAllModelsChart --');
        },
      });
  }

  createPagesChart(chart: ChartDTO){
    let dates = chart.uniqueDates;
    this.pagesCounts = chart.itemsCount;
    let uniquePages = chart.uniqueItems;
    let i = 0;
    this.chartData = [];
    uniquePages.forEach((item: any) => {
      let x = this.calculateAverage(this.pagesCounts[i]);
      this.chartData.push({
        label: item.name + '/av:' + x,
        data: this.pagesCounts[i],
        fill: false,
        borderColor: this.getRandomColor(item.name),
        tension: 0.4,
        hidden: x < 4,
      });
      i++;
    });
    this.pagesCountData = {
      labels: dates,
      datasets: this.chartData,
    };
  }
  createPagesOChart(data: any) {
    const pagesData: number[] = Object.values(data)
    .filter((obj: any) => obj.paid && obj.name !== "Total")
    .map((obj: any) => obj.paid); // Assuming you want to extract `paid` values

    const pagesLabel: string[] = Object.values(data)
      .filter((obj: any) => obj.name && obj.name !== "Total")
      .map((obj: any) => obj.name);

    this.PagesData = {
      labels: pagesLabel,
      datasets: [
        {
          data: pagesData,
          backgroundColor: ['blue', 'green', 'red', 'grey', 'yellow', 'pink'],
          hoverBackgroundColor: [
            'blue',
            'green',
            'red',
            'grey',
            'yellow',
            'pink',
          ],
        },
      ],
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
    let i = 1;
    this.pagesTableData.forEach((item : any) => {
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

  getItemIndex(item: any,pagesTableData: any): number[] {
    console.log("item", item);

    console.log("1",pagesTableData);


    let x = pagesTableData.indexOf(item);
    console.log(x);
    let y = pagesTableData[x];
    console.log(y);
    return y;
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
