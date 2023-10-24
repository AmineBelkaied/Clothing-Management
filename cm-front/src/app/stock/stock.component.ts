import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { ModelService } from 'src/shared/services/model.service';
import { ProductService } from '../../shared/services/product.service';
import { Model } from 'src/shared/models/Model';
import { ProductHistoryService } from 'src/shared/services/product-history.service';
import { Subject, switchMap, takeUntil } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { StatsService } from 'src/shared/services/stats.service';
import { ProductCountDTO } from 'src/shared/models/ProductCountDTO';
import { DateUtils } from 'src/shared/utils/date-utils';

@Component({
  selector: 'app-stock',
  templateUrl: './stock.component.html',
  styleUrls: ['./stock.component.scss'],
})
export class StockComponent implements OnInit {
  products: any[] = [];
  models: Model[] = [];

  chartOptions : String[] = ["Color","Size","Id"];
  selectedChart : String = "Color";

  selectedModel: Model= {
    id: null,
    name: '',
    reference: '',
    colors: [],
    sizes: [],
    products:[],
  };


  productsHistory: any;
  selectedProducts: number[] = [];
  isRowSelectable = true;
  isMultiple = false;
  qte: number = 0;
  @ViewChild('dt') dt!: Table;
  @ViewChild('el') el!: ElementRef;
  sizes: any[] = [];
  selectAll: boolean = false;
  modelId: number;
  hide0 : boolean = false;
  stock:boolean = true;

  rangeDates: Date[] = [];
  startDateString : String;
  endDateString : String;

  searchField: string = '';
  $unsubscribe: Subject<void> = new Subject();
  productsCount: ProductCountDTO[] = [];

  modelName:String;

  chart: any;
  chartData:any;
  productChartCounts: number[];
  allDatesChart: Date[];

  dataSetArray : any[];
  basicData: any;
  basicOptions: any;
  daysChart : any[]

  constructor(
    private productService: ProductService,
    private productHistoryService: ProductHistoryService,
    private messageService: MessageService,
    private activateRoute: ActivatedRoute,
    private statsService: StatsService,
    private modelService: ModelService,
    private dateUtils: DateUtils,
  ) {}

