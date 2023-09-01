import {
  AfterViewChecked,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  ConfirmationService,
  MessageService,
  SelectItemGroup,
} from 'primeng/api';
import { Packet } from '../../../shared/models/Packet';
import { OfferService } from '../../../shared/services/offer.service';
import { PacketService } from '../../../shared/services/packet.service';
import { DatePipe } from '@angular/common';
import { Table } from 'primeng/table';
import { CityService } from '../../../shared/services/city.service';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from '../../../shared/services/fb-page.service';
import {
  catchError,
  Observable,
  of,
  Subject,
  takeUntil,
} from 'rxjs';
import { Offer } from 'src/shared/models/Offer';
import { PrimeIcons } from 'primeng/api';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-list-packets',
  templateUrl: './list-packets.component.html',
  styleUrls: ['./list-packets.component.css'],
  providers: [DatePipe],
})
export class ListPacketsComponent
  implements OnInit, AfterViewChecked, OnDestroy {
  display: boolean = false;
  displayStatus: boolean = false;
  suiviHeader: string = 'Suivi';
  events: any[] = [];
  statusEvents: any[] = [];
  packets: Packet[] = [];
  private static FIRST = 'FIRST';
  packet: Packet = {
    id: '',
    date: new Date(),
    customerName: '',
    customerPhoneNb: '',
    city: { id: 0 },
    address: '',
    relatedProducts: '',
    packetReference: '',
    packetDescription: '',
    price: 0,
    status: 'Non Confirmé',
    barcode: '',
    lastDeliveryStatus: '',
    lastUpdateDate: '',
  };
  @ViewChild('dt') private _table: Table | undefined;
  packetsClone: Packet[] = [];
  cols: any[] = [];
  confirmation: any[] = [];
  statusList: any[] = [];
  selectedPackets: Packet[] = [];
  rangeDates: Date[] = [];
  startDate: Date = new Date();
  endDate: Date = new Date();
  editMode = false;
  countRows = 0;
  isLoading = false;
  selectedPacket?: string = '';
  today: Date = new Date();
  today_2: Date = new Date(Date.now() - 172800000);

  modelDialog!: boolean;
  submitted!: boolean;

  first = 0;
  rows = 100;
  oldField: any;
  offersList: any[] = [];
  groupedCities: SelectItemGroup[] = [];
  fbPages: FbPage[] = [];
  selectedCity: any;
  filter: any;
  clonedProducts: { [s: string]: Packet } = {};
  subcriber = new Subject<void>();

  selectedStatusList: any[] = [];
  statusListEtat0: any[] = [];
  statusListEtat1: any[] = [];
  statusListEtat2: any[] = [];
  statusListEtat3: any[] = [];
  statusListLivree: any[] = [];
  statusListRetour: any[] = [];
  statesList: any[] = [];
  selectedStatus: FormControl = new FormControl();
  selectedStates: any[] = [];
  $unsubscribe: Subject<void> = new Subject();

  @ViewChild('dt') dt?: Table;
  @ViewChild('calendar')
  calendar: any;
  reg = /,/gi;
  regBS = /\n/gi;
  constructor(
    private messageService: MessageService,
    private packetService: PacketService,
    private confirmationService: ConfirmationService,
    private offerService: OfferService,
    private cityService: CityService,
    private fbPageService: FbPageService,
    public datePipe: DatePipe,
    private cdRef: ChangeDetectorRef
  ) {
    this.statusList = [
      'Non confirmée',
      'Confirmée',
      'En rupture',
      'En cours (1)',
      'En cours (2)',
      'En cours (3)',
      'Livrée',
      'Payée',
      'Retour Expediteur',
      'A verifier',
      'Retour',
      'Retour Echange',
      'Retour reçu',
      'Supprimé',
    ];
    this.statusListEtat0 = [
      'Non confirmée',
      'Confirmée',
      'En rupture',
      'Annulée'
    ];
    this.statusListLivree = ['Payée'];
    this.statusListRetour = ['Retour reçu','Retour Expediteur'];
    this.statesList = [
      'Bureau',
      'En Cours',
      'Retour',
      'Terminé',
    ];
  }

  ngAfterViewChecked() {
    this.cdRef.detectChanges();
  }

  ngOnInit(): void {
    console.log("listPacket|ngOnInit");
    let params = {
       page : 0,
       size: 100,
       startDate : this.formatDateToString(new Date()),
       endDate : this.formatDateToString(new Date())
      };
    this.packetService.findAllPackets(params)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({   next:(response: any) => {
        console.log(response);

        this.packets = response.result;
        //this.filterChange('date');
      },
      error: (error: any) => {
        console.log('Error:', error);
      },
      complete: () => {
        console.log('Observable completed-- All Packets From Base --');
      }});

    this.cols = [
      //{ field: 'id', header: 'Id' },
      { field: 'date', header: 'Date' },
      { field: 'fbPage.name', header: 'PageFB' },
      {
        field: 'customerName',
        header: 'Client',
        customExportHeader: 'Product Code',
      },
      { field: 'customerPhoneNb', header: 'Téléphone' },
      { field: 'city', header: 'Ville' },
      { field: 'address', header: 'Adresse' },
      { field: 'relatedProducts', header: 'Articles' },
      { field: 'price', header: 'Prix' },
      { field: 'status', header: 'Statut' },
      { field: 'Help', header: 'Help' },
      { field: 'barcode', header: 'Barcode' },
    ];

    this.offerService.findAllOffers().subscribe((offers: any) => {
      this.offersList = offers.filter((offer: Offer) => offer.enabled);
    });

    this.cityService.findAllGroupedCities().subscribe((groupedCities: any) => {
      this.groupedCities = this.cityService.adaptListToDropDown(groupedCities);
      this.groupedCities = [...new Set(this.groupedCities)];
    });

    this.fbPageService.findAllFbPages().subscribe((result: any) => {
      this.fbPages = result;
    });

    this.rangeDates[0] = this.today;

    this.selectedStatus.setValue([]);
    this.selectedStatusList = this.statusList;
  }



  onEditInit(packet: any) {
    this.oldField = packet.data[packet.field];
  }

  onEditComplete(packet: any) {
    if (this.oldField !== packet.data[packet.field]) {
      if (packet.field == 'city' || packet.field == 'fbPage') {
        this.updatePacket(packet.data);
      } else {
        let updatedField = { [packet.field]: packet.data[packet.field] };
        let msg = 'Le champ a été modifié avec succés';
        if (
          packet.field == 'status' &&
          (packet.data[packet.field] == 'Confirmée')
        ) {
          if (!this.checkPacketValidity(packet.data)) {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: 'Veuillez saisir tous les champs',
            });
            packet.data[packet.field] = this.oldField;
            return;
          }
          this.selectedPacket = packet['data'].id;
          this.isLoading = true;
        }
        this.packetService
          .patchPacket(packet['data'].id, updatedField)
          .pipe(
            catchError((err: any, caught: Observable<any>): Observable<any> => {
              this.messageService.add({
                severity: 'error',
                summary: 'Error',
                detail: 'Erreur lors de la mise à jour ' + err.error.message,
              });
              packet.data[packet.field] = this.oldField;
              this.isLoading = false;
              return of();
            })
          )
          .subscribe((responsePacket: Packet) => {
            console.log('responsePacket0',responsePacket);
            console.log('packet.data[packet.field]',packet.data[packet.field]);
            console.log('packet.field == status',packet.field);
            if (packet.field === 'status' && ((packet.data[packet.field] === 'Confirmée') || (packet.data[packet.field] === 'Retour Echange'))) {
              console.log('packet.data[packet.field]');

              if((packet.data[packet.field] === 'Confirmée') || (packet.data[packet.field] === 'Retour Echange')){
                console.log('responsePacket.barcode',responsePacket.barcode);
                this.isLoading = false;
              if (responsePacket.barcode != null) {
                let pos = this.packetService.allPackets
                  .map((packet) => packet.id)
                  .indexOf(responsePacket.id);
                  console.log('pos',pos);
                this.packetService.allPackets.splice(pos, 1, responsePacket);
                this.packetService.allPacketsReadySubject.next(true);
                msg = 'Le barcode a été crée avec succés';
              } else {
                this.messageService.add({
                  severity: 'error',
                  summary: 'Error',
                  detail: 'Erreur lors de la creation du barcode',
                });
              }
              if(packet.data[packet.field] === 'Retour Echange'){
                packet.data[packet.field] = "Livrée";
              }
              }

            }
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: msg,
            });
          });
      }
    }
  }

  checkPacketValidity(packet: Packet) {
    console.log(packet);
    return (
      this.isValid(packet.fbPage) &&
      this.isValid(packet.address) &&
      this.isValid(packet.customerName) &&
      this.isValid(packet.customerPhoneNb) &&
      this.isValid(packet.city) &&
      this.isValid(packet.packetDescription)
    );
  }

  newPacket(): Packet {
    return {
      date: this.getDate(new Date()),
      barcode: '',
      lastDeliveryStatus: '',
      customerName: '',
      customerPhoneNb: '',
      address: '',
      relatedProducts: '',
      packetReference: '',
      packetDescription: '',
      price: 0,
      status: 'Non confirmée',
      exchange: false,
    };
  }

  updatePacket(packet: any) {
    this.packetService
      .updatePacket(packet)
      .pipe(
        catchError((err: any, caught: Observable<any>): Observable<any> => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Erreur lors de la mise à jour ' + err.error.message,
          });
          packet.data[packet.field] = this.oldField;
          return of();
        })
      )
      .subscribe((response: any) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'La commande est ajoutée avec succés',
        });
      });
  }

  getLastStatus(packet: Packet) {
    if (packet.status != 'Payée' && packet.status != 'Retour reçu'&& packet.status != 'Retour Expediteur' && packet.status != 'Livrée')
      this.packetService.getLastFirstStatus(packet);
  }

  openLink(code: string) {
    const formData = new FormData();
    formData.append('code', code);
    const link = 'https://www.firstdeliverygroup.com/fournisseur/recherche.php';
    // Create a hidden form
    const form = document.createElement('form');
    form.action = link;
    form.method = 'POST';
    form.target = '_blank';
    // Append the form data as hidden fields
    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = 'code';
    input.value = code.toString();
    form.appendChild(input);
    // Append the form to the document and submit it
    document.body.appendChild(form);
    form.submit();
  }

  printFirst(link: string) {
    window.open(link, '_blank');
  }

  showDialogStatus(packet: Packet) {
    try {
      this.packetService.getPacketAllStatus(packet.id).subscribe(
        (response: any) => {
          this.statusEvents = [];
          console.log(response);
          this.suiviHeader = 'Suivi Status de packet num :' + packet.id;
          if (response != null && response.length > 0) {
            response.forEach((element: any) => {
              this.statusEvents.push({
                status: element.status,
                date: element.date,
                icon: PrimeIcons.ENVELOPE,
                color: '#9C27B0',
              });
            });
          }
          this.cdRef.detectChanges();
          this.displayStatus = true;
        },
        (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Erreur dans le status',
          });
        }
      );
    } catch (error) {
      console.log('response' + error);
    }
  }

  addNewRow() {
    this.packetService
      .addPacket(this.newPacket())
      .subscribe((response: any) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'La commande est ajoutée avec succés',
          life: 1000,
        });
        this.packets.unshift(response);
        this.packetService.allPackets.unshift(response);
      });
  }

