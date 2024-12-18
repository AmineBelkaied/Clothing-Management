

// import modules, directives and services
import { AppRoutingModule } from './app-routing.module';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { MultiSelectModule } from 'primeng/multiselect';
import { DropdownModule } from 'primeng/dropdown';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { ToolbarModule } from 'primeng/toolbar';
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { SelectButtonModule } from 'primeng/selectbutton';
import { InputSwitchModule } from 'primeng/inputswitch';
import { CalendarModule } from 'primeng/calendar';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { TabViewModule } from 'primeng/tabview';
import { SplitterModule } from 'primeng/splitter';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ChangeColorDirective } from 'src/shared/directives/change-color.directive';
import { TimelineModule } from 'primeng/timeline';
import { TreeTableModule } from 'primeng/treetable';
import { RadioButtonModule } from 'primeng/radiobutton';
import { FileUploadModule } from 'primeng/fileupload';
import { TagModule } from 'primeng/tag';
import { CheckboxModule } from 'primeng/checkbox';
import { SplitButtonModule } from 'primeng/splitbutton';
import { SpeedDialModule } from 'primeng/speeddial';
import { ContextMenuModule } from 'primeng/contextmenu';
import { BadgeModule } from 'primeng/badge';
import { CardModule } from 'primeng/card';
import { ListboxModule } from 'primeng/listbox';
import { ChipModule } from 'primeng/chip';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { AvatarGroupModule } from 'primeng/avatargroup';
import { AvatarModule } from 'primeng/avatar';
import { ColorPickerModule } from 'primeng/colorpicker';
import { ReactiveFormsModule } from '@angular/forms';
import { RippleModule } from 'primeng/ripple';
import { ScrollerModule } from 'primeng/scroller';
import { PanelMenuModule } from 'primeng/panelmenu';



// import components
import { AppComponent } from './app.component';
import { AddCityComponent } from './config/city/add-city/add-city.component';
import { ListCitiesComponent } from './config/city/list-cities/list-cities.component';
import { AddColorComponent } from './config/color/add-color/add-color.component';
import { ListColorsComponent } from './config/color/list-colors/list-colors.component';
import { ConfigComponent } from './config/config.component';
import { AddFbpageComponent } from './config/fbpage/add-fbpage/add-fbpage.component';
import { AddDeliveryCompanyComponent } from './config/delivery-company/add-delivery-company/add-delivery-company.component';
import { ListFbpagesComponent } from './config/fbpage/list-fbpages/list-fbpages.component';
import { ListDeliveryCompaniesComponent } from './config/delivery-company/list-delivery-companies/list-delivery-companies.component';
import { ListGovernoratesComponent } from './config/list-governorates/list-governorates.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { AddModelComponent } from './model/add-model/add-model.component';
import { AddOfferComponent } from './offer/add-offer/add-offer.component';
import { ListOffersComponent } from './offer/list-offers/list-offers.component';
import { AddPacketComponent } from './packet/add-packet/add-packet.component';
import { ListPacketsComponent } from './packet/list-packets/list-packets.component';
import { StockComponent } from './stock/stock.component';
import { ListModelsComponent } from './model/list-models/list-models.component';
import { AddSizeComponent } from './config/size/add-size/add-size.component';
import { ListSizesComponent } from './config/size/list-sizes/list-sizes.component';
import { ChartModule } from 'primeng/chart';
import { ToggleButtonModule } from 'primeng/togglebutton';


import { StockHistoryComponent } from './stock/stock-history/stock-history.component';
import { StockTableComponent } from './stock/stock-table/stock-table.component';
import { StockTableMinComponent } from './stock/stock-table-min/stock-table-min.component';
import { PaginatorModule } from 'primeng/paginator';
import { PaidReturnComponent } from './paid-return/paid-return.component';
import { VerificationComponent } from './verification/verification.component';
import { PickListModule } from 'primeng/picklist';
import { GlobalConfComponent } from './config/global-conf/global-conf.component';
import { PacketsMenueContainerComponent } from './packet/list-packets/packets-menue-container/packets-menue-container.component';
import { StatusContainerComponent } from './packet/list-packets/packets-menue-container/status-container/status-container.component';

import { StepsModule } from 'primeng/steps';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { OverlayPanelModule } from 'primeng/overlaypanel';

