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
  selector: 'app-model-stat',
  templateUrl: './model-stat.component.html',
  styleUrls: ['./model-stat.component.scss'],
  providers: [DatePipe],
})

export class ModelStatComponent implements OnInit,OnChanges {


  selectedModels: FormControl = new FormControl();


  title: string = 'Stat-Tab';
  selectedModel: string = '';
  allModelsList: any[] = [];

  //packet by date
  rangeDates: Date[] = [];
  range: number = 30;
  today: Date = new Date();
  today_2: Date = new Date(Date.now() - 172800000);
  packetsByDate: Packet[] = [];
  //end packet by date

  @Input() countProgressEnabler : boolean = false;
  @Input() beginDateString: string;
  @Input() endDateString: string | null;
  datesCounts: CountDates = {};
  params: any;
  $unsubscribe: Subject<void> = new Subject();
  //totalItems: number;

  modelsDataSetArray: any[];
  modelsData: any;
  modelsOptions: any;

  dataSetArray: any[];
  daysChart: any[];
  daysModelChart: any[];

  basicData: any;
  basicOptions: any;

  modelChartOptions: string[] = ['Chart', 'Table'];
  modelChartBoolean: boolean = true;
  modelTableData: any;

  selectedModelChart: string = 'Chart';

  totalCard = {
    quantity: 0,         // Total articles in stock
    profits: 0,          // Projected profit
    purchasePrice: 0,    // Value of the stock based on purchase price
    sellingPrice: 0      // Selling price of the stock
  };
  dates: any[];
  stockValueTableData: any;
  totalModel = {
    name: 'Default Name', // Product or offer name
    min: 0,               // Minimum range (e.g., price or quantity)
    max: 0,               // Maximum range (e.g., price or quantity)
    avg: 0,               // Average value
    payed: 0,             // Total articles sold
    progress: 0,          // Current progress
    retour: 0,            // Return rate or count
    purchasePrice: 0,     // Purchase price
    sellingPrice: 0,      // Selling price
    profits: 0            // Projected or actual profit
  };
  totalStock: any;

  constructor(
    private statsService: StatsService,
    public datePipe: DatePipe,
    private dateUtils: DateUtils,
    private modelService: ModelService,
  ) {}
  ngOnInit() {
    this.intitiateLists();
  }
  ngOnChanges(simpleChanges: SimpleChanges): void {
      if((simpleChanges['endDateString'] || simpleChanges['countProgressEnabler']) && this.endDateString)
      {
          this.getStatAllModelsChart();
      }
  }

  getStatAllModelsChart() {
    if(this.endDateString)
    this.statsService
      .statAllModels(this.beginDateString, this.endDateString,this.countProgressEnabler)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: any) => {
          //console.log('statAllModels', response);
          this.createModelsChart(response);
          this.totalCard = response.statValuesDashboard[0];
        },
        error: (error: any) => {
          console.log('ErrorProductsCount:', error);
        },
        complete: () => {
          console.log('Observable completed-- getStatAllModelsChart --');
        },
      });
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

  createModelsChart(data: any) {
    this.modelTableData = [];
    let modelsCounts: any[] = [];
    this.modelTableData = data.modelsRecapCount;
    modelsCounts = data.modelsCount;
    this.dates = data.dates;
    this.modelsDataSetArray = [];

    let i = 0;
    this.modelTableData.forEach((item: any) => {
      this.modelsDataSetArray.push({
        label: item.name + '/av:' + this.modelTableData[i].avg,
        data: modelsCounts[i],
        fill: false,
        borderColor: this.getRandomColor(item.name),
        tension: 0.4,
        hidden: this.modelTableData[i].avg < 4,
      });
      i++;
    });
    this.totalModel = this.modelTableData[this.modelTableData.length - 1];
    this.modelTableData.pop();
    this.modelsData = {
      labels: this.dates,
      datasets: this.modelsDataSetArray,
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
  calculateAverage(numbers: number[]): number {
    if (numbers.length === 0) {
      return 0; // Handle division by zero
    }
    const sum = numbers.reduce((acc, current) => acc + current, 0);
    const average = sum / numbers.length;
    return Number(average.toFixed(1));
  }
  calculateSomme(numbers: number[]): number {
    if (numbers.length === 0) {
      return 0; // Handle division by zero
    }
    const sum = numbers.reduce((acc, current) => acc + current, 0);
    return Number(sum);
  }

  formatNumber(item: any) {
    let value = (item.payed*100) / (item.retour+item.payed)
      if (!isNaN(value)) {
        return value.toFixed(2);
      }
      return '0.00';
  }

  intitiateLists() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue(
      '--text-color-secondary'
    );
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.modelsOptions = {
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

    this.basicData = {
      labels: [],
      datasets: [],
    };

    this.modelsData = {
      labels: [],
      datasets: [],
    };

  }
}

interface CountDates {
  [date: string]: {
    count: number;
    payed: number;
    return: number;
    exchange: number;
    out: number;
  };
}
