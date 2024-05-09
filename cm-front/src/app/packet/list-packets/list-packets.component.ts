
import { ConfirmationService, MessageService, SelectItemGroup, PrimeIcons, MenuItem } from 'primeng/api';
import { Packet } from '../../../shared/models/Packet';
import { OfferService } from '../../../shared/services/offer.service';
import { PacketService } from '../../../shared/services/packet.service';
import { DatePipe } from '@angular/common';
import { Table } from 'primeng/table';
import { CityService } from '../../../shared/services/city.service';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from '../../../shared/services/fb-page.service';
import { catchError, identity, Observable, of, Subject,takeUntil} from 'rxjs';
import { Offer } from 'src/shared/models/Offer';
import { FormControl } from '@angular/forms';
import { StorageService } from 'src/shared/services/strorage.service';
import { DateUtils } from 'src/shared/utils/date-utils';
import { A_VERIFIER, BUREAU, CANCELED, CONFIRMEE, CORBEIL, ENDED, EN_COURS, EN_COURS_1, EN_COURS_2,
  EN_COURS_3, INJOIGNABLE, LIVREE, NON_CONFIRMEE, NOT_SERIOUS, PAYEE, PROBLEME, RETOUR, RETOUR_RECU,
  DELETED, TERMINE, statesList, statusList } from 'src/shared/utils/status-list';
