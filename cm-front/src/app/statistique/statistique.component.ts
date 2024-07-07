import { Component, OnInit, ViewChild } from '@angular/core';
import { DaySales, Colors, Model } from 'src/shared/models/stat';
import { PacketService } from 'src/shared/services/packet.service';
import { Packet } from 'src/shared/models/Packet';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';
import { DateUtils } from 'src/shared/utils/date-utils';
import { Subject, takeUntil } from 'rxjs';
import { ResponsePage } from 'src/shared/models/ResponsePage';
import { ModelService } from 'src/shared/services/model.service';
import { ActivatedRoute } from '@angular/router';
import { ProductCountDTO } from 'src/shared/models/ProductCountDTO';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-statistique',
  templateUrl: './statistique.component.html',
  styleUrls: ['./statistique.component.scss'],
  providers: [DatePipe],
})

export class StatistiqueComponent implements OnInit {
  caluculatePurshasePrice(arg0: any) {

  }


  offersCount: ProductCountDTO[] = [];
  selectedModels: FormControl = new FormControl();
  colorsChartEnabler : boolean = false;
  stockChartEnabler : boolean = false;
  pagesChartEnabler : boolean = false;
  statesChartEnabler : boolean = false;
  offersChartEnabler : boolean = false;
  countProgressEnabler : boolean = false;
  //countProductsPerDay : Number[] = [];
  packets: Packet[] = [];
  daySales!: DaySales;
  sales!: DaySales[];
  totalPerSize: number[] = [];
  sizesInisialized: boolean = false;

  colors: String[] = [];
  //ligne afficher des tailles(titre)
  sizesRow: String[] = [];
  //sizes of first color
  sizes: String[] = [];
  rowByDate: number[] = [];

  title: string = 'Stat-Tab';
  statTab: number[][] = [];
  totalPerSizeRow: number[] = [];

  columnPerColor: number = 8;
  totalPerDayColorColumn: boolean = true;
  totalPerDayColumn: boolean = true;
  qtePerSizeColumn: boolean = false;
  totalRow: boolean = true;
  colorAndSizes: boolean = false;
  selectedModel: string = '';
  allModelsList: any[] = [];
  citys: number[][] = [];
  listPacket: Packet[] = [];

  //tree table

  //packet by date
  rangeDates: Date[] = [];
  range: number = 30;
  today: Date = new Date();
  today_2: Date = new Date(Date.now() - 172800000);
  packetsByDate: Packet[] = [];
  //end packet by date

  startDateString: String;
  endDateString: String;

  cityCounts: CountCitys = {};
  pagesCounts: CountPages = {};
  datesCounts: CountDates = {};
  params: any;
  $unsubscribe: Subject<void> = new Subject();
  totalItems: number;

  //models chart
  packetsDataSetArray: any[];
  packetsOptions: any;
  packetsData: any;
  modelsDataSetArray: any[];
  modelsData: any;
  modelsOptions: any;
  colorsDataSetArray: any[];
  colorsData: any;
  colorsOptions: any;
  offersDataSetArray: any[];
  offersData: any;
  offersOptions: any;
  dataSetArray: any[];
  daysChart: any[];
  daysModelChart: any[];
  //stock chart
  stockData: any;
  stockDataSetArray: any[];
  stockOptions: any;

  basicData: any;
  basicOptions: any;

  modelChartOptions: String[] = ['Chart', 'Table'];
  modelChartBoolean: boolean = true;
  modelTableData: any;

  offerChartOptions: String[] = ['Chart', 'Table'];
  offerChartBoolean: boolean = true;
  offerTableData: any;

  colorChartOptions: String[] = ['Chart', 'Table'];
  colorChartBoolean: boolean = true;
  colorsTableData: any;

  selectedModelChart: String = 'Chart';
  filtredCitysCount: any;
  packetsTableData: any;
  dates: any[];
  enablerOptions : any[] = [{label: 'Off', value: false}, {label: 'On', value: true}];
  progressOptions : any[] = [{label: 'Off', value: false}, {label: 'On', value: true}];

  constructor(
    private packetService: PacketService,
    private statsService: StatsService,
    public datePipe: DatePipe,
    private dateUtils: DateUtils,
    private modelService: ModelService,
    private activateRoute: ActivatedRoute
  ) {}

  StatesData: any;
  StatesOptions: any;
  PagesData: any;
  PagesOptions: any;

