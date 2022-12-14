import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { ConfirmationService, FilterService, MessageService, SelectItem } from 'primeng/api';
import { Packet } from '../../shared/models/Packet';
import { OfferService } from '../services/offer.service';
import { PacketService } from '../services/packet.service';
import * as FileSaver from 'file-saver';
import { DatePipe } from '@angular/common';
import { Table } from 'primeng/table';
var jsPDF: any; // Important

@Component({
  selector: 'app-list-packets',
  templateUrl: './list-packets.component.html',
  styleUrls: ['./list-packets.component.css'],
  providers: [DatePipe]
})
export class ListPacketsComponent implements OnInit {

  packets: Packet[] = [
    /*     {
          id: "1",
          date: new Date(),
          customerName: "ahmed",
          customerPhoneNb: "200000",
          governorate: "nabeul",
          address: "hamm",
          articles: "2p",
          price: 0,
          confirmation: false
        },
        {
          id: "2",
          date: new Date(),
          customerName: "amine",
          customerPhoneNb: "50000",
          governorate: "nabeul",
          address: "wxcwxcw",
          articles: "2p55",
          price: 0,
          confirmation: false
        } */
  ]
  packet: Packet = {
    id: "",
    date: new Date(),
    customerName: "",
    customerPhoneNb: "",
    governorate: "",
    address: "",
    relatedProducts: "",
    packetReference: "",
    price: 0,
    confirmation: false
  }
  @ViewChild('dt') private _table: Table | undefined;
  packetsClone: Packet[] = [];
  cols: any[] = [];
  confirmation: any[] = [];
  value: any;
  selectedPackets: Packet[] = [];
  exportColumns: any[] = [];
  rangeDates: Date[] = [];
  editMode = false;

  modelDialog!: boolean;
  submitted!: boolean;
  packetTest = {
    "address": "sousse"
  }

  oldField: any;
  offersList: any[] = [];
  clonedProducts: { [s: string]: Packet; } = {};
  constructor(private messageService: MessageService, private packetService: PacketService,
    private confirmationService: ConfirmationService, private offerService: OfferService,
    private filterService: FilterService, public datepipe: DatePipe) {
    this.confirmation = [
      { name: 'Tous', code: 'all', inactive: false },
      { name: 'Confirm??', code: true, inactive: false },
      { name: 'Non Confirm??', code: false, inactive: false },
    ];
  }

  ngOnInit(): void {

    let yesterDay = new Date();
    yesterDay.setDate(yesterDay.getDate() - 1);
    let todayPackets = true;
    this.packetService.findAllPackets()
      .subscribe((packetsList: any) => {
        this.packetsClone = Object.assign([], packetsList);
        this.packets = this.packetsClone.filter((packet: Packet) => this.transformDate(packet.date) == this.transformDate(new Date()));
        if (this.packets.length == 0) {
          this.packets = this.packetsClone.filter((packet: Packet) => this.transformDate(packet.date) == this.transformDate(yesterDay));
          this.rangeDates[0] = yesterDay;
          todayPackets = false;
        }
          
      })
    this.cols = [
      { field: 'id', header: 'Id' },
      { field: 'date', header: 'Date' },
      { field: 'customerName', header: 'Client', customExportHeader: 'Product Code' },
      { field: 'customerPhoneNb', header: 'T??l??phone' },
      { field: 'governorate', header: 'Gouvernorat' },
      { field: 'address', header: 'Quantity' },
      { field: 'relatedProducts', header: 'Articles' },
      { field: 'price', header: 'Prix' },
      { field: 'confirmation', header: 'Confirm??' }
    ];

    this.exportColumns = this.cols.map(col => ({ title: col.header, dataKey: col.field }));
    this.offerService.findAllOffers()
      .subscribe((offers: any) => {
        this.offersList = offers;
      })

    this.rangeDates[0] = todayPackets ? new Date() : yesterDay;
    // filter by range date
    this.filterService.register('filterDate', (value: any, filter: any): any => {

      this.packets = this.packetsClone.slice();
      if (this.rangeDates === undefined || this.rangeDates === null)
        return true;

      if (filter === undefined || filter === null) {
        return true;
      }

      if (value === undefined || value === null) {
        return false;
      }
      // get the from/start value
      let startDate = this.rangeDates[0].getTime();
      let endDate;
      // the to/end value might not be set
      // use the from/start date and add 1 day
      // or the to/end date and add 1 day
      if (this.rangeDates[1]) {
        endDate = this.rangeDates[1].getTime() + 86400000;
      } else {
        endDate = startDate + 86400000;
      }
      // compare it to the actual values
      return new Date(value).getTime() >= startDate && new Date(value).getTime() <= endDate;
    });
  }

  onEditInit(packet: any) {
    this.oldField = packet.data[packet.field];
  }

  onEditComplete(packet: any) {
    /* const oldPacket = this.packets.filter(p => p.id == packet['data'].id)
     let check =JSON.stringify(oldPacket[0]) === JSON.stringify(packet.data)*/
    console.log(this.oldField)
    console.log(packet.data[packet.field])
    if (this.oldField !== packet.data[packet.field]) {
      console.log(packet)
      let updatedField = { [packet.field]: packet.data[packet.field] }
      console.log(updatedField)
      this.packetService.patchPacket(packet['data'].id, updatedField)
        .subscribe((response: any) => {
          console.log(response);
          this.messageService.add({ severity: 'success', summary: 'Success', detail: '?????????? ??????????' });
        });
    }
  }

