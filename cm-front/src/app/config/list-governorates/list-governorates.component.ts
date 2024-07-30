import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Governorate } from 'src/shared/models/Governorate';
import { GovernorateService } from '../../../shared/services/governorate.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-list-governorates',
  templateUrl: './list-governorates.component.html',
  styleUrls: ['./list-governorates.component.scss']
})
export class ListGovernoratesComponent implements OnInit,OnDestroy {

  selectedGovernorates: Governorate[] = [];
  oldGovernorate!: Governorate;
  governorates: Governorate[] = [];

  $unsubscribe: Subject<void> = new Subject();
  constructor(private governorateService: GovernorateService, private messageService: MessageService, private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    this.governorateService.findAllGovernorates()
    .subscribe((governorates: any) => {
      this.governorates = governorates;
      console.log(this.governorates)
    })
  }

  addNewRow() {
    this.governorateService.addGovernorate(this.newPacket()).pipe(takeUntil(this.$unsubscribe))
      .subscribe((response: any) => {
        this.governorates.push(response);
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La gouvernorat est ajoutée avec succés', life: 1000 });
      });
  }

  newPacket() {
    return {
      id: "",
      name: ""
    }
  }

  deleteSelectedGovernorates() {
    let selectedGovernoratesId = this.selectedGovernorates.map((selectedGovernorate: Governorate) => selectedGovernorate.id);
    console.log(selectedGovernoratesId);
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer les gouvernorats séléctionnées ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.governorateService.deleteSelectedGovernorates(selectedGovernoratesId).pipe(takeUntil(this.$unsubscribe))
          .subscribe(result => {
            this.governorates = this.governorates.filter((gouvernorate: Governorate) => selectedGovernoratesId.indexOf(gouvernorate.id) == -1);
            this.selectedGovernorates = [];
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Les gouvernorats séléctionnées ont été supprimé avec succés', life: 1000 });
          })
      }
    });
  }

  onEditInit($event: any) {
    this.oldGovernorate = Object.assign({}, $event.data);
  }

  onEditComplete($event: any) {
    if(JSON.stringify(this.oldGovernorate) != JSON.stringify($event.data)) {
      this.governorateService.updateGovernorate($event.data).pipe(takeUntil(this.$unsubscribe))
      .subscribe(result => {
        console.log("governorate successfully updated !");
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'La gouvernorat a été mise à jour avec succés', life: 1000 });
      })
    }
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
this.$unsubscribe.complete();
  }

}
