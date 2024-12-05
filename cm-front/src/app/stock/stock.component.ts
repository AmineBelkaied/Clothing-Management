import { Component, OnDestroy, OnInit } from '@angular/core';
import { ModelService } from 'src/shared/services/model.service';
import { Model } from 'src/shared/models/Model';
import { Subject, takeUntil, tap } from 'rxjs';
import { StatsService } from 'src/shared/services/stats.service';
import { DateUtils } from 'src/shared/utils/date-utils';

@Component({
  selector: 'app-stock',
  templateUrl: './stock.component.html',
  styleUrls: ['./stock.component.scss'],
})
export class StockComponent implements OnInit, OnDestroy {


  //products: any[][] = [];
  models: Model[] = [];

  chartOptions: string[] = ['Color', 'Size', 'Id'];
  selectedChart: string = 'Color';


  chartEnablerOptions: any[] = [
    { label: 'Off', value: false },
    { label: 'On', value: true },
  ];
  chartEnabler: boolean = false;

  filteredModels = this.models;
  filterQuery: string = '';

  selectedModel: Model;

  isMultiple = false;


  sizes: number[] = [];
  colors: number[] = [];
  modelId: number;


  today: Date = new Date();

  rangeDates: Date[] = [];
  range: number = 30;
  beginDateString: string;
  endDateString: string;


  $unsubscribe: Subject<void> = new Subject();


  modelName: string;

  chart: any;
  chartData: any;
  productChartCounts: number[];
  allDatesChart: Date[];

  dataSetArray: any[];
  basicData: any;
  basicOptions: any;

  modelsDataSetArray: any[];

  daysChart: any[];
  daysModelChart: any[];
  lastModelId: number;

  datesList: any = [];
  datesCount: number = 1;



  constructor(
    private statsService: StatsService,
    private modelService: ModelService,
    private dateUtils: DateUtils
  ) {}

