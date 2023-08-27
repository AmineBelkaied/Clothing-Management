import { Component, OnInit, ViewChild } from '@angular/core';
import { DaySales, Colors, Model } from 'src/shared/models/stat';
import { PacketService } from 'src/shared/services/packet.service';
import { Packet } from 'src/shared/models/Packet';
import { DatePipe } from '@angular/common';
import { StatsService } from 'src/shared/services/stats.service';

@Component({
  selector: 'app-statistique',
  templateUrl: './statistique.component.html',
  styleUrls: ['./statistique.component.scss'],
  providers: [DatePipe],
})
export class StatistiqueComponent implements OnInit {
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
  models: string[] = [];
  citys: number[][] = [];
  listPacket: Packet[] = [];
  //stat chart
  basicData: any;
  basicOptions: any;
  //tree table

  //packet by date
  rangeDates: Date[] = [];
  packetsByDate: Packet[] = [];
  //end packet by date

  cityCounts: CountCitys = {};
  pagesCounts: CountPages = {};
  datesCounts: CountDates = {};

  constructor(
    private packetService: PacketService,
    private statsService: StatsService,
    public datePipe: DatePipe
  ) {
    this.packetService.allPacketsReady$.subscribe(
      {next:() => {
      this.listPacket = this.packetService.allPackets;
      }
    });
  }

  StatesData: any;
  StatesOptions: any;

  DatesData: any;
  DatesOptions: any;

  PagesData: any;
  PagesOptions: any;

