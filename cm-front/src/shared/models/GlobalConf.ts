import { DeliveryCompany } from "./DeliveryCompany";

export interface GlobalConf {
  id? : string;
  applicationName?: string;
  deliveryCompany?: DeliveryCompany;
  fbPage?: number;
  comment?: string;
  exchangeComment?: string;
  cronExpression?: string;
  oneSourceApp: boolean;
  noDefaultSource: boolean;
}
