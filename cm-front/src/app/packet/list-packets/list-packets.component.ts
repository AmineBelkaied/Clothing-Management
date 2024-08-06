
import { ConfirmationService, SelectItemGroup, PrimeIcons, MenuItem } from 'primeng/api';
import { MessageService } from 'primeng/api';
import { Packet } from '../../../shared/models/Packet';
import { OfferService } from '../../../shared/services/offer.service';
import { PacketService } from '../../../shared/services/packet.service';
import { DatePipe } from '@angular/common';
import { Table, TableRowSelectEvent } from 'primeng/table';
import { CityService } from '../../../shared/services/city.service';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from '../../../shared/services/fb-page.service';
import { catchError, Observable, of, Subject,takeUntil} from 'rxjs';
import { Offer } from 'src/shared/models/Offer';
import { StorageService } from 'src/shared/services/strorage.service';
import { DateUtils } from 'src/shared/utils/date-utils';
import { CANCELED, OOS, NOT_SERIOUS, PROBLEME,
  DELETED, TERMINE, statesList, statusList,
  IN_PROGRESS_1,
  IN_PROGRESS_2,
  IN_PROGRESS_3,
  CONFIRMED,
  TO_VERIFY,
  RETURN,
  RETURN_RECEIVED,
  PAID,
  NOT_CONFIRMED,
  DELIVERED,
  UNREACHABLE,
  IN_PROGRESS} from 'src/shared/utils/status-list';
import { City } from 'src/shared/models/City';
import { ResponsePage } from 'src/shared/models/ResponsePage';
import { DashboardCard } from 'src/shared/models/DashboardCard';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { ChangeDetectorRef, Component, ElementRef, EventEmitter, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ContextMenu } from 'primeng/contextmenu';
import { ProductService } from 'src/shared/services/product.service';
import { ClientReason, ClientReasonDetails } from 'src/shared/enums/client-reason';
import { Note } from 'src/shared/models/Note';
import { StringUtils } from 'src/shared/utils/string-utils';

@Component({
  selector: 'app-list-packets',
  templateUrl: './list-packets.component.html',
  styleUrls: ['./list-packets.component.css'],
  providers: [DatePipe]
})
export class ListPacketsComponent implements OnInit, OnDestroy {
  onRowSelect($event: TableRowSelectEvent) {
    console.log($event);

  }

  x: number;

  @ViewChild('contextMenu', { static: false }) contextMenu: ContextMenu;
  display: boolean = false;
  displayStatus: boolean = false;
  suiviHeader: string = 'Suivi';
  events: any[] = [];
  statusEvents: any[] = [];
  packets: Packet[];
  totalItems: number;
  packet: Packet;
  cols: object[] = [];
  selectedPackets: Packet[] = [];
  rangeDates: Date[] = [];
  startDate: Date = new Date();
  endDate: Date = new Date();
  today: Date = new Date();
  today_2: Date = new Date(Date.now() - 172800000);
  editMode = false;
  isLoading = false;
  selectedPacket: Packet;


  modelDialog!: boolean;

  first = 0;
  rows = 100;
  currentPage = 0;
  oldFieldValue: string = "";
  offersIdsListByFbPage: any[] = [];
  allOffersList: any[] = [];
  groupedCities: SelectItemGroup[] = [];
  fbPages: FbPage[] = [];
  selectedCity: City | undefined;
  //selectedCity: string | undefined;
  filter: string;

  enCoursStatus: string[] = [];
  statusList: string[] = [];
  optionButtons: MenuItem[];
  packetStatusList: string[] = [];
  statesList: string[] = [];
  selectedStatus: FormControl = new FormControl();
  selectedStates: string[] = [];
  $unsubscribe: Subject<void> = new Subject();
  pageSize: number = 100;
  params: any = {
    page: 0,
    size: this.pageSize,
    startDate: this.dateUtils.formatDateToString(this.today),
    endDate: this.dateUtils.formatDateToString(this.today),
    mandatoryDate: false
  };
  loading: boolean = false;
  mandatoryDateCheckBox: boolean = false;
  oldDateFilterCheckBox: boolean = false;
  dateOptions: any[] = [{ label: 'Off', value: false }, { label: 'On', value: true }];
  value: boolean = this.mandatoryDateCheckBox;
  nbrConfirmed: number = 0;
  countUNREACHABLE: string = "0";

  statusItems: any[] = [
    {
      label: "Erreur Chargement",
      title: "Tous",
      badge: 0,
      badgeByDate: 0,
      command: (event: any) => {
      },
      disabled: true
    },
    {
      label: OOS,
      title: OOS,
      badge: 0,
      badgeByDate: 0,
      command: (event: any) => {
      }
    },
    {
      label: NOT_CONFIRMED,
      title: NOT_CONFIRMED,
      badge: 0,
      badgeByDate: 0,
      command: (event: any) => {
      }
    },
    {
      label: UNREACHABLE,
      title: UNREACHABLE,
      icon: 'pi-power-off',
      badge: 0,
      badgeByDate: 0,
      command: (event: any) => {
      }
    },
    {
      label: CONFIRMED,
      title: CONFIRMED,
      badge: 0,
      badgeByDate: 0,
      command:
        (event: any) => {
        }
    },
    {
      label: IN_PROGRESS,
      title: IN_PROGRESS,
      badge: 0,
      badgeByDate: 0,
      command: (event: any) => {
      },
    },
    {
      label: RETURN,
      title: RETURN,
      badge: 0,
      badgeByDate: 0,
      command: (event: any) => {
      }
    },
    {
      label: CANCELED,
      title: CANCELED,
      badge: 0,
      badgeByDate: 0,
      command: (event: any) => {
      }
    },
    {
      label: 'Terminé',
      title: 'Terminé',
      badge: 0,
      badgeByDate: 0,
      command: (event: any) => { }
    }
  ];
  showStatus: boolean = false;
  activeIndex: number = 2;
  oldActiveIndex: number = 2;

  @ViewChild('dt') dt: Table;
  private readonly reg: RegExp = /,/gi;
  regBS = /\n/gi;
  private readonly FIRST: string = 'FIRST';
  countCanceled: number = 0;
  realTotalItems: number;
  selectedPhoneNumber: string = '';
  //deliveryCompanyName?: DeliveryCompany;
  @Output() confirmEvent: EventEmitter<string> = new EventEmitter<string>();
  visibleNote: boolean = false;
  activeClass: boolean;
  userName: string;
  isLoggedIn: boolean;
  isAdmin: boolean;
  isSuperAdmin: boolean;
  meterGroupValue= [
    { label: 'Space used', value: 15, color: '#34d399' }
  ];
  selectedField: keyof Packet;
  reasonOptions: { label: string, value: string }[] = [];
  selectedReason: string;
  explanation: string;
  readonly explanationSuffix: string = ' : ';
  clientReasons: any[];
  note: Note = {
    date: new Date()
  };
  selectedPacketNotes: Note[] = [];
  @ViewChild("expRef") explanationElement: ElementRef;
  explanationTitle: string;
  noteActionStatus: string;

