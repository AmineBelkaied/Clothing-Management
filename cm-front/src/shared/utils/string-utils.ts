export class StringUtils {

    public static removeChars(field: string, number: number): string {
        return field.substring(0, field.length - number);
    }

    public static checkExistence(array1: any, array2: any): boolean {
       return array1.some((element: any) => {
            return array2.includes(element);
          });
    }
}