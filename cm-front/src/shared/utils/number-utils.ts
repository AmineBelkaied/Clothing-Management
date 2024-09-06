export class NumberUtils {

    public static isNumberValid(input: number | null | undefined): boolean {
        return typeof input === 'number' && !isNaN(input);
    }
}
