import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';
import { DateUtils } from 'src/shared/utils/date-utils';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-page-stat',
  templateUrl: './page-stat.component.html',
  styleUrls: ['./page-stat.component.scss'],
  providers: [DatePipe],
})

export class PageStatComponent implements OnInit ,OnChanges{

  @Input() beginDateString: string;
  @Input() endDateString: string | null;


  pagesCountDataSetArray: any[];
  pagesCountData: any;
  pagesCountOptions: any;
  pagesCountChartOptions: string[] = ['Chart', 'Table'];
  pagesCountChartBoolean: boolean = true;
  pagesCountTableData: any;
  PagesData: any;
  PagesOptions: any;

  $unsubscribe: Subject<void> = new Subject();
  dates: any[];

  constructor(
    private statsService: StatsService,
    public datePipe: DatePipe,
    private dateUtils: DateUtils
  ) {}
  ngOnInit() {
    this.intitiateLists();
  }
  ngOnChanges(simpleChanges: SimpleChanges): void {
    if(simpleChanges['endDateString'] && this.endDateString){
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
      .statAllPagesCount(this.beginDateString, this.endDateString)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: any) => {
          this.createPagesCountChart(response);
        },
        error: (error: any) => {
          console.log('ErrorProductsCount:', error);
        },
        complete: () => {
          console.log('Observable completed-- getStatAllModelsChart --');
        },
      });
  }


  createPagesCountChart(data: any) {
    this.pagesCountTableData = [];
    let pagesCounts: any[] = [];
    this.pagesCountTableData = data.pagesRecapCount;
    pagesCounts = data.pagesCount;
    this.dates = data.dates;
    this.pagesCountDataSetArray = [];

    this.createPagesChart(pagesCounts);
    this.createPagesOChart(this.pagesCountTableData);
  }

  createPagesChart(pagesCounts:any[]){
    let i = 0;
    this.pagesCountTableData.forEach((item: any) => {
      this.pagesCountDataSetArray.push({
        label: item.name + '/av:' + this.pagesCountTableData[i].avg,
        data: pagesCounts[i],
        fill: false,
        borderColor: this.getRandomColor(item.name),
        tension: 0.4,
        hidden: this.pagesCountTableData[i].avg < 4,
      });
      i++;
    });
    this.pagesCountData = {
      labels: this.dates,
      datasets: this.pagesCountDataSetArray,
    };
  }
  createPagesOChart(data: any) {
    const pagesData: number[] = Object.values(data)
    .filter((obj: any) => obj.payed && obj.name !== "Total")
    .map((obj: any) => obj.payed); // Assuming you want to extract `payed` values

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
    let value = (item.payed*100) / (item.retour+item.payed)
      if (!isNaN(value)) {
        return value.toFixed(2);
      }
      return '0.00';
  }
}
