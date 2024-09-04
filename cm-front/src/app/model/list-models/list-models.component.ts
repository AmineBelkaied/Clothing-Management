import { Component, OnDestroy, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { catchError, Subject, switchMap, take, takeUntil, tap, throwError } from 'rxjs';
import { Color } from 'src/shared/models/Color';
import { Model } from 'src/shared/models/Model';
import { Size } from 'src/shared/models/Size';
import { ColorService } from 'src/shared/services/color.service';
import { ModelService } from 'src/shared/services/model.service';
import { SizeService } from 'src/shared/services/size.service';
import { NumberUtils } from 'src/shared/utils/number-utils';
import { StringUtils } from 'src/shared/utils/string-utils';

@Component({
  selector: 'app-list-models',
  templateUrl: './list-models.component.html',
  styleUrls: ['./list-models.component.css'],
})
export class ListModelsComponent implements OnInit, OnDestroy {
  modelDialog!: boolean;
  msg: String = 'Erreur de connexion';

  models: Model[] = [];

  model: Model = {
    id: 0, // Or any default value
    name: '',
    description: '',
    colors: [] =[],
    sizes: [] = [],
    products: [] = [],
    earningCoefficient: 2,
    purchasePrice: 15,
    deleted: false,
    enabled: false
  }
  editMode = false;

  colors: Color[] = [];
  sizes: Size[] = [];
  selectedModels: Model[] = [];
  isValidModel: boolean = false;
  modelNameExists: boolean = false;

  submitted: boolean = false;
  selectedFile: any;
  $unsubscribe: Subject<void> = new Subject();
  clonedModel: Model;

  constructor(
    private modelService: ModelService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) { }

  ngOnInit() {
    this.modelService
      .getModelsSubscriber()
      .pipe(takeUntil(this.$unsubscribe))
      .pipe(
        tap((models: Model[]) => {
          this.models = models;
        })
      )
      .subscribe();
  }

  saveModel() {
    this.submitted = true;
    console.log(this.model);
    
    if (this.isValidModel) {
      return this.modelService.addModel(this.model).pipe(
        tap((response: Model) => {
          this.modelService.updateModelsSubscriber(response);
          if (this.editMode) {
            this.messageService.add({
              severity: 'success',
              summary: 'Succés',
              detail: 'Le modèle a été mise a jour avec succés',
              life: 3000,
            });
          } else {
            this.messageService.add({
              severity: 'success',
              summary: 'Succés',
              detail: 'Le modèle a été crée avec succés',
              life: 3000,
            });
          }
        }),
        catchError((error) => {//Correction a verifier
          console.error('There was an error when saving model!', error);
          return throwError(() => error);
        })
      )
        .subscribe({
          complete: () => {
            this.modelDialog = false;
            this.model = {...this.modelService.defaultModel};  // Reset model to default
          }
        });
    }
  }

  checkFormValidation(event: any): void {
    this.modelNameExists = this.modelAlreadyExists(event.model.name);

    this.isValidModel = StringUtils.isStringValid(event.model.name) && NumberUtils.isNumberValid(event.model.purchasePrice) &&
      NumberUtils.isNumberValid(event.salePrice) && event.model.sizes.length > 0 && event.model.colors.length > 0 && !this.modelNameExists;
    console.log(this.modelNameExists);
    
    }

  private modelAlreadyExists(name: string): boolean {
    return this.editMode ?
      this.models.filter(model => model.name !== this.clonedModel.name).some(model => model.name.toLowerCase() === name.toLowerCase()) :
      this.models.some(model => model.name.toLowerCase() === name.toLowerCase());
  }

  openNew() {
    this.submitted = false;
    this.modelDialog = true;
    this.editMode = false;
    this.model = this.modelService.defaultModel;
  }

  deleteSelectedModels() {
    let selectedModelsId = this.selectedModels.map(
      (selectedModel: any) => selectedModel.id
    );
    console.log(selectedModelsId);
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer les modèles séléctionnés ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.modelService
          .deleteSelectedModels(selectedModelsId)
          .pipe(takeUntil(this.$unsubscribe))
          .subscribe(() => {
            this.models = this.models.filter(
              (val) => !this.selectedModels.includes(val)
            );
            this.selectedModels = [];
            this.messageService.add({
              severity: 'success',
              summary: 'Succés',
              detail: 'Les modèles séléctionnés ont été supprimé avec succés',
              life: 1000,
            });
          });
      },
    });
  }

  editModel(model: Model) {
    this.model = { ...model };
    this.model.colors = this.model.colors?.filter(
      (color: Color) => color.reference != '?'
    );
    this.model.sizes = this.model.sizes?.filter(
      (size: Size) => size.reference != '?'
    );
   // this.modelService.setModel(model);
    this.modelDialog = true;
    this.editMode = true;
    this.clonedModel = {...model};
  }

  deleteModel(model: Model) {
    console.log(model.id);
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete ' + this.model.name + '?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.modelService
          .deleteModelById(model.id!)
          .pipe(takeUntil(this.$unsubscribe))
          .subscribe(() => {
            this.models = this.models.filter((val) => val.id !== model.id);
            this.model = Object.assign({}, this.model);
            this.messageService.add({
              severity: 'success',
              summary: 'Successful',
              detail: 'model Deleted',
              life: 3000,
            });
          });
      },
    });
  }

  hideDialog() {
    this.modelDialog = false;
    this.submitted = false;
  }



  findIndexById(id: number): number {
    let index = -1;
    for (let i = 0; i < this.models.length; i++) {
      if (this.models[i].id === id) {
        index = i;
        break;
      }
    }
    return index;
  }

  modelColorsDisplay(colors: any[]) {
    let colorDisplay = '';
    colors = [...colors.filter((color) => color.name != '?')];
    colors.forEach((color, index) => {
      colorDisplay += color.name;
      if (index < colors.length - 1) colorDisplay += ',';
    });
    return colorDisplay;
  }

  modelSizesDisplay(sizes: any[]) {
    let sizeDisplay = '';
    sizes = [...sizes.filter((size) => size.reference != '?')];
    sizes.forEach((size, index) => {
      sizeDisplay += size.reference;
      if (index < sizes.length - 1) sizeDisplay += '-';
    });
    return sizeDisplay;
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }

}
