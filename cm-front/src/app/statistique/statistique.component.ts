import { Component, OnInit } from '@angular/core';
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
  selector: 'app-statistique',
  templateUrl: './statistique.component.html',
  styleUrls: ['./statistique.component.scss'],
  providers: [DatePipe],
})

export class StatistiqueComponent implements OnInit {

  offersCount: ProductCountDTO[] = [];
  selectedModels: FormControl = new FormControl();
  packetsChartEnabler : boolean = true;
  modelsChartEnabler : boolean = false;
  pagesCountChartEnabler : boolean = false;
  offersChartEnabler : boolean = false;
  colorsChartEnabler : boolean = false;
  stockChartEnabler : boolean = false;
  statesChartEnabler : boolean = false;
  countProgressEnabler : boolean = false;
  //countProductsPerDay : Number[] = [];
  packets: Packet[] = [];
  daySales!: DaySales;
  sales!: DaySales[];
  totalPerSize: number[] = [];
  sizesInisialized: boolean = false;

  colors: string[] = [];
  //ligne afficher des tailles(titre)
  sizesRow: string[] = [];
  //sizes of first color
  sizes: string[] = [];
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

  beginDateString: string;
  endDateString: string | null;

  cityCounts: CountCitys = {};
  pagesCounts: CountPages = {};
  datesCounts: CountDates = {};
  params: any;
  $unsubscribe: Subject<void> = new Subject();
  //totalItems: number;

  //models chart
  packetsDataSetArray: any[];
  packetsOptions: any;
  packetsData: any;
  modelsDataSetArray: any[];
  modelsData: any;
  modelsOptions: any;
  pagesCountDataSetArray: any[];
  pagesCountData: any;
  pagesCountOptions: any;
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

  modelChartOptions: string[] = ['Chart', 'Table'];
  modelChartBoolean: boolean = true;
  modelTableData: any;

  pagesCountChartOptions: string[] = ['Chart', 'Table'];
  pagesCountChartBoolean: boolean = true;
  pagesCountTableData: any;

  offerChartOptions: string[] = ['Chart', 'Table'];
  offerChartBoolean: boolean = true;
  offerTableData: any;

  colorChartOptions: string[] = ['Chart', 'Table'];
  colorChartBoolean: boolean = true;
  colorsTableData: any;

  selectedModelChart: string = 'Chart';
  filtredCitysCount: any;
  packetsTableData: any;
  dates: any[];
  deliveryCompanyName : string = "ALL";
  deliveryCompanyList: DeliveryCompany[] = [];
  stockValueTableData: any;
  totalModel: any;
  totalOffer: any;
  totalStock: any;

  constructor(
    private packetService: PacketService,
    private statsService: StatsService,
    public datePipe: DatePipe,
    private dateUtils: DateUtils,
    private modelService: ModelService,
    private deliveryCompanyService: DeliveryCompanyService,
  ) {}

  StatesData: any;
  StatesOptions: any;
  PagesData: any;
  PagesOptions: any;

  ngOnInit() {

    this.deliveryCompanyService.getDeliveryCompaniesSubscriber()
    .subscribe((stesList: DeliveryCompany[]) => {
      this.deliveryCompanyList = stesList.filter((deliveryCompany: any) => deliveryCompany.enabled);
      //console.log("this.stes",this.deliveryCompanyList);
      const uniqueCompanies = new Map();
        stesList.forEach((company: DeliveryCompany) => {
            if (!uniqueCompanies.has(company.name)) {
                uniqueCompanies.set(company.name, company);
            }
        });
        // Convert the Map values back to an array
        this.deliveryCompanyList = Array.from(uniqueCompanies.values());
    });
    this.selectedModels.setValue([]);

    this.intitiateLists();
    //this.getAllModels();//models List
    this.findAllPackets();
  }



  findAllPackets(): void {
    this.setCalendar();
    if(this.endDateString){
      this.packetsChartEnablerChange();
      this.modelsChartEnablerChange();
      this.pagesCountChartEnablerChange();
      this.offersChartEnablerChange();
      this.stockChartEnablerChange();
      this.colorsChartEnablerChange();
      this.statesChartEnablerChange();
    }

  }


  getStatesChart(){
    if(this.endDateString)
    this.statsService
    .statAllStates(this.beginDateString, this.endDateString)
    .pipe(takeUntil(this.$unsubscribe))
    .subscribe({
      next: (response: any) => {
        this.createStatesChart(response);
      },
      error: (error: Error) => {
        console.log('Error:', error);
      },
    });
  }

