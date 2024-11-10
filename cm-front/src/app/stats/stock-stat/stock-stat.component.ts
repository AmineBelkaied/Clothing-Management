import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';
import { DateUtils } from 'src/shared/utils/date-utils';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-stock-stat',
  templateUrl: './stock-stat.component.html',
  styleUrls: ['./stock-stat.component.scss'],
  providers: [DatePipe],
})

export class StockStatComponent implements OnInit, OnChanges {

  @Input() beginDateString: string;
  @Input() endDateString: string | null;
  stockData: any;
  stockDataSetArray: any[];
  stockOptions: any;
  stockValueTableData: any;
  totalStock = {
    name: 'Stock Name',  // Name of the stock item
    quantity: 0,                 // Quantity of items in stock
    purchasePrice: 0,            // Total purchase price
    sellingPrice: 0,             // Total selling price
    profits: 0                   // Total profits
  };
  $unsubscribe: Subject<void> = new Subject();
  dates: any[];

  constructor(
    private statsService: StatsService,
    public datePipe: DatePipe
  ) {}
  ngOnInit() {
    this.intitiateLists();
    //this.getAllModels();//models List
  }
  ngOnChanges(simpleChanges: SimpleChanges): void {
    if(simpleChanges['endDateString'] && this.endDateString)
        this.getStatStockChart();
  }
  intitiateLists() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue(
      '--text-color-secondary'
    );
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');
    this.stockOptions = {
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
    this.stockData = {
      labels: [],
      datasets: [],
    };
  }

  getStatStockChart() {
    if(this.endDateString)
    this.statsService
      .statStock(this.beginDateString, this.endDateString)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: any) => {
          //console.log('statAllModels', response);
          this.createStockChart(response);
        },
        error: (error: any) => {
          console.log('ErrorStockCount:', error);
        },
        complete: () => {
          console.log('Observable completed-- getStatStockChart --');
        },
      });
      this.getStatValueStockTable();
  }
  getStatValueStockTable() {
    if(this.endDateString)
    this.statsService
      .statValueStock()
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: any) => {
          console.log('statValueStock', response);
          this.stockValueTableData = response;
          this.totalStock = this.stockValueTableData[this.stockValueTableData.length - 1];
          this.stockValueTableData.pop();
        },
        error: (error: any) => {
          console.log('ErrorStockCount:', error);
        },
        complete: () => {
          console.log('Observable completed-- getStatStockChart --');
        },
      });
  }
  createStockChart(data: any) {
    //console.log('createModelsChart', data);

    let modelsList: string[] = [];
    let statStock = data.statStock;
    //console.log('statStock', statStock);
    modelsList = data.models;
    this.dates = data.dates;

    this.stockDataSetArray = [];

    let j = 0;
    statStock.forEach((item: any) => {
      this.stockDataSetArray.push({
        label: modelsList[j] + ':' + item[item.length - 1],
        data: item,
        fill: false,
        borderColor: this.getRandomColor(item),
        tension: 0.4,
      });
      j++;
    });

    this.stockData = {
      labels: this.dates,
      datasets: this.stockDataSetArray,
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
}
