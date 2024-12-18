import { Status } from "../enums/status";

export class StatusUtils {

  public static getStatusKey(value: string): string | undefined {
    return Object.keys(Status).find(key => Status[key as keyof typeof Status] === value);
  }
  

  public static getStatusList() {
    return Object.values(Status);
  }

}