  createStatesChart(data: any) {
    const labels: String[] = Object.values(data).flatMap(
      (obj : any) => obj.governerateName
    );
    const payed: number[] = Object.values(data).flatMap(
      (obj : any) => obj.countPayed
    );
    const returned: number[] = Object.values(data).flatMap(
      (obj : any) => obj.countReturn
    );
    this.StatesData = {
      labels: labels,
      datasets: [
        {
          type: 'bar',
          label: 'Payée:',
          backgroundColor: 'blue',
          data: payed,
        },
        {
          type: 'bar',
          label: 'Retour:',
          backgroundColor: 'green',
          data: returned,
        },
      ],
    };
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
        },
        error: (error: any) => {
          console.log('ErrorProductsCount:', error);
        },
        complete: () => {
          console.log('Observable completed-- getStatAllModelsChart --');
        },
      });
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

  getStatAllPacketsChart() {
    if(this.deliveryCompanyName==null)this.deliveryCompanyName="ALL";
    if(this.endDateString){
      this.statsService
        .statAllPackets(this.beginDateString, this.endDateString, this.deliveryCompanyName)
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
/*       this.statsService
        .statPacketsDashboard(this.beginDateString, this.endDateString, this.deliveryCompanyName)
        .pipe(takeUntil(this.$unsubscribe))
        .subscribe({
          next: (response: any) => {
            console.log(response);

            //this.createPacketsChart(response);
          },
          error: (error: any) => {
            console.log('ErrorProductsCount:', error);
          },
          complete: () => {
            console.log('Observable completed-- getStatAllPacketsChart --');
          },
        });*/
      }
  }
  getStatAllOffersChart() {
    if(this.endDateString)
    this.statsService
      .statAllOffers(this.beginDateString, this.endDateString)
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
    let statusCounts: any[];

    this.packetsTableData = data.statusRecapCount;
    statusCounts = data.statusCountLists;
    this.dates = data.dates;

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
  createOffersChart(data: any) {
    console.log("createOffersChart",data);

    this.offerTableData = [];
    let offersCounts: any[];
    this.offerTableData = data.offersRecapCount;
    offersCounts = data.countOffersLists;

    this.offersDataSetArray = [];
    this.dates = data.dates;

    let k = 0;
    this.offerTableData.forEach((item: any) => {
      this.offersDataSetArray.push({
        label: item.name + '/av:' + this.offerTableData[k].avg,
        data: offersCounts[k],
        fill: false,
        borderColor: this.getRandomColor(item.name),
        tension: 0.4,
        hidden: this.offerTableData[k].avg < 3
      });
      k++;
    });
    this.totalOffer = this.offerTableData[this.offerTableData.length - 1];
    this.offerTableData.pop();

    this.offersData = {
      labels: this.dates,
      datasets: this.offersDataSetArray,
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

    this.beginDateString = this.dateUtils.formatDateToString(
      this.rangeDates[0]
    );
    this.endDateString =
      this.rangeDates[1]
        ? this.dateUtils.formatDateToString(this.rangeDates[1])
        : null;//this.beginDateString;
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
    if (this.rangeDates[0] && this.rangeDates[1]) {
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
      this.rangeDates[1] ? newFirst : new Date(this.rangeDates[1]);
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
      this.rangeDates[1] ? newFirst : new Date(this.rangeDates[1]);
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
    this.modelChartBoolean = $event.option == 'Chart';
  }


  getAllModels() {
    this.modelService.findAllModels().subscribe((data: any) => {
      this.allModelsList = data;
    });
  }


  packetsChartEnablerChange() {
    if(this.packetsChartEnabler)
      this.getStatAllPacketsChart();
  }

  modelsChartEnablerChange() {
    if(this.modelsChartEnabler)
      this.getStatAllModelsChart();
  }
  pagesCountChartEnablerChange() {
    if(this.pagesCountChartEnabler)
      this.getStatAllPagesCountChart();
  }

  colorsChartEnablerChange() {
    if(this.colorsChartEnabler)
      this.getStatAllColorsChart();
  }

  offersChartEnablerChange() {
    if(this.offersChartEnabler)
      this.getStatAllOffersChart();
  }

  statesChartEnablerChange() {
    if(this.statesChartEnabler)
      this.getStatesChart();
  }

  stockChartEnablerChange() {
    if(this.stockChartEnabler)
      this.getStatStockChart();
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
