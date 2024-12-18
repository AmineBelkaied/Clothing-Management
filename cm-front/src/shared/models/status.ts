export interface StatusItem {
  label: string,
  value: string,
  icon: string,
  color: string,
  count: number,
  dayCount: number,
  onlyAdmin?: boolean,
  items?: any[],
  selectedOptions?: [],
  isUserOption?: boolean
}
