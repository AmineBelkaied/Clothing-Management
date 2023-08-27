import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpHeaders, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UploadFileService {

  private baseUrl = 'http://localhost:2233/files';

  constructor(private http: HttpClient) { }

  upload(file: File, modelId: number): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();

    formData.append('modalImage', file);

    const req = new HttpRequest('POST', `${this.baseUrl}/upload/` + modelId, formData, {
      reportProgress: true,
      responseType: 'json'
    });

    return this.http.request(req);
  }

  getFiles(): Observable<any> {
    return this.http.get(`${this.baseUrl}/files`);
  }

  getImage(modelId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/download/` +  modelId, 
    {
      observe: 'response',
      responseType: 'blob' 
  });
  }
}