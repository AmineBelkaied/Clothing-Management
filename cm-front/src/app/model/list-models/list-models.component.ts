import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { catchError, of, Subject, switchMap, take, takeUntil, tap, throwError } from 'rxjs';
import { Color } from 'src/shared/models/Color';
import { Model, ModelDeleteDTO } from 'src/shared/models/Model';
import { Size } from 'src/shared/models/Size';
import { ModelService } from 'src/shared/services/model.service';
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
  showStockDialog: boolean = false;
  selectedModel: Model = {
    name: '',
    description: '',
    colors: [] = [],
    sizes: [] = [],
    products: [] = [],
    earningCoefficient: 2,
    purchasePrice: 15,
    deleted: false,
    enabled: false
  }

  models: Model[] = [];

  model: Model = {
    id: 0, // Or any default value
    name: '',
    description: '',
    colors: [] = [],
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
  selectedActionsOptions: { name: string; label: string; }[];
  actionsOptions: { name: string; label: string; }[];

  submitted: boolean = false;
  selectedFile: any;
  $unsubscribe: Subject<void> = new Subject();
  clonedModel: Model;
  isOptionsDialogVisible: boolean = false;
  error: string = '';
  hasDeletionErrors: boolean;
  errors: any;

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
          this.models = models.filter((model: Model) => !model.deleted);
        })
      )
      .subscribe();

    this.actionsOptions = [
      { name: 'showArchivedModels', label: 'Modèles archivés' },
      { name: 'showEnabledModels', label: 'Modèles activés' },
      { name: 'showDisabledModels', label: 'Modèles désactivés' }
    ];

  }

  saveModel() {
    this.submitted = true;
    if (this.isValidModel) {
      return this.modelService.addModel(this.model).pipe(
        tap((response: any) => {
          console.log(response);
          if(!response.success) {
            this.hasDeletionErrors = true;
            this.errors = response.errors.replace(/\n/g, '<p>');
/*             this.confirmationService.confirm({
              message: formattedErrors,
              header: 'Confirmation',
              icon: 'pi pi-exclamation-triangle',
              accept: () => {
              }
            }); */
          }
          this.modelService.updateModelsSubscriber(response.modelDTO);

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
            this.model = { ...this.modelService.defaultModel };  // Reset model to default
          }
        });
    }
  }

  checkFormValidation(salePrice: number): void {
    this.modelNameExists = this.modelAlreadyExists(this.model.name);
    this.isValidModel = StringUtils.isStringValid(this.model.name) && NumberUtils.isNumberValid(this.model.purchasePrice) &&
      NumberUtils.isNumberValid(salePrice) && this.model.sizes.length > 0 && this.model.colors.length > 0 && !this.modelNameExists;
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

  editModel(model: Model) {
    this.model = { ...model };
    this.model.colors = this.model.colors?.filter(
      (color: Color) => color != null
    );
    this.model.sizes = this.model.sizes?.filter(
      (size: Size) => size != null
    );
    this.modelDialog = true;
    this.editMode = true;
    this.clonedModel = { ...model };
  }

  deleteModel(model: Model) {
    this.modelService.checkModelUsage(model.id!)
      .subscribe((modelDeleteDTO: ModelDeleteDTO) => {

        this.confirmationService.confirm({
          message: this.getModelConfirmationMessage(modelDeleteDTO, model.name),
          header: 'Confirmation',
          icon: 'pi pi-exclamation-triangle',
          accept: () => {
            this.modelService
              .deleteModelById(model.id!, modelDeleteDTO.usedOffersCount > 0)
              .pipe(takeUntil(this.$unsubscribe))
              .subscribe(() => {
                this.models = this.models.filter((val) => val.id !== model.id);
                this.model = Object.assign({}, this.model);
                this.messageService.add({
                  severity: 'success',
                  summary: 'Successful',
                  detail: modelDeleteDTO.usedOffersCount > 0 ? 'Modèle archivé avec succès' : 'Modèle supprimé avec succès',
                  life: 3000,
                });
              });
          },
        });
      });
  }

  rollBackModel(model: Model) {
    this.confirmationService.confirm({
      message: 'Etes-vous sûr de bien vouloir restaurer ce modèle?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.modelService
          .rollBackModel(model.id!)
          .pipe(takeUntil(this.$unsubscribe))
          .subscribe(() => {
            this.models = this.models.filter((val) => val.id !== model.id);
            this.model = Object.assign({}, this.model);
            this.messageService.add({
              severity: 'success',
              summary: 'Successful',
              detail: 'Modèle restauré avec succès',
              life: 3000,
            });
          });
      },
    });
  }

  private getModelConfirmationMessage(modelDeleteDTO: ModelDeleteDTO, modelName: string) {
    const offersList = modelDeleteDTO.usedOffersNames.join(', ');
    return modelDeleteDTO.usedOffersCount ? `<p>Le modèle <strong>${modelName}</strong> est utilisé
                  <strong style="color: red;">${modelDeleteDTO.usedOffersCount} fois</strong> au niveau des commandes.</p>
                  <p>Les offres qui utilisent ce modèle sont :
                  <strong style="color: blue;">${offersList}</strong></p>
                  <p style="font-weight: bold; margin-top: 10px;">Etes-vous sûr de bien vouloir archiver le modèle et les offres associés ?</p>` :

      `<p>Le modèle <strong>${modelName}</strong> n'est pas encore utilisé` +
      (modelDeleteDTO.usedOffersNames.length > 0 ?
        `<p>Les offres qui utilisent ce modèle sont :
                  <strong style="color: blue;">${offersList}</strong></p>` : ``) +
      `<p style="font-weight: bold; margin-top: 10px;">Etes-vous sûr de bien vouloir supprimer le modèle et les offres associés ?</p>`
  }

  onOptionSelect(): void {
    const filters: { [key: string]: (model: Model) => boolean } = {
      'showArchivedModels': (model) => model.deleted,
      'showEnabledModels': (model) => model.enabled,
      'showDisabledModels': (model) => !model.enabled && !model.deleted
    };

    this.models = this.selectedActionsOptions.length
      ?
      this.selectedActionsOptions.flatMap(selectedAction =>
        this.modelService.models.filter(filters[selectedAction.name] || (() => true))
      )
      : this.modelService.models.filter((model: Model) => !model.deleted);
  }

  hideDialog() {
    this.modelDialog = false;
    this.submitted = false;
  }

  showOptionsDialog() {
    this.isOptionsDialogVisible = true;
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
  openStockTable(model: any) {
    this.selectedModel = model;
    this.showStockDialog = true;
  }

  closeStockDialog() {
    this.showStockDialog = false;
  }
  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }

}