/*   newDuplicatePacket(packet: Packet): Packet {
    return {
      date: this.getDate(new Date()),
      barcode: '',
      lastDeliveryStatus: '',
      customerName: packet.customerName + '  echange id: ' + packet.id,
      customerPhoneNb: packet.customerPhoneNb,
      address: packet.address,
      relatedProducts: packet.relatedProducts,
      packetReference: packet.packetReference,
      packetDescription: packet.packetDescription,
      price: packet.price,
      status: 'Non confirmée',
      fbPage: packet.fbPage,
      city: packet.city,
      exchange: true,
    };
  } */

  duplicatePacket(packet: Packet) {
    this.packetService
      .duplicatePacket(packet.id)
      .subscribe((response: any) => {
        console.log(response);

        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'La commande est dupliqué avec succés',
          life: 1000,
        });
        this.packetService.allPackets.unshift(response);
        this.packetService.allPacketsReadySubject.next(true);
      });
  }

  deleteSelectedPackets() {
    let selectedPacketsById = this.selectedPackets.map(
      (selectedPacket: Packet) => selectedPacket.id
    );
    console.log(selectedPacketsById);
    this.confirmationService.confirm({
      message:
        'Êtes-vous sûr de vouloir supprimer les commandes séléctionnées ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.packetService
          .deleteSelectedPackets(selectedPacketsById)
          .subscribe((result) => {
            console.log('packets successfully deleted !');
            this.packetService.allPackets =
              this.packetService.allPackets.filter(
                (packet: Packet) => selectedPacketsById.indexOf(packet.id) == -1
              );
            this.packetService.allPacketsReadySubject.next(true);
            this.selectedPackets = [];
            this.messageService.add({
              severity: 'success',
              summary: 'Succés',
              detail:
                'Les commandes séléctionnées ont été supprimé avec succés',
              life: 1000,
            });
          });
      },
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
    console.log('edit pproduct');
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
    let packet = this.packets.filter((p) => p.id == $event.packet.idPacket)[0];
    let posAllPackets = this.packetService.allPackets.indexOf(packet);
    packet.packetDescription = $event.packet.packetDescription?.replace(this.reg, '\n');
    packet.price = $event.packet.totalPrice
    packet.deliveryPrice = $event.packet.deliveryPrice
    packet.discount = $event.packet.discount;
    this.packetService.allPackets.splice(posAllPackets, 1, packet);
    this.packetService.allPacketsReadySubject.next(true)
    if (!this.editMode)
      this.messageService.add({
        severity: 'info',
        summary: 'Success',
        detail: 'Les articles ont été ajoutés avec succés',
        life: 1000,
      });
    else
      this.messageService.add({
        severity: 'info',
        summary: 'Success',
        detail: 'Les articles ont été mis à jour avec succés',
        life: 1000,
      });
  }

  checkValidity(date1: Date, date2: Date, status: String) {
    if (status != 'Payée' && status != 'Retour reçu')
      return this.getDate(date1) < this.getDate(date2);
    return false;
  }

  filterByText(text: string){
    console.log(this.rangeDates);
    this.createRangeDate();
    let params = {
      page : 0,
      size: 100,
      searchText: text != null && text != '' ? text : null,
      startDate : this.rangeDates !== null && this.rangeDates.length > 0 ? this.formatDateToString(this.startDate) : null,
      endDate : this.rangeDates !== null && this.rangeDates.length > 0 ? this.formatDateToString(this.endDate) : null
     };
   this.packetService.findAllPackets(params)
     .pipe(takeUntil(this.$unsubscribe))
     .subscribe({   next:(response: any) => {
       console.log(response);

       this.packets = response.result;
       //this.filterChange('date');
     }, 
     error: (error: any) => {
       console.log('Error:', error);
     },
     complete: () => {
       console.log('Observable completed-- All Packets From Base --');
     }});

  }

  createRangeDate() {
    if (this.rangeDates !== null && this.rangeDates !== undefined) {
      this.startDate = this.rangeDates[0];
      if (this.rangeDates[1]) {
        this.endDate = this.rangeDates[1];
      } else {
        this.endDate = this.startDate;
      }
    }
  }

  filterChange($event: string) {
    if($event == 'clear'){
      this.selectedStates = [];
      this.selectedStatusList = this.statusList;
      this.selectedStatus.setValue([]);
    }
    if ($event == 'states') {
      this.selectedStatus.setValue([]);
      this.selectedStatusList = [];
      if (this.selectedStates.indexOf('Bureau') > -1) {
        this.selectedStatus.patchValue([
          'Non confirmée',
          'En rupture',
          'Injoignable',
          'Annulée',
          'Confirmée'
        ]);
        this.selectedStatusList = this.statusListEtat0;
      }
      if (this.selectedStates.indexOf('En Cours') > -1) {
        this.selectedStatus.patchValue([
          'En cours (1)',
          'En cours (2)',
          'En cours (3)',
          'En cours',
        ]);
        this.selectedStatusList = this.statusList;
      }
      if (this.selectedStates.indexOf('Livrée') > -1) {
        this.selectedStatus.patchValue(['Livrée']);
        this.selectedStatusList = this.statusListLivree;
      }
      if (this.selectedStates.indexOf('Retour') > -1) {
        this.selectedStatus.patchValue(['Retour','Retour Expediteur']);
        this.selectedStatusList = this.statusListRetour;
      }
      if (this.selectedStates.indexOf('Terminé') > -1) {
        this.selectedStatus.patchValue(['Payée', 'Retour reçu']);
      }
    }

    let startDate = new Date();
    let endDate = new Date();
    if (this.rangeDates !== null && this.rangeDates !== undefined) {
      startDate = this.rangeDates[0];
      if (this.rangeDates[1]) {
        endDate = this.rangeDates[1];
      } else {
        endDate = startDate;
      }
    }
    let rangeDateExist = this.rangeDates[0] !== null && this.rangeDates[0] !== undefined;
    if(this.selectedStatus.value == null)this.selectedStatus.setValue([]);
    this.packets = this.packetService.allPackets.filter((packet: any) =>
      (rangeDateExist
        ? this.getDate(packet.date) >= this.getDate(startDate) &&
        this.getDate(packet.date) <= this.getDate(endDate)
        : true) &&
      (this.selectedStatus.value.length === 0
        ? packet.status !== 'Supprimé'
        : this.selectedStatus.value.indexOf(packet.status) > -1)
    );
    this.packets = [...this.packets];
  }

  resetTable() {
    this.rangeDates = [];
    this.selectedStates = [];
    this.selectedPackets = [];
    this.selectedStatus.setValue([]);
    this.filterChange('global');
  }

  changeColor(this: any) {
    this.style.color = 'red';
  }

  calculatePrice(packet: Packet) {
    return packet.price! + packet.deliveryPrice! - packet.discount!;
  }

  getValue(fieldName: any) {
    return fieldName != null && fieldName != undefined ? fieldName : '';
  }

  isValid(field: any) {
    return field != null && field != undefined && field != '';
  }

  transformDate(date: any) {
    return this.datePipe.transform(date, 'dd/MM/yyyy');
  }

  trackByFunction = (index: any, item: { id: any }) => {
    return item.id; // O index
  };

  getDate(date: Date): any {
    return this.datePipe.transform(date, 'yyyy-MM-dd');
  }

  getPhoneNumber1(phoneNumber1: string) {
    if (this.getValue(phoneNumber1) != '' && phoneNumber1.includes('/')) {
      return phoneNumber1.substring(0, 8);
    }
    return this.getValue(phoneNumber1);
  }

  getPhoneNumber2(phoneNumber: string) {
    if (this.getValue(phoneNumber) != '' && phoneNumber.includes('/')) {
      return phoneNumber.substring(9, phoneNumber.length);
    }
    return '';
  }

  ngOnDestroy() {
    this.subcriber.next();
    this.subcriber.complete();
  }

  getLastStatusDate(array: any) {
    let lastDate = '';
    array.forEach((element: any) => {
      lastDate = element.date;
    });
    return lastDate;
  }

  clearStatus() {
    this.selectedStates = [];
    this.selectedStatusList = this.statusList;
    this.selectedStatus.setValue([]);
    if (this.filter != '' && this.filter != null) {
      this.dt!.reset();
      this.dt!.filterGlobal(this.filter, 'contains');
    }
  }

  formatDateToString(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    
    return `${year}-${month}-${day}`;
  }

  
}
