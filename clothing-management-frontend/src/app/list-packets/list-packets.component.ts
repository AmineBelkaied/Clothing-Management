import { AfterViewChecked, AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ConfirmationService, FilterService, MessageService, SelectItem, SelectItemGroup } from 'primeng/api';
import { Packet } from '../../shared/models/Packet';
import { OfferService } from '../services/offer.service';
import { PacketService } from '../services/packet.service';
import * as FileSaver from 'file-saver';
import { DatePipe } from '@angular/common';
import { Table } from 'primeng/table';
import { CityService } from '../services/city.service';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from '../services/fb-page.service';
import { filter, map, Subject, takeUntil, tap } from 'rxjs';
var jsPDF: any; // Important

@Component({
  selector: 'app-list-packets',
  templateUrl: './list-packets.component.html',
  styleUrls: ['./list-packets.component.css'],
  providers: [DatePipe]
})
export class ListPacketsComponent implements OnInit, AfterViewChecked, OnDestroy {

  packets: Packet[] = []
  packet: Packet = {
    id: "",
    date: new Date(),
    customerName: "",
    customerPhoneNb: "",
    city: null,
    address: "",
    relatedProducts: "",
    packetReference: "",
    price: 0,
    status: "NOTCONFIRMED"

  }
  @ViewChild('dt') private _table: Table | undefined;
  packetsClone: Packet[] = [];
  cols: any[] = [];
  confirmation: any[] = [];
  statusList: any[] = [];
  selectedStatus: any[] = [];
  selectedPackets: Packet[] = [];
  exportColumns: any[] = [];
  rangeDates: Date[] = [];
  editMode = false;
  modelDialog!: boolean;
  submitted!: boolean;
  packetTest = {
    "address": "sousse"
  }

  first= 0;
  rows = 100;
  oldField: any;
  offersList: any[] = [];
  groupedCities: SelectItemGroup[] = [];
  fbPages: FbPage[] = [];
  selectedCity: any;
  clonedProducts: { [s: string]: Packet; } = {};
  subcriber = new Subject<void>();
  packetsByDate: Packet[] = [];
  @ViewChild('dt') dt?: Table;
  @ViewChild('calendar')
  calendar: any;
  constructor(private messageService: MessageService, private packetService: PacketService,
    private confirmationService: ConfirmationService, private offerService: OfferService, private cityService: CityService,
    private fbPageService: FbPageService, private filterService: FilterService, public datePipe: DatePipe, private cdRef: ChangeDetectorRef) {
    this.confirmation = [
      { name: 'Tous', code: 'all', inactive: false },
      { name: 'Confirmé', code: true, inactive: false },
      { name: 'Non Confirmé', code: false, inactive: false },
    ];
    this.statusList = ['Non confirmée','Confirmée','En cours (1)','En cours (2)','En cours (3)','Livrée','Payée','Retour','Annulée','Injoignable','Echange'];
  }

  ngAfterViewChecked() {
    this.cdRef.detectChanges();
  }

  ngOnInit(): void {
    this.packetService.findAllTodaysPackets().subscribe((allPackets: any) => {
     this.packets = allPackets;
     console.log(this.packets);
     
     this.packetsByDate = this.packets.slice()
    });

    this.cols = [
      { field: 'id', header: 'Id' },
      { field: 'date', header: 'Date' },
      { field: 'fbPage', header: 'PageFB'},
      { field: 'customerName', header: 'Client', customExportHeader: 'Product Code' },
      { field: 'customerPhoneNb', header: 'Téléphone' },
      { field: 'city', header: 'Ville' },
      { field: 'address', header: 'Adresse' },
      { field: 'relatedProducts', header: 'Articles' },
      { field: 'price', header: 'Prix' },
      { field: 'status', header: 'Statut' }
    ];

    this.exportColumns = this.cols.map(col => ({ title: col.header, dataKey: col.field }));
    this.offerService.findAllOffers()
      .subscribe((offers: any) => {
        this.offersList = offers;
      })
    this.cityService.findAllGroupedCities()
      .subscribe((groupedCities: any) => {
        console.log(groupedCities);

        this.groupedCities = this.cityService.adaptListToDropDown(groupedCities);
        console.log(JSON.stringify(groupedCities))
      })

    this.fbPageService.findAllFbPages()
    .subscribe((result: any) => {
      this.fbPages = result;
    })

    this.rangeDates[0] = new Date();
  }

