import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { DateUtils } from 'src/shared/utils/date-utils';
import { StatusContainerComponent } from 'src/app/packet/list-packets/packets-menue-container/status-container/status-container.component';
import { NOT_CONFIRMED, statesList, statusList } from 'src/shared/utils/status-list';
import { Packet } from 'src/shared/models/Packet';
import { StorageService } from 'src/shared/services/strorage.service';
import { Status } from 'src/shared/models/status';
import { PacketFilterParams } from 'src/shared/models/PacketFilterParams';
import { SideBarService } from 'src/shared/services/sidebar.service';

@Component({
  selector: 'app-packets-menue-container',
  templateUrl: './packets-menue-container.component.html',
  styleUrls: ['./packets-menue-container.component.css']
})
export class PacketsMenueContainerComponent {

  //@ViewChild(StatusContainerComponent) statusContainerComponent!: StatusContainerComponent;
  packetStatusList: string[] = [];
  rangeDates: Date[] = [];
  beginDate: Date = new Date();
  endDate: Date | null = new Date();
  today: Date = new Date();

  //activeIndex: number = 2;
  //oldActiveIndex: number;
  @Input() filter: string;
  statesList: string[] = [];
  selectedStates: string[] = [];
  statusList: string[] = [];
  pageSize: number = 100;
  dateOptions: any[] = [{ label: 'Off', value: false }, { label: 'On', value: true }];
  showStatus: boolean = false;

  first = 0;
  rows = 100;
  currentPage = 0;

  params: any = {
    page: 0,
    size: this.pageSize,
    beginDate: this.dateUtils.formatDateToString(this.today),
    endDate: this.dateUtils.formatDateToString(this.today),
    mandatoryDate: false
  };
  mandatoryDateCheckBox: boolean = false;
  oldDateFilterCheckBox: boolean = false;
  value: boolean = this.mandatoryDateCheckBox;
  selectedStatus: string[] = [NOT_CONFIRMED];
  loading: boolean = false;
  statusItemsLabel: string;
  @Output()
  filterPacketsEmitter: EventEmitter<PacketFilterParams> = new EventEmitter()

  @Output()
  buttonPacketsEmitter: EventEmitter<String> = new EventEmitter();

  constructor(
    private dateUtils: DateUtils,
    private storageService: StorageService,
    private sideBarService: SideBarService

    ) {
    this.statusList = statusList;
    this.statesList = statesList;
  }

  activeClass: boolean;
  userName: string;
  isLoggedIn: boolean;
  isAdmin: boolean;
  isSuperAdmin: boolean;
  oldStatus: Status;

