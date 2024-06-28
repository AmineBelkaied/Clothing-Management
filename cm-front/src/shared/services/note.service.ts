import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Note } from '../models/Note';
import { NOTE_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class NoteService {

  private baseUrl: string = environment.baseUrl + `${NOTE_ENDPOINTS.BASE}`;
  public notes: Note[] = [];

  constructor(private http: HttpClient) {
  }

  findAllNotes() {
    return this.http.get(`${this.baseUrl}`);
  }

  findAllNotesByPacketId(packetId: number) {
    return this.http.get(`${this.baseUrl}${NOTE_ENDPOINTS.BY_PACKET}/${packetId}`);
  }
}