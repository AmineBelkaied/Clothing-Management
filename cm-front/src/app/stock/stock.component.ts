import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { ModelService } from 'src/shared/services/model.service';
import { ProductService } from '../../shared/services/product.service';
import { Model } from 'src/shared/models/Model';
import { ProductHistoryService } from 'src/shared/services/product-history.service';
import { Subject, takeUntil, tap } from 'rxjs';
import { StatsService } from 'src/shared/services/stats.service';
import { ProductCountDTO } from 'src/shared/models/ProductCountDTO';
import { DateUtils } from 'src/shared/utils/date-utils';
import { Color } from 'src/shared/models/Color';
import { Product } from '../../shared/models/Product';

@Component({
  selector: 'app-stock',
  templateUrl: './stock.component.html',
  styleUrls: ['./stock.component.scss'],
})
export class StockComponent implements OnInit,OnDestroy {
  products: Product[][] = [];
  models: Model[] = [];

  chartOptions : string[] = ["Color","Size","Id"];
  selectedChart : string = "Color";

  enablerHistoryOptions : any[] = [{label: 'Off', value: false}, {label: 'On', value: true}];
  historyEnabler : boolean = false;

  chartEnablerOptions : any[] = [{label: 'Off', value: false}, {label: 'On', value: true}];
  chartEnabler : boolean = false;

  selectedModel: Model= {
    id: 0,
    name: '',
    colors: [],
    sizes: [],
    products: [],
    purchasePrice:15,
    earningCoefficient:1,
    deleted: false,
    enabled: false
  };


  productsHistory: any;
  selectedProducts: number[] = [];
  isMultiple = false;
  qte: number = 0;
  comment: string;
  @ViewChild('dt') dt!: Table;
  sizes: any[] = [];
  colors: any[] = [];
  selectAll: boolean = false;
  modelId: number;
  hide0 : boolean = false;
  delai : boolean = false;
  stock:boolean = true;
  today: Date = new Date();

  rangeDates: Date[] = [];
  range: number = 30;
  startDatestring : string;
  endDatestring : string | null;

  searchField: string = '';
  $unsubscribe: Subject<void> = new Subject();
  productsCount: ProductCountDTO[] = [];

  modelName:string;

  chart: any;
  chartData:any;
  productChartCounts: number[];
  allDatesChart: Date[];

  dataSetArray : any[];
  basicData: any;
  basicOptions: any;

  modelsDataSetArray : any[];


  daysChart : any[]
  daysModelChart : any[]
  lastModelId: number;
  addEnabled: boolean = true;
  datesList: any = [];
  stockFabricationDelait: number=30;