  onEditInit(packet: any) {
    this.oldField = packet.data[packet.field];
  }

  onEditComplete(packet: any) {
    if (this.oldField !== packet.data[packet.field]) {
      if ((packet.field == 'city') || (packet.field == 'fbPage')) {
        this.packetService.updatePacket(packet.data)
          .subscribe((response: any) => {
            console.log(response);
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'عملية ناجحة' });
          });
      } else {
        let updatedField = { [packet.field]: packet.data[packet.field] }
        console.log(updatedField)
        this.packetService.patchPacket(packet['data'].id, updatedField)
          .subscribe((response: any) => {
            console.log(response);
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'عملية ناجحة' });
          });
      }
    }
  }

  newPacket(): Packet {
    return {
      date: this.getDate(new Date()),
      customerName: "",
      customerPhoneNb: "",
      address: "",
      relatedProducts: "",
      packetReference: "",
      price: 0,
      status: "Non confirmée"
    }
  }

  addNewRow() {
    // Insert a new row
    // Set the new row in edit mode
    this.packetService.addPacket(this.newPacket())
      .subscribe((response: any) => {
        console.log(response);
        
        this.packets.unshift(response);
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est ajoutée avec succés', life: 1000 });
      });
  }

  updateConfirmation(idPacket: any, packet: any) {
    let updatedField = { "confirmation": packet.checked }
    this.packetService.patchPacket(idPacket, updatedField)
      .subscribe((response: any) => {
        console.log(response);
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'عملية ناجحة', life: 1000 });
      });
  }

  deletePacket(packet: any) {
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer cette commande ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.packetService.deletePacketById(packet.id)
          .subscribe((response) => {
            console.log(response);
            this.packets = this.packets.filter(p => p.id != packet.id).slice();
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande a été supprimée avec succés', life: 1000 });
          })
      }
    });
  }

  deleteSelectedPackets() {
    let selectedPacketsId = this.selectedPackets.map((selectedPacket: Packet) => selectedPacket.id);
    console.log(selectedPacketsId);

    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer les commandes séléctionnées ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.packetService.deleteSelectedPackets(selectedPacketsId)
          .subscribe(result => {
            console.log("packets successfully deleted !");
            this.packets = this.packets.filter((packet: Packet) => selectedPacketsId.indexOf(packet.id) == -1);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Les commandes séléctionnées ont été supprimé avec succés', life: 1000 });
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
    packet.relatedProducts = $event.packet.productsRef.join(" , ");
    //packet.packetReference = $event.packet.packetReference;
    this.packets.splice(pos, 1, packet);
    if (!this.editMode)
      this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été ajoutés avec succés', life: 1000 });
    else
      this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été mis à jour avec succés', life: 1000 });
  }

  onChangeEndDate($event: any) {
    // get the from/start value
    // get the from/start value
    let startDate = this.rangeDates[0]
    let endDate: any;
    // the to/end value might not be set
    // use the from/start date and add 1 day
    // or the to/end date and add 1 day
    if (this.rangeDates[1]) {
      endDate = this.rangeDates[1];
    } else {
      endDate = startDate
    }
    this.packets = this.packetService.allPackets.filter((packet: any) => this.getDate(packet.date) >= this.getDate(startDate) && this.getDate(packet.date) <= this.getDate(endDate))
    // save packetsByDate state
    this.packetsByDate = [...this.packets]//console.log(this.packets);
  }

  selectStatus(dt: Table, event: any) {
    console.log(event);
    if(this.rangeDates[0] != null) {
      if (event.value.length === 0) 
      this.packets = this.packetsByDate.slice();
    else
      this.packets = this.packetsByDate.filter((packet: Packet) => event.value.indexOf(packet.status) > -1);
    } else {
      if (event.value.length === 0) 
      this.packets = this.packetService.allPackets.slice();
    else
      this.packets = this.packetService.allPackets.filter((packet: Packet) => event.value.indexOf(packet.status) > -1);
    }
  }

  clearStatus() {
    this.packets = this.packetsByDate;
  }

  changeDate(event: any) {
    console.log(event)
  }

  resetTable() {
    this.rangeDates = [];
    this.packets = this.packetService.allPackets;
  }

  changeColor(this: any) {
    this.style.color = "red";
  }

  exportExcel() {
    let packets = this.packets
      //.filter(packet => packet.confirmation)
      .map((packet: any) => packet = {
        "Id" : packet.id,
        "prix": packet.price,
        "Références" : packet.relatedProducts,
        "PageFB": packet.fbPage?.name
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
    FileSaver.saveAs(data, fileName + " - " + this.transformDate(new Date()) + EXCEL_EXTENSION);
  }

  exportCSV() {
    // map the packets to a customer packetList
    let packets = this.packets
    .map((packet: any) => packet = {
      "destinataire_nom": this.getValue(packet.customerName),
      "adresse": this.getValue(packet.address),
      "ville": this.getValue(packet.city?.name),
      "gouvernorat": this.getValue(packet.city?.governorate.name),
      "telephone": this.getValue(packet.customerPhoneNb),
      "telephone2": '',
      "nombre_de_colis": 1,
      "prix": this.getValue(packet.price),
      "designation": this.getValue(packet.id) + " " + this.getValue(packet.fbPage?.name) + " " + this.getValue(packet.relatedProducts),
      "commentaire": "Le colis peut etre ouvert lors de la commande du client"
    });

    // prepare the columns to be exported
    let cols: any[] = [
      { field: 'destinataire_nom', header: 'destinataire_nom'},
      { field: 'adresse', header: 'adresse' },
      { field: 'ville', header: 'ville'},
      { field: 'gouvernorat', header: 'gouvernorat'},
      { field: 'telephone', header: 'telephone' },
      { field: 'telephone2', header: 'telephone2' },
      { field: 'nombre_de_colis', header: 'nombre_de_colis'},
      { field: 'prix', header: 'prix' },
      { field: 'designation', header: 'designation' },
      { field: 'commentaire', header: 'commentaire' }
    ];
    let csv = '';
    let csvSeparator = ';';
    //headers
    for (let i = 0; i < cols.length; i++) {
        if (cols[i].field) {
            csv += cols[i].field;
  
            if (i < (cols.length - 1)) {
                csv += csvSeparator;
            }
        }
    }
    //body        
    packets.forEach((record: any, j) => {
        csv += '\n';
        for (let i = 0; i < cols.length; i++) {
            if (cols[i].field) {
                console.log(record[cols[i].field]);
                // resolveFieldData seems to check if field is nested e.g. data.something --> probably not needed
                csv += record[cols[i].field]; //this.resolveFieldData(record, this.columns[i].field);
                if (i < (cols.length - 1)) {
                    csv += csvSeparator;
                }
            }
        }
    });
    this.download(csv, 'first - ' + this.transformDate(new Date()));
  }
  
  download(text: any, filename: any) {
    let element = document.createElement('a');
    element.setAttribute('href', 'data:text/csv;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);
  
    element.style.display = 'none';
    document.body.appendChild(element);
  
    element.click();
  
    document.body.removeChild(element);
  }
  
  getValue(fieldName: any) {
    return (fieldName != null && fieldName!= undefined && fieldName != NaN) ? fieldName : '';
  }

  exportPdf() {
    const doc = new jsPDF();
    doc.autoTable(this.exportColumns, this.packets);
    doc.save('products.pdf');
  }

  transformDate(date: any) {
    return this.datePipe.transform(date, 'dd/MM/yyyy');
  }

  trackByFunction = (index: any, item: { id: any; }) => {
    return item.id // O index
  }

  getDate(date: Date) : any {
    return this.datePipe.transform(date, 'yyyy-MM-dd');
  }

  ngOnDestroy() {
    this.subcriber.next();
    this.subcriber.complete();
  }

  /*   transformAddress(text: any, nbr: number) {
    let newText = "";
    if(text.length > nbr) {
      while(text.length > nbr){
        let substring = text.substring(0 , nbr);
        newText += substring + "<br>";
        text = text.replace(substring , "");
    }
    return newText;
    }
    console.log(text);
    
  return text;
  } */
  
/*  transformProducts(packetReference: string) {
    let refsArray = packetReference.split("-");
    let displayedProducts = "";
    if(refsArray.length > 0)
    for(var i=0; i < refsArray.length ; i++) {
      displayedProducts += refsArray[i].split(":")[1].split(',').join(' - ') + "<br>";
    }
    return displayedProducts;
  } */

}