  ngOnInit(): void {

    this.getAllModel();
    //this.rangeDates = [new Date()]
    this.activateRoute.params.subscribe(params => {
        this.modelId = +params['id'];
        this.setCalendar();
        console.log("stats");

        this.getStats();
    });

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

  getStats(){
      this.getStatModelSoldChart(this.modelId,this.selectedChart);
      this.getStockByModelId(this.modelId);
      this.getProductsCountByModel(this.modelId);
      this.getProductHistory();
  }

  getAllModel(){
    this.modelService.findAllModels().subscribe((data: any) => {
      this.models = data;
      //console.log('this.models',this.models);
    });
  }


  getProductsCountByModel(modelId : number){
    this.statsService.productsCount(
      modelId,
      this.startDateString,
      this.endDateString
      )
    .pipe(takeUntil(this.$unsubscribe))
    .subscribe({
      next: (response: any) => {
        //console.log("statProductSold",response);
        //this.statsService.getStatsTreeNodesData(response);
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

  getStatModelSoldChart(modelId: number,option : String){
    this.statsService.statModelSold(
      modelId,
      this.startDateString,
      this.endDateString
      )
    .pipe(takeUntil(this.$unsubscribe))
    .subscribe({
      next: (response: any) => {
        console.log("getStatModelSold",response);
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

  selectChart($event: any){
    this.getStatModelSoldChart(this.modelId,$event.option)
  }

  createChart(data: any , option : String){
    let chartList = [];
    let chartCounts : any[]= [];

    if(option == "Size") {
      chartList = data.sizes;
      chartCounts =data.sizesCount;
    } else if (option == "Id"){
      chartList = data.productIds;
      chartCounts =data.productsCount;
    } else {
      chartList = data.colors;
      chartCounts =data.colorsCount;
    }

    this.dataSetArray =[];
    let i = 0;
    chartList.forEach((item: any) => {
          this.dataSetArray.push(
              {
                label: item +"/av:"+this.calculateAverage(chartCounts[i]),
                data: chartCounts[i],
                fill: false,
                borderColor: this.getRandomColor(),
                tension: .4
            }
          )
          i++;
    });
    this.basicData = {
      labels: data.dates,
      datasets: this.dataSetArray
    };
  }
  getRandomColor() {
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
      this.products = result.productsByColor;
      this.modelName = this.products[0][0].model.name;
      console.log('this.products[0]',this.products[0]);
      this.sizes = result.sizes;
    });
  }

  getCount(productId:number): number{
    let countProducts = this.productsCount.find(item => item.productId === productId);
    return (countProducts != undefined) ? countProducts.count : 0;
  }



  onCellClick(product: any, event: any, j: number) :void{
    const i = this.products[j].findIndex(
      (item: { id: number }) => item.id === product.id
    );
    if (this.selectedProducts.includes(product.id))
      this.unSelectProduct(j, i, this.products[j][i].id);
    else this.selectProduct(j, i, this.products[j][i].id);
    console.log(this.selectedProducts);
  }

  handleColorClick(j: number) {
    if (this.haveSelectedItems(j, true))
      for (var i = 0; i < this.products[j].length; i++)
        this.unSelectProduct(j, i, this.products[j][i].id);
    else
      for (var i = 0; i < this.products[j].length; i++)
        this.selectProduct(j, i, this.products[j][i].id);
    //console.log('selectedProducts',this.selectedProducts);
  }

  handleSizeClick(i: number) :void{
    if (this.haveSelectedItems(i, false))
      for (var j = 0; j < this.products.length; j++)
        this.unSelectProduct(j, i, this.products[j][i].id);
    else
      for (var j = 0; j < this.products.length; j++)
        this.selectProduct(j, i, this.products[j][i].id);
    //console.log('selectedProducts',this.selectedProducts);
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
    for (var j = 0; j < this.products.length; j++)
      for (var i = 0; i < this.products[j].length; i++)
        if (this.selectAll) this.selectProduct(j, i, this.products[j][i].id);
        else this.unSelectProduct(j, i, this.products[j][i].id);
  }

  totalRow(j: number) {
    let totRow = 0;
    for (var i = 0; i < this.products[j].length; i++)
    {
      if (this.stock==true)totRow += this.products[j][i].quantity;
      else totRow += this.getCount(this.products[j][i].id);
    }

    return totRow;
  }

  totalColumn(i: number) {
    let totColumn = 0;
    for (var j = 0; j < this.products.length; j++)
    if (this.stock==true)totColumn += this.products[j][i].quantity;
    else totColumn += this.getCount(this.products[j][i].id);
    return totColumn;
  }

  totalTable() {
    let tot = 0;
    for (var j = 0; j < this.products.length; j++)
      for (var i = 0; i < this.products[j].length; i++)
        if (this.stock==true)tot += this.products[j][i].quantity;
        else tot += this.getCount(this.products[j][i].id);

    return tot;
  }

  add() {
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
    this.productService
      .addStock(this.selectedProducts, this.qte, +this.modelId)
      .subscribe((result: any) => {
        console.log('result', result);

        for (var j = 0; j < this.products.length; j++)
          for (var i = 0; i < this.products[j].length; i++)
            if (this.selectedProducts.includes(this.products[j][i].id)) {
              //console.log('prod', this.products[j][i].color.name);
              this.products[j][i].quantity += this.qte;
              rows[j].cells[i + 1].setAttribute(
                'style',
                'background-color: rgb(152, 251, 152);'
              ); //.style.backgroundColor = 'rgb(152,251,152)';
            }
        this.productsHistory = result;
        this.selectedProducts = [];
        this.selectAll = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Le stock a été modifié avec succés',
        });
      });
  }

  dateFilterChange(event: any) {
    this.setCalendar();
    this.getStats();
  }

  getProductHistory(){
    this.productHistoryService
    .findAll(
      this.modelId,
      this.searchField,
      this.startDateString,
      this.endDateString
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

    this.startDateString = this.dateUtils.formatDateToString(this.rangeDates[0])
    this.endDateString = this.rangeDates[1] != null? this.dateUtils.formatDateToString(this.rangeDates[1]): this.startDateString;
  }

  haveSelectedItems(index: number, row: boolean):boolean {
    if (row) {
      for (var i = 0; i < this.products[index].length; i++)
        if (this.selectedProducts.includes(this.products[index][i].id)){
          console.log('have similar row',this.products[index][i].id);
          return true
        }
    } else
      for (var j = 0; j < this.products.length; j++)
        if (this.selectedProducts.includes(this.products[j][index].id)){
          console.log('have similar size',this.products[j][index].id);
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
      for (var j = 0; j < this.products.length; j++)
        for (var i = 0; i < this.products[j].length; i++)
        if(product.productId == this.products[j][i].id)
          this.products[j][i].quantity = this.products[j][i].quantity - product.quantity;
    });
  }

  getSeverity(product: any,button:boolean) {
    if (button)
    switch (true) {
      case product.quantity <1:
        return 'danger';

        case product.quantity < 10:
            return 'warning';

        default:
            return 'success';
    }
    else switch (true) {
      case product.quantity <1:
        return 'RUPTURE';

        case product.quantity < 10:
            return 'LOW STOCK';

        default:
            return 'EN STOCK';
    }
  }
}