  constructor(
    private productService: ProductService,
    private productHistoryService: ProductHistoryService,
    private messageService: MessageService,
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
          tension: .4
      },
      {
          label: 'Second Dataset',
          data: null,
          fill: false,
          borderColor: '#FFA726',
          tension: .4
      }
    ];
    this.daysChart = ['all'];
    this.basicData = {
      labels: this.daysChart,
      datasets: this.dataSetArray
    };



    this.basicOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.6,
      plugins: {
          legend: {
              labels: {
                  color: '#495057'
              }
          }
      },
      scales: {
          x: {
              ticks: {
                  color: '#495057'
              },
              grid: {
                  color: '#ebedef'
              }
          },
          y: {
              ticks: {
                  color: '#495057'
              },
              grid: {
                  color: '#ebedef'
              }
          }
      }
  };
  }

  selectModel(model: Model): void {
    console.log("model",model);

    this.selectedModel = model;
    this.getStats();
  }

  getAllModel() {
    this.modelService.getModelsSubscriber()
      .pipe(
        takeUntil(this.$unsubscribe),
        tap((models: Model[]) => {
          if (models && models.length > 0) {
            this.models = models.filter((model: Model) => model.enabled);
            let modelsLength = this.models.length - 1;
            this.modelId = this.models[modelsLength].id!;
            this.selectedModel = this.models[modelsLength] || this.models[0];
            this.getStats();
          } else {
            console.warn("No models available.");
          }
        })
      )
      .subscribe({
        error: (err) => console.error('Error in getAllModel:', err)
      });
  }

  getStats(){
      this.modelId= this.selectedModel.id!;
      this.setCalendar();
      this.getStockByModelId(this.modelId);
      this.getProductsCountByModelId(this.modelId);
      this.chartEnablerChange();
      this.historyEnablerChange();
  }

  getProductsCountByModelId(modelId : number){
    if(this.endDatestring)
    this.statsService.productsCount(
      modelId,
      this.startDatestring,
      this.endDatestring
      )
    .pipe(takeUntil(this.$unsubscribe))
    .subscribe({
      next: (response: any) => {
        this.productsCount = response;
      },
      error: (error: any) => {
        console.log('ErrorProductsCount:', error);
      },
      complete: () => {
        console.log('Observable completed-- All statProductSold --');
      }
    });
  }

  getStatModelSoldChart(option : string){
    if(this.endDatestring)
    this.statsService.statModelSold(
      this.modelId,
      this.startDatestring,
      this.endDatestring
      )
    .pipe(takeUntil(this.$unsubscribe))
    .subscribe({
      next: (response: any) => {
        console.log("getStatModelSold",response);
        this.colors=response.colors;
        this.createChart(response,option);
      },
      error: (error: any) => {
        console.log('ErrorStatProductSold:', error);
      },
      complete: () => {
        console.log('Observable completed-- All statProductSold --');
      }
    });
  }

  createChart(data: any , option : string){
    let chartList;
    let chartCounts : any[]= [];
    this.datesList = data.dates;
    if(option == "Size") {
      chartList = data.sizes;
      chartCounts =data.sizesCount;
    } else if (option == "Id"){
      //console.log('data',data);
      chartList = data.productRefs;
      chartCounts =data.productsCount;
    } else {
      chartList = data.colors;
      chartCounts =data.colorsCount;
    }

    this.dataSetArray =[];
    let i = 0;
    chartList.forEach((item: any) => {
      let name: string;
      if(option == "Color")name = item.name;
      else if(option == "Size") name= item.reference;
      else name= item;

          this.dataSetArray.push(
              {
                label: name +"/av:"+this.calculateAverage(chartCounts[i]),
                data: chartCounts[i],
                fill: false,
                borderColor: this.getRandomColor(name),
                tension: .4
            }
          )
          i++;
    });
    this.basicData = {
      labels: this.datesList,
      datasets: this.dataSetArray
    };
  }

  getRandomColor(x : string) {
    //console.log('x',x);
    if(x == 'Noir')return 'black'
    else if (x == 'Vert')return 'green'
    else if(x == 'Beige')return '#D1AF76'
    else if(x == 'Bleu')return 'bleu'
    else if(x == 'Gris')return 'grey'

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

  getStockByModelId(modelId: number) {
    this.productService.getStock(modelId).subscribe((result: any) => {
      this.modelName=result.model.name!;
      this.colors=result.model.colors!;
      this.products = result.productsByColor;
      this.sizes = result.sizes;
    });
  }

  getCount(productId:number): number{
    let products = this.productsCount.find(item => item.productId === productId);
    return (products) ? products.countPayed: 0;
  }

  getCountProgress(productId:number): number{
    let product = this.productsCount.find(item => item.productId === productId);
    //console.log("id:"+productId+"/product",product);
    return (product) ? product.countProgress: 0;
  }

  getCountExchange(productId:number): number{
    let products = this.productsCount.find(item => item.productId === productId);
    return (products) ? products.countExchange: 0;
  }

  getCountRupture(productId:number): number{
    let products = this.productsCount.find(item => item.productId === productId);
    return (products) ? products.countOos: 0;
  }



  onCellClick(product: any, event: any, j: number) :void{
    if(this.hide0)this.hide0 = false;
    else{
      const i = this.products[j].findIndex(
        (item: { id: number }) => item.id === product.id
      );
      if (this.selectedProducts.includes(product.id))
        this.unSelectProduct(j, i, this.products[j][i].id);
      else this.selectProduct(j, i, this.products[j][i].id);
      console.log(this.selectedProducts);
    }
  }

  handleColorClick(j: number) {
    if(this.hide0)this.hide0 = false;
    else{
      let haveSelectedItems = false;
      if (this.haveSelectedItems(j, true))
        haveSelectedItems = true
      for (let i = 0; i < this.products[j].length; i++)
        {
          console.log('j:'+j,'i:'+i);
          if (haveSelectedItems)
              this.unSelectProduct(j, i, this.products[j][i].id);
          else
              this.selectProduct(j, i, this.products[j][i].id);
        }
    }
  }

  handleSizeClick(i: number) :void{
    if(this.hide0)this.hide0 = false;
    else{
      let haveSelectedItems = false;
      if (this.haveSelectedItems(i, false))
        haveSelectedItems = true
      for (let j = 0; j < this.products.length; j++)
        {
          //console.log('j:'+j,'i:'+i);
          if (haveSelectedItems)
            this.unSelectProduct(j, i, this.products[j][i].id);
          else
            this.selectProduct(j, i, this.products[j][i].id);
        }
    }
  }

  selectProduct(j: number, i: number, productId: any) {
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
      //console.log('select row', j+' column '+i);
      rows[j].cells[i + 1].style.backgroundColor = 'rgb(220, 231, 243)';
      this.selectedProducts.push(productId);
  }
  unSelectProduct(j: number, i: number, productId: any) {
    //console.log('unSelect row', j+' column '+i);
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
    rows[j].cells[i + 1].setAttribute('style','cursor:pointer');//.style.backgroundColor = 'rgb(255, 255, 255)';
    let index =this.selectedProducts.indexOf(productId);
    if(index>-1) this.selectedProducts.splice(index, 1);
    //console.log('this.selectedProducts after unselect',this.selectedProducts+' unselected '+productId+' index '+index);
  }

  selectAllProducts() {
    for (let j = 0; j < this.products.length; j++)
      for (let i = 0; i < this.products[j].length; i++)
        if (this.selectAll) this.selectProduct(j, i, this.products[j][i].id);
        else this.unSelectProduct(j, i, this.products[j][i].id);
  }

  totalRow(j: number) {
    let totRow = 0;
    for (let i = 0; i < this.products[j].length; i++)
    {
      if (this.stock)totRow += this.products[j][i].qte;
      else totRow += this.getCount(this.products[j][i].id);
    }
    return totRow;
  }

  totalRow2(j: number) {
    let totRow = 0;
    let totRow2 = 0;
    for (let i = 0; i < this.products[j].length; i++)
    {
      totRow += this.products[j][i].qte;
      totRow2 += this.getCount(this.products[j][i].id);
    }
    let nbrJours = this.datesList.length+1;
    let delait = totRow/(totRow2/nbrJours);
    return Number(delait.toFixed(1))+" jours";
  }

  getDaysStock(qte: any,productId: any): number {
    let dayStock= qte/(this.getCount(productId)/this.datesList.length);
    return Number(dayStock.toFixed(1));
  }

  totalColumn(i: number) {
    //console.log("products:",this.products);
    let totColumn = 0;
    if(this.products.length<1)
      return 0;
    for (let j = 0; j < this.products.length; j++)
    if (this.products[j][i])
    if (this.stock){
       totColumn += this.products[j][i].qte;
      }
    else totColumn += this.getCount(this.products[j][i].id);
    return totColumn;
  }

  totalTable() {
    let tot = 0;
    if(this.products.length<1)return 0;
    for (let j = 0; j < this.products.length; j++)
      for (let i = 0; i < this.products[j].length; i++)
        if (this.stock)tot += this.products[j][i].qte;
        else tot += this.getCount(this.products[j][i].id);

    return tot;
  }

  add() {

    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
    if(this.addEnabled){
      this.addEnabled=false;
      if(this.qte == 0)
        this.messageService.add({
          severity: 'warn',
          summary: 'Warning Message',
          detail: 'Quantité 0',
        });
      else if(this.selectedProducts.length < 1)
        this.messageService.add({
          severity: 'warn',
          summary: 'Warning Message',
          detail: 'Aucun élément sélectionné',
        });
      else this.productService
      .addStock(this.selectedProducts, this.qte, +this.modelId, this.comment)
      .subscribe((result: any) => {
        for (let j = 0; j < this.products.length; j++)
          for (let i = 0; i < this.products[j].length; i++)
            if (this.selectedProducts.includes(this.products[j][i].id)) {
              this.products[j][i].qte += this.qte;
              rows[j].cells[i + 1].setAttribute(
                'style',
                'background-color: rgb(152, 251, 152);'
              ); //.style.backgroundColor = 'rgb(152,251,152)';
            }
        this.productsHistory = result;
        this.selectedProducts = [];
        this.selectAll = false;
        this.addEnabled=true;
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Le stock a été ajusté avec succès',
        });
      });
    }


  }

  dateFilterChange() {
    this.setCalendar();
    if(this.endDatestring){
      this.getStats();
    }
  }

  getProductHistory(){
    this.productHistoryService
    .findAll(
      this.modelId,
      this.searchField,
      this.startDatestring,
      this.endDatestring
    )
    .subscribe((result: any) => {
      this.productsHistory = result;
    });
  }

  setCalendar() {
    const oneMonthAgo = new Date();
    const today = new Date();
    oneMonthAgo.setMonth(today.getMonth() - 1);
    if (!this.rangeDates || this.rangeDates.length === 0) {
      this.rangeDates = [oneMonthAgo, today];
    }

    this.startDatestring = this.dateUtils.formatDateToString(this.rangeDates[0])
    this.endDatestring = this.rangeDates[1] != null? this.dateUtils.formatDateToString(this.rangeDates[1]): null;//this.startDatestring;
  }

  haveSelectedItems(index: number, row: boolean):boolean {
    if (row) {
      for (let i = 0; i < this.products[index].length; i++)
        if (this.selectedProducts.includes(this.products[index][i].id)){
          //console.log('have similar row',this.products[index][i].id);
          return true
        }
    } else
      for (let j = 0; j < this.products.length; j++)
        if (this.selectedProducts.includes(this.products[j][index].id)){
          //console.log('have similar size',this.products[j][index].id);
          return true
        }
    return false; // No common items found
  }

  hideRow(j:number){
    if(this.totalRow(j)==0)
      this.products.splice(j,1);
  }

  onDeleteProductsHistory($event: any): void {
    $event.products.forEach((product: any) => {
      for (let j = 0; j < this.products.length; j++)
        for (let i = 0; i < this.products[j].length; i++)
        if(product.productId == this.products[j][i].id)
          this.products[j][i].qte = this.products[j][i].qte - product.qte;
    });
  }

  existingColor(color: Color): boolean {
    return this.colors.some(c => c.id === color.id);

  }

  getSeverity(product: Product) {
    let delait = this.getDaysStock(product.qte,product.id);

    switch (true) {
      case product.qte <1:
        return 'danger';

        case delait < this.stockFabricationDelait:
            return 'warning';

        case delait > 60:
            return 'info';

        default:
            return 'success';
    }
  }
  getSeverityMsg(product: Product) {
    let delait = this.getDaysStock(product.qte,product.id);
    switch (true) {
      case product.qte <1:
        return 'RUPTURE';
        case delait < this.stockFabricationDelait:
            return 'LOW STOCK';
        case delait > 60:
            return 'OVER STOCK';
        default:
            return 'EN STOCK';
    }
  }

  allDateFilter(){
    this.rangeDates = [new Date(2023, 0, 1), new Date()];
    this.getStats();
  }
  todayDate(){
    this.range = 1;
    if(this.rangeDates[0] != undefined && this.rangeDates[1]==undefined)
      {
        this.endDatestring = this.dateUtils.formatDateToString(this.today)
        this.rangeDates= [this.rangeDates[0],this.today];
        //this.setCalendar();
      }
    else this.rangeDates = [this.today];
    this.getStats();
  }
  yesterdayDate(){
    this.range = 1;
    const yesterday = new Date();
    yesterday.setDate(this.today.getDate() - 1);
    //this.rangeDates= [yesterday,this.today];
    if(this.rangeDates[0] != undefined && this.rangeDates[1]==undefined)
      {
        this.endDatestring = this.dateUtils.formatDateToString(this.today)
        this.rangeDates= [this.rangeDates[0],yesterday];
        //this.setCalendar();
      }
    else this.rangeDates = [yesterday];
    this.getStats();
  }

  weekDate(){
    this.range = 7;
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(this.today.getDate() - 6);
    this.rangeDates= [oneWeekAgo,this.today];
    this.getStats();
  }
  twoWeekDate(){
    this.range = 14;
    const twoWeekAgo = new Date();
    twoWeekAgo.setDate(this.today.getDate() - 14);
    this.rangeDates= [twoWeekAgo,this.today];
    this.getStats();
  }

  monthDate(){
    this.range = 30;
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(this.today.getMonth() - 1);
    this.rangeDates= [oneMonthAgo,this.today];
    this.getStats();
  }

  nextDate(){
    //const newLast = new Date(this.rangeDates[1]);
    const newFirst = new Date(this.rangeDates[0]);
    const newLast = this.rangeDates[1]== undefined? newFirst : new Date(this.rangeDates[1]);

    if (newLast.getDate() == this.today.getDate() && newLast.getMonth() == this.today.getMonth()){
      console.log("max");
      return;
    }

    // Subtract the range in days
    newFirst.setDate(newFirst.getDate() + this.range);
    console.log("newFirst1", newFirst);

    newLast.setDate(newLast.getDate() + this.range);
    this.rangeDates = [newFirst, newLast];

    this.getStats();
  }

  previousDate() {
    //const newLast = new Date(this.rangeDates[1]);
    const newFirst = new Date(this.rangeDates[0]);
    const newLast = this.rangeDates[1]== undefined? newFirst : new Date(this.rangeDates[1]);
    console.log("newFirst0", newFirst);

    // Subtract the range in days
    newFirst.setDate(newFirst.getDate() - this.range);
    console.log("newFirst1", newFirst);

    newLast.setDate(newLast.getDate() - this.range);
    this.rangeDates = [newFirst, newLast];

    this.getStats();
  }

  historyEnablerChange() {
    if(this.historyEnabler)
      this.getProductHistory();
  }
  chartEnablerChange(){
    if(this.chartEnabler)
      this.getStatModelSoldChart(this.selectedChart);
  }
  clearDate(){
      this.rangeDates = [];
    this.getStats();
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
