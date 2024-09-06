import { SelectItem } from 'primeng/api';

// Custom interface extending SelectItem
export interface CustomSelectItem extends SelectItem {
  governorate: string;  // Add governorate property
}
