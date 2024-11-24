import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';
import { Subject, takeUntil } from 'rxjs';
import { DeliveryCompanyService } from 'src/shared/services/delivery-company.service';

@Component({
  selector: 'app-states-stat',
  templateUrl: './states-stat.component.html',
  styleUrls: ['./states-stat.component.scss'],
  providers: [DatePipe],
})

export class StatesStatComponent implements OnInit,OnChanges {
  @Input() beginDateString: string;
  @Input() endDateString: string | null;
  StatesData: any;
  StatesOptions: any;
  dates: any[];
  $unsubscribe: Subject<void> = new Subject();

  constructor(
    private statsService: StatsService,
    public datePipe: DatePipe,
    private deliveryCompanyService: DeliveryCompanyService,
  ) {}

  ngOnInit() {
    this.intitiateLists();
  }
  ngOnChanges(simpleChanges: SimpleChanges): void {
    if(simpleChanges['endDateString'] && this.endDateString){
          this.getStatesChart();
      }
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
    const paid: number[] = Object.values(data).flatMap(
      (obj : any) => obj.countPaid
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
          data: paid,
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

  calculateAverage(numbers: number[]): number {
    if (numbers.length === 0) {
      return 0; // Handle division by zero
    }
    const sum = numbers.reduce((acc, current) => acc + current, 0);
    const average = sum / numbers.length;
    return Number(average.toFixed(1));
  }


}
