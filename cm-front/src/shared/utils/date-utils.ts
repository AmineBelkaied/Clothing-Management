import { DatePipe } from "@angular/common";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class DateUtils {

    private constructor(private datePipe: DatePipe) {}

    public  formatDateToString(date: Date): string {
      //console.log('date',date);

    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  public transformDate(date: any) {
    return this.datePipe.transform(date, 'dd/MM/yyyy');
  }

  public getDate(date: Date): any {
    return this.datePipe.transform(date, 'yyyy-MM-dd');
  }

}
