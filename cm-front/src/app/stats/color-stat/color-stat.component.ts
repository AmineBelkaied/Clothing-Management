import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-color-stat',
  templateUrl: './color-stat.component.html',
  styleUrls: ['./color-stat.component.scss'],
  providers: [DatePipe],
})

export class ColorStatComponent implements OnInit,OnChanges,OnDestroy {

  @Input() beginDateString: string;
  @Input() endDateString: string | null;

  colorsDataSetArray: any[];
  colorsData: any;
  colorsOptions: any;
  colorChartOptions: string[] = ['Chart', 'Table'];
  colorChartBoolean: boolean = true;
  colorsTableData: any;
  dates: any[];
  $unsubscribe: Subject<void> = new Subject();

  constructor(
    private statsService: StatsService,
    public datePipe: DatePipe,
  ) {}

  ngOnInit() {
    this.intitiateLists();

  }
  ngOnChanges(simpleChanges: SimpleChanges): void {
    if(simpleChanges['endDateString'] && this.endDateString)
      {
          this.getStatAllColorsChart();
      }
  }

  getStatAllColorsChart() {
    if(this.endDateString)
    this.statsService
      .statAllColors(
        this.beginDateString,
        this.endDateString
      )
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: any) => {
          this.createColorsChart(response);
        },
        error: (error: any) => {
          console.log('ErrorProductsCount:', error);
        },
        complete: () => {

          console.log('Observable completed-- getStatAllColorsChart --');
        },
      });
  }

  createColorsChart(data: any) {
    this.colorsTableData = [];
    let colorsCounts: any[];
    this.colorsTableData = data.colorsRecapCount;
    colorsCounts = data.countColorsLists;

    this.colorsDataSetArray = [];
    this.dates = data.dates;

    let k = 0;
    this.colorsTableData.forEach((item: any) => {
      this.colorsDataSetArray.push({
        label: item.name+'/av:' + this.colorsTableData[k].avg,
        data: colorsCounts[k],
        fill: false,
        borderColor: item.hex,
        tension: 0.4,
        hidden: this.colorsTableData[k].avg < 3
      });
      k++;
    });

    this.colorsData = {
      labels: this.dates,
      datasets: this.colorsDataSetArray,
    };
  }


  intitiateLists() {

    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue(
      '--text-color-secondary'
    );
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');
    this.colorsOptions = {
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
    this.colorsData = {
      labels: [],
      datasets: [],
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

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