  ngOnInit() {
    this.rangeDates[0] = new Date();
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue(
      '--text-color-secondary'
    );
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');
      let counts = this.statsService.getStatsTreeNodesData(this.listPacket);
    this.cityCounts = counts.cityCounts;
    //console.table(JSON.stringify(this.cityCounts));
    this.createCityStatChart(this.cityCounts);
    //this.getData();//get data from stat.js

    this.pagesCounts = counts.pageCounts;
    //console.table(JSON.stringify(this.cityCounts));
    this.createPageStatChart(this.pagesCounts);

    this.datesCounts = counts.dateCounts;
    //console.table(JSON.stringify(this.cityCounts));
    this.createDateStatChart(this.datesCounts);

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
      plugins: {
        legend: {
          labels: {
            usePointStyle: true,
            color: textColor,
          },
        },
      },
    };
    this.DatesOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.6,
      plugins: {
          legend: {
              labels: {
                  color: textColor
              }
          }
      },
      scales: {
          x: {
              ticks: {
                  color: textColorSecondary
              },
              grid: {
                  color: surfaceBorder,
                  drawBorder: false
              }
          },
          y: {
              ticks: {
                  color: textColorSecondary
              },
              grid: {
                  color: surfaceBorder,
                  drawBorder: false
              }
          }
      }
    };
  }

  createDateStatChart(dataCount: CountDates){
    const datesData: number[] = Object.values(dataCount).flatMap(
      (obj) => obj.count
    );
    const datesPayed: number[] = Object.values(dataCount).flatMap(
      (obj) => obj.payed
    );
    const datesOut: number[] = Object.values(dataCount).flatMap(
      (obj) => obj.out
    );
    const pagesLabel: string[] = Object.keys(dataCount);
    this.DatesData = {
      labels: pagesLabel,
      datasets: [
          {
              label: 'All',
              data: datesData,
              fill: false,
              borderColor: 'blue',
              tension: 0.4
          },
          {
            label: 'Payées',
            data: datesPayed,
            fill: false,
            borderColor: 'pink',
            tension: 0.4
        },
        {
          label: 'Sortie',
          data: datesOut,
          fill: false,
          borderColor: 'red',
          tension: 0.4
      }
      ]
  };
  }

  createPageStatChart(dataCount: CountPages){
    const pagesData: number[] = Object.values(dataCount).flatMap(
      (obj) => obj.count
    );
    const pagesLabel: string[] = Object.keys(dataCount);
    this.PagesData = {
      labels: pagesLabel,
      datasets: [
        {
          data: pagesData,
          backgroundColor: [
            'blue','green','red','grey','yellow','pink'
          ],
          hoverBackgroundColor: [
            'blue','green','red','grey','yellow','pink'
          ],
        },
      ],
    };
  }
  createCityStatChart(dataCount: CountCitys) {
    console.log('createCityStatChart');
    const confirmed: number[] = Object.values(dataCount).flatMap(
      (obj) => obj.confirm
    );
    const totCmd: number[] = Object.values(dataCount).flatMap(
      (obj) => obj.count - obj.confirm
    );
    const label: string[] = Object.keys(dataCount);
/*     console.log('label:', label);
    console.log('confirmed', confirmed);
    console.log('tot:', totCmd); */
    this.StatesData = {
      labels: label,
      datasets: [
        {
          type: 'bar',
          label: 'Payée',
          backgroundColor: 'blue',
          data: confirmed,
        },
        {
          type: 'bar',
          label: 'nonPayer',
          backgroundColor: 'green',
          data: totCmd,
        },
      ],
    };
  }
  onChangeEndDate($event: any) {
    // get the from/start value
    let startDate = this.rangeDates[0];
    let endDate: any;
    // the to/end value might not be set
    // use the from/start date and add 1 day
    // or the to/end date and add 1 day
    if (this.rangeDates[1]) {
      endDate = this.rangeDates[1];
    } else {
      endDate = startDate;
    }
    //console.log('start', startDate);
    //console.log('this.filteredBydatePackets',this.filterBydatePackets(startDate, endDate));
    let filtredCitysCount = this.statsService.getStatsTreeNodesData(
      this.filterBydatePackets(startDate, endDate)
    );
    //console.log('filtredDataCount',filtredDataCount)
    this.createCityStatChart(filtredCitysCount.cityCounts);
    this.createPageStatChart(filtredCitysCount.pageCounts);
    this.createDateStatChart(filtredCitysCount.dateCounts);
  }

  filterBydatePackets(startDate: Date, endDate: Date) {
    //console.log('p3000', this.listPacket[3000].date);

    let filterBydatePackets = this.listPacket.filter((packet) => {
      const packetDate = this.getDate(packet.date);
      //return dob >= startDate && dob <= endDate;
      return (
        packetDate >= this.getDate(startDate) &&
        packetDate <= this.getDate(endDate)
      );
    });
    return filterBydatePackets;
  }

  getDate(date: Date): any {
    return this.datePipe.transform(date, 'yyyy-MM-dd');
  }

  resetTable() {
    this.rangeDates = [];
    this.packets = this.packetService.allPackets.slice();
  }

  /*     getData() {
      this.statService.getStat().then((data) => {
        this.sales = data;
        console.log('this.sales', this.sales);
        this.selectedModel = this.sales[0].model[0].modelName;
        this.createStat();
      });
    } */
  /*
    createStat() {
      console.log('createStat');
      this.sales.forEach((sale) => {
        //console.log(element);
        sale.model.forEach((model) => {
          if (this.models.indexOf(model.modelName) < 0) {
            this.models.push(model.modelName);
          }
        });
        let row = this.createRow(sale, this.selectedModel);
        if (row) this.statTab.push(row);
      });
    }

    createRow(daySales: DaySales, modelName: string) {
      let model1 = daySales.model.filter((obj) => {
        return obj.modelName == modelName;
      });
      if (model1.length > 0) {
        //console.log('create row', daySales.day);
        let totalPerDay = 0;
        this.rowByDate = [];
        //compteur pour les colonnes
        let i = 0;
        this.colors = [];
        this.sizesRow = [];
        this.rowByDate.push(daySales.day);
        //parcourir les couleurs du model
        for (let color of model1[0].modelColors) {
          //ajouter la couleur a la liste des couleurs
          this.colors.push(color.colorName);
          //compteur des sizes, mise a zero chaque couleur
          let j = 0;
          //console.log('this.colors', this.colors,"j",j);
          //variable qui calcule la somme des qte vendue par couleur
          let totalPerDayColor = 0;
          //parcourir les tailles d'un couleur
          for (let size of color.sizes) {
            //ajoute la taille a la liste des tailles a afficher
            if (this.qtePerSizeColumn) this.sizesRow.push(size.sizeName);
            //calcule le nombre de vente par jour
            if (this.totalPerDayColumn) totalPerDay += size.qte;
            //calcule le nombre de vente par jour par couleur
            if (this.totalPerDayColorColumn) totalPerDayColor += size.qte;
            //calcule la somme des colonnes
            if (this.qtePerSizeColumn) {
              this.rowByDate.push(size.qte);
              if (this.totalPerSize[i] == undefined) this.totalPerSize[i] = 0;
              this.totalPerSize[i] = this.totalPerSize[i] + size.qte;
              i++;
            }
            if (this.totalPerSizeRow[j] == undefined) this.totalPerSizeRow[j] = 0;
            this.totalPerSizeRow[j] += size.qte;
            j++;
            if (!this.sizesInisialized) {
              this.sizes.push(size.sizeName);
              //console.log('this.sizesRow', this.sizesRow, 'i', i, 'j', j);
            }
          }
          this.sizesInisialized = true;
          //calcule la somme des article vendues par couleur chaque jour
          if (this.totalPerDayColorColumn) {
            this.sizesRow.push('total/jour ' + color.colorName);
            this.rowByDate.push(totalPerDayColor);
            //console.log('i:',i,' totalpercolorTot:',totalPerDayColor)
            if (this.totalPerSize[i] == undefined) this.totalPerSize[i] = 0;
            this.totalPerSize[i] += totalPerDayColor;
            i++;
          }
        }

        //calcule la somme des article vendues par jour
        if (this.totalPerDayColumn) {
          this.rowByDate.push(totalPerDay);
          if (this.totalPerSize[i] == undefined) this.totalPerSize[i] = 0;
          this.totalPerSize[i] += totalPerDay;
          i++;
        }

        //console.log('total', this.totalPerSize);
        return this.rowByDate;
      }
      return;
    }

    handleChange() {
      if (this.totalPerDayColorColumn) this.columnPerColor = 8;
      else this.columnPerColor = 7;
      this.totalPerSize = [];
      this.statTab = [];
      this.totalPerSizeRow = [];
      this.createStat();
    }
    */
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
    out:number;
  };
}
