export class StringUtils {

    public static removeChars(field: string, number: number): string {
        return field.substring(0, field.length - number);
    }
}