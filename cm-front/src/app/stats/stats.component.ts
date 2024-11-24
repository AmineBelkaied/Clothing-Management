import { Component, OnInit } from '@angular/core';
import { DaySales} from 'src/shared/models/stat';
import { PacketService } from 'src/shared/services/packet.service';
import { Packet } from 'src/shared/models/Packet';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';
import { DateUtils } from 'src/shared/utils/date-utils';
import { Subject, takeUntil } from 'rxjs';
import { ModelService } from 'src/shared/services/model.service';
import { ProductCountDTO } from 'src/shared/models/ProductCountDTO';
import { FormControl } from '@angular/forms';
import { DeliveryCompanyService } from 'src/shared/services/delivery-company.service';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';

@Component({
  selector: 'app-stats',
  templateUrl: './stats.component.html',
  styleUrls: ['./stats.component.scss'],
  providers: [DatePipe],
})

export class StatsComponent implements OnInit {

  rangeDates: Date[] = [];
  range: number = 30;
  today: Date = new Date();
  today_2: Date = new Date(Date.now() - 172800000);

  beginDateString: string;
  endDateString: string | null;

  $unsubscribe: Subject<void> = new Subject();

  dates: any[];
  activeIndex: number;
  selectedIndex: number = 0;
  deliveryCompanyList: DeliveryCompany[] = [];
  deliveryCompanyName : string = "ALL";

  countProgressEnabler : boolean = false;

  constructor(
    public datePipe: DatePipe,
    private dateUtils: DateUtils,
    private deliveryCompanyService: DeliveryCompanyService,
  ) {}



  ngOnInit() {

    this.setCalendar();
    this.deliveryCompanyService.getDeliveryCompaniesSubscriber()
    .subscribe((stesList: DeliveryCompany[]) => {
      this.deliveryCompanyList = stesList.filter((deliveryCompany: any) => deliveryCompany.enabled);
      const uniqueCompanies = new Map();
        stesList.forEach((company: DeliveryCompany) => {
            if (!uniqueCompanies.has(company.name)) {
                uniqueCompanies.set(company.name, company);
            }
        });
        this.deliveryCompanyList = Array.from(uniqueCompanies.values());
    });
  }

  getTitle(index: number) {
    switch (index){
      case 0: return "Packets";
      case 1: return "Models";
      case 2: return "Offers";
      case 3: return "Pages";
      case 4: return "Colors";
      case 5: return "Gouvernerat";
      case 6: return "Stock";
      default: return "Statistique";
    }
  }

  resetTable() {
    this.rangeDates = [];
    this.setCalendar();
  }
  onClickOutside(){
    this.rangeDates[1] = this.rangeDates[1] ? this.rangeDates[1]
        : this.rangeDates[0];
        this.setCalendar();
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
        : null;//this.dateUtils.formatDateToString(this.rangeDates[0])

    this.range = this.getDateRange();
  }
  getDateRange(){
    if(this.rangeDates[1]){
      const differenceInTime = this.rangeDates[1].getTime() - this.rangeDates[0].getTime();
      return Math.round(differenceInTime / (1000 * 3600 * 24))+1;
    }
    return this.range;
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
    this.setCalendar();
  }
  todayDate() {
    this.range = 1;
    if (this.rangeDates[0] && this.rangeDates[1]) {
      this.endDateString = this.dateUtils.formatDateToString(this.today);
      this.rangeDates = [this.rangeDates[0], this.today];
    } else this.rangeDates = [this.today,this.today];
    this.setCalendar();
  }

  weekDate() {
    this.range = 7;
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(this.today.getDate() - 6);
    this.rangeDates = [oneWeekAgo, this.today];
    this.setCalendar();
  }
  twoWeekDate() {
    this.range = 14;
    const twoWeekAgo = new Date();
    twoWeekAgo.setDate(this.today.getDate() - 14);
    this.rangeDates = [twoWeekAgo, this.today];
    this.setCalendar();
  }

  monthDate() {
    this.range = 30;
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(this.today.getMonth() - 1);
    this.rangeDates = [oneMonthAgo, this.today];
    this.setCalendar();
  }

  minus4daysDate() {
    this.range = 4;
    this.previousDate();
    this.setCalendar();
  }

  nextDate() {
    const newFirst = new Date(this.rangeDates[0]);
    const newLast = new Date(this.rangeDates[1]);
      console.log("newFirst",newFirst);
      console.log("newLast",newLast);
    newFirst.setDate(newFirst.getDate() + this.range);
    newLast.setDate(newLast.getDate() + this.range);
    this.rangeDates = [newFirst, newLast];
    this.setCalendar();
  }

  previousDate() {
    const newFirst = new Date(this.rangeDates[0]);
    console.log(this.rangeDates[0]);

    const newLast = new Date(this.rangeDates[1]);
      console.log("newFirst",newFirst);
      console.log("newLast",newLast);

    newFirst.setDate(newFirst.getDate() - this.range);
    newLast.setDate(newLast.getDate() - this.range);
    this.rangeDates = [newFirst, newLast];
    console.log(this.rangeDates);

    this.setCalendar();
  }

  clearDate() {
    this.rangeDates = [];
    this.setCalendar();
  }

  formatNumber(item: any) {
    let value = (item.payed*100) / (item.retour+item.payed)
      if (!isNaN(value)) {
        return value.toFixed(2);
      }
      return '0.00';
  }
}