  ngOnInit(): void {
    this.storageService.isLoggedIn.subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
      this.userName = this.storageService.getUserName();
      this.isAdmin = this.storageService.hasRoleAdmin();
      this.isSuperAdmin = this.storageService.hasRoleSuperAdmin();
      this.activeClass = true;
    });
    this.statusItemsLabel = this.selectedStatus[0];
    this.rangeDates = [this.today,this.today];
    this.filterPackets('global');
  }

  handleInputChange() {
    const inputValue = this.filter;
    const numbersCount = (inputValue.match(/\d/g) || []).length;
    console.log(inputValue,numbersCount);

    if ( numbersCount === 5 || numbersCount === 8 || numbersCount === 12  ) {
      //this.oldActiveIndex = this.activeIndex;
      this.filterPackets('global');
      //this.activeIndex = 0;
    }
    if (this.filter === '') {
      this.filterPackets('global');
      //this.activeIndex = this.oldActiveIndex;//Correction
    }
  }

  resetTable(): void {
    this.selectedStates = [];
    this.buttonPacketsEmitter.next('reset');
    this.selectedStatus=[];
    this.rangeDates = [new Date(2023, 0, 1), new Date(Date.now())];
    this.filterPackets('global');
  }



  addNewRow(): void {
    //console.log("activeIndex", this.activeIndex);
    console.log(this.statusItemsLabel);

    if (this.statusItemsLabel != NOT_CONFIRMED){
      this.statusItemsLabel =NOT_CONFIRMED;
      this.selectedStatus=[NOT_CONFIRMED];
      this.filterPackets('global');
    }
    else if (!this.loading) {
      console.log("new pack");
      this.buttonPacketsEmitter.next('add');
    }
  }
  deleteSelectedPackets() {
    this.buttonPacketsEmitter.next('delete');
  }

  checkPacketNotNull(packet: Packet): boolean {
    return (this.isValid(packet.address) || this.isValid(packet.customerName) ||
      this.isValid(packet.customerPhoneNb) || packet.cityId! >0 || this.isValid(packet.packetDescription));
  }
  isValid(field: any) {
    return field != null && field != '';
  }
  $unsubscribe($unsubscribe: any): import("rxjs").OperatorFunction<any, any> {
    throw new Error('Method not implemented.');
  }

  filterPackets($event?: string): void {
    this.createRangeDate();
    console.log(this.selectedStatus);

    this.showStatus = false;
    if(this.endDate){
      let page = 0;
      if ($event == 'clear') {
        this.selectedStates = [];
        this.selectedStatus=[];
      } else if ($event == 'page')
        page = this.currentPage;
      if (this.selectedStatus == null) this.selectedStatus =[];

/*       if (this.filter !== '' && this.filter)
        {
          this.oldActiveIndex = this.activeIndex;
          this.activeIndex = 0;
        } */

      this.params = {
        page: page,
        size: this.pageSize,
        searchText: this.filter != null && this.filter != '' ? this.filter : null,
        beginDate: this.dateUtils.formatDateToString(this.beginDate),
        endDate: this.dateUtils.formatDateToString(this.endDate),
        status: this.selectedStatus.length == 0 ? null : this.selectedStatus,
        mandatoryDate: this.mandatoryDateCheckBox
      };
      this.filterPacketsEmitter.next(this.params);
      //this.findAllPackets();

    }
  }

  createRangeDate(): void {
    if (this.rangeDates !== null) {
      this.beginDate = this.rangeDates[0];
      if (this.rangeDates[1]) {
        this.endDate = this.rangeDates[1];
      } else {
        this.endDate = null;
      }
    } else {
      this.beginDate = this.today;
      this.endDate = this.today;
    }
  }

  showStatusButton() {
    this.showStatus= !this.showStatus;
  }

  mandatoryDateChange(){
    if(this.selectedStatus != null && this.selectedStatus.length > 0)
    this.filterPackets('global')
  }
  onStatusChange(item: Status) {
    console.log('Selected statuses in parent:', item);
    if(this.oldStatus != item){
      if (item.options) {
        this.showStatus = true
      } else this.showStatus = false

      this.selectedStatus = item.options ? item.selectedOptions.length==0 ? this.getArrayFromStatusItems(item.options) : item.selectedOptions : [item.label];

      this.statusItemsLabel = item.label;
      this.filterPackets('global')
      this.oldStatus=item;
    }
  }
  getArrayFromStatusItems(statusItems:any) : string[]{
    return statusItems.map((item :any) => item.value);
  }
  oldDateFilter(){
      this.rangeDates = [new Date(2023, 0, 1), new Date(Date.now() - 86400000)];
      this.filterPackets('global');
  }
  todayDate(){
    if(this.rangeDates[0] != undefined && this.rangeDates[1]==undefined)
      {
        this.rangeDates[0]=this.beginDate;
        this.endDate = this.today;
        this.rangeDates= [this.beginDate,this.today];
      }
    else this.rangeDates = [this.today];
    this.filterPackets('global');
  }

  clearDate(){
    this.rangeDates = [];
    this.filterPackets('global');
  }
  toggleStatus() {
    this.showStatus=!this.showStatus;
  }

  /*clearStatus(): void {
    this.selectedStates = [];
    this.packetStatusList = this.statusList;
    this.selectedStatus=[];
    if (this.filter != '' && this.filter != null) {
      this.dt!.reset();
      this.dt!.filterGlobal(this.filter, 'contains');
    }
  }*/

}