  onEditCancel(packet: any) {
    console.log("cancell")
    console.log(packet)
    //this.messageService.add({severity:'success', summary: 'Success', detail:'Product is updated'});
  }

  newPacket(): Packet {
    return {
      id: "",
      date: new Date(),
      customerName: "",
      customerPhoneNb: "",
      governorate: "",
      address: "",
      relatedProducts: "",
      packetReference: "",
      price: 0,
      confirmation: false
    }
  }

  addNewRow() {
    // Insert a new row
    // Set the new row in edit mode
    this.packetService.addPacket(this.newPacket())
      .subscribe((response: any) => {
        this.packets.push(response);
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est ajout??e avec succ??s', life: 1000 });
      });
  }

  updateConfirmation(idPacket: any, packet: any) {
    let updatedField = { "confirmation": packet.checked }
    this.packetService.patchPacket(idPacket, updatedField)
      .subscribe((response: any) => {
        console.log(response);
        this.messageService.add({ severity: 'success', summary: 'Success', detail: '?????????? ??????????', life: 1000 });
      });
  }

  deletePacket(packet: any) {
    this.confirmationService.confirm({
      message: 'Etes vous s??re de vouloir supprimer cette commande ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.packetService.deletePacketById(packet.id)
          .subscribe((response) => {
            console.log(response);
            this.packets = this.packets.filter(p => p.id != packet.id).slice();
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande a ??t?? supprim??e avec succ??s', life: 1000 });
          })
      }
    });
  }

  deleteSelectedPackets() {
    let selectedPacketsId = this.selectedPackets.map((selectedPacket: Packet) => selectedPacket.id);
    console.log(selectedPacketsId);
    
    this.confirmationService.confirm({
      message: 'Etes vous sure de vouloir supprimer les commandes s??l??ctionn??s ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.packetService.deleteSelectedPackets(selectedPacketsId)
        .subscribe(result => {
          console.log("packets successfully deleted !");
          this.packets = this.packets.filter((packet: Packet) => selectedPacketsId.indexOf(packet.id) == -1);
          this.messageService.add({ severity: 'success', summary: 'Succ??s', detail: 'Les commandes s??l??ctionn??es ont ??t?? supprim?? avec succ??s', life: 1000 });
        })
      }
    });
  }


  openNew(packet: Packet) {
    this.packet = Object.assign({}, packet);
    this.submitted = false;
    this.modelDialog = true;
    this.editMode = false;
  }

  editProducts(packet: Packet) {
    this.packet = Object.assign({}, packet);
    console.log("edit pproduct");
    console.log(packet);
    this.submitted = false;
    this.modelDialog = true;
    this.editMode = true;
  }

  hideDialog() {
    this.modelDialog = false;
    this.submitted = false;
  }

  OnSubmit($event: any) {
    this.modelDialog = $event.modelDialog;
    let packet = this.packets.filter(p => p.id == $event.packet.idPacket)[0];
    let pos = this.packets.indexOf(packet);
    console.log(packet);
    packet.relatedProducts = $event.packet.productsRef;
    //packet.packetReference = $event.packet.packetReference;
    packet.price = $event.packet.price;
    this.packets.splice(pos, 1, packet);
    if (!this.editMode)
      this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont ??t?? ajout??s avec succ??s', life: 1000 });
    else
      this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont ??t?? mis ?? jour avec succ??e', life: 1000 });
  }

  changeColor(this: any) {
    this.style.color = "red";
  }

  exportPdf() {
    const doc = new jsPDF();
    doc.autoTable(this.exportColumns, this.packets);
    doc.save('products.pdf');
  }

  exportExcel() {
    let packets = this.packets
      //.filter(packet => packet.confirmation)
      .map((packet: any) => packet = {
        "Id": packet.id,
        "Date": packet.date,
        "Nom client": packet.customerName,
        "T??l client": packet.customerPhoneNb,
        "Gouvernorat": packet.governorate,
        "Adresse": packet.address,
        "Articles": packet.relatedProducts,
        "Description": packet.packetReference,
        "Prix": packet.price
      });
    import("xlsx").then(xlsx => {
      const worksheet = xlsx.utils.json_to_sheet(packets);
      const workbook = { Sheets: { 'data': worksheet }, SheetNames: ['data'] };
      const excelBuffer: any = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });
      this.saveAsExcelFile(excelBuffer, "products");
    });
  }

  saveAsExcelFile(buffer: any, fileName: string): void {
    let EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
    let EXCEL_EXTENSION = '.xlsx';
    const data: Blob = new Blob([buffer], {
      type: EXCEL_TYPE
    });
    FileSaver.saveAs(data, fileName + '_export_' + new Date().getTime() + EXCEL_EXTENSION);
  }

  changeOption(event: any) {
    if (event.option.code === 'all')
      this.packets = this.packetsClone.slice();
    else
      this.packets = this.packetsClone.filter((packet: Packet) => packet.confirmation == event.option.code)
  }

  changeDate(event: any) {
    console.log(event)
  }

  resetTable(dt: Table) {
    this.packets = this.packetsClone.slice();
    dt.clear();
    console.log(dt.value);
    console.log(this.packets);
  }

  search(dt: any, event: any) {
    this.packets = this.packetsClone.slice();
    console.log(event.target.value);
    dt.filterGlobal(event.target.value, 'contains')
  }


  transformDate(date: any) {
    let transformDate: any = this.datepipe.transform(date, 'dd/MM/yyyy');
    return new Date(transformDate).toDateString()
  }

}
