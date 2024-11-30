import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Packet } from 'src/shared/models/Packet';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';
import { Subject, takeUntil } from 'rxjs';
import { FormControl } from '@angular/forms';

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
  modelsCounts: any[] = [];

  totals = {
    paid: 0,
    progress: 0,
    retour: 0,
    purchasePrice: 0,
    sellingPrice: 0,
    profits: 0,
  };

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
    paid: 0,             // Total articles sold
    progress: 0,          // Current progress
    retour: 0,            // Return rate or count
    purchasePrice: 0,     // Purchase price
    sellingPrice: 0,      // Selling price
    profits: 0            // Projected or actual profit
  };
  totalStock: any;

  constructor(
    private statsService: StatsService,
    public datePipe: DatePipe
  ) {}
  ngOnInit() {
    this.intitiateLists();
  }
  ngOnChanges(simpleChanges: SimpleChanges): void {
      if((simpleChanges['endDateString'] || simpleChanges['countProgressEnabler']) && this.endDateString)
      {
          this.getStatAllModelsChart();

          this.getStatAllModelsTable();
      }
  }

  getStatAllModelsChart() {
    if(this.endDateString)
    this.statsService
      .statAllModelsChart(this.beginDateString, this.endDateString,this.countProgressEnabler)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (data: any) => {
          this.dates = data.dates;
          this.createModelsChart(data);
        },
        error: (error: any) => {
          console.log('ErrorProductsCount:', error);
        },
        complete: () => {
          console.log('Observable completed-- getStatAllModelsChart --');
        },
      });
  }

  getStatAllModelsTable() {
    if(this.endDateString)
    this.statsService
      .statAllModels(this.beginDateString, this.endDateString,this.countProgressEnabler)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (data: any) => {
          this.modelTableData = [];
          //this.createModelsTable(data.modelsStat);
          this.modelTableData = data.modelsStat;
          this.totalCard = data.statValuesDashboard[0];
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

  createModelsChart(data: any) {
    this.modelsDataSetArray = [];
    this.modelsCounts = data.modelsCount;
    let i = 0;
    this.modelsCounts.forEach((item: any) => {
      let x = this.calculateAverage(this.modelsCounts[i]);
      this.modelsDataSetArray.push({
        label: item.name+":"+x,
        data: this.modelsCounts[i],
        fill: false,
        borderColor: this.getRandomColor(item.name),
        tension: 0.4,
        hidden: x < 3
      });
      i++;
    });
    this.modelsData = {
      labels: this.dates,
      datasets: this.modelsDataSetArray,
    };
  }

  createModelsTable(data: any) {
    this.modelTableData = data;
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
    console.log(numbers);

    if (numbers.length === 0 || numbers == undefined) {
      return 0; // Handle division by zero
    }
    const sum = numbers.reduce((acc, current) => acc + current, 0);
    const average = sum / numbers.length;
    return Number(average.toFixed(1));
  }

  getMinMax(numbers: number[]): string {
    console.log(numbers);

    if (numbers.length === 0 || numbers == undefined) {
      return ""; // Handle division by zero
    }
    let min = 1000;
    let max = 0;
    for(let x of numbers){
      if(x<min) min = x;
      if(x>max) max = x;
    }
    return min+"-"+max;
  }
  calculateSomme(numbers: number[]): number {
    if (numbers.length === 0) {
      return 0; // Handle division by zero
    }
    const sum = numbers.reduce((acc, current) => acc + current, 0);
    return Number(sum);
  }

  formatNumber(item: any) {
    let value = (item.countPaid*100) / (item.countReturn+item.countPaid)
      if (!isNaN(value)) {
        return value.toFixed(2);
      }
      return '0.00';
  }

  getItemIndex(item: any): number[] {
    let x = this.modelTableData.indexOf(item);
    let y = this.modelsCounts[x];
    console.log(this.modelsCounts);

    return y;
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
    this.modelTableData.forEach((item : any) => {
      totals.paid += item.countPaid || 0;
      totals.progress += item.countProgress || 0;
      totals.retour += item.countReturn || 0;
      totals.purchasePrice += (item.model.purchasePrice * item.countPaid) || 0;
      totals.sellingPrice += ((item.model.purchasePrice * item.countPaid) + item.profits) || 0;
      totals.profits += item.profits || 0;
      i++;
    });
    return totals;
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

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}

interface CountDates {
  [date: string]: {
    count: number;
    paid: number;
    return: number;
    exchange: number;
    out: number;
  };
}