  constructor(
    private packetService: PacketService,
    private confirmationService: ConfirmationService,
    private offerService: OfferService,
    private cityService: CityService,
    private fbPageService: FbPageService,
    private dateUtils: DateUtils,
    public storageService: StorageService,
    public messageService:MessageService,
    private cdRef: ChangeDetectorRef,
    private productService : ProductService
    ) {
    this.statusList = statusList;
    this.statesList = statesList;
  }



  ngAfterViewChecked(){
    this.cdRef.detectChanges();
  }

  ngOnInit(): void {
    this.storageService.isLoggedIn.subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
      this.userName = this.storageService.getUserName();
      this.isAdmin = this.storageService.hasRoleAdmin();
      this.isSuperAdmin = this.storageService.hasRoleSuperAdmin();
      this.activeClass = true;
    });
    this.offerService.getOffersSubscriber();
    this.createColumns();
    this.findAllGroupedCities();
    this.findAllFbPages();
    this.rangeDates = [this.today];
    this.onActiveIndexChange(2);
    this.selectedStatus.setValue([]);
  }

  findAllFbPages(): void {
    this.fbPageService.findAllFbPages().subscribe((result: any) => {
      this.fbPages = result.filter((fbPage: any) => fbPage.enabled);
    });
  }

  findAllPackets(): void {
    this.loading = true;
    this.packetService.findAllPackets(this.params)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: ResponsePage) => {

          this.packets = response.result.filter((packet: any) => this.checkPacketNotNull(packet));
          this.realTotalItems = response.totalItems;
          this.totalItems = this.packets.length;
          let countConfirmed =response.result.filter(packet => packet.status === CONFIRMED).length;
          this.statusItems[3].badge = countConfirmed > 0 ? countConfirmed:0;
          this.loading = false;
          this.createNotification();
        },
        error: (error: Error) => {
          console.log('Error:', error);
          this.loading = false;
        }
      });
  }

  createColumns(): void {
    this.cols = [
      { field: 'date', header: 'Date' },
      { field: 'fbPage.name', header: 'PageFB' },
      { field: 'customerName', header: 'Client', customExportHeader: 'Product Code'},
      { field: 'customerPhoneNb', header: 'Téléphone' },
      { field: 'city', header: 'Ville' },
      { field: 'address', header: 'Adresse' },
      { field: 'relatedProducts', header: 'Articles' },
      { field: 'price', header: 'Prix' },
      { field: 'status', header: 'Statut' },
      { field: 'barcode', header: 'Barcode' },
    ];
  }



  findAllGroupedCities(): void {
    this.cityService.findAllGroupedCities().subscribe((groupedCities: any) => {
      this.groupedCities = this.cityService.adaptListToDropDown(groupedCities);
      this.groupedCities = [...new Set(this.groupedCities)];
    });
  }

  onEditComplete($event: any): void {
    const packet = $event.data;
    this.loading = true;

    try {
      if (this.oldFieldValue !== packet[this.selectedField] && packet[this.selectedField] !== undefined) {
        switch (this.selectedField) {
          case 'status':
            this.handleStatusField(packet);
            break;
          default:
            this.patchPacketService(packet);
            break;
        }
      } else {
        console.log("No changes");
        this.loading = false;
        return;
      }
    } catch (error) {
      console.error(error);
    } finally {
      this.loading = false;
      this.oldFieldValue = '';
    }
  }

  patchPacketService(packet : Packet) {
      let updatedField;
      if(this.selectedField ==='city')
        updatedField = { [this.selectedField]: packet.city?.id };
      else if(this.selectedField ==='fbPage')
        updatedField = { [this.selectedField]: packet.fbPage?.id };
      else if(this.selectedField ==='date')
        updatedField = { [this.selectedField]: this.onDateSelect(packet.date) };
      else
        updatedField = { [this.selectedField]: packet[this.selectedField] };

      let status = packet.status;
      this.packetService.patchPacket(packet.id!, updatedField)
        .pipe(
          catchError((err: any, caught: Observable<any>): Observable<any> => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error updating packet ' + err.error.message });
            const packetIndex = this.packets.findIndex((p: any) => p.id === packet.id);
            if (packetIndex !== -1) {
              this.packets[packetIndex][this.selectedField] = this.oldFieldValue;
            }
            this.loading = false;
            return of();
          })
        )
        .subscribe({
          next: (responsePacket: any) => {
            let msg = "Packet updated successfully";
            if (this.selectedField === 'status') {
              this.createNotification();
            }
            if (packet.stock! < 10 && this.selectedField === 'status' && ((status === CONFIRMED && responsePacket.barcode != null) || status === CANCELED || status === RETURN_RECEIVED)) {
              this.getLastStock(packet.id!);
            }
            if (this.selectedField === 'status' && status === CONFIRMED && responsePacket.barcode != null) {
              this.updatePacketFields(responsePacket);
              msg ='Barcode created successfully';
              //this.statusItems[3].badge += 1;
            } else if (responsePacket.oldClient !== undefined && this.selectedField === 'customerPhoneNb') {

              const packetIndex = this.packets.findIndex((p: Packet) => p.id === responsePacket.id);
              if (packetIndex !== -1) {
                this.packets[packetIndex].oldClient = responsePacket.oldClient;
                msg ='Phone number updated successfully';
              }
            }

            this.messageService.add({ severity: 'success', summary: 'Success', detail: msg });
            this.loading = false;
          },
          error: () => {
            this.loading = false;
          }
        });
  }

  onDateSelect(event: any) {
    // Manually set the hours to 3 AM, minutes, and seconds to zero
    const selectedDate = new Date(event);
    selectedDate.setHours(8, 0, 0, 0);
    return selectedDate;
  }


  checkPacketValidity(packet: Packet): boolean {
      if (!(this.isValid(packet.fbPage) && this.isValid(packet.address) && this.isValid(packet.customerName) &&
      this.isValid(packet.customerPhoneNb) && this.isValid(packet.city) && this.isValid(packet.packetDescription)))
      {
        this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Veuillez saisir tous les champs' });
        return false;
      }
      return true;
  }

  onSubmit(event:Event) {
      this.confirmationService.confirm({
        target: event.target as EventTarget,
        message: 'Stock 0-Veiller le remplacer par un fake size.',
        icon: 'pi pi-exclamation-circle',
        acceptIcon: 'pi pi-check mr-1',
        rejectIcon: 'pi pi-times mr-1',
        rejectButtonStyleClass: 'p-button-danger p-button-sm',
        acceptButtonStyleClass: 'p-button-outlined p-button-sm',
        accept: () => {
            this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Veiller remplir fake size', life: 3000 });
        },
        reject: () => {
            //this.submitProductsOffers(productsOffers,false);
            this.messageService.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 });
        }
      });
  }

  checkPacketNotNull(packet: Packet): boolean {
    return (this.isValid(packet.address) || this.isValid(packet.customerName) ||
      this.isValid(packet.customerPhoneNb) || this.isValid(packet.city) || this.isValid(packet.packetDescription));
  }

  checkPacketDescription(packet: Packet): boolean {
    return packet.packetDescription!= undefined && packet.packetDescription.includes('(');
  }

  getLastStatus(packet: Packet): void {
    if (packet.status != PAID && packet.status != RETURN_RECEIVED && packet.status != DELIVERED)
    this.packetService.getLastStatus(packet.id!)
      .subscribe({
          next: (response: any) => {//correction Packet
            this.updatePacketFields(response)
            this.createNotification();
          },
          error : (error: Error) => {
            console.log(error);
          }
        });
  }

  updatePacketFields(packet: Packet, action?: string) {
    console.log('packet',packet);
    let listId = this.packets.map((packetX: Packet) => packetX.id);
    let X = listId.indexOf(packet.id);
    if (X > -1) {
      this.packets[X].status = packet.status;
      this.packets[X].lastDeliveryStatus = packet.lastDeliveryStatus;
      this.packets[X].lastUpdateDate = packet.lastUpdateDate;
      if (action === 'ADD_NOTE_ACTION') {
        this.packets[X].notes!.length++;
        this.packets[X].notes = packet.notes?.slice();
      }
      this.packets[X].barcode = packet.barcode;
      this.packets[X].packetDescription = packet.packetDescription;
      this.packets[X].oldClient = packet.oldClient;
      this.packets[X].stock = packet.stock;
      this.packets[X].price = packet.price;
      this.packets[X].deliveryPrice = packet.deliveryPrice;
      this.packets[X].discount = packet.discount;
      this.packets[X].attempt=packet.attempt;
    }
  }

  addAttempt(packet: Packet, status: string): void {
    this.noteActionStatus = status;
    this.selectedPacket = packet;
    this.clientReasons = this.getReasonOptionsByStatus(status);
    this.visibleNote = true;
    this.explanationTitle = '';
    this.explanation = '';
    this.note = {
      date: new Date(),
      packet: packet,
      clientReason: '',
      status: ''
    };
    this.clientReasons.forEach(clientReason => {
      clientReason.text = true
      clientReason.outlined = false
    });
  }

  confirmNote() {
    this.note.date = new Date();
    this.note.explanation = StringUtils.isStringValid(this.explanation) ? this.explanationTitle + ' : ' + this.explanation : this.explanationTitle;
    /* if (this.note.trim() !== '')  // Check if value is not empty
       {
         this.confirmEvent.emit(this.note);
         note = this.note;
       }*/
    switch (this.noteActionStatus) {
      case 'DELETED': {
        let selectedPacketsByIds = this.selectedPackets.map((selectedPacket: Packet) => selectedPacket.id!);
        this.packetService
          .deleteSelectedPackets(selectedPacketsByIds)
          .subscribe(() => {
            this.packets = this.packets.filter((packet: Packet) => selectedPacketsByIds.indexOf(packet.id!) == -1);
            this.selectedPackets = [];
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Les commandes séléctionnées ont été supprimé avec succés', life: 1000 });
          });
        break;
      }
      case 'UNREACHABLE': {
        this.packetService.addAttempt(this.note, this.selectedPacket.id!)
          .subscribe({
            next: (response: any) => {
              this.updatePacketFields(response, 'ADD_NOTE_ACTION');
              this.messageService.add({ severity: 'info', summary: 'Success', detail: 'La note a été ajoutée avec succés', life: 1200 });
            },
            error: (error: Error) => {
              this.messageService.add({ severity: 'error', summary: 'Error', detail: "Une erreur est survenue lors de l'ajout de la note", life: 1200 });
            }
          });
          break;
      }
      default: {
        console.log("Note Action Status did not match any cases.");
      }
    }
    this.visibleNote = false;
}

  getLastStock(packetId: number): void {
    console.log("getLasStock-packetId", packetId);
    //if (packet.status != PAID && packet.status != RETURN_RECEIVED && packet.status != DELIVERED)
    this.packetService.getLastStock(packetId)
      .subscribe({
          next: (listupdatedStock:any) => {
            console.log("getLasStock-listupdatedStock", listupdatedStock);
            listupdatedStock.forEach((element: any) => {
              this.updatePacketFields(element)
            });
          },
          error : (error: Error) => {
            console.log(error);
          }
        });
  }

  openLinkGetter(code: any, deliveryCompany: DeliveryCompany): void {
    let link = deliveryCompany.barreCodeUrl + code;
    console.log("link", link + "/code:" + code);
    window.open(link, '_blank');
  }

  printFirst(link: string): void {
    window.open(link, '_blank');
  }

  showTimeLineDialog(packet: Packet): void {
    try {
      this.packetService.getPacketTimeLine(packet.id!).subscribe((response: any) => {
        this.statusEvents = [];
        this.suiviHeader = "Suivi Historique - Commande N° " + packet.id;
        if (response != null && response.length > 0) {
          response.forEach((element: any) => {
            this.statusEvents.push({ status: element.status, date: element.date, user: element.user, icon: PrimeIcons.ENVELOPE, color: '#9C27B0' });
          });
        }

        this.displayStatus = true;
      }
      );
    } catch (error) {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur dans le status'});
    }
  }

  addNewRow(): void {
    if (this.activeIndex != 2)
      this.onActiveIndexChange(2);
    if (this.loading == false) {
      //this.activeIndex=2;
      this.loading = true;
      this.packetService
        .addPacket()
        .subscribe((response: Packet) => {
          console.log("new pack", response);

          this.loading = false;
          this.packets.unshift(response);
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est ajoutée avec succés', life: 1000 });
        });
    }
  }

  duplicatePacket(packet: Packet): void {
    this.packetService
      .duplicatePacket(packet.id!)
      .subscribe((response: any) => {// correction------------------------
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est dupliqué avec succés', life: 1000});
        this.packets.unshift(response);
      });
  }

  deleteSelectedPackets(): void {
    this.addAttempt(this.selectedPackets[0], 'DELETED');
  }

  loadOfferListAndOpenOffersDialog(packet: Packet,editMode:boolean): void {
    if (packet.fbPage) {
      this.productService.loadProducts().subscribe(() => {
        this.packet = Object.assign({}, packet);
        this.modelDialog = true;
        this.editMode = editMode;
      });
    } else {
      this.messageService.add({ severity: 'info', summary: 'Info', detail: 'Pas de page Facebook selectionné', life: 1000 });
    }
  }

  hideDialog(): void {
    this.modelDialog = false;
  }

  OnSubmit($event: any): void {
    //console.log("onsubmit list packet",$event);
    this.modelDialog = $event.modelDialog;
    //console.log("$event.modelDialog"+this.modelDialog);

    this.updatePacketFields($event.packet);
    this.editMode ? this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été mis à jour avec succés', life: 1000 }) : this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été ajoutés avec succés', life: 1000 });
  }

  checkValidity(date1: Date, date2: Date, status: String): boolean {
    if (status != PAID && status != RETURN_RECEIVED)
      return this.dateUtils.getDate(date1) < this.dateUtils.getDate(date2);
    return false;
  }

  handleInputChange() {
    const inputValue = this.filter;
    const numbersCount = (inputValue.match(/\d/g) || []).length;
    if (numbersCount === 5 || numbersCount === 8 || numbersCount === 12  ) {
      this.oldActiveIndex = this.activeIndex;
      this.filterPackets('global');
      this.activeIndex = 0;
    }
    if (this.filter === '') {
      this.filterPackets('global');
      this.activeIndex = this.oldActiveIndex;
    }
  }

  filterPackets($event?: string): void {
    this.createRangeDate();
    let page = 0;
    if ($event == 'clear') {
      this.selectedStates = [];
      this.selectedStatus.setValue([]);
    } else if ($event == 'page')
      page = this.currentPage;
    if (this.selectedStatus.value == null) this.selectedStatus.setValue([]);

    if (this.filter !== '' && this.filter !== undefined)
      { this.oldActiveIndex = this.activeIndex;this.activeIndex = 0;console.log("filter:",this.filter);
    }

    this.params = {
      page: page,
      size: this.pageSize,
      searchText: this.filter != null && this.filter != '' ? this.filter : null,
      startDate: this.dateUtils.formatDateToString(this.startDate),
      endDate: this.dateUtils.formatDateToString(this.endDate),
      status: this.selectedStatus.value.length == 0 ? null : this.selectedStatus.value.join(),
      mandatoryDate: this.mandatoryDateCheckBox
    };
    this.findAllPackets();
  }

  createRangeDate(): void {
    if (this.rangeDates !== null) {
      this.startDate = this.rangeDates[0];
      if (this.rangeDates[1]) {
        this.endDate = this.rangeDates[1];
      } else {
        this.endDate = this.startDate;
      }
    } else {
      this.startDate = this.today;
      this.endDate = this.today;
    }
  }

  onPageChange($event: any): void {
    this.currentPage = $event.page;
    this.pageSize = $event.rows;
    console.log("filterPackets-onPageChange");

    this.filterPackets('page');
  }

  resetTable(): void {
    this.selectedStates = [];
    this.selectedPackets = [];
    this.selectedStatus.setValue([]);
    this.rangeDates = [new Date(2023, 0, 1), new Date(Date.now())];
    console.log("filterPackets-resetTable");
    this.filterPackets('global');
  }

  changeColor(this: any): void {
    this.style.color = 'red';
  }

  calculatePrice(packet: Packet): number {
    return packet.price! + packet.deliveryPrice! - packet.discount!;
  }

  getValue(fieldName: any): string {
    return fieldName != null && fieldName != undefined ? fieldName : '';
  }

  isValid(field: any) {
    return field != null && field != undefined && field != '';
  }

  trackByFunction = (index: any, item: { id: any }) => {
    return item.id;
  };

  getPhoneNumber1(phoneNumber1: string): string {
    if (this.getValue(phoneNumber1) != '' && phoneNumber1.includes('/')) {
      return phoneNumber1.substring(0, 8);
    }
    return this.getValue(phoneNumber1);
  }

  getPhoneNumber2(phoneNumber: string): string {
    if (this.getValue(phoneNumber) != '' && phoneNumber.includes('/')) {
      return phoneNumber.substring(9, phoneNumber.length);
    }
    return '';
  }

  clearStatus(): void {
    this.selectedStates = [];
    this.packetStatusList = this.statusList;
    this.selectedStatus.setValue([]);
    if (this.filter != '' && this.filter != null) {
      this.dt!.reset();
      this.dt!.filterGlobal(this.filter, 'contains');
    }
  }

  checkCodeABarreExist(packet:Packet){
    return packet!= undefined && packet.barcode !="" && packet.barcode!= null
  }

  checkPhoneNbExist(packet:Packet){
    return packet!= undefined && packet.customerPhoneNb !="" && packet.customerPhoneNb!= null
  }

  mandatoryDateChange(){
    if(this.selectedStatus.value != null && this.selectedStatus.value.length > 0)
      //console.log("filterPackets-mandatoryDateChange");
    this.filterPackets('global')
  }

  oldDateFilter(){
      this.rangeDates = [new Date(2023, 0, 1), new Date(Date.now() - 86400000)];
      //console.log("filterPackets-oldDateFilter");
      this.filterPackets('global');

    //console.log('aaa',this.rangeDates);
  }
  todayDate(){
    if(this.rangeDates[0] != undefined && this.rangeDates[1]==undefined)
      {
        this.rangeDates[0]=this.startDate;
        this.endDate = this.today;
        this.rangeDates= [this.startDate,this.today];
      }
    else this.rangeDates = [this.today];
    this.filterPackets('global');
  }

  clearDate(){
    this.rangeDates = [];
    this.filterPackets('global');
  }

  selectCity( packet: Packet) {
    this.selectedCity = packet.city;
  }
  selectPhoneNumber( packet: any) {
    this.selectedPhoneNumber = packet.customerPhoneNb;
  }

  onActiveIndexChange(event: any) {
    this.activeIndex = event;
    if(this.statusItems[event].title == IN_PROGRESS)
      {
        this.nonConfirmedOptionsValue=[];
        this.endedOptionsValue=[];
        this.canceledOptionsValue = [];
        if(this.enCoursOptionsValue != undefined && this.enCoursOptionsValue.length > 0)
        this.selectedStatus.patchValue(this.enCoursOptionsValue);
        else
        this.selectedStatus.patchValue([ TO_VERIFY, IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3]);
      }
    else if(this.statusItems[event].title == NOT_CONFIRMED)
      {
        this.endedOptionsValue=[];
        this.canceledOptionsValue = [];
        this.enCoursOptionsValue = [];
        if(this.nonConfirmedOptionsValue != undefined && this.nonConfirmedOptionsValue.length > 0)
            this.selectedStatus.patchValue(this.nonConfirmedOptionsValue);
        else
            this.selectedStatus.patchValue([ NOT_CONFIRMED, UNREACHABLE]);
      }
    else if(this.statusItems[event].title == CANCELED)
      {
        this.nonConfirmedOptionsValue=[];
        this.endedOptionsValue=[];
        this.enCoursOptionsValue = [];
        if(this.canceledOptionsValue != undefined && this.canceledOptionsValue.length > 0)
            this.selectedStatus.patchValue(this.canceledOptionsValue);
        else
            this.selectedStatus.patchValue([ CANCELED, DELETED]);
      }

    else if(this.statusItems[event].title == "Terminé")
      {
        this.nonConfirmedOptionsValue=[];
        this.canceledOptionsValue = [];
        this.enCoursOptionsValue = [];
        if(this.endedOptionsValue != undefined && this.endedOptionsValue.length > 0)
          this.selectedStatus.patchValue(this.endedOptionsValue);
      else{
        this.selectedStatus.patchValue([DELIVERED,PAID,RETURN_RECEIVED]);
        }
      }
    else {
      this.clearAllSelectedStatus();
      this.selectedStatus.setValue([]);
      this.selectedStatus.patchValue([this.statusItems[event].title]);
    }
    //console.log("filterPackets-onActiveIndexChange",this.params);
    this.filterPackets('status');
  }

  showStatusButton() {
    this.showStatus= !this.showStatus;
    this.createNotification();
  }

  getReasonOptionsByStatus(status: string) {
    return (Object.keys(ClientReason) as (keyof typeof ClientReason)[])
      .filter(key => isNaN(Number(key)) && ClientReasonDetails[ClientReason[key]].status === status)  // Filter out any non-number keys
      .map(key => {
        const reasonKey = ClientReason[key];
        const clientReasonDetails = ClientReasonDetails[reasonKey];
        return {
          value: key,
          label: clientReasonDetails.label,
          description: clientReasonDetails.description,
          status: clientReasonDetails.status,
          text: clientReasonDetails.text,
          outlined: clientReasonDetails.outlined,
          severity: clientReasonDetails.severity,
        }
      })
  }

  onSelectReason(clientReason: ClientReason, index: number) {
    this.explanationTitle = this.clientReasons[index]?.description;
    if (this.explanationElement && this.explanationElement.nativeElement) {
      setTimeout(() => {
        this.explanationElement.nativeElement.focus();
      }, 0);
    }
    this.clientReasons.forEach(clientReason => {
      clientReason.text = true
      clientReason.outlined = false
    });
    this.clientReasons[index].text = false;
    this.clientReasons[index].outlined = true;

    this.note.clientReason = clientReason;
    this.note.status = this.clientReasons[index].status;
  }

  onNoteClick(event: MouseEvent, op: any, notes: Note[]): void {
    event.stopPropagation(); // Prevent the click event from propagating
    if (notes.length > 1) {
      op.toggle(event);
      this.selectedPacketNotes = notes;
    }
  }

  clearAllSelectedStatus(){
    this.nonConfirmedOptionsValue=[];
    this.endedOptionsValue=[];
    this.canceledOptionsValue = [];
    this.enCoursOptionsValue = [];
  }

  onEditInit($event: any): void {
    //console.log("onEditInit", $event);

    this.selectedField = $event.field;
    this.oldFieldValue = $event.data[this.selectedField];

    if (this.selectedField === 'status') {
      this.packetStatusList = [];

      if ($event.data.stock === -1) {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: "Please fill in all article fields" });
        this.packetStatusList = [UNREACHABLE];
      } else if ([NOT_CONFIRMED, NOT_SERIOUS, UNREACHABLE, CANCELED, DELETED, OOS].includes(this.oldFieldValue)) {
        this.packetStatusList = [NOT_CONFIRMED, OOS, CONFIRMED, NOT_SERIOUS, UNREACHABLE, CANCELED];
      } else if ([CONFIRMED, TO_VERIFY].includes(this.oldFieldValue)) {
        this.packetStatusList = [IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3, CANCELED, TO_VERIFY, DELIVERED, RETURN, PAID, RETURN_RECEIVED, PROBLEME];
      } else if ([IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3].includes(this.oldFieldValue)) {
        this.packetStatusList = [UNREACHABLE, IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3, TO_VERIFY, DELIVERED, RETURN, PAID, RETURN_RECEIVED, PROBLEME];
      } else if ([DELIVERED, PAID].includes(this.oldFieldValue)) {
        this.packetStatusList = [PAID, RETURN, RETURN_RECEIVED];
      } else if (this.oldFieldValue === RETURN) {
        this.packetStatusList = [PROBLEME, RETURN_RECEIVED];
      } else if (this.oldFieldValue === PROBLEME) {
        this.packetStatusList = [RETURN_RECEIVED, DELIVERED, PAID, IN_PROGRESS_2, CANCELED];
      }
    }
  }

  private handleStatusField(packet: any): void {
    const status = packet[this.selectedField];
    const barcode = packet.barcode;
    let errorMessage: string | undefined = undefined;

    switch (status) {
      case DELETED:
        if (barcode != null && barcode !== "") {
          errorMessage = 'Please do not delete outgoing packets';
        }
        break;
      case CANCELED:
        if (this.oldFieldValue !== CONFIRMED && this.oldFieldValue !== TO_VERIFY && this.oldFieldValue !== DELETED) {
          errorMessage = 'Please do not cancel outgoing packets';
        }else this.addAttempt(packet, 'DELETED');
        break;
      case NOT_CONFIRMED:
      case OOS:
      case NOT_SERIOUS:
        if (this.oldFieldValue === IN_PROGRESS_1 || this.oldFieldValue === IN_PROGRESS_2 || this.oldFieldValue === IN_PROGRESS_3) {
          errorMessage = 'This packet is already in progress';
        }
        break;
      case IN_PROGRESS_1:
      case IN_PROGRESS_2:
      case IN_PROGRESS_3:
      case CANCELED:
      case DELIVERED:
      case RETURN:
      case RETURN_RECEIVED:
      case PAID:
        if (barcode == null || barcode === "") {
          errorMessage = "This packet has not been dispatched yet";
        }
        break;
      case UNREACHABLE:
        {
          if (barcode != null && barcode !== "") {
          errorMessage = 'This packet is already completed';
        }}
        break;
      case CONFIRMED:
        if (!this.checkPacketValidity(packet)) {
          errorMessage = 'Packet validation failed';
        } else if (!this.checkPacketDescription(packet)) {
          errorMessage = 'Please enter the size of the item';
        }
        break;
      default:
        break;
    }

    if (errorMessage) {
      packet[this.selectedField] = this.oldFieldValue;
      this.messageService.add({ severity: 'error', summary: 'Error', detail: errorMessage });
      this.loading = false;
      return;
    }

    this.patchPacketService(packet);

  }

  loadNotification(){
    this.statusItems= [
      {
        label: "Tous",
        title: "Tous",
        icon: 'pi pi-align-justify',
        color: 'green',
        badge:this.statusItems[0].badge,
        badgeByDate:this.statusItems[0].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:"All", detail: event.item.label});
          //this.onNotificationClick(event.item.title);
          this.onActiveIndexChange(event.index);
        },
        disabled:true
      },
      {
        label: OOS+"("+this.statusItems[1].badge+")",
        title: OOS,
        icon: 'pi pi-times',
        color: 'red',
        badge:this.statusItems[1].badge,
        badgeByDate:this.statusItems[1].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:OOS, detail: event.item.label});
          //this.onActiveIndexChange(event.index);
        }
      },
      {
        label: NOT_CONFIRMED+"("+this.statusItems[2].badge+")",
        title: NOT_CONFIRMED,
        icon: 'pi pi-phone',
        color: 'orange',
        badge:this.statusItems[2].badge,
        badgeByDate:this.statusItems[2].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:NOT_CONFIRMED, detail: event.item.label});
          //this.onActiveIndexChange(event.index);
        }
      },
      {
        label: CONFIRMED+"("+this.statusItems[3].badge+")",
        title: CONFIRMED,
        icon: 'pi pi-check',
        color: 'green',
        badge:this.statusItems[3].badge,
        badgeByDate:this.statusItems[3].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:NOT_CONFIRMED, detail: event.item.label});
          //this.onActiveIndexChange(event.index);
        }
      },
      {
        label: IN_PROGRESS+"("+this.statusItems[4].badge+")",
        title: IN_PROGRESS,
        icon: 'pi pi-truck',
        color: 'purple',
        badge:this.statusItems[4].badge,
        badgeByDate:this.statusItems[4].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:IN_PROGRESS, detail: event.item.label});
          //this.onActiveIndexChange(event.index);
        },
      },
      {
        label: RETURN+"("+this.statusItems[5].badge+")",
        title: RETURN,
        icon: 'pi pi-thumbs-down',
        color: 'red',
        badge:this.statusItems[5].badge,
        badgeByDate:this.statusItems[5].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:RETURN, detail: event.item.label});
          //this.onActiveIndexChange(event.index);
          }
      },
      {
        label: CANCELED+"("+this.statusItems[6].badge+")",
        title: CANCELED,
        icon: 'pi pi-thumbs-down',
        color: 'red',
        badge:this.statusItems[6].badge,
        badgeByDate:this.statusItems[6].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:RETURN, detail: event.item.label});
          //this.onActiveIndexChange(event.index);
          }
      },
      {
        label: 'Terminé',
        title: 'Terminé',
        icon: 'pi pi-flag',
        color: 'red',
        badge:this.statusItems[7].badge,
        badgeByDate:this.statusItems[7].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:'Last Step', detail: event.item.label})
        }
      }
  ];
  }

  createNotification(): void {
    if(this.showStatus){
      this.statusItems[1].badge = 0;
      this.statusItems[1].badgeByDate = 0;
      this.statusItems[2].badge = 0;
      this.statusItems[2].badgeByDate = 0;
      this.statusItems[3].badge = 0;
      this.statusItems[3].badgeByDate = 0;
      this.statusItems[4].badge = 0;
      this.statusItems[4].badgeByDate = 0;
      this.statusItems[5].badge = 0;
      this.statusItems[5].badgeByDate = 0;
      this.statusItems[6].badge = 0;
      this.statusItems[6].badgeByDate = 0;
      this.statusItems[7].badge = 0;
      this.statusItems[7].badgeByDate = 0;
      let all = 0;

    this.packetService.syncNotification(this.params.startDate,this.params.endDate)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: DashboardCard[]) => {
          if (response != null && response.length > 0) {
            response.forEach((element: any) => {

              switch (element.status) {
                case OOS:
                  this.statusItems[1].badge = element.statusCount;
                  this.statusItems[1].badgeByDate = element.statusByDateCount;
                  break;
                case NOT_CONFIRMED:
                case UNREACHABLE:
                  this.statusItems[2].badge += element.statusCount;
                  this.statusItems[2].badgeByDate += element.statusByDateCount;
                  break;
                case CONFIRMED:
                  this.statusItems[3].badge = element.statusCount;
                  this.statusItems[3].badgeByDate += element.statusByDateCount;
                  break;
                case IN_PROGRESS_1:
                case IN_PROGRESS_2:
                case IN_PROGRESS_3:
                case TO_VERIFY:
                  this.statusItems[4].badge += element.statusCount;
                  this.statusItems[4].badgeByDate += element.statusByDateCount;
                  break;
                case RETURN:
                  this.statusItems[5].badge = element.statusCount;
                  this.statusItems[5].badgeByDate = element.statusByDateCount;
                  break;
                case CANCELED:
                case DELETED:
                  this.statusItems[6].badge += element.statusCount;
                  this.statusItems[6].badgeByDate += element.statusByDateCount;
                  break;
                case DELIVERED:
                case PAID:
                case RETURN_RECEIVED:
                  this.statusItems[7].badge += element.statusCount;
                  this.statusItems[7].badgeByDate += element.statusByDateCount;
                  break;

              }
              all += element.statusCount;
            });
          }
          this.statusItems[0].badge = all;


          this.loadNotification();
        },
        error: (error: Error) => {
          console.log('Error:', error);
        }
      });
    }

  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }

  //intiator

  openContextMenu(event:any,packet:any, contextMenu:ContextMenu) {
    event.preventDefault();
    this.selectedPacket = packet;
    console.log("pp",packet);
    this.contextMenu.target = event.currentTarget;
    if (contextMenu) {
      this.optionButtons = [
        {
          label: 'Duplicate',
          icon: 'pi pi-refresh',
          disabled:!( packet.status == DELIVERED || packet.status == PAID ),
          command: () => {
            this.duplicatePacket(packet)
          }
        },
        {
          label: 'Ajouter tentative',
          icon: 'pi pi-refresh',
          disabled:packet.status!=UNREACHABLE,
          command: () => {
            this.addAttempt(packet, 'UNREACHABLE')
          }
        },
        {
          label: 'Chercher Tel',
          icon: 'pi pi-phone',
          disabled:!this.checkPhoneNbExist(packet),
          command: () => {
            this.filter= packet.customerPhoneNb;
            this.filterPackets('phone');
          }
        },
        {
          label: 'History',
          icon: 'pi pi-history',
          disabled:false,
          command: () => {
            this.showTimeLineDialog(packet);
          }
        },
        { separator: true },
        {
          label: 'Actualiser status',
          icon: 'pi pi-sync',
          disabled:!(this.checkCodeABarreExist(packet)),
          command: () => {
            this.getLastStatus(packet);
          }
        },
        {
          label: 'Tel',
          icon: 'pi pi-search-plus',
          disabled:!this.checkPhoneNbExist(packet),
          command: () => {
            this.openLinkGetter(packet.customerPhone,packet.deliveryCompany);
          }
        },
        {
          label: 'BarreCode',
          icon: 'pi pi-qrcode',
          disabled:!this.checkCodeABarreExist(packet),
          command: () => {
            this.openLinkGetter(packet.barcode,packet.deliveryCompany)
          }
        },
        {
          label: 'Print',
          icon: 'pi pi-print',
          disabled:!(this.checkCodeABarreExist(packet)),
          command: () => {
            this.printFirst(packet.printLink)
          }
        }
      ];
      //contextMenu.show(event);
    }
  }

  /*   this.contextMenu.target = $event.currentTarget;
  if(this.contextMenu != null && this.contextMenu != undefined){
    console.log(this.contextMenu);

    this.contextMenu.show(event);
  } */

  enCoursOptionsValue !:any;
  enCoursOptions: any[] = [
    { name: '1' , value:IN_PROGRESS_1},
    { name: '2' , value:IN_PROGRESS_2},
    { name: '3' , value:IN_PROGRESS_3},
    { name: TO_VERIFY , value:TO_VERIFY}
  ];

  canceledOptionsValue !:any;
  canceledOptions: any[] = [
    { name: CANCELED , value:CANCELED},
    { name: DELETED , value:DELETED}
  ];

  nonConfirmedOptionsValue !:any;
  nonConfirmedOptions: any[] = [
      { name: NOT_CONFIRMED , value:NOT_CONFIRMED},
      { name: UNREACHABLE , value:UNREACHABLE},
  ];

  endedOptionsValue !:any;
  endedOptions: any[] = [
      { name: DELIVERED , value:DELIVERED},
      { name: PAID , value:PAID},
      { name: RETURN_RECEIVED , value:RETURN_RECEIVED}
  ];

}

  /*private updatePacket(packet: any): void {
    this.packetService.updatePacket(packet)
      .pipe(
        catchError((err: any, caught: Observable<any>): Observable<any> => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'An error occurred while updating ' + err.error.message });
          const packetIndex = this.packets.findIndex((p: any) => p.id === packet.id);
          if (packetIndex !== -1) {
            this.packets[packetIndex][this.selectedField] = this.oldFieldValue;
          }
          this.loading = false;
          return of();
        })
      )
      .subscribe(() => {
        this.loading = false;
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'The packet is successfully updated' });
      });
  }
  download(text: any, filename: any) {
    let element = document.createElement('a');
    element.setAttribute(
      'href',
      'data:text/csv;charset=utf-8,' + encodeURIComponent(text)
    );
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
  }

  selectCity(packet: Packet) {
    this.selectedCity = packet.city;
  }
  selectPhoneNumber(packet: any) {
    this.selectedPhoneNumber = packet.customerPhoneNb;
  }

  onActiveIndexChange(event: any) {

    console.log(event, this.canceledOptionsValue);
    this.activeIndex = event;

    if (this.statusItems[event].title == IN_PROGRESS) {
      if (this.enCoursOptionsValue != undefined && this.enCoursOptionsValue.length > 0)
        this.selectedStatus.patchValue(this.enCoursOptionsValue);
      else
        this.selectedStatus.patchValue([TO_VERIFY, IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3]);
    }
    else if (this.statusItems[event].title == NOT_CONFIRMED) {
      if (this.nonConfirmedOptionsValue != undefined && this.nonConfirmedOptionsValue.length > 0)
        this.selectedStatus.patchValue(this.nonConfirmedOptionsValue);
      else
        this.selectedStatus.patchValue([NOT_CONFIRMED, UNREACHABLE]);
    }
    else if (this.statusItems[event].title == CANCELED) {
      if (this.canceledOptionsValue != undefined && this.canceledOptionsValue.length > 0)
        this.selectedStatus.patchValue(this.canceledOptionsValue);
      else
        this.selectedStatus.patchValue([CANCELED, DELETED]);
    }

    else if (this.statusItems[event].title == "Terminé") {
      if (this.endedOptionsValue != undefined && this.endedOptionsValue.length > 0)
        this.selectedStatus.patchValue(this.endedOptionsValue);
      else
        this.selectedStatus.patchValue([DELIVERED, PAID, RETURN_RECEIVED]);
    }
    else {
      this.selectedStatus.setValue([]);
      this.selectedStatus.patchValue([this.statusItems[event].title]);
    }
    //console.log("filterPackets-onActiveIndexChange",this.params);
    this.filterPackets('status');
  }

  showStatusButton() {
    this.showStatus = !this.showStatus;
  }

  getReasonOptionsByStatus(status: string) {
    return (Object.keys(ClientReason) as (keyof typeof ClientReason)[])
      .filter(key => isNaN(Number(key)) && ClientReasonDetails[ClientReason[key]].status === status)  // Filter out any non-number keys
      .map(key => {
        const reasonKey = ClientReason[key];
        const clientReasonDetails = ClientReasonDetails[reasonKey];
        return {
          value: key,
          label: clientReasonDetails.label,
          description: clientReasonDetails.description,
          status: clientReasonDetails.status,
          text: clientReasonDetails.text,
          outlined: clientReasonDetails.outlined,
          severity: clientReasonDetails.severity,
        }
      })
  }

  onSelectReason(clientReason: ClientReason, index: number) {
    this.explanationTitle = this.clientReasons[index]?.description;
    if (this.explanationElement && this.explanationElement.nativeElement) {
      setTimeout(() => {
        this.explanationElement.nativeElement.focus();
      }, 0);
    }
    this.clientReasons.forEach(clientReason => {
      clientReason.text = true
      clientReason.outlined = false
    });
    this.clientReasons[index].text = false;
    this.clientReasons[index].outlined = true;

    this.note.clientReason = clientReason;
    this.note.status = this.clientReasons[index].status;
  }

  onNoteClick(event: MouseEvent, op: any, notes: Note[]): void {
    event.stopPropagation(); // Prevent the click event from propagating
    if (notes.length > 1) {
      op.toggle(event);
      this.selectedPacketNotes = notes;
    }
  }
}
/*notificationList : any [] = [
  {
    class:'pi-check-circle',
    severity:'info',
    status: CONFIRMEE,
    count: this.nbrConfirmed
  },
  {
    class:'pi-question-circle',
    severity:'info',
    status: A_VERIFIER,
    count: '0'
  },
  {
    class:'pi-power-off',
    severity:'warning',
    status: UNREACHABLE,
    count: '0'
  },
  {
    class:'pi-phone',
    severity:'danger',
    status: NOT_CONFIRMED,
    count: '0'
  }
];*/

