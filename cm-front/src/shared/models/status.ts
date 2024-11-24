export interface Status {
  label: string,
  value: string,
  icon: string,
  color: string,
  count: number,
  dayCount: number,
  onlyAdmin?: boolean,
  options?: any[],
  selectedOptions?: [],
  isUserOption?: boolean
}
