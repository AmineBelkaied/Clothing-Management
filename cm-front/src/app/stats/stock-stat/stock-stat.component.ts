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
  @Input() calculateSomme:(numbers: number[]) => number;
  @Input() getRandomColor:(x: string)=> string;
  @Input() getMinMax:(numbers: number[])=> string;
  @Input() formatNumber:(item: any)=> string;

  stockChartData: any;
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
  stockTable: any;

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
    this.stockChartData = {
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
          this.stockValueTableData = response.statStockTable;
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
    this.totalStock = this.stockValueTableData[this.stockValueTableData.length - 1];
    this.stockValueTableData.pop();
  }
  createStockChart(data: any) {
    let modelsList: any[] = [];
    let statStockChart = data.statStockChart;
    modelsList = data.models;
    this.dates = data.dates;

    this.stockDataSetArray = [];

    let j = 0;
    statStockChart.forEach((item: any) => {
      let name = modelsList[j].name;
      let qte = item[item.length - 1];
      this.stockDataSetArray.push({
        label: name + ':' + qte,
        data: item,
        fill: false,
        borderColor: this.getRandomColor(name),
        tension: 0.4,
        hidden: qte < 5
      });
      j++;
    });

    this.stockChartData = {
      labels: this.dates,
      datasets: this.stockDataSetArray,
    };
  }

}
