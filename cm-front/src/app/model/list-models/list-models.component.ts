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
export class ListModelsComponent implements OnInit, OnDestroy{
  modelDialog!: boolean;
  msg :String = 'Erreur de connexion';

  models: Model[] = [];

  model: Model; /*  = {
    "id":"",
    "name": "",
    "description": "",
    "colors": [],
    "sizes": [],
    "products":[],
    "earningCoefficient":1,
    "purchasePrice":15,
    "deleted":false
  } */
  editMode = false;

  colors: Color[] = [];
  sizes: Size[] = [];
  selectedModels: Model[] = [];
  isValidModel: boolean = false;
  modelNameExists: boolean = false;

  submitted: boolean = false;
  selectedFile: any;
  $unsubscribe: Subject<void> = new Subject();

  constructor(
    private modelService: ModelService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private sizeService: SizeService,
    private colorService: ColorService
  ) {}

  ngOnInit() {
    this.modelService
      .getModelsSubscriber()
      .pipe(takeUntil(this.$unsubscribe))
      .pipe(
        tap((data: any) => {
          this.models = data;
        })
        )
      .subscribe();
  }

  saveModel() {
    this.submitted = true;
    if(this.isValidModel) {
    // Get the current model from the subscriber
    this.modelService.getModelSubscriber().pipe(
      take(1),  // Ensure we take only the first emission and then complete
      switchMap((model: Model) => {
        this.model = model;
        console.log('this.model', this.model);
        
        // Save the model
        return this.modelService.addModel(this.model).pipe(
          tap((response: Model) => {
            //console.log(response);
            this.modelService.updateModelsSubscriber(response);
            if (this.editMode) {
              this.messageService.add({
                severity: 'success',
                summary: 'Succés',
                detail: 'Le modèle a été mise a jour avec succés',
                life: 3000,
              });
            } else {
              this.models.push(response);
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
        );
      })
    ).subscribe({
      complete: () => {
        this.modelDialog = false;
        this.model = Object.assign({}, this.modelService.defaultModel);  // Reset model to default
      }
    });
    }
  }

  checkFormValidation(event: any): void {
    this.modelNameExists = this.modelAlreadyExists(event.model.name);

    this.isValidModel = StringUtils.isStringValid(event.model.name) && NumberUtils.isNumberValid(event.model.purchasePrice) &&
    NumberUtils.isNumberValid(event.salePrice) && event.model.sizes.length > 0 && event.model.colors.length > 0 && !this.modelNameExists;
  }

  private modelAlreadyExists(name: string): boolean {
    return this.models.some(model => model.name.toLowerCase() === name.toLowerCase());
  }
 
  openNew() {
    this.submitted = false;
    this.modelDialog = true;
    this.editMode = false;
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
    console.log('editModel', model);
    this.model = { ...model };
    this.model.colors = this.model.colors?.filter(
      (color: Color) => color.reference != '?'
    );
    this.model.sizes = this.model.sizes?.filter(
      (size: Size) => size.reference != '?'
    );
    this.modelService.setModel(model);
    this.modelDialog = true;
    this.editMode = true;
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
