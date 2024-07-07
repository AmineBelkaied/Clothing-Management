import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { Color } from 'src/shared/models/Color';
import { Model } from 'src/shared/models/Model';
import { Size } from 'src/shared/models/Size';
import { UploadFileService } from 'src/shared/services/upload.service';

@Component({
  selector: 'app-add-model',
  templateUrl: './add-model.component.html',
  styles: [`
  :host ::ng-deep .p-dialog .product-image {
      width: 150px;
      margin: 0 auto 2rem auto;
      display: block;
  }
`]
})
export class AddModelComponent implements OnInit{


  @Input() model: Model = {
    "id" : "",
    "name" : "",
    "reference" : "",
    "description" : "",
    "colors" : [],
    "sizes": [],
    "purchasePrice":10
  }
  @Input() colors: Color[] = [];
  @Input() sizes: Size[] = [];
  @Input() editMode!: boolean;
  selectedColors: any[] = [];

  selectedFile: File | undefined;
  currentFile: any;
  progress = 0;
  message : any;
  image: any;
  fileInfos: Observable<any> = new Observable();

  selectedSize: any;
  @Output()
  selectedFileEvent: EventEmitter<any> = new EventEmitter();

  constructor(private uploadFileService: UploadFileService, private sanitizer: DomSanitizer) {
  }

  ngOnInit(): void {
    if(this.editMode) {
      this.uploadFileService.getImage(this.model.id)
      .subscribe((data: any) => {
              // Replace this with your actual byte array and MIME type
        /* const byteArray = new Uint8Array(data);
        const mimeType = 'image/png'; // Replace with the appropriate MIME type
        this.convertByteArrayToImageUrl(byteArray, mimeType);
        console.log(data) */
        console.log(data);

        const blobUrl = URL.createObjectURL(data.body);
        this.image = this.sanitizer.bypassSecurityTrustUrl(blobUrl);
      });
    }
  }

  selectFile($event: any) {
    this.selectedFile = $event.files[0];
    this.selectedFileEvent.emit(this.selectedFile);
  }

    // Method to convert byte array to data URL
    convertByteArrayToImageUrl(byteArray: Uint8Array, mimeType: string) {
      const binary:any = [];
      byteArray.forEach(byte => binary.push(String.fromCharCode(byte)));
      const base64String = btoa(binary.join(''));
      this.image = `data:${mimeType};base64,${base64String}`;
    }



}