  ngOnInit(): void {

    this.getAllModel();
    // Create the line chart
    this.dataSetArray = [
      {
        label: 'First Dataset',
        data: [],
        fill: false,
        borderColor: '#42A5F5',
        tension: 0.4,
      },
      {
        label: 'Second Dataset',
        data: null,
        fill: false,
        borderColor: '#FFA726',
        tension: 0.4,
      },
    ];
    this.daysChart = ['all'];
    this.basicData = {
      labels: this.daysChart,
      datasets: this.dataSetArray,
    };

    this.basicOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.6,
      plugins: {
        legend: {
          labels: {
            color: '#495057',
          },
        },
      },
      scales: {
        x: {
          ticks: {
            color: '#495057',
          },
          grid: {
            color: '#ebedef',
          },
        },
        y: {
          ticks: {
            color: '#495057',
          },
          grid: {
            color: '#ebedef',
          },
        },
      },
    };
  }

  selectModel(model: Model): void {
    this.selectedModel = model;
    this.modelId = model.id!;
    this.modelName = this.selectedModel.name!;
    this.getStats();
  }

  filterModels() {
    const query = this.filterQuery.toLowerCase();
    this.filteredModels = this.models.filter((model) =>
      model.name.toLowerCase().includes(query)
    );
  }

  getAllModel() {
    this.modelService
      .getModelsSubscriber()
      .pipe(
        takeUntil(this.$unsubscribe),
        tap((models: Model[]) => {
          if (models && models.length > 0) {
            this.models = models.filter((model: Model) => model.enabled);
            //let modelsLength = this.models.length - 1;
            this.filteredModels = this.models;
            this.selectModel(this.models[0]);
          }/*  else {
            console.warn('No models available.');
          } */
        })
      )
      .subscribe({
        error: (err) => console.error('Error in getAllModel:', err),
      });
  }

  getStats() {
    this.setCalendar();
    this.chartEnablerChange();
  }

  getStatModelSoldChart(option: string) {
    if (this.endDateString)
      this.statsService
        .statModelSold(this.modelId, this.beginDateString, this.endDateString)
        .pipe(takeUntil(this.$unsubscribe))
        .subscribe({
          next: (response: any) => {
            this.colors = response.colors;
            this.createChart(response, option);
          },
          error: (error: any) => {
            console.log('ErrorStatProductSold:', error);
          },
          complete: () => {
            console.log('Observable completed-- All statProductSold --');
          },
        });
  }

  createChart(data: any, option: string) {
    let chartList;
    let chartCounts: any[] = [];
    this.datesList = data.dates;
    if (option == 'Size') {
      chartList = data.sizes;
      chartCounts = data.sizesCount;
    } else if (option == 'Id') {
      chartList = data.productRefs;
      chartCounts = data.productsCount;
    } else {
      chartList = data.colors;
      chartCounts = data.colorsCount;
    }

    this.dataSetArray = [];
    let i = 0;
    chartList.forEach((item: any) => {
      let name: string;
      if (option == 'Color') name = item.name;
      else if (option == 'Size') name = item.reference;
      else name = item;

      this.dataSetArray.push({
        label: name + '/av:' + this.calculateAverage(chartCounts[i]),
        data: chartCounts[i],
        fill: false,
        borderColor: this.getRandomColor(name),
        tension: 0.4,
      });
      i++;
    });
    this.basicData = {
      labels: this.datesList,
      datasets: this.dataSetArray,
    };
  }

  getRandomColor(x: string) {
    if (x == 'Noir') return 'black';
    else if (x == 'Vert') return 'green';
    else if (x == 'Beige') return '#D1AF76';
    else if (x == 'Bleu') return 'bleu';
    else if (x == 'Gris') return 'grey';

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

  dateFilterChange() {
    this.setCalendar();
    if (this.endDateString) {
      this.getStats();
    }
  }

  setCalendar() {
    const oneMonthAgo = new Date();
    const today = new Date();
    oneMonthAgo.setMonth(today.getMonth() - 1);
    if (!this.rangeDates || this.rangeDates.length === 0) {
      this.rangeDates = [oneMonthAgo, today];
    }
    const startDate = this.rangeDates[0];
    const endDate = this.rangeDates[1] || startDate;
    const timeDiff = Math.abs(endDate.getTime() - startDate.getTime());
    this.datesCount = Math.ceil(timeDiff / (1000 * 60 * 60 * 24)) + 1;
    this.beginDateString = this.dateUtils.formatDateToString(
      this.rangeDates[0]
    );
    this.endDateString =
      this.rangeDates[1] != null
        ? this.dateUtils.formatDateToString(this.rangeDates[1])
        : this.beginDateString;
  }

  allDateFilter() {
    this.rangeDates = [new Date(2023, 0, 1), new Date()];
    console.log(this.rangeDates);

    this.getStats();
  }

  onClickOutside(){
    this.rangeDates[1] = this.rangeDates[1] ? this.rangeDates[1]
        : this.rangeDates[0];
    this.getStats();
  }
  todayDate() {
    this.range = 1;
    if (this.rangeDates[0] != undefined && this.rangeDates[1] == undefined) {
      this.endDateString = this.dateUtils.formatDateToString(this.today);
      this.rangeDates = [this.rangeDates[0], this.today];
      //this.setCalendar();
    } else this.rangeDates = [this.today,this.today];
    this.getStats();
  }

  yesterdayDate() {
    this.range = 1;
    const yesterday = new Date();
    yesterday.setDate(this.today.getDate() - 1);
    //this.rangeDates= [yesterday,this.today];
    if (this.rangeDates[0] != undefined && this.rangeDates[1] == undefined) {
      this.endDateString = this.dateUtils.formatDateToString(this.today);
      this.rangeDates = [this.rangeDates[0], yesterday];
      //this.setCalendar();
    } else this.rangeDates = [yesterday,yesterday];
    this.getStats();
  }

  weekDate() {
    this.range = 7;
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(this.today.getDate() - 6);
    this.rangeDates = [oneWeekAgo, this.today];
    this.getStats();
  }
  twoWeekDate() {
    this.range = 14;
    const twoWeekAgo = new Date();
    twoWeekAgo.setDate(this.today.getDate() - 14);
    this.rangeDates = [twoWeekAgo, this.today];
    this.getStats();
  }

  monthDate() {
    this.range = 30;
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(this.today.getMonth() - 1);
    this.rangeDates = [oneMonthAgo, this.today];
    this.getStats();
  }

  nextDate() {
    //const newLast = new Date(this.rangeDates[1]);
    const newFirst = new Date(this.rangeDates[0]);
    const newLast =
      this.rangeDates[1] == undefined ? newFirst : new Date(this.rangeDates[1]);

    if (
      newLast.getDate() == this.today.getDate() &&
      newLast.getMonth() == this.today.getMonth()
    ) {
      return;
    }

    // Subtract the range in days
    newFirst.setDate(newFirst.getDate() + this.range);

    newLast.setDate(newLast.getDate() + this.range);
    this.rangeDates = [newFirst, newLast];

    this.getStats();
  }

  previousDate() {
    //const newLast = new Date(this.rangeDates[1]);
    const newFirst = new Date(this.rangeDates[0]);
    const newLast =
      this.rangeDates[1] == undefined ? newFirst : new Date(this.rangeDates[1]);

    // Subtract the range in days
    newFirst.setDate(newFirst.getDate() - this.range);
    newLast.setDate(newLast.getDate() - this.range);
    this.rangeDates = [newFirst, newLast];

    this.getStats();
  }


  chartEnablerChange() {
    if(this.chartEnabler) this.getStatModelSoldChart(this.selectedChart);
  }
  clearDate() {
    this.rangeDates = [];
    this.getStats();
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