/*exportCSV() {
    let packets: any[] = [];
    let selectedPackets = this.selectedPackets.map((p) => p.id);
    packets = this.packets.slice();
    if (this.filter != '' && this.filter != null)
      packets = this.dt!.filteredValue;
    if (this.selectedPackets.length > 0) {
      let filteredPackets = packets.filter(
        (packet) => selectedPackets.indexOf(packet.id) > -1
      );
      if (filteredPackets.length > 0) packets = filteredPackets.slice();
    }

    // map the packets to a customer packetList
    packets = packets?.map(
      (packet: any) =>
      (packet = {
        destinataire_nom: this.getValue(packet.customerName),
        adresse: this.getValue(packet.address).replace(this.regBS, ' '),
        ville: this.getValue(packet.city?.name),
        gouvernorat: this.getValue(packet.city?.governorate.name),
        telephone: this.getPhoneNumber1(packet.customerPhoneNb),
        telephone2: this.getPhoneNumber2(packet.customerPhoneNb),
        nombre_de_colis: 1,
        prix: this.getValue(packet.price-packet.discount+packet.deliveryPrice),
        designation:
          this.getValue(packet.id) +
          ' ' +
          this.getValue(packet.fbPage?.name) +
          ' | ' +
          this.getValue(packet.packetDescription?.replace(this.regBS, ', ')),
        commentaire:
          'Le colis peut etre ouvert lors de la commande du client',
        barcode: this.getValue(packet.barcode),
      })
    );

    // prepare the columns to be exported
    let cols: any[] = [
      { field: 'destinataire_nom', header: 'destinataire_nom' },
      { field: 'adresse', header: 'adresse' },
      { field: 'ville', header: 'ville' },
      { field: 'gouvernorat', header: 'gouvernorat' },
      { field: 'telephone', header: 'telephone' },
      { field: 'telephone2', header: 'telephone2' },
      { field: 'nombre_de_colis', header: 'nombre_de_colis' },
      { field: 'prix', header: 'prix' },
      { field: 'designation', header: 'designation' },
      { field: 'commentaire', header: 'commentaire' },
      { field: 'barcode', header: 'Barcode' },
    ];
    let csv = '';
    let csvSeparator = ';';
    //headers
    for (let i = 0; i < cols.length; i++) {
      if (cols[i].field) {
        csv += cols[i].field;

        if (i < cols.length - 1) {
          csv += csvSeparator;
        }
      }
    }
    //body
    packets?.forEach((record: any, j) => {
      csv += '\n';
      for (let i = 0; i < cols.length; i++) {
        if (cols[i].field) {
          //console.log(record[cols[i].field]);
          // resolveFieldData seems to check if field is nested e.g. data.something --> probably not needed
          csv += record[cols[i].field]; //this.resolveFieldData(record, this.columns[i].field);
          if (i < cols.length - 1) {
            csv += csvSeparator;
          }
        }
      }
    });
    this.download(csv, 'first - ' + this.dateUtils.formatDateToString(this.today));
  }*/

