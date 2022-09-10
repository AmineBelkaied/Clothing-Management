import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Size } from 'src/shared/models/Size';
import { SizeService } from '../services/size.service';

@Component({
  selector: 'app-add-size',
  templateUrl: './add-size.component.html',
  styleUrls: ['./add-size.component.css']
})
export class AddSizeComponent implements OnInit {

  size: Size = {
    'id' : '',
    'reference' : '',
    'description' : ''
   }
  
  constructor(private sizeService: SizeService, private messageService: MessageService) { }

  ngOnInit(): void {
  }

  addSize(sizeForm: NgForm) {
    this.sizeService.addSize(sizeForm.value)
    .subscribe((addedSize: any) => {
      this.sizeService.pushSize(addedSize);
      this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été crée avec succés", life: 1000 });
    })

  }

}