  ngOnInit() {
    this.selectedModels.setValue([]);
    //this.rangeDates[0] = new Date();
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue(
      '--text-color-secondary'
    );
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.StatesOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.8,
      plugins: {
        tooltips: {
          mode: 'index',
          intersect: false,
        },
        legend: {
          labels: {
            color: textColor,
          },
        },
      },
      scales: {
        x: {
          stacked: true,
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false,
          },
        },
        y: {
          stacked: true,
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

    this.packetsOptions = {
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

    this.daysChart = ['all'];
    this.basicData = {
      labels: [],
      datasets: [],
    };

    this.modelsData = {
      labels: [],
      datasets: [],
    };

    this.colorsData = {
      labels: [],
      datasets: [],
    };
    this.offersData = {
      labels: [],
      datasets: [],
    };
    this.packetsData = {
      labels: [],
      datasets: [],
    };

    this.stockData = {
      labels: [],
      datasets: [],
    };
    this.getAllModels();
    this.findAllPackets();
  }

  findAllPackets(): void {
    this.setCalendar();
    this.getStatAllModelsChart();
    this.getStatAllPacketsChart();
    if(this.stockChartEnabler)this.getStatStockChart();
    if(this.colorsChartEnabler)this.getStatAllColorsChart();
    if(this.offersChartEnabler)this.getStatAllOffersChart();


    this.packetService
      .findAllPacketsByDate(this.startDateString, this.endDateString)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: any) => {
          console.log('response findAllPacketsByDate:', response);
          this.packets = response;
          this.totalItems = response.totalItems;
          this.filtredCitysCount = this.statsService.getStatsTreeNodesData(
            this.packets
          );

          this.createCityStatChart(this.filtredCitysCount.cityCounts);
          this.createPageStatChart(this.filtredCitysCount.pageCounts);
          //this.createPacketStatChart(this.filtredCitysCount.dateCounts);
        },
        error: (error: Error) => {
          console.log('Error:', error);
        },
      });
  }

  getStatAllModelsChart() {
    this.statsService
      .statAllModels(this.startDateString, this.endDateString,this.countProgressEnabler)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: any) => {
          //console.log('statAllModels', response);
          this.createModelsChart(response);
        },
        error: (error: any) => {
          console.log('ErrorProductsCount:', error);
        },
        complete: () => {
          console.log('Observable completed-- getStatAllModelsChart --');
        },
      });
  }

  getStatStockChart() {
    this.statsService
      .statStock(this.startDateString, this.endDateString)
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
  }

  getStatAllPacketsChart() {
    this.statsService
      .statAllPackets(this.startDateString, this.endDateString)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: any) => {
          this.createPacketsChart(response);
        },
        error: (error: any) => {
          console.log('ErrorProductsCount:', error);
        },
        complete: () => {
          console.log('Observable completed-- getStatAllPacketsChart --');
        },
      });
  }
  getStatAllOffersChart() {
    this.statsService
      .statAllOffers(this.startDateString, this.endDateString)
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

  getStatAllColorsChart() {
    const modelsListIdsArray: number[] = this.selectedModels.value.flatMap(
      (obj: any) => obj.id
    );
    this.statsService
      .statAllColors(
        this.startDateString,
        this.endDateString,
        modelsListIdsArray
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

  createModelsChart(data: any) {
    //console.log('createModelsChart', data);
    this.modelTableData = [];
    let modelsList: string[] = [];
    let modelsCounts: any[] = [];
    //console.log('statStock', statStock);

    this.modelTableData = data.modelsRecapCount;
    modelsList = data.models;
    modelsCounts = data.modelsCount;
    this.dates = data.dates;
    this.modelsDataSetArray = [];

    let i = 0;
    modelsList.forEach((item: any) => {
      this.modelsDataSetArray.push({
        label: item + '/av:' + this.modelTableData[i].avg,
        data: modelsCounts[i],
        fill: false,
        borderColor: this.getRandomColor(item),
        tension: 0.4,
        hidden: this.modelTableData[i].avg<4?true:false,
      });
      i++;
    });

    this.modelsData = {
      labels: this.dates,
      datasets: this.modelsDataSetArray,
    };
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

  createPacketsChart(data: any) {
    console.log('createPacketsChart', data);
    this.packetsTableData = [];
    let statusCounts: any[] = [];

    this.packetsTableData = data.statusRecapCount;
    statusCounts = data.statusCountLists;
    const statusList: string[] = this.packetsTableData.flatMap(
      (obj: any) => obj.name
    );
    //this.packetsDataSetArray = [];
    this.dates = data.dates;
    //this.packetsDataSetArray = ;

    this.packetsData = {
      labels: this.dates,
      datasets: [
        {
          label: 'Echange/av:' + this.calculateAverage(statusCounts[0]),
          data: statusCounts[0],
          fill: false,
          borderColor: 'grey',
          tension: 0.4,
        },
        {
          label: 'Payées/av:' + this.calculateAverage(statusCounts[2]),
          data: statusCounts[2],
          fill: true,
          borderColor: 'pink',
          tension: 0.4,
        },
        {
          label: 'Retour/av:' + this.calculateAverage(statusCounts[1]),
          data: statusCounts[1],
          fill: false,
          borderColor: 'orange',
          borderDash: [5, 5],
          tension: 0.4,
        },
        {
          label: 'Sortie/av:' + this.calculateAverage(statusCounts[3]),
          data: statusCounts[3],
          fill: false,
          borderColor: 'red',
          tension: 0.4,
        },
        {
          label: 'En cours/av:' + this.calculateAverage(statusCounts[4]),
          data: statusCounts[4],
          fill: false,
          borderColor: 'green',
          tension: 0.4,
        },
        {
          label: 'En rupture/av:' + this.calculateAverage(statusCounts[5]),
          data: statusCounts[5],
          fill: false,
          borderColor: 'black',
          tension: 0.4,
        },
        {
          label: 'All/av:' + this.calculateAverage(statusCounts[6]),
          data: statusCounts[6],
          fill: false,
          borderColor: 'blue',
          tension: 0.4,
        },

      ],
    };
  }
  createColorsChart(data: any) {
    this.colorsTableData = [];
    let colorsList: string[] = [];
    let colorsCounts: any[] = [];
    this.colorsTableData = data.colorsRecapCount;
    colorsList = data.colors;
    colorsCounts = data.countColorsLists;

    this.colorsDataSetArray = [];
    this.dates = data.dates;

    let k = 0;
    colorsList.forEach((item: any) => {
      this.colorsDataSetArray.push({
        label: item.name + '/av:' + this.colorsTableData[k].avg,
        data: colorsCounts[k],
        fill: false,
        borderColor: this.getRandomColor(item.name),
        tension: 0.4,
      });
      k++;
    });

    this.colorsData = {
      labels: this.dates,
      datasets: this.colorsDataSetArray,
    };
  }
  createOffersChart(data: any) {
    console.log("createOffersChart",data);

    this.offerTableData = [];
    let offersList: string[] = [];
    let offersCounts: any[] = [];
    this.offerTableData = data.offersRecapCount;
    offersList = data.offers;
    offersCounts = data.countOffersLists;

    this.offersDataSetArray = [];
    this.dates = data.dates;

    let k = 0;
    offersList.forEach((item: any) => {
      console.log("offersList-item:",item);

      this.offersDataSetArray.push({
        label: item.name + '/av:' + this.offerTableData[k].avg,
        data: offersCounts[k],
        fill: false,
        borderColor: this.getRandomColor(item.name),
        tension: 0.4,
        hidden: this.offerTableData[k].avg<3?true:false
      });
      k++;
    });

    this.offersData = {
      labels: this.dates,
      datasets: this.offersDataSetArray,
    };
  }
  createPageStatChart(dataCount: CountPages) {
    const pagesData: number[] = Object.values(dataCount).flatMap(
      (obj) => obj.count
    );
    const pagesLabel: string[] = Object.keys(dataCount);
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
  createCityStatChart(dataCount: CountCitys) {
    const confirmed: number[] = Object.values(dataCount).flatMap(
      (obj) => obj.confirm
    );
    const totCmd: number[] = Object.values(dataCount).flatMap(
      (obj) => obj.count - obj.confirm
    );
    const label: string[] = Object.keys(dataCount);
    this.StatesData = {
      labels: label,
      datasets: [
        {
          type: 'bar',
          label: 'Payée/av:' + this.calculateAverage(confirmed),
          backgroundColor: 'blue',
          data: confirmed,
        },
        {
          type: 'bar',
          label: 'nonPayer/av:' + this.calculateAverage(totCmd),
          backgroundColor: 'green',
          data: totCmd,
        },
      ],
    };
  }

  resetTable() {
    this.rangeDates = [];
    this.setCalendar();
    this.packets = this.packetService.allPackets.slice();
  }

  setCalendar() {
    const oneMonthAgo = new Date();
    const today = new Date();
    oneMonthAgo.setMonth(today.getMonth() - 1);
    if (!this.rangeDates || this.rangeDates.length === 0) {
      this.rangeDates = [oneMonthAgo, today];
    }

    this.startDateString = this.dateUtils.formatDateToString(
      this.rangeDates[0]
    );
    this.endDateString =
      this.rangeDates[1] != null
        ? this.dateUtils.formatDateToString(this.rangeDates[1])
        : this.startDateString;
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
  allDateFilter() {
    this.rangeDates = [new Date(2023, 0, 1), new Date()];
    this.findAllPackets();
  }
  todayDate() {
    this.range = 1;
    if (this.rangeDates[0] != undefined && this.rangeDates[1] == undefined) {
      this.endDateString = this.dateUtils.formatDateToString(this.today);
      this.rangeDates = [this.rangeDates[0], this.today];
      //this.setCalendar();
    } else this.rangeDates = [this.today];

    this.findAllPackets();
  }

  weekDate() {
    this.range = 7;
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(this.today.getDate() - 6);
    this.rangeDates = [oneWeekAgo, this.today];
    this.findAllPackets();
  }
  twoWeekDate() {
    this.range = 14;
    const twoWeekAgo = new Date();
    twoWeekAgo.setDate(this.today.getDate() - 14);
    this.rangeDates = [twoWeekAgo, this.today];
    this.findAllPackets();
  }

  monthDate() {
    this.range = 30;
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(this.today.getMonth() - 1);
    this.rangeDates = [oneMonthAgo, this.today];
    this.findAllPackets();
  }

  minus4daysDate() {
    this.range = 4;
    this.previousDate();
    this.findAllPackets();
  }

  nextDate() {
    const newFirst = new Date(this.rangeDates[0]);
    const newLast =
      this.rangeDates[1] == undefined ? newFirst : new Date(this.rangeDates[1]);
    if (
      newLast.getDate() == this.today.getDate() &&
      newLast.getMonth() == this.today.getMonth()
    ) {
      return;
    }
    newFirst.setDate(newFirst.getDate() + this.range);
    newLast.setDate(newLast.getDate() + this.range);
    this.rangeDates = [newFirst, newLast];

    this.findAllPackets();
  }

  previousDate() {
    const newFirst = new Date(this.rangeDates[0]);
    const newLast =
      this.rangeDates[1] == undefined ? newFirst : new Date(this.rangeDates[1]);
    newFirst.setDate(newFirst.getDate() - this.range);
    newLast.setDate(newLast.getDate() - this.range);
    this.rangeDates = [newFirst, newLast];

    this.findAllPackets();
  }

  clearDate() {
    this.rangeDates = [];
    this.findAllPackets();
  }

  selectModelChart($event: any) {
    this.selectedModelChart = $event.option;
    if ($event.option == 'Chart') this.modelChartBoolean = true;
    else this.modelChartBoolean = false;
  }


  getAllModels() {
    this.modelService.findAllModels().subscribe((data: any) => {
      this.allModelsList = data;
    });
  }

  colorsChartEnablerChange() {
    if(this.colorsChartEnabler){
      this.getStatAllColorsChart();
    }
  }

  offersChartEnablerChange() {
    if(this.offersChartEnabler){
      this.getStatAllOffersChart();
    }
  }

  modelChartEnablerChange() {
    this.getStatAllModelsChart();
  }
  pagesChartEnablerChange() {
    if(this.pagesChartEnabler){
      this.getStatAllPacketsChart();
    }
  }

  statesChartEnablerChange() {
    if(this.statesChartEnabler){
      this.getStatAllPacketsChart();
    }
  }

  stockChartEnablerChange() {
    if(this.stockChartEnabler)this.getStatStockChart();
  }

  formatNumber(item: any) {
    let value = (item.payed*100) / (item.retour+item.payed)
      if (!isNaN(value)) {
        return value.toFixed(2);
      }
      return '0.00';
  }
}

interface CountCitys {
  [name: string]: {
    count: number;
    confirm: number;
    citys: { [name: string]: { count: number; confirm: number } };
  };
}
interface CountPages {
  [name: string]: {
    count: number;
    confirm: number;
  };
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