/*onNotificationClick($event?: string): void{
  console.log("aaaa",$event);
  //this.createNotification();
  this.selectedStatus.setValue([]);
  this.selectedStatus.patchValue([$event]);
  console.log("filterPackets-onNotificationClick");
  this.filterPackets('status');
}*/

/*onStateChange(): void {
  this.selectedStatus.setValue([]);
  //this.packetStatusList = [];
  if (this.selectedStates.indexOf(CORBEIL) > -1) {
    this.selectedStatus.patchValue([DELETED]);
  }

  if (this.selectedStates.indexOf(BUREAU) > -1) {
    this.selectedStatus.patchValue([ NOT_CONFIRMED, OOS, A_VERIFIER, CONFIRMEE ]);
  }
  if (this.selectedStates.indexOf(IN_PROGRESS) > -1) {
    this.selectedStatus.patchValue([ A_VERIFIER, IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3]);
    //this.packetStatusList = this.statusList;
  }
  if (this.selectedStates.indexOf(TERMINE) > -1) {
    this.selectedStatus.patchValue([PAID, RETURN_RECEIVED]);
  }
}*/
/*exportExcel() {
console.log(this.dt?._totalRecords);
console.log(this.dt?.totalRecords);
let packets: any[] = [];
let selectedPackets = this.selectedPackets.map((p) => p.id);
packets = this.packets.slice();
if (this.filter != '' && this.filter != null)
  packets = this.dt!.filteredValue;
if (this.selectedPackets.length > 0) {
  let filteredPackets = packets.filter(
    (packet) => selectedPackets.indexOf(packet.id) > -1
  );
  if (filteredPackets.length > 0) packets = filteredPackets.slice();
}

packets = packets?.map(
  (packet: any) =>
  (packet = {
    Id: packet.id,
    Prix: packet.price + packet.deliveryPrice - packet.discount,
    Références: packet.packetDescription,
    PageFB: packet.fbPage?.name,
  })
);
import('xlsx').then((xlsx) => {
  const worksheet = xlsx.utils.json_to_sheet(packets);
  const workbook = { Sheets: { data: worksheet }, SheetNames: ['data'] };
  const excelBuffer: any = xlsx.write(workbook, {
    bookType: 'xlsx',
    type: 'array',
  });
  this.saveAsExcelFile(excelBuffer, 'products');
});
}

/*   saveAsExcelFile(buffer: any, fileName: string): void {
let EXCEL_TYPE =
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
let EXCEL_EXTENSION = '.xlsx';
const data: Blob = new Blob([buffer], {
  type: EXCEL_TYPE,
});
FileSaver.saveAs(
  data,
  fileName + ' - ' + this.dateUtils.formatDateToString(new Date()) + EXCEL_EXTENSION
);
} */