import { City } from 'src/shared/models/City';
import { ResponsePage } from 'src/shared/models/ResponsePage';
import { DashboardCard } from 'src/shared/models/DashboardCard';
import { firstUrl } from 'src/assets/constants';
import * as FileSaver from 'file-saver';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { GlobalConfService } from 'src/shared/services/global-conf.service';
import { GlobalConf } from 'src/shared/models/GlobalConf';
import { AfterViewChecked, Component, EventEmitter, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';

@Component({
  selector: 'app-list-packets',
  templateUrl: './list-packets.component.html',
  styleUrls: ['./list-packets.component.css'],
  providers: [DatePipe],
})
export class ListPacketsComponent implements OnInit, AfterViewChecked, OnDestroy {


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
  submitted!: boolean;

  first = 0;
  rows = 100;
  currentPage = 0;
  oldField: string = "";
  offersList: any[] = [];
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
  pageSize : number = 100;
  params : any={
    page: 0,
    size: this.pageSize,
    startDate: this.dateUtils.formatDateToString(this.today),
    endDate: this.dateUtils.formatDateToString(this.today),
    mandatoryDate: false
  };
  loading: boolean = false;
  mandatoryDateCheckBox : boolean = false;
  oldDateFilterCheckBox : boolean = false;
  dateOptions : any[] = [{label: 'Off', value: false}, {label: 'On', value: true}];
  value : boolean = this.mandatoryDateCheckBox;
  nbrConfirmed : number= 0;
  countInjoignable :string = "0";

  items: any[] = [
    {
      label: "Erreur Chargement",
      title: "Tous",
      badge:0,
      command: (event: any) => {
      },
      disabled:true
  },
  {
    label: ENDED,
    title: ENDED,
    badge:0,
    command: (event: any) => {
    }
  },
    {
      label: NON_CONFIRMEE,
      title: NON_CONFIRMEE,
      badge:0,
      command: (event: any) => {
      }
  },
    {
        label: INJOIGNABLE,
        title: INJOIGNABLE,
        icon: 'pi-power-off',
        badge:0,
        command: (event: any) => {
        }
    },
    {
        label: CONFIRMEE,
        title: CONFIRMEE,
        badge:0,
        command:
          (event: any) => {
        }
    },
    {
      label: EN_COURS,
      title: EN_COURS,
      badge:0,
      command: (event: any) => {
      },
    },
    {
      label: A_VERIFIER,
      title: A_VERIFIER,
      badge:0,
      command: (event: any) => {
        }
    },
    {
      label: RETOUR,
      title: RETOUR,
      badge:0,
      command: (event: any) => {
        }
    },
    {
      label: 'Terminé',
      title: 'Terminé',
      badge:0,
      command: (event: any) => {}
    }
];
  showStatus: boolean = false;
  activeIndex: number = 2;
  oldActiveIndex:number = 2;

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
      status: INJOIGNABLE,
      count: '0'
    },
    {
      class:'pi-phone',
      severity:'danger',
      status: NON_CONFIRMEE,
      count: '0'
    }
  ];*/

  @ViewChild('dt') dt: Table;
  private readonly reg: RegExp = /,/gi;
  regBS = /\n/gi;
  private readonly FIRST: string = 'FIRST';
  countDeleted: number = 0;
  realTotalItems: number;
  selectedPhoneNumber: string = '';
  deliveryCompanyName?: DeliveryCompany;
  @Output() confirmEvent: EventEmitter<string> = new EventEmitter<string>();
  visibleNote: boolean = false;
  note: string = '';
  enCoursOptionsValue !:any;
  enCoursOptions: any[] = [
      { name: '1' , value:EN_COURS_1},
      { name: '2' , value:EN_COURS_2},
      { name: '3' , value:EN_COURS_3},
      { name: A_VERIFIER , value:A_VERIFIER}
  ];

  nonConfirmedOptionsValue !:any;
  nonConfirmedOptions: any[] = [
      { name: NON_CONFIRMEE , value:NON_CONFIRMEE},
      { name: INJOIGNABLE , value:INJOIGNABLE},
  ];

  endedOptionsValue !:any;
  endedOptions: any[] = [
      { name: LIVREE , value:LIVREE},
      { name: PAYEE , value:PAYEE},
      { name: RETOUR_RECU , value:RETOUR_RECU}
  ];

  activeClass: boolean;
  userName: string;
  isLoggedIn: boolean;
  isAdmin: boolean;
  isSuperAdmin: boolean;
  globalConf: GlobalConf = {
    applicationName: ""
  };
  meterGroupValue= [
    { label: 'Space used', value: 15, color: '#34d399' }
  ];

  constructor(
    private messageService: MessageService,
    private packetService: PacketService,
    private confirmationService: ConfirmationService,
    private offerService: OfferService,
    private cityService: CityService,
    private fbPageService: FbPageService,
    private dateUtils: DateUtils,
    private globalConfService: GlobalConfService,
    public storageService: StorageService
    ) {//private cdRef: ChangeDetectorRef,
    this.statusList = statusList;
    this.statesList = statesList;
  }

  ngAfterViewChecked() {
    //this.cdRef.detectChanges();
  }



  ngOnInit(): void {
    this.storageService.isLoggedIn.subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
      this.userName = this.storageService.getUserName();
      this.isAdmin = this.storageService.hasRoleAdmin();
      this.isSuperAdmin = this.storageService.hasRoleSuperAdmin();
      this.activeClass = true;
      this.globalConfService.getGlobalConf().subscribe((globalConf: GlobalConf) => {
        if(globalConf)
          this.globalConf = {...globalConf};
        this.deliveryCompanyName = this.globalConf.deliveryCompany;
      });
    });
    //this.createNotification();
    this.createColumns();
    this.findAllOffers();
    this.findAllGroupedCities();
    this.findAllFbPages();
    this.rangeDates = [this.today];
    this.onActiveIndexChange(2);
    //this.onNotificationClick(NON_CONFIRMEE);
    //this.onActiveIndexChange(0);

    this.selectedStatus.setValue([]);
    //this.loadNotification();
    //this.getGlobalConf();

  }

  loadNotification(){
    this.items= [
      {
        label: "Tous",
        title: "Tous",
        icon: 'pi pi-align-justify',
        color: 'green',
        badge:this.items[0].badge,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:"All", detail: event.item.label});
          //this.onNotificationClick(event.item.title);
          this.onActiveIndexChange(event.index);
        },
        disabled:true
      },
      {
        label: ENDED+"("+this.items[1].badge+")",
        title: ENDED,
        icon: 'pi pi-times',
        color: 'red',
        badge:this.items[1].badge,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:ENDED, detail: event.item.label});
          //this.onActiveIndexChange(event.index);
        }
      },
      {
        label: NON_CONFIRMEE+"("+this.items[2].badge+")",
        title: NON_CONFIRMEE,
        icon: 'pi pi-phone',
        color: 'orange',
        badge:this.items[2].badge,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:NON_CONFIRMEE, detail: event.item.label});
          //this.onActiveIndexChange(event.index);
        }
      },
      {
          label: CONFIRMEE+"("+this.items[3].badge+")",
          title: CONFIRMEE,
          icon: 'pi pi-thumbs-up',
          color: 'green',
          badge:this.items[3].badge,
          command:
            (event: any) => {
              this.messageService.add({severity:'info', summary:CONFIRMEE, detail: event.item.label});
              //this.onActiveIndexChange(event.index);
          }
      },
      {
        label: EN_COURS+"("+this.items[4].badge+")",
        title: EN_COURS,
        icon: 'pi pi-truck',
        color: 'purple',
        badge:this.items[4].badge,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:EN_COURS, detail: event.item.label});
          //this.onActiveIndexChange(event.index);
        },
      },
      {
        label: RETOUR+"("+this.items[5].badge+")",
        title: RETOUR,
        icon: 'pi pi-thumbs-down',
        color: 'red',
        badge:this.items[5].badge,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:RETOUR, detail: event.item.label});
          //this.onActiveIndexChange(event.index);
          }
      },
      {
        label: 'Terminé',
        title: 'Terminé',
        icon: 'pi pi-flag',
        color: 'red',
        badge:this.items[6].badge,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:'Last Step', detail: event.item.label})

          //this.onActiveIndexChange(event.index);
        }
      }
  ];
  }
 /* getGlobalConf() {
    this.globalConfService.getGlobalConf.subscribe((globalConf :GlobalConf) => {
      this.deliveryCompanyName = globalConf.deliveryCompany;
    });
  }*/
  findAllFbPages(): void {
    console.log("findAllFbPages");

    this.fbPageService.findAllFbPages().subscribe((result: any) => {
      this.fbPages = result.filter((fbPage: any) => fbPage.enabled);
    });
  }

  createNotification(): void {
    console.log("createNotification");
    let enCours =0;
    let nonConfirmed =0;
    let closed =0;
    let all =0;
    this.packetService.syncNotification()
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: DashboardCard[]) => {
          if (response != null && response.length > 0) {
            response.forEach((element: any) => {
              if(element.status == ENDED) this.items[1].badge = element.statusCount;
              else if(element.status == NON_CONFIRMEE) nonConfirmed += element.statusCount;
              else if(element.status == INJOIGNABLE) nonConfirmed += element.statusCount;
              else if(element.status == CONFIRMEE) this.items[3].badge = element.statusCount;
              else if(element.status == EN_COURS_1) enCours += element.statusCount;
              else if(element.status == EN_COURS_2) enCours += element.statusCount;
              else if(element.status == EN_COURS_3) enCours += element.statusCount;
              else if(element.status == A_VERIFIER) enCours += element.statusCount;
              else if(element.status == RETOUR) this.items[5].badge = element.statusCount;
              else if(element.status == LIVREE) closed += element.statusCount;
              else if(element.status == PAYEE) closed += element.statusCount;
              else if(element.status == RETOUR_RECU) closed += element.statusCount;
              all += element.statusCount;
            });
          }
          this.items[0].badge = all;
          this.items[2].badge = nonConfirmed;
          this.items[4].badge = enCours;
          this.items[6].badge = closed;

          this.loadNotification();
        },
        error: (error: Error) => {
          console.log('Error:', error);
        }
      });

  }

  findAllPackets(): void {
    console.log("findAllPackets",this.params);

    this.loading = true;
    this.packetService.findAllPackets(this.params)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: ResponsePage) => {

          this.packets = response.result.filter((packet: any) => this.checkPacketNotNull(packet));
          this.realTotalItems = response.totalItems;
          this.totalItems = this.packets.length;
          let countConfirmed =response.result.filter(packet => packet.status === CONFIRMEE).length;

          this.items[2].badge = countConfirmed > 0 ? countConfirmed:0;
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

  findAllOffers(): void {

    this.offerService.findAllOffers().subscribe((offers: any) => {
      this.allOffersList = offers;
      this.offersList = offers.filter((offer: Offer) => offer.enabled);
    });
  }

  findAllGroupedCities(): void {
    this.cityService.findAllGroupedCities().subscribe((groupedCities: any) => {
      this.groupedCities = this.cityService.adaptListToDropDown(groupedCities);
      this.groupedCities = [...new Set(this.groupedCities)];
    });
  }



  onEditInit(packet: any): void {
    this.oldField = packet.data[packet.field];
    if ( packet.field == 'status'){
      this.packetStatusList = [];
      if(packet.data.stock === -1){
        this.messageService.add({ severity: 'error',summary: 'Error', detail: "Veiller remplir tous les champs des articles" });
        this.packetStatusList = [INJOIGNABLE]
      }
           else if (this.oldField == NON_CONFIRMEE || this.oldField == NOT_SERIOUS || this.oldField == INJOIGNABLE
        || this.oldField == CANCELED || this.oldField == DELETED|| this.oldField == ENDED ) {

        this.packetStatusList = [ NON_CONFIRMEE, ENDED, CONFIRMEE, NOT_SERIOUS, INJOIGNABLE, CANCELED];
      }
      else if (this.oldField == CONFIRMEE || this.oldField == A_VERIFIER) {
        this.packetStatusList = [ EN_COURS_1, EN_COURS_2 ,EN_COURS_3, CANCELED, A_VERIFIER, LIVREE, RETOUR, PAYEE, RETOUR_RECU, PROBLEME];
      }

      else if (this.oldField == EN_COURS_1 || this.oldField == EN_COURS_2 || this.oldField ==  EN_COURS_3) {
        this.packetStatusList = [ INJOIGNABLE, EN_COURS_1, EN_COURS_2 ,EN_COURS_3, A_VERIFIER, LIVREE, RETOUR, PAYEE,RETOUR_RECU, PROBLEME];
      }
      else if (this.oldField == LIVREE || this.oldField == PAYEE) {
        this.packetStatusList = [ PAYEE, RETOUR ,RETOUR_RECU];
      }
      else if (this.oldField == RETOUR) {
        this.packetStatusList = [ PROBLEME ,RETOUR_RECU];
      }
      else if (this.oldField == PROBLEME) {
        this.packetStatusList = [ RETOUR_RECU ,LIVREE ,PAYEE ,EN_COURS_2 ,CANCELED ];
      }
    }
  }

  onEditComplete($event: any): void {
    console.log("$event00:",$event);
    this.loading = true
    //setTimeout(() => , 0);
    try
    {
      //console.log("this.selectedPhoneNumber0",this.selectedPhoneNumber);
      if(($event.data.customerPhoneNb == null || $event.data.customerPhoneNb == '') && this.selectedPhoneNumber != '' && $event.field == 'customerPhoneNb'){
        $event.data.customerPhoneNb= this.selectedPhoneNumber;
          this.selectedPhoneNumber = '';
        }
      if(($event.data.city == null || $event.data.city == '') && this.selectedCity != undefined && $event.field == 'city'){
        $event.data.city= this.selectedCity;
          this.selectedCity = undefined;
      }


      if (this.oldField !== $event.data[$event.field] && $event.data[$event.field] != undefined ){

        if ( $event.field == 'status') {
          console.log("edit status start");
          if($event.data[$event.field] == DELETED && $event.data.barcode != null && $event.data.barcode != "" ){
            this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Veuillez ne pas supprimée les packets sorties' });
            $event.data[$event.field] = this.oldField;
            console.log("false0");
            this.loading = false;
            return;
          }

          if($event.data[$event.field] == CANCELED && this.oldField != CONFIRMEE && this.oldField != A_VERIFIER && this.oldField != DELETED){
            this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Veuillez ne pas annuler que les packets sorties' });
            $event.data[$event.field] = this.oldField;
            console.log("false1");
            this.loading = false;
            return;
          }
          if(($event.data[$event.field] == NON_CONFIRMEE
            || $event.data[$event.field] == ENDED
            || $event.data[$event.field] == NOT_SERIOUS
            ) && (
            this.oldField == EN_COURS_1
            || this.oldField == EN_COURS_2
            || this.oldField == EN_COURS_3
            )){
            this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Ce Colis est déja en cours' });
            $event.data[$event.field] = this.oldField;
            console.log("false2");
            this.loading = false;
            return;
          }

          if(($event.data[$event.field] == EN_COURS_1
            || $event.data[$event.field] == EN_COURS_2
            || $event.data[$event.field] == EN_COURS_3
            || $event.data[$event.field] == CANCELED
            || $event.data[$event.field] == LIVREE
            || $event.data[$event.field] == RETOUR
            || $event.data[$event.field] == RETOUR_RECU
            || $event.data[$event.field] == PAYEE
            ) && (
              $event.data.barcode == null || $event.data.barcode == ""
            )){
            this.messageService.add({ severity: 'error',summary: 'Error', detail: "Ce Colis n'a pas sorti" });
            $event.data[$event.field] = this.oldField;
            console.log("false3");
            this.loading = false;
            return;
          }

          if(($event.data[$event.field] == NON_CONFIRMEE
            || $event.data[$event.field] == ENDED
            || $event.data[$event.field] == NOT_SERIOUS
            || $event.data[$event.field] == NON_CONFIRMEE
            || $event.data[$event.field] == ENDED
            || $event.data[$event.field] == NOT_SERIOUS
            || $event.data[$event.field] == INJOIGNABLE
            ) && (
              this.oldField == LIVREE
            || this.oldField == RETOUR
            || this.oldField == RETOUR_RECU
            || this.oldField == PAYEE
            )){
            this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Ce Colis est déja Terminée' });
            $event.data[$event.field] = this.oldField;
            console.log("false4");
            this.loading = false;
            return;
          }

          if($event.data[$event.field] == CONFIRMEE){
            if (!this.checkPacketValidity($event.data)) {
              $event.data[$event.field] = this.oldField;
              console.log("false5");
              this.loading = false;
              return;
            }
            if (!this.checkPacketDescription($event.data)) {
              this.messageService.add({ severity: 'error',summary: 'Error', detail: "Veuillez saisir la taille de l'article" });
              $event.data[$event.field] = this.oldField;
              console.log("false6");
              this.loading = false;
              return;
            }
            //this.selectedPacket_id = $event['data'].id;

          }

        }
        else if ( $event.field == 'city' || $event.field == 'fbPage' || $event.field == 'date') {
            //console.log("edit city/page/date start:");
            if($event.field == 'date'){
              $event.data[$event.field].setHours($event.data[$event.field].getHours() + 1);
            }
            this.updatePacket($event.data);
            return;
        }
        //console.log("edit start2",packet);
        let updatedField = { [$event.field]: $event.data[$event.field] };
        let msg = 'Le champ a été modifié avec succés';

        this.packetService
          .patchPacket($event['data'].id, updatedField)
          .pipe(
            catchError((err: any, caught: Observable<any>): Observable<any> => {
              this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur lors de la mise à jour ' + err.error.message });
              $event.data[$event.field] = this.oldField;
              console.log("false7");
              this.isLoading = false;
              return of();
            })
          )
          .subscribe({
              next: (responsePacket: Packet) => {
                console.log("isloading",this.loading);
                if($event.field=="status")this.createNotification();
                console.log($event.data);
                if($event.data.stock < 10 && $event.field === 'status'
                 && (($event.data[$event.field] === CONFIRMEE && responsePacket.barcode != null)
                 || $event.data[$event.field] === CANCELED
                 || $event.data[$event.field] === RETOUR_RECU))
                 {
                   let x =this.getLastStock($event.data.id)
                 }

                if ($event.field === 'status' && $event.data[$event.field] === CONFIRMEE) {
                  console.log("status confirmée");


                  if (responsePacket.barcode != null) {
                      let pos = this.packets.map((packet: Packet) => packet.id).indexOf(responsePacket.id);
                      this.packets.splice(pos, 1, responsePacket);
                      msg = 'Le barcode a été crée avec succés';
                      this.items[2].badge +=1;
                  } else {
                    $event.data[$event.field] = this.oldField;
                    this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur lors de la creation du barcode' });
                  }

                }else if(responsePacket.oldClient!= undefined){
                  let pos = this.packets.map((packet: Packet) => packet.id).indexOf(responsePacket.id);
                    this.packets.splice(pos, 1, responsePacket);
                };
                if ($event.field === 'status' && $event.data[$event.field] === INJOIGNABLE){
                  this.addAttempt(responsePacket);
                }
                this.messageService.add({ severity: 'success', summary: 'Success', detail: msg });
                console.log("false8");

                this.loading = false;
                  },
                  error : (error: Error) => {
                    console.log(error);
                    this.loading = false;
                  }
                });

      }else {
          console.log("no changes");
          console.log("false9");
          this.loading = false;
          return;

      }

      //this.loading = false;
    } catch (error) {
      console.error(error);
      //this.loading = false;
      // Handle errors if necessary
    } finally {
      // Ensure that the loading indicator is turned off

      this.oldField='';
    }

  }

  checkPacketValidity(packet: Packet): boolean {
      if (!(this.isValid(packet.fbPage) && this.isValid(packet.address) && this.isValid(packet.customerName) &&
      this.isValid(packet.customerPhoneNb) && this.isValid(packet.city) && this.isValid(packet.packetDescription)))
      {
        this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Veuillez saisir tous les champs' });
        return false;
      }
      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est Valid'});
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
    //console.log('phVal',this.isValid(packet.customerPhoneNb)+" aa "+packet.customerPhoneNb+'bb'+packet.id);

    return (this.isValid(packet.address) || this.isValid(packet.customerName) ||
      this.isValid(packet.customerPhoneNb) || this.isValid(packet.city) || this.isValid(packet.packetDescription));
  }

  checkPacketDescription(packet: Packet): boolean {
    return packet.packetDescription!= undefined && packet.packetDescription.includes('(');
  }

  updatePacket(packet: any): void {
    this.packetService.updatePacket(packet)
      .pipe(
        catchError((err: any, caught: Observable<any>): Observable<any> => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur lors de la mise à jour ' + err.error.message });
          packet.data[packet.field] = this.oldField;
          this.loading = false;
          return of();

        })
      )
      .subscribe((response: any) => {
        this.loading = false;
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est ajoutée avec succés'});
      });
  }

  getLastStatus(packet: Packet): void {
    if (packet.status != PAYEE && packet.status != RETOUR_RECU && packet.status != LIVREE)
    this.packetService.getLastStatus(packet)
      .subscribe({
          next: (response: Packet) => {
            this.packets.splice(this.packets.indexOf(packet), 1, response);
            this.createNotification();
          },
          error : (error: Error) => {
            console.log(error);
          }
        });
  }

  addAttempt(packet: Packet): void {
    this.visibleNote = true;
    this.selectedPacket = packet;
    this.note="";
  }
  confirmNote() {
    console.log("Confirm button clicked!");
    let note = "Client injoignable"
    if (this.note.trim() !== '')  // Check if value is not empty
      {
        this.confirmEvent.emit(this.note);
        note = this.note;
      }

      this.packetService.addAttempt(this.selectedPacket,note)
      .subscribe({
          next: (response: Packet) => {
            this.packets.splice(this.packets.indexOf(this.selectedPacket), 1, response);
          },
          error : (error: Error) => {
            console.log(error);
          }
        });
    this.visibleNote = false;
  }
  getLastStock(packetId: number): void {
    console.log("getLasStock-packetId", packetId);
    //if (packet.status != PAYEE && packet.status != RETOUR_RECU && packet.status != LIVREE)
    this.packetService.getLastStock(packetId)
      .subscribe({
          next: (listupdatedStock:any) => {
            console.log("getLasStock-listupdatedStock", listupdatedStock);
            listupdatedStock.forEach((element: any) => {
              console.log("element",element);
              let pos = this.packets.map((packet: Packet) => packet.id).indexOf(element.id);
              this.packets.splice(pos, 1, element);
              //this.packets.splice(this.packets.indexOf(element), 1, element);
            });
          },
          error : (error: Error) => {
            console.log(error);
          }
        });
  }

  openLinkGetter(code: any,deliveryCompany: DeliveryCompany): void {

    let link = deliveryCompany.barreCodeUrl + code;
    console.log("link",link+"/code:"+code);
    window.open(link, '_blank');
  }

  printFirst(link: string): void {
    window.open(link, '_blank');
  }

  showTimeLineDialog(packet: Packet): void {
    try {
      this.packetService.getPacketTimeLine(packet.id).subscribe((response: any) => {
          this.statusEvents = [];
          this.suiviHeader = "Suivi Historique - Commande N° " + packet.id;
          if (response != null && response.length > 0) {
            response.forEach((element: any) => {
              this.statusEvents.push({status: element.status, date: element.date, user: element.user?.fullName, icon: PrimeIcons.ENVELOPE, color: '#9C27B0'});
            });
          }
        //  this.cdRef.detectChanges();
          this.displayStatus = true;
        }
      );
    } catch (error) {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur dans le status'});
    }
  }

  addNewRow(): void {
    if(this.activeIndex!=2)
    this.onActiveIndexChange(2);
    if(this.loading == false){
      //this.activeIndex=2;
      this.loading=true;
      this.packetService
      .addPacket()
      .subscribe((response: Packet) => {
        console.log("new pack", response);

        this.loading=false;
        this.packets.unshift(response);
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est ajoutée avec succés', life: 1000 });
      });
    }
  }

  duplicatePacket(packet: Packet): void {
    this.packetService
      .duplicatePacket(packet.id)
      .subscribe((response: Packet) => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est dupliqué avec succés', life: 1000});
        this.packets.unshift(response);
      });
  }

  deleteSelectedPackets(): void {
    let selectedPacketsById = this.selectedPackets.map((selectedPacket: Packet) => selectedPacket.id);
    this.confirmationService.confirm({
      message:'Êtes-vous sûr de vouloir supprimer les commandes séléctionnées ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.packetService
          .deleteSelectedPackets(selectedPacketsById)
          .subscribe(() => {
            this.packets = this.packets.filter((packet: Packet) => selectedPacketsById.indexOf(packet.id) == -1);
            this.selectedPackets = [];
            this.messageService.add({severity: 'success',summary: 'Succés', detail: 'Les commandes séléctionnées ont été supprimé avec succés', life: 1000});
          });
      }
    });
  }

  openNew(packet: Packet): void {
    this.packet = Object.assign({}, packet);
    this.submitted = false;
    this.modelDialog = true;
    this.editMode = false;
  }

  editProducts(packet: Packet): void {
    this.packet = Object.assign({}, packet);
    this.submitted = false;
    this.modelDialog = true;
    this.editMode = true;
  }

  hideDialog(): void {
    this.modelDialog = false;
    this.submitted = false;
  }

  OnSubmit($event: any): void {
    console.log("onsubmit list packet");
    this.modelDialog = $event.modelDialog;
    let pos = this.packets.map((packet: Packet) => packet.id).indexOf($event.packet.id);
    this.packets.splice(pos, 1, $event.packet);
    this.editMode ? this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été mis à jour avec succés', life: 1000 }) : this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été ajoutés avec succés', life: 1000 });
  }

  checkValidity(date1: Date, date2: Date, status: String): boolean {
    if (status != PAYEE && status != RETOUR_RECU)
      return this.dateUtils.getDate(date1) < this.dateUtils.getDate(date2);
    return false;
  }

  handleInputChange() {
    const inputValue = this.filter;
    const numbersCount = (inputValue.match(/\d/g) || []).length;
    if (numbersCount === 5 || numbersCount === 8 || numbersCount === 12  ) {
      console.log("filterPackets-handleInputChange");
      this.oldActiveIndex = this.activeIndex;
      this.filterPackets('global');
      this.activeIndex = 0;
    }
    if (this.filter === '') {
      console.log("filterPackets-handleInputChange vide");
      this.filterPackets('global');
      this.activeIndex = this.oldActiveIndex;
    }
    console.log("this.activeIndex ",this.activeIndex );


  }

  filterPackets($event?: string): void {
    //console.log("filterPackets",$event);

    this.createRangeDate();
    //this.setPacketStatusList();
    let page = 0;

    /* if ($event == 'states') {
      this.onStateChange();
    } else  */
    if ($event == 'clear') {
      this.selectedStates = [];
      //this.packetStatusList = this.statusList;
      this.selectedStatus.setValue([]);
    }else if($event == 'page')
      page = this.currentPage;
    if (this.selectedStatus.value == null) this.selectedStatus.setValue([]);

    if (this.filter !== '' && this.filter !== undefined){ this.oldActiveIndex = this.activeIndex;this.activeIndex = 0;console.log("filter:",this.filter);
    }
    //else this.activeIndex = 0;

    this.params = {
      page: page,
      size: this.pageSize,
      searchText: this.filter != null && this.filter != '' ? this.filter : null,
      startDate: this.dateUtils.formatDateToString(this.startDate),
      endDate: this.dateUtils.formatDateToString(this.endDate),
      status: this.selectedStatus.value.length == 0 ? null : this.selectedStatus.value.join(),
      mandatoryDate: this.mandatoryDateCheckBox
    };

    console.log("filterPackets params : ", this.params);

    this.findAllPackets();
  }

  createRangeDate(): void {
    console.log("createRangeDate : ", this.startDate);
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
      console.log("createRangeDate startDate: ", this.startDate);
  }


  onPageChange($event: any): void {
    this.currentPage = $event.page;
    this.pageSize = $event.rows;
    console.log("filterPackets-onPageChange");

    this.filterPackets('page');
  }

  resetTable(): void{
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

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }

  checkCodeABarreExist(packet:Packet){
    return packet!= undefined && packet.barcode !="" && packet.barcode!= null
  }

  checkPhoneNbExist(packet:Packet){
    return packet!= undefined && packet.customerPhoneNb !="" && packet.customerPhoneNb!= null
  }

  mandatoryDateChange(){
    if(this.selectedStatus.value != null && this.selectedStatus.value.length > 0)
      console.log("filterPackets-mandatoryDateChange");
    this.filterPackets('global')
  }

  oldDateFilter(){
      this.rangeDates = [new Date(2023, 0, 1), new Date(Date.now() - 86400000)];
      console.log("filterPackets-oldDateFilter");
      this.filterPackets('global');

    //console.log('aaa',this.rangeDates);
  }
  todayDate(){
    if(this.rangeDates[0] != undefined && this.rangeDates[1]==undefined)
      {
        this.rangeDates[0]=this.startDate;
        this.endDate = this.today;
        this.rangeDates= [this.startDate,this.today];
        //this.createRangeDate();
      }
    else this.rangeDates = [this.today];
    //this.createRangeDate();
    this.filterPackets('global');
  }

  clearDate(){
    this.rangeDates = [];
    //this.createRangeDate();
    console.log("filterPackets-clearDate");
    this.filterPackets('global');
  }

  openShowOptionMenu(packet:any) {
    console.log("pp",packet);

    this.optionButtons = [
      {
        label: 'Duplicate',
        icon: 'pi pi-refresh',
        disabled:!this.checkCodeABarreExist(packet),
        command: () => {
          this.duplicatePacket(packet)
        }
      },
      {
        label: 'Ajouter tentative',
        icon: 'pi pi-refresh',
        disabled:packet.status!=INJOIGNABLE,
        command: () => {
          this.addAttempt(packet)
        }
      },
      {
        label: 'Valider stock',
        icon: 'pi pi-refresh',
        disabled:packet.stock>15 || packet.stock==-1,
        command: () => {
          this.getLastStock(packet.id)
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
  }

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

  selectCity( packet: Packet) {
    this.selectedCity = packet.city;
}
  selectPhoneNumber( packet: any) {
    this.selectedPhoneNumber = packet.customerPhoneNb;
  }

  onActiveIndexChange(event: any) {

    console.log(event,this.enCoursOptionsValue);
    this.activeIndex = event;

    if(this.items[event].title == EN_COURS)
      {
        if(this.enCoursOptionsValue != undefined && this.enCoursOptionsValue.length > 0)
        this.selectedStatus.patchValue(this.enCoursOptionsValue);
        else
        this.selectedStatus.patchValue([ A_VERIFIER, EN_COURS_1, EN_COURS_2, EN_COURS_3]);
      }
    else if(this.items[event].title == NON_CONFIRMEE)
      {
        if(this.nonConfirmedOptionsValue != undefined && this.nonConfirmedOptionsValue.length > 0)
            this.selectedStatus.patchValue(this.nonConfirmedOptionsValue);
        else
            this.selectedStatus.patchValue([ NON_CONFIRMEE, INJOIGNABLE]);
      }

    else if(this.items[event].title == "Terminé")
      {
        if(this.endedOptionsValue != undefined && this.endedOptionsValue.length > 0)
          this.selectedStatus.patchValue(this.endedOptionsValue);
      else
        this.selectedStatus.patchValue([LIVREE,PAYEE,RETOUR_RECU]);
      }
    else {
      this.selectedStatus.setValue([]);
      this.selectedStatus.patchValue([this.items[event].title]);
    }
    //console.log("filterPackets-onActiveIndexChange",this.params);
    this.filterPackets('status');
  }

  showStatusButton() {
    this.showStatus= !this.showStatus;
}

}

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
      this.selectedStatus.patchValue([ NON_CONFIRMEE, ENDED, A_VERIFIER, CONFIRMEE ]);
    }
    if (this.selectedStates.indexOf(EN_COURS) > -1) {
      this.selectedStatus.patchValue([ A_VERIFIER, EN_COURS_1, EN_COURS_2, EN_COURS_3]);
      //this.packetStatusList = this.statusList;
    }
    if (this.selectedStates.indexOf(TERMINE) > -1) {
      this.selectedStatus.patchValue([PAYEE, RETOUR_RECU]);
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
