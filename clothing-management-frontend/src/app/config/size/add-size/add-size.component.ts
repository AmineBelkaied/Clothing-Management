import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Size } from 'src/shared/models/Size';
import { SizeService } from 'src/shared/services/size.service';

@Component({
  selector: 'app-add-size',
  templateUrl: './add-size.component.html',
  styleUrls: ['./add-size.component.css']
})
export class AddSizeComponent implements OnInit {

  size!: Size;
  editMode!: boolean;
  constructor(public sizeService: SizeService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.sizeService.size.subscribe(size => {
      this.size = size;
      console.log(this.size)
    });
  }

  addSize(form: NgForm) {
    if(this.sizeService.editMode){
      this.size.reference = form.value.reference;
      this.size.description = form.value.description;
      this.sizeService.updateSize(this.size)
      .subscribe((updateSize: any) => {
        console.log(updateSize)
        this.sizeService.spliceSize(updateSize);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été modifiée avec succés", life: 1000 });
        form.reset();
        this.sizeService.editMode = false;
      });
    } else {
      this.sizeService.addSize(form.value)
      .subscribe((addedColor: any) => {
        this.sizeService.sizes.push(addedColor);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été crée avec succés", life: 1000 });
        form.reset();
      });
    }
  }

  reset(sizeForm: NgForm){
    sizeForm.reset();
    this.sizeService.editMode = false;
  }

}