import { AuthInterceptor } from 'src/shared/helpers/interceptor';
import { UserComponent } from './config/user/user.component';
import { PasswordModule } from 'primeng/password';
import 'tslib';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { AuthGuard } from 'src/shared/services/auth-gard.service';
import { GlobalConfService } from 'src/shared/services/global-conf.service';
import { OfferService } from 'src/shared/services/offer.service';
import { DisplayListPipe } from 'src/shared/pipes/display-list.pipe';
import { DisplayOfferModelsPipe } from 'src/shared/pipes/display-offer-models.pipe';
import { DisplayColorPipe } from 'src/shared/pipes/display-color.pipe';
import { DisplaySizePipe } from 'src/shared/pipes/display-size.pipe';
import { DisplayFbPagePipe } from 'src/shared/pipes/display-fbPage.pipe';
import { DisplayJoinPipe } from 'src/shared/pipes/display-join.pipe';
import { DisplayColorHexPipe } from 'src/shared/pipes/display-colorHex.pipe';
import { DisplayDeliveryCompanyPipe } from 'src/shared/pipes/display-delivery-company.pipe';
import { TabMenuModule } from 'primeng/tabmenu';
import { ModelStatComponent } from './stats/model-stat/model-stat.component';
import { StatsComponent } from './stats/stats.component';
import { OfferStatComponent } from './stats/offer-stat/offer-stat.component';
import { PacketStatComponent } from './stats/packet-stat/packet-stat.component';
import { PageStatComponent } from './stats/page-stat/page-stat.component';
import { StatesStatComponent } from './stats/states-stat/states-stat.component';
import { StockStatComponent } from './stats/stock-stat/stock-stat.component';
import { ColorStatComponent } from './stats/color-stat/color-stat.component';

@NgModule({
  declarations: [
    AppComponent,
    SidebarComponent,
    AddModelComponent,
    AddPacketComponent,
    ListModelsComponent,
    AddPacketComponent,
    ListPacketsComponent,
    ChangeColorDirective,
    AddOfferComponent,
    ListOffersComponent,
    AddColorComponent,
    ListColorsComponent,
    AddSizeComponent,
    ListSizesComponent,
    AddCityComponent,
    ListCitiesComponent,
    ConfigComponent,
    ListGovernoratesComponent,
    AddFbpageComponent,
    AddDeliveryCompanyComponent,
    ListFbpagesComponent,
    ListDeliveryCompaniesComponent,
    StockComponent,
    StatsComponent,
    ModelStatComponent,
    OfferStatComponent,
    PageStatComponent,
    PacketStatComponent,
    StatesStatComponent,
    StockStatComponent,
    ColorStatComponent,
    StockHistoryComponent,
    StockTableComponent,
    StockTableMinComponent,
    PaidReturnComponent,
    VerificationComponent,
    PaidReturnComponent,
    PacketsMenueContainerComponent,
    StatusContainerComponent,
    UserComponent,
    GlobalConfComponent,
    DisplayJoinPipe,
    DisplayColorPipe,
    DisplayColorHexPipe,
    DisplaySizePipe,
    DisplayFbPagePipe,
    DisplayOfferModelsPipe,
    DisplayDeliveryCompanyPipe
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    CommonModule,
    AppRoutingModule, TimelineModule,
    InputTextModule,
    InputSwitchModule,
    MultiSelectModule,
    DropdownModule,
    ToolbarModule,
    TableModule,
    CalendarModule,
    InputNumberModule,
    TabViewModule,
    SplitterModule,
    SelectButtonModule,
    DialogModule,
    CardModule,
    ConfirmDialogModule,
    ToastModule,
    ProgressSpinnerModule,
    InputTextareaModule,
    HttpClientModule,
    ChartModule,
    ToggleButtonModule,
    TreeTableModule,
    PaginatorModule,
    RadioButtonModule,
    FileUploadModule,
    TagModule,
    CheckboxModule,
    PasswordModule,
    CheckboxModule,
    SplitButtonModule,
    SpeedDialModule,
    ContextMenuModule,
    BadgeModule,
    ListboxModule,
    ChipModule,
    PickListModule,
    ConfirmPopupModule,
    AvatarGroupModule,
    AvatarModule,
    StepsModule,
    BreadcrumbModule,
    ReactiveFormsModule,
    RippleModule,
    OverlayPanelModule,
    ColorPickerModule,
    ScrollerModule,
    TabMenuModule,
    PanelMenuModule
  ],
  providers: [
    MessageService,
    ConfirmationService,
    DatePipe,
    AuthGuard,
    GlobalConfService,
    OfferService,
    DecimalPipe,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }],
  bootstrap: [AppComponent]
})

export class AppModule { }
