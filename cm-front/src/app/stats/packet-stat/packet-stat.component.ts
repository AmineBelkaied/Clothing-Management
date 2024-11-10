import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';
import { Subject, takeUntil } from 'rxjs';
import { DeliveryCompanyService } from 'src/shared/services/delivery-company.service';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';

@Component({
  selector: 'app-packet-stat',
  templateUrl: './packet-stat.component.html',
  styleUrls: ['./packet-stat.component.scss'],
  providers: [DatePipe],
})

export class PacketStatComponent implements OnInit,OnChanges {
  deliveryCompanyList: DeliveryCompany[] = [];
  @Input() deliveryCompanyName : string = "ALL";
  @Input() beginDateString: string;
  @Input() endDateString: string | null;
  packetsDataSetArray: any[];
  packetsOptions: any;
  packetsData: any;
  packetsTableData: any;
  dates: any[];
  $unsubscribe: Subject<void> = new Subject();

  constructor(
    private statsService: StatsService,
    public datePipe: DatePipe
  ) {}

  ngOnInit() {
    this.intitiateLists();

  }

  ngOnChanges(simpleChanges: SimpleChanges): void {
    if((simpleChanges['endDateString'] || simpleChanges['deliveryCompanyName']) && this.endDateString){
      this.getStatAllPacketsChart();
    }
  }

  intitiateLists() {

    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue(
      '--text-color-secondary'
    );
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

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
    this.packetsData = {
      labels: [],
      datasets: [],
    };
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
      }
  }

  createPacketsChart(data: any) {
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
          label: 'Livrée/av:' + this.calculateAverage(statusCounts[2]),
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

  calculateAverage(numbers: number[]): number {
    if (numbers.length === 0) {
      return 0; // Handle division by zero
    }
    const sum = numbers.reduce((acc, current) => acc + current, 0);
    const average = sum / numbers.length;
    return Number(average.toFixed(1));
  }
}
