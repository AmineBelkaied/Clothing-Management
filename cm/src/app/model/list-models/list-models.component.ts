import { Component, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Color } from 'src/shared/models/color';
import { Model } from 'src/shared/models/Model';
import { Size } from 'src/shared/models/Size';
import { ColorService } from 'src/shared/services/color.service';
import { ModelService } from 'src/shared/services/model.service';
import { SizeService } from 'src/shared/services/size.service';

@Component({
  selector: 'app-list-models',
  templateUrl: './list-models.component.html',
  styleUrls: ['./list-models.component.css']
})
export class ListModelsComponent implements OnInit {
  modelDialog!: boolean;

  models: any[] = [];

  model: Model = {
    "name": "",
    "reference": "",
    "description": "",
    "colors": [],
    "sizes": []
  }
  editMode = false;

  colors: Color[] = [];
  sizes: Size[] = [];
  selectedModels: Model[] = [];

  submitted: boolean = false;

  constructor(private modelService: ModelService, private messageService: MessageService,
     private confirmationService: ConfirmationService, private colorService: ColorService, private sizeService: SizeService) {
  }

  ngOnInit() {

    this.colorService.findAllColors()
    .subscribe((colors: any) => {
      this.colors = colors;
      console.log(this.colors)
    })
    this.sizeService.findAllSizes()
    .subscribe((sizes: any) => {
      this.sizes = sizes;
      console.log(this.sizes)
    })
    this.modelService.findAllModels().subscribe((data: any) => {
      console.log(data)
      this.models = data;
    });
  }

  openNew() {
    this.model = {
      "name": "",
      "reference": "",
      "description": "",
      "colors": [],
      "sizes": []
    }
    this.submitted = false;
    this.modelDialog = true;
    this.editMode = false;
  }

  deleteSelectedModels() {
    let selectedModelsId = this.selectedModels.map((selectedModel: any) => selectedModel.id);
    console.log(selectedModelsId);
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer les modèles séléctionnés ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.modelService.deleteSelectedModels(selectedModelsId)
          .subscribe(result => {
            this.models = this.models.filter(val => !this.selectedModels.includes(val));
            this.selectedModels = [];
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Les modèles séléctionnés ont été supprimé avec succés', life: 1000 });
          })
      }
    });
  }

  editModel(model: Model) {
    /*       console.log(this.colors)
          console.log(this.colors.filter(color =>  colorsId.indexOf(color.code) != -1)); */
    this.model = { ...model };
    /*     let colorsId = model.colors.map(color => color.id);
        this.model.colors = [];
        this.colors.forEach(color => {
          if(colorsId.indexOf(+color.code) > - 1) {
              this.model.colors.push(color);
          }
  
        });
        console.log(this.model.colors);
        this.model = Object.assign({} , this.model)
        console.log(this.model) */
    this.modelDialog = true;
    this.editMode = true;
  }

  deleteModel(model: Model) {
    console.log(model.id)
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete ' + this.model.name + '?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.modelService.deleteModelById(model.id).subscribe((response: any) => {
          this.models = this.models.filter(val => val.id !== model.id);
          this.model = Object.assign({}, this.model);
          this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'model Deleted', life: 3000 });
        })
      }
    });
  }

  hideDialog() {
    this.modelDialog = false;
    this.submitted = false;
  }

  saveModel() {
    this.submitted = true;

    if (this.model.name.trim()) {
      if (this.model.id) {
        this.modelService.updateModel(this.model)
          .subscribe({
            next: response => {
              console.log(response);
              this.models[this.findIndexById(this.model.id)] = this.model;
              this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Le modèle a été mis à jour avec succés', life: 3000 });
            }
          });
      }
      else {
        console.log(this.model.colors)
        //this.model.colors = this.model.colors.map((color: any) => {return  {"code" : color.code , "name" : color.name}});
        this.modelService.addModel(this.model)
          .subscribe({
            next: response => {
              console.log(response);
              //this.model.id = this.createId();
              //this.model.image = 'model-placeholder.svg';
              this.models.push(response);
              this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Le modèle a été crée avec succés', life: 3000 });
            },
            error: error => {

              console.error('There was an error!', error);
            }
          })
      }
      this.models = [...this.models];
      this.modelDialog = false;
      this.model = Object.assign({}, this.model);
    }
  }

  findIndexById(id: string): number {
    let index = -1;
    for (let i = 0; i < this.models.length; i++) {
      if (this.models[i].id === id) {
        index = i;
        break;
      }
    }

    return index;
  }

  /*findColorById(id: string): any {
    for (let i = 0; i < this.colors.length; i++) {
      if (this.colors[i].code === id) {
        return this.colors[i].name;
      }
    }

  }*/

  createId(): string {
    let id = '';
    var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    for (var i = 0; i < 5; i++) {
      id += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return id;
  }

  modelColorsDisplay(colors: any[]) {
    let colorDisplay = "";
    colors.forEach((color, index) => {
      colorDisplay += color.name;
      if (index < colors.length - 1)
        colorDisplay += ",";
    });
    return colorDisplay;
  }

  modelSizesDisplay(sizes: any[]) {
    let sizeDisplay = "";
    sizes.forEach((size, index) => {
      sizeDisplay += size.reference;
      if (index < sizes.length - 1)
        sizeDisplay += "-";
    });
    return sizeDisplay;
  }
}
